package NewVersion

object ProtocolField {
    const val pcOnline : Byte      = 0x00.toByte()
    const val phoneOnline : Byte   = 0x01.toByte()
    const val onlineSuccess : Byte = 0x02.toByte()
    const val onlineFailed : Byte  = 0x03.toByte()
}