import JavaBean.Account
import com.sun.xml.internal.ws.resources.SenderMessages
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
                        printWriter.println(createParams(ServerProtocol.CONNECTED_SUCCESS))
                        printWriter.flush()
                    } else {
                        printWriter.println(createParams(ServerProtocol.CONNECTED_FAILED))
                        printWriter.flush()
                        return
                    }
                }
                ServerProtocol.FILE_LIST_FLAG->{
                    dispatchMessage(request)
                }
                ServerProtocol.FILE_READY->{
                    NewFileTransmission(params[1])
                }
                ServerProtocol.FILE_NOT_READY->{
                    dispatchMessage(request)
                }
                /*ServerProtocol.FILE_LIST_FLAG -> {
                    if (isBind()) {
                        fileTransmission(params[1])
                    } else {
                        sendErrorMsg(ServerProtocol.UNBIND_ERROR)
                    }
                }*/
                ServerProtocol.COMMAND -> {
                    if (isBind()) {
                        bindThread!!.printWriter.println(createParams(ServerProtocol.COMMAND, params[1]))
                        bindThread!!.printWriter.flush()
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
                flag = true
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
