package NewVersion;

import JavaBean.Account;
import NewVersion.thread.Pc2PhoneThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    public static ConcurrentHashMap<Account, Pc2PhoneThread> pcMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Account, Pc2PhoneThread> phoneMap = new ConcurrentHashMap<>();

    public static volatile int threadCount = 0;
    public static void main(String[] args) {
        Thread listener = new Thread(listner);
        listener.start();
    }

    private static Runnable listner = ()->{
        try {
            ServerSocket serverSocket = new ServerSocket(10086);
            while (!Thread.interrupted()){
                Socket socket = serverSocket.accept();
                threadCount++;
                Pc2PhoneThread thread = new Pc2PhoneThread(socket);
                thread.start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    };
}
