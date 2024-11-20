package com.client.impl.function.movement;

import com.client.event.events.NoSlowEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.game.entity.SelfUtils;
import com.client.utils.game.movement.MovementUtils;
import com.client.utils.math.MsTimer;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;

import java.util.List;

public class NoSlow extends Function {
    private final ListSetting mode = List().name("Режим").list(List.of("Grim", "Matrix", "Ванильный", "ReallyWorld")).defaultValue("Grim").build();
    private final BooleanSetting setSlot = Boolean().name("Свапать слот").defaultValue(true).visible(() -> mode.get().equals("Ванильный")).build();

    public NoSlow() {
        super("No Slow", Category.MOVEMENT);
    }

    MsTimer timer = new MsTimer();

    @Override
    public void onEnable() {
        timer.reset();
    }

    @Override
    public void onNoSlowEvent(NoSlowEvent event) {
        if (SelfUtils.hasElytra() && mc.player.isFallFlying() || mc.player.isRiding() || !mc.player.isUsingItem() || !MovementUtils.isMoving())
            return;

        switch (mode.get()) {
            case "ReallyWorld" -> {
                label35:
                {
                    if (mc.player.getOffHandStack().getUseAction() != UseAction.BLOCK) {
                        if (mc.player.getOffHandStack().getUseAction() != UseAction.EAT) {
                            break label35;
                        }
                    }

                    if (mc.player.getActiveHand() == Hand.MAIN_HAND) {
                        return;
                    }
                }

                if ((float) mc.player.getHungerManager().getFoodLevel() < 6.0F) {
                    if (mc.player.isSprinting()) {
                        return;
                    }
                }

                if (!mc.player.isSneaking()) {
                    if (!mc.player.isSwimming()) {
                        event.cancel();
                        if (mc.player.getActiveHand() == Hand.MAIN_HAND) {
                            mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.OFF_HAND));
                        } else {
                            mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
                        }
                    }
                }
            }

            case "Grim" -> {
                if (mc.player.getActiveHand() == Hand.OFF_HAND) {
                    mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.inventory.selectedSlot % 8 + 1));
                    mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.inventory.selectedSlot % 7 + 2));
                    mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.inventory.selectedSlot));
                } else if (mc.player.getActiveHand() == Hand.MAIN_HAND && (mc.player.getItemUseTime() <= 3 || mc.player.age % 2 == 0)) {
                    mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.OFF_HAND));
                }

                event.cancel();
            }

            case "Matrix" -> {
                if (mc.player.isOnGround() && !mc.options.keyJump.isPressed()) {
                    mc.player.setVelocity(mc.player.getVelocity().x * 0.3, mc.player.getVelocity().y, mc.player.getVelocity().z * 0.3);
                } else if (mc.player.fallDistance > 0.2f) {
                    mc.player.setVelocity(mc.player.getVelocity().x * 0.95f, mc.player.getVelocity().y, mc.player.getVelocity().z * 0.95f);
                }

                event.cancel();
            }

            default -> {
                if (setSlot.get())
                    mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.inventory.selectedSlot));

                event.cancel();
            }
        }
    }
}