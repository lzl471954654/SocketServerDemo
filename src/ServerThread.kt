import java.io.*
import java.net.Socket

class ServerThread @Throws(IOException::class)
constructor(internal var socket: Socket) : Thread() {
    internal var reader: BufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
    internal var printWriter: PrintWriter = PrintWriter(OutputStreamWriter(socket.getOutputStream()))
    internal var port = socket.port
    internal var inetAddress = socket.inetAddress
    internal var loop = true
    val END = ServerProtocol.END_FLAG
    var contorller = false
    lateinit var id:String


    override fun run() {
        try {
            server()
        }catch (e:Exception){
            e.printStackTrace()
            LogUtils.logException(this.javaClass.name,""+e.message)
        }
    }

    private fun server(){
        var line: String? = null
        while (loop) {
            try {
                line = readStringData()
                LogUtils.logInfo(javaClass.name,line)
                val data = line
                println(data)
                println(data.split("_"))
                when(data.split("_")[0])
                {
                    ServerProtocol.CONNECTED_TO_USER->{
                        val params = data.split("_")
                        if(params.size<3){
                            sendParamsError("Params is not enough")
                        }
                        val userMode = params[1]
                        val id = params[2]
                        println("id:$id")
                        when(userMode){
                            ServerProtocol.CONTROL->{
                                println("Control")
                                println(ServerMain.beControlledSocketMap.containsKey(id))
                                if(ServerMain.beControlledSocketMap.containsKey(id)) {
                                    val userServer = ServerMain.beControlledSocketMap.get(id)
                                    exchangeIpAddress(userServer!!)
                                }
                                else
                                {
                                    sendNormalMsg("No such user : $id")
                                }
                            }
                            /*ServerProtocol.BE_CONTROLLED->{
                                if(ServerMain.beControlledSocketMap.contains(id)){
                                    val userServer = ServerMain.beControlledSocketMap.get(id)
                                    exchangeIpAddress(userServer!!)
                                }
                                else{
                                    sendNormalMsg("No such user : $id")
                                }
                            }*/
                        }
                    }
                    ServerProtocol.HEATR_BEAT->{
                        printWriter.println(ServerProtocol.HEATR_BEAT+END)
                        printWriter.flush()
                    }
                    ServerProtocol.ONLINE->{
                        println("online")
                        val paramas = data.split("_")
                        if(paramas.size<3)
                        {
                            val error = "Params is not enough"
                            sendParamsError(error)
                            return
                        }
                        val id:String = paramas[1]
                        val mode = paramas[2]
                        println("mode")
                        when(mode){
                            ServerProtocol.CONTROL->{
                                if(ServerMain.controlSocketMap.containsKey(id))
                                {
                                    sendNormalMsg(ServerProtocol.ONLINE_FAILED)
                                    releaseSocket()
                                }
                                else{
                                    ServerMain.controlSocketMap.put(id,this)
                                    sendNormalMsg(ServerProtocol.ONLINE_SUCCESS)
                                    contorller = true
                                    this.id = id
                                }
                            }
                            ServerProtocol.BE_CONTROLLED->{
                                println(id)
                                if(ServerMain.beControlledSocketMap.containsKey(id)){
                                    sendNormalMsg(ServerProtocol.ONLINE_FAILED)
                                    releaseSocket()
                                }
                                else{
                                    println("add")
                                    ServerMain.beControlledSocketMap.put(id,this)
                                    sendNormalMsg(ServerProtocol.ONLINE_SUCCESS)
                                    contorller = false
                                    this.id = id
                                }
                            }
                            else->{
                                println("else")
                                sendParamsError("No such mode")
                            }
                        }
                    }
                    ServerProtocol.OFFLINE->{
                        val paramas = data.split("_")
                        if(paramas.size<2)
                        {
                            val error = "Params is not enough"
                            sendParamsError(error)
                            return
                        }
                        val id:String = paramas[1]
                        val mode:String = paramas[2]
                        when(mode){
                            ServerProtocol.CONTROL->{
                                if(ServerMain.controlSocketMap.containsKey(id))
                                {
                                    sendNormalMsg("OFFLINE SUCCESS")
                                    ServerMain.controlSocketMap.remove(id)
                                    releaseSocket()
                                }
                            }
                            ServerProtocol.BE_CONTROLLED->{
                                if(ServerMain.beControlledSocketMap.containsKey(id)){
                                    sendNormalMsg("OFFLINE SUCCESS")
                                    ServerMain.beControlledSocketMap.remove(id)
                                    releaseSocket()
                                }
                            }
                            else->{
                                sendParamsError("No such mode")
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                LogUtils.logException(this.javaClass.name,""+e.message)
                releaseSocket()
            }
            finally {

            }

        }
    }

    fun exchangeIpAddress(server:ServerThread){
        println("exchange")
        val userPort = server.port
        val userPrinter = server.printWriter
        val userAddress = server.inetAddress

        this.printWriter.println(ServerProtocol.MAKE_HOLE+"_${userAddress.hostAddress}_${userPort}_"+END)
        this.printWriter.flush()
        userPrinter.println(ServerProtocol.MAKE_HOLE+"_${this.inetAddress.hostAddress}_${this.port}_"+END)
        userPrinter.flush()
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
            Thread.sleep(1000)
        }
        return builder.toString()
    }

    fun sendParamsError(msg:String){
        printWriter.println(ServerProtocol.ERROR+"_$msg"+"_"+END)
        printWriter.flush()
        releaseSocket()
    }

    fun sendNormalMsg(msg:String){
        printWriter.println(ServerProtocol.NORMAL_MSG+"_"+msg+"_"+END)
        printWriter.flush()
    }

    fun releaseSocket()
    {
        printWriter.close()
        socket.close()
        loop = false
    }
}
