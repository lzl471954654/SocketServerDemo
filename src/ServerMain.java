import JavaBean.Account;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ServerMain {
    public static ConcurrentHashMap<Account,ServerThread> controlSocketMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Account,ControlledServerThread> beControlledSocketMap = new ConcurrentHashMap<>();

    public void serverRun() {
        try {
            ServerSocket serverSocket = new ServerSocket(10086);
            while(true){
                Socket socket = serverSocket.accept();
                ServerThread thread = new ServerThread(socket);
                LogUtils.logInfo(getClass().getName(),"Get a Socket -> IP: "+socket.getInetAddress()+" Port: "+socket.getPort());
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.logException(this.getClass().getName(),"请检查端口是否被绑定\t"+e.getMessage());
            LogUtils.releaseResource();
        }
    }
}
