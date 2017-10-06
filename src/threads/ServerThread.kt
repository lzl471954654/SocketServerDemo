package threads

import JavaBean.ServerProtocol
import Utils.LogUtils
import Utils.StringUtils
import mainClass.ServerMain
import java.io.*
import java.net.Socket

class ServerThread @Throws(IOException::class)
constructor(override var socket: Socket) : BaseServerThread(socket) {


    override fun run() {
        try {
            server()
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtils.logException(this.javaClass.name, "" + e.message)
            if (e is InterruptedException&&!loop){
                sendErrorMsg(ServerProtocol.OFFLINE)
            }
        }finally {
            releaseSocket()
        }
    }

    override fun releaseSocket() {
        loop = false
        if(isBind())
        {
            bindThread!!.removeAccount()
            bindThread!!.loop = false
            bindThread!!.interrupt()
            unBindServerThread()
        }
        if(containsAccount())
            removeAccount()
        socket.close()
    }


    /*override fun releaseSocket() {
        loop = false
        if(isBind())
            unBindServerThread()
        if(containsAccount())
        {
            removeAccount()
            println("remove user ${account.account}")
        }
        socket.close()
    }*/

    override fun server() {
        while (loop&&!isInterrupted) {
            println("when")
            val request = readStringData()
            val params = request.split("_")
            when (params[0]) {
                ServerProtocol.CONNECTED_TO_USER -> {
                    account.account = params[1]
                    account.password = params[2]
                    if (checkContolledOnline()) {
                        addAccount()
                        sendMsg(createParams(ServerProtocol.CONNECTED_SUCCESS))
                    } else {
                        sendMsg(createParams(ServerProtocol.CONNECTED_FAILED))
                        return
                    }
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
                    if (isBind()) {
                        fileTransmission(params[1])
                    } else {
                        sendErrorMsg(JavaBean.ServerProtocol.UNBIND_ERROR)
                    }
                }*/
                ServerProtocol.COMMAND -> {
                    if (isBind()) {
                        bindThread!!.sendMsg(createParams(ServerProtocol.COMMAND, StringUtils.splitStringStartAndEnd(request,"_")[1]))
                    } else {
                        sendErrorMsg(ServerProtocol.UNBIND_ERROR)
                    }
                }
                else->{
                    dispatchMessage(request)
                }
            }
        }
    }





    fun checkContolledOnline(): Boolean {
        var flag = false
        ServerMain.beControlledSocketMap.forEach {
            if (it.key==account) {
                flag = !it.value.isBind()
                if (flag)
                    bindServerThread(it.value)
                return@forEach
            }
        }
        return flag
    }

    override fun removeAccount() {
        ServerMain.controlSocketMap.remove(account)
    }

    override fun addAccount() {
        ServerMain.controlSocketMap.put(account, this)
    }

    override fun containsAccount(): Boolean {
        return ServerMain.controlSocketMap.contains(account)
    }
}
