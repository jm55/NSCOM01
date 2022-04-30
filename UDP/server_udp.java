/**
 * Reference: https://www.geeksforgeeks.org/working-udp-datagramsockets-java/
 * Modified accordingly for better execution.
 */

import java.io.IOException;
import java.net.*; //use DatagramPacket, DatagramSocket, InetAddress, and SocketException
import java.util.*;

public class server_udp{
    private DatagramSocket socket;
    private DatagramPacket packet;
    private byte[] receive;
    private boolean runtime = false;

    public static void main(String[] args){
        cls();
        
        server_udp s = new server_udp();

        Scanner sc = new Scanner(System.in);
        
        int p = -1;
        do{
            System.out.print("Enter port # (Valid Range: 49152-65535): ");
            p = Integer.parseInt(sc.nextLine());
        }while(p < 49152 || p > 65535);
        
        sc.close();
        try{
            s.activate_server_udp(p);
            s.listen();

            //4. CLOSING SOCKET
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Reference: https://stackoverflow.com/a/38342964
     */
    private final static void showSystemIP(int port){
        try {
            DatagramSocket s = new DatagramSocket();
            s.connect(InetAddress.getByName("1.1.1.1"), 53);
            System.out.println("Server Network IP:port : " + s.getLocalAddress().getHostAddress() + ":" + port);
            System.out.println("Server Local IP:port : " + InetAddress.getLocalHost().getHostAddress() + ":" + port);
            s.close();
        } catch (Exception e) {
            System.out.println("Server Network/Local IP cannot be determined. It may require an internet connection");
        }
        
    }

    public void activate_server_udp(int port) throws IOException{
        cls();
        System.out.println("Creating Server...");
        showSystemIP(port);
        runtime = true;
        //1. CREATING/OPENING A SOCKET TO LISTEN TO PORT SPECIFIED.
        socket = new DatagramSocket(port);
        receive = new byte[65535]; //65535 bytes worth of data

        //1.1. DISPLAYING SOCKET INFORMATION
        System.out.println("Socket is binded to: " + socket.getLocalPort());

        //Initial state being null
        packet = null;
    }

    public void listen() throws Exception{
        System.out.println("Listening...");
        System.out.println("==============================");
        while(runtime){
            //2. CREATE A DATAGRAMPACKET TO RECEIVE THE DATA.
            receive = new byte[65535]; //resetting contents
            packet = new DatagramPacket(receive, receive.length);
            
            //3. RECEIVE THE DATA IN BYTE BUFFER.
            socket.receive(packet);
            String rec_ip = packet.getAddress().getHostAddress();
            String rec_host = packet.getAddress().getHostName();

            //DECOMPRESS DATA
            compression c = new compression();
            byte[] decompressed = c.decompress(receive, false);

            System.out.println("Client " + rec_ip + ":" + socket.getLocalPort() + " (" + rec_host + "): " + data(decompressed));

            //EXIT:
            if(data(decompressed).equals("/exit")){
                System.out.println("Client " + rec_ip + " has exited.");
            }else if(data(decompressed).equals("/hardexit")){
                System.out.println("Request for server shutdown received...");
                runtime = false;
            }
        }
    }

    private static String data(byte[] b){
        if(b == null)
            return null;
        StringBuilder sb = new StringBuilder();
        int i, len = b.length;
        for(i = 0; i < len; i++)
            sb.append((char)b[i]);
        return sb.toString();
    }

    private final static void cls(){
        //https://stackoverflow.com/a/32295974
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        banner();
    }

    private final static void banner(){
        System.out.println("==============================\n          UDP SERVER\n==============================");
    }
}