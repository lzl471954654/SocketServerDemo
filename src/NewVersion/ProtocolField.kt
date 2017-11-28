package NewVersion

object ProtocolField {
    /**
     * pcOnline PC online
     *
     *
     * */
    const val pcOnline : Byte      = 0x00.toByte()
    const val phoneOnline : Byte   = 0x01.toByte()
    const val onlineSuccess : Byte = 0x02.toByte()
    const val onlineFailed : Byte  = 0x03.toByte()
    const val offline : Byte       = 0x04.toByte()

    /**
     *  File operation Fields
     * */
    const val fileSend =    0x10.toByte()
    const val fileSendBig = 0x11.toByte()
    const val fileSendOK =  0x12.toByte()
    const val fileGet =     0x13.toByte()
    const val fileGetBig =  0x14.toByte()
    const val fileGetOK =   0x15.toByte()
    const val fileHead =    0x16.toByte()
    const val fileBody =    0x17.toByte()
    const val fileEnd =     0x18.toByte()
    const val fileStop =    0x19.toByte()
    const val fileContinue =0x1a.toByte()


    /**
     *  Command operation Fields
     * */
    const val command = 0x20.toByte()

}