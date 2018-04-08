/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy;
import java.io.*;
import java.net.*;
import java.util.*;
import screen.load;

/**
 *
 * @author Administrator
 */
public class ProxyThread1 extends Thread{
    Socket client, server;
    load frame;
    Hashtable hashv;
    public ProxyThread1(Socket client, Socket server,load f,Hashtable h) {
        this.client = client;
        this.server = server;
        this.frame = f;
        hashv = h;
    }
    
    @Override
    public void run(){
        byte [] buffer = new byte[2048];
        int numberRead = 10;
        OutputStream toServer;
        InputStream fromClient;
        related r = new related(frame);
        String mac = (String) hashv.get("Mac Address");
        String id = Thread.currentThread().getName();
            try {
                toServer = server.getOutputStream();
                fromClient = client.getInputStream();
                StringBuffer buf = new StringBuffer();
                int a = 0;
               
                while(true){
                            numberRead = fromClient.read(buffer);
                            a++;
                            if(true){
                                buf.append(new String(buffer));
                                int b = buf.toString().toLowerCase().indexOf("http://info/");
                                int bb = buf.toString().toLowerCase().indexOf("www.info.com");
                                if((b>-1 | bb>-1)){
                                    int bbb = buf.toString().toLowerCase().indexOf("http://info/?more=x1234");
                                    boolean include = true;
                                    if(bbb>-1)
                                        include = false;
                                    r.writeInfoToClient(hashv, client, server,include);
                                    frame.close(client);
                                    break;
                                }else{
                                    int e = buf.toString().toLowerCase().indexOf("http://logout/");
                                    int ee = buf.toString().toLowerCase().indexOf("www.logout.com");
                                    if((e>-1 | ee>-1) & !r.isExempted(mac)){
                                        r.writeToClient(client, "Logout successfully", "Logout");
                                        frame.logout(mac);
                                        frame.print(hashv.get("Computer")+": User has logout out manually");
                                        break;
                                    }
                                    
                                }
                            int c = buf.toString().toLowerCase().indexOf("host:");
                            int d = buf.indexOf("\n", c);
                            if(c>-1 & d > c){
                                String host = buf.substring((c+5), d).trim();
                                if((host.equalsIgnoreCase("info") | host.equalsIgnoreCase("www.info.com") 
                                        | host.equalsIgnoreCase("info.com"))){
                                    int bbbb = buf.toString().toLowerCase().indexOf("http://info/?more=x1234");
                                    boolean include = true;
                                    if(bbbb>-1)
                                        include = false;
                                    r.writeInfoToClient(hashv, client, server,include);
                                    frame.close(client);
                                    break;
                                }
                                returnH("Host").put(id, host);
                            }
                        }
                       if(load.recordb)
                            addTotalDownload(buffer.length);    
                        if(numberRead == -1){                          
                            client.close();
                            server.close();
                            break;
                          }

                        toServer.write(buffer, 0, numberRead);
                        
                         
                }
              
    }catch(IOException ex){}
     catch(ArrayIndexOutOfBoundsException exe){}
    
    }
    
    private void addTotalDownload(int value){
        try{
            tableTimer tT = (tableTimer) hashv.get("Thread");
            tT.updateSpeeedUpload(hashv, value);
        }catch(NullPointerException e){
            System.out.println("Null Exception AddtoDownload="+e.getMessage());
        }    
   }
    
    public static void main(String arg[]){
    long time = System.currentTimeMillis();
    System.out.println(time);
    try{
        sleep(1);
        double x = 50;
        double y = 1000;
        double speed = 1000;
    double bypersec = (1000/x)*y;
    if(bypersec>speed){
    
    long sleep = (long) (((bypersec/speed)*1000)-1000);
//    sleep = Math.round(1.0);
    System.out.println("sleeping....."+sleep);    
    }
    
    System.out.println(bypersec);
    }catch(InterruptedException e){}
    System.out.println(System.currentTimeMillis());
    }
    
    private Hashtable returnH(String h){
        Hashtable hash = (Hashtable) hashv.get(h);
        return hash;
    }
}
    

