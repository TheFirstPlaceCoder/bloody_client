package com.client.impl.function.movement;

import api.interfaces.EventHandler;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.game.movement.MovementUtils;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

import java.util.List;

public class Sprint extends Function {
    public Sprint() {
        super("Sprint", Category.MOVEMENT);
    }

    private final ListSetting mode = List().name("Режим").enName("Mode").list(List.of("Обычный", "Matrix", "FunTime")).defaultValue("Обычный").build();
    private final BooleanSetting keepSprint = Boolean().name("Сохранять спринт").enName("Save Sprint").defaultValue(true).build();

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (MovementUtils.isMoving()) {
            set();
        }
    }

    public void set() {
        if (mc.player == null || mc.getNetworkHandler() == null) {
            return;
        }

        if (mc.options.keyForward.isPressed()
                && !mc.options.keyBack.isPressed()
                && !mc.player.isSneaking()
                && !mc.player.horizontalCollision
                && mc.player.getHungerManager().getFoodLevel() > 6
                && !mc.player.isSubmergedInWater()
                && !mc.player.isInLava()
                && !mc.player.hasStatusEffect(StatusEffects.BLINDNESS)) {
            switch (mode.get()) {
                case "Обычный" -> mc.player.setSprinting(true);
                case "Matrix" -> mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
                default -> mc.options.keySprint.setPressed(true);
            }
        }
    }

    public boolean keepSprint() {
        return isEnabled() && keepSprint.get();
    }
}