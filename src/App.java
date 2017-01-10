import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Shani on 1/6/2017.
 */
public class App {
    public static void main(String[] args) {
        Node node1=new Node("127.0.0.1",55000,"A",new ArrayList<String>(Arrays.asList("Game of thrones", "Big bang theory", "Friends")));
        Node node2=new Node("127.0.0.1",55001,"B",new ArrayList<String>(Arrays.asList( "Big bang theory", "Breaking bad","Vikings","Orange is the new Black")));
        Node node3=new Node("127.0.0.1",55002,"C",new ArrayList<String>(Arrays.asList("Wire", "Himym", "Last ship", "Black sails","Orange is the new Black")));
        Node node4=new Node("127.0.0.1",55003,"D",new ArrayList<String>(Arrays.asList("Vikings", "Game of thrones","West world")));
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
