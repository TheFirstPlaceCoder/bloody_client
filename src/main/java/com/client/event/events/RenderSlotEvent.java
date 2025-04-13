package com.client.event.events;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

import java.awt.*;

public class RenderSlotEvent {
    public Slot minSlot, minCountSlot;
    public Color minColor, minCountColor;

    public Text title;
    public ScreenHandler handler;
}
