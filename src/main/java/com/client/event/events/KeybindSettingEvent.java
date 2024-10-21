package com.client.event.events;

import com.client.event.IEvent;
import com.client.utils.misc.InputUtils;

public class KeybindSettingEvent extends IEvent {
    public boolean mouse;
    public int key;
    public InputUtils.Action action;

    public KeybindSettingEvent(boolean mouse, int key, InputUtils.Action action) {
        this.mouse = mouse;
        this.key = key;
        this.action = action;
    }
}