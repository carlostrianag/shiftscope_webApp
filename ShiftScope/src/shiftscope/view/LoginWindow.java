/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shiftscope.view;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import shiftscope.controller.DeviceController;
import shiftscope.controller.LibraryController;
import shiftscope.controller.UserCotroller;
import shiftscope.criteria.DeviceCriteria;
import shiftscope.criteria.LibraryCriteria;
import shiftscope.main.Main;
import shiftscope.model.Device;
import shiftscope.model.Library;
import shiftscope.model.User;
import shiftscope.netservices.HTTPService;
import shiftscope.util.LoginCredentials;
import shiftscope.util.SessionConstants;

/**
 *
 * @author carlos
 */
public class LoginWindow extends javax.swing.JFrame {

    /**
     * Creates new form LoginWindow
     */
    private Gson JSONParser;

    public LoginWindow() {
        initComponents();
        setSize(500, 200);
        setLocationRelativeTo(null);
    }

    public void verifyDeviceExistence() {
        String uuid;
        String pcName;
        Device device;
        Device createdDevice;
        HttpResponse response;
        Library library;
        Library createdLibrary;
        List<String> lines;
        DeviceCriteria criteria;
        Device returnedDevice;
        Library returnedLibrary;
        LibraryCriteria libraryCriteria;
        File f = new File("secure-key_"+SessionConstants.USER_ID+".shft");
        JSONParser = new Gson();
        if (f.exists()) {
            try {
                lines = Files.readAllLines(Paths.get("secure-key_"+SessionConstants.USER_ID+".shft"));
                uuid = lines.get(0);
                criteria = new DeviceCriteria();
                criteria.setUUID(uuid);
                response = DeviceController.getDeviceByUUID(criteria);
                returnedDevice = JSONParser.fromJson(HTTPService.parseContent(response.getEntity().getContent()), Device.class);
                
                SessionConstants.DEVICE_ID = returnedDevice.getId();
                criteria = new DeviceCriteria();
                criteria.setId(SessionConstants.DEVICE_ID);
                criteria.setOnline(true);
                DeviceController.connectDevice(criteria);                
                libraryCriteria = new LibraryCriteria();
                libraryCriteria.setDevice(SessionConstants.DEVICE_ID);
                response = LibraryController.getLibraryByDeviceId(libraryCriteria);
                returnedLibrary = JSONParser.fromJson(HTTPService.parseContent(response.getEntity().getContent()), Library.class);
                SessionConstants.LIBRARY_ID = returnedLibrary.getId();

            } catch (IOException ex) {
                Logger.getLogger(LoginWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            uuid = UUID.randomUUID().toString();
            try {
                do {
                    pcName = InetAddress.getLocalHost().getHostName();
                    f.createNewFile();
                    PrintWriter writer = new PrintWriter("secure-key_"+SessionConstants.USER_ID+".shft", "UTF-8");
                    writer.println(uuid);
                    writer.close();
                    f.setReadOnly();
                    device = new Device();
                    device.setOnline(true);
                    device.setOwnerUser(SessionConstants.USER_ID);
                    device.setUUID(uuid);
                    device.setName(pcName);
                } while ((response = DeviceController.createDevice(device)).getStatusLine().getStatusCode() != 200);

                createdDevice = JSONParser.fromJson(HTTPService.parseContent(response.getEntity().getContent()), Device.class);
                SessionConstants.DEVICE_ID = createdDevice.getId();
                library = new Library();
                library.setDevice(SessionConstants.DEVICE_ID);
                library.setUser(SessionConstants.USER_ID);
                response = LibraryController.createLibrary(library);
                createdLibrary = JSONParser.fromJson(HTTPService.parseContent(response.getEntity().getContent()), Library.class);
                SessionConstants.LIBRARY_ID = createdLibrary.getId();
            } catch (UnknownHostException ex) {
                Logger.getLogger(LoginWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(LoginWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        loginButton = new javax.swing.JButton();
        emailTextField = new javax.swing.JTextField();
        emailLabel = new javax.swing.JLabel();
        passwordLabel = new javax.swing.JLabel();
        passwordTextField = new javax.swing.JPasswordField();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(null);

        loginButton.setText("Get In!");
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });
        getContentPane().add(loginButton);
        loginButton.setBounds(210, 140, 100, 29);

        emailTextField.setText("trianag24@gmail.com");
        emailTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailTextFieldActionPerformed(evt);
            }
        });
        getContentPane().add(emailTextField);
        emailTextField.setBounds(160, 60, 214, 27);

        emailLabel.setText("Email: ");
        getContentPane().add(emailLabel);
        emailLabel.setBounds(100, 60, 44, 17);

        passwordLabel.setText("Password: ");
        getContentPane().add(passwordLabel);
        passwordLabel.setBounds(70, 100, 75, 17);

        passwordTextField.setText("medellin0707");
        getContentPane().add(passwordTextField);
        passwordTextField.setBounds(160, 90, 214, 27);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/shiftscope/view/back-c.png"))); // NOI18N
        getContentPane().add(jLabel3);
        jLabel3.setBounds(0, 0, 530, 210);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed

        LoginCredentials credentials = new LoginCredentials();
        credentials.setEmail(this.emailTextField.getText());
        credentials.setPassword(new String(this.passwordTextField.getPassword()));
        HttpResponse response = UserCotroller.login(credentials);
        if (response.getStatusLine().getStatusCode() == 200) {
            JSONParser = new Gson();
            try {
                User user = JSONParser.fromJson(HTTPService.parseContent(response.getEntity().getContent()), User.class);
                SessionConstants.USER_ID = user.getId();
                verifyDeviceExistence();
                this.setVisible(false);
                Main.home = new HomePage();
                Main.home.setVisible(true);
            } catch (IOException ex) {
                Logger.getLogger(LoginWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalStateException ex) {
                Logger.getLogger(LoginWindow.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_loginButtonActionPerformed

    private void emailTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_emailTextFieldActionPerformed

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
            java.util.logging.Logger.getLogger(LoginWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel emailLabel;
    private javax.swing.JTextField emailTextField;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JButton loginButton;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JPasswordField passwordTextField;
    // End of variables declaration//GEN-END:variables
}
