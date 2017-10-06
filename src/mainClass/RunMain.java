package mainClass;

import Utils.LogUtils;
import threads.BackServerThread;

public class RunMain {
    public static void main(String[] args) {
        try{
            LogUtils.initLog();
            LogUtils.logInfo("Main","LogInit!");
            BackServerThread backServerThread = new BackServerThread();
            backServerThread.start();
            ServerMain serverMain = new ServerMain();
            serverMain.serverRun();
            LogUtils.releaseResource();
        }catch (Exception e){
            e.printStackTrace();
            LogUtils.logException("Main",""+e.getMessage());
        }

    }
}
