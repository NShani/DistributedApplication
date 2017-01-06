import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Shani on 1/6/2017.
 */
public class Node {
    ArrayList<Neighbour> neighboursList = new ArrayList<>();

    private String port;
    private String ip;
    private String username;
    private BlockingQueue queue = new LinkedBlockingQueue<>();
    ExecutorService executor = Executors.newFixedThreadPool(5);

    public Node(String ip, String port, String username) {
        this.port = port;
        this.ip = ip;
        this.username = username;
    }


    private Runnable listner = new Runnable() {
        @Override
        public void run() {
            DatagramSocket sock = null;
            try {
                sock = new DatagramSocket(Integer.parseInt(port));
                while (true) {
                    byte[] buffer = new byte[65536];
                    DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                    sock.receive(incoming);

                    byte[] data = incoming.getData();
                    String st = new String(data, 0, incoming.getLength());
                    System.out.println(st);
                    queue.put(st);
                }
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private Runnable requestHandler = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    String str = queue.take().toString();
                    System.out.println("At Request Handler" + str);
                    str=str.substring(5);
                    StringTokenizer tokenizer= new StringTokenizer(str, " ");
                    String opr=tokenizer.nextToken();
                    if(opr.equals("JOIN")){
                        String ip=tokenizer.nextToken();
                        int port=        Integer.parseInt(tokenizer.nextToken());
                        Neighbour neighbour=  new Neighbour(ip,port);
                        neighboursList.add(neighbour);
                        sendDataPacket(ip,port,"0014 JOIN OK 0");

                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void sendDataPacket(String ipAdr,int port,String msg){
        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket();
            InetAddress ip = null;
            ip = InetAddress.getByName(ipAdr);
            DatagramPacket dp = new DatagramPacket(msg.getBytes(), msg.length(), ip, port);
            ds.send(dp);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void register() {

        try {
            DatagramSocket ds = new DatagramSocket();
            String str = "0036 REG 129.82.123.45 " + port + " 124d";
            str = "REG " + ip + " " + port + " " + username;
            int len = str.length() + 5;
            str = String.format("%04d", len) + " " + str;
            InetAddress ip = null;
            ip = InetAddress.getByName("127.0.0.1");
            DatagramPacket dp = new DatagramPacket(str.getBytes(), str.length(), ip, 55555);
            ds.send(dp);

            byte[] buf = new byte[1024];
            DatagramPacket reply = new DatagramPacket(buf, 1024);
            ds.receive(reply);
            String rep = new String(reply.getData(), 0, reply.getLength());
            System.out.println("Reply : " + rep);

            ds.close();

            rep = rep.substring(5);
            if (rep.startsWith("REGOK")) {
                String[] parts = rep.split(" ");
                int noOfNodes = Integer.parseInt(parts[1]);
                System.out.println(noOfNodes);
                for (int i = 0; i < noOfNodes; i++) {

                    Neighbour neighbour = new Neighbour(parts[2 * i + 2], Integer.parseInt(parts[2 * i + 3]));
                    neighboursList.add(neighbour);

                }
            } else {

            }
            System.out.println("dsd");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        executor.execute(listner);
        executor.execute(requestHandler);
    }

}
