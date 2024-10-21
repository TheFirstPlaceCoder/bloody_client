package com.client.utils.files;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SoundManager {
    private static final Identifier ENABLE_SOUND = new Identifier("bloody-client:enable");
    private static final Identifier DISABLE_SOUND = new Identifier("bloody-client:disable");

    private static final Identifier AMOGUS = new Identifier("bloody-client:amogus");
    private static final Identifier AWP = new Identifier("bloody-client:awp");
    private static final Identifier CLOCK = new Identifier("bloody-client:clock");
    private static final Identifier UUUH = new Identifier("bloody-client:uuuh");
    private static final Identifier YAY = new Identifier("bloody-client:yay");

    private static final Identifier BEEEP = new Identifier("bloody-client:beeep");
    private static final Identifier GLU = new Identifier("bloody-client:glu");
    private static final Identifier GLU2 = new Identifier("bloody-client:glu2");
    private static final Identifier PUNCH = new Identifier("bloody-client:punch");
    private static final Identifier PUNCH2 = new Identifier("bloody-client:punch2");
    private static final Identifier BONK = new Identifier("bloody-client:bonk");
    private static final Identifier BASS = new Identifier("bloody-client:bass");
    private static final Identifier CHIME = new Identifier("bloody-client:chime");
    private static final Identifier DONE = new Identifier("bloody-client:done");

    public static SoundEvent ENABLE_EVENT = new SoundEvent(ENABLE_SOUND);
    public static SoundEvent DISABLE_EVENT = new SoundEvent(DISABLE_SOUND);

    public static SoundEvent AMOGUS_EVENT = new SoundEvent(AMOGUS);
    public static SoundEvent AWP_EVENT = new SoundEvent(AWP);
    public static SoundEvent CLOCK_EVENT = new SoundEvent(CLOCK);
    public static SoundEvent UUUH_EVENT = new SoundEvent(UUUH);
    public static SoundEvent YAY_EVENT = new SoundEvent(YAY);

    public static SoundEvent BEEEP_EVENT = new SoundEvent(BEEEP);
    public static SoundEvent GLU_EVENT = new SoundEvent(GLU);
    public static SoundEvent GLU2_EVENT = new SoundEvent(GLU2);
    public static SoundEvent PUNCH_EVENT = new SoundEvent(PUNCH);
    public static SoundEvent PUNCH2_EVENT = new SoundEvent(PUNCH2);
    public static SoundEvent BONK_EVENT = new SoundEvent(BONK);
    public static SoundEvent BASS_EVENT = new SoundEvent(BASS);
    public static SoundEvent CHIME_EVENT = new SoundEvent(CHIME);
    public static SoundEvent DONE_EVENT = new SoundEvent(DONE);

    public static void init() {
        Registry.register(Registry.SOUND_EVENT, ENABLE_SOUND, ENABLE_EVENT);
        Registry.register(Registry.SOUND_EVENT, DISABLE_SOUND, DISABLE_EVENT);

        Registry.register(Registry.SOUND_EVENT, AMOGUS, AMOGUS_EVENT);
        Registry.register(Registry.SOUND_EVENT, AWP, AWP_EVENT);
        Registry.register(Registry.SOUND_EVENT, CLOCK, CLOCK_EVENT);
        Registry.register(Registry.SOUND_EVENT, UUUH, UUUH_EVENT);
        Registry.register(Registry.SOUND_EVENT, YAY, YAY_EVENT);

        Registry.register(Registry.SOUND_EVENT, BEEEP, BEEEP_EVENT);
        Registry.register(Registry.SOUND_EVENT, GLU, GLU_EVENT);
        Registry.register(Registry.SOUND_EVENT, GLU2, GLU2_EVENT);
        Registry.register(Registry.SOUND_EVENT, PUNCH, PUNCH_EVENT);
        Registry.register(Registry.SOUND_EVENT, PUNCH2, PUNCH2_EVENT);
        Registry.register(Registry.SOUND_EVENT, BONK, BONK_EVENT);
        Registry.register(Registry.SOUND_EVENT, BASS, BASS_EVENT);
        Registry.register(Registry.SOUND_EVENT, CHIME, CHIME_EVENT);
        Registry.register(Registry.SOUND_EVENT, DONE, DONE_EVENT);
    }
}
