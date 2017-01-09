/**
 * @author MaN
 *         on 1/9/2017.
 */
public class QueueObject {
    String ip;
    int port;
    String str;

    public QueueObject(String ip, int port, String str) {
        this.ip = ip;
        this.port = port;
        this.str = str;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }
}
