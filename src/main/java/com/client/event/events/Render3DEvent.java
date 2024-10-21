package com.client.event.events;

import com.client.event.IEvent;
import net.minecraft.client.util.math.MatrixStack;

public class Render3DEvent extends IEvent {
    private final MatrixStack matrices;
    private final float tickDelta;

    public Render3DEvent(MatrixStack matrices, float tickDelta) {
        this.matrices = matrices;
        this.tickDelta = tickDelta;
    }

    public MatrixStack getMatrices() {
        return matrices;
    }

    public float getTickDelta() {
        return tickDelta;
    }

    public static class PostRender extends Render3DEvent {
        public PostRender(MatrixStack matrices, float tickDelta) {
            super(matrices, tickDelta);
        }
    }
}