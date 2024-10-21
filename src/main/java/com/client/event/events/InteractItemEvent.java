package com.client.event.events;

import com.client.event.IEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class InteractItemEvent extends IEvent {
    private static final InteractItemEvent INSTANCE = new InteractItemEvent();

    public Hand hand;
    public ActionResult toReturn;

    public static InteractItemEvent get(Hand hand) {
        INSTANCE.hand = hand;
        INSTANCE.toReturn = null;

        return INSTANCE;
    }
}