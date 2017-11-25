import JavaBean.FileDescribe;
import Utils.IntConvertUtils;
import Utils.StringUtils;
import com.google.gson.Gson;

import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class TestClass {
    public static void main(String[] args) {
        System.out.println(IntConvertUtils.getShortByByteArray(IntConvertUtils.getShortBytes((short)32000)));
    }

}
