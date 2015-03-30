package com.shudder.listeners;

import com.shudder.utils.Operation;

/**
 * Created by Carlos on 2/5/2015.
 */
public abstract class WebSocketListener {
    public abstract void OnSync(Operation o);
}
