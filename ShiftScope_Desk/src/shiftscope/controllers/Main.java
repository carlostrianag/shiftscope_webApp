package shiftscope.controllers;

import java.net.URI;

/**
 *
 * @author carlos
 */
public class Main {
    public static ShiftScopeWebSocket s;
    public static void main(String[] args) {
        Handlers.buildLibraryTreeFromFile();
        s = new ShiftScopeWebSocket(URI.create("ws://54.148.12.107:8001"));
        s.connect();
        ShiftScopePlayer.initPlayer();
        ViewHandler.init();
    }
}
