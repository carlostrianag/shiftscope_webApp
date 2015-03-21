/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shudder.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;
import shudder.listeners.FolderListener;
import shudder.listeners.PlayerListener;
import shudder.model.Track;
import shudder.util.Operation;
import shudder.util.OperationType;
import shudder.util.SessionConstants;
import shudder.util.Sync;

/**
 *
 * @author Carlos
 */
public class PlayerController {

    private static int currentSongPosition;
    private static int totalSeconds;
    private static int currentSecond;
    private static int frameLength;
    private static boolean playlistPlaying;
    private static boolean paused;
    private static boolean volumeAdjustedByUser;
    private static Float frameRate;
    private static String totalTimeString;
    private static String elapsedTimeString;
    private static ArrayList<Track> queuePaths;
    private static Track currentSong;
    private static Sync sync;
    private static BasicPlayer player;
    private static BasicController control;
    private static ArrayList<PlayerListener> listeners = new ArrayList<>();
    private static FolderListener folderListener = new FolderListener() {
        
        @Override
        public void OnBuildFolderFinished() {
            sync.setNewFolders(true);
            Operation request = new Operation();
            request.setOperationType(OperationType.SYNC);
            request.setUserId(SessionConstants.USER_ID);
            request.setSync(sync);
            TCPController.sendRequest(request);
            sync.setNewFolders(false);
        }
        
        
        @Override
        public void OnProgressUpdated(int progress) {
            
        }

        @Override
        public void OnFilesScanned(int filesCount) {
            
        }

        @Override
        public void OnError(String error) {
            
        }
    };
    
    private static BasicPlayerListener basicPlayerListener = new BasicPlayerListener() {
        @Override
        public void opened(Object o, Map map) {
            Long duration = (Long) map.get("duration");
            int mili = (int) (duration / 1000);
            int sec = (int) (mili / 1000) % 60;
            int min = (int) (mili / 1000) / 60;
            totalTimeString = min + ":" + String.format("%02d", sec);
            totalSeconds = (Integer.parseInt(totalTimeString.split(":")[0]) * 60) + Integer.parseInt(totalTimeString.split(":")[1]);
            frameRate = (Float) map.get("mp3.framerate.fps");
            frameLength = (int) map.get("mp3.framesize.bytes");
            invokeOnOpened(totalTimeString, totalSeconds);
        }

        @Override
        public void progress(int i, long l, byte[] bytes, Map map) {
            Long duration1 = (Long) map.get("mp3.position.microseconds");
            int mili = (int) (duration1 / 1000);
            int sec = (int) (mili / 1000) % 60;
            int min = (int) (mili / 1000) / 60;
            elapsedTimeString = min + ":" + String.format("%02d", sec);
            currentSecond = (Integer.parseInt(elapsedTimeString.split(":")[0]) * 60) + Integer.parseInt(elapsedTimeString.split(":")[1]);
            invokeOnProgress(elapsedTimeString, currentSecond);
        }

        @Override
        public void stateUpdated(BasicPlayerEvent event) {
            //display("stateUpdated : " + event.toString());
            Operation request = new Operation();
            request.setOperationType(OperationType.SYNC);
            request.setUserId(SessionConstants.USER_ID);
            switch (event.getCode()) {

                case BasicPlayerEvent.PLAYING:
                    invokeOnPlaying(currentSong.getTitle(), currentSong.getArtist());
                    sync.setCurrentSongId(currentSong.getId());
                    sync.setCurrentSongName(currentSong.getTitle());
                    sync.setCurrentSongArtist(currentSong.getArtist());
                    sync.setCurrentSongDuration(currentSong.getDuration());
                    sync.setCurrentVolume((int) player.getGainValue());
                    sync.setIsPlaying(true);
                    sync.setIsPaused(false);
                    request.setSync(sync);
                    TCPController.sendRequest(request);
                    paused = false;
                    break;

                case BasicPlayerEvent.PAUSED:
                    sync.setIsPlaying(false);
                    sync.setIsPaused(true);
                    request.setSync(sync);
                    TCPController.sendRequest(request);
                    paused = true;
                    break;

                case BasicPlayerEvent.RESUMED:
                    sync.setIsPlaying(true);
                    sync.setIsPaused(false);
                    request.setSync(sync);
                    TCPController.sendRequest(request);
                    paused = false;
                    break;

                case BasicPlayerEvent.EOM:
                    next();
                    break;

                case BasicPlayerEvent.GAIN:
                    if (volumeAdjustedByUser) {
                        System.out.println("enviar por sockett");
                    } else {
                        System.out.println("AJUSTADO DE SOCKET");
                        invokeOnVolumeChanged((int) (player.getGainValue() * 100));
                    }
                    break;
            }
        }

        @Override
        public void setController(BasicController controller) {
        }
    };

