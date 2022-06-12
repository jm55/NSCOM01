//CLIENT SIDE
//REFERENCE: https://www.geeksforgeeks.org/working-udp-datagramsockets-java/

import java.net.*; //use DatagramPacket, DatagramSocket, InetAddress, and SocketException
import java.util.Scanner;

public class UDP_Client_Simple{
    public static void main(String[] args) throws Exception{
        DatagramSocket socket = null;
        DatagramPacket packet = null;
        byte[] buffer = null;
        
        InetAddress target = InetAddress.getByName("localhost");
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
            System.out.print("Enter message: ");
            text = sc.nextLine();

            buffer = text.getBytes(); //CONVERT TEXT TO BYTE[]

            // 2 CREATE UDP/DATAGRAM PACKET
            packet = new DatagramPacket(buffer, buffer.length);
            // 3 SEND UDP PACKET USING SOCKET
            socket.send(packet);

            byte[] rcv = new byte[65535];
            DatagramPacket p = new DatagramPacket(rcv,rcv.length);
            socket.receive(p);

            if(text.equals("/exit"))
                runtime = false;
            else{
                System.out.println(data(p.getData()));
            }
        }
        
        // 4 CLOSING SOCKET
        socket.close();

        //CLOSING SCANNER
        sc.close();
    }


    /**
     * Reassembles byte[] as string
     * @param b Received packet in byte[] 
     * @return Reassembled String from b.
     */
    private static String data(byte[] b){
        if(b == null)
            return null;
        StringBuilder sb = new StringBuilder();
        int i, len = b.length;
        for(i = 0; i < len; i++)
            sb.append((char)b[i]);
        return sb.toString();
    }
}
