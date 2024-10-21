package com.client.impl.function.misc;

import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.files.SoundManager;
import com.client.utils.misc.FunctionUtils;
import net.minecraft.sound.SoundEvent;

import java.util.List;

public class HitSound extends Function {
    private final ListSetting mode = List().name("Режим").list(List.of("Удар", "Удар 2", "Бип", "Глухой", "Глухой 2", "Bonk", "Bass", "Chime", "Done")).defaultValue("Глухой").build();

    public HitSound() {
        super("Hit Sound", Category.MISC);
    }

    @Override
    public void onDisable() {
        FunctionUtils.soundEvent = null;
    }

    @Override
    public void tick(TickEvent.Pre event) {
        FunctionUtils.soundEvent = getEvent();
    }

    public SoundEvent getEvent() {
        return switch (mode.get()) {
            case "Удар" -> SoundManager.PUNCH_EVENT;
            case "Удар 2" -> SoundManager.PUNCH2_EVENT;
            case "Бип" -> SoundManager.BEEEP_EVENT;
            case "Глухой" -> SoundManager.GLU_EVENT;
            case "Bonk" -> SoundManager.BONK_EVENT;
            case "Bass" -> SoundManager.BASS_EVENT;
            case "Chime" -> SoundManager.CHIME_EVENT;
            case "Done" -> SoundManager.DONE_EVENT;
            default -> SoundManager.GLU2_EVENT;
        };
    }
}
