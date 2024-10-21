package com.client.event.events;

import com.client.event.IEvent;

public class ChunkOcclusionEvent extends IEvent {
    private static final ChunkOcclusionEvent INSTANCE = new ChunkOcclusionEvent();

    public static ChunkOcclusionEvent get() {
        INSTANCE.setCancelled(false);
        return INSTANCE;
    }
}