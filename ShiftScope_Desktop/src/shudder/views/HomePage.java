/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shudder.views;

import com.beaglebuddy.mp3.MP3;
import com.google.gson.Gson;
import com.ning.http.client.Response;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;
import shudder.controllers.FolderController;
import shudder.controllers.UserCotroller;
import shudder.criteria.FolderCriteria;
import shudder.dto.FolderCreationDTO;
import shudder.dto.FolderDTO;
import shudder.listeners.FolderListener;
import shudder.listeners.LoginListener;
import shudder.model.Folder;
import shudder.model.Track;
import shudder.netservices.TCPService;
import shudder.util.Constants;
import shudder.util.Operation;
import shudder.util.OperationType;
import shudder.util.SessionConstants;
import shudder.util.Sync;
import shudder.util.comparators.ArtistComparator;
import shudder.util.comparators.TitleComparator;
import shudder.views.dialogs.MainDialog;

/**
 *
 * @author VeronicaEncinales
 */
public class HomePage extends javax.swing.JFrame implements BasicPlayerListener {

    //Listeners
    private final FolderListener folderListener = new FolderListener() {

        @Override
        public void OnContentFetched(FolderDTO folderContent) {
            drawFolder(folderContent);
        }

        @Override
        public void fetchingContent() {
            folderPane.removeAll();
            JLabel loadingLabel = new JLabel("Fetching please wait...");
            loadingLabel.setFont(serifFont);
            loadingLabel.setBounds(0, 0, 200, 20);
            folderPane.add(loadingLabel);
            folderPane.revalidate();
            folderPane.repaint();
        }

        @Override
        public void OnError(String error) {

        }
    };
    
    private final LoginListener loginListener = new LoginListener() {

        @Override
        public void OnInit() {
            init();
        }
        
        @Override
        public void loading() {
            
        }

        @Override
        public void laoded() {
            
        }

        @Override
        public void OnError(String error) {
            
        }
    };
    
    //IMAGES
    ImageIcon musicIcon;
    ImageIcon folderIcon;
    //FONTS
    Font serifFont = new Font("sans-serif", Font.PLAIN, 14);
    Font serifFontArtist = new Font("sans-serif", Font.PLAIN, 12);

    //GUI VARIABLES
    public TCPService webSocket;
    private Gson JSONParser;
    private JFileChooser fileChooser;
    private float totalFiles;
    private float currentFileScanned;

    private int currentSongPosition;
    private int layoutWidth;
    private boolean orderBySongName;
    private boolean orderByArtist;
    private ArrayList<Track> queuePaths;
    private FolderDTO folderContent;
    private Track currentSong;
    private Sync sync;

    private BasicPlayer player;
    private BasicController control;
    private boolean playlistPlaying;
    private boolean paused;
    private boolean volumeAdjustedByUser;
    private String totalTimeString;
    private String elapsedTimeString;
    private int totalSeconds;
    private int currentSecond;
    private int frameLength;
    private Float frameRate;

    @Override
    public void opened(Object o, Map map) {
        //display("opened : " + map.toString());
        Long duration = (Long) map.get("duration");
        int mili = (int) (duration / 1000);
        int sec = (int) (mili / 1000) % 60;
        int min = (int) (mili / 1000) / 60;
        totalTimeString = min + ":" + String.format("%02d", sec);
        totalTime.setText(totalTimeString);
        totalSeconds = (Integer.parseInt(totalTimeString.split(":")[0]) * 60) + Integer.parseInt(totalTimeString.split(":")[1]);
        frameRate = (Float) map.get("mp3.framerate.fps");
        frameLength = (int) map.get("mp3.framesize.bytes");
        songPositionSlider.setMaximum(totalSeconds);
    }

    @Override
    public void progress(int i, long l, byte[] bytes, Map map) {
        //display("progress : " + map.toString());
        Long duration1 = (Long) map.get("mp3.position.microseconds");
        int mili = (int) (duration1 / 1000);
        int sec = (int) (mili / 1000) % 60;
        int min = (int) (mili / 1000) / 60;
        elapsedTimeString = min + ":" + String.format("%02d", sec);
        elapsedTime.setText(elapsedTimeString);
        currentSecond = (Integer.parseInt(elapsedTimeString.split(":")[0]) * 60) + Integer.parseInt(elapsedTimeString.split(":")[1]);
        songPositionSlider.setValue(currentSecond);
    }

