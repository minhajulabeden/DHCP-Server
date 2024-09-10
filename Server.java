import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

import javax.print.attribute.standard.Destination;
class Server {

    public static void main(String[] args) throws Exception {
        
        byte[] buffer = new byte[1024];
        byte[] responseData=new byte[1024];
        String line=new String();
        int port = 67;
        
        DatagramSocket socket = new DatagramSocket(port);
        System.out.println("DHCP server started on port " + port);


        while (true) {
            //Receive packet
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

           // String message = new String(packet.getData(),0,packet.getLength());
            byte[] dhcpDiscoverMessage = new byte[packet.getLength()];
            System.arraycopy(packet.getData(), packet.getOffset(), dhcpDiscoverMessage, 0, packet.getLength());
           
            //Process DhcpDiscover method
            processDHCPDiscoverMessage(dhcpDiscoverMessage);

            // Clear the buffer for the next receive operation
            packet.setLength(buffer.length);
            System.out.println(" hi ");

            int destinationPort = 68;
            //DatagramSocket socket=new DatagramSocket(destinationPort);

            // process the message and send a response to the client
            socket.setBroadcast(true);

            // Create the DHCP offer packet
            byte[] offerPacket = DHCPOffer(packet);
            System.out.println(Arrays.toString(offerPacket));

            // Set the destination address and port to the broadcast address
            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
             
            DatagramPacket responsePacket = new DatagramPacket(offerPacket, offerPacket.length,broadcastAddress , destinationPort);
            System.out.println(responsePacket.getData());
            socket.send(responsePacket);
            //socket.close();
        
        }
    }

   /* 
        DhcpIpPool ipPool=new DhcpIpPool();
        String newIp=ipPool.allocateIp(); 
        System.out.println(newIp);
        int tmp=0,j=16;
        for(int i=0;i<newIp.length();i++){
            
            if(newIp.charAt(i)!='.'){
                char str=newIp.charAt(i);
                tmp=(int)str;
            }
            else  {
                offerData[j]=(byte)tmp;
                j++;

            }*/
    

            public static byte[] DHCPOffer(DatagramPacket receivePacket) throws UnknownHostException
            { 
                DhcpIpPool pool=new DhcpIpPool();
                String myIp;
                System.out.print("Building DHCPOffer\n");
                DHCPMessage message = new DHCPMessage(receivePacket.getData());
                
                message.opCode = DHCPMessage.BOOTREPLY;
                
                //Reserve new IP address
                //int leaseDuration = Utility.toInt(message.getOptionData((byte)51));//(int)(Math.random()*300);
                myIp=pool.allocateIp();
                message.yourIP =Utility.stringToByte(myIp);
                System.out.println(message.yourIP.length);
                
                String str=Utility.byteToString(message.yourIP);
                System.out.println("IP Address:"+str);
                // Server IP set
                message.serverIP = InetAddress.getByName("10.5.201.82" ).getAddress();
                
                // reset options fields
                message.resetoptions();
                
                // Set option 53 to value 2
                byte[] i = {DHCPMessage.DHCPOFFER};
                message.addOption((byte)53, (byte)1, i);
                
                message.addOption((byte)54, (byte)4, message.serverIP);
                
                // Set option 255
                int[] j = {0};
                message.addOption((byte) 255, (byte)0, Utility.toBytes(j));
        
                InetAddress IPClient = receivePacket.getAddress();
                int portClient = receivePacket.getPort();
                
                byte[] sendingBytes = message.retrieveBytes();
               // DatagramPacket response = new DatagramPacket(sendingBytes, sendingBytes.length, IPClient, portClient);
               return sendingBytes;
            } 
    
    private static void processDHCPDiscoverMessage(byte[] dhcpDiscoverMessage) {
        // Convert the byte array to a string or process it as needed
        String message = new String(dhcpDiscoverMessage);
        int opCode=dhcpDiscoverMessage[0];
        System.out.println("DHCP Operation Code: " + opCode);
        int HardWareType=dhcpDiscoverMessage[1];
        System.out.println("DHCP Hardware Type: "+HardWareType);
        //MAC address
        byte[] macAddress = Arrays.copyOfRange(dhcpDiscoverMessage, 28, 34);
        String macAddressString = formatMacAddress(macAddress);
        System.out.println("Client MAC Address: " + macAddressString);
        // Print the DHCP discover message
        System.out.println("Received DHCP discover message: " + message);
        byte[] requestedIp = Arrays.copyOfRange(dhcpDiscoverMessage, 12, 16);
        InetAddress requestedIpAddress = bytesToInetAddress(requestedIp);
        System.out.println("Requested IP Address: " + requestedIpAddress.getHostAddress());
        //  server ip address
        byte[] serverIp=Arrays.copyOfRange(dhcpDiscoverMessage,20,24);
        InetAddress serverIpAddress=bytesToInetAddress(serverIp);
        System.out.println("Server ip address:"+serverIpAddress);
        // Extract and print the DHCP options
        byte[] dhcpOptions = Arrays.copyOfRange(dhcpDiscoverMessage, 240, dhcpDiscoverMessage.length);
        System.out.println("DHCP Options: " + bytesToHexString(dhcpOptions));
    }
    private static String formatMacAddress(byte[] macAddress) {
        StringBuilder sb = new StringBuilder();
        for (byte b : macAddress) {
            sb.append(String.format("%02X:", b));
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }
    private static InetAddress bytesToInetAddress(byte[] ipAddress) {
        try {
            return InetAddress.getByAddress(ipAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}