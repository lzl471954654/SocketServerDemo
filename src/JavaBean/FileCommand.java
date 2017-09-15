package JavaBean;

/**
 * Created by LZL on 2017/9/15.
 */

public class FileCommand {
    private String type;
    private FileDescribe[] describe;
    private boolean isBack = false;

    public FileCommand(){}

    public FileCommand(String type, FileDescribe[] describe, boolean isBack) {
        this.type = type;
        this.describe = describe;
        this.isBack = isBack;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FileDescribe[] getDescribe() {
        return describe;
    }

    public void setDescribe(FileDescribe[] describe) {
        this.describe = describe;
    }

    public boolean isBack() {
        return isBack;
    }

    public void setBack(boolean back) {
        isBack = back;
    }
}
