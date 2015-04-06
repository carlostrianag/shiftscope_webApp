/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shudder.controllers;

import com.google.gson.Gson;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
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
    private Float frameRate;
    private String totalTimeString;
    private String elapsedTimeString;
    private ArrayList<Track> queuePaths;
    public Track currentSong;
    private float volume;
    private MediaPlayer player;
    private Duration duration;
    private Runnable onReady = new Runnable() {

        @Override
        public void run() {
            duration = player.getMedia().getDuration();
            invokeOnOpened(formatTime(duration, duration), (int)duration.toSeconds());
            
        }
    };
    private Runnable onPlaying = new Runnable() {

        @Override
        public void run() {
            Operation request = new Operation();
            request.setOperationType(OperationType.SYNC);
            request.setUserId(SessionConstants.USER_ID);
            invokeOnPlaying(currentSong.getTitle(), currentSong.getArtist());
            SessionConstants.sync.setCurrentSongId(currentSong.getId());
            SessionConstants.sync.setCurrentSongName(currentSong.getTitle());
            SessionConstants.sync.setCurrentSongArtist(currentSong.getArtist());
            SessionConstants.sync.setCurrentSongDuration(currentSong.getDuration());
            SessionConstants.sync.setCurrentVolume((int) player.getVolume());
            SessionConstants.sync.setIsPlaying(true);
            SessionConstants.sync.setIsPaused(false);
            request.setSync(SessionConstants.sync);
            TCPController.sendJSRequest(request);      
        }
    };
    private Runnable onPaused = new Runnable() {

        @Override
        public void run() {
            Operation request = new Operation();
            request.setOperationType(OperationType.SYNC);
            request.setUserId(SessionConstants.USER_ID);            
            SessionConstants.sync.setIsPlaying(false);
            SessionConstants.sync.setIsPaused(true);
            request.setSync(SessionConstants.sync);
            TCPController.sendJSRequest(request);
            paused = true;
            invokeOnPaused();            
        }
    };
    private InvalidationListener valueListener = new InvalidationListener() {
            public void invalidated(Observable ov) {
                invokeOnProgress(formatTime(player.getCurrentTime(), duration), (int)player.getCurrentTime().toSeconds());
            }  
    } ;
    private ArrayList<PlayerListener> listeners = new ArrayList<>();
    
    public void initPlayer() {
        queuePaths = new ArrayList<>();
        currentSong = null;
        currentSongPosition = 0;
        playlistPlaying = false;
        SessionConstants.sync = new Sync();
        volume = 1.0f;
        SessionConstants.sync.setCurrentVolume(volume);
    }    
    
    

