/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import static java.lang.System.*;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
//import jpcap.JpcapCaptor;

/**
 *
 * @author MZ
 */
public class MacAddress extends Mac{
//    public void setDevice
    public static void main(String args[]) throws SocketException{
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets))
            displayInterfaceInformation(netint);
//            System.out.println(MacAddress.getName(11));
    }

    static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        out.printf("Display name: %s\n", netint.getDisplayName());
        out.printf("Name: %s\n", netint.getName());
        out.printf("Mac: %s\n", getMac(netint.getHardwareAddress()));
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            out.printf("InetAddress: %s\n", inetAddress);
            
        }
        out.printf("\n");
        System.out.println(netint.getIndex());
     }
    
    public static String getName(int networkIndex) throws SocketException{
//      
        if(networkIndex==-2)
            return "127.0.0.1";
        NetworkInterface device = NetworkInterface.getByIndex(networkIndex);
        Enumeration enu = device.getInetAddresses();
        while(enu.hasMoreElements()){
            InetAddress in = (InetAddress) enu.nextElement();
            System.out.println("Connecting to "+in.getHostAddress()); 
            return in.getHostAddress();
        }
        return null;
    }
    
    public static String getDevice(byte[] mac){
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
                for(NetworkInterface c :Collections.list(nets)){
                    byte [] m = c.getHardwareAddress();
                    if(Arrays.equals(mac, m))
                       return c.getName();
                }
        } catch (SocketException ex) {
            Logger.getLogger(MacAddress.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
//    public static HashMap<String,Integer> getNetworkCard(int a){
//        HashMap<String,Integer> h = new HashMap<String,Integer>();
////            jpcap.NetworkInterface[] devices = JpcapCaptor.getDeviceList();
//            for(int i = 0; i <devices.length; i++){
//                System.out.println("foreach mac = "+devices[i].description+"="+i);
//                h.put(devices[i].description,i);}
//        
//        return h;
//    }
    public static HashMap<String,Integer> getNetworkCard() throws SocketException{
        HashMap<String,Integer> h = new HashMap<String,Integer>();
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        
        for(NetworkInterface c :Collections.list(nets)){
            String mac = getMac(c.getHardwareAddress());
            if(mac.length()>8){
                h.put(c.getDisplayName(),c.getIndex());}
    }
        return h;
    }
        
    public static String getMac(byte[] mac){
        if(mac == null) return "";
        String a = "";
        for (byte b : mac)
        a = a+Integer.toHexString(b&0xff)+ ":";
        return a;
    }
    
}
