package JavaBean;

import com.google.gson.Gson;

public class Command {
    String cmd;
    boolean isBack =false;

    @Override
    public String toString() {
        return "Command{" +
                "cmd='" + cmd + '\'' +
                ", isBack=" + isBack +
                '}';
    }

    public void startCommand(){
        if(isBack){
            backRun();
        }
        else
        {
            noBackRun();
        }
    }

    public void backRun(){

    }

    public void noBackRun(){

    }

    public static void main(String[] args) {
        Command command = new Command();
        command.isBack = false;
        command.cmd = "start www.baidu.com";
        Gson gson = new Gson();
        String data = gson.toJson(command);
        System.out.println(data);
        String cmd = "COMMAND_"+data+"_END";
        System.out.println(cmd);
        String[] strings = cmd.split("_");
        Command command1 = gson.fromJson(strings[1],Command.class);
        System.out.println(command1);
    }
}
