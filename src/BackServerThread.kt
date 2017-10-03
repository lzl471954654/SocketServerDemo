import java.util.*

class BackServerThread:Thread() {

    override fun run() {
        val scanner = Scanner(System.`in`)
        while (true){
            val instruction = scanner.nextLine()
            when (instruction) {
                "pc" -> {
                    println("This is pc Client List")
                    println("AccountName\t\tThread")
                    ServerMain.beControlledSocketMap.forEach {
                        println(it.key.account+"\t\t"+it.value.toString())
                    }
                }
                "client" -> {
                    println("\nThis is client List")
                    println("AccountName\t\tThread")
                    ServerMain.controlSocketMap.forEach {
                        println(it.key.account+"\t\t"+it.value.toString())
                    }
                }
                "exit"->{
                    System.exit(0)
                }
            }
        }
    }

}