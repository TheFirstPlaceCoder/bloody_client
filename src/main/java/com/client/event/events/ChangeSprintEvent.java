package com.client.event.events;

import com.client.event.IEvent;

public class ChangeSprintEvent extends IEvent {
    public boolean set;

    public ChangeSprintEvent(boolean set) {
        this.set = set;
    }
}
