/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;
import java.awt.TextArea;
import java.awt.TextField;
import java.sql.*;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import screen.load;
import screen.loading;

/**
 *
 * @author Administrator
 */
public class database {
//public    TableOfEmployees employee = new TableOfEmployees("sup");
    String create = "Create TABLE IF NOT EXISTS TICKETS (ID INT(11) NOT NULL AUTO_INCREMENT, TICKETS VARCHAR(20),TOTAL_TIME INT,TIME_LEFT INT,LAST_LOGIN VARCHAR(50),"
            + "MAX_MB INT, MB INT, ACT_DATE DATETIME, EXPIRE_DATE DATETIME, AMOUNT INT,PRIMARY KEY (ID))";
    String create2 = "Create TABLE IF NOT EXISTS INCOME (ID INT(11) NOT NULL AUTO_INCREMENT,DATE DATETIME,"
            + "TIME VARCHAR(15), AMOUNT INT, USERS VARCHAR(30), SOURCE VARCHAR(50), PRIMARY KEY (ID))";
    String create3 = "Create TABLE IF NOT EXISTS EXPENSES (ID INT(11) NOT NULL AUTO_INCREMENT,DATE DATETIME,"
            + "TIME VARCHAR(15), AMOUNT INT, USERS VARCHAR(30), SOURCE VARCHAR(50), PRIMARY KEY (ID))";
    String create4 = "CREATE TABLE IF NOT EXISTS LIST (  ID int(11) NOT NULL AUTO_INCREMENT,NAME VARCHAR(50),TOTAL int(11) DEFAULT '0', BOUGHT int(11) DEFAULT '0',BOUGHT_DATE datetime,BAL int(11) DEFAULT '0',AMOUNT int(11) NOT NULL, EDITABLE tinyint(1) ,DATE datetime, PRIMARY KEY (ID))";
    
    Connection con;
    Statement stment;
    static String name, surname, sex, age;
    static String tickets;
    static int value,time; 
    String [] search;
    String keyword;
    String userName = load.databaseuser, password = load.databasepassword;
    load frame;
    TextArea txt;
    StringBuffer str = new StringBuffer();
    public database(String dbName,TextArea txt){
        this.txt = txt;
        new database(dbName);
    }
    
    public database(String dbName,load frame){
        this.frame = frame;
        new database(dbName);
    }
    
    public void setFrame(load frame){
        this.frame = frame;
    }
    private void writef(String x, Object... y){
        write(String.format(x, y));
    }
    private void write(String x){
        if(this.frame!=null){
            this.frame.print(x);
        }else str.append(x+". ");
    }
    public String getErr(){
        String x = str.toString();
        str = new StringBuffer();
        return x;
    }
   private void setP(){
      try{
          loading.setP();
      }catch(Exception e){
          System.out.println(e.getMessage());
      }
   }
   
   public database(String dbName){
//       String dbAddress = "jdbc:derby:"+dbName+";create=true";
       System.out.println("hello");
       String dbAddress = "jdbc:mysql://localhost:3306/?zeroDateTimeBehavior=convertToNull";
       String createDatabase = "CREATE DATABASE "+dbName+" DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci";
       try{
           setP();
            Properties connectionProps = new Properties();
            connectionProps.put("user", this.userName);
            connectionProps.put("password", this.password);
           con = DriverManager.getConnection(dbAddress,connectionProps);
           write("Connecting to Database '"+dbName+"'......");
           stment = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
           boolean x = true;
           do{
           try{
               stment.execute(createDatabase);
           }catch(SQLException ex){
               stment.close();
               dbAddress = "jdbc:mysql://localhost:3306/"+dbName+"?zeroDateTimeBehavior=convertToNull";
               con = DriverManager.getConnection(dbAddress,connectionProps);
               stment = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
               x = false;
               write("Successfully connected to database");
           }
           }while(x);
           try{
                stment.execute(create4);
                stment.execute(create3);
                stment.execute(create2);
                stment.execute(create);
           }catch(SQLException sql){
                sql.printStackTrace();
           } 
           setP();
   }catch (SQLException sql){
       System.out.println("error "+sql.getMessage());
       write("Error occurred when connecting to database: "+sql.getMessage());
       setP();
   } 
   }
   
   public boolean addTicket(String ticket,int time,int mb,String act,String expire,int amount){
       try{
            
            ticket = ticket.toUpperCase();
            String SQL = String.format("INSERT INTO TICKETS (TICKETS,TOTAL_TIME,TIME_LEFT,LAST_LOGIN,MAX_MB,MB,"
                    + "ACT_DATE,EXPIRE_DATE,AMOUNT) "
                    + "VALUES ('%s',%d,%d,'',%d,%d,'%s','%s',%d)",ticket,time,time,mb,0,act,expire,amount);
            System.out.println(SQL);
            stment.executeUpdate(SQL);
           return true;
       }catch (SQLException sql){System.out.println("error inserting="+sql.getMessage());    }    
    return false;    
   }
   
