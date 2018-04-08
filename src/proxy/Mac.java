package proxy;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.*;
import java.util.Arrays;

//import jpcap.*;
//import jpcap.packet.*;

public class Mac{
//    public static jpcap.NetworkInterface device;
    
    
//    public static byte[] getMac(InetAddress ip) throws java.io.IOException{
//                //find network interface
////                jpcap.NetworkInterface[] devices = JpcapCaptor.getDeviceList();
////                jpcap.NetworkInterface device=null;
//        byte[] bip = null;
//        byte[] bif = null;
////                    System.out.println(devices.length);
////        loop:    for(jpcap.NetworkInterface d:devices){
////                    for(NetworkInterfaceAddress addr:d.addresses){
//                        if(!(addr.address instanceof Inet4Address)) continue;
//                       bip=ip.getAddress();
//                       System.out.println(ip.getHostAddress());
//                        byte[] subnet=addr.subnet.getAddress();
//                        bif=addr.address.getAddress();
//                        for(int i=0;i<4;i++){
//                            bip[i]=(byte)(bip[i]&subnet[i]);
//                            bif[i]=(byte)(bif[i]&subnet[i]);
//                        }
//                        if(Arrays.equals(bip,bif)){
//                            device=d;
//                            break loop;
//                        }
//                    }
//                }
//                if(device==null)
//                    throw new IllegalArgumentException(ip+" is not a local address"+new String(bif));
//
//                //open Jpcap
//                JpcapCaptor captor=JpcapCaptor.openDevice(device,2000,false,3000);
//                captor.setFilter("arp",true);
//                JpcapSender sender=captor.getJpcapSenderInstance();
//
//                InetAddress srcip=null;
//                for(NetworkInterfaceAddress addr:device.addresses)
//                    if(addr.address instanceof Inet4Address){
//                        srcip=addr.address;
//                        break;
//                    }
//
//                byte[] broadcast=new byte[]{(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255};
//                ARPPacket arp=new ARPPacket();
//                arp.hardtype=ARPPacket.HARDTYPE_ETHER;
//                arp.prototype=ARPPacket.PROTOTYPE_IP;
//                arp.operation=ARPPacket.ARP_REQUEST;
//                arp.hlen=6;
//                arp.plen=4;
//                arp.sender_hardaddr=device.mac_address;
//                arp.sender_protoaddr=srcip.getAddress();
//                arp.target_hardaddr=broadcast;
//                arp.target_protoaddr=ip.getAddress();
//
//                EthernetPacket ether=new EthernetPacket();
//                ether.frametype=EthernetPacket.ETHERTYPE_ARP;
//                ether.src_mac=device.mac_address;
//                ether.dst_mac=broadcast;
//                arp.datalink=ether;
//
//                sender.sendPacket(arp);
//
//                while(true){
//                    ARPPacket p=(ARPPacket)captor.getPacket();
//                    if(p==null){
//                        throw new IllegalArgumentException(ip+" is not a local address");
//                    }
//                    if(Arrays.equals(p.target_protoaddr,srcip.getAddress())){
//                        return p.sender_hardaddr;
//                    }
//                }
//    }
//    
    
//    public static byte[] gMac(InetAddress ip) throws IOException{
//
//        JpcapCaptor captor=JpcapCaptor.openDevice(device,2000,false,3000);
//        captor.setFilter("arp",true);
//        JpcapSender sender=captor.getJpcapSenderInstance();
//      
//        InetAddress srcip=null;
//        for(NetworkInterfaceAddress addr:device.addresses)
//            if(addr.address instanceof Inet4Address){
//                srcip=addr.address;
//                break;
//            }

//        byte[] broadcast=new byte[]{(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255};
//        ARPPacket arp=new ARPPacket();
//        arp.hardtype=ARPPacket.HARDTYPE_ETHER;
//        arp.prototype=ARPPacket.PROTOTYPE_IP;
//        arp.operation=ARPPacket.ARP_REQUEST;
//        arp.hlen=6;
//        arp.plen=4;
//        arp.sender_hardaddr=device.mac_address;
//        arp.sender_protoaddr=srcip.getAddress();
//        arp.target_hardaddr=broadcast;
//        arp.target_protoaddr=ip.getAddress();
//      
//        EthernetPacket ether=new EthernetPacket();
//        ether.frametype=EthernetPacket.ETHERTYPE_ARP;
//        ether.src_mac=device.mac_address;
//        ether.dst_mac=broadcast;
//        arp.datalink=ether;
//      
//        sender.sendPacket(arp);
//      
//        while(true){
//            ARPPacket p=(ARPPacket)captor.getPacket();
//            if(p==null){
//                throw new IllegalArgumentException(ip+" is not a local address");
//            }
//            if(Arrays.equals(p.target_protoaddr,srcip.getAddress())){
//                return p.sender_hardaddr;
//            }
//        }
//    }
    
//    public static String getMc(InetAddress inet) throws IOException{
//        StringBuffer buff = new StringBuffer();
//        for (byte b : gMac(inet))
//                buff.append(Integer.toHexString(b&0xff)+":");
//        return buff.toString();        
//    }  
    public static void main(String[] args) throws Exception{
//        if(args.length==0){
//            jpcap.NetworkInterface[] devices = JpcapCaptor.getDeviceList();
//            System.err.println(devices.length);
//            for(int i=0;i<devices.length;i++){
//                System.out.println(devices[i].addresses);
//            }
//            MacAddress.getName(1);
//        }else{
//            MacAddress.getName(1);
//            Mac.getMac(InetAddress.getByName("server"));

//            System.exit(0);
//        }
    }
}