/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy;



import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import screen.load;

/**
 *
 * @author MZ
 */
public class related {
    load frame;
    StringBuffer buf = new StringBuffer();
    boolean t = false;
    Socket server;

    public related(load frame){
        this.frame = frame;
    }
    
    public String read(byte [] buffer){
        buf.append(new String(buffer)); 
        int h = buf.toString().toLowerCase().indexOf("host:");
        if(h == -1) return null;
        int l = buf.indexOf("\n",h);
        if(h == -1) return null;
        String a = buf.substring(h, l);
        buf = new StringBuffer();
        return a;//http://info/?more=x1234
    }
    private String getRefresh(){
        String a = "<script type=\"text/javascript\">\n"
                + "<!--\n"
                + "window.onload = setupRefresh;var time = 6;var check = true; function setupRefresh() {setInterval(\"dorefresh();\", 1000);};"
                + "function dorefresh(){var output = document.getElementById(\"head\");if(time==0){output.innerHTML = \"Refreshing.....\";refreshPage();time = 6; }else if(check){time--;output.innerHTML = \"Auto refreshing in \"+time+\" seconds\";}}\n"
                + "function refreshPage() {var output  = document.getElementById(\"info\"); var xhr = (\"XMLHttpRequest\" in window) ? new XMLHttpRequest() : new ActiveXObject(\"Msxml3.XMLHTTP\"); xhr.open(\"GET\", 'http://info/?more=x1234', true);  xhr.onreadystatechange = function() {      if (xhr.readyState === 4)  {         var serverResponse = xhr.responseText;\nif(serverResponse.indexOf(\"<h1>WHITE HOUSE CAFE</h1>\", 0) > -1){ document.title = \"User Logout\"\ncheck=false;\nalert(\"You have logout\"); var output = document.getElementById(\"head\"); output.innerHTML = \"USER LOGOUT\";};\n var array = serverResponse.toString().split(\"&\", 20); \n"
                + "var timeleft = 0; var mbleft = 0; for(var i = 0;i < array.length; i++){var a = array[i];var array2 = a.toString().split(\"=\", 3); try{var info = array2[0];var value = array2[1];var write = document.getElementById(info);write.innerHTML = value;if(info==\"tleft\"){timeleft = value;"
                + "}if(info == \"mbleft\"){ mbleft = value;} }catch(err){ }} document.title = timeleft+\" - \"+mbleft+\"(REM)\";}};xhr.send(null);};\n"
                + "\n-->\n</script>\n\n";
        return a;
    }
    public String print(byte[] buffer,StringBuffer buff){
        if(t)
            return null;
        buff.append(new String(buffer));
        int end = buff.indexOf("\n\n");
        if(end!=-1){
            System.out.println(buff.toString()+"\n.....END.....\n");
        buff.setLength(end); t=true;}
        
        return null;
    }
    
    
    public int login(Socket client,Hashtable hashv){
        String refresh = "<META HTTP-EQUIV=\"refresh\""
                + "CONTENT=\"10;URL=http://www.netmechanic.com\">";
        String body = "<HTML>"
                + "<HEAD><!-- Send users to the new location. -->"
                + "<TITLE>redirect</TITLE>"
                + "<META HTTP-EQUIV=\"refresh\""
                + "CONTENT=\"10;URL=http://www.netmechanic.com\">"
                + "</HEAD>"
                + "<BODY>"
                + "Your Mac Address is Invalid. Trying login in from our local Server.<br>Thank You"
                + "to its new location in 10 seconds. "
                + "</BODY></HTML>";
        
        String head3 = "HTTP/1.1 300 OK\n" +
                    "GET localhost\n"
                + "Host: localhost\n" +
                    "Server: Apache/2.2.21 (Win32) mod_ssl/2.2.21 O\n" +
                    "read\n" +
                    "penSSL/0.9.8t PHP/5.3.10\n" +
                     "X-Powered-By: PHP/5.3.10\n" +
                    "Content-Length: "+body.length()+"\n" +
                    "Content-Type: text/html\n\n";
        try {
            BufferedInputStream in = new BufferedInputStream(client.getInputStream());
            StringBuffer buf = new StringBuffer();
            int a;
            t = false;
            while((a = in.read())>-1){
                char c = (char) a;
                buf.append(c);
                if (a == 13)
			{
				in.mark(1);
				if ((a=in.read()) == 10){
                                    if(t)break;t=true;continue;}
                                else {
                                    in.reset();;
                                }
			}
                t=false;
            }
            
            String dir = System.getProperty("user.dir");
            File file = new File(dir+"/index.html");
            BufferedReader read = new BufferedReader(new FileReader(file));
            String b = "";

            StringBuffer loginStr = new StringBuffer();
            
            while((b = read.readLine())!=null){
                loginStr.append(b+"\n");
            }
            String err = "";
            String ticket = getTicket(buf.toString());
            if(ticket!=null){
                Vector vec = frame.database().getData(ticket);
                if(vec!=null){
                    String act = vec.elementAt(5)+"";
//                    int act = getNum(at);
                    String expire = vec.elementAt(6)+"";
//                    int expire = getNum(ex);
//                    int today = getNum(frame.getToday());
                    int time = (int) vec.elementAt(1);
                    int mb = (int) vec.elementAt(2) - (int) vec.elementAt(3);
                    String m = frame.converToMB(mb);
                    long dl = (int) vec.elementAt(2);
                    long extra = (dl * (long)load.percentage) / 100;
                    int amount = getNum(vec.elementAt(7)+"");
                    if(0 > mb){
                        m = "0 MB";
                        extra = extra + mb;
                    }
                    
                    boolean c = true,login = true;
                    if(time<=0)
                        c = false;
                    if(act.equals("0000/00/00 00:00:00"))
                        login = false;
                    
                    if(time==0)
                        err = "Time Exhausted; Time Left = 00:00:00";
                    else if(getCalendar(expire).before(getCalendar(frame.getToday())) & login)
                        err = "TimeCode has expired = "+stringToDate(expire);
                    else if(mb<=0 & false){
                        err = "Download/Browsing Limit reached; Megabyte left = 0MB";
                    }else if(checkAvailableTicket(ticket)){
                        err = "This ticket is already login in another system. "
                                + "<br>Automatically logging out the ticket. Try Loging in again";
                    }else{
                    String actN,expireN;     
                        if(act.equals("0000/00/00 00:00:00")){
                            actN = frame.getToday();
                            int x = getNum(expire.substring(0,4));
                            expireN = frame.date(x);
                            expire = expireN;
                            if(frame.database().add2Record("INCOME",actN, getDate(4)
                                    , amount, "AUTOMATIC", "LAPTOP TICKET")){
                                frame.database().updateActivate(ticket, actN, expireN);
                                frame.reloadTotal();
                                frame.print("Automatic Activation of "+ticket+"");
                            }else
                                return 4;
                        }    
                    hashv.put("Ticket",ticket);
                    hashv.put("Total Time", vec.elementAt(0));
                    hashv.put("Time Left", vec.elementAt(1));
                    hashv.put("Download Limit",((int) vec.elementAt(2)));
                    hashv.put("Total Download", vec.elementAt(3));
                    hashv.put("Activated",act);
                    hashv.put("Expires",expire);
                    hashv.put("Login",getDate(3));
                    hashv.put("Connection Limit", load.LConnLimit);
                    hashv.put("Max Speed", load.LMaxSpeed);
                    hashv.put("Amount",amount);
                    
                    frame.database().updateLogin(ticket, getDate(3));
                    writeToClient(client,loadCSS()+ "Welcome:"
                            + "<br>Total Time = "+getTime((int)vec.elementAt(0))
                            + "<br>Time Left = "+getTime((int)vec.elementAt(1))
                            + "<br>"
                            + "<br>BandWidth = "+frame.converToMB((int) vec.elementAt(2))
                            + "<br>MB Remaining = "+m
                            + (c?"<br>Extra MB (Remaining) = "+frame.converToMB(extra):"")
                            + "<br><br> Expires On = "+stringToDate(expire+"")
                            + "<br>Amount = N"+amount
                            + "<br><br> <div style='font-weight:bold;font-size:14px;'><a style='color: red;' href='http://info/'>Ticket "
                            + "Details</a></div>"
                            + "<br><div style='color: blue; font-size: 15px;'  ><a href='javascript:()' onclick='document.location.reload()'>CLICK HERE TO CONTINUE BROWSING....</a></div>"
                            ,"WELCOME");
                    frame.print(hashv.get("Computer")+": login with Ticket: "+ticket);
                    client.close();
                    return 3;}
                }else
                    err = "Invalid TimeCode";
            }
            String log = loginStr.toString().replaceAll("&nbsp;", err);
            String head = "HTTP/1.1 200 OK\r\n"+
                    "penSSL/0.9.8t PHP/5.3.10\r\n" +
                     "X-Powered-By: PHP/5.3.10\r\n" +
                    "Content-Length: "+log.length()+"\r\n" +
                    "Content-Type: text/html\r\n\r\n";
            String write = head+log;
            client.getOutputStream().write(write.getBytes());
            client.close();
        } catch (IOException ex) {
//            System.out.println("exception in related"+ex.getMessage());
        }
       
        return 0;
    }

