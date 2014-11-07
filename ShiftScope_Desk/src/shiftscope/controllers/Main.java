package shiftscope.controllers;

import java.net.URI;

/**
 *
 * @author carlos
 */
public class Main {
    public static void main(String[] args) {
        ShiftScopeWebSocket s = new ShiftScopeWebSocket(URI.create("ws://127.0.0.1:9876"));
        s.connect();
    }
}
