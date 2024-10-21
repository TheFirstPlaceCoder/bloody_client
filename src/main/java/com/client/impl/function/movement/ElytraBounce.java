package com.client.impl.function.movement;

import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.system.setting.settings.ListSetting;
import mixin.accessor.LivingEntityAccessor;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

import java.util.List;

public class ElytraBounce extends Function {
    public ElytraBounce() {
        super("Elytra Bounce", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        ItemStack is = mc.player.getEquippedStack(EquipmentSlot.CHEST);
        if (is.getItem() != Items.ELYTRA || !ElytraItem.isUsable(is)) {
            NotificationManager.add(new Notification(NotificationType.CLIENT, "На вас нет элитр!"), NotificationManager.NotifType.Error);
            this.toggle(false);
        }
    }

    @Override
    public void onDisable() {
        if (!InputUtil.isKeyPressed(mc.getWindow().getHandle(), mc.options.keyForward.getDefaultKey().getCode()))
            mc.options.keyForward.setPressed(false);
        if (!InputUtil.isKeyPressed(mc.getWindow().getHandle(), mc.options.keyJump.getDefaultKey().getCode()))
            mc.options.keyJump.setPressed(false);
    }

    @Override
    public void tick(TickEvent.Pre event) {
        mc.options.keyJump.setPressed(true);
        mc.options.keyForward.setPressed(true);

        if (!mc.player.isFallFlying() && mc.player.fallDistance > 0 && checkElytra() && !mc.player.isFallFlying())
            castElytra();

        ((LivingEntityAccessor) mc.player).setLastJumpCooldown(0);
    }

    public boolean castElytra() {
        if (checkElytra() && check()) {
            mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            return true;
        }
        return false;
    }

    private boolean checkElytra() {
        if (mc.player.input.jumping && !mc.player.abilities.flying && !mc.player.hasVehicle() && !mc.player.isClimbing()) {
            ItemStack is = mc.player.getEquippedStack(EquipmentSlot.CHEST);
            return is.getItem() == Items.ELYTRA && ElytraItem.isUsable(is);
        }
        return false;
    }

    private boolean check() {
        if (!mc.player.isTouchingWater() && !mc.player.hasStatusEffect(StatusEffects.LEVITATION)) {
            ItemStack is = mc.player.getEquippedStack(EquipmentSlot.CHEST);
            if (is.getItem() == Items.ELYTRA && ElytraItem.isUsable(is)) {
                mc.player.startFallFlying();
                return true;
            }
        }
        return false;
    }
}