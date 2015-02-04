package shudder.player;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import shudder.views.HomePage;

public class Music {

    private boolean playingMainLine, playingAuxLine, mute, pause;
    private File file;
    private final int byteChunkSize = 1024;
    private byte[] muteData;

    private SourceDataLine mainLine, auxLine;
    
    private Thread mainLineThread;
    private Thread auxLineThread;
    private Thread mergingThread;
    
    private int totalBytes;
    private float volumeValueMainLine;
    private float volumeValueAuxLine;
    
    private boolean merging;
    private boolean songHasFinished;

    private Runnable runnablePlayer = new Runnable() {
        @Override
        public synchronized void run() {
            playSong();
        }
    };

    public Music(HomePage parent) {
        file = null;
        playingMainLine = false;
        playingAuxLine = false;
        mute = false;
        pause = false;
        muteData = setMuteData();
        volumeValueMainLine = 0;
        volumeValueAuxLine = 0;
    }

    public void determineLine() {
        if (!playingMainLine && !playingAuxLine) {
            mainLineThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    playSong();
                }

            });
            mainLineThread.start();

        } else if (!playingAuxLine) {
            mergingThread = new Thread(new Runnable() {
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
                    while (volumeValueAuxLine < originalVolumeValue) {
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
            mergingThread = new Thread(new Runnable() {
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
                    while (volumeValueMainLine < originalVolumeValue) {
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

    public boolean loadSong(String filePath) {
        file = new File(filePath);
        if (file.exists() && file.getName().toLowerCase().endsWith(".mp3")) {
            return true;
        } else {
            file = null;
            return false;
        }
    }

    public void play() {
        if (file != null) {
            playingMainLine = true;
            if (mainLineThread != null && mainLineThread.isAlive()) {
                mainLineThread.stop();
                mainLine.close();
                setAuxLineVolume(volumeValueMainLine);
            }

            if (auxLineThread != null) {
                auxLineThread.stop();
                auxLine.close();
                setVolume(volumeValueAuxLine);
            }
            mainLineThread = null;
            mainLineThread = new Thread(runnablePlayer);
            mainLineThread.start();
        }

    }

    public void pause() {
        pause = !pause;
    }

    public void stop() {
        pause();
        file = null;
        playingMainLine = false;
        playingAuxLine = false;
        mute = false;
        pause = false;
        muteData = setMuteData();
    }

    public void mute() {
        if (file != null) {
            if (mute) {
                mute = false;
            } else {
                mute = true;
            }
        }
    }
    
    public boolean isMuted() {
        return mute;
    }

    public boolean isPaused() {
        return pause;
    }

    public boolean isPlaying() {
        return (playingMainLine || playingAuxLine) && !pause;
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

    private void stream(AudioFormat targetFormat, AudioInputStream din) {
        try {
            totalBytes = 0;
            playingMainLine = true;
            songHasFinished = false;
            byte[] data = new byte[byteChunkSize];
            getLine(targetFormat);
            if (mainLine != null) {
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
                mainLine.drain();
                mainLine.stop();
                mainLine.close();
                wait(1000);

                playingMainLine = false;
                din.close();
                if (nBytesRead == -1) {
                    songHasFinished = true;
                }
            }
        } catch (Exception e) {
            System.err.println("Problem playing audio!");
            e.printStackTrace();
        }
    }

    private void streamAuxLine(AudioFormat targetFormat, AudioInputStream din) {
        try {
            totalBytes = 0;
            songHasFinished = false;
            playingAuxLine = true;
            byte[] data = new byte[byteChunkSize];
            getAuxLine(targetFormat);
            if (auxLine != null) {
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
                auxLine.drain();
                auxLine.stop();
                auxLine.close();
                wait(1000);

                playingAuxLine = false;
                din.close();
                if (nBytesRead == -1) {
                    songHasFinished = true;
                }
            }
        } catch (Exception e) {
            System.err.println("Problem playing audio!");
            e.printStackTrace();
        }
    }
    
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
    
    private void wait(int time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            System.err.println("Could not wait!");
            e.printStackTrace();
        }
    }
    
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

    public void setVolumeFromValue(float volValue)  {
        volumeValueMainLine = volValue;
        volumeValueAuxLine = volValue;
        if (mainLine != null && mainLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl volume = (FloatControl) mainLine.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(volumeValueMainLine);
        }
        if (auxLine != null && auxLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl volume = (FloatControl) auxLine.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(volumeValueAuxLine);
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
    
    public boolean songHasFinished() {
        return songHasFinished;
    }
    
    public boolean isMerging() {
        return merging;
    }
    
    public float getVolume() {
        if(playingMainLine) {
            return volumeValueMainLine;
        } else if(playingAuxLine) {
            return volumeValueAuxLine;
        } else {
            return (volumeValueMainLine > volumeValueAuxLine)?volumeValueMainLine:volumeValueAuxLine;
        }
    }
}
