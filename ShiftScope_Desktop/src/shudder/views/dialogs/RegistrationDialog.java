/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shudder.views.dialogs;

import com.ning.http.client.Response;
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
import shudder.controllers.UserCotroller;
import shudder.model.User;

/**
 *
 * @author Carlos
 */
public class RegistrationDialog extends JDialog {
    
    
    private JPanel dialogPanel;
    private JLabel backgroundLabel;
    private JLabel nameLabel;
    private JLabel lastNameLabel;
    private JLabel emailLabel;
    private JLabel passwordLabel;
    private JButton registerBtn;
    private JTextField nameTextField;
    private JTextField lastNameTextField;
    private JTextField emailTextField;
    private JPasswordField passwordTextField;
    
    private ActionListener registerActionListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(nameTextField.getText().equals("") || lastNameTextField.getText().equals("") || emailTextField.getText().equals("") || passwordTextField.getPassword().length == 0){
                JOptionPane.showMessageDialog(rootPane, "All fields are required. Try again!");
            } else {
                User user = new User();
                user.setName(nameTextField.getText());
                user.setLastName(lastNameTextField.getText());
                user.setEmail(emailTextField.getText());
                user.setPassword(new String(passwordTextField.getPassword()));
                Response response = UserCotroller.createUser(user);
                if (response.getStatusCode() == 200){
                    dispose();
                    openLoginDialog();
                } else {

                }
            }            
        }
    };
    
   public RegistrationDialog(Frame owner, boolean modal) {
        super(owner, "ShiftScope - Register", modal);
        createComponents();
        setResizable(false);
        setModalityType(ModalityType.MODELESS);
        setLocationRelativeTo(owner);
    }
    
    private void createComponents() {

        dialogPanel = new JPanel();
        dialogPanel.setLayout(null);
        dialogPanel.setBounds(0, 0, 0, 0);
        dialogPanel.setPreferredSize(new Dimension(450, 250));
        
        backgroundLabel = new JLabel();
        backgroundLabel.setBounds(0, 0, 500, 550);
        backgroundLabel.setIcon(createImageIcon("../images/back-c.png", "background"));
        
        nameLabel = new JLabel("Name");
        nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        nameLabel.setBounds(100, 40, 80, 35);
        
        nameTextField = new JTextField();
        //nameTextField.setText("Carlos");
        nameTextField.setBounds(200, 40, 150, 25);
        
        lastNameLabel = new JLabel("Last Name");
        lastNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        lastNameLabel.setBounds(100, 80, 80, 35);
        
        lastNameTextField = new JTextField();
        //lastNameTextField.setText("Triana");
        lastNameTextField.setBounds(200, 80, 150, 25);
        
        emailLabel = new JLabel("Email");
        emailLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        emailLabel.setBounds(100, 120, 80, 35);
        
        emailTextField = new JTextField();
        //emailTextField.setText("trianag24@gmail.com");
        emailTextField.setBounds(200, 120, 150, 25);
        
        passwordLabel = new JLabel("Password");
        passwordLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        passwordLabel.setBounds(100, 160, 80, 35);
        
        passwordTextField = new JPasswordField();
        //passwordTextField.setText("medellin0707");
        passwordTextField.setBounds(200, 160, 150, 25);
        
        registerBtn = new JButton("Register");
        registerBtn.setBounds(200, 200, 100, 25);
        registerBtn.addActionListener(registerActionListener);

        dialogPanel.add(backgroundLabel);
        dialogPanel.add(nameLabel);
        dialogPanel.add(lastNameLabel);
        dialogPanel.add(emailLabel);
        dialogPanel.add(passwordLabel);
        dialogPanel.add(registerBtn);
        dialogPanel.add(nameTextField);
        dialogPanel.add(lastNameTextField);
        dialogPanel.add(emailTextField);
        dialogPanel.add(passwordTextField);
        
        dialogPanel.setComponentZOrder(backgroundLabel, 9);
        
        this.add(dialogPanel);
        revalidate();
        repaint();
        pack();        
    }
    
    private void openLoginDialog() {
        LoginDialog loginDialog = new LoginDialog((Frame)getOwner(), true);
        loginDialog.setVisible(true);
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
