import JavaBean.Account
import JavaBean.FileCommand
import JavaBean.FileDescribe
import com.google.gson.Gson
import java.io.*
import java.net.Socket

abstract class BaseServerThread:Thread {
    abstract var socket: Socket
    internal var reader: BufferedReader
    internal var printWriter: PrintWriter

    internal var loop = true
    val classTag = javaClass.name
    val END = ServerProtocol.END_FLAG
    var account = Account()
    var bindFlag = false
    var bindThread:BaseServerThread? = null

    constructor(socket: Socket){
        this.socket = socket
        println("constructor socket is ${if (socket==null){"null"}else{"not NULL"}}")
        reader = BufferedReader(InputStreamReader(socket.getInputStream()))
        printWriter = PrintWriter(OutputStreamWriter(socket.getOutputStream()))
    }

    fun releaseSocket()
    {
        printWriter.close()
        socket.close()
        loop = false
        if(isBind())
            unBindServerThread()
        if(containsAccount())
            removeAccount()
    }

    fun sendErrorMsg(msg:String){
        printWriter.println(createParams(ServerProtocol.ERROR,msg))
        printWriter.flush()
        releaseSocket()
    }

    fun sendNormalMsg(msg:String){
        printWriter.println(createParams(msg))
        printWriter.flush()
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
            bindThread!!.printWriter.println(request)
            bindThread!!.printWriter.flush()
        } else {
            sendErrorMsg(ServerProtocol.UNBIND_ERROR)
        }
    }

    fun NewFileTransmission(jsonSrc: String){
        val gson = Gson()
        val fileArray = gson.fromJson<FileCommand>(jsonSrc, FileCommand::class.java)


        bindThread!!.printWriter.println(createParams(ServerProtocol.FILE_READY, jsonSrc))
        bindThread!!.printWriter.flush()

        fileArray.describe.forEach {
            readAndSendByteData(this,bindThread!!,it.fileSize)
        }
    }

    fun fileTransmission(jsonSrc: String) {
        val gson = Gson()
        val fileArray = gson.fromJson<FileCommand>(jsonSrc, FileCommand::class.java)

        bindThread!!.printWriter.println(createParams(ServerProtocol.FILE_LIST_FLAG, jsonSrc))
        bindThread!!.printWriter.flush()
        val response = bindThread!!.readStringData()
        val params = response.split("_")
        when (params[0]) {
            ServerProtocol.FILE_READY->{
                this.printWriter.println(createParams(ServerProtocol.FILE_READY))
                this.printWriter.flush()
                fileArray.describe.forEach {
                    readAndSendByteData(this,bindThread!!,it.fileSize)
                }
            }
            else->{
                this.printWriter.println(createParams(ServerProtocol.ERROR,params[0]))
                this.printWriter.flush()
            }
        }
    }

    fun readAndSendByteData(srcServerThread: BaseServerThread,targetServerTHread:BaseServerThread,fileSize:Long){
        var bufferedInput = BufferedInputStream(srcServerThread.socket.getInputStream())
        var bufferedOutput = BufferedOutputStream(targetServerTHread.socket.getOutputStream())
        var bytes = ByteArray(4096)
        var sizeCount = 0L
        var count = 0
        while (true){
            count = bufferedInput.read(bytes)
            println("count is " + count)
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
        var line:String? = null
        var builder:StringBuilder = StringBuilder()
        while (true){
            line = reader.readLine()
            if(line!=null)
                builder.append(line)
            if(line!=null&&line.endsWith(END))
                break
            Thread.sleep(500)
        }
        return builder.toString()
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