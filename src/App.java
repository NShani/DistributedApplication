/**
 * Created by Shani on 1/6/2017.
 */
public class App {
    public static void main(String[] args) {
        Node node1=new Node("127.0.0.1",55000,"A");
        Node node2=new Node("127.0.0.1",55001,"B");
        Node node3=new Node("127.0.0.1",55002,"C");
        Node node4=new Node("127.0.0.1",55003,"D");
        node1.register();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        node2.register(); try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        node3.register();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        node4.register();
    }
}
