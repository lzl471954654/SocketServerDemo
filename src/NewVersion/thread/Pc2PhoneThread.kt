package NewVersion.thread

import JavaBean.Account
import JavaBean.ServerProtocol
import NewVersion.Main
import NewVersion.ProtocolField
import Utils.IntConvertUtils
import java.io.*
import java.net.Socket
import java.nio.charset.Charset
import java.util.concurrent.locks.ReentrantLock

class Pc2PhoneThread(val socket:Socket?) : Thread(){
    internal val lock = ReentrantLock()
    internal val waitCondition = lock.newCondition()
    lateinit var account :Account
    lateinit var input:InputStream
    lateinit var output:OutputStream
    lateinit var inPiped : PipedInputStream
    lateinit var outPiped : PipedOutputStream

    var isControlled : Boolean = false

    override fun run() {
        try{
            if(socket!=null){
                input = socket.getInputStream()
                output = socket.getOutputStream()
                service()
            }
        }catch (e:IOException){
            e.printStackTrace()
        }catch (e:InterruptedException){
            e.printStackTrace()
        }
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
                    if(Main.pcMap.containsKey(account)){
                        ProtocolField.onlineSuccess
                    }else{
                        Main.phoneMap.put(account,this)
                        ProtocolField.onlineSuccess
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
        }else{
            /*
            *  online Failed , shutdown the connection
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