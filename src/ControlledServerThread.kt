import JavaBean.Account
import JavaBean.FileDescribe
import com.google.gson.Gson
import java.io.*
import java.net.Socket
import java.net.SocketException
import java.net.SocketTimeoutException

class ControlledServerThread(override var socket: Socket) : BaseServerThread(socket) {


    override fun server() {
        while (loop&&!isInterrupted) {
            val request = readStringData()
            val params = request.split("_")
            when (params[0]) {
                ServerProtocol.ONLINE -> {
                    account.account = params[1]
                    account.password = params[2]
                    if (containsAccount()) {
                        sendErrorMsg(ServerProtocol.ONLINE_FAILED)
                        return
                    } else
                    {
                        addAccount()
                        sendNormalMsg(ServerProtocol.ONLINE_SUCCESS)
                    }
                }
                ServerProtocol.OFFLINE -> {
                    return
                }
                ServerProtocol.FILE_LIST_FLAG -> {
                    if (isBind())
                        fileTransmission(params[1])
                    else {
                        sendErrorMsg(ServerProtocol.UNBIND_ERROR)
                    }
                }
                ServerProtocol.COMMAND_RESULT->{
                    if(isBind()){
                        bindThread!!.printWriter.println(createParams(ServerProtocol.COMMAND_RESULT,params[1]))
                        bindThread!!.printWriter.flush()
                    }
                    else{
                        sendErrorMsg(ServerProtocol.UNBIND_ERROR)
                    }
                }
                else->{
                    if(isBind()){
                        bindThread!!.printWriter.println(request)
                        bindThread!!.printWriter.flush()
                    }
                    else{
                        sendErrorMsg(ServerProtocol.UNBIND_ERROR)
                    }
                }
            }
        }
    }


    override fun run() {
        try {
            server()
        } catch (e: Exception) {
            LogUtils.logException(classTag, "" + e.message)
        } finally {
            if(isBind()){
                bindThread!!.loop = false
                bindThread!!.interrupt()
            }
            releaseSocket()
        }
    }



    override fun removeAccount() {
        ServerMain.beControlledSocketMap.remove(account)
    }

    override fun addAccount() {
        ServerMain.beControlledSocketMap.put(account, this)
    }

    override fun containsAccount(): Boolean {
        return ServerMain.beControlledSocketMap.contains(account)
    }
}