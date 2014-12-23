package shiftscope.player;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import shiftscope.view.HomePage;

/**
 *
 * @author Connor Mahaffey
 *
 * This class is a rewrite of the example from:
 * http://www.javazoom.net/mp3spi/documents.html
 *
 * This class needs the following libraries: JLayer -
 * http://www.javazoom.net/javalayer/javalayer.html MP3 SPI -
 * http://www.javazoom.net/mp3spi/mp3spi.html Tritonus Share
 * (tritonus_share-0.3.6.jar) - http://www.tritonus.org/plugins.html
 *
 * All credit goes to the creators of JLayer, MP3 SPI, and Tritonus.
 *
 * This simple re-write adds loop, mute, pause, restart, and stop methods to the
 * example mentioned above.
 *
 * This code is completely free to use for any purpose whatsoever. JLayer, MP3
 * SPI, and Tritonus are all released under the LGPL.
 *
 *
 * Known Issues:
 *
 * - Though using .stop() and then .play() *technically* works for restarting
 * the audio, doing this too fast causes problems because the old audio stream
 * is never stopped (writing to the audio line takes a bit, and it can't be
 * stopped once it's started). - Distorted audio (rarely? Problem with code or
 * with audio APIs?) - General Efficiency
 *
 */
public class Music {

    private boolean playingMainLine, playingAuxLine, mute, pause;
    private File file;
    private final int byteChunkSize = 1024;
    private byte[] muteData;
    private float volumeValueMainLine;
    private float volumeValueAuxLine;
    private int totalBytes;
    private SourceDataLine mainLine, auxLine;
    private HomePage parent;
    private Thread mainLineThread;
    private Thread auxLineThread;
    private Thread mergingThread;
    public int getTotalBytes() {
        return totalBytes;
    }

    /**
     * Declares default variable values.
     * @param parent
     */
    public Music(HomePage parent) {
        file = null;
        playingMainLine = false;
        playingAuxLine = false;
        mute = false;
        pause = false;
        muteData = setMuteData();
        volumeValueMainLine = 0;
        volumeValueAuxLine = 0;
        this.parent = parent;
    }

