/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy;
import java.io.*;
import java.net.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import screen.load;



/**
 *
 * @author Administrator
 */
public class ProxyThread2 extends Thread{
    Socket server, client;
    int speed;
    load frame;
    Hashtable hashv;
    String id = "";
    String mac = "";
    double size = 1000;
    int fByte = 0;
    int nByte = 0;
    int now = 0;
    static int time = 0;
    public ProxyThread2(){
        
    }
    public ProxyThread2(Socket server, Socket client,load f,Hashtable h) {
        this.server = server;
        this.client = client;
        frame = f;
        hashv = h;
        mac = (String)hashv.get("Mac Address");
    }
    
    int formalbyte = 0;
    
    public void run(){
     related r = new related(frame);
     id = Thread.currentThread().getName();
     int numberRead = 10;
     OutputStream toClient;
     InputStream fromServer;
         try {
                toClient = client.getOutputStream();
                fromServer = server.getInputStream();
                int tbyte = 0;
                int remain = 0;
                int use = 1024;
                long ttime = 0l;
                long uptime = 0;
                int upbyte = 0;
                byte [] buf = new byte[use];
                int u = buf.length;
//                int bandW = getBandW(false);
//                Conn(true);
//                fByte = totalspeed();
                StringBuffer buff = new StringBuffer();
//                buff.append("SERVER SIDE:....\n");
                int a = 0;
                while(!maxReached()){
//                    long timestamp = System.nanoTime();    
//                    u = use;
//                    if(remain<use)
//                        u = remain;

                    numberRead = fromServer.read(buf);   
//                    System.out.println("INCOMING SIZE="+buf.length);
                    if(a<2){
                        
                    }
                    
                    if(numberRead == -1){
                            close();
                            break;
                    }
//                    r.print(buf,buff);
                    addTotalDownload(buf.length);
                    
                    toClient.write(buf,0,numberRead);
//                    
//                    Long e = System.nanoTime();
//                    
//                    Long t = timediff(timestamp, e);
//                    tbyte += buf.length;
//                    ttime += t;
//                    uptime += t;
//                    upbyte += buf.length;
//                    
//                    remain = bandW - tbyte;
//System.out.printf("....Start Time = %d,Endtime=%d, ttime=%d tbyte=%d bandw=%d\n",timestamp,e,ttime,tbyte,bandW);
                    
//                    if(tbyte>=bandW){
//                            if(retTime(ttime)<=1000){
////                                System.out.print("sleeping but checking bonus 1st="+checkBonus()+"sleep for="+ttime);
//                                if(checkBonus()){
//                                    bandW = getBandW(true);
//                                    ttime = 0;tbyte=0;
//                                    continue;
//                                }
//                                slip(ttime);
//                            }else{
//                                double tym = retTime(ttime);
//                                double a = (tym - 1000.00)*(tbyte/1024.00);
//                                bonus((int)a,false);
//                                bandW = getBandW(false);
//                                
////                        System.out.printf("\nEXCEED TYME time=%d bonus=%d totalbyte=%d\n",(int)tym,(int) a,tbyte);
//                        ttime = 0l; tbyte = 0; remain = 0;;
//                        continue;
//                            }
//                        bandW = getBandW(false);
//                        ttime = 0l; tbyte = 0; remain = 0;;
//                        continue;
//                    }
//                    if(retTime(t) > 1000){
//                        double tym = retTime(t);
//                        double a = (tym - 1000.00)*(tbyte/1024.00);
//                        bonus((int)a,false);
//                        bandW = getBandW(false);
////                        System.out.printf("\nDOWN EXCEED TYME time=%d bonus=%d totalbyte=%d\n\n",(int)tym,(int) a,tbyte);
//                        ttime = 0l; remain = 0; tbyte=0;
//                    }
//                    if(retTime(ttime) < 1000){
//                        bandW = getBandW(true);
////                        ttime = 0l;
//                    }
                    
                    
                    
                         
                }
                
              
                
                if(error==1)
                    r.login(client,hashv);
                else if(error==2 |error ==3){
                    String t = "Maximum Connection Limit Reached, Reduce Open/loading pages then refresh.";
                    if(error == 3)
                        t = "Maximum Download or Browsing Limit Reached";
                    r.writeToClient(client, t);
                }
                closes();
         } catch( SocketTimeoutException | ConnectException ex){closes();
         }catch(IOException ex){closes();
    }catch(ArrayIndexOutOfBoundsException exe){closes();}
   
    
         
    }
    
