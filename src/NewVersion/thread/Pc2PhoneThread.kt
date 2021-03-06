package NewVersion.thread

import JavaBean.Account
import JavaBean.ServerProtocol
import NewVersion.Main
import NewVersion.ProtocolField
import Utils.IntConvertUtils
import java.io.*
import java.net.Socket
import java.net.SocketException
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.locks.ReentrantLock

class Pc2PhoneThread(val socket:Socket?) : Thread(){
    internal val lock = ReentrantLock()
    internal val waitCondition = lock.newCondition()
    lateinit var account :Account
    lateinit var input:InputStream
    lateinit var output:OutputStream

    /*lateinit var inPiped : PipedInputStream
    lateinit var outPiped : PipedOutputStream*/

    var isControlled : Boolean = false
    @Volatile
    var isBind : Boolean = false
    var bindThread : Pc2PhoneThread? = null


    override fun run() {
        try{
            if(socket!=null){
                input = socket.getInputStream()
                output = socket.getOutputStream()
                service()
            }
        }catch (e:IOException){
            //e.printStackTrace()
            println("${Date(System.currentTimeMillis())}-Thread $id : IOException , $account")
        }catch (e:InterruptedException){
            //e.printStackTrace()
            println("${Date(System.currentTimeMillis())}-Thread $id : InterruptedException , $account")
        }catch (e:SocketException){
            println("${Date(System.currentTimeMillis())}-Thread $id : SocketException : ${e.message} , $account")
        }
        finally {
            println("unbind")
            unbindThread()
            //input.close()
            //output.close()
            socket?.close()
        }
    }


    /**
     *
     * This function is aimed to unbind the thread that had already bind.
     * Example:
     * A is online
     * B connected to A
     * connected successful
     * B bind A each other
     * A <---> B
     *
     * if A thread or B thread Unexpected collapse and terminated , will call this method
     * A <---> B  then  A <-  broke  -> B
     *
     * */
    private fun unbindThread(){
        isBind = false
        bindThread?.isBind = false
        bindThread?.interrupt()
        bindThread?.bindThread = null
        bindThread = null
        var removeFlag = false
        if(isControlled){
            removeFlag = Main.pcMap.remove(account,this)
        }else{
            removeFlag = Main.phoneMap.remove(account,this)
        }
        println("Remove thread "+removeFlag+" , Thread is ${id} , account = $account")
    }

    /**
     * first read one byte to judge the message type
     * second read two bytes to convert to short to get the message body's size
     * finally read the size of message body's size to get the real message
     * */
    private fun service(){
        val flag = input.read().toByte()
        val answer : Byte = if(flag == ProtocolField.pcOnline || flag == ProtocolField.phoneOnline){
            val sizeBytes = readBySize(2)
            val messageSize = IntConvertUtils.getShortByByteArray(sizeBytes)
            val messageBytes = readBySize(messageSize.toInt())
            val ticket = String(messageBytes,Charset.forName("UTF-8"))
            val userParam = ticket.split("|")
            if(userParam.size < 2){
                ProtocolField.onlineFailed
            }else
            {
                account = Account(userParam[0],userParam[1])
                if(flag == ProtocolField.pcOnline){
                    if(Main.pcMap.containsKey(account)){
                        ProtocolField.onlineFailed
                    }else{
                        Main.pcMap.put(account,this)
                        isControlled = true
                        ProtocolField.onlineSuccess
                    }
                }else{
                    /**
                     * phone to connect a pc , so search in pcMap
                     * */
                    if(Main.pcMap.containsKey(account)){
                        /**
                         * connected successful , bind each other
                         * */
                        Main.phoneMap.put(account,this)
                        bindThread = Main.pcMap[account]
                        bindThread?.bindThread = this
                        isBind = true
                        bindThread?.isBind = true
                        ProtocolField.onlineSuccess
                    }else{
                        ProtocolField.onlineFailed
                    }
                }
            }
        }else{
            ProtocolField.onlineFailed
        }
        output.write(answer.toInt())
        if(answer == ProtocolField.onlineSuccess)
        {
            /*
            *  online Success , to continue the connection
            * */

            /*inPiped = PipedInputStream()
            outPiped = PipedOutputStream()*/

            if( flag == ProtocolField.pcOnline){
                lock.lock()
                try {
                    println("will be waited")
                    waitCondition.await()
                    println("waited end")
                }catch (e:InterruptedException){
                    //e.printStackTrace()
                    throw  e
                } finally {
                    println("unlock")
                    lock.unlock()
                }
            }else{
                try {
                    bindThread?.lock?.lock()
                    bindThread?.waitCondition?.signalAll()
                }finally {
                    bindThread?.lock?.unlock()
                }
                /*inPiped.connect(bindThread!!.outPiped)
                outPiped.connect(bindThread!!.inPiped)*/
            }

            var count:Int
            var bytes = ByteArray(4096)
            while (isBind && !Thread.interrupted()){
                count = bindThread!!.input.read(bytes)
                if(count == -1)
                    break
                output.write(bytes,0,count)
            }

        }else{
            /*
            *  online Failed , shutdown the connection , do nothing
            * */
        }
    }

    private fun readBySize(size:Int):ByteArray{
        val bytes = ByteArray(size)
        var count = 0
        while (count<size){
            bytes[count] = input.read().toByte()
            count++
        }
        return bytes
    }
}