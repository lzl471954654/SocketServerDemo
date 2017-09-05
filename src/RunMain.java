import java.net.Socket;

public class RunMain {
    public static void main(String[] args) {
        try{
            LogUtils.initLog();
            LogUtils.logInfo("Main","LogInit!");
            ServerMain serverMain = new ServerMain();
            serverMain.serverRun();
            LogUtils.releaseResource();
        }catch (Exception e){
            e.printStackTrace();
            LogUtils.logException("Main",""+e.getMessage());
        }

    }
}