    public boolean checkAvailableTicket(String tick){
            Enumeration en = load.unique.elements();
        while(en.hasMoreElements()){
            Hashtable hashv = (Hashtable) en.nextElement();
            String ticket;
            try{
                ticket = (String) hashv.get("Ticket");
            }catch(NullPointerException e){
                ticket = null;
            }
                if(ticket==null)
                    continue;
                if(ticket.equalsIgnoreCase(tick)){
                    frame.logout((String)hashv.get("Mac Address"));
                    return true;
                }
            }
        return false;
    }
    
    public void addAccess(String ip,String mac){
        try {
            StringBuffer buf = new StringBuffer();
            String arp = "arp -s "+ip+" "+mac;
            System.out.println("added"+arp);
            Process p = Runtime.getRuntime().exec(arp);
            int b = p.waitFor();
         }catch(InterruptedException ex){
            System.out.println("MAC time interupted"+ex.getMessage());
        }catch (IOException ex) {
            Logger.getLogger(related.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getMac(String ip){
        try {
            StringBuffer buf = new StringBuffer();
            String arp = "arp -a "+ip;
            frame.macConnect();
            Process p = Runtime.getRuntime().exec(arp);
            int b = p.waitFor();
            frame.macDisconnect();
            int a = 0;
            if(b!=0) return null;
            while((a = p.getInputStream().read())>-1){
                buf.append((char)a);
            }
            if(buf.indexOf("Physical Address")==-1){
                return null;
            }
            String s = null;
            StringTokenizer token = new StringTokenizer(buf.toString(),"\n");
           while(token.hasMoreTokens()){
               s = token.nextToken(" ");
               if(s.trim().length()==17)break;
           }
           return s; 
        }catch(InterruptedException ex){
            System.out.println("MAC time interupted"+ex.getMessage());
        }catch (IOException ex) {
            Logger.getLogger(related.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public Calendar returnCalendar(String x){
        Calendar c =Calendar.getInstance();
        if(x.length()!=12)
            return c;
//          c.set(field, value)
//        c.set(x.substring(beginIndex, endIndex), month, date, hourOfDay, minute, second);
        return c;
    }
    
    public String date(int d){
        Calendar c =Calendar.getInstance();
        c.add(Calendar.DATE, d);
        String yr = p(c.get(Calendar.YEAR));
        String mon = p((c.get(Calendar.MONTH)+1));
        String day = p(c.get(Calendar.DATE));
        String hr = p(c.get(Calendar.HOUR_OF_DAY));
        String min = p(c.get(Calendar.MINUTE));
        String sec = p(c.get(Calendar.SECOND));
        String all = String.format("%s-%s-%s %s:%s:%s",yr,mon,day,hr,min,sec);
        return all;
    }
    
    public Calendar getCalendar(String x){
        Calendar c = Calendar.getInstance();
        if(x.length() != 19)
            return c;
        c.set(getNum(x.substring(0, 4)),getNum(x.substring(5, 7))-1,getNum(x.substring(8, 10)),getNum(x.substring(11, 13)),getNum(x.substring(14, 16)),getNum(x.substring(17, 19)));
        return c;
    } 
    public static void main(String args[]) throws ParseException{
//    System.out.println(new related(null).convert2date("01/12/2014",false));
//    public void getMac
        related r = new related(null);
//        Calendar x = (Calendar) r.date(0);
//    System.out.println((r.date(3).after(r.date(1))));
//    System.out.println(r.date(3));
        String x = "2013/12/30 21:13:19";
//        System.out.println(x.substring(17, 19));
    System.out.println(r.getCalendar(x).getTime());
    
    }
    
    
    public void writeToClient(Socket client,String text){
        writeToClient(client, text,"Error From White House Cafe");
    }
    
    private String loadCSS(){
        return "<style type=\"text/css\">"
                + "<!--\n"
                + getLogout()+"\n"
                + "-->\n"
                + "</style>\n\n";
    }
    
    public String getLogout(){
        return "#login {display: inline-block;"
                + "color: #fff;height:"
                + " 26px;font-size:"
                + " 11px;font-weight: 700;"
                + "line-height: 26px;cursor: pointer;border: "
                + "1px solid #333;"
                + "background: #0ec4f7;	"
                + "margin-right: 4px;"
                + "}";
                
    }
    
    private String returnHash(Hashtable hash,String name){
        try{
            return hash.get(name)+"";
        }catch(NullPointerException e){}
        return "";
    }
    
    public String writeInfoToClient(Hashtable hashv,Socket client,Socket server,
            boolean include){
        try {    
                String ticket = (String) hashv.get("Ticket");
                boolean d = true,c = true;
                if(ticket==null){
                    d = false;
                 }
                int time = getNum(hashv.get("Time Left")+"");
                if(time<=0)
                    c = false;
                
                    String act = returnHash(hashv, "Activated");
                    String expire = returnHash(hashv, "Expires");
                    String login = returnHash(hashv, "Login");
                    int tleft = getNum(returnHash(hashv, "Time Left"));
                    int ttime = getNum(returnHash(hashv, "Total Time"));
                    int td = getNum(returnHash(hashv, "Total Download"));
                    int dl = getNum(returnHash(hashv, "Download Limit"));
                    int amount = getNum(returnHash(hashv, "Amount"));
                int mb = dl - td;
                long extra = ((long)dl * (long)load.percentage) / 100;
                String m = frame.converToMB(mb);
                if(0 > mb){
                    m = "0 MB";
                    extra = extra + mb;
                }
                String value = "Login Details:-"
                        + (d?"<br/> Ticket = "+ticket:"")
                        +"<br/>"
                        + (d?"<br/>Total Time = <span id=ttime>"+getTime(ttime)+"</span>":"")
                        + (d?"<br>Time Left = <span id=tleft>"+getTime(tleft)+"</span>":"")
                        + (d?"<br>":"")
                        + "<br>BandWidth = <span id=tmb>"+frame.converToMB(dl)+"</span>"
                        + "<br>MB Remaining = <span id=mbleft>"+m+"</span>"
                        + (c?"<br>Extra MB (Remaining) = <span id=extra>"+frame.converToMB(extra)+"</span>":"")
                        + "<br><br>Speed = <span id=speed>"+returnHash(hashv, "Speed")+" out of "
                        + (getNum(returnHash(hashv, "Max Speed"))/load.size)+" kb/s</span>"
                        + "<br>Connections = <span id=conn>"+returnHash(hashv, "Connections")+" out of "
                        + returnHash(hashv, "Connection Limit")
                        + "</span><br>"
                        + (d?"<br> Last Login = "+login:"")
                        + (d?"<br> Expires On = "+stringToDate(expire):"")
                        + (d?"<br><br> Amount = N"+amount:"")
                        + (d?"<br> <div>"
                        + "<input type=\"button\" onclick=\"document.location.href='http://logout/'\" name=\"logout\" value=\"logout\" id=\"login\" />"
                        + "<div>":"");
                if(include)
                    writeToClientInfo(client,value,"WELCOME "+ticket!=null?ticket:"");
                else{
                    value = "ttime="+getTime(ttime)+"&tleft="+getTime(tleft)+"&tmb="+frame.converToMB(dl)
                            + "&mbleft="+m
                            + "&speed="+returnHash(hashv, "Speed")+" out of "
                            + (getNum(returnHash(hashv, "Max Speed"))/load.size)+" kb/s"
                            + "&conn="+returnHash(hashv, "Connections")+" out of "
                            + returnHash(hashv, "Connection Limit");
                
                    info(client, value);
                }
                server.close();
                client.close();
                return "success";
            } catch (IOException ex) {
                Logger.getLogger(related.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
    }
    
    public void writeToClient(Socket client,String text,String title){
        String body = "<html><head><title>"
                    + title
                    + "</title></head>"
                    + "<body><p> <h2>"+text+"</h2></p>"
                    + "</body></html>";
        String head = "HTTP/1.1 200 OK\n" +
                    "Date: Wed, 24 Oct 2012 18:14:39 GMT\n" +
                    "Server: Apache/2.2.21 (Win32) mod_ssl/2.2.21 O\n" +
                    "read\n" +
                    "penSSL/0.9.8t PHP/5.3.10\n" +
                     "X-Powered-By: PHP/5.3.10\n" +
                    "Content-Length: "+body.length()+"\n" +
                    "Content-Type: text/html\n\n";
        
        try{
            client.getOutputStream().write((head+body).getBytes());
            client.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void info(Socket client,String text){
        String body = text;
                    
        String head = "HTTP/1.1 200 OK\n" +
                    "Date: Wed, 24 Oct 2012 18:14:39 GMT\n" +
                    "Server: Apache/2.2.21 (Win32) mod_ssl/2.2.21 O\n" +
                    "read\n" +
                    "penSSL/0.9.8t PHP/5.3.10\n" +
                     "X-Powered-By: PHP/5.3.10\n" +
                    "Content-Length: "+body.length()+"\n" +
                    "Content-Type: text/html\n\n";
        
        try{
            client.getOutputStream().write((head+body).getBytes());
            client.close();
        }catch(IOException e){}
    }
    public void writeToClientInfo(Socket client,String text,String title){
        String body = "<html><head><title>"
                    + title
                    + "</title>"+getRefresh()+loadCSS()+"</head>"
                    + "<body>"
                    + "<div id=head>Auto refreshing in 6 seconds</div>"
                    + "<div> <h4>"+text+"</h4></div>"
                    + "Please contact server admin for more information/complaint."
                    + "</body></html>";
        String head = "HTTP/1.1 200 OK\n" +
                    "Date: Wed, 24 Oct 2012 18:14:39 GMT\n" +
                    "Server: Apache/2.2.21 (Win32) mod_ssl/2.2.21 O\n" +
                    "read\n" +
                    "penSSL/0.9.8t PHP/5.3.10\n" +
                     "X-Powered-By: PHP/5.3.10\n" +
                    "Content-Length: "+body.length()+"\n" +
                    "Content-Type: text/html\n\n";
        
        try{
            client.getOutputStream().write((head+body).getBytes());
            client.close();
        }catch(IOException e){}
    }
    
    
    
    public synchronized int getTotalTransfered(Hashtable hashv){
        Object a = hashv.get("Transfered");
        if(a==null)
            hashv.put("Transfered", 0);
        Integer h; 
        try{
           h = Integer.parseInt(hashv.get("Transfered")+""); 
        return h;
        }catch(NumberFormatException e){
            hashv.put("Total Transfer",0);
           return 0; 
        }
    }
    
    public String get(String x){
        String[] s = x.split("\n");
        for (int i = 0; i < s.length; i++) {
            System.out.println("NEXT="+s[i]);
        }
        return null;
    }

    public String getTicket(String x){
        int a = x.indexOf("TICKET");
        int b = x.indexOf("&",a);
        int c = x.indexOf("\n",a);
        if(a>-1 & b>-1)
            return x.substring(a+7, b);
        else if(a>-1 & c > a)
            return x.substring(a+7,c).trim();
        return null;
    }
    
    public String getTime(int time){
        if(time==-1)
            return "None";
        int h = time/3600;
        int m = (time/60)-(h*60);
        int s = (time - ((h*3600)+(m*60)));
        String hr = h+"",mn = m+"",sc=s+"";
        return (hr.length()==1?"0"+hr:hr)+":"+(mn.length()==1?"0"+mn:mn)+":"+(sc.length()==1?("0"+sc):sc);
        
    }
    public String convert2date(String a,boolean start){
        if(a.length() < 10)
            return "2013-01-01 00:00:00";
        String x = a.substring(6, 10)+"-"+a.substring(3, 5)+"-"+a.substring(0, 2)+" "+((start)?"00:00:00":"23:59:59");
        return x;
    }
    public String getDate(int z){
    String str = null;
    Date d = new Date();
    String dat=d.getMonth()+1+"",day=d.getDate()+"",yr=Calendar.getInstance().get(1)+"",hr=d.getHours()+"",min=d.getMinutes()+"",sec=d.getSeconds()+"";
    String dat2=(d.getDate()-1)>1?(d.getDate()-1)+"":d.getDate()+"";
    day = day.length()==1?"0"+day:day;
    dat2 = dat2.length()==1?"0"+dat2:dat2;
    dat = dat.length()==1?"0"+dat:dat;
    hr  = hr.length()==1?"0"+hr:hr;
    min = min.length()==1?"0"+min:min;
    sec = sec.length()==1?"0"+sec:sec;
    if(z==1)
        str = day+"/"+dat+"/"+yr;
    if(z==2)
    str = day+"/"+dat2+"/"+yr;
    if(z==3)
    str = day+"/"+dat+"/"+yr+" "+hr+":"+min+":"+sec;
    if(z==4)
    str = hr+":"+min+":"+sec+ " GMT";
    if(z==5)
    str = hr+":"+min;    
    if(z==6)
    str = yr+"/"+dat+"/"+day+" "+hr+":"+min+":"+sec;    
    return str;        
}
    
    public String dateToString(String a){
        if(a.length()<10)
            return "";
        String d = a.substring(0,2);
        String m = a.substring(3, 5);
        int mn = getNum(m);
        mn = mn-1;
        m = p(mn);
        String y = a.substring(6);
        return y+m+d;
    }
    
    public String stringToDate(String a){
        if(a.length()<8)
            return "";
        String y = a.substring(0,4);
        int m = getNum(a.substring(5, 7));
        String d = a.substring(8,10);
        String t = a.substring(11);
        String mn = p(m);
        return String.format("%s/%s/%s %s",d,mn,y,t );
    }
    
    public int getNum(String a){
        try{
            return Integer.parseInt(a);
        }catch(NumberFormatException e){}
        return 0;
    }
    
    public String p(int a){
        return ((a+"").length()==1)?("0"+a):a+"";
    }
    
    public String getSocketName(Socket client){
        String a = client.getInetAddress().getHostName();
        int b = a.indexOf(".");
        if(b>4)
            return a.substring(0, b);
        return a;
    }
    
    public boolean isExempted(String mac){
        Vector vec = (Vector) load.settings.get("Exempt");
        if(vec==null)
            return false;
        return vec.contains(mac);
    }
     
    public void reduceSpeed(Hashtable hash){
        hash.put("Max Speed",load.reducedSpeed);
    }
    
    public void reduceConnections(Hashtable hash){
        hash.put("Connection Limit",load.ReducedConn);
    }
}
