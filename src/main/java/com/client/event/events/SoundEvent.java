package com.client.event.events;

import com.client.event.IEvent;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.WeightedSoundSet;

public class SoundEvent extends IEvent {
    private static final SoundEvent INSTANCE = new SoundEvent();
    public SoundInstance soundInstance;
    public WeightedSoundSet weightedSoundSet;

    public static SoundEvent get(SoundInstance si, WeightedSoundSet wss) {
        INSTANCE.setCancelled(false);
        INSTANCE.soundInstance = si;
        INSTANCE.weightedSoundSet = wss;
        return INSTANCE;
    }
}