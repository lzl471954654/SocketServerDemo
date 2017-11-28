import JavaBean.FileDescribe;
import Utils.IntConvertUtils;
import Utils.StringUtils;
import com.google.gson.Gson;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class TestClass {
    private static PipedInputStream inputStream = new PipedInputStream();
    private static PipedOutputStream outputStream = new PipedOutputStream();
    public static void main(String[] args) throws IOException {
        String line = null;
        outputStream.connect(inputStream);


        new Thread(runnable).start();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            line = scanner.nextLine();
            if(line.equals("exit")){
                System.out.println("Main Thread exit!");
                break;
            }
            outputStream.write(line.getBytes());
            outputStream.flush();
        }
        outputStream.close();
        inputStream.close();
    }

    static Runnable runnable = ()->{
        try {
            byte[] bytes = new byte[1024];
            int count = 0;
            while( (count = inputStream.read(bytes))!=-1){
                String line = new String(bytes,0,count);
                System.out.println("Sub Thread :"+line);
            }
            System.out.println("Sub Thread exit!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    };
}
