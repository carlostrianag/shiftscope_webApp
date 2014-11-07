package shiftscope.controllers;

import java.util.logging.Level;
import java.util.logging.Logger;
import maryb.player.Player;
import maryb.player.PlayerState;


/**
 *
 * @author Carlos
 */
public class ShiftScopePlayer {

    private static Player player;


    public static void play() {
        if (PlayerState.PAUSED == player.getState()) {
            try {
                player.playSync();
            } catch (InterruptedException ex) {
                Logger.getLogger(ShiftScopePlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void play(String path) {
        if (PlayerState.PLAYING == player.getState()) {
        } else {
            if (!player.isEndOfMediaReached()) {
                try {
                    player.stopSync();
                    
                } catch (InterruptedException ex) {
                    Logger.getLogger(ShiftScopePlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            player.setSourceLocation(path);
            player.play();
        }
    }
    

    public static void pause() {
        if (PlayerState.PLAYING == player.getState()) {
            try {
                player.pauseSync();
            } catch (InterruptedException ex) {
                Logger.getLogger(ShiftScopePlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void mute(){
        float volume = player.getCurrentVolume();
        if (volume != 0) {
            try {
                player.pauseSync();
                player.setCurrentVolume(0);
                player.playSync();
            } catch (InterruptedException ex) {
                Logger.getLogger(ShiftScopePlayer.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            try {
                player.pauseSync();
                player.setCurrentVolume(0.5f);
                player.playSync();
            } catch (InterruptedException ex) {
                Logger.getLogger(ShiftScopePlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void initPlayer() {
        player = new Player();
    }
}
