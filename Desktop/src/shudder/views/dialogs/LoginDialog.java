/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shudder.views.dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import shudder.listeners.LoginListener;
import shudder.controllers.UserController;
import shudder.util.LoginCredentials;

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

    private LoginListener loginListener = new LoginListener() {

        @Override
        public void OnSuccessfulLogin() {
            dispose();
        }

        @Override
        public void loading() {
            
        }

        @Override
        public void laoded() {
            
        }

        @Override
        public void OnError(String error) {
            JOptionPane.showMessageDialog(getRootPane(), error, "Shudder - An error has occured.", JOptionPane.ERROR_MESSAGE);
        }
    };
    private ActionListener getInActionListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            LoginCredentials credentials = new LoginCredentials();
            credentials.setEmail(emailTextField.getText());
            credentials.setPassword(new String(passwordField.getPassword()));
            //UserController.login(credentials);

        }
    };
    
    public LoginDialog(Frame owner, boolean modal) {
        super(owner, "Shudder - Log In", modal);
        createComponents();
        setResizable(false);
        setModalityType(ModalityType.MODELESS);
        setLocationRelativeTo(owner);
        //UserController.addListener(loginListener);
    }
    
    private void createComponents() {


        dialogPanel.setLayout(null);
        dialogPanel.setBounds(0, 0, 0, 0);
        dialogPanel.setPreferredSize(new Dimension(350, 200));
        dialogPanel.setBackground(new Color(38,0,38));
        
        backgroundLabel.setBackground(Color.red);
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
