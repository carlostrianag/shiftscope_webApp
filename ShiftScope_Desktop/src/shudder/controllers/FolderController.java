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
import java.awt.Event;
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
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.SwingWorker;
import shudder.criteria.FolderCriteria;
import shudder.dto.FolderCreationDTO;
import shudder.dto.FolderDTO;
import shudder.listeners.FolderListener;
import shudder.model.Folder;
import shudder.model.Track;
import shudder.netservices.HTTPService;
import shudder.util.Events;
import shudder.util.SessionConstants;
import shudder.util.comparators.ArtistComparator;
import shudder.util.comparators.TitleComparator;
import shudder.views.HomePage;

/**
 *
 * @author carlos
 */
public class FolderController {
    
    private static ArrayList<FolderListener> listeners = new ArrayList<>();
    private static FolderDTO folderContent;
    private static Gson JSONParser;
    private static int currentFileScanned = 0;
    private static int totalFiles;
    private static boolean orderBySongName = true;
    private static boolean orderByArtists = false;
    
    
    public static void addListener(FolderListener listener) {
        listeners.add(listener);
    }
    
    public static Response createFolder(Folder folder){
        JSONParser = new Gson();
        String object = JSONParser.toJson(folder);
        return HTTPService.HTTPSyncPost("/folder/create", object);
    }
    
    public static void createFolderTracks(FolderCreationDTO folderTracks) {
        AsyncCompletionHandler<Void> responseHandler = new AsyncCompletionHandler<Void>() {

            @Override
            public Void onCompleted(Response response) throws Exception {
                System.out.println("Tracks creados asincronicamente ....");
                return null;
            }
        };
        JSONParser = new Gson();
        String object = JSONParser.toJson(folderTracks);
        System.out.println("Entró...");
        HTTPService.HTTPPost("/folder/createFolderTracks", object, responseHandler);
    }    
    
    public static Response createFolderOptimized(FolderCreationDTO folder) {
        JSONParser = new Gson();
        String object = JSONParser.toJson(folder);
        return HTTPService.HTTPSyncPost("/folder/createOptimized", object);
    }
    
    public static void getFolderContentById(FolderCriteria criteria) {
        AsyncCompletionHandler<Void> responseHandler = new AsyncCompletionHandler<Void>() {
            @Override
            public Void onCompleted(Response response) throws Exception {
                if (response.getStatusCode() == 200) {
                    JSONParser = new Gson();
                    folderContent = JSONParser.fromJson(response.getResponseBody(), FolderDTO.class);
                    SessionConstants.PARENT_FOLDER_ID = folderContent.getParentFolder();
                    new FolderWorker(Events.ON_CONTENT_FETCHED).execute();
                } else {
                    SessionConstants.PARENT_FOLDER_ID = -1;
                }
                return null;
            }
        };
        HTTPService.HTTPGet("/folder/getFolderContentById?id="+criteria.getId()+"&library="+criteria.getLibrary(), responseHandler);
        for (FolderListener listener : listeners) {
            listener.fetchingContent();
        }
    }
    
    public static void drawFolder() {
        new FolderWorker(Events.ON_DRAW_FOLDER).execute();
    }
    
    public static void buildFolder(File[] files) {
        new FolderWorker(Events.BUILD_FOLDER_HIERARCHY, files).execute();
    }
    
    public static void buildFolderHierarchy(File[] files){
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
    
    
    public static void orderTracksBySongName() {
        orderBySongName = true;
        orderByArtists = false;
        new FolderWorker(Events.ON_ORDER_TRACKS).execute();
    }
    
    public static void orderTracksByArtistName() {
        orderByArtists = true;
        orderBySongName = false;
        new FolderWorker(Events.ON_ORDER_TRACKS).execute();
    }
    
    public static void search(String text) {     
        new FolderWorker(Events.ON_SEARCH, text).execute();
    }
    
    private static void buildFolderOptimized(File folder, int parentId) {
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
                    for(FolderListener listener : listeners) {
                        listener.OnProgressUpdated(currentFileScanned);
                    }
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
    
    private static Folder createFolderByLimit(FolderCreationDTO folder, int trackLimit) {
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
                    } while (start < total);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return createdFolder;
    }
        
    private static void countFiles(File selectedFile) {
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

    
    private static class FolderWorker extends SwingWorker<Void, Void> {

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
