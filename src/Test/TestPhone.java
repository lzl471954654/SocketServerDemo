package Test;

import NewVersion.ProtocolField;
import Utils.IntConvertUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TestPhone {
    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("127.0.0.1",10086);
        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();
        String s = "lzl|lzl";
        byte[] bytes = s.getBytes("UTF-8");
        outputStream.write(ProtocolField.phoneOnline);
        outputStream.write(IntConvertUtils.getShortBytes((short)bytes.length));
        outputStream.write(bytes);
        byte flag = (byte) inputStream.read();
        System.out.println("flag is "+flag);
        if (flag == ProtocolField.onlineSuccess){
            System.out.println("Online success");
        }else{
            System.out.println("online Failed");
        }
        int i = 0;
        while(true){
            outputStream.write(i);
            i++;
            Thread.sleep(1000);
        }
    }


}
