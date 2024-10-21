package com.client.event.events;

import com.client.event.IEvent;
import com.client.utils.misc.InputUtils;

public class KeyEvent extends IEvent {
    public int key;
    public InputUtils.Action action;

    public KeyEvent(int key, InputUtils.Action action) {
        this.key = key;
        this.action = action;
    }
}
