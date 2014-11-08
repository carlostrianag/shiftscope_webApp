
package shiftscope.controllers;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
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
        switch(type) {
            case RequestTypes.FETCH:
                System.out.println("Fetching");
                r.setUserId(124);
                r.setType(1);
                r.setFrom("DESKTOP");
                if(ShiftScopePlayer.isPlaying()){
                    String path = ShiftScopePlayer.getCurrentLocation();
                    LibraryElement l = Handlers.getLibraryElementByAbsolutePath(path);
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
                Handlers.playSong(obj.getId(), obj.getAbsolutePath());
                LibraryElement l = Handlers.getLibraryElementById(obj.getId());
                
                r.setUserId(124);
                r.setType(10);
                r.setFrom("DESKTOP");
                m.setCurrentSong(l.getTitle());
                m.setCurrentArtist(l.getArtist());
                r.setContent(m);
                break;
            
            case RequestTypes.PAUSE:
                Handlers.pause();
                r = null;
                break;
            
            case RequestTypes.RESUME:
                Handlers.resume();
                r = null;
                break;
        }
        return r;
    }
}
