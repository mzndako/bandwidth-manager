/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.io.*;
import java.util.*;


/**
 *
 * @author Administrator
 */
public class Records {
ObjectOutputStream out;
ObjectInputStream in;

public void saveSettings(Hashtable hash,String value){
            String dir = System.getProperty("user.dir");
            String path = dir;
            File file;
            file = new File(dir+"/"+value);
            try{
                out = new ObjectOutputStream(new FileOutputStream(file));
                out.writeObject(hash);
            }catch(IOException e){}
}

public Hashtable getSettings(String value){
        Hashtable hash = null;
    try {
            String dir = System.getProperty("user.dir");
            String path = dir;
            File file;
            file = new File(dir+"/"+value);
            if(file.exists()){
                in = new ObjectInputStream(new FileInputStream(file));
                hash = (Hashtable) in.readObject();
                return hash;
            }else{
                file.createNewFile();
                out = new ObjectOutputStream(new FileOutputStream(file));
                out.writeObject(new Hashtable());
                return new Hashtable();
            }
            
        } catch (IOException ex) {
            System.out.println("IO error = "+ex.getMessage());
        }catch(ClassNotFoundException ex){
            System.out.println("Class Not Found = "+ex.getMessage());
        }
        return new Hashtable();
}

 public static void main(String arg[]){
     Records r = new Records();
//     System.out.println(r.getSettings());
//     Hashtable h = r.getSettings();
//     r.saveSettings(h);
 }   
}
