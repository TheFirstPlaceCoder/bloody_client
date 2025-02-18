package com.client.impl.function.misc;

import com.client.event.events.AttackEntityEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.files.SoundManager;
import com.client.utils.misc.CustomSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

import java.util.List;

public class HitSound extends Function {
    private final ListSetting mode = List().name("Режим").enName("Mode").list(List.of("Удар", "Удар 2", "Бип", "Глухой", "Глухой 2", "Bonk", "Bass", "Chime")).defaultValue("Глухой").build();
    public final DoubleSetting volume = Double().name("Громкось").enName("Volume").defaultValue(1.0).min(0).max(1).build();

    public HitSound() {
        super("Hit Sound", Category.MISC);
    }

    @Override
    public void onAttackEntityEvent(AttackEntityEvent.Post event) {
        if (!(event.entity instanceof PlayerEntity)) return;

        CustomSoundInstance customSoundInstance = new CustomSoundInstance(getEvent(), SoundCategory.MASTER);
        customSoundInstance.setVolume(volume.floatValue());
        mc.getSoundManager().play(customSoundInstance);
    }

    public SoundEvent getEvent() {
        return switch (mode.get()) {
            case "Удар" -> SoundManager.PUNCH_EVENT;
            case "Бип" -> SoundManager.BEEEP_EVENT;
            case "Глухой" -> SoundManager.GLU_EVENT;
            case "Глухой 2" -> SoundManager.GLU2_EVENT;
            case "Bonk" -> SoundManager.BONK_EVENT;
            case "Bass" -> SoundManager.BASS_EVENT;
            default -> SoundManager.CHIME_EVENT;
        };
    }
}
