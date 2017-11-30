package Test;

import NewVersion.ProtocolField;
import Utils.IntConvertUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TestPC {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1",10086);
        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();
        String s = "lzl|lzl";
        byte[] bytes = s.getBytes("UTF-8");
        outputStream.write(ProtocolField.pcOnline);
        outputStream.write(IntConvertUtils.getShortBytes((short)bytes.length));
        outputStream.write(bytes);
        byte flag = (byte) inputStream.read();
        System.out.println("flag is "+flag);
        if (flag == ProtocolField.onlineSuccess){
            System.out.println("Online success");
        }else{
            System.out.println("online Failed");
        }
        while (true){
            flag = (byte) inputStream.read();
            System.out.println("message is "+flag);
        }
    }
}