   public boolean addData(int date,String time,int amount,String user,String source){
       try{
            
        
            String SQL = String.format("INSERT INTO DATA (DATE,TIME,AMOUNT,USERS,SOURCE) "
                    + "VALUES (%d,'%s',%d,'%s','%s')",date,time,amount,user,source);
//            System.out.println(SQL);
            stment.executeUpdate(SQL);
           return true;
       }catch (SQLException sql){System.out.println("error inserting="+sql.getMessage());    }    
    return false;    
   }
      public boolean add2Record(String what,String date,String time,int amount,String user,String source){
       try{
            String SQL = String.format("INSERT INTO %s (DATE,TIME,AMOUNT,USERS,SOURCE) "
                    + "VALUES ('%s','%s',%d,'%s','%s')",what,date,time,amount,user,source);
            System.out.println(SQL);
            stment.executeUpdate(SQL);
           return true;
       }catch (SQLException sql){System.out.println("error inserting="+sql.getMessage());    }    
    return false;    
   }
    
   public Vector getData(String ticket){
        String date,act,expire;
        int t,tl,m,tm,amount;
        ticket = ticket.toUpperCase();
        Vector vector = new Vector();
       try{
            ResultSet result;
            String SQL = String.format("SELECT * FROM TICKETS WHERE TICKETS = '%s'",ticket);
            result = stment.executeQuery(SQL);
               
            while (result.next()){
                t = result.getInt("TOTAL_TIME");
                tl = result.getInt("TIME_LEFT");
                m = result.getInt("MAX_MB");
                tm = result.getInt("MB");
                date = result.getString("LAST_LOGIN");
                act = c(result.getString("ACT_DATE"));
                expire = c(result.getString("EXPIRE_DATE"));
                amount = result.getInt("AMOUNT");
                vector.add(t);vector.add(tl);vector.add(m);vector.add(tm);vector.add(date);
                vector.add(act); vector.add(expire);vector.add(amount);
                return vector;
       }
            return null;
   }catch(SQLException sql){
       System.out.println("Failure Searching = "+sql.getMessage()); 
       sql.printStackTrace();
   }
   return null;       
   }
   
   private String c(String x){
      try{
          if(x.length()==21)
                    x = x.substring(0,19);
      }catch(NullPointerException e){
          return "0000/00/00 00:00:00";
      }
       return x;
   }
   
   public Hashtable getByDate(String what, String date1,String date2){
        String time,user,source;
        int amount;
        String date;
        java.sql.Date d;
        Hashtable hash = new Hashtable();
        Vector vector = new Vector();
        int a = 0;
       try{
            ResultSet result;
            String SQL = String.format("SELECT * FROM %s WHERE DATE >= '%s' AND DATE <= '%s' ORDER BY SOURCE",what,date1,date2);
            result = stment.executeQuery(SQL);
            while (result.next()){
                vector = new Vector();
                date = c(result.getString("DATE"));
//                time = result.getString("TIME");
                amount = result.getInt("AMOUNT");
                user = result.getString("USERS");
                source = result.getString("SOURCE");
                vector.add(date);vector.add(user);vector.add(source);
                vector.add(amount);
                a++;
                hash.put(a, vector);
            }

            return hash;
   }catch(SQLException sql){
       System.out.println("Failure Searching = "+sql.getMessage());    
   }catch(NullPointerException e){}
   return null;       
   }

   public void updateLogin(String ticket,String lastLogin){
       ticket = ticket.toUpperCase();
       try{
           String SQL = String.format("UPDATE TICKETS SET LAST_LOGIN='%s' WHERE TICKETS='%s'"
               ,lastLogin,ticket);
           stment.executeUpdate(SQL);
       }catch(SQLException s){}
   }
   
   public boolean updateActivate(String ticket,String act,String expire){
       ticket = ticket.toUpperCase();
       try{
           String SQL = String.format("UPDATE TICKETS SET ACT_DATE='%s' , EXPIRE_DATE='%s' WHERE TICKETS='%s'"
               ,act,expire,ticket);
           stment.executeUpdate(SQL);
           return true;
       }catch(SQLException s){}
       return false;
   }
   
   public Vector listData(){
       Vector vector = new Vector();
       try{
           String date,ticket,act,expire;
           int t,tl,m,ml;
         ResultSet result;
            String SQL = String.format("SELECT * FROM TICKETS");
            result = stment.executeQuery(SQL);
            while (result.next()){
                ticket = result.getString("TICKETS");
                t = result.getInt("TOTAL_TIME");
                tl = result.getInt("TIME_LEFT");
                m = result.getInt("MAX_MB");
                ml = result.getInt("MB");
                date = result.getString("LAST_LOGIN");
                act = c(result.getString("ACT_DATE"));
                expire = c(result.getString("EXPIRE_DATE"));
          vector.add(ticket);vector.add(t);vector.add(tl);vector.add(m);vector.add(ml);
          vector.add(date); vector.add(act);vector.add(expire);
       }
            System.out.println("TICKETS = "+vector);
       return vector;
   }catch(SQLException sql){
       System.out.println("Failure Listing = "+sql.getMessage());    
       sql.printStackTrace();
   }
   
   return vector;
   }
   
