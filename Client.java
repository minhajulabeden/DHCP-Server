import java.io.*;
import java.lang.ref.Cleaner.Cleanable;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Client {
  public static  int serverPort=67;
  public static  byte BOOTREQUEST = 1;
  public static  byte BOOTREPLY = 2;
  public static  byte opcode = 1;
  public static  byte HardWareType = 1;
  public static  byte HardWareLength=6;
  public static  byte HopCount = 0;
  public static  byte[] transactionId = new byte[4];
  public static  byte[] clientIp = new byte[4];
  public static  byte[] yourIp = new byte[4];
  public static  byte[] serverIp = new byte[4];
  public static  byte[] clientHardwareAddress = new byte[16];
  public static  byte[] finalByte = new byte[40];

  public static void main(String[] args) throws Exception {
    InetAddress address = InetAddress.getLocalHost();
    int port = 67;

    // send a DHCP discover message to the server
    byte[] message = DHCPdiscover();
    String messageString = new String(message);
    System.out.println("data:"+Arrays.toString(message));
    System.out.println(messageString);
    DatagramPacket requestPacket = new DatagramPacket(message, message.length, address, port);
    DatagramSocket socket = new DatagramSocket();
    socket.send(requestPacket);
    System.out.println("Sent DHCP discover message to server.");

    // receive the response from the server
    byte[] buffer = new byte[1024];
    DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
    socket.receive(responsePacket);
    String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
    System.out.println("Received message from server: " + response);

    socket.close();
}

    public static byte[] DHCPdiscover() {
       // System.out.println("DHCP Discover massege:");
        ByteBuffer a = ByteBuffer.allocate(4);
        a.putInt(transactionId.hashCode());
        transactionId = a.array();
        System.out.println("ID:"+Arrays.toString(transactionId));
        int[] b = { 19, 68, 0, 2 };
        clientIp = Converter.toBytes(b);
        System.out.println(Arrays.toString(clientIp));
        int[] c = { 0, 0, 0, 0 };
        yourIp = Converter.toBytes(c);
        int[] d = { 19, 68, 0, 1 };
        serverIp = Converter.toBytes(d);
        System.out.println("serverIp:"+Arrays.toString(serverIp));
        clientHardwareAddress =Converter.toBytes(getMacAddress());
        byte[] packet=DHCPdiscoverPacket();
        return packet;
    }

    public static byte[] DHCPdiscoverPacket(){
        finalByte[0]=opcode;
        finalByte[1]=HardWareType;
        finalByte[2]=HardWareLength;
        finalByte[3]=HopCount;
       
        int j=4;
        for(int i=0;i<transactionId.length;i++){
            finalByte[i+j]=transactionId[i];
        }
        j=j+transactionId.length;
        for(int i=0;i<clientIp.length;i++){
            finalByte[i+j]=clientIp[i];
        }
        j=j+yourIp.length;
        for(int i=0;i<yourIp.length;i++){
            finalByte[i+j]=yourIp[i];
        }
        j=j+serverIp.length;
        for(int i=0;i<serverIp.length;i++){
            finalByte[i+j]=serverIp[i];
        }
        j=j+clientHardwareAddress.length;
        for(int i=0;i<clientHardwareAddress.length;i++){
            finalByte[i+j]=clientHardwareAddress[i];
        }
       // System.out.println("mac:"+Arrays.toString(clientHardwareAddress));
        //System.out.println(Arrays.toString(finalByte));
        return finalByte;
    }

    public static int[] getMacAddress(){
        byte[] mac=null;
        int[] macAddressIntArray=new int[48];
        try{
            NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            mac = ni.getHardwareAddress();
            if (mac != null) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < mac.length; i++) {
                    stringBuilder.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                }
                String macAddressString = stringBuilder.toString();
                String[] macAddressComponents = macAddressString.split("-");
                macAddressIntArray=new int[macAddressComponents.length];
                 macAddressIntArray = new int[macAddressComponents.length];
                 for (int i = 0; i < macAddressComponents.length; i++) {
                    macAddressIntArray[i] = Integer.parseInt(macAddressComponents[i], 16);
             }
               // System.out.println("mac:"+ stringBuilder.toString());
            }  
           
        }catch(
            Exception exception
        ){
            System.out.println(exception.getMessage());
        }
       
        return macAddressIntArray;
    }
}