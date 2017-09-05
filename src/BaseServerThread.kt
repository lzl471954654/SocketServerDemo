import JavaBean.Account
import JavaBean.FileDescribe
import com.google.gson.Gson
import java.io.*
import java.net.Socket

abstract class BaseServerThread(open var socket: Socket):Thread() {
    internal var reader: BufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
    internal var printWriter: PrintWriter = PrintWriter(OutputStreamWriter(socket.getOutputStream()))
    internal var port = socket.port
    internal var inetAddress = socket.inetAddress
    internal var loop = true
    val classTag = javaClass.name
    val END = ServerProtocol.END_FLAG
    var account = Account()
    var bindFlag = false
    var bindThread:BaseServerThread? = null

    fun releaseSocket()
    {
        printWriter.close()
        socket.close()
        loop = false
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
        printWriter.println(createParams(ServerProtocol.NORMAL_MSG,msg))
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

    fun fileTransmission(jsonSrc: String) {
        val gson = Gson()
        val fileArray = gson.fromJson<Array<FileDescribe>>(jsonSrc, Array<FileDescribe>::class.java)
        bindThread!!.printWriter.println(createParams(ServerProtocol.FILE_LIST_FLAG, jsonSrc))
        val response = bindThread!!.readStringData()
        val params = response.split("_")
        when (params[0]) {
            ServerProtocol.FILE_READY->{
                this.printWriter.println(createParams(ServerProtocol.FILE_READY))
                fileArray.forEach {
                    readAndSendByteData(this,bindThread!!,it.fileSize)
                }
            }
            else->{
                this.printWriter.println(createParams(ServerProtocol.ERROR,params[0]))
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
            if(count==-1)
                break
            bufferedOutput.write(bytes,0,count)
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