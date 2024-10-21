package com.client.event.events;

import com.client.event.IEvent;
import net.minecraft.particle.ParticleEffect;

public class ParticleRenderEvent extends IEvent {
    public ParticleEffect particle;

    public ParticleRenderEvent(ParticleEffect particle) {
        this.particle = particle;
    }
}
