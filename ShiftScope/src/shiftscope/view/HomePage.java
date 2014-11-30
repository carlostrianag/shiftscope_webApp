/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shiftscope.view;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;
import org.apache.http.HttpResponse;
import org.farng.mp3.MP3File;
import shiftscope.controller.FolderController;
import shiftscope.controller.TrackController;
import shiftscope.criteria.FolderCriteria;
import shiftscope.dto.FolderDTO;
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

    /**
     * Creates new form HomePage
     */
    
    //GUI Variables
    private TimeCounter timeCounter;
    public TCPService webSocket;
    private Gson JSONParser;
    private JFileChooser fileChooser;
    private float totalFiles;
    private float currentFileScanned;
    private float currentSecond;

    
    //Player Variables
    
    private float volumeValue;
    private boolean playlistPlaying;
    private int currentSongPosition;    
    private ArrayList<Track> queuePaths;
    private Track currentSong;
    private Sync sync;
    private Music player;
    
    
    public class FolderBuilder extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            totalFiles = 0;
            currentFileScanned = 0;
            File [] selectedFiles = fileChooser.getSelectedFiles();
            for(File f : selectedFiles){
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
            float totalSeconds = (Integer.parseInt(duration.split(":")[0])*60)+Integer.parseInt(duration.split(":")[1]);
            System.out.println(totalSeconds);
            currentSecond = 0;
            currentSongTimeSlider.setMaximum((int)totalSeconds);
            currentSongTimeSlider.setValue(0);
            while(true){
                if(isPlaying()) {
                    int minutes = (int)(currentSecond/60);
                    int seconds = (int)(currentSecond%60);
                    String elapsedtTimeString = minutes+ ":"+String.format("%02d", seconds);
                    elapsedTime.setText(elapsedtTimeString);                    
                    currentSecond++;
                    currentSongTimeSlider.setValue((int)currentSecond);
                    Thread.sleep(1000);
                } else if(isPaused()){
                    Thread.sleep(1);
                } else {
                    break;
                }
            }
            return null;
        }

        @Override
        protected void done() {
            System.out.println("Song Finished or Stopped...");
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
        
        getFolderContent(-1);
        
    }
   

    public void playSong(Track t) {  
        if(player.isPlaying()){
            player.pause();
        }
        currentSong = t;
        volumeValue = player.getVolumeValue();
        player = new Music();
        player.setVolumeValue(volumeValue);
        player.loadFile(t.getPath());
        
        sync.setCurrentSongId(t.getId());
        sync.setCurrentSongName(t.getTitle());
        sync.setCurrentSongArtist(t.getArtist());
        sync.setCurrentSongDuration(t.getDuration());
        sync.setIsPlaying(true);
        sync.setIsPaused(false);
        
        currentSongLabel.setText(t.getTitle() + " - " + t.getArtist());
        
        if(timeCounter != null){
            timeCounter.cancel(true);
            System.out.println("Cancelado");
        }
        timeCounter = new TimeCounter();
        timeCounter.execute();
        
        player.play();
        
        Operation request = new Operation();
        request.setOperationType(OperationType.SYNC);
        request.setUserId(SessionConstants.USER_ID);
        request.setSync(sync);
        
        webSocket.sendRequest(request);
        playlistPlaying = false;
    }

    public void playSongFromPlaylist(int pos) {
        currentSongPosition = pos;
        currentSong = queuePaths.get(currentSongPosition);
        playSong(currentSong);
        playlistPlaying = true;
    }
    
    public void playPlaylist() {
        currentSongPosition = 0;
        currentSong = queuePaths.get(currentSongPosition);
        playSong(currentSong);
        playlistPlaying = true;
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
    
    public boolean next() {
        if(currentSongPosition < queuePaths.size()-1) {
            currentSongPosition++;
            currentSong = queuePaths.get(currentSongPosition);
            playSong(currentSong);
            playlistPlaying = true;
            return true;
        }
        return false;
    }
    
    public boolean back() {
        if(currentSongPosition > 0) {
            currentSongPosition--;
            currentSong = queuePaths.get(currentSongPosition);
            playSong(currentSong);
            playlistPlaying = true;
            return true;
        }
        return false;
        
    }

    public void mute() {
        player.mute();
    }
    
    public void volumeDown(){
        player.volumeDown();
    }
    
    public void volumeUp() {
        player.volumeUp();
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }
    
    public boolean isPaused() {
        return player.isPaused();
    }
    
    
    public void enqueueSong(Track q) {
        queuePaths.add(q);
        System.out.println(queuePaths.toString());
    }   
    
    public Sync getSync(){
        return sync;
    }

    public final void initPlayer() {
        player = new Music();
        player.setVolumeValue(volumeValue);
        queuePaths = new ArrayList<>();
        volumeValue = 0;
        currentSong = null;
        currentSongPosition = 0;
        playlistPlaying = false;
        sync = new Sync();
    }  

    
    private void countFiles(File selectedFile) {
        File[] selectedFiles;
        if(selectedFile.isDirectory() && !selectedFile.isHidden()){
            selectedFiles = selectedFile.listFiles();
            for(File f : selectedFiles){
                totalFiles++;
                countFiles(f);
            }
        } else if(!selectedFile.isFile() && !selectedFile.isHidden() && selectedFile.getName().endsWith(".mp3")) {
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
                MP3File mp3;
                File files[] = folder.listFiles();
                currentFileScanned++;
                progressBar.setValue((int) currentFileScanned);
                for (File f : files) {
                    if (f.isDirectory()) {
                        buildFolder(f, parentId);
                    } else if (f.getName().endsWith(".mp3") && !f.isHidden()) {
                        Track track = new Track();
                        try {
                            File file = f;
                            AudioFileFormat baseFileFormat;
                            baseFileFormat = new MpegAudioFileReader().getAudioFileFormat(file);
                            Map properties = baseFileFormat.properties();
                            Long duration = (Long) properties.get("duration");
                            int mili = (int) (duration / 1000);
                            int sec = (mili / 1000) % 60;
                            int min = (mili / 1000) / 60;
                            track.setDuration(min + ":" + sec);

                        } catch (UnsupportedAudioFileException ex) {
                            Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        try {
                            mp3 = new MP3File(f);
                        } catch (Exception ex) {
                            continue;
                        }
                        try {
                            if (!mp3.getID3v1Tag().getSongTitle().equals("")) {
                                track.setTitle(mp3.getID3v1Tag().getSongTitle());
                                track.setArtist(mp3.getID3v1Tag().getArtist());
                            } else {
                                track.setTitle(f.getName());
                            }

                        } catch (Exception ex) {
                            track.setTitle(f.getName());
                        }
                        track.setPath(f.getAbsolutePath());
                        track.setParentFolder(parentId);
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

        folderPane.setPreferredSize(new Dimension(foldersScrollPane.getWidth(), totalElements * 35));
        foldersScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        ArrayList<Folder> folders = fetchedFolder.getFolders();
        ArrayList<Track> tracks = fetchedFolder.getTracks();
        int delta = 0;
        for (int i = 0; i < folders.size(); i++) {
            final Folder folder = folders.get(i);
            JLabel folderLabel = new JLabel(folder.getTitle());
            JLabel iconLabel = new JLabel("F");
            iconLabel.setBounds(0, (i * 35) + delta, 35, 35);
            iconLabel.setBorder(BorderFactory.createLineBorder(Color.black));
            folderLabel.setBounds(50, (i * 35) + delta, 300, 35);
            folderLabel.setBorder(BorderFactory.createLineBorder(Color.black));
            folderLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    getFolderContent(folder.getId());
                }
            });
            folderPane.add(iconLabel);
            folderPane.add(folderLabel);
        }
        delta = folders.size() * 35;
        for (int i = 0; i < tracks.size(); i++) {
            final Track track = tracks.get(i);
            JLabel folderLabel = new JLabel(track.getTitle());
            JLabel iconLabel = new JLabel("T");
            iconLabel.setBounds(0, (i * 35) + delta, 35, 35);
            iconLabel.setBorder(BorderFactory.createLineBorder(Color.black));
            folderLabel.setBounds(50, (i * 35) + delta, 300, 35);
            folderLabel.setBorder(BorderFactory.createLineBorder(Color.red));
            folderLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    playSong(track);
                }

            });
            folderPane.add(iconLabel);
            folderPane.add(folderLabel);
        }
        foldersScrollPane.revalidate();
        foldersScrollPane.repaint();

    }

    private void getFolderContent(int id) {
        HttpResponse response;
        FolderDTO folderContent = new FolderDTO();
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
    
    public void syncGUI(){
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        searchTextField = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        backButton = new javax.swing.JLabel();
        selectFolderButton = new javax.swing.JLabel();
        foldersScrollPane = new javax.swing.JScrollPane();
        folderPane = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        backBtn = new javax.swing.JLabel();
        pauseBtn = new javax.swing.JLabel();
        stopBtn = new javax.swing.JLabel();
        nextBtn = new javax.swing.JLabel();
        volumeDownBtn = new javax.swing.JLabel();
        volumeUpBtn = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        songNameLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        elapsedTime = new javax.swing.JLabel();
        currentSongTimeSlider = new javax.swing.JSlider();
        totalTime = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        currentSongLabel = new javax.swing.JLabel();
        toolBar = new javax.swing.JToolBar();
        progressBar = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setBounds(new java.awt.Rectangle(0, 0, 865, 654));
        setMinimumSize(new java.awt.Dimension(800, 600));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/shiftscope/view/icon.png"))); // NOI18N
        jLabel1.setAlignmentX(-0.0F);
        jPanel4.add(jLabel1);

        searchTextField.setForeground(new java.awt.Color(204, 204, 204));
        searchTextField.setText("Search");
        searchTextField.setPreferredSize(new java.awt.Dimension(350, 27));
        searchTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchTextFieldActionPerformed(evt);
            }
        });
        jPanel4.add(searchTextField);

        backButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/shiftscope/view/Arrow.png"))); // NOI18N
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                backButtonMouseClicked(evt);
            }
        });
        jPanel3.add(backButton);

        selectFolderButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/shiftscope/view/Folder.png"))); // NOI18N
        selectFolderButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectFolderButtonMouseClicked(evt);
            }
        });
        jPanel3.add(selectFolderButton);

        foldersScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        folderPane.setBackground(new java.awt.Color(254, 254, 254));

        javax.swing.GroupLayout folderPaneLayout = new javax.swing.GroupLayout(folderPane);
        folderPane.setLayout(folderPaneLayout);
        folderPaneLayout.setHorizontalGroup(
            folderPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1497, Short.MAX_VALUE)
        );
        folderPaneLayout.setVerticalGroup(
            folderPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 835, Short.MAX_VALUE)
        );

        foldersScrollPane.setViewportView(folderPane);

        jPanel2.setBorder(null);

        backBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/shiftscope/view/control_05.png"))); // NOI18N
        jPanel2.add(backBtn);

        pauseBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/shiftscope/view/pause-normal.png"))); // NOI18N
        pauseBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pauseBtnMouseClicked(evt);
            }
        });
        jPanel2.add(pauseBtn);

        stopBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/shiftscope/view/stop-normal.png"))); // NOI18N
        stopBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                stopBtnMouseClicked(evt);
            }
        });
        jPanel2.add(stopBtn);

        nextBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/shiftscope/view/control_04.png"))); // NOI18N
        jPanel2.add(nextBtn);

        volumeDownBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/shiftscope/view/volumeDown.png"))); // NOI18N
        volumeDownBtn.setToolTipText("");
        volumeDownBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                volumeDownBtnMouseClicked(evt);
            }
        });
        jPanel2.add(volumeDownBtn);

        volumeUpBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/shiftscope/view/volumeUp.png"))); // NOI18N
        volumeUpBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                volumeUpBtnMouseClicked(evt);
            }
        });
        jPanel2.add(volumeUpBtn);

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/shiftscope/view/Playlist.png"))); // NOI18N
        jLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel9MouseClicked(evt);
            }
        });
        jPanel2.add(jLabel9);

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/shiftscope/view/focos.png"))); // NOI18N
        jPanel2.add(jLabel10);
        jPanel2.add(songNameLabel);

        elapsedTime.setText("0:00");
        jPanel1.add(elapsedTime);

        currentSongTimeSlider.setToolTipText("");
        currentSongTimeSlider.setValue(0);
        currentSongTimeSlider.setPreferredSize(new java.awt.Dimension(640, 62));
        jPanel1.add(currentSongTimeSlider);

        totalTime.setText("0:00");
        jPanel1.add(totalTime);

        jPanel5.add(currentSongLabel);

        toolBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.add(progressBar);

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
                            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(foldersScrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 787, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(foldersScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void searchTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchTextFieldActionPerformed

    private void jLabel9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel9MouseClicked
        PlaylistsBook nuevaVentana = new PlaylistsBook();
        nuevaVentana.setVisible(true);        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel9MouseClicked

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

    private void volumeDownBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_volumeDownBtnMouseClicked
        volumeDown();
    }//GEN-LAST:event_volumeDownBtnMouseClicked

    private void volumeUpBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_volumeUpBtnMouseClicked
        volumeUp();
    }//GEN-LAST:event_volumeUpBtnMouseClicked

    private void backButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backButtonMouseClicked
        getFolderContent(SessionConstants.PARENT_FOLDER_ID);
    }//GEN-LAST:event_backButtonMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel backBtn;
    private javax.swing.JLabel backButton;
    private javax.swing.JLabel currentSongLabel;
    private javax.swing.JSlider currentSongTimeSlider;
    private javax.swing.JLabel elapsedTime;
    private javax.swing.JPanel folderPane;
    private javax.swing.JScrollPane foldersScrollPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel nextBtn;
    private javax.swing.JLabel pauseBtn;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTextField searchTextField;
    private javax.swing.JLabel selectFolderButton;
    private javax.swing.JLabel songNameLabel;
    private javax.swing.JLabel stopBtn;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JLabel totalTime;
    private javax.swing.JLabel volumeDownBtn;
    private javax.swing.JLabel volumeUpBtn;
    // End of variables declaration//GEN-END:variables

}
