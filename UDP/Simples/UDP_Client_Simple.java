//CLIENT SIDE
//REFERENCE: https://www.geeksforgeeks.org/working-udp-datagramsockets-java/

import java.net.*; //use DatagramPacket, DatagramSocket, InetAddress, and SocketException
import java.util.Scanner;

public class UDP_Client_Simple{
    public static void main(String[] args) throws Exception{
        DatagramSocket socket = null;
        DatagramPacket packet = null;
        byte[] buffer = null;
        

        InetAddress target = InetAddress.getByName("192.168.1.100");
        Scanner sc = new Scanner(System.in);

        // 1 CREATE SOCKET
        socket = new DatagramSocket(); //AUTOMATICALLY BINDS SOCKET TO PORT AVAILABLE ON LOCALHOST
        int port = 65535;
        
        // 1.2 CONNECT SOCKET TO TARGET
        socket.connect(target, port);
        System.out.println("Socket is binded to: " + socket.getLocalPort());
        System.out.println("Socket is connected to: " + target.getHostAddress() + ":" + socket.getPort());

        // TALK LOOP
        boolean runtime = true;
        String text = "";
        while(runtime){
            text = sc.nextLine();

            buffer = text.getBytes(); //CONVERT TEXT TO BYTE[]

            // 2 CREATE UDP/DATAGRAM PACKET
            packet = new DatagramPacket(buffer, buffer.length, target, port);

            // 3 SEND UDP PACKET USING SOCKET
            socket.send(packet);

            if(text.equals("/exit"))
                runtime = false;
        }
        
        // 4 CLOSING SOCKET
        socket.close();

        //CLOSING SCANNER
        sc.close();
    }
}
