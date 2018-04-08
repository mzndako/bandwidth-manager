/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import screen.load;

/**
 *
 * @author MZ
 */
public class Time extends Thread{
    load frame;
    boolean stop = false;
    public Time(load frame){
        this.frame = frame;
    }
   
    public void run() {
      while(!stop){  
            try{
                sleep(1000);
            }catch(InterruptedException e){}
            frame.updateTree();
            
    }
    }    
    public void stopNow(){
        this.stop = true;
    }
  }

    

