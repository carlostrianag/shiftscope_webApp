/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shiftscope.view;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.Mp3File;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
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
import org.apache.http.HttpResponse;
import shiftscope.controller.FolderController;
import shiftscope.controller.TrackController;
import shiftscope.criteria.FolderCriteria;
import shiftscope.criteria.TrackCriteria;
import shiftscope.dto.FolderDTO;
import shiftscope.dto.SearchDTO;
import shiftscope.model.Folder;
import shiftscope.model.Track;
import shiftscope.netservices.HTTPService;
import shiftscope.netservices.TCPService;
import shiftscope.player.Music;
import shiftscope.util.Constants;
import shiftscope.util.Operation;
import shiftscope.util.OperationType;
import shiftscope.util.SessionConstants;
import shiftscope.util.Sync;

/**
 *
 * @author VeronicaEncinales
 */
public class HomePage extends javax.swing.JFrame {

    //GUI Variables
    //IMAGES
    ImageIcon musicIcon;
    ImageIcon folderIcon;
    //
    Font serifFont = new Font("sans-serif", Font.PLAIN, 14);
    Font serifFontArtist = new Font("sans-serif", Font.PLAIN, 12);
    private TimeCounter timeCounter;
    public TCPService webSocket;
    private Gson JSONParser;
    private JFileChooser fileChooser;
    private float totalFiles;
    private float currentFileScanned;
    private float currentSecond;
    private int currentFolder;

    //Player Variables
    private boolean playlistPlaying;
    private int currentSongPosition;
    private ArrayList<Track> queuePaths;
    private Track currentSong;
    private Sync sync;
    private Music player;
    
    private int layoutWidth;
    
