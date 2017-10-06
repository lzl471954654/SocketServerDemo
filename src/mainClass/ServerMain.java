package mainClass;

import JavaBean.Account;
import Utils.LogUtils;
import threads.ControlledServerThread;
import threads.PhoneServerThread;
import threads.ServerThread;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ServerMain {
    public static ConcurrentHashMap<Account, ServerThread> controlSocketMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Account, ControlledServerThread> beControlledSocketMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Account, PhoneServerThread> phoneMap = new ConcurrentHashMap<>();


    public void serverRun(){
        try{
            run();
            acceptPhone();
        }catch (Exception e){
            e.printStackTrace();
            LogUtils.logException(getClass().getName(),""+e.getMessage());
            LogUtils.releaseResource();
        }
    }

    private void acceptPhone(){
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(10085);
            while(true){
                Socket socket = serverSocket.accept();
                PhoneServerThread thread = new PhoneServerThread(socket);
                thread.start();
                System.out.println("Get a Phone Socket ip:"+socket.getInetAddress()+"\tport:"+socket.getPort());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void run(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerSocket serverSocket;
                try{
                    serverSocket = new ServerSocket(10087);
                }catch (IOException e){
                    e.printStackTrace();
                    LogUtils.logException(this.getClass().getName(),"请检查端口是否被绑定\t"+e.getMessage());
                    LogUtils.releaseResource();
                    return;
                }
                try{
                    while(true){
                        Socket socket = serverSocket.accept();
                        System.out.println("controlledThread socket is"+socket==null);
                        ControlledServerThread thread = new ControlledServerThread(socket);
                        LogUtils.logInfo(getClass().getName(),"Get a ControlledSocket -> IP: "+socket.getInetAddress()+" Port: "+socket.getPort());
                        thread.start();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                    LogUtils.logException(this.getClass().getName(),"\t"+e.getMessage());
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    ServerSocket serverSocket = new ServerSocket(10086);
                    while(true){
                        Socket socket = serverSocket.accept();
                        System.out.println("threads.ServerThread socket is"+socket==null);
                        ServerThread thread = new ServerThread(socket);
                        LogUtils.logInfo(getClass().getName(),"Get a ControlledSocket -> IP: "+socket.getInetAddress()+" Port: "+socket.getPort());
                        thread.start();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                    LogUtils.logException(this.getClass().getName(),"请检查端口是否被绑定\t"+e.getMessage());
                }
            }
        }).start();
    }
}
