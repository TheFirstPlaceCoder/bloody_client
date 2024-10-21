package com.client.impl.function.misc;

import com.client.event.events.PacketEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.files.SoundManager;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.sound.SoundEvent;

import java.util.List;

public class KillSound extends Function {
    private final ListSetting mode = List().name("Режим").list(List.of("Among Us", "AWP", "Звон", "Uuuh", "Еее")).defaultValue("AWP").build();
    public final DoubleSetting volume = Double().name("Громкось").defaultValue(1d).min(0).max(1).build();

    public KillSound() {
        super("Kill Sound", Category.MISC);
    }

    @Override
    public void onPacket(PacketEvent.Receive event) {
        if (event.packet instanceof EntityStatusS2CPacket p) {
            if (p.getEntity(mc.world) != mc.player && p.getStatus() == 3 && mc.player.distanceTo(p.getEntity(mc.world)) < 7) {
                mc.player.playSound(getEvent(), volume.floatValue(), 1f);
            }
        }
    }

    public SoundEvent getEvent() {
        return switch (mode.get()) {
            case "Among Us" -> SoundManager.AMOGUS_EVENT;
            case "AWP" -> SoundManager.AWP_EVENT;
            case "Звон" -> SoundManager.CLOCK_EVENT;
            case "Uuuh" -> SoundManager.UUUH_EVENT;
            default -> SoundManager.YAY_EVENT;
        };
    }
}
