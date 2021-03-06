package OldVersion.threads

import JavaBean.FileDescribe
import JavaBean.ServerProtocol
import Utils.LogUtils
import Utils.StringUtils
import com.google.gson.Gson
import OldVersion.mainClass.ServerMain
import java.net.Socket

class ControlledServerThread(override var socket: Socket) : BaseServerThread(socket) {
    var onlineFlag = false

    override fun server() {
        while (loop&&!isInterrupted) {
            val request = readStringData()
            val params = request.split("_")
            if(params.size<2){
                return
            }
            when (params[0]) {
                ServerProtocol.ONLINE -> {
                    account.account = params[1]
                    account.password = params[2]
                    if (containsAccount()) {
                        onlineFlag = false
                        sendErrorMsg(ServerProtocol.ONLINE_FAILED)
                        return
                    } else
                    {
                        onlineFlag = true
                        addAccount()
                        sendNormalMsg(ServerProtocol.ONLINE_SUCCESS)
                    }
                }
                ServerProtocol.OFFLINE -> {
                    return
                }
                ServerProtocol.FILE_LIST_FLAG ->{
                    synchronized(lockObject){
                        dispatchMessage(request)
                        println("${this.javaClass.name}\t pid:${Thread.currentThread().id} is lock")
                        lockObject.wait()
                        println("${this.javaClass.name}\t pid:${Thread.currentThread().id} is unlock")
                    }
                }
                ServerProtocol.FILE_READY ->{
                    println("FIle Ready!"+this.javaClass.name)
                    synchronized(bindThread!!.lockObject){
                        NewFileTransmission(StringUtils.splitStringStartAndEnd(request,"_")[1])
                        bindThread!!.lockObject.notifyAll()
                        println("${this.javaClass.name}\t pid:${Thread.currentThread().id} is called notify")
                    }
                }
                ServerProtocol.FILE_NOT_READY ->{
                    dispatchMessage(request)
                }
                /*JavaBean.ServerProtocol.FILE_LIST_FLAG -> {
                    if (isBind())
                        fileTransmission(params[1])
                    else {
                        sendErrorMsg(JavaBean.ServerProtocol.UNBIND_ERROR)
                    }
                }*/
                ServerProtocol.PIC_SEND ->{
                    if(isBind()){
                        sendPic(StringUtils.splitStringStartAndEnd(request,"_")[1])
                    }
                    else{
                        sendErrorMsg(ServerProtocol.UNBIND_ERROR)
                    }
                }
                ServerProtocol.COMMAND_RESULT ->{
                    if(isBind()){
                        bindThread!!.sendMsg(createParams(ServerProtocol.COMMAND_RESULT,StringUtils.splitStringStartAndEnd(request,"_")[1]))
                    }
                    else{
                        sendErrorMsg(ServerProtocol.UNBIND_ERROR)
                    }
                }
                else->{
                    if(isBind()){
                        dispatchMessage(request)
                    }
                    else{
                        sendErrorMsg(ServerProtocol.UNBIND_ERROR)
                    }
                }
            }
        }
    }

    fun sendPic(jsonSrc:String){
        val gson = Gson()
        val fileDesc = gson.fromJson(jsonSrc,FileDescribe::class.java)
        bindThread!!.sendMsg(createParams(ServerProtocol.PIC_SEND,jsonSrc))
        val response = bindThread!!.readStringData()
        val params = response.split("_")
        when(params[0]){
            ServerProtocol.FILE_READY ->{
                this.sendMsg(createParams(ServerProtocol.FILE_READY))
                readAndSendByteData(this,bindThread!!,fileDesc.fileSize)
            }
            else->{
                this.sendMsg(createParams(ServerProtocol.ERROR,params[0]))
            }
        }
    }


    override fun run() {
        try {
            server()
        } catch (e: Exception) {
            LogUtils.logException(classTag, "" + e.message)
            println("$classTag pid : ${Thread.currentThread().id}  ${e.message}")
        } finally {
            if(isBind()){
                bindThread!!.loop = false
                bindThread!!.interrupt()
            }
            releaseSocket()
        }
    }



    override fun removeAccount() {
        if (onlineFlag)
            ServerMain.beControlledSocketMap.remove(account,this)
    }

    override fun addAccount() {
        if(onlineFlag)
            ServerMain.beControlledSocketMap.put(account, this)
    }

    override fun containsAccount(): Boolean {
        var flag = false
        ServerMain.beControlledSocketMap.forEach {
            if(it.key == account)
            {
                flag = true
                return@forEach
            }
        }
        return flag
    }
}