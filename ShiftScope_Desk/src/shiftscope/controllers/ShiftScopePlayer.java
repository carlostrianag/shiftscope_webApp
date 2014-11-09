package shiftscope.controllers;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import maryb.player.Player;
import maryb.player.PlayerEventListener;
import maryb.player.PlayerState;
import shiftscope.model.LibraryElement;

/**
 *
 * @author Carlos
 */
public class ShiftScopePlayer {

    private static Player player;
    private static ArrayList<LibraryElement> queuePaths;
    private static int currentSongId;
    private static int currentSong;
    private static boolean isPlaylistPlaying;
    
    private static final PlayerEventListener playerListener = new PlayerEventListener() {

        @Override
        public void endOfMedia() {
            currentSong++;
            if(currentSong < getQueuePaths().size() && isIsPlaylistPlaying()){
                LibraryElement l = Handlers.getLibraryElementByAbsolutePath(getQueuePaths().get(currentSong).getAbsolutePath());
                playSong(l.getId(), l.getAbsolutePath());
            }
        }

        @Override
        public void stateChanged() {
            
        }

        @Override
        public void buffer() {
            
        }
    };
    
    
    
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
            try {
                player.stopSync();
            } catch (InterruptedException ex) {
                Logger.getLogger(ShiftScopePlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            if (!player.isEndOfMediaReached()) {
                try {
                    player.stopSync();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ShiftScopePlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        player.setSourceLocation(path);
        player.play();
    }
    
    public static void playSong(int id, String path) {
        if (PlayerState.PLAYING == player.getState()) {
            try {
                player.stopSync();
            } catch (InterruptedException ex) {
                Logger.getLogger(ShiftScopePlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            if (!player.isEndOfMediaReached()) {
                try {
                    player.stopSync();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ShiftScopePlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        currentSongId = id;
        player.setSourceLocation(path);
        player.play();
    }
    
    
        public static void resume() {
        if (PlayerState.PAUSED == player.getState()) {
            try {
                player.playSync();
            } catch (InterruptedException ex) {
                Logger.getLogger(ShiftScopePlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
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
    
    public static boolean isPlaying() {
        return (player.getState() == PlayerState.PLAYING || player.getState() == PlayerState.PAUSED);
    }
    
    public static String getCurrentLocation() {
        return player.getSourceLocation();
    }
    
    public static int getCurrentSongId() {
        return currentSongId;
    }
    
    public static void enqueueSong(LibraryElement q) {
        getQueuePaths().add(q);
    }
    
    public static void playPlaylist() {
        setIsPlaylistPlaying(true);
        currentSong = 0;
        LibraryElement l = Handlers.getLibraryElementByAbsolutePath(getQueuePaths().get(currentSong).getAbsolutePath());
        playSong(l.getId(), l.getAbsolutePath());
    }

    public static void initPlayer() {
        player = new Player();
        player.setListener(playerListener);
        queuePaths = new ArrayList<>();
        currentSong = 0;
        setIsPlaylistPlaying(false);
    }

    /**
     * @return the queuePaths
     */
    public static ArrayList<LibraryElement> getQueuePaths() {
        return queuePaths;
    }

    /**
     * @return the isPlaylistPlaying
     */
    public static boolean isIsPlaylistPlaying() {
        return isPlaylistPlaying;
    }

    /**
     * @param aIsPlaylistPlaying the isPlaylistPlaying to set
     */
    public static void setIsPlaylistPlaying(boolean aIsPlaylistPlaying) {
        isPlaylistPlaying = aIsPlaylistPlaying;
    }
}