   public boolean updateTicket(String ticket,int timeleft,int mb){
       ticket = ticket.toUpperCase();
   try{
//       System.out.println("Start saving...");
       String SQL = String.format("UPDATE TICKETS SET TIME_LEFT=%d , MB=%d WHERE TICKETS='%s'"
               ,timeleft,mb,ticket);
//       System.out.println("saved="+SQL);
       stment.executeUpdate(SQL);
       return true;
   }catch(SQLException sql){
       System.out.println("Failure inserting = "+sql.getMessage());    
   }
   return false;
   }
   
   public boolean recharge(String ticket,int ttime,int tleft,int maxmb,int amount,String expires){
       ticket = ticket.toUpperCase();
   try{
//       System.out.println("Start saving...");
       String SQL = String.format("UPDATE TICKETS SET TOTAL_TIME=%d, TIME_LEFT=%d ,"
               + "MAX_MB=%d,AMOUNT=%d, EXPIRE_DATE='%s' WHERE TICKETS='%s'"
               ,ttime,tleft,maxmb,amount,expires,ticket);
       stment.executeUpdate(SQL);
       return true;
   }catch(SQLException sql){
       System.out.println("Failure inserting = "+sql.getMessage());    
   }
   return false;
   }
   
   public boolean add2List(String name,int quantity,int amount,boolean editable){
       try{
            String SQL = String.format("INSERT INTO LIST (NAME,BAL,AMOUNT,EDITABLE) "
                    + "VALUES ('%s',%d,%d,%b)",name,quantity,amount,editable);
            stment.executeUpdate(SQL);
           return true;
       }catch (SQLException sql){System.out.println("error inserting="+sql.getMessage());    }    
    return false;    
   }   
   public Hashtable getList(){
       Hashtable hash = new Hashtable();
       Vector vector;
       try{
           int a = 0;
            String name;boolean editable;
            int bal,amount;
            ResultSet result;
            String SQL = String.format("SELECT * FROM LIST");
            result = stment.executeQuery(SQL);
            while (result.next()){
                vector = new Vector();
                name = result.getString("NAME");
                amount = result.getInt("AMOUNT");
                bal = result.getInt("BAL");
                editable = result.getBoolean("EDITABLE");
                vector.add(amount);vector.add(bal);vector.add(editable);
                hash.put(name, vector);
            }
        return hash;
   }catch(SQLException e){
       
   }
       return null;
   }
   public boolean updateList(String name,int bal,int amount){
       try{
           ResultSet result;
           System.out.format("%s, %d, %d",name,bal,amount);
           String SQL = String.format("SELECT * FROM LIST WHERE NAME = '%s'",name);
            result = stment.executeQuery(SQL);
            int balp = -1;
            if(result.next()){
                balp = result.getInt("BAL");}
            if(balp > -1)
                bal += balp;
            String add = "";
            if(amount > 1)
                add = ",AMOUNT='"+amount+"'";
           SQL = String.format("UPDATE LIST SET BAL=%d %s WHERE NAME='%s'"
               ,bal,add,name);
           System.out.println(SQL);
           stment.executeUpdate(SQL);
           return true;
       }catch(SQLException s){
           System.out.println(s.getSQLState());
       }
       return false;
   }
   public boolean deleteList(String name){
   try{
       stment.executeUpdate(String.format("DELETE FROM LIST WHERE NAME='%s'",name));
       return true;
   }catch(SQLException sql){
       System.out.println("Failure inserting = "+sql.getMessage());    
   }
        return false;
   }
   
   public void deleteTicket(String ticket){
       ticket = ticket.toUpperCase();
   try{
       
       stment.executeUpdate(String.format("DELETE FROM TICKETS WHERE TICKET='%s'",ticket));
   }catch(SQLException sql){
       System.out.println("Failure inserting = "+sql.getMessage());    
   }
   }
   
  public static void main(String []arg){
      database db = new database("bandwidth");
      //database db2 = new database();
      System.out.println(db.listData());
//      db.addTicket("mzndako", 300, 200000, "20121103", "20121107", 230);
      
//      db.updateTicket("mzndako", 119, 10);
//      db.addData(20122312,"12:55:55 GMT", 40, "ADMIN", "LAPTOP");
//   System.out.println(   db.getByDate("expenses","2010-03-20 00:00:00","2014-03-25 23:59:59"));
//   db.add2List("Printing", 3, 40, true);
//      db.deleteTicket("mzndako");
//      System.out.println(db.getData("rspm"));
//      db.searchData("ndak");
  }
}
