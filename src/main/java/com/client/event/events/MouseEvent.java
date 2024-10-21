package com.client.event.events;

import com.client.event.IEvent;
import com.client.utils.misc.InputUtils;

public class MouseEvent extends IEvent {
    public int button;
    public InputUtils.Action action;
    public double x, y;

    public MouseEvent(int button, InputUtils.Action action, double x, double y) {
        this.button = button;
        this.action = action;
        this.x = x;
        this.y = y;
    }
}
