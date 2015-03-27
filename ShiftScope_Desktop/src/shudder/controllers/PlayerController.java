/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shudder.controllers;

import com.google.gson.Gson;
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

    private int currentSongPosition;
    private int totalSeconds;
    private int currentSecond;
    private int frameLength;
    private boolean playlistPlaying;
    private boolean paused;
    private boolean volumeAdjustedByUser;
    private Float frameRate;
    private String totalTimeString;
    private String elapsedTimeString;
    private ArrayList<Track> queuePaths;
    private Track currentSong;
    private Sync sync;
    private BasicPlayer player;
    private BasicController control;
    private ArrayList<PlayerListener> listeners = new ArrayList<>();
    private FolderListener folderListener = new FolderListener() {
        
        @Override
        public void OnBuildFolderFinished() {
            sync.setNewFolders(true);
            Operation request = new Operation();
            request.setOperationType(OperationType.SYNC);
            request.setUserId(SessionConstants.USER_ID);
            request.setSync(sync);
            TCPController.sendJSRequest(request);
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
    
    private BasicPlayerListener basicPlayerListener = new BasicPlayerListener() {
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
                    TCPController.sendJSRequest(request);
                    paused = false;
                    invokeOnPlayed();
                    break;

                case BasicPlayerEvent.PAUSED:
                    sync.setIsPlaying(false);
                    sync.setIsPaused(true);
                    request.setSync(sync);
                    TCPController.sendJSRequest(request);
                    paused = true;
                    invokeOnPaused();
                    break;

                case BasicPlayerEvent.RESUMED:
                    sync.setIsPlaying(true);
                    sync.setIsPaused(false);
                    request.setSync(sync);
                    TCPController.sendJSRequest(request);
                    paused = false;
                    invokeOnPlayed();
                    break;

                case BasicPlayerEvent.EOM:
                    next();
                    break;

                case BasicPlayerEvent.GAIN:
                    System.out.println(event.getValue());
                    if (volumeAdjustedByUser) {
                        //System.out.println("enviar por sockett");
                    } else {
                        //System.out.println("AJUSTADO DE SOCKET");
                        //invokeOnVolumeChanged((int) (player.getGainValue() * 100));
                    }
                    break;
            }
        }

        @Override
        public void setController(BasicController controller) {
        }
    };

    public void addListener(PlayerListener listener) {
        listeners.add(listener);
    }

    public void removeListener(PlayerListener listener) {
        listeners.remove(listener);
    }

    public void getQueue() {
        invokeOnPlaylistFetched();
    }

    public int getCount() {
        return queuePaths.size();
    }

    private void invokeOnOpened(String totalTime, int totalSeconds) {
        for (PlayerListener listener : listeners) {
            listener.OnOpened(totalTime, totalSeconds);
        }
    }
    
    private void invokeOnPlaylistFetched() {
        for (PlayerListener listener : listeners) {
            listener.OnPlaylistFetched(queuePaths);
        }
    }    
    
    public boolean isPaused() {
        return paused;
    }
    
    public void clearPlaylist() {
        queuePaths.clear();
        sync.setCurrentPlaylist(queuePaths);
        Operation request = new Operation();
        request.setOperationType(OperationType.SYNC);
        request.setUserId(SessionConstants.USER_ID);
        request.setSync(sync);
        TCPController.sendJSRequest(request);
    }

    private void invokeOnProgress(String elapsedTime, int currentSecond) {
        for (PlayerListener listener : listeners) {
            listener.OnProgress(elapsedTime, currentSecond);
        }
    }

    private void invokeOnVolumeChanged(int value) {
        for (PlayerListener listener : listeners) {
            listener.OnVolumeChanged(value);
        }
    }

    private void invokeOnPlaying(String songName, String artistName) {
        for (PlayerListener listener : listeners) {
            listener.OnPlaying(songName, artistName);
        }
    }

    private void invokeOnQueueChanged(Track addedTrack, Track deletedTrack) {
        for (PlayerListener listener : listeners) {
            listener.OnQueueChanged(addedTrack, deletedTrack);
        }
    }
    
    private void invokeOnPlayed() {
        for(PlayerListener listener : listeners) {
            listener.OnPlayed();
        }
    }
    
    private void invokeOnPaused() {
        for(PlayerListener listener : listeners) {
            listener.OnPaused();
        }
    }
    
        
    private void invokeOnStopped() {
        for(PlayerListener listener : listeners) {
            listener.OnStopped();
        }
    }
    

    public void playSong(String track, boolean playedFromPlaylist) {
        System.out.println(track);
        try {
            Track t = new Gson().fromJson(track, Track.class);
            control.open(new File(t.getPath()));
            control.play();
            currentSong = t;
            if (playedFromPlaylist) {
                getPosition(t);
            }
            playlistPlaying = playedFromPlaylist;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }        
    }

    public void playSong(Track t, boolean playedFromPlaylist) {
        try {
            control.open(new File(t.getPath()));
            control.play();
            currentSong = t;
            if (playedFromPlaylist) {
                getPosition(t);
            }
            playlistPlaying = playedFromPlaylist;
        } catch (Exception ex) {
            System.err.print(ex.getMessage());
        }
    }
    public void merge() {
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

    public void getPosition(Track t) {
        for (int i = 0; i < queuePaths.size(); i++) {
            if (t.equals(queuePaths.get(i))) {
                currentSongPosition = i;
                break;
            }
        }
    }

    public void playPlaylist() {
        currentSongPosition = 0;
        currentSong = queuePaths.get(currentSongPosition);
        playSong(currentSong, true);
    }

    public void resume() {
        try {
            control.resume();
        } catch (BasicPlayerException ex) {
            Logger.getLogger(PlayerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void pause() {
        try {
            control.pause();
        } catch (BasicPlayerException ex) {
            Logger.getLogger(PlayerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stop() {
        try {
            control.stop();
            invokeOnStopped();
        } catch (BasicPlayerException ex) {
            Logger.getLogger(PlayerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void next() {
        if (playlistPlaying) {
            if (currentSongPosition < queuePaths.size() - 1) {
                currentSongPosition++;
                currentSong = queuePaths.get(currentSongPosition);
                playSong(currentSong, true);
            }
        }
    }

    public void back() {
        if (playlistPlaying) {
            if (currentSongPosition > 0) {
                currentSongPosition--;
                currentSong = queuePaths.get(currentSongPosition);
                playSong(currentSong, true);
            }
        }
    }

    public void mute() {
//        player.mute();
    }

    public void volumeDown() {
//        player.volumeDown();
    }

    public void volumeUp() {
//        player.volumeUp();
    }

    public void setVolumeFromValue(double value, boolean fromUser) {
        System.out.println(value);
        try {
            volumeAdjustedByUser = fromUser;
            player.setGain(value);
        } catch (BasicPlayerException ex) {
            Logger.getLogger(PlayerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setVolume(float value) {
//        player.setVolumeFromValue(value);
//        volumeSlider.setValue((int) value);
    }

    public boolean isPlaying() {
//        return player.isPlaying();
        return false;
    }

    public void enqueueSong(String song) {
        Track q = new Gson().fromJson(song, Track.class);
        queuePaths.add(q);

        invokeOnQueueChanged(q, null);
        Operation request = new Operation();
        request.setOperationType(OperationType.SYNC);
        request.setUserId(SessionConstants.USER_ID);
        sync.setCurrentPlaylist(queuePaths);
        sync.setAddedTrack(q);
        sync.setDeletedTrack(null);
        request.setSync(sync);
        TCPController.sendJSRequest(request);
    }

    public void dequeueSong(String song) {
        Track t = new Gson().fromJson(song, Track.class);        
        if (currentSong != null && t.equals(currentSong)) {
            next();
        }

        for (Track track : queuePaths) {
            if (track.getId() == t.getId()) {
                queuePaths.remove(track);
                break;
            }
        }
        if (currentSong != null) {
            getPosition(currentSong);
        }
        invokeOnQueueChanged(null, t);
        Operation request = new Operation();
        request.setOperationType(OperationType.SYNC);
        request.setUserId(SessionConstants.USER_ID);
        sync.setCurrentPlaylist(queuePaths);
        sync.setAddedTrack(null);
        sync.setDeletedTrack(t);
        request.setSync(sync);
        TCPController.sendJSRequest(request);
    }

    public Sync getSync() {
        return sync;
    }
    
    public void initPlayer() {
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