    private void determineLine() {
        System.out.println("determinando linea valiemia");
        System.out.println(playingMainLine + " ----- " + playingAuxLine);
        if(!playingMainLine && !playingAuxLine) {
            mainLineThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    playSong();
                }
            
            });
            mainLineThread.start();
            
        } else if(!playingAuxLine){
            System.out.println("caso 2");
            
            mergingThread = new Thread(new Runnable(){
                @Override
                public void run() {
                    float originalVolumeValue = volumeValueMainLine;
                    setAuxLineVolume(-50);
                    auxLineThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            playSongAuxLine();
                        }
                    });
                    auxLineThread.start();
                    while(volumeValueAuxLine < originalVolumeValue) {
                        volumeValueMainLine -= 2.5;
                        volumeValueAuxLine += 2.5;
                        setVolume(volumeValueMainLine);
                        setAuxLineVolume(volumeValueAuxLine);
                        try {
                            Thread.sleep(80);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Music.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    playingMainLine = false;
                }
                    
            });
            mergingThread.start();
        } else {
            System.out.println("entro al caso 3");
            mergingThread = new Thread(new Runnable(){
                @Override
                public void run() {
                    float originalVolumeValue = volumeValueAuxLine;
                    setVolume(-50);
                    mainLineThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            playSong();
                        }
                    });
                    mainLineThread.start();
                    while(volumeValueMainLine < originalVolumeValue) {
                        volumeValueMainLine += 2.5;
                        volumeValueAuxLine -= 2.5;
                        setVolume(volumeValueMainLine);
                        setAuxLineVolume(volumeValueAuxLine);
                        try {
                            Thread.sleep(80);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Music.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    playingAuxLine = false;
                } 
            });
            mergingThread.start();
        }
    }
    
    /**
     * Creates a file object. If the file path exists on the system, the given
     * file is an mp3, and a song is not currently playing in this instance of
     * the program, true is returned.
     *
     * @param filePath Path to the file.
     * @return If the file is loaded or not.
     */
    public boolean loadSong(String filePath) {
        file = new File(filePath);
        if (file.exists() && file.getName().toLowerCase().endsWith(".mp3")) {
            return true;
        } else {
            file = null;
            return false;
        }
    }
 /**
     * Starts playing the audio in a new thread.
     */
    public void play() {
        if (file != null) {
            mergingThread = new Thread(new Runnable() {
                @Override
                public void run() {
                   determineLine();
                }

            });
            mergingThread.start();
        }
    }

    /**
     * Pauses the audio at its current place. Calling this method once pauses
     * the audio stream, calling it again unpauses the audio stream.
     */
    public void pause() {
 
            pause = !pause;

            


    }

    /**
     * Closes the audio stream. This method takes some time to execute, and as
     * such you should never call .stop() followed immediately by .play(). If
     * you need to restart a song, use .restart().
     */
    public void stop() {
        pause();
        file = null;
        playingMainLine = false;
        mute = false;
        pause = false;
        muteData = setMuteData();
    }

    /**
     * Stream continues to play, but no sound is heard. Calling this method once
     * mutes the audio stream, calling it again unmutes the audio stream.
     */
    public void mute() {
        if (file != null) {
            if (mute) {
                mute = false;
            } else {
                mute = true;
            }
        }
    }

    /**
     * Makes a given audio file loop back to the beginning when the end is
     * reached. Calling this method once will make it loop, calling it again
     * will make it stop looping, but will not stop the audio from playing to
     * the end of a given loop.
     */

    /**
     * Restarts the current song. Always use this method to restart a song and
     * never .stop() followed by .play(), which is not safe.
     */

    /**
     * Returns whether or not a clip will loop when it reaches the end.
     *
     * @return Status of the variable loop.
     */

    /**
     * Returns if the audio is muted or not.
     *
     * @return Status of mute variable.
     */
    public boolean isMuted() {
        return mute;
    }

    /**
     * Returns if the audio is paused or not.
     *
     * @return Status of pause variable.
     */
    public boolean isPaused() {
        return pause;
    }

    /**
     * Returns if audio is currently playing (if the audio is muted, this will
     * still be true).
     *
     * @return If the thread is running or not.
     */
    public boolean isPlaying() {
        return playingMainLine;
    }

    public void playSong() {
        try {
            AudioInputStream in = AudioSystem.getAudioInputStream(file);
            AudioInputStream din = null;
            AudioFormat baseFormat = in.getFormat();
            AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false);
            din = AudioSystem.getAudioInputStream(decodedFormat, in);                
            stream(decodedFormat, din);
            in.close();
        } catch (Exception e) {
            System.err.println("Problem getting audio stream!");
            e.printStackTrace();
        }
    }
    
    public void playSongAuxLine() {
        try {
            AudioInputStream in = AudioSystem.getAudioInputStream(file);
            AudioInputStream din = null;
            AudioFormat baseFormat = in.getFormat();
            AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false);
            din = AudioSystem.getAudioInputStream(decodedFormat, in);                
            streamAuxLine(decodedFormat, din);
            in.close();
        } catch (Exception e) {
            System.err.println("Problem getting audio stream!");
            e.printStackTrace();
        }
    }

    /**
     * Small sections of audio bytes are read off, watching for a call to stop,
     * pause, restart, or mute the audio.
     *
     * @param targetFormat Format the audio will be changed to.
     * @param din The audio stream.
     */
    private void stream(AudioFormat targetFormat, AudioInputStream din) {
        try {
            totalBytes = 0;
            byte[] data = new byte[byteChunkSize];
            getLine(targetFormat);
            if (mainLine != null) {
                playingMainLine = true;
                mainLine.start();
                int nBytesRead = 0;
                while (nBytesRead != -1 && playingMainLine) {
                    nBytesRead = din.read(data, 0, data.length);
                    totalBytes += nBytesRead;
                    if (nBytesRead != -1) {
                        if (mute) {
                            mainLine.write(muteData, 0, nBytesRead);
                        } else {
                            mainLine.write(data, 0, nBytesRead);
                        }
                    }
                    while (pause) {
                        wait(15);
                    }
                }
                wait(1000);
                parent.next();
                mainLine.drain();
                mainLine.stop();
                mainLine.close();
                playingMainLine = false;
                din.close();
            }
        } catch (Exception e) {
            System.err.println("Problem playing audio!");
            e.printStackTrace();
        }
    }
    
    private void streamAuxLine(AudioFormat targetFormat, AudioInputStream din) {
        try {
            totalBytes = 0;
            byte[] data = new byte[byteChunkSize];
            getAuxLine(targetFormat);         
            if (auxLine != null) {
                playingAuxLine = true;
                auxLine.start();
                int nBytesRead = 0;
                while (nBytesRead != -1 && playingAuxLine) {
                    nBytesRead = din.read(data, 0, data.length);
                    totalBytes += nBytesRead;
                    if (nBytesRead != -1) {
                        if (mute) {
                            auxLine.write(muteData, 0, nBytesRead);
                        } else {
                            auxLine.write(data, 0, nBytesRead);
                        }
                    }
                    while (pause) {
                        wait(15);
                    }
                }
                wait(1000);
                parent.next();
                auxLine.drain();
                auxLine.stop();
                auxLine.close();
                playingAuxLine = false;
                din.close();
            }
        } catch (Exception e) {
            System.err.println("Problem playing audio!");
            e.printStackTrace();
        }
    }
    

    /**
     * Gets the line of audio.
     *
     * @param audioFormat The format of the audio.
     * @return The line of audio.
     */
    private void getLine(AudioFormat audioFormat) {
        mainLine = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

        try {
            mainLine = (SourceDataLine) AudioSystem.getLine(info);
            mainLine.open(audioFormat);
            setVolume();
        } catch (Exception e) {
            System.err.println("Could not get audio line!");
            e.printStackTrace();
        }
    }
    
    private void getAuxLine(AudioFormat audioFormat) {
        auxLine = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

        try {
            auxLine = (SourceDataLine) AudioSystem.getLine(info);
            auxLine.open(audioFormat);
            setAuxLineVolume(volumeValueAuxLine);
        } catch (Exception e) {
            System.err.println("Could not get audio line!");
            e.printStackTrace();
        }
    }

    /**
     * Waits a specified number of milliseconds.
     *
     * @param time Time to wait (in milliseconds).
     */
    private void wait(int time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            System.err.println("Could not wait!");
            e.printStackTrace();
        }
    }

    /**
     * Sets a byte array of all zeros to "play" when audio is muted. This
     * produces no sound.
     *
     * @return Byte array of all zeros.
     */
    private byte[] setMuteData() {
        byte[] x = new byte[byteChunkSize];
        for (int i = 0; i < x.length; i++) {
            x[i] = 0;
        }

        return x;
    }

    public void volumeUp() {
        if (mainLine != null && mainLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl volume = (FloatControl) mainLine.getControl(FloatControl.Type.MASTER_GAIN);
            volumeValueMainLine = volume.getValue();
            if (volumeValueMainLine < 0) {
                volumeValueMainLine += 2.5f;
            }
            volume.setValue(volumeValueMainLine);
        }
    }

    public void volumeDown() {
        if (mainLine != null && mainLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl volume = (FloatControl) mainLine.getControl(FloatControl.Type.MASTER_GAIN);
            if (volumeValueMainLine > -70) {
                volumeValueMainLine -= 2.5f;
            }
            volume.setValue(volumeValueMainLine);
        }
    }

    public void setVolume(float volValue) {
        volumeValueMainLine = volValue;
        if (mainLine != null && mainLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl volume = (FloatControl) mainLine.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(volumeValueMainLine);
        }
    }
    
    private void setVolume() {
        if (mainLine != null && mainLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl volume = (FloatControl) mainLine.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(volumeValueMainLine);
        }
    }    


    
    public void setAuxLineVolume(float v) {
        volumeValueAuxLine = v;
        if (auxLine != null && auxLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl volume = (FloatControl) auxLine.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(volumeValueAuxLine);
        }
    }

    public float getVolumeValue() {
        return volumeValueMainLine;
    }
}
