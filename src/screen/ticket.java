/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package screen;

import java.awt.Dialog;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.swing.*;
import javax.swing.plaf.OptionPaneUI;
import javax.swing.text.*;

/**
 *
 * @author SERVER
 */
public class ticket extends javax.swing.JFrame {

    /**
     * Creates new form ticket
     */
    Hashtable settings;
    Vector newT,list;
    load frame;
    boolean save = false;
    public ticket() {
        System.out.println(new Random().nextInt(9999999));
        initComponents();
        load("250MB","N5000","5 hrs 30min","45",6);
    }
    public ticket(Hashtable settings, load frame) {
        this.settings = settings;
        this.frame = frame;
        initComponents();
        jComboBox2.setModel(new DefaultComboBoxModel(vTickets()));
    }
    public void load(String mb,String amount,String time, String expires,int copies){
        StyledDocument document = (StyledDocument) jTextPane1.getStyledDocument();
        Document doc = jTextPane1.getDocument();
             try {
                Style head1 = document.addStyle("head1", null);
                Style head2 = document.addStyle("head2", null);
                Style body = document.addStyle("body", null);
                
                TabSet set = new TabSet(new TabStop[5]);
                TabStop stop = new TabStop(150f);
                TabStop stop1 = new TabStop(300f);
                StyleContext sc = StyleContext.getDefaultStyleContext();
                
                TabSet tabs = new TabSet(new TabStop[] { stop,stop1,stop });
                
                AttributeSet paraSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.TabSet, tabs);
                jTextPane1.setParagraphAttributes(paraSet, false);
                StyleConstants.setUnderline(head1, true);
                
           for(int x=0; x<copies; x++){
               if(x!=0)
                    document.insertString(document.getLength(), "\n\n\n", head1);
               document.insertString(document.getLength(), "\t         Page"+(x+1)+"\n", null);
              for(int y=0; y<6; y++){ 
                if(y!=0)
                    document.insertString(document.getLength(), "\n\n", null);
                StyleConstants.setUnderline(head2, true);
                StyleConstants.setSpaceBelow(head2, 50.5f);
                StyleConstants.setFontSize(head2, 13);
                StyleConstants.setBold(head2, true);
                for(int i=0; i<3; i++){
                    document.insertString(document.getLength(), "WHITE HOUSE CAFE", head2);
                    jTextPane1.setCaretPosition(document.getLength());
                    document.insertString(document.getLength(), i<2?"\t":"", null);
                }
                
                document.insertString(document.getLength(), "\n", null);
                StyleConstants.setFontSize(body, 8);
                for(int i=0; i<3; i++){
                    document.insertString(document.getLength(), "Behind Abdulsalam Garage Tunga Mx", body);
                    document.insertString(document.getLength(), i<2?"\t":"", null);
                }
                
                document.insertString(document.getLength(), "\n", null);
                for(int i=0; i<3; i++){
                    StyleConstants.setFontSize(body, 10);
                    StyleConstants.setBold(body, false);
                    StyleConstants.setUnderline(body, false);
                    document.insertString(document.getLength(), "TIMECODE: ", body);
                    StyleConstants.setFontSize(body, 12);
                    StyleConstants.setBold(body, true);
                    StyleConstants.setUnderline(body, true);
                    document.insertString(document.getLength(), random(), body);
                    document.insertString(document.getLength(), i<2?"\t":"", null);
                }
                
                document.insertString(document.getLength(), "\n", null);
                StyleConstants.setUnderline(body, false);
                StyleConstants.setFontSize(body, 12);
                for(int i=0; i<3; i++){
                    StyleConstants.setFontSize(body, 11);
                    StyleConstants.setBold(body, true);
                    document.insertString(document.getLength(), time, body);
                    StyleConstants.setFontSize(body, 11);
                    document.insertString(document.getLength(), " ("+mb+")", body);
                    StyleConstants.setFontSize(body, 11);
                    StyleConstants.setBold(body, true);
                    document.insertString(document.getLength(), " "+amount, body);
                    
                    document.insertString(document.getLength(), i<2?" \t":"", null);
                }
                
                
                document.insertString(document.getLength(), "\n", null);
                StyleConstants.setUnderline(body, false);
                StyleConstants.setFontSize(body, 12);
                for(int i=0; i<3; i++){
                    StyleConstants.setFontSize(body, 8);
                    StyleConstants.setBold(body, false);
                    document.insertString(document.getLength(), " Expires "+expires+" days after first use", body);
                    document.insertString(document.getLength(), i<2?"\t":"", null);
                }
              }  
           }   
        } catch (BadLocationException e){
            System.err.println("ERROR"+e);
        }
        if(jTextPane1.getText().length()>1)
            jTextPane1.setCaretPosition(0);
    }
    String prefix;
    String name;
    private void generate(){
        save = false;
        newT = new Vector();
        list = frame.database().listData();
        prefix = jTextField1.getText();
        name = jComboBox2.getSelectedItem()+"";
        int copies = jComboBox1.getSelectedIndex() + 1;
        Hashtable hd = (Hashtable) settings.get("Laptop");
        if(hd==null)
            return;
        Vector v = (Vector) hd.get(name);
        if(v==null)
            return;
        int time = (int) v.elementAt(0);
        int bandW = (int) v.elementAt(1);
        int day = (int) v.elementAt(2);
        int amount = (int) v.elementAt(3);
        jTextPane1.setText("");
        load((bandW/(frame.size * frame.size))+"MB", "N"+amount, getTicketTime(time), day+"", copies);
        jButton4.setText("Re-Generate");
    }
    
    private String getTicketTime(int time){
        if(time == -1)
            return "<No Time>";
        if(time < 2200)
            return "35 min";
        if(time < 4300)
            return "1hr 10min";
        int a = time/(60 * 60);
        return a+"hrs 10min";
    }
    
    private String random(){
        boolean check = false;
        do{
            if(prefix == null)
                prefix = "";
            if(prefix.length()>2)
                prefix = prefix.substring(0,2);
            Random r = new Random();
            int a = r.nextInt(9999999);
            prefix = prefix+a;
            if(prefix.length()>7)
                prefix = prefix.substring(0,6);
            if(list==null)
                list=new Vector();
            if(list.contains(prefix))
                check = true;
           }while(check);
        if(newT==null)
            newT=new Vector();
        newT.add(prefix);
        return prefix;
    }
    
    private void activate(){
        Hashtable hd = (Hashtable) settings.get("Laptop");
        if(hd==null)
            return;
        Vector v = (Vector) hd.get(name);
        if(v==null)
            return;
        int time = (int) v.elementAt(0);
        int bandW = (int) v.elementAt(1);
        int day = (int) v.elementAt(2);
        int amount = (int) v.elementAt(3);
        String ticket;
        for(int i=0; i<newT.size();i++){
            ticket = newT.elementAt(i)+"";
            if(frame.database().addTicket(ticket, time, bandW,"0000/00/00 00:00:00",int2ExpDate(day),amount));
        }
        return;
    }
    
    public String int2ExpDate(int x){
        String y = x+"";
        y = y.length()==4?y:y.length()==3?"0"+y:y.length()==2?"00"+y:"000"+y;
        return y+"/00/00 00:00:00";
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
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jPanel3 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Generate Ticket");
        setResizable(false);

        jLabel1.setText("Ticket Type:");

        jTextField1.setText("LP");

        jLabel2.setText("Prefix:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6" }));

        jLabel3.setText("Page:");

        jButton4.setText("Generate");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton4)
                .addContainerGap(105, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTextPane1.setEditable(false);
        jTextPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane1.setViewportView(jTextPane1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jButton3.setText("Print");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton2.setText("Print & Save");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton1.setText("Back");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(67, 67, 67)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 11, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton1)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(406, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 406, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(42, 42, 42)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(48, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        save();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        print();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        jTextPane1.setText("\n\tGenerating, Please Wait.......");
        generate();
    }//GEN-LAST:event_jButton4ActionPerformed
    public void save(){
        if(jTextPane1.getText().length()<10)
            return;
        if(print()){
            if(!save){
                activate();
                save = true;
                JOptionPane.showMessageDialog(this, "Ticket saved to database");}
        }else{
            JOptionPane.showMessageDialog(this, "Cancel!! Not Save");
        }
    }
    public boolean print(){
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            PageFormat pf = job.defaultPage();
         
            Paper pp = pf.getPaper();
        double margin = 80;
            pp.setImageableArea(margin, margin, pp.getWidth() - margin * 2, pp.getHeight()
        -    margin * 2);
            pf.setPaper(pp);    
            Printable print = jTextPane1.getPrintable(null, null);
            pp.setSize(1, 2);
            job.setPrintable(print,pf);
            if(job.printDialog()){
                job.print();
                return true;
            }
        } catch (PrinterException ex) {}
        return false;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ticket.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ticket.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ticket.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ticket.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                System.out.println(new ticket().int2ExpDate(95235));
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables
}
