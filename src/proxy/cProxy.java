/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy;
import screen.load;
import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import javax.swing.JFrame;


/**
 *
 * @author Administrator
 */
public class cProxy extends Thread{
    public static int speed = 30 * 1024;
    public static int maxspeed = 20 * 1024 * 1024;
    public static int maxconn = 10;
    public static int maxTransfer = 10 * 1000;
    public static int conn = 0;
    private ServerSocket incoming = null;
    private InetAddress inetA = null;
//    private Socket server;
    private Socket client;
    public static boolean stop = false;
    public static int port = 0;
    load frame;
    private HashMap hash;
    private String host;
    private String mac;
    public cProxy(load frame){
        this.frame = frame;
    }
    public void setFrame(load frame){
        this.frame = frame;
    }
    public static double speed(){
        return speed;
    }
    
    public void setport(int port){
        this.port = port;
    }
    
    public void sethost(String host){
        this.host = host;
        System.out.println("host="+host);
    }
    
    public void stop(boolean s){
        stop = s;
    }
    
    public void disconnect(){
        stop = true;
        try{
            if(client!=null) client.close();
            if(incoming!=null) incoming.close();
        }catch(IOException e){}
    }
    
    public void run(){
        proxy.Time t = new proxy.Time(frame);
        t.start();
       try{
           
           inetA = InetAddress.getByName(host);           
//           incoming = new ServerSocket(port);
           incoming = new ServerSocket(port, 0, inetA);
       }catch(IOException ex){
           System.err.println("Error IO "+ex.getMessage());
           stop = true;
           frame.printf("Error Occurred when connecting: %s", ex.getMessage());
           frame.showErr("Error: "+ex.getMessage());
           frame.disconnect();
           
       }
       frame.display("Connected to port "+port);
        while(!stop){
                try{
                    new ProxyThread(incoming.accept(), frame).start();
                }catch(UnknownHostException ex){
                    System.out.println("unknow host "+ex.getMessage());
                }catch(IOException ex){
                    System.out.println("IO thread "+ex.getMessage());
                }catch(NoClassDefFoundError e){
                    System.out.println("Error classnofound"+e.getMessage());
                }
       }
        frame.display("Discconnected");
        t.stopNow();
   
    } 
    
    public static void main (String [] args){
        Hashtable<String,String> h = new Hashtable<>();
        h.put("mz", "10");
        String a = (String)h.get("mzk");
        System.out.println(a);
    }    
}
