package NewVersion;

import JavaBean.Account;
import NewVersion.thread.Pc2PhoneThread;

import java.util.concurrent.ConcurrentHashMap;

public class Main {
    public static ConcurrentHashMap<Account, Pc2PhoneThread> pcMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Account, Pc2PhoneThread> phoneMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {

    }
}
