/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shiftscope.main;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import shiftscope.view.HomePage;
import shiftscope.view.ShiftScope;

/**
 *
 * @author carlos
 */
public class Main {
    public static HomePage home;
    public static void main(String[] args) {
        try {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            // handle exception
        } catch (ClassNotFoundException e) {
            // handle exception
        } catch (InstantiationException e) {
            // handle exception
        } catch (IllegalAccessException e) {
            // handle exceptio

        }
        ShiftScope mainScreen = new ShiftScope();
        mainScreen.setVisible(true);
    }
}
