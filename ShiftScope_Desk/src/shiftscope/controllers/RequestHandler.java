
package shiftscope.controllers;

import com.google.gson.Gson;
import shiftscope.model.LibraryElement;
import utils.Metadata;
import utils.Request;
import utils.RequestTypes;

/**
 *
 * @author carlos
 */
public class RequestHandler {
    private int userId;
    private String from;
    private int type;
    private int response;
    private Metadata content;
    private Gson JSONParser;
    private Metadata obj;

    public RequestHandler(Request request) {
        this.userId = request.getUserId();
        this.from = request.getFrom();
        this.type = request.getType();
        this.response = request.getResponse();
        this.content = request.getContent();
    }
    
    public Request handle() {
        Request r = new Request();
        Metadata m = new Metadata();
        String path;
        LibraryElement l;
        switch(type) {
            case RequestTypes.FETCH:
                System.out.println("Fetching");
                r.setUserId(124);
                r.setType(1);
                r.setFrom("DESKTOP");
                if(ShiftScopePlayer.isPlaying()){
                    path = ShiftScopePlayer.getCurrentLocation();
                    l = Handlers.getLibraryElementByAbsolutePath(path);
                    m.setCurrentSong(l.getTitle());
                    m.setCurrentArtist(l.getArtist());
                    m.setIsPlaying(true);
                }
                JSONParser = new Gson();
                obj = JSONParser.fromJson(JSONParser.toJson(content), Metadata.class);
                m.setLibrary(Handlers.fetchLibraryByParentFolder(obj.getParentFolder()));
                r.setContent(m);
                
                break;
            
            case RequestTypes.SELECT_FOLDERS:
                ViewHandler.selectFolders();
                System.out.println("Fetching");
                r.setUserId(124);
                r.setType(1);
                r.setFrom("DESKTOP");
                if(ShiftScopePlayer.isPlaying()){
                    path = ShiftScopePlayer.getCurrentLocation();
                    l = Handlers.getLibraryElementByAbsolutePath(path);
                    m.setCurrentSong(l.getTitle());
                    m.setCurrentArtist(l.getArtist());
                    m.setIsPlaying(true);
                }
                JSONParser = new Gson();
                obj = JSONParser.fromJson(JSONParser.toJson(content), Metadata.class);
                m.setLibrary(Handlers.fetchLibraryByParentFolder(obj.getParentFolder()));
                r.setContent(m);
                break;
                
            case RequestTypes.BACK_FOLDER:
                System.out.println("Backing...");
                r.setUserId(124);
                r.setType(15);
                r.setFrom("DESKTOP");
                JSONParser = new Gson();
                obj = JSONParser.fromJson(JSONParser.toJson(content), Metadata.class);
                String parent = Handlers.getParentFolderOf(obj.getCurrentFolder());
                m.setCurrentFolder(parent);
                m.setLibrary(Handlers.fetchLibraryByParentFolder(parent));
                r.setContent(m);
                break;
               
            case RequestTypes.PLAY:
                JSONParser = new Gson();
                obj = JSONParser.fromJson(JSONParser.toJson(content), Metadata.class);
                l = Handlers.getLibraryElementById(obj.getId());
                Handlers.playSong(l);                
                r.setUserId(124);
                r.setType(10);
                r.setFrom("DESKTOP");
                m.setCurrentSong(l.getTitle());
                m.setCurrentArtist(l.getArtist());
                r.setContent(m);
                l = null;
                break;
            
            case RequestTypes.PAUSE:
                Handlers.pause();
                r = null;
                break;
            
            case RequestTypes.RESUME:
                Handlers.resume();
                r = null;
                break;
                
            case RequestTypes.ENQUEUE:
                JSONParser = new Gson();
                obj = JSONParser.fromJson(JSONParser.toJson(content), Metadata.class);
                LibraryElement lib = Handlers.getLibraryElementById(obj.getId());
                Handlers.enqueue(lib);
                r = null;

                break;
                
            case RequestTypes.FETCH_PLAYLIST:
                r.setUserId(124);
                r.setType(19);
                r.setFrom("DESKTOP");
                if(ShiftScopePlayer.isPlaying()){
                    path = ShiftScopePlayer.getCurrentLocation();
                    l = Handlers.getLibraryElementByAbsolutePath(path);
                    m.setCurrentSongId(Handlers.getCurrentSongId());
                    m.setCurrentSong(l.getTitle());
                    m.setCurrentArtist(l.getArtist());
                    m.setIsPlaying(true);
                }
                m.setPlaylist(Handlers.getPlayList());
                m.setCurrentSongId(Handlers.getCurrentSongId());
                r.setContent(m);
                break;
             
            case RequestTypes.PLAY_PLAYLIST:
                Handlers.playPlayList();
                r.setUserId(124);
                r.setType(19);
                r.setFrom("DESKTOP");
                m.setPlaylist(Handlers.getPlayList());
                m.setCurrentSongId(Handlers.getCurrentSongId());
                path = ShiftScopePlayer.getCurrentLocation();
                l = Handlers.getLibraryElementByAbsolutePath(path);
                m.setCurrentSongId(Handlers.getCurrentSongId());
                m.setCurrentSong(l.getTitle());
                m.setCurrentArtist(l.getArtist());
                m.setIsPlaying(true);
                r.setContent(m);
                break;
                
            case RequestTypes.PLAY_FROM_PLAYLIST:
                JSONParser = new Gson();
                obj = JSONParser.fromJson(JSONParser.toJson(content), Metadata.class);
                LibraryElement lk = Handlers.getLibraryElementById(obj.getId());
                Handlers.playSong(lk);
                Handlers.setIsPlayingPlaylist(true);
                r.setUserId(124);
                r.setType(19);
                r.setFrom("DESKTOP");
                m.setPlaylist(Handlers.getPlayList());
                path = ShiftScopePlayer.getCurrentLocation();
                l = Handlers.getLibraryElementByAbsolutePath(path);
                m.setCurrentSong(l.getTitle());
                m.setCurrentSongId(Handlers.getCurrentSongId());
                m.setCurrentArtist(l.getArtist());
                m.setIsPlaying(true);
                r.setContent(m);
                break;
        }
        return r;
    }
    
    public void send(){
        Request r = new Request();
        r.setUserId(userId);
        r.setFrom(from);
        r.setContent(content);
        r.setType(type);
        r.setResponse(response);
        Main.s.send(JSONParser.toJson(r));
    }
    
}
