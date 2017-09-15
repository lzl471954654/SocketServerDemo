import JavaBean.FileDescribe;
import com.google.gson.Gson;

public class TestClass {
    public static void main(String[] args) {
        Gson gson = new Gson();
        FileDescribe describe = new FileDescribe("hello","txt",20L);
        System.out.println(gson.toJson(describe));
    }
}