    @Override
    public void stateUpdated(BasicPlayerEvent event) {
        display("stateUpdated : " + event.toString());
        Operation request = new Operation();
        request.setOperationType(OperationType.SYNC);
        request.setUserId(SessionConstants.USER_ID);
        switch (event.getCode()) {

            case BasicPlayerEvent.PLAYING:
                sync.setCurrentSongId(currentSong.getId());
                sync.setCurrentSongName(currentSong.getTitle());
                sync.setCurrentSongArtist(currentSong.getArtist());
                sync.setCurrentSongDuration(currentSong.getDuration());
                sync.setCurrentVolume((int) player.getGainValue());
                sync.setIsPlaying(true);
                sync.setIsPaused(false);
                request.setSync(sync);
                webSocket.sendRequest(request);
                paused = false;
                break;

            case BasicPlayerEvent.PAUSED:
                sync.setIsPlaying(false);
                sync.setIsPaused(true);
                request.setSync(sync);
                webSocket.sendRequest(request);
                paused = true;
                break;
            case BasicPlayerEvent.RESUMED:
                sync.setIsPlaying(true);
                sync.setIsPaused(false);
                request.setSync(sync);
                webSocket.sendRequest(request);
                paused = false;
                break;

            case BasicPlayerEvent.EOM:
                next();
                break;

            case BasicPlayerEvent.GAIN:
                if (volumeAdjustedByUser) {
                    //enviar por socket
                    System.out.println("enviar por sockett");
                } else {
                    System.out.println("AJUSTADO DE SOCKET");
                    volumeSlider.setValue((int) (player.getGainValue() * 100));
                }
                break;
        }

    }

    @Override
    public void setController(BasicController controller) {
        //display("setController : " + controller); 
    }

    private void drawFolder(FolderDTO fetchedFolder) {
        folderContent = fetchedFolder;
        ArrayList<JPanel> panels = new ArrayList<>();
        if (fetchedFolder != null) {
            if (orderByArtist) {
                Collections.sort(folderContent.getTracks(), new ArtistComparator());
            } else {
                Collections.sort(folderContent.getTracks(), new TitleComparator());
            }
            folderPane.removeAll();
            JLabel loadingLabel = new JLabel("Loading please wait...");
            loadingLabel.setFont(serifFont);
            loadingLabel.setBounds(0, 0, 200, 20);
            folderPane.add(loadingLabel);
            folderPane.revalidate();
            folderPane.repaint();
            int totalElements = fetchedFolder.getFolders().size() + fetchedFolder.getTracks().size();

            //folderPane.removeAll();
            folderPane.setPreferredSize(new Dimension(layoutWidth, totalElements * 45));
            foldersScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            foldersScrollPane.getVerticalScrollBar().setValue(0);

            ArrayList<Folder> folders = fetchedFolder.getFolders();
            ArrayList<Track> tracks = fetchedFolder.getTracks();

            int delta = 0;
            for (int i = 0; i < folders.size(); i++) {
                final Folder folder = folders.get(i);
                final JPanel folderPanel = new JPanel() {

                    @Override
                    public void paint(Graphics g) {
                        super.paint(g);
                        g.setColor(Color.GRAY);
                        g.drawRoundRect(0, 0, layoutWidth - 2, 43, 3, 3);
                    }

                };
                folderPanel.addMouseListener(new MouseListener() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            getFolderContent(folder.getId());
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
//                                JPopupMenu popupMenu = new JPopupMenu();
//                                popupMenu.setLabel("Folder");
//                                JMenuItem remove = new JMenuItem("Remove folder");
//                                remove.addActionListener(new ActionListener() {
//
//                                    @Override
//                                    public void actionPerformed(ActionEvent e) {
//                                        
//                                    }
//
//                                });
//
//                                popupMenu.add(remove);
//                                popupMenu.show(e.getComponent(), e.getX(), e.getY());
                        }

                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        folderPanel.setBackground(new Color(245, 245, 245));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        folderPanel.setBackground(Color.WHITE);
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {

                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {

                    }
                });
                folderPanel.setLayout(null);
                folderPanel.setBackground(Color.white);
                folderPanel.setBounds(0, (i * 45), layoutWidth, 45);
                JLabel folderLabel = new JLabel(folder.getTitle());
                folderLabel.setFont(serifFont);
                JLabel iconLabel = new JLabel();
                iconLabel.setIcon(folderIcon);

                iconLabel.setBounds(15, 0, 35, 45);
                folderLabel.setBounds(45, 0, layoutWidth - 35, 45);

                folderPanel.add(iconLabel);
                folderPanel.add(folderLabel);
                panels.add(folderPanel);

            }
            delta = folders.size() * 45;
            for (int i = 0; i < tracks.size(); i++) {
                final Track track = tracks.get(i);
                final JPanel trackPanel = new JPanel() {
                    @Override
                    public void paint(Graphics g) {
                        super.paint(g);
                        g.setColor(Color.GRAY);
                        g.drawRoundRect(0, 0, layoutWidth - 2, 43, 3, 3);
                    }

                };
                trackPanel.addMouseMotionListener(new MouseMotionAdapter() {

                    @Override
                    public void mouseDragged(MouseEvent e) {
                            //e.translatePoint(e.getComponent().getLocation().x, e.getComponent().getLocation().y);
                        //trackPanel.setLocation(0, e.getY()+5);
                    }

                });
                trackPanel.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            playSong(track, false);
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            JPopupMenu popupMenu = new JPopupMenu();
                            popupMenu.setLabel("Folder");
                            JMenuItem play = new JMenuItem("Play");
                            play.addActionListener(new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    playSong(track, false);
                                }

                            });

