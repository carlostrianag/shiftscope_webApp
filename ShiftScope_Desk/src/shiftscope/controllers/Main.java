package shiftscope.controllers;

import java.net.URI;

/**
 *
 * @author carlos
 */
public class Main {
    public static void main(String[] args) {
        ShiftScopeWebSocket s = new ShiftScopeWebSocket(URI.create("ws://127.0.0.1:8001"));
        FolderFetcher ff = new FolderFetcher();
        s.connect();
        while (!s.isOpen()) {
            System.out.println("Connecting ...");
        }
        String path = "/home/carlos/Music/carlitos";
        ShiftScopePlayer.initPlayer();
        ShiftScopePlayer.play(ff.folderFetch(path)[2].getAbsolutePath());
        
        s.send(ff.JSONfolderFetch(path));
    }
}
