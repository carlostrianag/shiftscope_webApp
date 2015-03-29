/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shudder.listeners;

/**
 *
 * @author Carlos
 */
public abstract class WebSocketListener {
    public abstract void OnOpened();
    public abstract void OnError(String error);
    public abstract void loading();
    public abstract void loaded();
}
