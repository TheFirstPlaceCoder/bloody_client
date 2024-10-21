package com.client.event.events;

import com.client.event.IEvent;
import net.minecraft.client.util.math.MatrixStack;

public class GpsRenderEvent extends IEvent {
    private static final GpsRenderEvent INSTANCE = new GpsRenderEvent();

    public MatrixStack matrices;
    public float tickDelta;
    public double offsetX, offsetY, offsetZ;

    public static GpsRenderEvent get(MatrixStack matrices, float tickDelta, double offsetX, double offsetY, double offsetZ) {
        INSTANCE.matrices = matrices;
        INSTANCE.tickDelta = tickDelta;
        INSTANCE.offsetX = offsetX;
        INSTANCE.offsetY = offsetY;
        INSTANCE.offsetZ = offsetZ;
        return INSTANCE;
    }
}
