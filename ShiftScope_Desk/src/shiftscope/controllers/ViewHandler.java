package shiftscope.controllers;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import shiftscope.views.ShiftScope;

/**
 *
 * @author carlos
 */
public class ViewHandler {
    public static ShiftScope mainScreen;
    
    public static void init(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            mainScreen = new ShiftScope();
            //mainScreen.setVisible(true);  
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ViewHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ViewHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ViewHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(ViewHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void selectFolders() {
        mainScreen.setVisible(true);
        Handlers.addPaths(mainScreen.openFileChooser());
        mainScreen.setVisible(false);
        Handlers.buildLibraryTree();
        Handlers.putFoldersFirst();
        try {
            Handlers.savePathsOnDisk();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ViewHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ViewHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
