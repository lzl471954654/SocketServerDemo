import JavaBean.FileDescribe;
import Utils.StringUtils;
import com.google.gson.Gson;

import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class TestClass {
    public static void main(String[] args) {
        ConcurrentHashMap<String,Integer> map = new ConcurrentHashMap<>();
        String s = "abc";
        map.put(s,1);
        Scanner scanner = new Scanner(System.in);
        System.out.println(s.hashCode());
    }

}