    public boolean checkBonus(){
        Integer h = (Integer) hashv.get("Bonus");
        if(h>0)
            return true;
        return false;
    }
    
    public void closes(){
        try{ server.close();}catch(IOException e){}
    }
    
       
    public void closec(){
        try{ client.close();}catch(IOException e){}
    }
    
    public void close(){ closes();closec();}
    
    public int blen(byte[] b){
    return b.length;
    }
    int error = 0;
    
    private boolean isExempted(){
        Vector vec = (Vector) load.settings.get("Exempt");
        if(vec==null)
            return false;
        return vec.contains(mac);
    }
    
    public boolean maxReached(){
        if(!isExempted()){
            error = 1;
        try{
            int timeLeft = (int) hashv.get("Time Left");
            if(timeLeft==0){
                return true;
            }
        }catch(NullPointerException e){
            return true;
        }
        }

        if( (int) hashv.get("Connection Limit") <= (int)getConnections()){
            error = 2;
            return true;
        }if((int)hashv.get("Total Download") >= (int) hashv.get("Download Limit")){
            error = 3;
             if(!isExempted()){
            try{
                int timeLeft = (int) hashv.get("Time Left");
                int td = (int) hashv.get("Total Download");
                int dl = (int) hashv.get("Download Limit");
                long per = ((long)load.percentage*(long)dl)/100;
                long ttl = per + (long) dl;
                if(td >= ttl)
                    return true;
                if(timeLeft>0){
                    frame.r().reduceSpeed(hashv);
                    frame.r().reduceConnections(hashv);
                    return false;
                }
            }catch(NullPointerException e){
                    return true;
                }     
            }
            return true;
        }
        error = 0;
        return false;
    }
    
    public boolean check(){
        int max = (Integer) hashv.get("Download Limit");
        int down = (Integer) hashv.get("Total Download");
        if(down >= max){
            close();
        return false;
        }
        return true;
    }
    private Long timediff(long f,Long e){
        long a = e - f;
        return a;
    }
    
    public double getS(long f,Long e,int by){
        long a = (e - f);
        return getS(a, by);        
    }
    
    private double getS(Long time,int by){
        int t = retTime(time);
        double speed = 1;
        if(t <=1000){
            speed = by * (t+1000);
//        System.out.printf("%s * (%s+1000)=%s\n",by+"",t,speed+"");
        return (speed/(1000*1000));}
        if(t>1000){
            speed = by / (t+1000);
        System.err.printf("%s / (%s+1000)=%s\n",by+"",t,speed+"");
        }
        return speed;
    }
    
    
    
    public String mbcon(int v){
        String d = v+" Byte";
        if(v>=(size*size*size)){
            d = retSpeed((double) (v/(size*size*size)))+" GB";
            
        }
        else if(v>=(size*size))
            d = retSpeed((double) (v/(size*size)))+" MB";
        else if(v>=size){
            d = retSpeed(v/(size))+" KB";}
         return d;
    }
    
    public synchronized int spd(Long value){
        int newbyte = mb(0);
        int now = newbyte - formalbyte;
        double d = ((now/1000.00)/(value/1000000000.00));
//        System.out.printf("formal=%d new=%d diff=%d diffTime=%s multi=%s\n",formalbyte,newbyte,now,value+"",d+"");
        hashv.put("Transfer Rate",retSpeed(d)+" kb/s");
//        update();
           return 0; 
    }
    
    public synchronized int mb(int value){
//        System.out.println("mb add = "+value);
        Object a = hashv.get("Transfered");
        if(a==null)
            hashv.put("Transfered", 0);
        Integer h; 
        try{
           h = Integer.parseInt(hashv.get("Transfered")+""); 
           hashv.put("Total Transfer",mbcon((h+value)));
           hashv.put("Transfered",h+value);
    
        return h;
        }catch(NumberFormatException e){
            hashv.put("Total Transfer","0");
           return 0; 
        }
    }
    
    public synchronized void disconnect(){
        Integer h; 
        try{
           h = Integer.parseInt(hashv.get("Connections")+""); 
           if(h<=1){
           hashv.put("Connections","Idle");
           hashv.put("Transfer Rate", "--");}
           else hashv.put("Connections",(h-1));
           
        }catch(NumberFormatException e){
           hashv.put("Connections","Idle");
           hashv.put("Transfer Rate", "--");
        }
    }
    
