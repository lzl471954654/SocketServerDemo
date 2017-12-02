import NewVersion.Main;
import OldVersion.mainClass.RunMain;

public class FirstRun {
    public static void main(String[] args) {
        boolean old = false;
        if(args.length == 0){
            System.out.println("No version params , normally use new version !");
            old = false;
        }else {
            if(args[0].equals("old")){
                old = true;
            }else if(args[0].equals("new")){
                old = false;
            }else if (args[0].equals("-help")){
                System.out.println("Help Information\nUse old param to use old version to run\nUse new param to use new version to run\n");
                System.exit(0);
            }
            else{
                System.out.println("wrong version params , please use -help to use more help");
                System.exit(0);
            }
        }
        if (old)
            RunMain.main(null);
        else
            Main.main(null);
    }
}