    public static void addListener(PlayerListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(PlayerListener listener) {
        listeners.remove(listener);
    }

    public static ArrayList<Track> getQueue() {
        return queuePaths;
    }

    public static int getCount() {
        return queuePaths.size();
    }

    private static void invokeOnOpened(String totalTime, int totalSeconds) {
        for (PlayerListener listener : listeners) {
            listener.OnOpened(totalTime, totalSeconds);
        }
    }
    
    public static boolean isPaused() {
        return paused;
    }
    
    public static void clearPlaylist() {
        queuePaths.clear();
        sync.setCurrentPlaylist(queuePaths);
        Operation request = new Operation();
        request.setOperationType(OperationType.SYNC);
        request.setUserId(SessionConstants.USER_ID);
        request.setSync(sync);
        TCPController.sendRequest(request);
    }

    private static void invokeOnProgress(String elapsedTime, int currentSecond) {
        for (PlayerListener listener : listeners) {
            listener.OnProgress(elapsedTime, currentSecond);
        }
    }

    private static void invokeOnVolumeChanged(int value) {
        for (PlayerListener listener : listeners) {
            listener.OnVolumeChanged(value);
        }
    }

    private static void invokeOnPlaying(String songName, String artistName) {
        for (PlayerListener listener : listeners) {
            listener.OnPlaying(songName, artistName);
        }
    }

    private static void invokeOnQueueChanged() {
        for (PlayerListener listener : listeners) {
            listener.OnQueueChanged();
        }
    }

    public static void playSong(Track t, boolean playedFromPlaylist) {
        try {
            control.open(new File(t.getPath()));
            control.play();
            currentSong = t;
            if (playedFromPlaylist) {
                getPosition(t);
            }
            playlistPlaying = playedFromPlaylist;
        } catch (BasicPlayerException ex) {
            Logger.getLogger(PlayerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void merge() {
//        Track t;
//        if (playlistPlaying) {
//            if (currentSongPosition < queuePaths.size() - 1) {
//                currentSongPosition++;
//                currentSong = queuePaths.get(currentSongPosition);
//                t = currentSong;
//                player.loadSong(t.getPath());
//                sync.setCurrentSongId(t.getId());
//                sync.setCurrentSongName(t.getTitle());
//                sync.setCurrentSongArtist(t.getArtist());
//                sync.setCurrentSongDuration(t.getDuration());
//                sync.setIsPlaying(true);
//                sync.setIsPaused(false);
//
//                currentSongLabel.setText(t.getTitle() + " - " + t.getArtist());
//
//                if (timeCounter != null) {
//                    timeCounter.cancel(true);
//                }
//
//                player.determineLine();
//                timeCounter = new TimeCounter();
//                timeCounter.execute();
//                Operation request = new Operation();
//                request.setOperationType(OperationType.SYNC);
//                request.setUserId(SessionConstants.USER_ID);
//                request.setSync(sync);
//
//                webSocket.sendRequest(request);
//                playlistPlaying = true;
//            }
//        }

    }

    public static void getPosition(Track t) {
        for (int i = 0; i < queuePaths.size(); i++) {
            if (t.equals(queuePaths.get(i))) {
                currentSongPosition = i;
                break;
            }
        }
    }

    public static void playPlaylist() {
        currentSongPosition = 0;
        currentSong = queuePaths.get(currentSongPosition);
        playSong(currentSong, true);
    }

    public static void resume() {
        try {
            control.resume();
        } catch (BasicPlayerException ex) {
            Logger.getLogger(PlayerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void pause() {
        try {
            control.pause();
        } catch (BasicPlayerException ex) {
            Logger.getLogger(PlayerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void stop() {
        try {
            control.stop();
        } catch (BasicPlayerException ex) {
            Logger.getLogger(PlayerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void next() {
        if (playlistPlaying) {
            if (currentSongPosition < queuePaths.size() - 1) {
                currentSongPosition++;
                currentSong = queuePaths.get(currentSongPosition);
                playSong(currentSong, true);
            }
        }
    }

    public static void back() {
        if (playlistPlaying) {
            if (currentSongPosition > 0) {
                currentSongPosition--;
                currentSong = queuePaths.get(currentSongPosition);
                playSong(currentSong, true);
            }
        }
    }

    public static void mute() {
//        player.mute();
    }

    public static void volumeDown() {
//        player.volumeDown();
    }

    public static void volumeUp() {
//        player.volumeUp();
    }

    public static void setVolumeFromValue(double value, boolean fromUser) {
        try {
            volumeAdjustedByUser = fromUser;
            player.setGain(value);
        } catch (BasicPlayerException ex) {
            Logger.getLogger(PlayerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setVolume(float value) {
//        player.setVolumeFromValue(value);
//        volumeSlider.setValue((int) value);
    }

    public static boolean isPlaying() {
//        return player.isPlaying();
        return false;
    }

    public static void enqueueSong(Track q) {
        queuePaths.add(q);

        invokeOnQueueChanged();
        Operation request = new Operation();
        request.setOperationType(OperationType.SYNC);
        request.setUserId(SessionConstants.USER_ID);
        sync.setCurrentPlaylist(queuePaths);
        request.setSync(sync);
        TCPController.sendRequest(request);
    }

    public static void dequeueSong(Track t) {
        if (t.equals(currentSong)) {
            next();
        }

        for (Track track : queuePaths) {
            if (track.getId() == t.getId()) {
                queuePaths.remove(track);
                break;
            }
        }
        getPosition(currentSong);
        invokeOnQueueChanged();
        Operation request = new Operation();
        request.setOperationType(OperationType.SYNC);
        request.setUserId(SessionConstants.USER_ID);
        sync.setCurrentPlaylist(queuePaths);
        request.setSync(sync);
        TCPController.sendRequest(request);
    }

    public static Sync getSync() {
        return sync;
    }
    
    public static void initPlayer() {
        player = new BasicPlayer();
        player.addBasicPlayerListener(basicPlayerListener);
        control = (BasicController) player;
        try {
            control.setGain(1.0);
        } catch (BasicPlayerException ex) {
            Logger.getLogger(PlayerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        queuePaths = new ArrayList<>();
        currentSong = null;
        currentSongPosition = 0;
        playlistPlaying = false;
        sync = new Sync();
    }

}
