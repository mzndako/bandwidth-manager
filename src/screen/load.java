package screen;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author MZ
 */
import data.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import proxy.*;

public class load extends javax.swing.JFrame {
    cProxy proxy;
    public static int size = 1024;
    public static int maxSpeed = 30 * (int) size ;
    public static int reducedSpeed = 8 * (int) size ;
    public static int LMaxSpeed = 60 * size;
    public static int LConnLimit = 30;
    public static int ReducedConn = 8;
    public static int maxConn = 20;
    public static int percentage = 100;
    public static int ConnTimeout = 0;
    public static int autoLogout = 300;
    public static String servername = "127.0.0.1";
    public static int serverport = 8080;
    public static String databasename = "whitehouse";
    public static String databaseuser = "whitehouse";
    public static String databasepassword = "whitehouse";
    
    public static int limit = 20 * (int) (size * size);
    public static String computerName = null;
    public static Hashtable<String,Hashtable> unique = new Hashtable<String,Hashtable>();
    public static Hashtable<String,String> timer = new Hashtable<String,String>();
    related r = new related(this);
    int port = 8080;
    HashMap<String,Integer> hash;
    public static Hashtable settings;
    private Hashtable rSite;
    private Hashtable tBandWidth,prize;
    int intDownload,previousDownload,intUpload,previousUpload;
    database database;
    Records record;
    int sTime = 0;
    int connectedMac = 1;
    int maxConnection = 15;
    public static boolean pause = false;
    public static boolean recordb = false;
    int xx = 0;
    Timer timeBandWidth = new Timer(1000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if(recordb)
                refreshBandWidth();
        }
     });
    
    Timer time = new Timer(1000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            refresh();
            sTime++;
            if(sTime>4){
                save();
                sTime = 0;
            }
        }
    });
    /**
     * Creates new form load
     */

    
    public load() {
        loading.setP();
        record = new Records();
            loading.setP();
        loadSettings();
        database = new database(databasename); 
        
        initComponents();
            loading.setP();
           
        
        time.start();
        timeBandWidth.start();
        jComboBox2.setModel(new DefaultComboBoxModel(vTickets()));
        updateNumber();
            loading.setP();
      new trayIcon().createAndShowGUI();
      loading.setP();
      try{
        loadPrize();
      }catch(NullPointerException e){
      }
      print(database.getErr());
      jLabel43.setText("Loading Account. Please wait....");
    }
    
    private void loadPrize(){
      jTextField24.setBackground(Color.BLUE);
      jTextField24.setForeground(Color.white);
      prize = database.getList();
      if(prize == null) return;
      jComboBox9.removeAllItems();
      Enumeration en = prize.keys();
      while(en.hasMoreElements()){
          String name = en.nextElement()+"";
          Vector v = (Vector) prize.get(name);
          if(v==null)continue;
          int x = getNum(v.elementAt(1)+"");
          if(x>-1){
              name = name+":("+x+")";
          }
          jComboBox9.addItem(name);
      }
    }
    
    private int getV(Vector v,int num){
        try{
            return (int) v.elementAt(num);
        }catch(Exception e){}
        return 0;
    }
    
    public void loadSettings(){
        settings = record.getSettings("settings");
       System.out.println("settings="+settings);
        Vector v = (Vector) settings.get("content");
        if(v!=null){
            maxSpeed = (int) v.elementAt(0);
            maxConn = (int) v.elementAt(1);
            limit = (int) v.elementAt(2);
            ConnTimeout = getV(v, 3);
        }
        
        Vector vv = (Vector) settings.get("lcontent");
        if(vv!=null){
            LMaxSpeed = (int) vv.elementAt(0);
            LConnLimit = (int) vv.elementAt(1);
            reducedSpeed = getV(vv, 2);
            ReducedConn = getV(vv,3);
            autoLogout = getV(vv,4);
            percentage = getV(vv,5);
        }
        
        Vector vvv = (Vector) settings.get("server");
        if(vvv!=null){
            servername = (String) vvv.elementAt(0);
            serverport = (int) vvv.elementAt(1);
            databasename = (String) vvv.elementAt(2);
            databaseuser = (String) vvv.elementAt(3);
            databasepassword = (String) vvv.elementAt(4);
            
            limit = (int) v.elementAt(2);
        }
        
        Hashtable hd = (Hashtable) settings.get("Laptop");
        if(hd==null){
            hd = new Hashtable();
            settings.put("Laptop",hd);
        }
        rSite = record.getSettings("URL");
        if(rSite==null){
            rSite = new Hashtable();
            record.saveSettings(rSite, "URL");
        }
        
        tBandWidth = record.getSettings("bandwidth");
        if(tBandWidth==null){
            tBandWidth = new Hashtable();
            Vector vec = new Vector();vec.add(0);vec.add(0);
            tBandWidth.put(getToday(),vec);
        }
        getBandW();
    }
    int nxtDayD = 0;
    int nxtDayU = 0;
    private void refreshBandWidth(){
        Vector today;
        String day = r.getDate(1);
        try{
            today = (Vector) tBandWidth.get(day);
        }catch(NullPointerException e){
            today = null;
        }
        if(today==null){
            intDownload = 0;
            intUpload = 0;
        }
        Vector v = new Vector();
        v.add(intUpload);v.add(intDownload);
        tBandWidth.put(day,v);
        record.saveSettings(tBandWidth, "bandwidth");            
    }
    
    private void getBandW(){
            Vector v = (Vector) tBandWidth.get(getToday());
            if(v!=null){
                intUpload = (int) v.elementAt(0);
                intDownload = (int) v.elementAt(1);
            }else{
                Vector vec = new Vector();vec.add(0);vec.add(0);
                tBandWidth.put(getToday(),vec);
                intDownload = 0;
                intUpload = 0;
            }               
            record.saveSettings(tBandWidth, "bandwidth");
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        loading.setP();
        dialogConnect = new javax.swing.JDialog();
        jPanel1 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        dialogSettings = new javax.swing.JDialog();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jTextField14 = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jTextField29 = new javax.swing.JTextField();
        jLabel56 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jTextField30 = new javax.swing.JTextField();
        jPanel23 = new javax.swing.JPanel();
        jLabel61 = new javax.swing.JLabel();
        jTextField31 = new javax.swing.JTextField();
        jLabel63 = new javax.swing.JLabel();
        jTextField33 = new javax.swing.JTextField();
        jLabel64 = new javax.swing.JLabel();
        jPasswordField1 = new javax.swing.JPasswordField();
        jButton26 = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jTextField15 = new javax.swing.JTextField();
        jTextField16 = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jTextField17 = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jTextField18 = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jTextField20 = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jTextField19 = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        jTextField9 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        jComboBox6 = new javax.swing.JComboBox();
        jButton19 = new javax.swing.JButton();
        jPanel19 = new javax.swing.JPanel();
        jButton22 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jPanel20 = new javax.swing.JPanel();
        jTextField22 = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jTextField25 = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jScrollPane10 = new javax.swing.JScrollPane();
        jTable6 = new javax.swing.JTable();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        dialogSettings2 = new javax.swing.JDialog();
        jPanel7 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jFrame1 = new javax.swing.JFrame();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jFrame2 = new javax.swing.JFrame();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jToolBar4 = new javax.swing.JToolBar();
        jLabel16 = new javax.swing.JLabel();
        jTextField12 = new javax.swing.JTextField();
        jButton16 = new javax.swing.JButton();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jFrame3 = new javax.swing.JFrame();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jToolBar3 = new javax.swing.JToolBar();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(11, 0), new java.awt.Dimension(11, 0), new java.awt.Dimension(11, 32767));
        jButton12 = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(11, 0), new java.awt.Dimension(11, 0), new java.awt.Dimension(11, 32767));
        jMenuBar3 = new javax.swing.JMenuBar();
        jMenu5 = new javax.swing.JMenu();
        jInternalFrame1 = new javax.swing.JInternalFrame();
        jFrame4 = new javax.swing.JFrame();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jFrameReport = new javax.swing.JFrame();
        jPanel15 = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        jPanel18 = new javax.swing.JPanel();
        jToolBar5 = new javax.swing.JToolBar();
        jLabel26 = new javax.swing.JLabel();
        jComboBox3 = new DateComboBox();
        jLabel41 = new javax.swing.JLabel();
        jComboBox8 = new DateComboBox();
        jButton17 = new javax.swing.JButton();
        jLabel33 = new javax.swing.JLabel();
        jTextField21 = new javax.swing.JTextField();
        jComboBox7 = new javax.swing.JComboBox();
        jButton20 = new javax.swing.JButton();
        jMenuItem8 = new javax.swing.JMenuItem();
        jFrameReport1 = new javax.swing.JFrame();
        jPanel17 = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        jTable7 = new javax.swing.JTable();
        jToolBar6 = new javax.swing.JToolBar();
        jLabel28 = new javax.swing.JLabel();
        jComboBox5 = new DateComboBox();
        jButton18 = new javax.swing.JButton();
        jMenuBar6 = new javax.swing.JMenuBar();
        jMenu12 = new javax.swing.JMenu();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem12 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenu11 = new javax.swing.JMenu();
        loading.setP();
        dialogConnect1 = new javax.swing.JDialog();
        jPanel22 = new javax.swing.JPanel();
        jComboBox10 = new javax.swing.JComboBox();
        jLabel48 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jSpinner2 = new javax.swing.JSpinner();
        jLabel53 = new javax.swing.JLabel();
        jTextField23 = new javax.swing.JTextField();
        jButton11 = new javax.swing.JButton();
        jLabel54 = new javax.swing.JLabel();
        jTextField26 = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        loading.setP();
        jTextArea1 = new javax.swing.JTextArea(){
            public void append(String a){
                super.append(a+"\n");
                setCaretPosition(super.getText().length());
            }
        };
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        loading.setP();
        jTree1 = new javax.swing.JTree();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jPanel21 = new javax.swing.JPanel();
        jComboBox9 = new javax.swing.JComboBox();
        jLabel44 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jTextField27 = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jTextField24 = new javax.swing.JTextField();
        jButton24 = new javax.swing.JButton();
        jComboBox11 = new javax.swing.JComboBox();
        jButton25 = new javax.swing.JButton();
        jTextField28 = new javax.swing.JTextField();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel43 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        loading.setP();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jToolBar1 = new javax.swing.JToolBar();
        jButton4 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jToggleButton1 = new javax.swing.JToggleButton();
        jToolBar2 = new javax.swing.JToolBar();
        jLabel15 = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(11, 0), new java.awt.Dimension(11, 0), new java.awt.Dimension(11, 32767));
        jTextField8 = new javax.swing.JTextField();
        jComboBox2 = new javax.swing.JComboBox();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(11, 0), new java.awt.Dimension(11, 0), new java.awt.Dimension(11, 32767));
        jButton10 = new javax.swing.JButton();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(11, 0), new java.awt.Dimension(11, 0), new java.awt.Dimension(11, 32767));
        jButton21 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        loading.setP();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        jMenu7 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenu9 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenu10 = new javax.swing.JMenu();
        jMenu8 = new javax.swing.JMenu();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();

        dialogConnect.setTitle("Network Connect");
        dialogConnect.setAlwaysOnTop(true);
        dialogConnect.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        dialogConnect.setFocusTraversalPolicyProvider(true);
        dialogConnect.setMinimumSize(new java.awt.Dimension(415, 100));
        dialogConnect.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        dialogConnect.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL);
        dialogConnect.setType(java.awt.Window.Type.POPUP);
        dialogConnect.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                dialogConnectFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                dialogConnectFocusLost(evt);
            }
        });

        jPanel1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPanel1KeyPressed(evt);
            }
        });

        jComboBox1.setModel(model());
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel2.setText("Select Network Adapter to listen to :");

        jTextField1.setText("8080");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel3.setText("Port Number : ");

        jButton3.setText("Connect");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                        .addComponent(jButton3))))
        );

        javax.swing.GroupLayout dialogConnectLayout = new javax.swing.GroupLayout(dialogConnect.getContentPane());
        dialogConnect.getContentPane().setLayout(dialogConnectLayout);
        dialogConnectLayout.setHorizontalGroup(
            dialogConnectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogConnectLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        dialogConnectLayout.setVerticalGroup(
            dialogConnectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        dialogSettings.setTitle("BANDWIDTH SETTINGS");
        dialogSettings.setBackground(new java.awt.Color(153, 255, 255));
        dialogSettings.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialogSettings.setResizable(false);

        jTabbedPane2.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        jTabbedPane2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane2StateChanged(evt);
            }
        });
        jTabbedPane2.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jTabbedPane2ComponentShown(evt);
            }
        });
        jTabbedPane2.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTabbedPane2PropertyChange(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jLabel4.setFont(jLabel4.getFont().deriveFont(jLabel4.getFont().getSize()+1f));
        jLabel4.setText("Maximum Connection: ");
        jLabel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel5.setFont(jLabel5.getFont().deriveFont(jLabel5.getFont().getSize()+1f));
        jLabel5.setText("Maximum Speed: ");
        jLabel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jTextField2.setText("10");
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField2KeyTyped(evt);
            }
        });

        jTextField3.setText("30");
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });
        jTextField3.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                jTextField3InputMethodTextChanged(evt);
            }
        });
        jTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField3KeyTyped(evt);
            }
        });

        jLabel6.setText("kb/s");

        jTextField4.setText("20");
        jTextField4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField4KeyTyped(evt);
            }
        });

        jLabel7.setFont(jLabel7.getFont().deriveFont(jLabel7.getFont().getSize()+1f));
        jLabel7.setText("Maximum Download: ");
        jLabel7.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel8.setText("MB");

        jLabel14.setFont(jLabel14.getFont().deriveFont(jLabel14.getFont().getSize()+1f));
        jLabel14.setText("Connection Timeout: ");
        jLabel14.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jTextField14.setText("20");
        jTextField14.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField14KeyTyped(evt);
            }
        });

        jLabel27.setText("Sec");

        jTextField29.setBackground(new java.awt.Color(255, 255, 153));
        jTextField29.setText("127.0.0.1");

        jLabel56.setBackground(new java.awt.Color(0, 255, 0));
        jLabel56.setFont(jLabel56.getFont().deriveFont(jLabel56.getFont().getSize()+1f));
        jLabel56.setText("Server Address:");
        jLabel56.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel56.setOpaque(true);

        jLabel60.setBackground(new java.awt.Color(0, 255, 0));
        jLabel60.setFont(jLabel60.getFont().deriveFont(jLabel60.getFont().getSize()+1f));
        jLabel60.setText("Port:");
        jLabel60.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel60.setOpaque(true);

        jTextField30.setBackground(new java.awt.Color(255, 255, 153));
        jTextField30.setText("8080");

        jPanel23.setBackground(new java.awt.Color(0, 51, 153));

        jLabel61.setBackground(new java.awt.Color(0, 255, 0));
        jLabel61.setFont(jLabel61.getFont().deriveFont(jLabel61.getFont().getStyle() | java.awt.Font.BOLD, jLabel61.getFont().getSize()+2));
        jLabel61.setForeground(new java.awt.Color(255, 255, 255));
        jLabel61.setText("Database Name:");
        jLabel61.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jTextField31.setBackground(new java.awt.Color(255, 255, 153));
        jTextField31.setText("127.0.0.1");

        jLabel63.setBackground(new java.awt.Color(0, 255, 0));
        jLabel63.setFont(jLabel63.getFont().deriveFont(jLabel63.getFont().getStyle() | java.awt.Font.BOLD, jLabel63.getFont().getSize()+2));
        jLabel63.setForeground(new java.awt.Color(255, 255, 255));
        jLabel63.setText("Database Username:");
        jLabel63.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jTextField33.setBackground(new java.awt.Color(255, 255, 153));
        jTextField33.setText("127.0.0.1");

        jLabel64.setBackground(new java.awt.Color(0, 255, 0));
        jLabel64.setFont(jLabel64.getFont().deriveFont(jLabel64.getFont().getStyle() | java.awt.Font.BOLD, jLabel64.getFont().getSize()+2));
        jLabel64.setForeground(new java.awt.Color(255, 255, 255));
        jLabel64.setText("Database Password:");
        jLabel64.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jPasswordField1.setBackground(new java.awt.Color(255, 255, 102));
        jPasswordField1.setText("jPasswordField1");

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jTextField33, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel61, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel63, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                    .addComponent(jLabel64, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField31, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPasswordField1))
                .addGap(0, 18, Short.MAX_VALUE))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addComponent(jLabel61)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel63)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel64)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 27, Short.MAX_VALUE))
        );

        jButton26.setText("Reconnect");
        jButton26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton26ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jTextField4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField3)
                            .addComponent(jTextField14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel8)
                            .addComponent(jLabel27)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel60, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel56, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE))
                        .addGap(28, 28, 28)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField29, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField30, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(40, 40, 40)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jButton26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6))
                                .addGap(17, 17, 17)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jTextField2))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel7)
                                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel27))
                        .addGap(19, 19, 19)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel56)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel60))
                    .addComponent(jButton26, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(77, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("General Settings", jPanel5);

        jPanel13.setBorder(javax.swing.BorderFactory.createCompoundBorder());

        jPanel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jLabel29.setFont(jLabel29.getFont().deriveFont(jLabel29.getFont().getSize()+1f));
        jLabel29.setText("Reduced Speed: ");
        jLabel29.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel30.setFont(jLabel30.getFont().deriveFont(jLabel30.getFont().getSize()+1f));
        jLabel30.setText("Maximum Speed: ");
        jLabel30.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jTextField15.setText("10");
        jTextField15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField15ActionPerformed(evt);
            }
        });
        jTextField15.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField15KeyTyped(evt);
            }
        });

        jTextField16.setText("30");
        jTextField16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField16ActionPerformed(evt);
            }
        });
        jTextField16.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                jTextField16InputMethodTextChanged(evt);
            }
        });
        jTextField16.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField16KeyTyped(evt);
            }
        });

        jLabel31.setText("kb/s");

        jTextField17.setText("20");
        jTextField17.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField17KeyTyped(evt);
            }
        });

        jLabel32.setFont(jLabel32.getFont().deriveFont(jLabel32.getFont().getSize()+1f));
        jLabel32.setText("Maximum Connections: ");
        jLabel32.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel34.setFont(jLabel34.getFont().deriveFont(jLabel34.getFont().getSize()+1f));
        jLabel34.setText("Reduced Connections: ");
        jLabel34.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jTextField18.setText("20");
        jTextField18.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField18KeyTyped(evt);
            }
        });

        jLabel37.setText("kb/s");

        jLabel38.setText("Sec");

        jLabel39.setFont(jLabel39.getFont().deriveFont(jLabel39.getFont().getSize()+1f));
        jLabel39.setText("Download Limit (Reduced): ");
        jLabel39.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jTextField20.setText("20");
        jTextField20.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField20KeyTyped(evt);
            }
        });

        jLabel35.setFont(jLabel35.getFont().deriveFont(jLabel35.getFont().getSize()+1f));
        jLabel35.setText("Automatic Logout (Timeout): ");
        jLabel35.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jTextField19.setText("20");
        jTextField19.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField19KeyTyped(evt);
            }
        });

        jLabel40.setText("%");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel39, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField15, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                    .addComponent(jTextField16, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField18, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField17, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                    .addComponent(jTextField20)
                    .addComponent(jTextField19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel31)
                    .addComponent(jLabel37)
                    .addComponent(jLabel38)
                    .addComponent(jLabel40))
                .addGap(0, 220, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jLabel31))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel30)
                            .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(jTextField17, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel37)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel29)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel39)
                    .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35)
                    .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38))
                .addContainerGap(98, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("Settings", jPanel6);

        jPanel16.setBorder(javax.swing.BorderFactory.createCompoundBorder());

        jButton14.setText("Add");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton15.setText("Remove");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jPanel14.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 44;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 1, 0, 0);
        jPanel14.add(jTextField9, gridBagConstraints);

        jLabel18.setText("Ticket Name :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(14, 10, 0, 0);
        jPanel14.add(jLabel18, gridBagConstraints);

        jLabel19.setText("Time : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(9, 10, 0, 0);
        jPanel14.add(jLabel19, gridBagConstraints);

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0.5", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "Nill" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 1, 0, 0);
        jPanel14.add(jComboBox4, gridBagConstraints);

        jLabel17.setText("Hr");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(9, 4, 0, 0);
        jPanel14.add(jLabel17, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 44;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 1, 0, 0);
        jPanel14.add(jTextField10, gridBagConstraints);

        jLabel20.setText("BandWidth :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(9, 10, 0, 0);
        jPanel14.add(jLabel20, gridBagConstraints);

        jLabel21.setText("MB");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(9, 4, 0, 0);
        jPanel14.add(jLabel21, gridBagConstraints);

        jLabel22.setText("Days");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(14, 4, 0, 10);
        jPanel14.add(jLabel22, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 44;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 1, 0, 0);
        jPanel14.add(jTextField11, gridBagConstraints);

        jLabel23.setText("Expires :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(14, 10, 0, 0);
        jPanel14.add(jLabel23, gridBagConstraints);

        jLabel25.setText("Amount :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 10, 0, 0);
        jPanel14.add(jLabel25, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.ipadx = 44;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 1, 11, 0);
        jPanel14.add(jTextField13, gridBagConstraints);

        jLabel24.setText("N");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 4, 0, 0);
        jPanel14.add(jLabel24, gridBagConstraints);

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Name", "Time", "BandWidth", "Expires", "Amount"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane8.setViewportView(jTable4);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 507, Short.MAX_VALUE)
            .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel16Layout.createSequentialGroup()
                    .addGap(26, 26, 26)
                    .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton15)
                    .addGap(30, 30, 30))
                .addGroup(jPanel16Layout.createSequentialGroup()
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 293, Short.MAX_VALUE)
            .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel16Layout.createSequentialGroup()
                    .addGap(5, 5, 5)
                    .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(25, 25, 25)
                    .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(35, Short.MAX_VALUE)))
        );

        jTabbedPane3.addTab("Tickets", jPanel16);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane3)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane3, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        jTabbedPane2.addTab("Laptop", jPanel13);

        jLabel36.setFont(jLabel36.getFont().deriveFont(jLabel36.getFont().getSize()+1f));
        jLabel36.setText("Theme: ");
        jLabel36.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jButton19.setText("Set Theme");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton19)
                    .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(306, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton19)
                .addContainerGap(246, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Theme", jPanel4);

        jButton22.setText("Add");
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        jButton23.setText("Remove");
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });

        jLabel42.setText("Name :");

        jLabel45.setText("Stock :");

        jLabel49.setText("Prize/Unit :");

        jLabel50.setText("N");

        jLabel51.setText("Editable :");

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel42)
                    .addComponent(jLabel45)
                    .addComponent(jLabel49)
                    .addComponent(jLabel51))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField22)
                            .addComponent(jTextField25, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jCheckBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(jTextField22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)
                        .addComponent(jCheckBox2))
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(jLabel42)
                        .addGap(19, 19, 19)
                        .addComponent(jLabel45)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel49)
                            .addComponent(jTextField25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel50))))
                .addGap(18, 18, 18)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox1)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel51)))
                .addContainerGap(49, Short.MAX_VALUE))
        );

        jTable6.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Name", "Quantity", "Amount"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Integer.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane10.setViewportView(jTable6);

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton23)
                .addGap(29, 29, 29))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                .addContainerGap(41, Short.MAX_VALUE)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton23, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(41, 41, 41))
        );

        jTabbedPane2.addTab("Prizes", jPanel19);

        jButton7.setText("Save");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("Cancel");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dialogSettingsLayout = new javax.swing.GroupLayout(dialogSettings.getContentPane());
        dialogSettings.getContentPane().setLayout(dialogSettingsLayout);
        dialogSettingsLayout.setHorizontalGroup(
            dialogSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogSettingsLayout.createSequentialGroup()
                .addGroup(dialogSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dialogSettingsLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(dialogSettingsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTabbedPane2)))
                .addGap(24, 24, 24))
        );
        dialogSettingsLayout.setVerticalGroup(
            dialogSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dialogSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        dialogSettings.getAccessibleContext().setAccessibleParent(dialogSettings);

        dialogSettings2.setTitle("Network Connect");
        dialogSettings2.setAlwaysOnTop(true);
        dialogSettings2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        dialogSettings2.setMinimumSize(new java.awt.Dimension(290, 100));
        dialogSettings2.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        dialogSettings2.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL);
        dialogSettings2.setType(java.awt.Window.Type.POPUP);

        jPanel7.setMinimumSize(new java.awt.Dimension(200, 90));

        jLabel9.setText("Maximum Speed : ");
        jLabel9.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), null));

        jTextField5.setText("0");
        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });

        jLabel10.setText("Download Limit   : ");
        jLabel10.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), null));

        jButton5.setText("save");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel11.setText("Connection Limit : ");
        jLabel11.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), null));

        jTextField6.setText("0");
        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });

        jTextField7.setText("0");
        jTextField7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField7ActionPerformed(evt);
            }
        });

        jLabel12.setText("MB");

        jLabel13.setText("kb/s");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(17, 17, 17)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel13))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(17, 17, 17)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel12))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(16, 16, 16)
                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(jButton5))))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13)))
                .addGap(10, 10, 10)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12)))
                .addGap(10, 10, 10)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout dialogSettings2Layout = new javax.swing.GroupLayout(dialogSettings2.getContentPane());
        dialogSettings2.getContentPane().setLayout(dialogSettings2Layout);
        dialogSettings2Layout.setHorizontalGroup(
            dialogSettings2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        dialogSettings2Layout.setVerticalGroup(
            dialogSettings2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jTextArea3.setColumns(20);
        jTextArea3.setLineWrap(true);
        jTextArea3.setRows(5);
        jScrollPane5.setViewportView(jTextArea3);

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 682, Short.MAX_VALUE)
            .addGroup(jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE))
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 372, Short.MAX_VALUE)
            .addGroup(jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE))
        );

        jFrame2.setTitle("Tickets");

        jScrollPane6.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));

        jTable2.setAutoCreateRowSorter(true);
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Ticket", "Total Time", "Time Left", "Total Download", "Download Limit", "Last Login"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.setMinimumSize(new java.awt.Dimension(20, 24));
        jScrollPane6.setViewportView(jTable2);
        jTable2.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(3).setHeaderValue("Total Download");
            jTable2.getColumnModel().getColumn(4).setHeaderValue("Download Limit");
            jTable2.getColumnModel().getColumn(5).setHeaderValue("Last Login");
        }

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 611, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 611, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 360, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel8Layout.createSequentialGroup()
                    .addGap(52, 52, 52)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jToolBar4.setRollover(true);

        jLabel16.setText("Search:");
        jToolBar4.add(jLabel16);

        jTextField12.setMinimumSize(new java.awt.Dimension(60, 30));
        jTextField12.setPreferredSize(new java.awt.Dimension(60, 30));
        jTextField12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField12ActionPerformed(evt);
            }
        });
        jTextField12.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField12KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField12KeyTyped(evt);
            }
        });
        jToolBar4.add(jTextField12);

        jButton16.setText("Search");
        jButton16.setFocusable(false);
        jButton16.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton16.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });
        jToolBar4.add(jButton16);

        jMenu3.setText("File");
        jMenuBar2.add(jMenu3);

        jMenu4.setText("Tools");
        jMenuBar2.add(jMenu4);

        jFrame2.setJMenuBar(jMenuBar2);

        javax.swing.GroupLayout jFrame2Layout = new javax.swing.GroupLayout(jFrame2.getContentPane());
        jFrame2.getContentPane().setLayout(jFrame2Layout);
        jFrame2Layout.setHorizontalGroup(
            jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame2Layout.createSequentialGroup()
                .addComponent(jToolBar4, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 398, Short.MAX_VALUE))
            .addGroup(jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jFrame2Layout.setVerticalGroup(
            jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(324, Short.MAX_VALUE))
            .addGroup(jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jFrame2.getAccessibleContext().setAccessibleParent(this);

        jFrame3.setTitle("Exempted Computers");

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Computer", "IP Address", "Mac Address"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable3.setMinimumSize(new java.awt.Dimension(20, 24));
        jScrollPane7.setViewportView(jTable3);
        jTable3.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 524, Short.MAX_VALUE)
            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 290, Short.MAX_VALUE)
            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel9Layout.createSequentialGroup()
                    .addGap(25, 25, 25)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jToolBar3.setRollover(true);
        jToolBar3.add(filler3);

        jButton12.setText("Remove");
        jButton12.setFocusable(false);
        jButton12.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton12.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });
        jToolBar3.add(jButton12);
        jToolBar3.add(filler4);

        jMenu5.setText("File");
        jMenuBar3.add(jMenu5);

        jFrame3.setJMenuBar(jMenuBar3);

        javax.swing.GroupLayout jFrame3Layout = new javax.swing.GroupLayout(jFrame3.getContentPane());
        jFrame3.getContentPane().setLayout(jFrame3Layout);
        jFrame3Layout.setHorizontalGroup(
            jFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame3Layout.createSequentialGroup()
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 431, Short.MAX_VALUE))
            .addGroup(jFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jFrame3Layout.setVerticalGroup(
            jFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame3Layout.createSequentialGroup()
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 265, Short.MAX_VALUE))
            .addGroup(jFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFrame3Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jInternalFrame1.setVisible(true);

        javax.swing.GroupLayout jInternalFrame1Layout = new javax.swing.GroupLayout(jInternalFrame1.getContentPane());
        jInternalFrame1.getContentPane().setLayout(jInternalFrame1Layout);
        jInternalFrame1Layout.setHorizontalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 618, Short.MAX_VALUE)
        );
        jInternalFrame1Layout.setVerticalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 419, Short.MAX_VALUE)
        );

        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 540, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 70, Short.MAX_VALUE)
        );

        jPanel10.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 540, 70));

        jPanel12.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel10.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 540, 370));

        javax.swing.GroupLayout jFrame4Layout = new javax.swing.GroupLayout(jFrame4.getContentPane());
        jFrame4.getContentPane().setLayout(jFrame4Layout);
        jFrame4Layout.setHorizontalGroup(
            jFrame4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 540, Short.MAX_VALUE)
            .addGroup(jFrame4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jFrame4Layout.setVerticalGroup(
            jFrame4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 441, Short.MAX_VALUE)
            .addGroup(jFrame4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE))
        );

        jFrameReport.setTitle("Daily Report");

        jPanel15.setLayout(new java.awt.BorderLayout());

        jScrollPane9.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));

        jTable5.setAutoCreateRowSorter(true);
        jTable5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Date", "Ticket", "Amount"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable5.setMinimumSize(new java.awt.Dimension(20, 24));
        jScrollPane9.setViewportView(jTable5);
        jTable5.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jPanel15.add(jScrollPane9, java.awt.BorderLayout.CENTER);

        jToolBar5.setRollover(true);

        jLabel26.setText("SEARCH   From:");
        jToolBar5.add(jLabel26);

        jComboBox3.addItem(r.getDate(1));
        jToolBar5.add(jComboBox3);

        jLabel41.setText("     To:");
        jToolBar5.add(jLabel41);

        jComboBox3.addItem(r.getDate(1));
        jToolBar5.add(jComboBox8);

        jButton17.setText("Search");
        jButton17.setFocusable(false);
        jButton17.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton17.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });
        jToolBar5.add(jButton17);

        jLabel33.setText("Add to Expenditures.   Amount (N):");

        jTextField21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField21ActionPerformed(evt);
            }
        });

        jComboBox7.setEditable(true);
        jComboBox7.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Dad", "Fuel", "Nepa", "Paper", "Generator Repairs" }));
        jComboBox7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox7ActionPerformed(evt);
            }
        });
        jComboBox7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox7KeyPressed(evt);
            }
        });

        jButton20.setText("Add");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar5, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField21, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton20, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(87, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(jToolBar5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(jTextField21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton20))
                .addContainerGap())
        );

        javax.swing.GroupLayout jFrameReportLayout = new javax.swing.GroupLayout(jFrameReport.getContentPane());
        jFrameReport.getContentPane().setLayout(jFrameReportLayout);
        jFrameReportLayout.setHorizontalGroup(
            jFrameReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jFrameReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, 611, Short.MAX_VALUE))
        );
        jFrameReportLayout.setVerticalGroup(
            jFrameReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrameReportLayout.createSequentialGroup()
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 349, Short.MAX_VALUE))
            .addGroup(jFrameReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jFrameReportLayout.createSequentialGroup()
                    .addGap(64, 64, 64)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jMenuItem8.setText("jMenuItem8");

        jFrameReport1.setTitle("Total BandWidth");

        jScrollPane11.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));

        jTable7.setAutoCreateRowSorter(true);
        jTable7.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Date", "Total Upload", "Total Download"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable7.setColumnSelectionAllowed(true);
        jTable7.setMinimumSize(new java.awt.Dimension(20, 24));
        jScrollPane11.setViewportView(jTable7);
        jTable7.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 611, Short.MAX_VALUE)
            .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 611, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 368, Short.MAX_VALUE)
            .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                    .addGap(0, 40, Short.MAX_VALUE)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)))
        );

        jToolBar6.setRollover(true);

        jLabel28.setText("Search:");
        jToolBar6.add(jLabel28);

        jComboBox3.addItem(r.getDate(1));
        jToolBar6.add(jComboBox5);

        jButton18.setText("Search");
        jButton18.setFocusable(false);
        jButton18.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton18.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });
        jToolBar6.add(jButton18);

        jMenu12.setText("File");
        jMenuBar6.add(jMenu12);

        jFrameReport1.setJMenuBar(jMenuBar6);

        javax.swing.GroupLayout jFrameReport1Layout = new javax.swing.GroupLayout(jFrameReport1.getContentPane());
        jFrameReport1.getContentPane().setLayout(jFrameReport1Layout);
        jFrameReport1Layout.setHorizontalGroup(
            jFrameReport1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrameReport1Layout.createSequentialGroup()
                .addComponent(jToolBar6, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 186, Short.MAX_VALUE))
            .addGroup(jFrameReport1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel17, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jFrameReport1Layout.setVerticalGroup(
            jFrameReport1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrameReport1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(332, Short.MAX_VALUE))
            .addGroup(jFrameReport1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFrameReport1Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jMenuItem12.setText("jMenuItem12");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem12);
        jPopupMenu1.add(jSeparator1);

        jMenu11.setText("jMenu11");
        jPopupMenu1.add(jMenu11);

        dialogConnect1.setTitle("STOCKS");
        dialogConnect1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        dialogConnect1.setFocusTraversalPolicyProvider(true);
        dialogConnect1.setMinimumSize(new java.awt.Dimension(415, 100));
        dialogConnect1.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialogConnect1.setType(java.awt.Window.Type.POPUP);
        dialogConnect1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                dialogConnect1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                dialogConnect1FocusLost(evt);
            }
        });

        jPanel22.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPanel22KeyPressed(evt);
            }
        });

        jComboBox10.setModel(model());
        jComboBox10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox10ActionPerformed(evt);
            }
        });

        jLabel48.setText("Stock Name:");

        jLabel52.setText("Number of Stock: ");

        jSpinner2.setModel(new javax.swing.SpinnerNumberModel(0, 0, 99999, 1));

        jLabel53.setText("Total Amount (N): ");

        jTextField23.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField23.setText("0");
        jTextField23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField23ActionPerformed(evt);
            }
        });

        jButton11.setText("Add to Stock");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jLabel54.setText("Amount/Stock (N):");

        jTextField26.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField26ActionPerformed(evt);
            }
        });

        jLabel55.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel55.setText("Leave this blank if you are not changing the stock amount");

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton11))
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel48)
                            .addComponent(jLabel52)
                            .addComponent(jLabel53)
                            .addComponent(jLabel54))
                        .addGap(16, 16, 16)
                        .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox10, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel22Layout.createSequentialGroup()
                                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel55)
                                    .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jSpinner2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                                        .addComponent(jTextField26, javax.swing.GroupLayout.Alignment.LEADING))
                                    .addComponent(jTextField23, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))))))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48)
                    .addComponent(jComboBox10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSpinner2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel52, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel54))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel53))
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addComponent(jLabel55)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                        .addComponent(jTextField23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton11)
                .addGap(4, 4, 4))
        );

        javax.swing.GroupLayout dialogConnect1Layout = new javax.swing.GroupLayout(dialogConnect1.getContentPane());
        dialogConnect1.getContentPane().setLayout(dialogConnect1Layout);
        dialogConnect1Layout.setHorizontalGroup(
            dialogConnect1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogConnect1Layout.createSequentialGroup()
                .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        dialogConnect1Layout.setVerticalGroup(
            dialogConnect1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogConnect1Layout.createSequentialGroup()
                .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        setTitle("BANDWIDTH MANAGER");
        setFocusTraversalPolicyProvider(true);
        setName("BandWidth"); // NOI18N
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });

        jPanel2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel2.setPreferredSize(new java.awt.Dimension(250, 250));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jTabbedPane1.setName("View Log"); // NOI18N
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(214, 230));

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setName("View Log"); // NOI18N
        jScrollPane2.setViewportView(jTextArea1);

        jTabbedPane1.addTab("View Log", jScrollPane2);

        jSplitPane1.setDividerLocation(299);
        jSplitPane1.setDividerSize(4);

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jTree1.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTree1ValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(jTree1);

        jSplitPane1.setLeftComponent(jScrollPane3);

        jScrollPane4.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane4.setViewportBorder(javax.swing.BorderFactory.createCompoundBorder());

        jTextArea2.setEditable(false);
        jTextArea2.setBackground(new java.awt.Color(153, 153, 153));
        jTextArea2.setColumns(15);
        jTextArea2.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jTextArea2.setForeground(new java.awt.Color(255, 255, 255));
        jTextArea2.setRows(5);
        jTextArea2.setCaretColor(new java.awt.Color(255, 255, 255));
        jTextArea2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jTextArea2.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        jScrollPane4.setViewportView(jTextArea2);

        jSplitPane1.setRightComponent(jScrollPane4);

        jTabbedPane1.addTab("Details", jSplitPane1);

        jComboBox9.addItemListener(changeSelection());
        jComboBox9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox9ActionPerformed(evt);
            }
        });

        jLabel44.setText("Quantity: ");

        jSpinner1.addChangeListener(changeSpinner());
        jSpinner1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jSpinner1KeyTyped(evt);
            }
        });

        jTextField27.setMinimumSize(new java.awt.Dimension(20, 30));
        jTextField27.setPreferredSize(new java.awt.Dimension(20, 50));
        jTextField27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField27ActionPerformed(evt);
            }
        });
        jTextField27.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField27FocusGained(evt);
            }
        });
        jTextField27.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField27KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField27KeyTyped(evt);
            }
        });

        jLabel46.setText("Amount: ");

        jLabel47.setText("Total: ");

        jTextField24.setEditable(false);
        jTextField24.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTextField24.setMinimumSize(new java.awt.Dimension(60, 20));
        jTextField24.setOpaque(false);
        jTextField24.setPreferredSize(new java.awt.Dimension(80, 30));
        jTextField24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField24ActionPerformed(evt);
            }
        });

        jButton24.setText("Record");
        jButton24.setFocusable(false);
        jButton24.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton24.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });

        jComboBox11.setEditable(true);
        jComboBox11.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Dad", "Fuel", "Nepa", "Paper", "Generator Repairs" }));
        jComboBox11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox11ActionPerformed(evt);
            }
        });
        jComboBox11.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox11KeyPressed(evt);
            }
        });

        jButton25.setText("Add");
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });

        jTextField28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField28ActionPerformed(evt);
            }
        });

        jLabel57.setText(" Amount (N):");

        jLabel58.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel58.setForeground(new java.awt.Color(51, 102, 255));
        jLabel58.setText("SALES");

        jLabel59.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel59.setForeground(new java.awt.Color(51, 102, 255));
        jLabel59.setText("EXPENDITURES");

        jSeparator2.setBackground(new java.awt.Color(51, 51, 51));
        jSeparator2.setOpaque(true);

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(96, 96, 96)
                .addComponent(jLabel58)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 484, Short.MAX_VALUE)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel21Layout.createSequentialGroup()
                                .addComponent(jLabel57)
                                .addGap(23, 23, 23)
                                .addComponent(jTextField28))
                            .addComponent(jComboBox11, 0, 233, Short.MAX_VALUE)
                            .addComponent(jButton25, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(78, 78, 78))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                        .addComponent(jLabel59)
                        .addGap(122, 122, 122))))
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel44)
                            .addComponent(jLabel46)
                            .addComponent(jLabel47))
                        .addGap(37, 37, 37)
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextField27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSpinner1))))
                .addGap(18, 18, 18)
                .addComponent(jButton24)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                    .addContainerGap(448, Short.MAX_VALUE)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(490, Short.MAX_VALUE)))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel58)
                    .addComponent(jLabel59))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel44)
                        .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel57)
                        .addComponent(jTextField28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel46)
                            .addComponent(jTextField27, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel47)
                            .addComponent(jTextField24, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton24)))
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(jButton25)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(76, 76, 76))
            .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel21Layout.createSequentialGroup()
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 41, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("Services", jPanel21);

        jPanel2.add(jTabbedPane1, java.awt.BorderLayout.PAGE_START);
        jTabbedPane1.getAccessibleContext().setAccessibleName("View Log");
        jTabbedPane1.getAccessibleContext().setAccessibleDescription("");

        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel43.setText("Account:");
        jPanel2.add(jLabel43, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        jPanel3.setToolTipText("");

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        jTable1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                { new Integer(1), null, null, null, null, null, null, null, null, null},
                { new Integer(2), null, null, null, null, null, null, null, null, null},
                { new Integer(3), null, null, null, null, null, null, null, null, null},
                { new Integer(4), null, null, null, null, null, null, null, null, null},
                { new Integer(5), null, null, null, null, null, null, null, null, null},
                { new Integer(6), null, null, null, null, null, null, null, null, null},
                { new Integer(7), null, null, null, null, null, null, null, null, null},
                { new Integer(8), null, null, null, null, null, null, null, null, null},
                { new Integer(9), null, null, null, null, null, null, null, null, null},
                { new Integer(10), null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "No", "Computer", "IP Address", "Connections", "Mac Address", "Speed", "Total Download", "Download Limit", "Total Time", "Time Left"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTable1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTable1.setDropMode(javax.swing.DropMode.ON);
        jTable1.setFillsViewportHeight(true);
        jTable1.setInheritsPopupMenu(true);
        jTable1.setIntercellSpacing(new java.awt.Dimension(2, 2));
        jTable1.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        jTable1.setMinimumSize(new java.awt.Dimension(0, 0));
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable1MousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
        }

        jLabel1.setForeground(new java.awt.Color(255, 0, 0));
        jLabel1.setText("Not Connected");

        jButton2.setText("Stop");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton1.setText("Start");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jToolBar1.setRollover(true);
        jToolBar1.setEnabled(false);

        jButton4.setText("Reset ");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton4);

        jButton6.setText("Settings");
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton6);

        jButton13.setText("Logout");
        jButton13.setFocusable(false);
        jButton13.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton13.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton13);

        jButton9.setText("view hash");
        jButton9.setFocusable(false);
        jButton9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton9.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton9);

        jToggleButton1.setText("Pause");
        jToggleButton1.setFocusable(false);
        jToggleButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jToggleButton1);

        jToolBar2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToolBar2.setEnabled(false);

        jLabel15.setText("Ticket: ");
        jToolBar2.add(jLabel15);
        jToolBar2.add(filler2);

        jTextField8.setMinimumSize(new java.awt.Dimension(60, 30));
        jTextField8.setPreferredSize(new java.awt.Dimension(60, 30));
        jToolBar2.add(jTextField8);
        jToolBar2.add(jComboBox2);
        jToolBar2.add(filler1);

        jButton10.setText("Activate");
        jButton10.setFocusable(false);
        jButton10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton10.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton10);
        jToolBar2.add(filler5);

        jButton21.setText("Recharge");
        jButton21.setFocusable(false);
        jButton21.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton21.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton21);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addGap(6, 6, 6)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1)
                        .addComponent(jButton2))
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

        jMenu1.setText("File");

        jMenuItem1.setText("Exit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Tools");

        jMenuItem2.setText("Settings");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuItem13.setText("Exempt Selected Computer");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem13);

        jMenuItem6.setText("Reload Computer Name");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem6);
        jMenu2.add(jSeparator3);

        jCheckBoxMenuItem1.setText("Record Bandwidth");
        jCheckBoxMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jCheckBoxMenuItem1);

        jMenuBar1.add(jMenu2);

        jMenu7.setText("View");

        jMenuItem3.setText("Tickets");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem3);

        jMenuItem4.setText("Exempt List");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem4);

        jMenuItem11.setText("Bandwidth");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem11);

        jMenuBar1.add(jMenu7);

        jMenu9.setText("Account");

        jMenuItem5.setText("Report");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem5);

        jMenuItem10.setText("Generate Ticket");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem10);

        jMenuItem14.setText("Add to Stock");
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem14);

        jMenuBar1.add(jMenu9);

        jMenu10.setText("Operator");
        jMenuBar1.add(jMenu10);

        jMenu8.setText("Help");

        jMenuItem9.setText("Content");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem9);

        jMenuItem7.setText("About");
        jMenu8.add(jMenuItem7);

        jMenuBar1.add(jMenu8);

        setJMenuBar(jMenuBar1);

        setSize(new java.awt.Dimension(967, 542));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if(confirm(this,"Stop", "Do you want to stop all connections"))
        disconnect();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        dialogConnect.pack();
        dialogConnect.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        dialogConnect.setLocation(this.getLocation().x, this.getLocation().y);
        jTextField1.requestFocus();
        dialogConnect.setVisible(true);
        jTextField1.grabFocus();
        jTextField1.requestFocus();
        jTextField1.requestFocusInWindow();
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        connect();
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        connect();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTree1ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTree1ValueChanged
        
        
    }//GEN-LAST:event_jTree1ValueChanged

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
           String mac = getMac();
           if(mac!=null){
              Vector v = (Vector) settings.get("Exempt");
              if(v!=null){
                  if(!v.contains(mac))
                      showErr("Can only reset exempted Computers");
                  else
                      if(confirm(this, "Reset", "Reset Total Download"))
                       resetDownload(unique.get(mac));
              }
           }
           
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        saveSettings();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void jTextField7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField7ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        showSettings();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        jFrame1.pack();
        jFrame1.setVisible(true);
        jTextArea3.setText(unique.toString()+settings.get("Exempt"));
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        activate();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        removeExempt();
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
          logout();
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jPanel1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPanel1KeyPressed
    }//GEN-LAST:event_jPanel1KeyPressed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        dialogSettings.setVisible(false);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        saveAdmin();
        loadPrize();
        print("Setting saved successfuly");
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        removeTicket();
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        addTicket();
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jTextField4KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField4KeyTyped
        verify(evt);
    }//GEN-LAST:event_jTextField4KeyTyped

    private void jTextField3KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyTyped
        verify(evt);
    }//GEN-LAST:event_jTextField3KeyTyped

    private void jTextField3InputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_jTextField3InputMethodTextChanged
        evt.consume();
    }//GEN-LAST:event_jTextField3InputMethodTextChanged

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jTextField2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyTyped
        verify(evt);
    }//GEN-LAST:event_jTextField2KeyTyped

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jTextField12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField12ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        searchTicket();
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jTextField12KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField12KeyTyped
        
    }//GEN-LAST:event_jTextField12KeyTyped

    private void jTextField12KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField12KeyReleased
        searchTicket();
    }//GEN-LAST:event_jTextField12KeyReleased

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        String date = jComboBox3.getSelectedItem()+"";
        String date2 = jComboBox8.getSelectedItem()+"";
        jFrameReport.setTitle("Report from "+date+" To "+date2);
        viewReport();
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        viewTickets();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        viewExempt();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        jFrameReport.pack();
        jFrameReport.setVisible(true);
        jComboBox3.setSelectedItem(r.getDate(1));
        jComboBox8.setSelectedItem(r.getDate(1));
        jFrameReport.setTitle("Daily Report: ("+r.getDate(1)+")");
        viewReport();

    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        loadAdmin();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        if(confirm(this, "Exit", "Close Bandwidth Application"))
        System.exit(0);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void dialogConnectFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_dialogConnectFocusGained
        jButton3.grabFocus();
        System.out.println("focus got");
    }//GEN-LAST:event_dialogConnectFocusGained

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        System.out.println("focus got");
        jTextField8.requestFocusInWindow();
    }//GEN-LAST:event_formFocusGained

    private void dialogConnectFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_dialogConnectFocusLost
        System.out.println("focus lost");
    }//GEN-LAST:event_dialogConnectFocusLost

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        pause(false);
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        if(confirm(this,"Reload" ,"Re-Fetch Selected Computer Name?"))
        computerName = getMac();
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        generate();
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jTextField14KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField14KeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField14KeyTyped

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        viewBandWidth();
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        jFrameReport1.pack();
        jFrameReport1.setVisible(true);
        jComboBox5.setSelectedItem(r.getDate(1));
        viewBandWidth();
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jTextField15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField15ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField15ActionPerformed

    private void jTextField15KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField15KeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField15KeyTyped

    private void jTextField16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField16ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField16ActionPerformed

    private void jTextField16InputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_jTextField16InputMethodTextChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField16InputMethodTextChanged

    private void jTextField16KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField16KeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField16KeyTyped

    private void jTextField17KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField17KeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField17KeyTyped

    private void jTextField18KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField18KeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField18KeyTyped

    private void jTabbedPane2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane2StateChanged
        
    }//GEN-LAST:event_jTabbedPane2StateChanged

    private void jTabbedPane2ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jTabbedPane2ComponentShown
    }//GEN-LAST:event_jTabbedPane2ComponentShown

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        setTheme();
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jTabbedPane2PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTabbedPane2PropertyChange

    }//GEN-LAST:event_jTabbedPane2PropertyChange

    private void jTextField19KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField19KeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField19KeyTyped

    private void jTextField20KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField20KeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField20KeyTyped

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MousePressed
     
    }//GEN-LAST:event_jTable1MousePressed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        addExpenses();
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jTextField21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField21ActionPerformed
        addExpenses();
    }//GEN-LAST:event_jTextField21ActionPerformed

    private void jComboBox7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox7ActionPerformed

    }//GEN-LAST:event_jComboBox7ActionPerformed

    private void jComboBox7KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox7KeyPressed
    }//GEN-LAST:event_jComboBox7KeyPressed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        recharge();
    }//GEN-LAST:event_jButton21ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
       add2Prize();
    }//GEN-LAST:event_jButton22ActionPerformed

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
       removePrize();
    }//GEN-LAST:event_jButton23ActionPerformed

    private void jTextField24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField24ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField24ActionPerformed

    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        add2Record();
    }//GEN-LAST:event_jButton24ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        exempt();
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void jComboBox9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox9ActionPerformed

    private void jTextField27KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField27KeyTyped
        
    }//GEN-LAST:event_jTextField27KeyTyped

    private void jTextField27KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField27KeyReleased
        checkKey(evt);// TODO add your handling code here:
    }//GEN-LAST:event_jTextField27KeyReleased

    private void jSpinner1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jSpinner1KeyTyped
        try {
            jSpinner1.commitEdit();
        } catch (ParseException ex) {
            Logger.getLogger(load.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jSpinner1KeyTyped

    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
        dialogConnect1.pack();
        dialogConnect1.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        dialogConnect1.setLocation(this.getLocation().x, this.getLocation().y);
        reloadList();
        dialogConnect1.setVisible(true);
        jTextField23.grabFocus();
        jTextField23.requestFocus();
        jTextField23.requestFocusInWindow();
    }//GEN-LAST:event_jMenuItem14ActionPerformed

    private void jComboBox10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox10ActionPerformed

    private void jTextField23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField23ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField23ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        updateList();
        loadPrize();
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jPanel22KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPanel22KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel22KeyPressed

    private void dialogConnect1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_dialogConnect1FocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_dialogConnect1FocusGained

    private void dialogConnect1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_dialogConnect1FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_dialogConnect1FocusLost

    private void jTextField26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField26ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField26ActionPerformed

    private void jTextField27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField27ActionPerformed
        add2Record();
    }//GEN-LAST:event_jTextField27ActionPerformed

    private void jComboBox11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox11ActionPerformed

    private void jComboBox11KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox11KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox11KeyPressed

    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        addExpenses2();
    }//GEN-LAST:event_jButton25ActionPerformed

    private void jTextField28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField28ActionPerformed
        addExpenses2();
    }//GEN-LAST:event_jTextField28ActionPerformed

    private void jTextField27FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField27FocusGained
        jTextField27.selectAll();
    }//GEN-LAST:event_jTextField27FocusGained

    private void jCheckBoxMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem1ActionPerformed
        recordBandwidth();
    }//GEN-LAST:event_jCheckBoxMenuItem1ActionPerformed

    private void jButton26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton26ActionPerformed
        saveAdmin(false);
        database = new database(databasename,this);
    }//GEN-LAST:event_jButton26ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        Hashtable settings = new Records().getSettings("settings");
        String name = (String) settings.get("Theme");
        if(name == null)
            name = "Nimbus";
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if (name.equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(load.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(load.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(load.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(load.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
                loading l = new loading();
                l.run();
                load ld = new load();
                ld.pack();
                ld.setVisible(true);
                l.setVisible(false);
                ld.reloadTotal();
                ld.setTableSize();
//            }
//        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog dialogConnect;
    private javax.swing.JDialog dialogConnect1;
    private javax.swing.JDialog dialogSettings;
    private javax.swing.JDialog dialogSettings2;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox10;
    private javax.swing.JComboBox jComboBox11;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBox4;
    private javax.swing.JComboBox jComboBox5;
    private javax.swing.JComboBox jComboBox6;
    private javax.swing.JComboBox jComboBox7;
    private javax.swing.JComboBox jComboBox8;
    private javax.swing.JComboBox jComboBox9;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JFrame jFrame2;
    private javax.swing.JFrame jFrame3;
    private javax.swing.JFrame jFrame4;
    private javax.swing.JFrame jFrameReport;
    private javax.swing.JFrame jFrameReport1;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu10;
    private javax.swing.JMenu jMenu11;
    private javax.swing.JMenu jMenu12;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenu jMenu9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuBar jMenuBar3;
    private javax.swing.JMenuBar jMenuBar6;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSpinner jSpinner2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTable jTable5;
    private javax.swing.JTable jTable6;
    private javax.swing.JTable jTable7;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField16;
    private javax.swing.JTextField jTextField17;
    private javax.swing.JTextField jTextField18;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField20;
    private javax.swing.JTextField jTextField21;
    private javax.swing.JTextField jTextField22;
    private javax.swing.JTextField jTextField23;
    private javax.swing.JTextField jTextField24;
    private javax.swing.JTextField jTextField25;
    private javax.swing.JTextField jTextField26;
    private javax.swing.JTextField jTextField27;
    private javax.swing.JTextField jTextField28;
    private javax.swing.JTextField jTextField29;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField30;
    private javax.swing.JTextField jTextField31;
    private javax.swing.JTextField jTextField33;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JToolBar jToolBar5;
    private javax.swing.JToolBar jToolBar6;
    public javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables
    
    public void start(){
        
    }
    
    public DefaultComboBoxModel model(){
        DefaultComboBoxModel m = new DefaultComboBoxModel();
        try {
            hash = MacAddress.getNetworkCard();
            hash.put("Local Host",-2);
            Iterator it = hash.keySet().iterator();
            while(it.hasNext()){
                m.addElement(it.next());
            }
        } catch (SocketException ex) {
            showErr("Cant Connect right now.\n Restart the Application");
        }
        
        return m;
    }
    public void connect(){
        String p = jTextField1.getText();
        try{
            port = Integer.parseInt(p);
            if(port==0) port =80;
            
        }catch(NumberFormatException e){ this.port = 80;}
        
        if(proxy==null) 
            proxy = new cProxy(this);
        
        if(!proxy.isAlive()){
        String a = (String)jComboBox1.getSelectedItem();
            try {
                proxy.sethost(MacAddress.getName(hash.get(a)));
            } catch (SocketException ex) {
                Logger.getLogger(load.class.getName()).log(Level.SEVERE, null, ex);
            }
        proxy.setport(port);
        proxy.stop(false);
        proxy.start();
        jButton1.setEnabled(false);
        jButton2.setEnabled(true);
        if(strtItem != null){ strtItem.setEnabled(false);stopItem.setEnabled(true);}
        dialogConnect.setVisible(false);
        showInf("Connected to port : "+port);
        String macc = "00-00-00-00-00-aa";
            try {
                r.addAccess(MacAddress.getName(hash.get(a)), macc);
            } catch (SocketException ex) {
                Logger.getLogger(load.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else {
            showErr("Connection was already established");
            dialogConnect.setVisible(false);
        }
        

    }
    private void checkKey(java.awt.event.KeyEvent evt){
        char c = evt.getKeyChar();
            int x = getNum(jTextField27.getText());
            int y = getNum(jSpinner1.getValue()+"");
            jTextField24.setText("N "+(x*y));
        if(Character.isDigit(c)){

        }else {
            evt.consume();}
    }
    
    public void disconnect(){
        if(proxy!=null){
            proxy.disconnect();
            if(proxy.isAlive())
                proxy.interrupt();
            processClose();
            showInf("Connection closed");
        }
        proxy = null;
        jButton2.setEnabled(false);
        jButton1.setEnabled(true);
        if(strtItem != null){ strtItem.setEnabled(true);stopItem.setEnabled(false);}
    }
    public void close(Socket server){
        try{
            server.close();
        }catch(IOException ex){}
    }
    public void processClose(){
        Enumeration en = unique.elements();
        while(en.hasMoreElements()){
            Hashtable hashv = (Hashtable) en.nextElement();
            Hashtable soc = (Hashtable) hashv.get("Sockets");
            Enumeration enn = soc.elements();
            while(enn.hasMoreElements()){
                Socket socket = (Socket) enn.nextElement();
                close(socket);
                
            }
        }
    }
    
    public int showErr(String text){
        JOptionPane.showMessageDialog(dialogConnect, text,"Error", JOptionPane.ERROR_MESSAGE);
    return 0;    
    }
    public int showInf(String text){
        JOptionPane.showMessageDialog(dialogConnect, text,"Information", JOptionPane.INFORMATION_MESSAGE);    
    return 0;
    }
    
    public void display(String text){
        jLabel1.setText(text);
    }
    
    public void addColum(String computer,String ip,String connection,String mac,
            String transfer,String download){
       
    }
    
    
    public void updateRow(int row,Hashtable h){
        Enumeration loop = h.keys();
        while(loop.hasMoreElements()){
            String element = (String) loop.nextElement();
            
            int col = columnIndex(element);
            if(col==-1)
                continue;
            if(element.equalsIgnoreCase("Total Download") | element.equalsIgnoreCase("Download Limit"))
                insert(converToMB((int)h.get(element)), row, col);
            else if(element.equalsIgnoreCase("Total Time") | element.equalsIgnoreCase("Time Left")){
                int a = (int)h.get(element);
                if(a>0)
                    insert(r.getTime(a), row, col);
                else if(a==-1)
                    insert("None", row, col);
                else
                    insert(" ", row, col);
            }
            else insert(h.get(element), row, col);
        }
    }
    
    public void update(String mac,Hashtable h){
        int col = columnIndex("Mac Address");
        int rowCount = jTable1.getRowCount();
        int a = -1;
        for(int i = 0; i < rowCount; i++){
            String value = (String) jTable1.getValueAt( i, col);
            if(value==null)continue;
            if(value.equalsIgnoreCase(mac)){
                a = i;break;}
        }
        if(a == -1)
            updateRow(freeRow(), h);
        else updateRow(a, h); 
    }
    
    public int freeRow(){
        int col = columnIndex("Mac Address");
        int rowCount = jTable1.getRowCount();
        int a = rowCount;
        for(int i = 0; i < rowCount; i++){
            String value = (String) jTable1.getValueAt( i, col);
            if(value==null){a=i;break;}
            if(value.length()<4){
                a = i;break;}
        }
        return a;
    }
    
    public void print(String a){
        jTextArea1.append(time()+a);
    }
    
    private String time(){
        Date d = new Date(System.currentTimeMillis());
        String hour = p(d.getHours());
        String min = p(d.getMinutes());
        String sec = p(d.getSeconds());
        return String.format("%s:%s:%s GMT : ", hour,min,sec);
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
    
    public String getToday(){
        return date(0);
    }
    private String p(int a){
        return ((a+"").length()==1)?("0"+a):a+"";
    }
    
    void insert(Object value,int row,int col){
        jTable1.setValueAt(value,row, col);
    }
    
    int columnIndex(String name){
        int b = jTable1.getColumnCount();
        for(int i=0;i<b;i++){
            String c = jTable1.getColumnName(i);
            if(c.equalsIgnoreCase(name))
                return i;
        }
        return -1;
    }
    
    public String getMac(){
        int a = jTable1.getSelectedRow();
        int b = columnIndex("Mac Address");
        if(a==-1)
            return null;
        String c = (String) jTable1.getValueAt(a, b);
        return c;
    }
    
    public void loadThis(){
    }
    
    boolean create = true;

    public int updateTree(){
        String mac = getMac();
        if(mac == null)
            return 0;
        if(this.mac.equalsIgnoreCase(mac))
            create = false;
        else create = true;
        this.mac = mac;
        updateT((Hashtable)unique.get(mac));
        return 1;
    }
    public String mac = "";
    
    
    public synchronized boolean updateT(Hashtable hashv){
     try{   
        DefaultMutableTreeNode head;
        DefaultMutableTreeNode head2;
        javax.swing.tree.DefaultMutableTreeNode root = new javax.swing.tree.DefaultMutableTreeNode(hashv.get("Computer"));
        head = menu("Details");
        root.add(head);
        head = menu("Connections");
        root.add(head);
        
        if(create){
            jTree1.setModel(new DefaultTreeModel(root));
         }
        TreePath path = jTree1.getSelectionPath();
        if(path==null)
            return false;    
        if(path.toString().indexOf("Details")>-1)
            jTextArea2.setText(getDetails(hashv));
        else
            jTextArea2.setText(getConnections(hashv));
     }catch(NullPointerException e){}    
        return true;
    }
    public void verify(KeyEvent e){
        char a = e.getKeyChar();
        if(!Character.isDigit(a))
            e.consume();
    }
    
    public String getDetails(Hashtable hashv){
        StringBuffer buff = new StringBuffer();
        buff.append("Computer Name : "+hashv.get("Computer"));
        buff.append("\nIP Address : "+hashv.get("IP Address"));
        buff.append("\nTotal Downloaded : "+converToMB((int)hashv.get("Total Download")));
        buff.append("\nBandWidth : "+converToMB((int)hashv.get("Download Limit")));
        buff.append("\nDownload Speed : "+hashv.get("Speed"));
        buff.append("\nTotal Upload : "+converToMB(getNum(hashv.get("Total Upload")+"")));
        String ticket = (String) hashv.get("Ticket");
        int td = getNum(hashv.get("Total Download")+"");
        int dl = getNum(hashv.get("Download Limit")+"");
        int mb = dl - td;
        long extra = ((long)dl * (long)percentage) / 100;
        if(ticket!=null){
            if(0 > mb){
               extra = extra + mb;
            }
            buff.append("\n\nTicket : "+ticket);
            int time = (int) hashv.get("Time Left");
            if(time > 0){
                buff.append("\nExtra MB (R): "+converToMB(extra));
            }
            buff.append("\nLast Login : "+hashv.get("Login"));
            buff.append("\nActivated : "+r.stringToDate(hashv.get("Activated")+""));
            buff.append("\nExpires : "+r.stringToDate(hashv.get("Expires")+""));
        }
        return buff.toString();
    }
 
    public String getConnections(Hashtable hashv){
        StringBuffer buff = new StringBuffer();
        try{
        Hashtable hashb = (Hashtable) hashv.get("Total Byte");
        Hashtable hashs = (Hashtable) hashv.get("Total Speed");
        Hashtable hashh = (Hashtable) hashv.get("Host");
        Enumeration enn = hashb.keys();
        int a = 1;
        while(enn.hasMoreElements()){
            String id = (String) enn.nextElement();
            Integer down = (Integer) hashb.get(id);
            String speed = (String) hashs.get(id);
            String host = (String) hashh.get(id);
            if(host==null)
                buff.append(String.format("CONNECTION %d:\nDownloaded = %s\nSpeed = %s\n\n",a,converToMB(down),speed));
            else
                buff.append(String.format("CONNECTION %d:\nDownloaded = %s\nSpeed = %s\nHost = %s\n\n"
                        ,a,converToMB(down),speed,host));
            a++;
        }
        }catch(NullPointerException e){
        System.out.println("NULL POINTER ERROR ="+e.getMessage());}
        return buff.toString();
    }
    public int connections(Hashtable hashv){
        Hashtable h = (Hashtable) hashv.get("Total Byte");
//        System.out.println(h);
        return h.size();
    }
    
    public DefaultMutableTreeNode menu(String name){
        javax.swing.tree.DefaultMutableTreeNode t = new javax.swing.tree.DefaultMutableTreeNode(name);
        return t;
    }
    
    private void loadAdmin(){
        viewTheme();
        dialogSettings.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        dialogSettings.setLocation(this.getLocation().x+40, this.getLocation().y+40);
        jTextField3.setText((maxSpeed/size)+"");
        jTextField2.setText((maxConn)+"");
        jTextField4.setText((limit/(size*size))+"");
        jTextField14.setText(ConnTimeout+"");
        
        jTextField16.setText((LMaxSpeed/size)+"");
        jTextField17.setText((LConnLimit)+"");
        jTextField15.setText((reducedSpeed/(size))+"");
        jTextField18.setText(ReducedConn+"");
        jTextField19.setText(autoLogout+"");
        jTextField20.setText(percentage+"");
        
        jTextField29.setText(servername);
        jTextField30.setText(serverport + "");
        jTextField31.setText(databasename);
        jTextField33.setText(databaseuser);
        jPasswordField1.setText(databasepassword);
        
        dialogSettings.pack();
        updateTicket();
        updatePrizeList();
        dialogSettings.setVisible(true);
    }
    
    private Vector vTickets(){
        Vector a = new Vector();
        Hashtable hd = (Hashtable) settings.get("Laptop");
        if(hd!=null){
            Enumeration enn = hd.keys();
            
            while(enn.hasMoreElements()){
                a.add(enn.nextElement().toString());
            }
        }
        return a;
    }
    
    private void updateTicket(){
        Vector v;
        Hashtable hd = (Hashtable) settings.get("Laptop");
        String name;
        String[] head = {"Name","Time","BandWidth","Expires","Amount"};
        String[][]tail = new String[hd.size()][5];
        if(hd!=null){
            Enumeration enn = hd.keys();
            int i = 0;
            while(enn.hasMoreElements()){
                
                name = enn.nextElement().toString();
                v = (Vector) hd.get(name);
                tail[i][0] = name;
                tail[i][1] = ((int)v.elementAt(0)==-1)?"----":(r.getTime((int)v.elementAt(0))+"");
                tail[i][2] = converToMB((int)v.elementAt(1))+"";
                tail[i][3] = v.elementAt(2)+" day(s)";
                tail[i][4] = "N"+v.elementAt(3)+"";
                i++;
            }
        }
        jTable4.setModel(new DefaultTableModel(tail, head));
        
    }
    
    private void saveAdmin(){
        saveAdmin(true);
    }
    
    private void saveAdmin(boolean close){
        try{
            maxSpeed = getNum(jTextField3.getText()) * size;
            maxConn = getNum(jTextField2.getText());
            limit = getNum(jTextField4.getText()) * size* size;
            ConnTimeout = getNum(jTextField14.getText());
            Vector v = new Vector();
            v.add(maxSpeed);v.add(maxConn);v.add(limit);v.add(ConnTimeout);
            settings.put("content", v);
  
            LMaxSpeed = getNum(jTextField16.getText())* size;
            LConnLimit = getNum(jTextField17.getText());
            reducedSpeed = getNum(jTextField15.getText())* size;
            ReducedConn = getNum(jTextField18.getText());
            autoLogout = getNum(jTextField19.getText());
            percentage = getNum(jTextField20.getText());
            Vector vv = new Vector();
            vv.add(LMaxSpeed);vv.add(LConnLimit);vv.add(reducedSpeed);
            vv.add(ReducedConn);vv.add(autoLogout);vv.add(percentage);
            settings.put("lcontent", vv);
            
            servername = jTextField29.getText();
            serverport = getNum(jTextField30.getText());
            databasename = jTextField31.getText();
            databaseuser = jTextField33.getText();
            databasepassword = jPasswordField1.getText();
            
            Vector vvv = new Vector();
            vvv.add(servername);vvv.add(serverport);vvv.add(databasename);
            vvv.add(databaseuser);vvv.add(databasepassword);
            settings.put("server", vvv);
            
            record.saveSettings(settings,"settings");
            jComboBox2.setModel(new DefaultComboBoxModel(vTickets()));
            showInf("Configuration Saved");
            if (close) dialogSettings.setVisible(false);
            reloadTotal();
        }catch(NumberFormatException ex){ 
            showErr("Error saving.....");
        }
    }
    
    
    public String converToMB(long b){
        double v = b;
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
    public int getHashSpeed(Hashtable hashv){
        return r.getTotalTransfered(hashv);
    }
    
    private void resetDownload(Hashtable hashv){
        if(hashv!=null){
        hashv.put("Total Download", 0);
        print(hashv.get("Computer")+" Download reset to 0");
        }
    }
    
    private Hashtable returnH(String mac,String h){
        Hashtable hash = (Hashtable) unique.get(mac);
        return (Hashtable) hash.get(h);
    }
    
    private Hashtable returnHashv(String mac){
        Hashtable hash = (Hashtable) unique.get(mac);
        return hash;
    }
    
    private int showSettings(){
        String mac = getMac();
        if(mac==null)
            return 1;
        dialogSettings2.pack();
        
        int maxS = (Integer) returnHashv(mac).get("Max Speed");
        int downL = (Integer) returnHashv(mac).get("Download Limit");
        int maxCon = (Integer) returnHashv(mac).get("Connection Limit");
        
        jTextField5.setText((int)(maxS/size)+"");
        jTextField7.setText((int)(downL/(size*size))+"");
        jTextField6.setText(maxCon+"");
        dialogSettings2.setVisible(true);
    return 0;
}
    private int saveSettings(){
        try{
            String mac = getMac();
            if(mac==null){
                showErr("No System Selected");
                return 1;
            }
            int MaxS = Integer.parseInt(jTextField5.getText());
            int downL = Integer.parseInt(jTextField7.getText());
            int MaxCon = Integer.parseInt(jTextField6.getText());
            returnHashv(mac).put("Max Speed", (int)(MaxS*size));
            returnHashv(mac).put("Download Limit", (int)(downL*size*size));
            returnHashv(mac).put("Connection Limit", MaxCon);
            showInf("Saved");
            dialogSettings2.setVisible(false);
        }catch(NumberFormatException e){
            showErr("Invalid Input");
        }catch(NullPointerException ex){
            showErr("Unrecognise Null error=\n"+ex.getMessage());
        }    
       return 0;       
    }
    
    public database database(){
        return database;
    }
    
    public int activate(){
        String ticket = jTextField8.getText();
        if(ticket.length() < 5){
            showErr("Ticket most be greater than 4 characters");
            return 0;
        }
        String name = jComboBox2.getSelectedItem()+"";
        Hashtable hd = (Hashtable) settings.get("Laptop");
        if(hd==null)
            return showErr(name +" is not saved yet");
        Vector v = (Vector) hd.get(name);
        if(v==null)
            return showErr(name +" doesnt exist");
        if(!confirm(this,"Confirm","Activate "+ticket+"?"))
            return 1;
        int time = (int) v.elementAt(0);
        int bandW = (int) v.elementAt(1);
        int day = (int) v.elementAt(2);
        int amount = (int) v.elementAt(3);
        
        if(database.addTicket(ticket, time, bandW,getToday(),date(day),amount)){
           database.add2Record("INCOME",getToday(), getTime(), amount, "ADMIN", "LAPTOP TICKET"); 
           printf("Activate %s %s for N%d",r.getTime(time),ticket,amount);
           showInf(ticket+" activated");
        }else{
            printf("Error Activating...... %s Ticket already exist",ticket);
            showErr(ticket +" already exist");}
            jTextField8.setText("");
        return 1;
    }
    
    public String getTime(){
        return r.getDate(4);
    }
    public void printf(String value,Object... args){
        print(String.format(value, args));
    }
    public void refresh(){
        Enumeration enn = unique.elements();
        while(enn.hasMoreElements()){
            Hashtable hashv = (Hashtable) enn.nextElement();
            try{
                    int t = (int) hashv.get("Time Left");
                    if(t<=0)
                        continue;
                    hashv.put("Time Left", (t-1));
            }catch(NullPointerException e){}
        }
    }
    
    public void save(){
        Enumeration enn = unique.elements();
        while(enn.hasMoreElements()){
            Hashtable hashv = (Hashtable) enn.nextElement();
            String ticket = null;
            try{
                ticket = (String) hashv.get("Ticket");
                if(ticket==null)
                    continue;
                int t = (int) hashv.get("Time Left");
                int mb = (int) hashv.get("Total Download");
                database.updateTicket(ticket, t, mb);
            }catch(NullPointerException e){
                try{
                    hashv.remove("Time Left");
                    hashv.remove(ticket);
                }catch(NullPointerException ee){}
            }
        }
    }
    
    private int exempt(){
        String mac = getMac();
        if(mac==null)
            return 1;
        Hashtable hashv = returnHashv(mac);
        if(mac==null | hashv == null)
            return showErr("Select a System");
        
        int a = JOptionPane.showConfirmDialog(this, "Add this system to exempt list", 
                "EXEMPT", JOptionPane.YES_NO_OPTION);
        
        if(a==JOptionPane.YES_OPTION){
            Vector v = (Vector) settings.get("Exempt");
            if(v==null)
                v = new Vector();
            if(v.contains(mac))
                return showErr("Selected Computer already exist");
            v.add(returnHashv(mac).get("Computer"));
            v.add(returnHashv(mac).get("IP Address"));
            v.add(mac);
            settings.put("Exempt", v);
            record.saveSettings(settings,"settings");
            hashv.put("Connection Limit", load.maxConn);
            hashv.put("Download Limit",load.limit);        
            hashv.put("Max Speed",load.maxSpeed);
            return showInf(returnHashv(mac).get("Computer")+" added");
        }
         return 0;   
    }
    
    private void view(){
        Vector v = (Vector) settings.get("Exempt");
        Vector head = new Vector();
        head.add("Computer");
        head.add("IP Address");
        head.add("Mac Address");
        jTable3 = new JTable(v, head);
        jFrame3.setVisible(true);
    }
    
    private int viewExempt(){
        jFrame3.pack();
        jFrame3.setVisible(true);
        Vector v = (Vector) settings.get("Exempt");
        
        if(v==null)
            return 1;
        int s = v.size()/3;
        int a = 0;
        int count = 0;
        int col = 0;
        String []head = {"Computer","IP Adddress", "Mac Address"};
        String[][] tail = new String[s][3];
        System.out.println("s="+s+" vs="+v.size());
        for(int c = 0;c<s;c++){
            for(int i = 0;i<3;i++){
                try{
                tail[c][i] = v.elementAt(count)+"";
                }catch(ArrayIndexOutOfBoundsException e){
                    System.out.println("out of bounds i"+i+" c="+c+" count="+count +" eer="+e.getMessage());
                    continue;
                    
                }
                count++;
            }
        }
        jTable3.setModel(new DefaultTableModel(tail, head));
        return 0;
    }
    
    int removeExempt(){
        Vector vec = (Vector) settings.get("Exempt");
        if(vec==null)
            return showErr("No settings found");
        int a = jTable3.getSelectedRow();
        if(a==-1)
            return showErr("Select a system");
        int b = JOptionPane.showConfirmDialog(jFrame3, "Remove select system from exempt list?", 
                "REMOVE EXEMPT", JOptionPane.YES_NO_OPTION);
        if(b == JOptionPane.YES_OPTION){
            int value = (a*3);
            for (int i = 0; i < vec.size(); i++) {
                if(i==value){
                    System.out.println(i);
                    vec.remove(value);
                    vec.remove(value);
                    vec.remove(value);
                    break;
                }
            }
            settings.put("Exempt", vec);
            record.saveSettings(settings,"settings");
            viewExempt();
        }
        
        showInf("Delete Successfull");
        
        return 0;
    }
    
    private void viewTickets(){
        loadTicket(null);
        jFrame2.pack();
        jFrame2.setVisible(true);
    }
    
    private void searchTicket(){
        String text = jTextField12.getText();
        loadTicket(text);
    }
    
    private void loadTicket(String a){
     try{
        Vector v = database.listData();
        int colC = 8;
        String[] head = {"Ticket","Total Time","Time Left","Download limit","Downloaded","Last Login","Activated","Expiring Date"};
        int s = v.size()/colC;
        String[][]tail = new String[s][colC];
        int count = 0;
        int d = -1;
        boolean t = false;
        for(int c = 0;c<s;c++){
            if(!t)
            d++;
            for(int i = 0;i<colC;i++){
                try{
                    if(t & i!=0){
                        count++;
                        continue;
                    }
                if(i==0){    
                    String b = v.elementAt(count)+"";
                    if(a!=null)
                    if(b.toLowerCase().indexOf(a.toLowerCase())==-1){
                        t = true;
                        count++;
                        continue;}
                    t = false;
                    tail[d][i] = v.elementAt(count)+"";
                }
                else if(i==1 | i==2)
                    tail[d][i] = r.getTime((int)v.elementAt(count))+"";
                else if(i==3 | i==4)
                    tail[d][i] = converToMB((int)v.elementAt(count))+"";
                else if(i==6 | i==7){
                    String st = v.elementAt(count)+"";
                    tail[d][i] = r.stringToDate(st) +"";
                }else
                    tail[d][i] = v.elementAt(count)+"";
                }catch(ArrayIndexOutOfBoundsException e){
                    continue;
                    
                }
                count++;
            }
            
        }
        jTable2.setModel(new DefaultTableModel(tail, head));
     }catch(NullPointerException e){
         System.out.println("Null Error = "+e.getMessage());
         e.printStackTrace();;
     }
    }
    private int logout(){
        String mac = getMac();
        if(mac==null)
            return 1;
        Vector v = (Vector) settings.get("Exempt");
        if(v!=null)
            if(v.contains(mac))
                return showErr("Cant logout Exempt List");
        if(confirm(this,"Logout" ,"Logout "+returnHashv(mac).get("Computer")))
            logout(mac);
            print("Server Admin logout "+returnHashv(mac).get("Computer") );
            
        return 0;
    }
    
    public void logoutClear(String mac){
        logout(mac);
        unique.remove(mac);
        removeRow(mac);
    }
    public void setTableSize(){
//        jTable1.getColumnModel().getColumn(1).setResizable(true);
        jTable1.getColumnModel().getColumn(0).setMinWidth(20);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(20);
        jTable1.getColumnModel().getColumn(1).setMinWidth(115);
    }   
    public synchronized void logout(String mac){
        Hashtable hashv = returnHashv(mac);
          try{
            Hashtable soc = (Hashtable) hashv.get("Sockets");
            Enumeration enn = soc.elements();
            while(enn.hasMoreElements()){
                Socket socket = (Socket) enn.nextElement();
                close(socket);
                
            }
            hashv.remove("Ticket");
            hashv.put("Time Left",0);
            hashv.put("Total Time",0);
            hashv.put("Download Limit",0);
            int dd = (int) hashv.get("Total Download");
            int uu = (int) hashv.get("Total Upload");
            previousDownload += dd;
            previousUpload +=uu;
            hashv.put("Total Download",0);
            hashv.put("Total Upload",0);
            }catch(NullPointerException e){};
        
    }
    
    public synchronized void updateDownload(int val){
            intDownload +=val;
    }
    
    public synchronized void updateUpload(int val){
            intUpload +=val;
    }
    
    private void removeRow(String mac){
        try{
        int col = columnIndex("Mac Address");
        int rowCount = jTable1.getRowCount();
        int a = -1;
        for(int i = 0; i < rowCount; i++){
            String value = (String) jTable1.getValueAt( i, col);
            if(value==null)continue;
            if(value.equalsIgnoreCase(mac)){
                a = i;break;}
        }
        DefaultTableModel d = (DefaultTableModel) jTable1.getModel();
        d.removeRow(a);
        d.addRow(new Vector());
        }catch(ArrayIndexOutOfBoundsException e){}
        updateNumber();
    }
    
    private void updateNumber(){
        int maxRow = jTable1.getRowCount();
        for(int i=0;i<maxRow;i++){
            jTable1.setValueAt((i+1), i, 0);
        }
    }
    
    private int addTicket(){
        String name = jTextField9.getText();
        if(name.length()==0)
            return showErr("Invalid Ticket");
        int time;
        String t = (String) jComboBox4.getSelectedItem();
        if(t.equalsIgnoreCase("0.5"))
            time = 1800 + 300;
        else if(t.equalsIgnoreCase("Nill"))
            time = -1;
        else time = (getNum(t)*3600) + 600;

        int bandW = getNum(jTextField10.getText());
        if(bandW==0)
            return showErr("No BandWidth Specified");
        bandW = bandW * size * size;
        
        int days = getNum(jTextField11.getText());
        if(days==0)
            return showErr("No Expire day specified");
        
        int amount = getNum(jTextField13.getText());
        if(amount==0)
            return showErr("Amount Field is empty");
        Hashtable hd = (Hashtable) settings.get("Laptop");
        if(hd==null)
            return showErr("Error saving");
        Vector v = new Vector();
        v.add(time);
        v.add(bandW);
        v.add(days);
        v.add(amount);
        hd.put(name, v);
        showInf("Successfully added");
        jTextField9.setText("");jTextField13.setText("");
        updateTicket();
        return 0;
    }
    
    private int add2Prize(){
        prize = database.getList();
        String name = jTextField22.getText();
        if(name.length()==0)
            return showErr("Please specify a Prize Name");
        if(prize.containsKey(name))
            return showErr("Prize Name already exist");
        int quantity = -1;
        boolean stock = jCheckBox2.isSelected();
        if(stock)
            quantity = 0;
        
        int amount = getNum(jTextField25.getText());
        if(amount==0)
            return showErr("Amount Field is empty");
        boolean edit = jCheckBox1.isSelected();
        if(database.add2List(name, quantity, amount, edit)){
            showInf("Successfully added\n"+(stock?"Proceed by stocking it":""));
            jTextField22.setText("");jTextField25.setText("");
        }else showErr("Error occur when saving. Please try again");
        updatePrizeList();
        return 0;
    }
    private void reloadList(){
      prize = database.getList();
      if(prize == null) return;
      jComboBox10.removeAllItems();
      Enumeration en = prize.keys();
      while(en.hasMoreElements()){
          String name = en.nextElement()+"";
          Vector v = (Vector) prize.get(name);
          if(v==null)continue;
          int x = getNum(v.elementAt(1)+"");
          if(x>-1){
              name = name+":("+x+")";
          }else continue;
          jComboBox10.addItem(name);
      }
    }
    
    private int updateList(){
        String name = jComboBox10.getSelectedItem()+"";
        if(name.length()==0)
            return showErr("Select the Name of the Stock");
        int x = name.indexOf(":(");
        if(x>-1) name = name.substring(0, x);

        int quantity = getNum(jSpinner2.getValue()+"");
        int pamount = getNum(jTextField26.getText());
        int amount = getNum(jTextField23.getText());
        String date = r.getDate(6);
        String time = r.getDate(4);
        if(pamount==0 & amount == 0)
            return showErr("The two amount field cant be empty or zero");
        if(quantity == 0 & pamount == 0){
            return showErr("Please specify the stock quantity you have purchase");
        }
        if(confirm(dialogConnect1, "Add to Stock",String.format("Stock %d Numbers of %s "+(pamount>0?"for N"+pamount+" each":"")+"\nCost of purchase = N%d",quantity,name,amount))){
            
            if(database.updateList(name, quantity,pamount)){
                showInf("Successfully added");
                if(amount > 0){
                    database.add2Record("EXPENSES",date, time, amount, "ADMIN", "PURCHASE ("+name+":("+quantity+"))");
                }
                jTextField26.setText("");jTextField23.setText("");
            }else showErr("Error occur when adding stock. Please try again");
            reloadList();
            reloadTotal();
        }else showErr("Error occur when adding stock. Please try again");
       return 1;
    }
    
    private int removePrize(){
        int a = jTable6.getSelectedRow();
        String value = (String) jTable6.getValueAt(a, 0);
        if(value==null)
            return showErr("Select a value");
        if(!confirm(dialogSettings,"Remove", "Delete "+value))
            return 1;
        try{
            if(database.deleteList(value))
                showInf("Successfully deleted");
            else showErr("Error deleting "+value);
            updatePrizeList();
        }catch(NullPointerException e){}
            
       return 0;
    }
    private ChangeListener changeSpinner(){
        ChangeListener ch = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                   int x = getNum(jTextField27.getText());
                   int y = getNum(jSpinner1.getValue()+"");
                   if(y<1){ jSpinner1.setValue(1);return;}
                   jTextField24.setText("N "+(y*x));
            }
        };
        return ch;
    }
    
    private ItemListener changeSelection(){
        ItemListener it = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                String name = e.getItem()+"";
                int x = name.indexOf(":(");
                if(x>-1) name = name.substring(0, x);
                Vector v = (Vector) prize.get(name);
                if(v==null)return;
                int amount = getNum(v.elementAt(0)+"");
                boolean edit = (boolean) v.elementAt(2);
                if(edit){
                    jTextField27.setEnabled(true);
                }else{ jTextField27.setEnabled(false);
}
                jSpinner1.setValue(1);
                jTextField27.setText(amount+"");
                jTextField24.setText("N "+amount);
                
            }
        };
                return it;
    }
    private int add2Record(){
        String name = jComboBox9.getSelectedItem()+"";
        int index = jComboBox9.getSelectedIndex();
        int x = name.indexOf(":(");
        if(x>-1){ name = name.substring(0, x);}
        try {
            jSpinner1.commitEdit();
        } catch (ParseException ex) {
            Logger.getLogger(load.class.getName()).log(Level.SEVERE, null, ex);
        }
        int unit = getNum(jSpinner1.getValue()+"");
        
        int amount = getNum(jTextField27.getText());
        if(amount <= 0 | unit <= 0)
            return showErr("Amount or Stock should be more than zero");
        int total = amount * unit;
        String date = r.getDate(6);
        String time = r.getDate(4);
        int y = -1;
        Vector v = (Vector) prize.get(name);
        if(v!=null){
           y  = getNum(v.elementAt(1)+"");
           if(y>-1){
               if(unit>y)
                   return showErr("Quantity to buy is more than Quantiy in stock.\nPlease re-stock and try again");
           }
        }
        boolean edit = (boolean) v.elementAt(2);
        if(confirm(this, "Add to Record", "Record "+unit+" "+name+"?\nTotal Amount = N"+total+"")){
            if(database.add2Record("INCOME",date, time, total, "ADMIN", name)){
                printf("Sale: %s for N%d",name,total);
                showInf("Recorded");
                reloadTotal();
                if(y>-1)
                    database.updateList(name, -unit,0);
                loadPrize();
                try{jComboBox9.setSelectedIndex(index);}catch(IllegalArgumentException e){}
            }
            else showErr("Error occured when adding record to database.\nPlease try again later");
        }
        
        return 1;
    }
    public void reloadTotal(){
        String d = r.getDate(1);
        String date1 = r.convert2date(d,true);
        String date2 = r.convert2date(d,false);
        Hashtable hashf = this.database.getByDate("INCOME", date1, date2);
        if(hashf==null)return;
        Enumeration ehash = hashf.elements();
        int amount=0,total_in=0,total_ex=0,bal=0;
        while (ehash.hasMoreElements()) {
          Vector v = new Vector();
          v = (Vector)ehash.nextElement();
          try {
                amount = getNum(v.elementAt(3) + "");
                total_in +=amount;
            }catch(NullPointerException e){}
        }
        
        hashf = this.database.getByDate("EXPENSES", date1, date2);
        ehash = hashf.elements();
        while (ehash.hasMoreElements()) {
          Vector v = new Vector();
          v = (Vector)ehash.nextElement();
          try {
                amount = getNum(v.elementAt(3) + "");
                total_ex +=amount;
            }catch(NullPointerException e){}
        }
        bal = total_in - total_ex;
        jLabel43.setText(String.format("[SALES: N%d] |  [EXPENSES: N%d] |  [BALANCE: N%d]    ",total_in,total_ex, bal));
    }
    private void updatePrizeList(){

        try{
            Vector v;
            prize = database.getList();
            System.out.println(prize);
            String name;
            String[] head = {"Name","Prize","Stock","Editable"};
            String[][]tail = new String[prize.size()][4];
            if(prize!=null){
                Enumeration enn = prize.keys();
                int i = 0;
                while(enn.hasMoreElements()){
                    name = enn.nextElement().toString();
                    v = (Vector) prize.get(name);
                    tail[i][0] = name;
                    tail[i][1] = "N"+v.elementAt(0)+"";
                    tail[i][2] = ((int)v.elementAt(1)==-1)?"No":((int)v.elementAt(1)+"");
                    tail[i][3] = ((boolean)v.elementAt(2))?"True":"False";
                    i++;
                }
            }
            jTable6.setModel(new DefaultTableModel(tail, head));
        }catch(Exception ex){
            
        }
    }
    private boolean confirm(Component c,String title,String text){
        return (JOptionPane.showConfirmDialog(c, text, title, JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION);
    }
    private int removeTicket(){
        int a = jTable4.getSelectedRow();
        String value = (String) jTable4.getValueAt(a, 0);
        if(value==null)
            return showErr("Select a value");
        if(!confirm(dialogSettings,"Remove", "Delete "+value))
            return 1;
        try{
            Hashtable hd = (Hashtable) settings.get("Laptop");
            hd.remove(value);
            updateTicket();
        }catch(NullPointerException e){}
            
       return 0;
    }
    private int getNum(String a){
        try{
            return Integer.parseInt(a);
        }catch(NumberFormatException e){}
        return 0;
        
        
    }
    
    private int viewBandWidth(){
        String date = jComboBox5.getSelectedItem()+"";
        String dt = date;
           try{
                Vector v = (Vector) tBandWidth.get(dt);
                if(v==null)
                    return 1;
                int colC = 3;
                String[] head = {"Date","Total Upload","Total Download"};
                int s = v.size()/colC;
                String[][]tail = new String[3][colC];
                tail[0][0] = r.stringToDate(dt);
                int u = (int)v.elementAt(0);
                tail[0][1] = converToMB(u);
                int d = (int)v.elementAt(1);
                tail[0][2] = converToMB(d);
                int count = 0;
                int total = 0;
//                for(int c = 0;c<s;c++){
//                    for(int i = 0;i<colC;i++){
//                        try{
//                            
//                        if(i==0){
//                            String st = v.elementAt(count)+"";
//                            tail[c][i] = r.stringToDate(st);}
//                        else if(i==2){
//                            int am = getNum(v.elementAt(count)+"");
//                            total +=am;
//                            tail[c][i] = "N"+am+"";
//                        }else
//                            tail[c][i] = v.elementAt(count)+"";
//                        
//                        }catch(ArrayIndexOutOfBoundsException e){
//                            continue;
//
//                        }
//                        count++;
//                    }
//
//                }
                tail[2][0] = "TOTAL";
                tail[2][1] = converToMB(u);
                tail[2][2] = converToMB(d);
        jTable7.setModel(new DefaultTableModel(tail, head));
     }catch(NullPointerException e){}
        
           return 0;
    }
    
 private Vector new_v(String a, String b, String c, String d) {
    Vector v = new Vector();
    v.add(a); v.add(b); v.add(c); v.add(d);
    return v;
  }
  private Vector new_v() {
    return new_v("", "", "", "");
}
  
private int viewReport() {
    String date = this.jComboBox3.getSelectedItem() + "";
    String date1 = r.convert2date(date,true);
    String date2 = r.convert2date(this.jComboBox8.getSelectedItem() + "",false);
    String[] head = { "DATE", "USER", "SOURCE", "AMOUNT" };
    String[][] tail = new String[1][head.length];
    DefaultTableModel model = new DefaultTableModel(tail, head);
    this.jTable5.setModel(model);
    int cc = 0;
    try {
      model.addRow(new_v("INCOME", "", "", ""));
      Hashtable hashf = this.database.getByDate("INCOME", date1, date2);
      if (hashf == null)
        return 1;
      int colC = 4;
      int count = -1;
      int total = 0;
      int total_in = 0;
      int x = 0;
      String d = "";
      Enumeration ehash = hashf.elements();
      while (ehash.hasMoreElements()) {
        Vector v = new Vector();
        v = (Vector)ehash.nextElement();
        try {
          String dt = v.elementAt(0) + "";
          String user = v.elementAt(1) + "";
          String source = v.elementAt(2) + "";
          int amount = getNum(v.elementAt(3) + "");
          total_in += amount;
          if (d.length() == 0) {
            d = source;
          } else if (!d.equals(source))
          {
            if (x > 1) {
              model.addRow(new_v("", "", "SUB-TOTAL", total + ""));
            }
            model.addRow(new_v());
            total = 0; d = source;
            x = 0;
          }
          total += amount;
          Vector vec = new Vector();
          vec.add(dt); vec.add(user); vec.add(source); vec.add(Integer.valueOf(amount));
          model.addRow(vec);
          x++;
        } catch (ArrayIndexOutOfBoundsException e) {
          e.printStackTrace();
          System.out.println("error1" + e.getMessage());
        }
      }
      if (x > 1) {
        model.addRow(new_v("", "", "SUB-TOTAL", total + ""));
      }
      model.addRow(new_v("__________", "__________", "___________", "___________"));
      model.addRow(new_v("TOTAL INCOME", "", "", total_in + ""));
      model.addRow(new_v());model.addRow(new_v());
      model.addRow(new_v("EXPENSES", "", "", ""));
      hashf = null;
      hashf = this.database.getByDate("EXPENSES", date1, date2);
      ehash = hashf.elements();
      int total_ex = 0; total = 0; x = 0;
      while (ehash.hasMoreElements()) {
        Vector v = new Vector();
        v = (Vector)ehash.nextElement();
        System.out.println("value2 = " + v);
        try {
          String dt = v.elementAt(0) + "";
          String user = v.elementAt(1) + "";
          String source = v.elementAt(2) + "";
          int amount = getNum(v.elementAt(3) + "");
          total_ex += amount;

          if (d.length() == 0) {
            d = source;
          } else if (!d.equals(source))
          {
            if (x > 1) {
              model.addRow(new_v("", "", "SUB-TOTAL", total + ""));
              model.addRow(new_v());
            }
            total = 0; d = source;
            x = 0;
          }

          total += amount;
          Vector vec = new Vector();
          vec.add(dt); vec.add(user); vec.add(source); vec.add(Integer.valueOf(amount));
          model.addRow(vec);
          cc++; x++;
        } catch (ArrayIndexOutOfBoundsException e) {
        }
      }
      if (x > 1) {
        model.addRow(new_v("", "", "SUB-TOTAL", total + ""));
      }
      model.addRow(new_v("__________", "__________", "___________", "___________"));
      model.addRow(new_v("TOTAL EXPENSES", "", "", total_ex + ""));
      model.addRow(new_v()); model.addRow(new_v("__________", "__________", "___________", "___________"));
      model.addRow(new_v("BALANCE", "", "", total_in - total_ex + ""));
    }
    catch (NullPointerException e) {
      e.printStackTrace();
    }

    return 0;
  }
    
    public synchronized int setValue(String mac, Hashtable hashv,Socket client){
        hashv = unique.get(mac);
        if(hashv!=null)
            return 1;
        hashv = new Hashtable();
        hashv.put("Computer", r.getSocketName(client));
        hashv.put("IP Address", client.getInetAddress().getHostAddress());
        hashv.put("Mac Address", mac);
        hashv.put("Connections", ""+"Idle");
        hashv.put("Connection Limit", 0);
        hashv.put("Total Download",0);
        hashv.put("Total Upload",0);
        hashv.put("Speed","--");
        hashv.put("Max Speed",0);
        hashv.put("Download Limit",0);
        hashv.put("Bonus",0);
        hashv.put("Total Speed", new Hashtable<String,String>());
        hashv.put("Sockets", new Hashtable<String,Socket>());
        hashv.put("Total Byte", new Hashtable<String,Integer>());
        hashv.put("Host", new Hashtable<String,String>());
        load.unique.put(mac, hashv);
        tableTimer table = new tableTimer(hashv, this);
        hashv.put("Thread", table);
        table.start();
        print(r.getSocketName(client)+" Connected");
        return 0;
    }

    public related r(){
        return r;
    }
    
    private Vector getDisplay(){
        Vector v = new Vector();    
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                v.add(info.getName());
            }        
    return v;
    }
    
    private void viewTheme(){
        jComboBox6.setModel(new DefaultComboBoxModel(getDisplay()));
        String name = (String) settings.get("Theme");
        if(name!=null)
            jComboBox6.setSelectedItem(name);
    }
    private void setTheme(){
        String name = jComboBox6.getSelectedItem()+"";
        settings.put("Theme",name);
        showInf("Theme set to "+name+"\nYou most Restart the application to take effect");
        
    }
    private void generate(){
        new ticket(settings, this).setVisible(true);
    }
    private int addExpenses(){
        String date = r.getDate(6);
        String time = r.getDate(4);
        String source = jComboBox7.getSelectedItem()+"";
        int amount = getNum(jTextField21.getText());
        if(source.length()<2)
            return showErr("Invalid Name");
        if(amount<1)
            return showErr("Invalid Amount");
        if(!confirm(jFrameReport, "Update", "Add "+source+" = N"+amount+" to Expenses?"))
            return 0;
       if(database.add2Record("EXPENSES",date, time, amount, "ADMIN", source)){
            jTextField21.setText("");
            printf("Added N%d '%s' to Expenses",amount,source);
            showInf("Expenses:\n"+source+" = N"+amount+" ADDED");
            reloadTotal();
       }else {
           showErr("Error occured when saving to expenditure.....");
           print("Error occured when saving to expenditure.....");
       }
        viewReport();
        return 0;
    }
    private int addExpenses2(){
        String date = r.getDate(6);
        String time = r.getDate(4);
        String source = jComboBox11.getSelectedItem()+"";
        int amount = getNum(jTextField28.getText());
        if(source.length()<2)
            return showErr("Invalid Name");
        if(amount<1)
            return showErr("Invalid Amount");
        if(!confirm(jFrameReport, "Update", "Add "+source+" = N"+amount+" to Expenses?"))
            return 0;
       if(database.add2Record("EXPENSES",date, time, amount, "ADMIN", source)){
            jTextField28.setText("");
            printf("Added N%d '%s' to Expenses",amount,source);
            showInf("Expenses:\n"+source+" = N"+amount+" ADDED");
            reloadTotal();
       }else {
           showErr("Error occured when saving to expenditure.....");
           print("Error occured when saving to expenditure.....");
       }
        viewReport();
        return 0;
    }
    
    private int recharge(){
        String mac = getMac();
        if(mac==null)
            return showErr("Select a System");
        Hashtable hashv = returnHashv(mac);
        if(hashv==null)
            return showErr("Select a System");
        Vector v = (Vector) settings.get("Exempt");
            if(v!=null){
                if(v.contains(mac))
                    return showErr("Cant recharge exempted Computer");
              }
        String sys = hashv.get("Computer")+"";    
        String name = jComboBox2.getSelectedItem()+"";
        if(!confirm(this,"RECHARGE","Recharge "+sys+" with "+name+"?"))
            return 0;
        Hashtable hd = (Hashtable) settings.get("Laptop");
        if(hd==null)
            return showErr(name +" is not saved yet");
        String ticket = (String) hashv.get("Ticket");
        if(ticket==null)
            return showErr(sys+" not log in");
        Vector vec = database.getData(ticket);
        if(vec==null)
            return showErr("Invalid Ticket");
            int ttimef = getNum(vec.elementAt(0)+"");
            int tleftf = getNum(vec.elementAt(1)+"");
            int totalmbf = getNum(vec.elementAt(2)+"");
            //add screen MB
            int expiref = getNum(vec.elementAt(6)+"");
            int amountf = getNum(vec.elementAt(7)+"");
        
        v = (Vector) hd.get(name);
        int time = (int) v.elementAt(0);
        int bandW = (int) v.elementAt(1);
        int day = (int) v.elementAt(2);
        int amount = (int) v.elementAt(3);
        
        if(time == -1 | ttimef == -1){
            if(time != ttimef)
                return showErr("Invalid Ticket Choice:\nTime ticket can only recharge Time"
                        + "\nAnd Likewise (No Time Ticket)");
        }

            int ttime = time == -1? -1:(ttimef+time);
            int tleft = time == -1? -1:(tleftf+time);
            int maxmb = totalmbf + bandW;
            int tamount = amount+amountf;
            int expires = expiref+day;
            int f_time = getNum(hashv.get("Time Left")+"");
            hashv.put("Time Left", tleft);
        if(database.recharge(ticket, ttime, tleft, maxmb, tamount, expires+"")){
           database.add2Record("INCOME",getToday(), getTime(), amount, "ADMIN", "LAPTOP"); 
           hashv.put("Total Time", ttime);
           hashv.put("Time Left", tleft);
           hashv.put("Expires",expires);
           hashv.put("Amount",tamount);
           hashv.put("Connection Limit", load.maxConn);
           hashv.put("Download Limit",maxmb);        
           hashv.put("Max Speed",load.maxSpeed);
           printf("(%s) time was added to %s Computer",r.getTime(time),ticket);
           showInf(ticket+" activated");
        }else{
            hashv.put("Time Left", f_time);
            showErr("Error recharging...");
        }

         return 0;   
    }
    
  MenuItem showItem,aboutItem,strtItem,stopItem,exitItem;  
  CheckboxMenuItem checkPause;
  class trayIcon {
    public void createAndShowGUI() {
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon =
                new TrayIcon(createImage("icon.png", "tray icon"));
        final SystemTray tray = SystemTray.getSystemTray();
         
        // Create a popup menu components
        aboutItem = new MenuItem("About");
        checkPause = new CheckboxMenuItem("Pause");
        strtItem = new MenuItem("Start");
        stopItem = new MenuItem("Stop");
        exitItem = new MenuItem("Exit");
        showItem = new MenuItem("Show"); 
        //Add components to popup menu
        popup.add(showItem);
        popup.add(strtItem);
        popup.add(stopItem);
        popup.add(checkPause);
        popup.addSeparator();
        popup.add(exitItem);
         
        trayIcon.setPopupMenu(popup);
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Bandwidth Manager");
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            return;
        }
         
        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(true);
            }
        });
         
        showItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 setVisible(true);
            }
        });
        strtItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 connect();
            }
        });
        stopItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               int x = JOptionPane.showConfirmDialog(null, "Disconnect all connections?","STOP",JOptionPane.YES_NO_OPTION);
               if(x == JOptionPane.YES_OPTION)
                   disconnect();
            }
        });
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int x = JOptionPane.showConfirmDialog(null, "Exit Bandwidth Application","EXIT",JOptionPane.YES_NO_OPTION);
                 if(x == JOptionPane.YES_OPTION)
                    System.exit(0);
            }
        });         
        checkPause.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                pause(true);
            }
        });
         
        
         
        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MenuItem item = (MenuItem)e.getSource();
                //TrayIcon.MessageType type = null;
                System.out.println(item.getLabel());
                switch (item.getLabel()) {
                    case "Error":
                        //type = TrayIcon.MessageType.ERROR;
                        trayIcon.displayMessage("Sun TrayIcon Demo",
                                "This is an error message", TrayIcon.MessageType.ERROR);
                        break;
                    case "Warning":
                        //type = TrayIcon.MessageType.WARNING;
                        trayIcon.displayMessage("Sun TrayIcon Demo",
                                "This is a warning message", TrayIcon.MessageType.WARNING);
                        break;
                    case "Info":
                        //type = TrayIcon.MessageType.INFO;
                        trayIcon.displayMessage("Sun TrayIcon Demo",
                                "This is an info message", TrayIcon.MessageType.INFO);
                        break;
                    case "None":
                        //type = TrayIcon.MessageType.NONE;
                        trayIcon.displayMessage("Sun TrayIcon Demo",
                                "This is an ordinary message", TrayIcon.MessageType.NONE);
                        break;
                }
            }
        };
         
    }
     
    //Obtain the image URL
    protected Image createImage(String path, String description) {
        URL imageURL = load.class.getResource(path);
         
        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }
}

  public synchronized void macConnect(){
      if(connectedMac > maxConnection){
          try{
              wait();
          }catch(InterruptedException e){
              
          }
      }
      connectedMac++;
  }
  
  public synchronized void macDisconnect(){
      connectedMac--;
      notifyAll();
  }
  
  public void pause(boolean c){
      boolean x = jToggleButton1.isSelected();
      boolean y = checkPause.getState();
      if(!c){
        if(x == true){
            pause = true;
            if(checkPause != null) checkPause.setState(true);
            print("Connection PAUSED");
        }else{
            pause = false;
            if(checkPause != null) checkPause.setState(false);
            print("Connection RESUMED");
        }
      }else{
        if(y == true){
            pause = true;
            jToggleButton1.setSelected(true);
            print("Connection PAUSED");
        }else{
            pause = false;
            jToggleButton1.setSelected(false);
            print("Connection RESUMED");
        }
      }
  }
  private void recordBandwidth(){
    boolean x = jCheckBoxMenuItem1.isSelected();
    if(x){
        recordb = true;
        print("Bandwidth Recording Started");
    }else{
        recordb = false;
        print("Bandwidth Recording Stoped");
    }
  }
}
