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
import java.util.Random;
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
    private boolean playlistPlaying;
    private boolean paused;
    private ArrayList<Track> queuePaths;
    private ArrayList<Track> shuffleQueue;
    private boolean shuffle = false;
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
            if (paused) {
                SessionConstants.sync.setIsPlaying(true);
                SessionConstants.sync.setIsPaused(false);
                request.setSync(SessionConstants.sync);
                TCPController.sendJSRequest(request);
                paused = false;
                invokeOnPlayed();
            } else {
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
    private Runnable onEndOfMedia = new Runnable() {

        @Override
        public void run() {
            next();
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
        shuffleQueue = new ArrayList<>();
        currentSong = null;
        currentSongPosition = 0;
        playlistPlaying = false;
        SessionConstants.sync = new Sync();
        volume = 1.0f;
        SessionConstants.sync.setCurrentVolume(volume);
    }

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
            playSong(t, playedFromPlaylist);
        } catch (Exception ex) {
            ex.printStackTrace();
        }        
    }

    public void playSong(Track t, boolean playedFromPlaylist) {
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
            player.setOnEndOfMedia(onEndOfMedia);
            player.currentTimeProperty().addListener(valueListener);
            setVolumeFromValue(volume, false);
            player.play();
            if (playedFromPlaylist) {
                getPosition(t);
            }
            playlistPlaying = playedFromPlaylist;
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
        if(player != null && paused) {
            player.play();
        }
    }
    
    public void pause() {
        if(player != null && !paused) {
            paused = true;
            player.pause();
        }
    }

    public void stop() {
        if(player != null) {
            player.stop();
        }
    }
    
    public void shuffle() {
        shuffle = !shuffle;
    }
    
    public void seek(float percentage) {
        if (player != null) {
            player.seek(duration.multiply(percentage));
        }
    }

    public void next() {
        if (playlistPlaying) {
            if(shuffle) {
                int position;
                System.out.println("1: " + shuffleQueue.size() + " 2: " + queuePaths.size() );
                do {
                    Random r = new Random();
                    position = r.nextInt(queuePaths.size()-1-0) + 0;
                    System.out.println(position);                    
                } while(shuffleQueue.contains(queuePaths.get(position)) && shuffleQueue.size() != queuePaths.size());
                if(shuffleQueue.size() != queuePaths.size()) {
                    shuffleQueue.add(queuePaths.get(position));
                    currentSong = queuePaths.get(position);
                    currentSongPosition = position;
                    playSong(currentSong, true);
                }
            } else {
                if (currentSongPosition < queuePaths.size() - 1) {
                    currentSongPosition++;
                    currentSong = queuePaths.get(currentSongPosition);
                    playSong(currentSong, true);
                }                
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
        SessionConstants.VOLUME_FROM_USER = fromUser;
        volume = (float)value;
        if(player != null) {
            player.setVolume(value);
        }
        SessionConstants.sync.setCurrentVolume(volume);
        if (SessionConstants.VOLUME_FROM_USER) {
            Operation volumeRequest = new Operation();
            volumeRequest.setOperationType(OperationType.SET_VOLUME);
            volumeRequest.setUserId(SessionConstants.USER_ID);
            volumeRequest.setSync(SessionConstants.sync);
            volumeRequest.setValue(volume);
            TCPController.sendJSRequest(volumeRequest);
        }        
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
    
    private String formatTime(Duration elapsed, Duration duration) {
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
