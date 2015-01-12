/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shiftscope.views.dialogs;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Carlos
 */
public class MainDialog extends JDialog {
    
    private JPanel dialogPanel;
    private JLabel label;
    private JLabel logoLabel;
    private JButton loginBtn;
    private JButton registerButton;    

    private ActionListener loginActionListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
            openLoginDialog();
        }
    };
    
    private ActionListener registerActionListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            
            dispose();
            openRegistrationDialog();
        }
    };
            
    public MainDialog(Frame owner, boolean modal) {
        super(owner, "ShiftScope", modal);
        createComponents();
        setResizable(false);
        setModalityType(ModalityType.MODELESS);
        setLocationRelativeTo(owner);          
    }
    
    private final void createComponents() {
        dialogPanel = new JPanel();
        label = new JLabel();
        logoLabel = new JLabel(createImageIcon("../images/2_48x48.png", "logo_shift"));
        loginBtn = new JButton("Log In");
        registerButton = new JButton("Register");
        
        dialogPanel.setLayout(null);
        dialogPanel.setBounds(0, 0, 0, 0);
        dialogPanel.setPreferredSize(new Dimension(300, 150));
        
        
        label.setBounds(0, 0, 350, 250);
        label.setIcon(createImageIcon("../images/back-c.png", "background"));
        
        logoLabel.setBounds(135, 10, 49, 49);
        
        
        
        loginBtn.setBounds(110, 70, 100, 25);
        loginBtn.addActionListener(loginActionListener);
        registerButton.setBounds(110, 110, 100, 25);
        registerButton.addActionListener(registerActionListener);
        
        dialogPanel.add(label);
        dialogPanel.add(loginBtn);
        dialogPanel.add(registerButton);
        dialogPanel.add(logoLabel);
        dialogPanel.setComponentZOrder(label, 3);
        dialogPanel.setComponentZOrder(logoLabel, 2);

        this.add(dialogPanel);
        revalidate();
        repaint();
        pack();
    }
    
    private void openLoginDialog() {
        LoginDialog loginDialog = new LoginDialog((Frame)getOwner(), true);
        loginDialog.setVisible(true);
    }
    private void openRegistrationDialog() {
        RegistrationDialog registrationDialog = new RegistrationDialog((Frame)getOwner(), true);
        registrationDialog.setVisible(true);
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
