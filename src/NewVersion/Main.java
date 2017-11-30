package NewVersion;

import JavaBean.Account;
import NewVersion.thread.Pc2PhoneThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    public static ConcurrentHashMap<Account, Pc2PhoneThread> pcMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Account, Pc2PhoneThread> phoneMap = new ConcurrentHashMap<>();

    private static volatile int threadCount = 0;
    public static void main(String[] args) {
        Thread listener = new Thread(listner);
        listener.start();
        Scanner scanner = new Scanner(System.in);
        String command = scanner.nextLine();
        switch (command){
            case "exit":{
                listener.interrupt();
                stopAllServer();
                System.exit(0);
                break;
            }
            case "pc":{
                for (Pc2PhoneThread thread : pcMap.values()) {
                    System.out.println("Thread "+thread.getId()+": isInterrupted - "+thread.isInterrupted());
                }
                break;
            }
            case "phone":{
                for (Pc2PhoneThread thread : phoneMap.values()) {
                    System.out.println("Thread "+thread.getId()+": isInterrupted - "+thread.isInterrupted());
                }
                break;
            }
        }
    }

    private static void stopAllServer(){
        for (Pc2PhoneThread thread : pcMap.values()) {
            thread.interrupt();
        }
        for (Pc2PhoneThread thread : phoneMap.values()) {
            thread.interrupt();
        }
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
