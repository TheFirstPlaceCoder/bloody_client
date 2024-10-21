package com.client.impl.function.movement;

import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ElytraUp extends Function {
    public ElytraUp() {
        super("Elytra Up", Category.MOVEMENT);
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
    public void tick(TickEvent.Pre event) {
        float SPEED = 0.065F;
        if (mc.player.isAlive()) {
            mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y + 0.06499999761581421D, mc.player.getVelocity().z);
        }
    }
}
