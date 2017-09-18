import JavaBean.Account
import JavaBean.FileCommand
import JavaBean.FileDescribe
import com.google.gson.Gson
import java.io.*
import java.net.Socket
import java.nio.charset.Charset
import kotlin.experimental.and
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

abstract class BaseServerThread:Thread {
    abstract var socket: Socket

    internal var loop = true
    val classTag = javaClass.name
    val END = ServerProtocol.END_FLAG
    var account = Account()
    var bindFlag = false
    var bindThread:BaseServerThread? = null
    var lockObject:java.lang.Object = java.lang.Object()

    constructor(socket: Socket){
        this.socket = socket
        println("constructor socket is ${if (socket==null){"null"}else{"not NULL"}}")
    }

    fun releaseSocket()
    {
        socket.close()
        loop = false
        if(isBind())
            unBindServerThread()
        if(containsAccount())
            removeAccount()
    }

    fun sendErrorMsg(msg:String){
        val bytes = createParams(ServerProtocol.ERROR,msg).toByteArray(Charset.forName("UTF-8"))
        socket.getOutputStream().write(IntConvertUtils.getIntegerBytes(bytes.size))
        socket.getOutputStream().write(bytes)
        releaseSocket()
    }

    fun sendNormalMsg(msg:String){
        val bytes = createParams(msg).toByteArray(Charset.forName("UTF-8"))
        socket.getOutputStream().write(IntConvertUtils.getIntegerBytes(bytes.size))
        socket.getOutputStream().write(bytes)
    }

    fun sendMsg(msg: String){
        val bytes = msg.toByteArray(Charset.forName("UTF-8"))
        socket.getOutputStream().write(IntConvertUtils.getIntegerBytes(bytes.size))
        socket.getOutputStream().write(bytes)
    }

    fun createParams(vararg args:String):String{
        val builder:StringBuilder = StringBuilder()
        args.forEach {
            builder.append(it+"_")
        }
        builder.append(END)
        return builder.toString()
    }

    fun dispatchMessage(request:String){
        if (isBind()) {
            bindThread!!.sendMsg(request)
        } else {
            sendErrorMsg(ServerProtocol.UNBIND_ERROR)
        }
    }

    fun NewFileTransmission(jsonSrc: String){
        val gson = Gson()
        val fileArray = gson.fromJson<FileCommand>(jsonSrc, FileCommand::class.java)
        println("fileTrans :"+this.javaClass.name)

        bindThread!!.sendMsg(createParams(ServerProtocol.FILE_READY,jsonSrc))

        fileArray.describe.forEach {
            readAndSendByteData(bindThread!!,this,it.fileSize)
        }
    }

    fun fileTransmission(jsonSrc: String) {
        val gson = Gson()
        val fileArray = gson.fromJson<FileCommand>(jsonSrc, FileCommand::class.java)
        bindThread!!.sendMsg(createParams(ServerProtocol.FILE_LIST_FLAG, jsonSrc))
        val response = bindThread!!.readStringData()
        val params = response.split("_")
        when (params[0]) {
            ServerProtocol.FILE_READY->{
                this.sendMsg(createParams(ServerProtocol.FILE_READY))
                fileArray.describe.forEach {
                    readAndSendByteData(this,bindThread!!,it.fileSize)
                }
            }
            else->{
                this.sendMsg(createParams(ServerProtocol.ERROR,params[0]))
            }
        }
    }

    fun readAndSendByteData(srcServerThread: BaseServerThread,targetServerTHread:BaseServerThread,fileSize:Long){
        var bufferedInput = srcServerThread.socket.getInputStream()
        var bufferedOutput = targetServerTHread.socket.getOutputStream()
        var bytes = ByteArray(4096)
        var sizeCount = 0L
        var count = 0
        println("class :${this.javaClass.name} pid = ${Thread.currentThread().id}\tfileSize is :$fileSize")
        while (true){
            count = bufferedInput.read(bytes)
            println("class :${this.javaClass.name} pid = ${Thread.currentThread().id}\tcount is " + count)
            println("class :${this.javaClass.name} pid = ${Thread.currentThread().id}\tdata : ${String(bytes)}")
            if(count==-1)
                break
            bufferedOutput.write(bytes,0,count)
            bufferedOutput.flush()
            sizeCount+=count
            if(sizeCount==fileSize)
                break
        }
    }

    fun readStringData():String{
        val inputStream = socket.getInputStream()

        var msgSize = 4
        var msgArray = ByteArray(msgSize)
        inputStream.read(msgArray)
        msgSize = IntConvertUtils.getIntegerByByteArray(msgArray)
        println("class :${this.javaClass.name} pid = ${Thread.currentThread().id}\tmsgSize is $msgSize")

        val dataBytes = ByteArray(msgSize)
        var i = 0
        while (i<msgSize){
            dataBytes[i] = inputStream.read().toByte()
            i++
        }

        val instruction = String(dataBytes)


        println("class :${this.javaClass.name} pid = ${Thread.currentThread().id}\treadStringData :${instruction}")
        return instruction
    }


    fun bindServerThread(thread:BaseServerThread){
        bindThread = thread
        bindFlag = true
        thread.bindThread = this
        thread.bindFlag = true
    }

    fun unBindServerThread(){
        bindThread!!.bindThread = null
        bindThread!!.bindFlag = false
        bindThread = null
        bindFlag = false
    }

    fun isBind():Boolean{
        return bindFlag
    }

    abstract fun server()
    abstract fun removeAccount()
    abstract fun addAccount()
    abstract fun containsAccount():Boolean
}