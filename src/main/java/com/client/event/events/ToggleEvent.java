package com.client.event.events;

import com.client.event.IEvent;
import com.client.system.function.Function;

public class ToggleEvent extends IEvent {
    public Function function;

    public ToggleEvent(Function function) {
        this.function = function;
    }
}
