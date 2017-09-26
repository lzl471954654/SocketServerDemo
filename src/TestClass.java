import JavaBean.FileDescribe;
import Utils.StringUtils;
import com.google.gson.Gson;

public class TestClass {
    public static void main(String[] args) {
        String s = "1nialnflij_sd__dk_123456";
        for (String s1 : StringUtils.splitStringStartAndEnd(s, "_")) {
            System.out.println(s1);
        }
    }
}
