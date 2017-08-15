import JavaBean.Account
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
        while (loop) {
            val request = readStringData()
            val params = request.split("_")
            when (params[0]) {
                ServerProtocol.CONNECTED_TO_USER -> {
                    account.account = params[1]
                    account.password = params[2]
                    if (checkContolledOnline()) {
                        printWriter.println(createParams(ServerProtocol.CONNECTED_TO_USER, ServerProtocol.CONNECTED_SUCCESS))
                    } else {
                        printWriter.println(createParams(ServerProtocol.CONNECTED_TO_USER,ServerProtocol.CONNECTED_FAILED))
                        return
                    }
                }
                ServerProtocol.FILE_LIST_FLAG -> {
                    if (isBind()) {
                        fileTransmission(params[0])
                    } else {
                        sendErrorMsg(ServerProtocol.UNBIND_ERROR)
                    }
                }
                ServerProtocol.COMMAND -> {
                    if (isBind()) {
                        bindThread!!.printWriter.println(createParams(ServerProtocol.COMMAND, params[1]))
                    } else {
                        sendErrorMsg(ServerProtocol.UNBIND_ERROR)
                    }
                }
            }
        }
    }


    fun exchangeIpAddress(server: ServerThread) {
        println("exchange")
        val userPort = server.port
        val userPrinter = server.printWriter
        val userAddress = server.inetAddress

        this.printWriter.println(ServerProtocol.MAKE_HOLE + "_${userAddress.hostAddress}_${userPort}_" + END)
        this.printWriter.flush()
        userPrinter.println(ServerProtocol.MAKE_HOLE + "_${this.inetAddress.hostAddress}_${this.port}_" + END)
        userPrinter.flush()
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
