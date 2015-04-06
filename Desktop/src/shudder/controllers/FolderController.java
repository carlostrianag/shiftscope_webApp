/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shudder.controllers;

import com.beaglebuddy.mp3.MP3;
import com.google.gson.Gson;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.Response;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.SwingWorker;
import shudder.criteria.FolderCriteria;
import shudder.dto.FolderCreationDTO;
import shudder.dto.FolderDTO;
import shudder.listeners.FolderListener;
import shudder.listeners.LoginListener;
import shudder.model.Folder;
import shudder.model.Track;
import shudder.netservices.HTTPService;
import shudder.util.Events;
import shudder.util.SessionConstants;
import shudder.util.comparators.ArtistComparator;
import shudder.util.comparators.TitleComparator;

/**
 *
 * @author carlos
 */
public class FolderController {
    
    private ArrayList<FolderListener> listeners = new ArrayList<>();
    private FolderDTO folderContent;
    private Gson JSONParser;
    private int currentFileScanned = 0;
    private int totalFiles;
    private boolean orderBySongName = true;
    private boolean orderByArtists = false;
    private Stage stage;

    public FolderController(Stage stage) {
        this.stage = stage;
    }
    
    public void openFile() {
        
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Please choose a folder");
        File folder = fileChooser.showDialog(stage);
        
        if (folder != null) {
            File[] files = new File[1];
            files[0] = folder;
            buildFolder(files);
        }
        
            //buildFolder(fileChooser.ge);
    }
    
    
    public void addListener(FolderListener listener) {
        listeners.add(listener);
    }
    
    public Response createFolder(Folder folder){
        JSONParser = new Gson();
        String object = JSONParser.toJson(folder);
        return HTTPService.HTTPSyncPost("/folder/create", object);
    }
    
    public void createFolderTracks(FolderCreationDTO folderTracks) {
        AsyncCompletionHandler<Void> responseHandler = new AsyncCompletionHandler<Void>() {

            @Override
            public Void onCompleted(Response response) throws Exception {
                System.out.println("Tracks creados asincronicamente ....");
                return null;
            }
        };
        JSONParser = new Gson();
        String object = JSONParser.toJson(folderTracks);
        HTTPService.HTTPPost("/folder/createFolderTracks", object, responseHandler);
    }    
    
    public Response createFolderOptimized(FolderCreationDTO folder) {
        JSONParser = new Gson();
        String object = JSONParser.toJson(folder);
        return HTTPService.HTTPSyncPost("/folder/createOptimized", object);
    }
    
    public void deleteFolder(int id) {
        AsyncCompletionHandler<Void> responseHandler = new AsyncCompletionHandler<Void>() {

            @Override
            public Void onCompleted(Response response) throws Exception {
                String responseObject = response.getResponseBody();
                System.out.println(responseObject);
                for(FolderListener listener : listeners) {
                    listener.OnFolderDeleted(responseObject);
                }
                return null;
            }
        };
        HTTPService.HTTPDelete("/folder/destroy/"+id+"?populate=false", responseHandler);
    }
    
    public void getFolderContentById(String JSONFolderCriteria) {
        Gson JSONParser = new Gson();
        FolderCriteria criteria = JSONParser.fromJson(JSONFolderCriteria, FolderCriteria.class);
        criteria.setLibrary(SessionConstants.LIBRARY_ID);
        AsyncCompletionHandler<Void> responseHandler = new AsyncCompletionHandler<Void>() {
            @Override
            public Void onCompleted(Response response) throws Exception {
                if (response.getStatusCode() == 200) {
                    folderContent = JSONParser.fromJson(response.getResponseBody(), FolderDTO.class);
                    SessionConstants.PARENT_FOLDER_ID = folderContent.getParentFolder();
                    new FolderWorker(Events.ON_CONTENT_FETCHED).execute();
                } else {
                    for (FolderListener listener : listeners) {
                        listener.OnError("The folder could not be fetched.");                       
                    }
                    SessionConstants.PARENT_FOLDER_ID = -1;
                }
                return null;
            }

            @Override
            public void onThrowable(Throwable t) {
                super.onThrowable(t);
                for (FolderListener listener : listeners) {
                    listener.OnError("There is no internet connection, please check and try again.");
                }                
            }
            
            
        };
        HTTPService.HTTPGet("/folder/getFolderContentById?id="+criteria.getId()+"&library="+criteria.getLibrary(), responseHandler);
        for (FolderListener listener : listeners) {
            listener.OnLoading();
        }
    }
    
