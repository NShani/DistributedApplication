import com.sun.corba.se.impl.orbutil.concurrent.Mutex;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.*;

/**
 * Created by Shani on 1/6/2017.
 */
public class Node {

    private String bootstrapServerIp = "127.0.0.1";
    private int bootstrapServerPort = 55555;

    List<Neighbour> neighboursList =
            Collections.synchronizedList(new ArrayList<Neighbour>());

    DatagramSocket socket = null;

    private int port;
    private String ip;
    private String username;
    private BlockingQueue queue = new LinkedBlockingQueue<>();
    ExecutorService executor = Executors.newFixedThreadPool(3);

    public Node(String ip, int port, String username) {
        this.port = port;
        this.ip = ip;
        this.username = username;
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    private Runnable listner = new Runnable() {
        @Override
        public void run() {
            try {
                while (true) {
                    byte[] buffer = new byte[65536];
                    DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                    socket.receive(incoming);

                    byte[] data = incoming.getData();
                    String st = new String(data, 0, incoming.getLength());
                    System.out.println("NODE " + username + " Recieved from " + incoming.getPort() + ":" + st);
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
                    str = str.substring(5);
                    StringTokenizer tokenizer = new StringTokenizer(str, " ");
                    String opr = tokenizer.nextToken();
                    if (opr.equals("JOIN")) {
                        String ip = tokenizer.nextToken();
                        int port = Integer.parseInt(tokenizer.nextToken());
                        Neighbour neighbour = new Neighbour(ip, port);
                        try {
                            neighboursList.add(neighbour);
                            sendDataPacket(ip, port, "JOINOK 0");
                        } catch (Exception e) {
                            sendDataPacket(ip, port, "JOINOK 9999");
                        }

                    } else if (opr.equals("PRINT")) {
                        printRoutineTable();
                    } else if (opr.equals("LEAVE")) {
                        String ip = tokenizer.nextToken();
                        int port = Integer.parseInt(tokenizer.nextToken());
                        for (Iterator<Neighbour> iterator = neighboursList.iterator(); iterator.hasNext(); ) {
                            Neighbour n = iterator.next();
                            if (port == n.getPort() && ip.equals(n.getIp())) {
                                try {
                                    iterator.remove();
                                    sendDataPacket(ip, port, "LEAVEOK 0");
                                } catch (Exception e) {
                                    sendDataPacket(ip, port, "LEAVEOK 9999");
                                }
                            }
                        }
                    } else if (opr.equals("REMOVE")) {
                        leave();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void sendDataPacket(String ipAdr, int port, String msg) {

        try {
            InetAddress ip = InetAddress.getByName(ipAdr);
            int len = msg.length() + 5;
            msg = String.format("%04d", len) + " " + msg;
            DatagramPacket dp = new DatagramPacket(msg.getBytes(), msg.length(), ip, port);
            socket.send(dp);
            System.out.println("NODE " + username + " Sent to port " + port + " : " + msg);
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

            String str = "REG " + ip + " " + port + " " + username;
            int len = str.length() + 5;
            str = String.format("%04d", len) + " " + str;
            InetAddress ip = null;
            ip = InetAddress.getByName(bootstrapServerIp);
            DatagramPacket dp = new DatagramPacket(str.getBytes(), str.length(), ip, bootstrapServerPort);
            socket.send(dp);

            byte[] buf = new byte[1024];
            DatagramPacket reply = new DatagramPacket(buf, 1024);
            socket.receive(reply);
            String rep = new String(reply.getData(), 0, reply.getLength());
            System.out.println("Node :" + username + " Reply from Bootstrap server  :" + rep);


            rep = rep.substring(5);
            if (rep.startsWith("REGOK")) {
                String[] parts = rep.split(" ");
                int noOfNodes = Integer.parseInt(parts[1]);

                for (int i = 0; i < noOfNodes; i++) {

                    Neighbour neighbour = new Neighbour(parts[2 * i + 2], Integer.parseInt(parts[2 * i + 3]));
                    neighboursList.add(neighbour);

                }
                join();
            } else {

            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        executor.execute(listner);
        executor.execute(requestHandler);
    }

    private void join() {
        String str = "JOIN " + ip + " " + port;

        for (Neighbour n : neighboursList) {

            sendDataPacket(n.getIp(), n.getPort(), str);

        }
    }

    private void leave() {
        sendDataPacket(ip, 55555, "UNREG " + ip + " " + port + " " + username);

        for (Iterator<Neighbour> iterator = neighboursList.iterator(); iterator.hasNext(); ) {

            Neighbour n = iterator.next();
            sendDataPacket(n.getIp(), n.getPort(), "LEAVE " + ip + " " + port);
            iterator.remove();

        }

    }

    private void printRoutineTable() {
        String str = "Routing table of Node " + username + "\n";
        for (Neighbour n : neighboursList) {
            str += "IP: " + n.getIp() + " Port: " + n.getPort() + "\n";
        }

        System.out.println(str);


    }

}
