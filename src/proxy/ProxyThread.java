/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import screen.load;
/**
 *
 * @author MZ
 */
public class ProxyThread extends Thread{
    Socket server,client;
    String remoteHost = load.servername;
    long no = 0;
    long number = new Random().nextInt(9999903);
    String id = number+"";
    int remotePort = load.serverport;
    load frame;
    double size = 1024;
    String mac = null;
    int formalbyte = 0;
    public Hashtable hashv;
//    private HTTP
    static int conn = 0;

    public ProxyThread(Socket client,load frame){
        this.client = client;
        this.frame = frame;
    }

    public void run() {
        try {
            if(frame.pause){
                error = 4;
                throw new UnknownError();
            }
            
            if(!isAvailable())
                throw new UnknownHostException("Cannot find mac address");
            
            login();
            
            if(maxReached())
                throw new UnknownError(client.getInetAddress().getHostName()+" = Maximum connection reached");
            
            server = new Socket(remoteHost,remotePort); 
            server.setSoTimeout(load.ConnTimeout*1000);
            client.setSoTimeout(load.ConnTimeout*1000);
            connect();
            
            ProxyThread1 thread1 = new ProxyThread1(client, server,frame,hashv);
            thread1.setName(id);
            thread1.start();
            ProxyThread2 thread2 = new ProxyThread2(server, client,frame,hashv);
            thread2.setName(id);
            thread2.start();
            
            while(!server.isClosed()){
                loadSpeed();
                try {
                    sleep(1000);
                } catch (InterruptedException ex) {}
            }
            
            disconnect();
        
        } catch (UnknownHostException ex) {
            
            frame.r().writeToClient(client, "Please Identify Your Computer");
        
        } catch (IOException ex) {
            disconnect();
            Logger.getLogger(ProxyThread.class.getName()).log(Level.SEVERE, null, ex);
        
        }catch(UnknownError ex){
            if(error==1)
                frame.r().login(client,hashv);
            else if(error==2 |error ==3 | error == 4){
                String t = "Too many Connections, Please reduce Open/loading pages then refresh."+refresh();
                if(error == 3){
                    System.out.println("maximum");
                    t = "Maximum Browsing Limit Reached = "
                            +frame.converToMB((int)hashv.get("Download Limit"))+"<br>Please ask the "
                            + "Server Admin to reset your limits.<br> Thank You"+refresh();
                    if(!isExempted())
                        t = "You have reached Your Maximum Allowable"
                            + " Download Limit "+frame.converToMB((int)hashv.get("Download Limit"))
                            + "<br>To buy more Time/MB, Please meet the Server Admin"
                            + "<br>Automatically logging you out in 10 seconds";
                }else if(error == 4){
                    t = "Connection has been PAUSE by the Server Admin."+refresh();
                }
              
                frame.r().writeToClient(client, t);
                if(error == 3 & !isExempted()){
                    try {
                        Thread.currentThread().sleep(10000);
                    } catch (InterruptedException ex1) {
                        Logger.getLogger(ProxyThread.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                    frame.logout(mac);
                    frame.print(hashv.get("Computer")+": MB exhausted, Automatically Logging out");
                }
                    
                    
            }
                
        }
           
    }
    private String refresh(){
        return "<div align=center><input type=\"button\" onclick=\"document.location.reload()\" name=\"logout\" value=\"Refresh Page\" id=\"login\" /></div>";
    }
    public boolean isAvailable(){
        InetAddress inet = client.getInetAddress();
        mac = frame.r().getMac(inet.getHostAddress());
        mac = mac == null?"mz-pc-te-st-u":mac;
        if(mac==null)return false;
                   
        return true;
    }
    
    private boolean login(){
        hashv = load.unique.get(mac);
        if(hashv==null){
            frame.setValue(mac,hashv,client);
            hashv = load.unique.get(mac);
            if(isExempted()){
                hashv.put("Connection Limit", load.maxConn);
                hashv.put("Download Limit",load.limit);        
                hashv.put("Max Speed",load.maxSpeed);
                return true;
            }
            frame.print("Computer ("+mac+") seen");
            return false;
        }
        reloadName();
        return true;
    }
    
    private int reloadName(){
        if(load.computerName==null)
            return 1;
        if(mac.equalsIgnoreCase(load.computerName)){
            hashv.put("Computer",getSocketName());
            load.computerName = null;
            frame.print(client.getInetAddress().getHostAddress()+
                    " has been reloaded to "+getSocketName());
        }
        return 1;
    }
    
    private boolean isExempted(){
        Vector vec = (Vector) load.settings.get("Exempt");
        if(vec==null)
            return false;
        return vec.contains(mac);
    }
    
                
    private int getConnections(){
        return returnH("Total Byte").size();
    }
    
    private Hashtable returnH(String h){
        Hashtable hash = (Hashtable) hashv.get(h);
        return hash;
    }
        
    int error = 0;
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
        return false;
    }
      
       
    public String convertDownloads(double v){
        String d = v+" byte";
        if(v>=(size*size*size)){
            d = convertSpeed((double) (v/(size*size*size)))+" GB";
        }
        else if(v>=(size*size))
            d = convertSpeed((double) (v/(size*size)))+" MB";
        else if(v>=size){
            d = convertSpeed(v/(size))+" KB";}
         return d;
    }
    
    private int getConnectionByte(){
        int h = 0;
        try{
            h = (int) returnH("Total Byte").get(id);
        }catch(NullPointerException e){}
        return h;
    }
    
    public synchronized double loadSpeed(){
        int newbyte = getConnectionByte();
        int now = newbyte - formalbyte;
        formalbyte = newbyte;
        double d = ((now/1000.00));
        if(d==0)return 0;
        returnH("Total Speed").put(id,convertSpeed(d)+" kb/s");
        return d; 
    }
    
    public double convertSpeed(double l){
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
    
    public void connect(){
        returnH("Total Byte").put(id, 0);
        returnH("Sockets").put(id, server);
    }
    
    public void update(){
        frame.update(mac, hashv);
    }
    
    public synchronized void disconnect(){
        try{
           returnH("Total Byte").remove(id);
           returnH("Sockets").remove(id);
           returnH("Total Speed").remove(id);
           returnH("Host").remove(id);
           
        }catch(NullPointerException n){}
        closec();
    }
    
    private void closec(){
        try{ client.close();}catch(IOException e){}
    }
    
    public String getSocketName(){
        String a = client.getInetAddress().getHostName();
        int b = a.indexOf(".");
        if(b>4)
            return a.substring(0, b);
        return a;
    }
}