    public void drawFolder() {
        new FolderWorker(Events.ON_DRAW_FOLDER).execute();
    }
    
    public void buildFolder(File[] files) {
        new FolderWorker(Events.BUILD_FOLDER_HIERARCHY, files).execute();
    }
    
    public void buildFolderHierarchy(File[] files){
            totalFiles = 0;
            currentFileScanned = 0;
            File[] selectedFiles = files;
            for (File f : selectedFiles) {
                countFiles(f);
            }
            for(FolderListener listener : listeners) {
                listener.OnFilesScanned(totalFiles);
            }
            System.out.println(totalFiles);
            System.out.println("Fetching your files...");
            for (File f : selectedFiles) {
                buildFolderOptimized(f, -1);
            }
    }
    
    
    public void orderTracksBySongName() {
        orderBySongName = true;
        orderByArtists = false;
        new FolderWorker(Events.ON_ORDER_TRACKS).execute();
    }
    
    public void orderTracksByArtistName() {
        orderByArtists = true;
        orderBySongName = false;
        new FolderWorker(Events.ON_ORDER_TRACKS).execute();
    }
    
    public void search(String text) {
        new FolderWorker(Events.ON_SEARCH, text).execute();
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
                    currentFileScanned++;
                    for(FolderListener listener : listeners) {
                        listener.OnProgressUpdated(currentFileScanned);
                    }
                    tracks.add(track);
                } catch (IOException ex) {
                    System.out.println("Ommited: " + f.getName());
                } catch (UnsupportedAudioFileException ex) {
                    System.out.println("Error on: " + f.getName());
                }
            }
        }

        folderToCreate.setTracks(tracks);
        System.out.println("Create " + folder + "   " + new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
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

        Response response = createFolderOptimized(folder);
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
                        createFolderTracks(folder);
                    } while (start < total);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(FolderController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return createdFolder;
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

    
    private class FolderWorker extends SwingWorker<Void, Void> {

        private Events event;
        private File[] files;
        private String matchingCriteria;

        public FolderWorker(Events event) {
            this.event = event;
        }

        public FolderWorker(Events event, File[] files) {
            this.event = event;
            this.files = files;
        }

        public FolderWorker(Events event, String matchingCriteria) {
            this.event = event;
            this.matchingCriteria = matchingCriteria;
        }
        
        
        @Override
        protected Void doInBackground() throws Exception {

            switch(event) {
                case ON_CONTENT_FETCHED:
                    if (orderBySongName) {
                        Collections.sort(folderContent.getTracks(), new TitleComparator());
                    } else {
                        Collections.sort(folderContent.getTracks(), new ArtistComparator());
                    }                    
                    for (FolderListener listener : listeners) {
                        listener.OnContentFetched(folderContent);
                    }
                    break;
                case ON_DRAW_FOLDER:
                    if (orderBySongName) {
                        Collections.sort(folderContent.getTracks(), new TitleComparator());
                    } else {
                        Collections.sort(folderContent.getTracks(), new ArtistComparator());
                    }
                    for (FolderListener listener : listeners) {
                        listener.OnContentFetched(folderContent);
                    }                    
                    break;
                    
                case BUILD_FOLDER_HIERARCHY:
                    buildFolderHierarchy(files);
                    break;
                    
                case ON_ORDER_TRACKS:
                    if (orderBySongName) {
                        Collections.sort(folderContent.getTracks(), new TitleComparator());
                    } else {
                        Collections.sort(folderContent.getTracks(), new ArtistComparator());
                    }
                    for (FolderListener listener : listeners) {
                        listener.OnContentFetched(folderContent);
                    }                        
                    break;
                    
                case ON_SEARCH:
                    ArrayList<Track> tracks = new ArrayList<>(folderContent.getTracks()
                            .stream()
                            .filter(p -> p.getTitle()
                                    .toLowerCase()
                                    .contains(matchingCriteria.toLowerCase()) || p.getArtist()
                                            .toLowerCase()
                                            .contains(matchingCriteria.toLowerCase()))
                            .collect(Collectors.toList()));
                    for(FolderListener listener : listeners) {
                        listener.drawSearchResults(tracks);
                    }
                    break;
            }
            return null;
        }

        @Override
        protected void done() {
            switch(event) {
                case BUILD_FOLDER_HIERARCHY:
                    for(FolderListener listener : listeners) {
                        listener.OnBuildFolderFinished();
                    }
                    break;
            }
        }
    }
}