//    public PlayerController() {
//        this.basicPlayerListener = new BasicPlayerListener() {
//            @Override
//            public void opened(Object o, Map map) {
//                Long duration = (Long) map.get("duration");
//                int mili = (int) (duration / 1000);
//                int sec = (int) (mili / 1000) % 60;
//                int min = (int) (mili / 1000) / 60;
//                totalTimeString = min + ":" + String.format("%02d", sec);
//                totalSeconds = (Integer.parseInt(totalTimeString.split(":")[0]) * 60) + Integer.parseInt(totalTimeString.split(":")[1]);
//                frameRate = (Float) map.get("mp3.framerate.fps");
//                frameLength = (int) map.get("mp3.framesize.bytes");
//                invokeOnOpened(totalTimeString, totalSeconds);
//            }
//            
//            @Override
//            public void progress(int i, long l, byte[] bytes, Map map) {
//                Long duration1 = (Long) map.get("mp3.position.microseconds");
//                int mili = (int) (duration1 / 1000);
//                int sec = (int) (mili / 1000) % 60;
//                int min = (int) (mili / 1000) / 60;
//                elapsedTimeString = min + ":" + String.format("%02d", sec);
//                currentSecond = (Integer.parseInt(elapsedTimeString.split(":")[0]) * 60) + Integer.parseInt(elapsedTimeString.split(":")[1]);
//                invokeOnProgress(elapsedTimeString, currentSecond);
//            }
//            
//            @Override
//            public void stateUpdated(BasicPlayerEvent event) {
//                //display("stateUpdated : " + event.toString());
//                Operation request = new Operation();
//                request.setOperationType(OperationType.SYNC);
//                request.setUserId(SessionConstants.USER_ID);
//                switch (event.getCode()) {
//                    
//                    case BasicPlayerEvent.PLAYING:
//                        invokeOnPlaying(currentSong.getTitle(), currentSong.getArtist());
//                        SessionConstants.sync.setCurrentSongId(currentSong.getId());
//                        SessionConstants.sync.setCurrentSongName(currentSong.getTitle());
//                        SessionConstants.sync.setCurrentSongArtist(currentSong.getArtist());
//                        SessionConstants.sync.setCurrentSongDuration(currentSong.getDuration());
//                        SessionConstants.sync.setCurrentVolume((int) player.getVolume());
//                        SessionConstants.sync.setIsPlaying(true);
//                        SessionConstants.sync.setIsPaused(false);
//                        request.setSync(SessionConstants.sync);
//                        TCPController.sendJSRequest(request);
//                        paused = false;
//                        invokeOnPlayed();
//                        break;
//                        
//                    case BasicPlayerEvent.PAUSED:
//                        SessionConstants.sync.setIsPlaying(false);
//                        SessionConstants.sync.setIsPaused(true);
//                        request.setSync(SessionConstants.sync);
//                        TCPController.sendJSRequest(request);
//                        paused = true;
//                        invokeOnPaused();
//                        break;
//                        
//                    case BasicPlayerEvent.RESUMED:
//                        SessionConstants.sync.setIsPlaying(true);
//                        SessionConstants.sync.setIsPaused(false);
//                        request.setSync(SessionConstants.sync);
//                        TCPController.sendJSRequest(request);
//                        paused = false;
//                        invokeOnPlayed();
//                        break;
//                        
//                    case BasicPlayerEvent.EOM:
//                        next();
//                        break;
//                        
//                    case BasicPlayerEvent.GAIN:
//                        SessionConstants.sync.setCurrentVolume(volume);
//                        if (SessionConstants.VOLUME_FROM_USER) {
//                            Operation volumeRequest = new Operation();
//                            volumeRequest.setOperationType(OperationType.SET_VOLUME);
//                            volumeRequest.setUserId(SessionConstants.USER_ID);
//                            volumeRequest.setSync(SessionConstants.sync);
//                            volumeRequest.setValue(volume);
//                            TCPController.sendJSRequest(volumeRequest);
//                        }
//                        break;
//                }
//            }
//            
//            @Override
//            public void setController(BasicController controller) {
//            }
//        };
//    }

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
        SessionConstants.sync.setCurrentPlaylist(queuePaths);
        Operation request = new Operation();
        request.setOperationType(OperationType.SYNC);
        request.setUserId(SessionConstants.USER_ID);
        request.setSync(SessionConstants.sync);
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
    

    public void play(String track, boolean playedFromPlaylist) {
        try {
            Track t = new Gson().fromJson(track, Track.class);
            currentSong = t;
            Path pathToFile = Paths.get(t.getPath());
            Media media = new Media(pathToFile.toUri().toString());
            if (player != null) {
                player.stop();
            }
            player = new MediaPlayer(media);
            player.setVolume(3);
            player.setOnReady(onReady);
            player.setOnPlaying(onPlaying);
            player.setOnPaused(onPaused);            
            player.currentTimeProperty().addListener(valueListener);
            player.play();
            if (playedFromPlaylist) {
                getPosition(t);
            }
            playlistPlaying = playedFromPlaylist;
        } catch (Exception ex) {
            ex.printStackTrace();
        }        
    }

    public void playSong(Track t, boolean playedFromPlaylist) {
        try {
            currentSong = t;
//            control.open(new File(t.getPath()));
//            control.play();
//            control.setGain(volume);            
            
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
//                SessionConstants.sync.setCurrentSongId(t.getId());
//                SessionConstants.sync.setCurrentSongName(t.getTitle());
//                SessionConstants.sync.setCurrentSongArtist(t.getArtist());
//                SessionConstants.sync.setCurrentSongDuration(t.getDuration());
//                SessionConstants.sync.setIsPlaying(true);
//                SessionConstants.sync.setIsPaused(false);
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
            if (t.getId() == (queuePaths.get(i).getId())) {
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
        if(player != null) {
            player.play();
        }
    }

    public void pause() {
        if(player != null) {
            player.pause();
        }
    }

    public void stop() {
//        try {
//            control.stop();
//            invokeOnStopped();
//        } catch (BasicPlayerException ex) {
//            Logger.getLogger(PlayerController.class.getName()).log(Level.SEVERE, null, ex);
//        }
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

    public void setVolumeFromValue(double value, boolean fromUser) {
//        try {
//            SessionConstants.VOLUME_FROM_USER = fromUser;
//            control.setGain(value);
//            volume = (float)value;
//        } catch (BasicPlayerException ex) {
//            Logger.getLogger(PlayerController.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    public void enqueueSong(String song) {
        Track q = new Gson().fromJson(song, Track.class);
        queuePaths.add(q);
        
        invokeOnQueueChanged(q, null);
        Operation request = new Operation();
        request.setOperationType(OperationType.SYNC);
        request.setUserId(SessionConstants.USER_ID);
        SessionConstants.sync.setCurrentPlaylist(queuePaths);
        SessionConstants.sync.setAddedTrack(q);
        SessionConstants.sync.setDeletedTrack(null);
        request.setSync(SessionConstants.sync);
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
        SessionConstants.sync.setCurrentPlaylist(queuePaths);
        SessionConstants.sync.setAddedTrack(null);
        SessionConstants.sync.setDeletedTrack(t);
        request.setSync(SessionConstants.sync);
        TCPController.sendJSRequest(request);
    }
    
//    public void initPlayer() {
//        player = new BasicPlayer();
//        player.addBasicPlayerListener(basicPlayerListener);
//        control = (BasicController) player;
//        try {
//            control.setGain(1.0);
//        } catch (BasicPlayerException ex) {
//            Logger.getLogger(PlayerController.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        queuePaths = new ArrayList<>();
//        currentSong = null;
//        currentSongPosition = 0;
//        playlistPlaying = false;
//        SessionConstants.sync = new Sync();
//        volume = 1.0f;
//        SessionConstants.sync.setCurrentVolume(volume);
//    }

    
    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60
                    - durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%2d:%02d",
                        elapsedMinutes, elapsedSeconds, durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }

}
