package com.client.utils.misc;

import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class CustomSoundInstance implements SoundInstance {
    protected Sound sound;
    protected final SoundCategory category;
    protected final Identifier id;
    protected float volume;
    protected float pitch;
    protected double x;
    protected double y;
    protected double z;
    protected boolean repeat;
    protected int repeatDelay;
    protected AttenuationType attenuationType;
    protected boolean looping;

    public CustomSoundInstance(SoundEvent sound, SoundCategory category) {
        this(sound.getId(), category);
    }

    public CustomSoundInstance(Identifier soundId, SoundCategory category) {
        this.volume = 1.0F;
        this.pitch = 1.0F;
        this.attenuationType = AttenuationType.LINEAR;
        this.id = soundId;
        this.category = category;
    }

    public Identifier getId() {
        return this.id;
    }

    public WeightedSoundSet getSoundSet(SoundManager soundManager) {
        WeightedSoundSet weightedSoundSet = soundManager.get(this.id);
        if (weightedSoundSet == null) {
            this.sound = SoundManager.MISSING_SOUND;
        } else {
            this.sound = weightedSoundSet.getSound();
        }

        return weightedSoundSet;
    }

    public Sound getSound() {
        return this.sound;
    }

    public SoundCategory getCategory() {
        return this.category;
    }

    public boolean isRepeatable() {
        return this.repeat;
    }

    public int getRepeatDelay() {
        return this.repeatDelay;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getVolume() {
        return this.volume * this.sound.getVolume();
    }

    public float getPitch() {
        return this.pitch * this.sound.getPitch();
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public AttenuationType getAttenuationType() {
        return this.attenuationType;
    }

    public boolean isLooping() {
        return this.looping;
    }

    public String toString() {
        return "SoundInstance[" + this.id + "]";
    }
}