    public synchronized int Conn(boolean add){
        Object a = hashv.get("Connections");
        if(a==null)
            hashv.put("Connections", add?1:"Idle");
        Integer h; 
        try{
           h = Integer.parseInt(hashv.get("Connections")+""); 
           if(add)
               hashv.put("Connections",(h+1));
        return h;
        }catch(NumberFormatException e){
            hashv.put("Connections",add?1:"Idle");
           return 0; 
        }
    }
    
    public double totalSpeed(){
        Hashtable<String,Double> h = (Hashtable<String,Double>) hashv.get("Total Speed");
        double s = 0;
        Enumeration e = h.elements();
        while(e.hasMoreElements()){
            double a = (Double) e.nextElement(); 
            s += a ;
        }
        return s;
    }
    
    
//   
//    public int getBandW(boolean t){
//        int maxSpeed = (Integer) hashv.get("Max Speed");
//        int down = getConnections();
//        int spd;
//        try{
//            spd = maxSpeed/down;
//        }catch(ArithmeticException e){
//            spd = 0;
//        }
//        int b= 0;
//        if(t)
//        b = bonus(0,true);
//        int speed = spd + b;
//        if(speed>maxSpeed){
//            bonus((speed-maxSpeed),false);
//            System.out.printf("max=%d remain=%d\n\n",maxSpeed,(speed-maxSpeed));
//            return maxSpeed;
//        }
//        return speed;
//    }
    
    private int bonus(int amount,boolean reset){
        Integer h = (Integer) hashv.get("Bonus");
        int a = h;
        if(reset){
            hashv.put("Bonus", 0);return a;
        }else{
            a = h + amount;
            hashv.put("Bonus", a);}
        return a;
    }
    
    private int getConnections(){
        return returnH("Total Byte").size();
    }
    
    public int totalByte(){
        Hashtable h = (Hashtable) hashv.get("Total Byte");
        if(h==null){
            h = new Hashtable();
            hashv.put("totalspeed", h);}
        int s = 0;
        
          Enumeration e = h.elements();
          while(e.hasMoreElements()){
              int c = 0;
              try{
                  c = Integer.parseInt(""+e.nextElement());
              }catch(NumberFormatException n){
              }
              s += c;
          }
//          System.out.println("total byte = "+s);
        return s;
   }
   public void wait(int time){
       try {
            sleep(time);
        } catch (InterruptedException ex) {
            Logger.getLogger(load.class.getName()).log(Level.SEVERE, null, ex);
        }
   } 
   
   private Hashtable returnH(String h){
        Hashtable hash = (Hashtable) hashv.get(h);
        return hash;
    }
   
   private void addTotalDownload(int value){
        try{
            tableTimer tT = (tableTimer) hashv.get("Thread");
            tT.updateSpeeed(hashv, value,id);
        }catch(NullPointerException e){
            System.out.println("Null Exception AddtoDownload="+e.getMessage());
        }    
   }
   
   private void addTotalDownload2(int value){
        try{
            int a = (int) hashv.get("Total Download");
            hashv.put("Total Download", (a+value));
            Hashtable hash = returnH("Total Byte");
            int b = (int) hash.get(id);
            hash.put(id, (b+value));
        }catch(NullPointerException e){
            System.out.println("Null Exception AddtoDownload="+e.getMessage());
        }    
   }
   
    private void updateSpd(long time,int b){
//        mb(b);
        double speed = getS(time, b);
        speed = retSpeed(speed);
        hashv.put("Transfer Rate", ""+speed+" kb/s");
//        update();
    }
    
    public void update(){
        
    }
    
        
    public void slip(Long time){
        try {
            sleep((1000- retTime(time)),retNano(time));
        } catch (InterruptedException ex) {
            Logger.getLogger(ProxyThread2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static  void main(String arg[]){
        
    }
    
    public int retNano(long l){
        String pattern = "000000000.000000";
        DecimalFormat myformat = new DecimalFormat(pattern);
        float b = (float)l/1000000;
        String a = myformat.format(b);
        int c = Integer.parseInt(a.substring(0, 9));
      return Integer.parseInt(a.substring(10));
    }
    public int retTime(long l){
    String pattern = "000000000.000000";
    DecimalFormat myformat = new DecimalFormat(pattern);
    float b = (float)l/1000000;
    String a = myformat.format(b);
    return Integer.parseInt(a.substring(0, 9));
    }
    
    public double retSpeed(double l){
    String pattern = "000000000.00";
    DecimalFormat myformat = new DecimalFormat(pattern);
    String a = myformat.format(l);
    double c;
    try{
        c = Double.parseDouble(a);
    }catch(NumberFormatException e){
        c = l;
    }
    return c;
    }
    
    
}
    

