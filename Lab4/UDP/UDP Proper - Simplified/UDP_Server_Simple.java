//SERVER SIDE
//REFERENCE: https://www.geeksforgeeks.org/working-udp-datagramsockets-java/

import java.io.*;
import java.net.*;

public class UDP_Server_Simple {
    public static void main(String[] args) throws IOException{
        DatagramSocket socket = null;
        DatagramPacket packet = null;
        byte[] receive = null;

        
        int port = 65535; //PORT THAT IT'LL BIND AND LISTEN TO

        // 1 CREATE/OPEN SOCKET THAT THE SERVER WILL BIND TO
        socket = new DatagramSocket(port); //BINDS SOCKET TO PORT SPECIFIED IN PARAMETER
        
        // 1.1 DISPLAYING SOCKET INFORMATION (SHOULD BE THE SAME AS VALUE OF PORT)
        System.out.println("Socket is binded to: " + socket.getLocalPort());

        receive = new byte[1]; //SET BUFFER SIZE FOR RECEIVING DATA
        /** WHY 65535 BYTES?
         * UDP Max Payload Size: 65507 bytes (IPv4) & 65527 bytes (IPv6)
         * UDP Header Size: 8 bytes
         * UDP Total Size: 65515 bytes (IPv4) & 65535 bytes (IPv6)
         * Reference: https://erg.abdn.ac.uk/users/gorry/course/inet-pages/udp.html
         */

        //LISTEN LOOP
        boolean runtime = true;
        while(runtime){
            // 2 CREATE A DATAGRAM PACKET TO RECEIVE THE DATA
            packet = new DatagramPacket(receive, receive.length);

            // 3 RECEIVE THE DATA IN BYTE BUFFER
            socket.receive(packet);

            System.out.println("Packet length: " + packet.getData().length);

            // 3.1 PRINT MESSAGE FOR USER
            String rec_ip = packet.getAddress().getHostAddress();
            String rec_host = packet.getAddress().getHostName();
            System.out.println("Client " + rec_ip + ":" + socket.getLocalPort() + " (" + rec_host + "): " + data(receive));

            //RESET RECEIVE BYTE[]
            receive = new byte[65535];

            //CHECK FOR EXIT
            if(data(receive).equals("/exit")){
                System.out.println("Client " + rec_ip + " has exited.\nClosing server...");
                runtime = false;
            }
        }

        // 4 CLOSING SOCKET
        socket.close();
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