    private FolderDTO folderContent;

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
            progressBar.setVisible(true);
            System.out.println("Fetching your files...");
            for (File f : fileChooser.getSelectedFiles()) {
                buildFolder(f, -1);
            }
            progressBar.setVisible(false);
            getFolderContent(-1);
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
            System.out.println("ShiftScope has finished...");
        }
    }

    public class TimeCounter extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {

            String duration = getSync().getCurrentSongDuration();
            totalTime.setText(duration);
            float totalSeconds = (Integer.parseInt(duration.split(":")[0]) * 60) + Integer.parseInt(duration.split(":")[1]);
            currentSecond = 0;
            //currentSongTimeSlider.setMaximum((int) totalSeconds);
            //currentSongTimeSlider.setValue(0);
            while (true) {
                if (isPlaying()) {
                    int minutes = (int) (currentSecond / 60);
                    int seconds = (int) (currentSecond % 60);
                    String elapsedtTimeString = minutes + ":" + String.format("%02d", seconds);
                    elapsedTime.setText(elapsedtTimeString);
                    currentSecond++;
                    //currentSongTimeSlider.setValue((int) currentSecond);
                    if (Math.abs(currentSecond - totalSeconds) <= 5 && !player.isMerging()) {
                        merge();
                    }
                    Thread.sleep(1000);
                } else if (isPaused()) {
                    Thread.sleep(1);
                } else {
                    break;
                }
            }
            return null;
        }

        @Override
        protected void done() {
            if (player.songHasFinished()) {
                next();
            }
        }
    }

    public HomePage() {
        initComponents();
        this.setLocationRelativeTo(null);
        progressBar.setVisible(false);
        foldersScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        initPlayer();
        try {
            System.out.println("Conectando........");
            webSocket = new TCPService(new URI(Constants.SOCKET_SERVER));
            webSocket.connect();
        } catch (URISyntaxException ex) {
            Logger.getLogger(ShiftScope.class.getName()).log(Level.SEVERE, null, ex);
        }
        musicIcon = createImageIcon("music.png", "music_icon");
        folderIcon = createImageIcon("folder.png", "music_icon");
        

        PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent changeEvent) {
                String propertyName = changeEvent.getPropertyName();
                if (propertyName.equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY)) {
                    calculateDifference();
                    drawFetchedFolder(folderContent);
                }
            }
        };

        //jSplitPane1.addPropertyChangeListener(propertyChangeListener);
        
        volumeSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if(source.getValueIsAdjusting()) {
                    setVolumeFromValue(source.getValue());
                }
            }
        });
        
        calculateDifference();
        getFolderContent(-1);
        drawPlaylist();
    }
    
    private void calculateDifference() {
        layoutWidth = jSplitPane1.getDividerLocation() - 22;
        //difference = this.getWidth() - jSplitPane1.getDividerLocation();
    }

    public void playSong(Track t, boolean playedFromPlaylist) {
        currentSong = t;
        if (playedFromPlaylist) {
            getPosition(t);
        }
        player.loadSong(t.getPath());

        sync.setCurrentSongId(t.getId());
        sync.setCurrentSongName(t.getTitle());
        sync.setCurrentSongArtist(t.getArtist());
        sync.setCurrentSongDuration(t.getDuration());
        sync.setIsPlaying(true);
        sync.setIsPaused(false);

        currentSongLabel.setText(t.getTitle() + " - " + t.getArtist());

        if (timeCounter != null) {
            timeCounter.cancel(true);
        }

        player.play();
        timeCounter = new TimeCounter();
        timeCounter.execute();
        Operation request = new Operation();
        request.setOperationType(OperationType.SYNC);
        request.setUserId(SessionConstants.USER_ID);
        request.setSync(sync);

        webSocket.sendRequest(request);
        playlistPlaying = playedFromPlaylist;
    }

    public void merge() {
        Track t;
        if (playlistPlaying) {
            if (currentSongPosition < queuePaths.size() - 1) {
                currentSongPosition++;
                currentSong = queuePaths.get(currentSongPosition);
                t = currentSong;
                player.loadSong(t.getPath());
                sync.setCurrentSongId(t.getId());
                sync.setCurrentSongName(t.getTitle());
                sync.setCurrentSongArtist(t.getArtist());
                sync.setCurrentSongDuration(t.getDuration());
                sync.setIsPlaying(true);
                sync.setIsPaused(false);

                currentSongLabel.setText(t.getTitle() + " - " + t.getArtist());

                if (timeCounter != null) {
                    timeCounter.cancel(true);
                }

                player.determineLine();
                timeCounter = new TimeCounter();
                timeCounter.execute();
                Operation request = new Operation();
                request.setOperationType(OperationType.SYNC);
                request.setUserId(SessionConstants.USER_ID);
                request.setSync(sync);

                webSocket.sendRequest(request);
                playlistPlaying = true;
            }
        }

    }

    public void getPosition(Track t) {
        for (int i = 0; i < queuePaths.size(); i++) {
            if (t.equals(queuePaths.get(i))) {
                currentSongPosition = i;
                break;
            }
        }
    }

    public void removeFromPlaylist(int position) {
        queuePaths.remove(position);
        drawPlaylist();
        Operation request = new Operation();
        request.setOperationType(OperationType.SYNC);
        request.setUserId(SessionConstants.USER_ID);
        sync.setCurrentPlaylist(queuePaths);
        request.setSync(sync);
    }

    public void playPlaylist() {
        currentSongPosition = 0;
        currentSong = queuePaths.get(currentSongPosition);
        playSong(currentSong, true);
    }

    public void resume() {
        player.pause();
    }

    public void pause() {
        player.pause();
        sync.setIsPlaying(isPlaying());
        sync.setIsPaused(!isPlaying());
    }

    public void stop() {
        player.stop();
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
        player.mute();
    }

    public void volumeDown() {
        player.volumeDown();
    }

    public void volumeUp() {
        player.volumeUp();
    }
    
    public void setVolumeFromValue(float value) {
        player.setVolumeFromValue(value);
    }

    public void setVolume(float value) {
        player.setVolume(value);
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public boolean isPaused() {
        return player.isPaused();
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

    public void dequeueSong(int position) {
        if (queuePaths.get(position).equals(currentSong)) {
            next();
        }

        queuePaths.remove(position);
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

    public final void initPlayer() {
        player = new Music(this);
        queuePaths = new ArrayList<>();
        currentSong = null;
        currentSongPosition = 0;
        playlistPlaying = false;
        sync = new Sync();
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

    private void buildFolder(File folder, int parentId) {
        JSONParser = new Gson();
        Folder createdFolder;
        Folder newFolder = new Folder();
        newFolder.setPath(folder.getAbsolutePath());
        newFolder.setTitle(folder.getName());
        newFolder.setParentFolder(parentId);
        newFolder.setLibrary(SessionConstants.LIBRARY_ID);
        HttpResponse response = FolderController.createFolder(newFolder);
        if (response.getStatusLine().getStatusCode() == 200) {
            try {
                createdFolder = JSONParser.fromJson(HTTPService.parseContent(response.getEntity().getContent()), Folder.class);
                parentId = createdFolder.getId();
                Mp3File mp3;
                File files[] = folder.listFiles();
                currentFileScanned++;
                progressBar.setValue((int) currentFileScanned);
                for (File f : files) {
                    if (f.isDirectory()) {
                        buildFolder(f, parentId);
                    } else if (f.getName().endsWith(".mp3") && !f.isHidden()) {
                        Track track = new Track();
                        try {
                            mp3 = new Mp3File(f.getAbsolutePath());
                            File file = new File(f.getAbsolutePath());
                            AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(file);
                            Map properties = baseFileFormat.properties();
                            Long duration1 = (Long) properties.get("duration");
                            int mili = (int) (duration1 / 1000);
                            int sec = (int) (mili / 1000) % 60;
                            int min = (int) (mili / 1000) / 60;
                            track.setDuration(min + ":" + String.format("%02d", sec));
                            if (mp3.hasId3v1Tag()) {
                                ID3v1 id3v1Tag = mp3.getId3v1Tag();
                                if (id3v1Tag.getTitle().equals("")) {
                                    track.setTitle(f.getName());
                                } else {
                                    track.setTitle(id3v1Tag.getTitle());
                                }

                                if (id3v1Tag.getArtist().equals("")) {
                                    track.setArtist("Unknown");
                                } else {
                                    track.setArtist(id3v1Tag.getArtist());
                                }
                            } else {
                                track.setTitle(f.getName());
                                track.setArtist("Unknown");
                            }
                        } catch (Exception ex) {
                            continue;
                        }
                        track.setPath(f.getAbsolutePath());
                        track.setParentFolder(parentId);
                        track.setLibrary(SessionConstants.LIBRARY_ID);
                        TrackController.createTrack(track);
                        currentFileScanned++;
                        progressBar.setValue((int) currentFileScanned);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalStateException ex) {
                Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void drawFetchedFolder(FolderDTO fetchedFolder) {
        int totalElements = fetchedFolder.getFolders().size() + fetchedFolder.getTracks().size();

        folderPane.removeAll();
        folderPane.setPreferredSize(new Dimension(layoutWidth, totalElements * 45));
        foldersScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

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
                    g.drawRoundRect(0, 0, layoutWidth-2, 43, 3, 3);
                }

            };
            folderPanel.addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        getFolderContent(folder.getId());
                    } else if (e.getButton() == MouseEvent.BUTTON3) {

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
            folderLabel.setBounds(45, 0, layoutWidth-35, 45);

            folderPanel.add(iconLabel);
            folderPanel.add(folderLabel);
            folderPane.add(folderPanel);

        }
        delta = folders.size();
        for (int i = 0; i < tracks.size(); i++) {
            final Track track = tracks.get(i);
            final JPanel trackPanel = new JPanel() {
                @Override
                public void paint(Graphics g) {
                    super.paint(g);
                    g.setColor(Color.GRAY);
                    g.drawRoundRect(0, 0, layoutWidth-2, 43, 3, 3);
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
            trackLabel.setBounds(35, 0, layoutWidth-35, 20);
            artistLabel.setBounds(38, 20, layoutWidth-38, 20);

            trackPanel.add(iconLabel);
            trackPanel.add(trackLabel);
            trackPanel.add(artistLabel);
            folderPane.add(trackPanel);

        }
        foldersScrollPane.revalidate();
        foldersScrollPane.repaint();

    }

    private void drawSearchResults(ArrayList<Track> tracks) {
        int totalElements = tracks.size();
        folderPane.removeAll();
        folderPane.setPreferredSize(new Dimension(foldersScrollPane.getWidth(), totalElements * 35));
        foldersScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        int delta = 0;
        for (int i = 0; i < tracks.size(); i++) {
            final Track track = tracks.get(i);
            final JPanel trackPanel = new JPanel() {

                @Override
                public void paint(Graphics g) {
                    super.paint(g);
                    g.setColor(Color.GRAY);
                    g.drawRoundRect(0, 0, 700, 35, 3, 3);
                }

            };
            trackPanel.addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        playSong(track, false);
                    } else if (e.getButton() == MouseEvent.BUTTON3) {
                        JPopupMenu popupMenu = new JPopupMenu();
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
            trackPanel.setBounds(0, (i * 35) + delta, 700, 35);
            JLabel trackLabel = new JLabel(track.getTitle());
            trackLabel.setFont(serifFont);
            JLabel artistLabel = new JLabel(track.getArtist());
            artistLabel.setFont(serifFont);
            JLabel iconLabel = new JLabel();
            iconLabel.setIcon(musicIcon);

            iconLabel.setBounds(15, 0, 35, 35);
            trackLabel.setBounds(50, 0, 300, 35);
            artistLabel.setBounds(350, 0, 300, 35);

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
                    g.drawRoundRect(0, 0, 230-2, 43, 3, 3);
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
                                dequeueSong(position);
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
            trackLabel.setBounds(35, 0, 230-35, 20);
            artistLabel.setBounds(38, 20, 230-38, 20);

            trackPanel.add(iconLabel);
            trackPanel.add(trackLabel);
            trackPanel.add(artistLabel);
            playlistPanel.add(trackPanel);

        }
        playlistScrollPane.revalidate();
        playlistScrollPane.repaint();

    }

    private void getFolderContent(int id) {
        currentFolder = id;
        HttpResponse response;
        folderContent = new FolderDTO();
        Folder parentFolder;
        JSONParser = new GsonBuilder().create();
        FolderCriteria criteria = new FolderCriteria();
        criteria.setId(id);
        criteria.setLibrary(SessionConstants.LIBRARY_ID);
        response = FolderController.getFolderParentId(criteria);
        try {
            if (response.getStatusLine().getStatusCode() == 200) {
                parentFolder = JSONParser.fromJson(HTTPService.parseContent(response.getEntity().getContent()), Folder.class);

                folderContent.setParentFolder(parentFolder.getParentFolder());
                SessionConstants.PARENT_FOLDER_ID = parentFolder.getParentFolder();
            } else {
                SessionConstants.PARENT_FOLDER_ID = -1;
            }
        } catch (IOException ex) {
            Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
        }
        folderPane.removeAll();
        int currentPage = 1;
        criteria = new FolderCriteria();
        criteria.setId(id);
        criteria.setLibrary(SessionConstants.LIBRARY_ID);
        response = FolderController.getFolderFoldersById(criteria);
        try {
            if (response.getStatusLine().getStatusCode() == 200) {
                do {
                    folderContent.addFolders(JSONParser.fromJson(HTTPService.parseContent(response.getEntity().getContent()), new TypeToken<List<Folder>>() {
                    }.getType()));
                    currentPage++;
                    criteria.setPage(currentPage);
                    response = FolderController.getFolderFoldersById(criteria);
                } while (response.getStatusLine().getStatusCode() != 404);
            }

            currentPage = 1;
            criteria.setPage(currentPage);
            response = FolderController.getFolderTracksById(criteria);
            if (response.getStatusLine().getStatusCode() == 200) {
                do {
                    folderContent.addTracks(JSONParser.fromJson(HTTPService.parseContent(response.getEntity().getContent()), new TypeToken<List<Track>>() {
                    }.getType()));
                    currentPage++;
                    criteria.setPage(currentPage);
                    response = FolderController.getFolderTracksById(criteria);
                } while (response.getStatusLine().getStatusCode() != 404);
            }
            drawFetchedFolder(folderContent);
        } catch (IOException ex) {
            Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
        }

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
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        searchTextField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
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

        jToolBar1.setRollover(true);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ShiftScope");
        setBackground(new java.awt.Color(254, 254, 254));
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

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/shiftscope/view/icon.png"))); // NOI18N
        jLabel1.setAlignmentX(-0.0F);
        jPanel4.add(jLabel1);

        searchTextField.setForeground(new java.awt.Color(204, 204, 204));
        searchTextField.setPreferredSize(new java.awt.Dimension(350, 27));
        searchTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchTextFieldKeyReleased(evt);
            }
        });
        jPanel4.add(searchTextField);

        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });
        jPanel4.add(searchButton);

        backButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/shiftscope/view/back.png"))); // NOI18N
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                backButtonMouseClicked(evt);
            }
        });
        jPanel3.add(backButton);

        selectFolderButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/shiftscope/view/folder-o.png"))); // NOI18N
        selectFolderButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectFolderButtonMouseClicked(evt);
            }
        });
        jPanel3.add(selectFolderButton);

        jPanel2.setPreferredSize(new java.awt.Dimension(150, 40));

        backBtn.setBackground(new java.awt.Color(34, 34, 34));
        backBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        backBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/shiftscope/view/backward.png"))); // NOI18N
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

        pauseBtn.setBackground(new java.awt.Color(34, 34, 34));
        pauseBtn.setForeground(new java.awt.Color(254, 254, 254));
        pauseBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pauseBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/shiftscope/view/pause.png"))); // NOI18N
        pauseBtn.setOpaque(true);
        pauseBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        pauseBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pauseBtnMouseClicked(evt);
            }
        });
        jPanel2.add(pauseBtn);

        stopBtn.setBackground(new java.awt.Color(34, 34, 34));
        stopBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        stopBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/shiftscope/view/stop.png"))); // NOI18N
        stopBtn.setOpaque(true);
        stopBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        stopBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                stopBtnMouseClicked(evt);
            }
        });
        jPanel2.add(stopBtn);

        nextBtn.setBackground(new java.awt.Color(34, 34, 34));
        nextBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nextBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/shiftscope/view/forward.png"))); // NOI18N
        nextBtn.setOpaque(true);
        nextBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        nextBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nextBtnMouseClicked(evt);
            }
        });
        jPanel2.add(nextBtn);
        jPanel2.add(songNameLabel);

        volumeSlider.setMaximum(0);
        volumeSlider.setMinimum(-70);
        jPanel6.add(volumeSlider);

        elapsedTime.setText("0:00");
        jPanel1.add(elapsedTime);

        totalTime.setText("0:00");
        jPanel1.add(totalTime);

        currentSongLabel.setFont(new java.awt.Font("SansSerif", 0, 15)); // NOI18N
        jPanel5.add(currentSongLabel);

        toolBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.add(progressBar);

        jSplitPane1.setBorder(null);
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
                            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 602, Short.MAX_VALUE)))
                        .addContainerGap())))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 673, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(630, Short.MAX_VALUE)
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

        pause();
        Operation request = new Operation();
        request.setOperationType(OperationType.SYNC);
        request.setUserId(SessionConstants.USER_ID);
        request.setSync(getSync());
        webSocket.sendRequest(request);
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

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        try {
            int currentPage = 1;
            SearchDTO searchResults = new SearchDTO();
            TrackCriteria criteria = new TrackCriteria();
            String word = URLEncoder.encode(searchTextField.getText(), "UTF-8");
            criteria.setWord(word);
            criteria.setLibrary(SessionConstants.LIBRARY_ID);
            criteria.setPage(currentPage);
            JSONParser = new GsonBuilder().create();
            HttpResponse response = TrackController.searchTrack(criteria);
            if (response.getStatusLine().getStatusCode() == 200) {
                do {
                    try {
                        searchResults.addTracks(JSONParser.fromJson(HTTPService.parseContent(response.getEntity().getContent()), new TypeToken<List<Track>>() {
                        }.getType()));
                        currentPage++;
                        criteria.setPage(currentPage);
                        response = TrackController.searchTrack(criteria);
                    } catch (IOException ex) {
                        Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalStateException ex) {
                        Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } while (response.getStatusLine().getStatusCode() != 404);
                drawSearchResults(searchResults.getTracks());
            } else if (response.getStatusLine().getStatusCode() == 404) {
                folderPane.removeAll();
                foldersScrollPane.revalidate();
                foldersScrollPane.repaint();
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_searchButtonActionPerformed

    private void searchTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchTextFieldKeyReleased
        if (searchTextField.getText().length() == 0) {
            getFolderContent(currentFolder);
        }
    }//GEN-LAST:event_searchTextFieldKeyReleased

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        webSocket.close();
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel backBtn;
    private javax.swing.JLabel backButton;
    private javax.swing.JLabel currentSongLabel;
    private javax.swing.JLabel elapsedTime;
    private javax.swing.JPanel folderPane;
    private javax.swing.JScrollPane foldersScrollPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel nextBtn;
    private javax.swing.JLabel pauseBtn;
    private javax.swing.JPanel playlistPanel;
    private javax.swing.JScrollPane playlistScrollPane;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchTextField;
    private javax.swing.JLabel selectFolderButton;
    private javax.swing.JLabel songNameLabel;
    private javax.swing.JLabel stopBtn;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JLabel totalTime;
    private javax.swing.JSlider volumeSlider;
    // End of variables declaration//GEN-END:variables

}
