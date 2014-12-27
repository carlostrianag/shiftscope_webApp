/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shiftscope.views.dialogs;

import com.google.gson.Gson;
import com.ning.http.client.Response;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import shiftscope.controller.DeviceController;
import shiftscope.controller.LibraryController;
import shiftscope.controller.UserCotroller;
import shiftscope.criteria.DeviceCriteria;
import shiftscope.criteria.LibraryCriteria;
import shiftscope.main.Main;
import shiftscope.model.Device;
import shiftscope.model.Library;
import shiftscope.model.User;
import shiftscope.util.LoginCredentials;
import shiftscope.util.SessionConstants;

/**
 *
 * @author Carlos
 */
public class LoginDialog extends JDialog{

    private final JPanel dialogPanel = new JPanel();
    private final JLabel backgroundLabel = new JLabel();
    private final JLabel emailLabel = new JLabel("Email: ");
    private final JLabel passwordLabel = new JLabel("Password: ");
    private JTextField emailTextField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private final JButton getIntBtn = new JButton("Get In!");
    private Gson JSONParser;
    private ActionListener getInActionListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            LoginCredentials credentials = new LoginCredentials();
            credentials.setEmail(emailTextField.getText());
            credentials.setPassword(new String(passwordField.getPassword()));
            Response response = UserCotroller.login(credentials);
            if (response.getStatusCode() == 200) {
                JSONParser = new Gson();
                try {
                    User user = JSONParser.fromJson(response.getResponseBody(), User.class);
                    SessionConstants.USER_ID = user.getId();
                    verifyDeviceExistence();
                    dispose();
                } catch (IOException ex) {
                    Logger.getLogger(LoginDialog.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalStateException ex) {
                    Logger.getLogger(LoginDialog.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    };
    
    public LoginDialog(Frame owner, boolean modal) {
        super(owner, "ShiftScope - Log In", modal);
        createComponents();
        setResizable(false);
        setModalityType(ModalityType.MODELESS);
        setLocationRelativeTo(owner);
    }
    
    private void createComponents() {


        dialogPanel.setLayout(null);
        dialogPanel.setBounds(0, 0, 0, 0);
        dialogPanel.setPreferredSize(new Dimension(350, 200));
        
        
        backgroundLabel.setBounds(0, 0, 400, 250);
        backgroundLabel.setIcon(createImageIcon("../images/back-c.png", "background"));
        emailLabel.setBounds(40, 50, 70, 35);
        emailLabel.setBackground(Color.red);
        emailLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        passwordLabel.setBounds(40, 100, 70, 35);
        passwordLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        emailTextField.setBounds(120, 50, 150, 25);
        emailTextField.setText("trianag24@gmail.com");
        passwordField.setBounds(120, 100, 150, 25);
        passwordField.setText("medellin0707");
        getIntBtn.setBounds(150, 150, 100, 25);
        getIntBtn.addActionListener(getInActionListener);
        
        
        dialogPanel.add(backgroundLabel);
        dialogPanel.add(emailLabel);
        dialogPanel.add(passwordLabel);
        dialogPanel.add(emailTextField);
        dialogPanel.add(passwordField);
        dialogPanel.add(getIntBtn);

        dialogPanel.setComponentZOrder(backgroundLabel, 5);
        
        this.add(dialogPanel);
        revalidate();
        repaint();
        pack();        
    }
    
    public void verifyDeviceExistence() {
        String uuid;
        String pcName;
        Device device;
        Device createdDevice;
        Response response;
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
                returnedDevice = JSONParser.fromJson(response.getResponseBody(), Device.class);
                
                SessionConstants.DEVICE_ID = returnedDevice.getId();
                criteria = new DeviceCriteria();
                criteria.setId(SessionConstants.DEVICE_ID);
                criteria.setOnline(true);
                DeviceController.connectDevice(criteria);                
                libraryCriteria = new LibraryCriteria();
                libraryCriteria.setDevice(SessionConstants.DEVICE_ID);
                response = LibraryController.getLibraryByDeviceId(libraryCriteria);
                returnedLibrary = JSONParser.fromJson(response.getResponseBody(), Library.class);
                SessionConstants.LIBRARY_ID = returnedLibrary.getId();

            } catch (IOException ex) {
                Logger.getLogger(LoginDialog.class.getName()).log(Level.SEVERE, null, ex);
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
                } while ((response = DeviceController.createDevice(device)).getStatusCode() != 200);

                createdDevice = JSONParser.fromJson(response.getResponseBody(), Device.class);
                SessionConstants.DEVICE_ID = createdDevice.getId();
                library = new Library();
                library.setDevice(SessionConstants.DEVICE_ID);
                library.setUser(SessionConstants.USER_ID);
                response = LibraryController.createLibrary(library);
                createdLibrary = JSONParser.fromJson(response.getResponseBody(), Library.class);
                SessionConstants.LIBRARY_ID = createdLibrary.getId();
            } catch (UnknownHostException ex) {
                Logger.getLogger(LoginDialog.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(LoginDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Main.home.init();
    }    
    
    protected final ImageIcon createImageIcon(String path,
            String description) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }    
    
}
