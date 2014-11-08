package shiftscope.controllers;

import java.net.URI;
import java.util.ArrayList;

/**
 *
 * @author carlos
 */
public class Main {
    public static void main(String[] args) {
        ShiftScopeWebSocket s = new ShiftScopeWebSocket(URI.create("ws://127.0.0.1:8001"));
        s.connect();
        ShiftScopePlayer.initPlayer();
        
        ArrayList<String> paths = new ArrayList();
        paths.add("/home/carlos/Music/carlitos");
        paths.add("/home/carlos/Music/MUSIC");
        Handlers.buildLibraryTree(paths);
    }
}
