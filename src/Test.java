import java.io.IOException;
import java.net.*;

/**
 * Created by Shani on 1/6/2017.
 */
public class Test {
    public static void main(String[] args) {
        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket();
            String str = "SEARCH West world";
            //  str="REG "+ip+" "+port+" "+username;
            int len = str.length() + 5;
            str = String.format("%04d", len) + " " + str;
            InetAddress ip = null;
            ip = InetAddress.getByName("127.0.0.1");
            DatagramPacket dp = new DatagramPacket(str.getBytes(), str.length(), ip, 55000);
            ds.send(dp);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
