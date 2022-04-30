/**
 * Reference: https://www.geeksforgeeks.org/working-udp-datagramsockets-java/
 * Modified accordingly for better execution.
 */

import java.io.*;
import java.net.*; //use DatagramPacket, DatagramSocket, InetAddress, and SocketException
import java.util.Random;
import java.util.Scanner;
import java.util.zip.*;

public class client_udp{
    private DatagramSocket socket;
    private DatagramPacket packet;
    private InetAddress tgtIP;
    private byte[] buffer;
    private boolean runtime = false;
    private int port;
    private Scanner sc;
    
    public static void main(String[] args){
        cls();

        client_udp c = new client_udp();
        c.sc = new Scanner(System.in);
        
        try{
            String target = "";
            int p = -1;
            do{
                System.out.print("Enter target server (localhost if otherwise): ");
                target = c.sc.nextLine();
                do{
                    System.out.print("Enter port # to target server (Valid Range: 49152-65535): ");
                    p = Integer.parseInt(c.sc.nextLine());
                }while(p < 49152 || p > 65535);
            }while(!c.activate_client_udp(target,p));
            c.talk();

            //4. CLOSING SOCKET
            c.socket.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void liveBanner() throws IOException{
        cls();
        System.out.println("Activating Client...");

        //TELL USER IF TARGET IS REACHABLE OR NOT
        if(this.tgtIP.isReachable(5000))
            System.out.println(this.tgtIP.getHostName() + " is reachable.");
        else
            System.out.println(this.tgtIP.getHostName() + " is not reachable.");

        //DISPLAY SOCKET INFORMATION
        System.out.println("Socket is binded to: " + socket.getLocalPort());
        System.out.println("Socket is connected to: " + this.tgtIP.getHostAddress() + ":" + socket.getPort());
        
        //DISPLAY SPECIAL COMMANDS
        System.out.println("==============================");
        System.out.println("Special Commands: ");
        System.out.println("/exit : Exit Client");
        System.out.println("/hardexit : Exit Client and Server");
        System.out.println("/cls : Perform cls on Client");
        System.out.println("/hardcls : Perform cls on Client and Server");
        System.out.println("==============================");
    }

    public boolean activate_client_udp(String target, int port) throws IOException{
        //1. CREATE/OPEN A SOCKET
        this.socket = new DatagramSocket();
        
        //1.1 SPECIFY TARGET PORT
        this.port = port;

        //1.3 CONNECT SOCKET TO TARGET IP AND PORT
        this.tgtIP = InetAddress.getByName(target);
        socket.connect(this.tgtIP, this.port);

        {//SETS IP ADDRESS DEPENDING ON INPUT, EITHER COMPUTER ON NETWORK OR LOCALHOST
        if(target.toLowerCase().equals("localhost"))
            this.tgtIP = InetAddress.getLocalHost();
        else
            this.tgtIP = InetAddress.getByName(target);
        }
                
        return this.tgtIP.isReachable(5000);
    }

    public void talk() throws IOException{
        Scanner scan = new Scanner(System.in); 
        this.buffer =  null;
        this.runtime = true;
        
        liveBanner();
        
        //ASKS FOR INPUT CONTINOUSLY UNTIL EXIT FLAG ('/exit'); CALLS FOR transmit(<String>); WHEN ACTUALLY 'TALKING'.
        while(runtime){
            //GET INPUT
            System.out.print("Enter message: ");
            String in = sc.nextLine();
            
            if(in.equals("/cls")){
                liveBanner();
            }else if(in.equals("/hardcls")){
                liveBanner();
                transmit(in);  
            }else if(in.equals("/exit") || in.equals("/hardexit")){ //CLIENT ONLY EXIT FLAG
                if(in.equals("/hardexit"))
                    transmit(in);
                scan.close();
                runtime = false;
            }else if(in.equals("/bruteforce") || in.equals("/flood")){ //BRUTEFORCE OR FLOOD MODE
                System.out.print("Enter # of words: ");
                int len = Integer.parseInt(sc.nextLine());
                String[] list = generateRandomWords(len);
                for(int i = 0; i < list.length; i++)
                    transmit(list[i]);
            }else //SEND ONE MESSAGE
                transmit(in);
        }
    }

    public void transmit(String in) throws IOException{
        //OPTIONAL: COMPRESS BYTES
        compression c = new compression();
        byte[] compressed = c.compress(in.getBytes(), Deflater.BEST_COMPRESSION, false);

        //CONVERT INPUT IN INTO BYTE[]
        this.buffer = compressed;
        
        //2. CREATE UDP/DATAGRAM PACKET
        //IF SOCKET WAS NOT CONNECTED (1.3) PRIOR TO CREATING DATAGRAMPACKET, USE MUST
        //USE NEW DATAGRAM PARAMS: (BUFFER, BUFFER.LENGTH, INETADDRES TARGET, PORT)
        this.packet = new DatagramPacket(this.buffer, this.buffer.length);

        //3. SEND PACKET TO SERVER USING SOCKET
        this.socket.send(packet);
        buffer = null;
    }

    //so that it'll be quick https://stackoverflow.com/a/4952066
    private static String[] generateRandomWords(int numberOfWords){
        String[] randomStrings = new String[numberOfWords];
        Random random = new Random();
        for(int i = 0; i < numberOfWords; i++)
        {
            char[] word = new char[random.nextInt(8)+3]; // words of length 3 through 10. (1 and 2 letter words are boring.)
            for(int j = 0; j < word.length; j++)
            {
                word[j] = (char)('a' + random.nextInt(26));
            }
            randomStrings[i] = new String(word);
        }
        return randomStrings;
    }
    
    private final static void cls(){
        //https://stackoverflow.com/a/32295974
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        banner();
    }

    private final static void banner(){
        System.out.println("==============================\n          UDP CLIENT\n==============================");
    }
}