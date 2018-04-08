/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy;

import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import screen.load;

/**
 *
 * @author MZ
 */
public class tableTimer extends Thread{
    load frame;
    Hashtable hashv;
    
    int formalbyte = 0;
    boolean t = true;
    double size = 1024;
    String id;
    int idle = 0;
    public tableTimer(Hashtable hash,load frame){
        this.hashv = hash;
        this.frame = frame;
    }
   
    public void run() {
        id = Thread.currentThread().getName();
           try{
               while(hashv!=null){
                    if(spd()==0){
                        t = false;
                    }else t = true;
                    frame.update(hashv.get("Mac Address")+"", hashv);
                    try{
                        sleep(1000);
                    }catch(InterruptedException e){}
                    if(idle>=load.autoLogout  & haveTime()){
                            frame.print("Automatically logging out "+hashv.get("Computer")+" after "+load.autoLogout+" Seconds of no usuage");
                            frame.logoutClear(hashv.get("Mac Address")+"");
                     break;}
                }
           }catch(NullPointerException e){}

    }    
    public synchronized int spd(){
        int newbyte = (int) hashv.get("Total Download");
        double now = (double) newbyte - (double)formalbyte;
        if(now==0){
            idle++;           
        }else
            idle = 0;
        formalbyte = newbyte;
        double d = ((now/1000.00));
        int c = getConnections();
        hashv.put("Connections",(c==0?"Idle":c+""));
//        if(d==0 & t)return 0;
        if(d==0)
        hashv.put("Speed", "---");
        else
        hashv.put("Speed",retSpeed(d)+" kb/s");
          return (int) d; 
    }
    int intByte = 0;
    int finalByte = 0;
    long timestamp = System.nanoTime();
    long ttime = 0l;
    public synchronized void updateSpeeed(Hashtable hash,int value,String id){
        int a = (int) hash.get("Total Download");
        hash.put("Total Download", (a+value));
        intByte += value;
        Long e = System.nanoTime();
        Long t = timediff(timestamp, e);
        ttime += t;
        
        addByte(hash,value,id);
        int maxSpeed = (Integer) hash.get("Max Speed");
        
        if(intByte>=maxSpeed){
            if(load.recordb)
                frame.updateDownload(intByte);
            intByte = 0;
            if(retTime(ttime)<=1000){
                slip(ttime);
            }
            ttime = 0;
           
        }
         timestamp = System.nanoTime();
    }
    int inByteU = 0;
    public synchronized void updateSpeeedUpload(Hashtable hash,int value){
        inByteU += value;
        int a = (int) hash.get("Total Upload");
        hash.put("Total Upload", (a+value));
        if(inByteU >= 20000){
            frame.updateUpload(inByteU);
            inByteU=0;
        }
    }

    private Long timediff(long f,Long e){
        long a = e - f;
        return a;
    }
    private void addByte(Hashtable hash,int value,String id){
        Hashtable hash1 = (Hashtable) hash.get("Total Byte");
        int b = (int) hash1.get(id);
        hash1.put(id, (b+value));
    }
    
    public void slip(Long time){
        try {
            sleep((1000- retTime(time)),retNano(time));
        } catch (InterruptedException ex) {
            Logger.getLogger(ProxyThread2.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    private boolean haveTime(){
        try{
            int time = (int) hashv.get("Time Left");
            if(time==-1)
                return false;
            else if(time>=0)
                return true;
           return false;       
        }catch(NullPointerException e){}
        return false;
    }
    
    private int getConnections(){
        return returnH("Total Byte").size();
    }
    
        
    private Hashtable returnH(String h){
        Hashtable hash = (Hashtable) hashv.get(h);
        return hash;
    }
    
    public double retSpeed(double l){
    String pattern = "000000000.000";
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
    
    public String mbcon(int v){
        String d = v+" Byte";
        if(v>=(size*size*size)){
            d = retSpeed((double) ((double)v/(size*size*size)))+" GB";
            
        }
        else if(v>=(size*size))
            d = retSpeed((double) ((double)v/(size*size)))+" MB";
        else if(v>=size){
            d = retSpeed((double)v/(size))+" KB";}
         return d;
    }
    
    private boolean isExempted(String mac){
        Vector vec = (Vector) load.settings.get("Exempt");
        if(vec==null)
            return false;
        return vec.contains(mac);
    }
  }

    

