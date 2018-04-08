/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy;
import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import screen.load;

/**
 *
 * @author Administrator
 */
public class ProxyThread21 extends Thread{
    Socket server, client;
    int speed;
    load frame;
    Hashtable hashv;
    
    public ProxyThread21(Socket server, Socket client,load f,Hashtable h) {
        this.server = server;
        this.client = client;
        frame = f;
        hashv = h;
        
    }
    
    
    
    public void run(){
        
        int numberRead = 10;
        OutputStream toClient;
        InputStream fromServer;
            try {
                toClient = client.getOutputStream();
                fromServer = server.getInputStream();
                int tbyte = 0;
                int remain = 0;
                int use = 1024+1024+1024;
                int ttime = 0;
                int step = 0;
long timestamp = System.currentTimeMillis();
int counter = 0;
int INTERVAL = 1000; // one second
int LIMIT = 20 * 1024; // bytes per INTERVAL
ByteArrayOutputStream bb = new ByteArrayOutputStream();
//ByteArrayInputStream bt = new ByteArrayInputStream(buf)
    byte [] buf = new byte[use];
//    LimitedBandwidthStream l = new LimitedBandwidthStream(fromServer, cProxy.speed());
    System.out.println(".........\n...........speed="+cProxy.speed());
                while(true){
                        
//                        speed = (int) Proxy1.speed(Proxy1.conn);
//                        byte [] buffer = new byte[speed];
//                        if(remain == 0)  use = speed;  
//                        else use = remain;
                            
                        
//                        long startTime = System.currentTimeMillis();
//                        System.err.println(speed+"="+use);
//                        numberRead = fromServer.read(buffer, 0, use);
                        
                        
                        
//                        toClient.write(buffer, 0, numberRead);
//                        long endTime = System.currentTimeMillis();
//                        int time = (int) (endTime - startTime);
//                        ttime += time;
//                        tbyte += numberRead;
    if (false) {
        System.err.println((step++)+"count="+counter+" limit="+cProxy.speed());
        long now = System.currentTimeMillis();
        if (timestamp + INTERVAL >= now) {
            System.err.println((step++)+"sleeping="+(timestamp + INTERVAL - now)+" timestamp="+timestamp+" now="+now);
            try {  
                toClient.write(bb.toByteArray());
                Thread.sleep(timestamp + INTERVAL - now);
            } catch (InterruptedException ex) {
                Logger.getLogger(ProxyThread2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }}
//        else{toClient.write(bb.toByteArray());}
//        now = System.currentTimeMillis();
//        timestamp = now;
//        counter = 0;
//        buf = new byte[(1024+1024+1024)];
//        bb = new ByteArrayOutputStream();
//              
//    }
//    remain = Proxy1.speed() - counter;
    
//    int res = fromServer.read(buf);
//    System.out.print((char)res);
     int res = fromServer.read(buf);   
    toClient.write(buf);
    
    if (res >= 0) {
        counter+=buf.length;
    }
    hashv.put("Transfer rate", step);
    frame.update(hashv.get("Mac Address")+"", hashv);
    System.err.println(res+"speed="+(char)res);
//                                        double s = (double) time;
//                                        double y = numberRead;
//                                        int speed = this.speed;
//                      System.out.println((step++)+"=bytesIn="+numberRead+" tbyte="+tbyte+" time="+ttime+" speed="+speed);
//                                        if(ttime<1000){
//                                            if(tbyte < speed){
//                                                remain = speed - tbyte;
//                                                System.err.println((step++)+"=contining="+remain); 
//                                                continue;
//                                            }else if(tbyte == speed){
//                                                System.err.println((step++)+"=sleeping="+(1000-ttime)); 
//                                                slp((1000 - ttime));
//                                                ttime = 0;
//                                                tbyte = 0;
//                                                continue;
//                                            }
//                                        }else {
//                                            System.err.println((step++)+"=not sleeping="+(ttime)); 
//                                            tbyte = 0; ttime = 0;
//                                        }
//                                            
//                                            if(tbyte >= speed){
//                                             s = ttime;   
//                                             ttime = 0;
//                                             remain = 0;
////                                             System.out.println("total byte="+tbyte+" time="+s+ " speed="+speed);
//                                             tbyte=0;
//                                             numberRead = 0;
//                                             if(s>=1000)continue;
//                                            }else if(numberRead < speed){
//                                            tbyte +=numberRead;
//                                            s = ttime; 
//                                            if(tbyte >= speed){
//                                                
//                                            }else{
//                                            remain = (int) (speed - tbyte);
////                                            System.out.println("bytesIn="+numberRead+" tbyte="+tbyte+" time="+s+" speed="+speed);
//                                            continue;}
//                                            } 
//                                            tbyte=0; ttime=0;remain = 0;
////                                            System.err.println("sleep for "+(1000-s)+" millisec");
//                                              try{
//                                              sleep(1000-(int)s);
//                                              }catch(InterruptedException e){System.out.println("interupted");}
//                                       }else System.out.println("not sleeping");
//                        if(tbyte<speed & ttime < 1000){
//                             remain = speed - tbyte;
//                             System.err.println("read="+numberRead+" speed="+speed+" time="+ttime+"sleep="+(1000-time)+" remain="+remain);
//                             continue;
//                        }
//                        System.err.println("read="+numberRead+" speed="+speed+" time="+ttime+"sleep="+(1000-time));
//                        if(ttime < 1000){
//                        try {
//                            
//                            sleep(1000-ttime);
//                            
//                        } catch (InterruptedException ex) {
//                            Logger.getLogger(ProxyThread2.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                            ttime = 0; remain = 0;tbyte = 0;
//                        }
////                        System.out.println("reading from client="+new String(buffer));
//                        
//                        
//                        if(ttime < 1000){
//                            
//                        }
//                        int len = buffer.length;
//                        sleep(time,buffer);
//                        System.out.println("wrote to client=");
                         if(res <= 0){
                            client.close();
                            server.close();
                            break;
                            }
                }
    }catch(IOException ex){System.out.println("error IO = "+ex.getMessage());
    }catch(ArrayIndexOutOfBoundsException exe){System.out.println("error Array = "+exe.getLocalizedMessage());}
            System.err.println("disconnected proxy2");
            cProxy.conn -= 1;
//            up();
                     
    }
    public int blen(byte[] b){
    return b.length;
    }
    
    
    
    public void disc(){
        
    }
    
    public void slp(int time){
        try {
            sleep(time);
        } catch (InterruptedException ex) {
            Logger.getLogger(ProxyThread2.class.getName()).log(Level.SEVERE, null, ex);
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
}
    

