package Utils

object StringUtils {

    @JvmStatic
    fun splitStringStartAndEnd(src:String,spliteString: String):Array<String>{
        val part1 = src.substring(0,src.indexOf(spliteString))
        val part2 = src.substring(src.indexOf(spliteString)+1,src.lastIndexOf(spliteString))
        val part3 = src.substring(src.lastIndexOf(spliteString)+1,src.length-1)
        return arrayOf(part1,part2,part3)
    }
}