package com.client.event.events;

import com.client.event.IEvent;

public class ReceiveChatMessageEvent extends IEvent {
    public String message;

    public ReceiveChatMessageEvent(String message) {
        this.message = message;
    }
}
