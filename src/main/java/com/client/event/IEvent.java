package com.client.event;

import api.interfaces.ICancellable;
import api.main.EventUtils;
import com.client.BloodyClient;

public class IEvent implements ICancellable {
    private boolean cancelled = false;

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    public void post() {
        if (BloodyClient.canUpdate()) {
            EventUtils.post(this);
        }
    }
}