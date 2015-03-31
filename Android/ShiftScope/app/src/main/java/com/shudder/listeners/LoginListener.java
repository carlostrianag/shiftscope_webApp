package com.shudder.listeners;

/**
 * Created by Carlos on 30/03/2015.
 */
public abstract class LoginListener{
    public void OnSuccess() {OnLoaded();};
    public void OnFailed() {OnError("Email/password combination is not valid.");};
    public abstract void OnLoading();
    public abstract void OnLoaded();
    public void OnError(String message){OnLoaded();}
}