                            JMenuItem addToPlaylist = new JMenuItem("Add to playlist");
                            addToPlaylist.addActionListener(new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    enqueueSong(track);
                                }

                            });
                            popupMenu.add(play);
                            popupMenu.add(addToPlaylist);
                            popupMenu.show(e.getComponent(), e.getX(), e.getY());
                        }

                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {

                        trackPanel.setBackground(new Color(245, 245, 245));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        trackPanel.setBackground(Color.WHITE);
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {

                    }
                });
                trackPanel.setLayout(null);
                trackPanel.setBackground(Color.white);
                trackPanel.setBounds(0, (i * 45) + delta, layoutWidth, 45);
                JLabel trackLabel = new JLabel(track.getTitle());
                trackLabel.setFont(serifFont);
                JLabel artistLabel = new JLabel(track.getArtist());
                artistLabel.setFont(serifFontArtist);
                JLabel iconLabel = new JLabel();
                iconLabel.setIcon(musicIcon);

                iconLabel.setBounds(10, 10, 35, 20);
                trackLabel.setBounds(35, 0, layoutWidth - 35, 20);
                artistLabel.setBounds(38, 20, layoutWidth - 38, 20);

                trackPanel.add(iconLabel);
                trackPanel.add(trackLabel);
                trackPanel.add(artistLabel);
                panels.add(trackPanel);

            }

        }
        folderPane.removeAll();
        for (JPanel panel : panels) {
            folderPane.add(panel);
        }
        folderPane.repaint();
        foldersScrollPane.revalidate();
        foldersScrollPane.repaint();
    }

    public class FolderBuilder extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            totalFiles = 0;
            currentFileScanned = 0;
            File[] selectedFiles = fileChooser.getSelectedFiles();
            for (File f : selectedFiles) {
                countFiles(f);
            }
            progressBar.setMaximum((int) totalFiles);
            System.out.println(totalFiles);
            progressBar.setVisible(true);
            System.out.println("Fetching your files...");
            for (File f : fileChooser.getSelectedFiles()) {
                buildFolderOptimized(f, -1);
            }
            return null;
        }

        @Override
        protected void done() {
            sync.setNewFolders(true);
            Operation request = new Operation();
            request.setOperationType(OperationType.SYNC);
            request.setUserId(SessionConstants.USER_ID);
            request.setSync(sync);
            webSocket.sendRequest(request);
            sync.setNewFolders(false);
            progressBar.setVisible(false);
            getFolderContent(-1);
            System.out.println("ShiftScope has finished...");
        }
    }

    public class TrackOrderer extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            if (orderBySongName) {
                Collections.sort(folderContent.getTracks(), new TitleComparator());
            } else {
                Collections.sort(folderContent.getTracks(), new ArtistComparator());
            }
            drawFetchedFolder(folderContent);
            return null;
        }

        @Override
        protected void done() {

        }
    };

    public HomePage() {
        initComponents();
        this.setLocationRelativeTo(null);
        ArrayList<Image> images = new ArrayList<Image>();
        images.add(createImageIcon("images/icon_48.png", "app_logo").getImage());
        images.add(createImageIcon("images/icon_72.png", "app_logo").getImage());
        images.add(createImageIcon("images/icon_96.png", "app_logo").getImage());
        images.add(createImageIcon("images/icon_144.png", "app_logo").getImage());
        setIconImages(images);
        getContentPane().setBackground(new Color(38, 0, 38));
        progressBar.setVisible(false);
        foldersScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        initPlayer();

        buttonGroup2.add(songTitleRadio);
        buttonGroup2.add(artistRadio);
        musicIcon = createImageIcon("images/music.png", "music_icon");
        folderIcon = createImageIcon("images/folder.png", "music_icon");
        songTitleRadio.setSelected(true);
        PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent changeEvent) {
                String propertyName = changeEvent.getPropertyName();
                if (propertyName.equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY)) {
                    //calculateDifference();
                    //drawFetchedFolder(folderContent);
                }
            }
        };

        //jSplitPane1.addPropertyChangeListener(propertyChangeListener);
        volumeSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (source.getValueIsAdjusting()) {

                    double value = new Double(String.valueOf(source.getValue()));
                    setVolumeFromValue(value / 100d, true);
                }
            }
        });

        songPositionSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (source.getValueIsAdjusting()) {
                    //System.out.println("esta ajustandose");
                    int value = source.getValue();
                    long skippedBytes = (long) (value * frameRate * frameLength);
//                    try {
//                        
//                        control.seek(skippedBytes);
//                        control.play();
//                    } catch (BasicPlayerException ex) {
//                        Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
//                    }
                } else {
                    //System.out.println("no se esta ajustando sino programando");
                }
            }
        ;
        });

        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                JLabel btn = (JLabel) e.getSource();
                btn.setBackground(new Color(57, 6, 57));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                JLabel btn = (JLabel) e.getSource();
                btn.setBackground(new Color(38, 0, 38));
            }
        };

        nextBtn.addMouseListener(mouseListener);
        backBtn.addMouseListener(mouseListener);
        stopBtn.addMouseListener(mouseListener);
        pauseBtn.addMouseListener(mouseListener);

        calculateDifference();
        drawPlaylist();
        MainDialog mainDialog = new MainDialog(this, true);
        mainDialog.setVisible(true);
        FolderController.addListener(folderListener);
        UserCotroller.addListener(loginListener);
    }

    private void init() {
        try {
            System.out.println("Conectando........");
            webSocket = new TCPService(new URI(Constants.SOCKET_SERVER));
            webSocket.connect();
        } catch (URISyntaxException ex) {
            Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFolderContent(-1);
    }

    private void calculateDifference() {
        layoutWidth = jSplitPane1.getDividerLocation() - 22;
    }

    public void playSong(Track t, boolean playedFromPlaylist) {
        try {
            control.open(new File(t.getPath()));
            control.play();
            currentSong = t;
            if (playedFromPlaylist) {
                getPosition(t);
            }
            currentSongLabel.setText(t.getTitle() + " - " + t.getArtist());
            playlistPlaying = playedFromPlaylist;
        } catch (BasicPlayerException ex) {
            Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
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
        System.out.println("before " + currentSongPosition);
        for (int i = 0; i < queuePaths.size(); i++) {
            if (t.equals(queuePaths.get(i))) {
                currentSongPosition = i;
                System.out.println("after " + currentSongPosition);
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
            Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void pause() {
        try {
            control.pause();
        } catch (BasicPlayerException ex) {
            Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stop() {
        try {
            control.stop();
        } catch (BasicPlayerException ex) {
            Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
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
        try {
            volumeAdjustedByUser = fromUser;
            player.setGain(value);
        } catch (BasicPlayerException ex) {
            Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
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

    public boolean isPaused() {
//        return player.isPaused();
        return false;
    }

    public void enqueueSong(Track q) {
        queuePaths.add(q);
        drawPlaylist();
        Operation request = new Operation();
        request.setOperationType(OperationType.SYNC);
        request.setUserId(SessionConstants.USER_ID);
        sync.setCurrentPlaylist(queuePaths);
        request.setSync(sync);
        webSocket.sendRequest(request);
    }
    
    public void dequeueSong(Track t) {
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
        drawPlaylist();
        Operation request = new Operation();
        request.setOperationType(OperationType.SYNC);
        request.setUserId(SessionConstants.USER_ID);
        sync.setCurrentPlaylist(queuePaths);
        request.setSync(sync);
        webSocket.sendRequest(request);
    }

    public Sync getSync() {
        return sync;
    }

//    public final void initPlayer() {
//        player = new Music(this);
//        queuePaths = new ArrayList<>();
//        currentSong = null;
//        currentSongPosition = 0;
//        playlistPlaying = false;
//        sync = new Sync();
//    }
    public final void initPlayer() {
        player = new BasicPlayer();
        player.addBasicPlayerListener(this);
        control = (BasicController) player;
        try {
            control.setGain(1.0);
        } catch (BasicPlayerException ex) {
            Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
        }
        queuePaths = new ArrayList<>();
        currentSong = null;
        currentSongPosition = 0;
        playlistPlaying = false;
        sync = new Sync();
    }

    public void display(String msg) {
        System.out.println(msg);
    }

    private void countFiles(File selectedFile) {
        File[] selectedFiles;
        if (selectedFile.isDirectory() && !selectedFile.isHidden()) {
            selectedFiles = selectedFile.listFiles();
            for (File f : selectedFiles) {
                totalFiles++;
                countFiles(f);
            }
        } else if (!selectedFile.isFile() && !selectedFile.isHidden() && selectedFile.getName().endsWith(".mp3")) {
            totalFiles++;
        }
    }

    private void buildFolderOptimized(File folder, int parentId) {
        JSONParser = new Gson();
        Folder createdFolder;
        FolderCreationDTO folderToCreate = new FolderCreationDTO();

        ArrayList<File> folders = new ArrayList<>();
        ArrayList<Track> tracks = new ArrayList<>();

        Folder newFolder = new Folder();
        newFolder.setPath(folder.getAbsolutePath());
        newFolder.setTitle(folder.getName());
        newFolder.setParentFolder(parentId);
        newFolder.setLibrary(SessionConstants.LIBRARY_ID);

        folderToCreate.setFolder(newFolder);

        File[] files = folder.listFiles();

        for (File f : files) {
            if (f.isDirectory()) {
                folders.add(f);
            } else if (f.getName().endsWith(".mp3") && !f.isHidden()) {
                Track track = new Track();
                try {
                    MP3 mp3 = new MP3(f);
                    AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(f);
                    Map properties = baseFileFormat.properties();
                    Long duration1 = (Long) properties.get("duration");
                    int mili = (int) (duration1 / 1000);
                    int sec = (int) (mili / 1000) % 60;
                    int min = (int) (mili / 1000) / 60;
                    track.setDuration(min + ":" + String.format("%02d", sec));
                    String path = f.getAbsolutePath();
                    String artist = mp3.getLeadPerformer();
                    String title = mp3.getTitle();
                    track.setArtist(artist);
                    track.setTitle(title);
                    if (artist != null) {
                        track.setArtist(artist);
                    } else {
                        track.setArtist("Unknown");
                    }
                    if (title != null) {
                        track.setTitle(title);
                    } else {
                        track.setTitle(f.getName());
                    }

                    track.setPath(path);
                    track.setLibrary(SessionConstants.LIBRARY_ID);
                    //jLabel2.setText(f.getAbsolutePath());
                    currentFileScanned++;
                    progressBar.setValue((int) currentFileScanned);
                    tracks.add(track);
                } catch (IOException ex) {
                } catch (UnsupportedAudioFileException ex) {
                    //Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        folderToCreate.setTracks(tracks);
        System.out.println("Crear " + folder + "   " + new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
        createdFolder = createFolderByLimit(folderToCreate, 300);
        if (createdFolder != null) {
            for (File file : folders) {
                buildFolderOptimized(file, createdFolder.getId());
            }
        }
    }

    private Folder createFolderByLimit(FolderCreationDTO folder, int trackLimit) {
        Folder createdFolder = null;
        ArrayList<Track> tracks = folder.getTracks();
        ArrayList<Track> tracksToCreate;
        int total = tracks.size();
        int start = 0;
        int end = 0;
        if (total > trackLimit) {
            start = 0;
            end = (start + trackLimit <= total - 1) ? start + trackLimit : total - 1;
            System.out.println(start + "  " + end);
            tracksToCreate = new ArrayList<>(tracks.subList(start, end));
            folder.setTracks(tracksToCreate);
        }

        Response response = FolderController.createFolderOptimized(folder);
        try {
            if (response.getStatusCode() == 200) {
                createdFolder = (Folder) JSONParser.fromJson(response.getResponseBody(), Folder.class);
                int parentId = createdFolder.getId();
                if (total > trackLimit) {
                    for (int i = end; i < total; i++) {
                        tracks.get(i).setParentFolder(parentId);
                    }
                    do {
                        start = end + 1;
                        end = (start + trackLimit <= total - 1) ? start + trackLimit : total - 1;
                        System.out.println(start + "  " + end + " " + new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
                        tracksToCreate = new ArrayList<>(tracks.subList(start, end));
                        folder.setFolder(null);
                        folder.setTracks(tracksToCreate);
                        FolderController.createFolderTracks(folder);
                    } while (response.getStatusCode() == 200 && start < total);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return createdFolder;
    }

    private void drawFetchedFolder(FolderDTO fetchedFolder) {
        FolderController.drawFolder(fetchedFolder);
    }

    private void drawSearchResults(ArrayList<Track> tracks) {
        folderPane.setPreferredSize(new Dimension(layoutWidth, tracks.size() * 45));
        foldersScrollPane.getVerticalScrollBar().setValue(0);
        folderPane.removeAll();
        for (int i = 0; i < tracks.size(); i++) {
            final Track track = tracks.get(i);
            final JPanel trackPanel = new JPanel() {
                @Override
                public void paint(Graphics g) {
                    super.paint(g);
                    g.setColor(Color.GRAY);
                    g.drawRoundRect(0, 0, layoutWidth - 2, 43, 3, 3);
                }

            };
            trackPanel.addMouseMotionListener(new MouseMotionAdapter() {

                @Override
                public void mouseDragged(MouseEvent e) {
                    //e.translatePoint(e.getComponent().getLocation().x, e.getComponent().getLocation().y);
                    //trackPanel.setLocation(0, e.getY()+5);
                }
            });

            trackPanel.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        playSong(track, false);
                    } else if (e.getButton() == MouseEvent.BUTTON3) {
                        JPopupMenu popupMenu = new JPopupMenu();
                        popupMenu.setLabel("Folder");
                        JMenuItem play = new JMenuItem("Play");
                        play.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                playSong(track, false);
                            }

                        });

                        JMenuItem addToPlaylist = new JMenuItem("Add to playlist");
                        addToPlaylist.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                enqueueSong(track);
                            }

                        });
                        popupMenu.add(play);
                        popupMenu.add(addToPlaylist);
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                    trackPanel.setBackground(new Color(245, 245, 245));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    trackPanel.setBackground(Color.WHITE);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }
            });
            trackPanel.setLayout(null);
            trackPanel.setBackground(Color.white);
            trackPanel.setBounds(0, (i * 45), layoutWidth, 45);
            JLabel trackLabel = new JLabel(track.getTitle());
            trackLabel.setFont(serifFont);
            JLabel artistLabel = new JLabel(track.getArtist());
            artistLabel.setFont(serifFontArtist);
            JLabel iconLabel = new JLabel();
            iconLabel.setIcon(musicIcon);

            iconLabel.setBounds(10, 10, 35, 20);
            trackLabel.setBounds(35, 0, layoutWidth - 35, 20);
            artistLabel.setBounds(38, 20, layoutWidth - 38, 20);

            trackPanel.add(iconLabel);
            trackPanel.add(trackLabel);
            trackPanel.add(artistLabel);
            folderPane.add(trackPanel);

        }
        foldersScrollPane.revalidate();
        foldersScrollPane.repaint();
    }

    private void drawPlaylist() {
        int totalElements = queuePaths.size();
        playlistPanel.removeAll();
        playlistPanel.setPreferredSize(new Dimension(230, totalElements * 45));
        playlistScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        ArrayList<Track> tracks = queuePaths;
        int delta = 0;

        for (int i = 0; i < tracks.size(); i++) {
            final Track track = tracks.get(i);
            final int position = i;
            final JPanel trackPanel = new JPanel() {

                @Override
                public void paint(Graphics g) {
                    super.paint(g);
                    g.setColor(Color.GRAY);
                    g.drawRoundRect(0, 0, 230 - 2, 43, 3, 3);
                }

            };
            trackPanel.addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        playSong(track, true);
                    } else if (e.getButton() == MouseEvent.BUTTON3) {
                        JPopupMenu popupMenu = new JPopupMenu();
                        popupMenu.setLabel("Folder");
                        JMenuItem remove = new JMenuItem("Remove from playlist");
                        remove.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                dequeueSong(track);
                            }

                        });

                        popupMenu.add(remove);
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    trackPanel.setBackground(new Color(245, 245, 245));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    trackPanel.setBackground(Color.WHITE);
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }
            });
            trackPanel.setLayout(null);
            trackPanel.setBackground(Color.white);
            trackPanel.setBounds(0, (i * 45) + delta, 230, 45);
            JLabel trackLabel = new JLabel(track.getTitle());
            trackLabel.setFont(serifFont);
            JLabel artistLabel = new JLabel(track.getArtist());
            artistLabel.setFont(serifFontArtist);
            JLabel iconLabel = new JLabel();
            iconLabel.setIcon(musicIcon);

            iconLabel.setBounds(10, 10, 35, 20);
            trackLabel.setBounds(35, 0, 230 - 35, 20);
            artistLabel.setBounds(38, 20, 230 - 38, 20);

            trackPanel.add(iconLabel);
            trackPanel.add(trackLabel);
            trackPanel.add(artistLabel);
            playlistPanel.add(trackPanel);

        }
        playlistScrollPane.revalidate();
        playlistScrollPane.repaint();

    }

    private void getFolderContent(int id) {
        FolderCriteria criteria = new FolderCriteria();
        criteria.setId(id);
        criteria.setLibrary(SessionConstants.LIBRARY_ID);
        FolderController.getFolderContentById(criteria);
    }

    protected final ImageIcon createImageIcon(String path,
            String description) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        jComboBox1 = new javax.swing.JComboBox();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        backButton = new javax.swing.JLabel();
        selectFolderButton = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        backBtn = new javax.swing.JLabel();
        pauseBtn = new javax.swing.JLabel();
        stopBtn = new javax.swing.JLabel();
        nextBtn = new javax.swing.JLabel();
        songNameLabel = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        volumeSlider = new javax.swing.JSlider();
        jPanel1 = new javax.swing.JPanel();
        elapsedTime = new javax.swing.JLabel();
        songPositionSlider = new javax.swing.JSlider();
        totalTime = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        currentSongLabel = new javax.swing.JLabel();
        toolBar = new javax.swing.JToolBar();
        progressBar = new javax.swing.JProgressBar();
        jSplitPane1 = new javax.swing.JSplitPane();
        foldersScrollPane = new javax.swing.JScrollPane();
        folderPane = new javax.swing.JPanel();
        playlistScrollPane = new javax.swing.JScrollPane();
        playlistPanel = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        clearPlaylistBtn = new javax.swing.JButton();
        songTitleRadio = new javax.swing.JRadioButton();
        artistRadio = new javax.swing.JRadioButton();
        searchTextField = new javax.swing.JTextField();

        jToolBar1.setRollover(true);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Shudder");
        setBackground(new java.awt.Color(38, 0, 38));
        setBounds(new java.awt.Rectangle(0, 0, 865, 654));
        setMinimumSize(new java.awt.Dimension(800, 600));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        jPanel4.setBackground(new java.awt.Color(38, 0, 38));
        jPanel4.setLayout(new java.awt.BorderLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/shudder/views/images/logo_shudder.png"))); // NOI18N
        jLabel1.setAlignmentX(-0.0F);
        jPanel4.add(jLabel1, java.awt.BorderLayout.CENTER);

        jPanel3.setBackground(new java.awt.Color(38, 0, 38));

        backButton.setBackground(new java.awt.Color(38, 0, 38));
        backButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/shudder/views/images/ic_unknown.png"))); // NOI18N
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                backButtonMouseClicked(evt);
            }
        });
        jPanel3.add(backButton);

        selectFolderButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/shudder/views/images/ic_folder.png"))); // NOI18N
        selectFolderButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectFolderButtonMouseClicked(evt);
            }
        });
        jPanel3.add(selectFolderButton);

        jPanel2.setBackground(new java.awt.Color(38, 0, 38));
        jPanel2.setPreferredSize(new java.awt.Dimension(150, 40));

        backBtn.setBackground(new java.awt.Color(38, 0, 38));
        backBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        backBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/shudder/views/images/ic_back.png"))); // NOI18N
        backBtn.setOpaque(true);
        backBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                backBtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backBtnMouseEntered(evt);
            }
        });
        jPanel2.add(backBtn);

        pauseBtn.setBackground(new java.awt.Color(38, 0, 38));
        pauseBtn.setForeground(new java.awt.Color(254, 254, 254));
        pauseBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pauseBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/shudder/views/images/ic_pause.png"))); // NOI18N
        pauseBtn.setOpaque(true);
        pauseBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        pauseBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pauseBtnMouseClicked(evt);
            }
        });
        jPanel2.add(pauseBtn);

        stopBtn.setBackground(new java.awt.Color(38, 0, 38));
        stopBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        stopBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/shudder/views/images/ic_stop.png"))); // NOI18N
        stopBtn.setOpaque(true);
        stopBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        stopBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                stopBtnMouseClicked(evt);
            }
        });
        jPanel2.add(stopBtn);

        nextBtn.setBackground(new java.awt.Color(38, 0, 38));
        nextBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nextBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/shudder/views/images/ic_next.png"))); // NOI18N
        nextBtn.setOpaque(true);
        nextBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        nextBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nextBtnMouseClicked(evt);
            }
        });
        jPanel2.add(nextBtn);
        jPanel2.add(songNameLabel);

        jPanel6.setBackground(new java.awt.Color(38, 0, 38));

        volumeSlider.setBackground(new java.awt.Color(38, 0, 38));
        volumeSlider.setValue(100);
        jPanel6.add(volumeSlider);

        jPanel1.setBackground(new java.awt.Color(38, 0, 38));

        elapsedTime.setText("0:00");
        jPanel1.add(elapsedTime);

        songPositionSlider.setBackground(new java.awt.Color(38, 0, 38));
        songPositionSlider.setValue(0);
        jPanel1.add(songPositionSlider);

        totalTime.setText("0:00");
        jPanel1.add(totalTime);

        jPanel5.setBackground(new java.awt.Color(38, 0, 38));

        currentSongLabel.setFont(new java.awt.Font("SansSerif", 0, 15)); // NOI18N
        jPanel5.add(currentSongLabel);

        toolBar.setBackground(new java.awt.Color(38, 0, 38));
        toolBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.add(progressBar);

        jSplitPane1.setDividerLocation(560);
        jSplitPane1.setDividerSize(0);
        jSplitPane1.setToolTipText("");
        jSplitPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jSplitPane1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jSplitPane1PropertyChange(evt);
            }
        });

        foldersScrollPane.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        foldersScrollPane.setMinimumSize(new java.awt.Dimension(500, 25));
        foldersScrollPane.setPreferredSize(new java.awt.Dimension(500, 948));

        folderPane.setBackground(new java.awt.Color(254, 254, 254));
        folderPane.setMaximumSize(new java.awt.Dimension(700, 32767));
        folderPane.setMinimumSize(new java.awt.Dimension(600, 0));

        javax.swing.GroupLayout folderPaneLayout = new javax.swing.GroupLayout(folderPane);
        folderPane.setLayout(folderPaneLayout);
        folderPaneLayout.setHorizontalGroup(
            folderPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 983, Short.MAX_VALUE)
        );
        folderPaneLayout.setVerticalGroup(
            folderPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 944, Short.MAX_VALUE)
        );

        foldersScrollPane.setViewportView(folderPane);

        jSplitPane1.setLeftComponent(foldersScrollPane);

        playlistScrollPane.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        playlistScrollPane.setViewportBorder(javax.swing.BorderFactory.createEtchedBorder());
        playlistScrollPane.setMaximumSize(new java.awt.Dimension(170, 500));
        playlistScrollPane.setMinimumSize(new java.awt.Dimension(150, 23));
        playlistScrollPane.setPreferredSize(new java.awt.Dimension(150, 100));

        playlistPanel.setBackground(new java.awt.Color(254, 254, 254));
        playlistPanel.setPreferredSize(new java.awt.Dimension(400, 837));

        javax.swing.GroupLayout playlistPanelLayout = new javax.swing.GroupLayout(playlistPanel);
        playlistPanel.setLayout(playlistPanelLayout);
        playlistPanelLayout.setHorizontalGroup(
            playlistPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        playlistPanelLayout.setVerticalGroup(
            playlistPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 837, Short.MAX_VALUE)
        );

        playlistScrollPane.setViewportView(playlistPanel);

        jSplitPane1.setRightComponent(playlistScrollPane);

        jPanel7.setBackground(new java.awt.Color(38, 0, 38));

        clearPlaylistBtn.setText("Clear Playlist");
        clearPlaylistBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearPlaylistBtnActionPerformed(evt);
            }
        });

        songTitleRadio.setBackground(new java.awt.Color(38, 0, 38));
        songTitleRadio.setText("Song Title");
        songTitleRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                songTitleRadioActionPerformed(evt);
            }
        });

        artistRadio.setBackground(new java.awt.Color(38, 0, 38));
        artistRadio.setText("Artist");
        artistRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                artistRadioActionPerformed(evt);
            }
        });

        searchTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchTextFieldActionPerformed(evt);
            }
        });
        searchTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchTextFieldKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addComponent(songTitleRadio)
                .addGap(18, 18, 18)
                .addComponent(artistRadio)
                .addGap(18, 18, 18)
                .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(clearPlaylistBtn))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(clearPlaylistBtn)
                .addGap(0, 25, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(songTitleRadio)
                    .addComponent(artistRadio)
                    .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 664, Short.MAX_VALUE)))
                        .addContainerGap())))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 763, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(789, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(24, 24, 24)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void selectFolderButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_selectFolderButtonMouseClicked
        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            new FolderBuilder().execute();
        }
    }//GEN-LAST:event_selectFolderButtonMouseClicked

    private void pauseBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pauseBtnMouseClicked
        if (paused) {
            resume();
        } else {
            pause();
        }
    }//GEN-LAST:event_pauseBtnMouseClicked

    private void stopBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stopBtnMouseClicked
        stop();
    }//GEN-LAST:event_stopBtnMouseClicked

    private void backButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backButtonMouseClicked
        getFolderContent(SessionConstants.PARENT_FOLDER_ID);
    }//GEN-LAST:event_backButtonMouseClicked

    private void nextBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextBtnMouseClicked
        next();
    }//GEN-LAST:event_nextBtnMouseClicked

    private void backBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backBtnMouseClicked
        back();
    }//GEN-LAST:event_backBtnMouseClicked

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (webSocket != null) {
            webSocket.closeConnection(5, "hola");
        }
    }//GEN-LAST:event_formWindowClosing

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        jSplitPane1.setDividerLocation(this.getWidth() - 300);
        calculateDifference();
        drawFetchedFolder(folderContent);
    }//GEN-LAST:event_formComponentResized

    private void jSplitPane1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jSplitPane1PropertyChange

    }//GEN-LAST:event_jSplitPane1PropertyChange

    private void backBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backBtnMouseEntered

    }//GEN-LAST:event_backBtnMouseEntered

    private void clearPlaylistBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearPlaylistBtnActionPerformed
        queuePaths.clear();
        sync.setCurrentPlaylist(queuePaths);

        Operation request = new Operation();
        request.setOperationType(OperationType.SYNC);
        request.setUserId(SessionConstants.USER_ID);
        request.setSync(sync);

        webSocket.sendRequest(request);
        drawPlaylist();
    }//GEN-LAST:event_clearPlaylistBtnActionPerformed

    private void songTitleRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_songTitleRadioActionPerformed
        if (songTitleRadio.isSelected()) {
            orderBySongName = true;
            orderByArtist = false;
            new TrackOrderer().execute();
        }
    }//GEN-LAST:event_songTitleRadioActionPerformed

    private void searchTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchTextFieldActionPerformed

    }//GEN-LAST:event_searchTextFieldActionPerformed

    private void searchTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchTextFieldKeyReleased
        if (searchTextField.getText().length() > 2) {
            String matchCriteria = searchTextField.getText();
            ArrayList<Track> tracks = new ArrayList<Track>(folderContent.getTracks().stream().filter(p -> p.getTitle().contains(matchCriteria) || p.getArtist().contains(matchCriteria)).collect(Collectors.toList()));
            drawSearchResults(tracks);
        } else if (searchTextField.getText().length() == 0) {
            drawFetchedFolder(folderContent);
        }
    }//GEN-LAST:event_searchTextFieldKeyReleased

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased

    }//GEN-LAST:event_formKeyReleased

    private void artistRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_artistRadioActionPerformed
        if (artistRadio.isSelected()) {
            orderByArtist = true;
            orderBySongName = false;
            new TrackOrderer().execute();
        }
    }//GEN-LAST:event_artistRadioActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton artistRadio;
    private javax.swing.JLabel backBtn;
    private javax.swing.JLabel backButton;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton clearPlaylistBtn;
    private javax.swing.JLabel currentSongLabel;
    private javax.swing.JLabel elapsedTime;
    private javax.swing.JPanel folderPane;
    private javax.swing.JScrollPane foldersScrollPane;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel nextBtn;
    private javax.swing.JLabel pauseBtn;
    private javax.swing.JPanel playlistPanel;
    private javax.swing.JScrollPane playlistScrollPane;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTextField searchTextField;
    private javax.swing.JLabel selectFolderButton;
    private javax.swing.JLabel songNameLabel;
    private javax.swing.JSlider songPositionSlider;
    private javax.swing.JRadioButton songTitleRadio;
    private javax.swing.JLabel stopBtn;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JLabel totalTime;
    private javax.swing.JSlider volumeSlider;
    // End of variables declaration//GEN-END:variables

}
