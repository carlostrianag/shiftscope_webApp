package com.shiftscope.listeners;

import com.shiftscope.utils.Operation;

/**
 * Created by Carlos on 2/5/2015.
 */
public abstract class WebSocketListener {
    public abstract void OnSync(Operation o);
}
