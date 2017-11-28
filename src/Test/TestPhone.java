package Test;

import java.io.IOException;
import java.net.Socket;

public class TestPhone {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1",10086);
    }


}
