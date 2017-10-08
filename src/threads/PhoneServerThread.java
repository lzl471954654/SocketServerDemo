package threads;

import JavaBean.Account;
import JavaBean.ServerProtocol;
import Utils.IntConvertUtils;
import mainClass.ServerMain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

public class PhoneServerThread extends Thread {
    Socket socket;
    InputStream in;
    OutputStream out;
    Account account = new Account();

    PhoneServerThread bindThread;
    boolean controlled = false;
    boolean loop = false;
    boolean isBind = false;


    public PhoneServerThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try{
            if(socket!=null)
            {
                in = socket.getInputStream();
                out = socket.getOutputStream();
                service();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        finally {
            System.out.println("disConnect");
            unBind();
            try {
                if(socket!=null){
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void service() throws IOException{
        String first = readLine();
        if(first.equals("")){
            System.out.println("empty instruction");
        }
        if(first.startsWith(ServerProtocol.ONLINE)){
            String[] params = first.split("_");
            if(params.length!=4){
                loop = false;
                sendMsg(createPramas(ServerProtocol.ONLINE_FAILED));
            }
            else{
                account.setAccount(params[1]);
                account.setPassword(params[2]);
                if(containsAccount()){
                    loop = false;
                    sendMsg(createPramas(ServerProtocol.ONLINE_FAILED));
                }
                else{
                    loop = true;
                    controlled = true;
                    addAccount();
                    sendMsg(createPramas(ServerProtocol.ONLINE_SUCCESS));
                }
            }
        }else if(first.startsWith(ServerProtocol.CONNECTED_TO_USER)){
            String[] params = first.split("_");
            if(params.length!=4){
                loop = false;
                sendMsg(createPramas(ServerProtocol.CONNECTED_FAILED));
            }else {
                account.setAccount(params[1]);
                account.setPassword(params[2]);
                if(containsAccount()){
                    loop = true;
                    bind(ServerMain.phoneMap.get(account));
                    sendMsg(createPramas(ServerProtocol.CONNECTED_SUCCESS));
                }else {
                    loop = false;
                    sendMsg(createPramas(ServerProtocol.CONNECTED_FAILED));
                }
            }
        }
        else{
            loop = false;
            sendMsg(createPramas(ServerProtocol.ONLINE_FAILED));
        }
        int count = 0;
        while(loop){
            System.out.println();
            /*
            * 这行 System.out.println 不能删掉！！！ 删掉不能正常运行！
            * */
            if(isBind){
                byte[] bytes = new byte[4096];
                if(controlled){
                    count = bindThread.in.read(bytes);
                    if(count==-1){
                        loop = false;
                        break;
                    }
                    //System.out.println("count is "+count);
                    out.write(bytes,0,count);
                    //System.out.println("pid:"+getId()+"\tcontrolled :"+new String(bytes,0,count,"UTF-8"));
                }else {
                    count = bindThread.in.read(bytes);
                    if(count==-1){
                        loop = false;
                        break;
                    }
                    //System.out.println("count is "+count);
                    out.write(bytes,0,count);
                    //System.out.println("pid:"+getId()+"\tphone :"+new String(bytes,0,count,"UTF-8"));
                }
            }
        }
        loop = false;
    }



    private boolean containsAccount(){
        for(Map.Entry<Account,PhoneServerThread> entry : ServerMain.phoneMap.entrySet()){
            if(entry.getKey().equals(account))
                return true;
        }
        return false;
    }

    private void bind(PhoneServerThread thread){
        thread.bindThread = this;
        thread.loop = true;
        thread.isBind = true;
        bindThread = thread;
        isBind = true;
        loop = true;
    }

    private void unBind(){
        if(bindThread!=null){
            removeAccount();
            bindThread.removeAccount();
            bindThread.isBind = false;
            bindThread.bindThread = null;
            bindThread.loop = false;
            bindThread.interrupt();
        }
    }

    private void removeAccount(){
        ServerMain.phoneMap.remove(account);
    }

    private void addAccount(){
        ServerMain.phoneMap.put(account,this);
    }

    private String createPramas(String... param){
        StringBuilder builder = new StringBuilder();
        for (String s : param) {
            builder.append(s);
            builder.append("_");
        }
        builder.append(ServerProtocol.END_FLAG);
        return builder.toString();
    }

    private void sendMsg(String msg){
        if(out!=null){
            try {
                byte[] bytes = msg.getBytes("UTF-8");
                out.write(IntConvertUtils.getIntegerBytes(bytes.length));
                out.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String readLine() throws IOException{
        byte[] msgSizeArray = new byte[4];
        int readSize = in.read(msgSizeArray);
        System.out.println("readSize :"+readSize);
        int msgSize = IntConvertUtils.getIntegerByByteArray(msgSizeArray);
        if(msgSize>20*102){
            System.out.println("msg is too large , size is "+msgSize);
            return "";
        }
        if (msgSize<=0){
            loop = false;
            return "";
        }
        byte[] data = new byte[msgSize];
        int i = 0;
        while(i<msgSize){
            data[i] = (byte) in.read();
            i++;
        }

        String is = new String(data,"UTF-8");
        System.out.printf("phone instruction is : "+is+"\n");
        if(is.endsWith(ServerProtocol.END_FLAG))
            return is;
        else
            return "";
    }
}
