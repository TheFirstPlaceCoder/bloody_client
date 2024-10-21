package com.client.impl.function.movement;

import com.client.event.events.KeybindSettingEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.KeybindSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.misc.InputUtils;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;

public class ElytraFly extends Function {
    private final ListSetting mode = List().name("Режим").list(List.of("Автоматически", "Бинды")).defaultValue("Автоматически").build();

    public final IntegerSetting minHeight = Integer().name("Высота подъема").defaultValue(100).min(0).max(360).visible(() -> mode.get().equals("Автоматически")).build();
    public final IntegerSetting maxHeight = Integer().name("Высота спуска").defaultValue(360).min(0).max(360).visible(() -> mode.get().equals("Автоматически")).build();
    public final KeybindSetting upBind = Keybind().name("Подъем").defaultValue(-1).visible(() -> !mode.get().equals("Автоматически")).build();
    public final KeybindSetting downBind = Keybind().name("Снижение").defaultValue(-1).visible(() -> !mode.get().equals("Автоматически")).build();

    public final DoubleSetting rotationSpeed = Double().name("Скорость").defaultValue(4.0).min(1).max(10).build();

    public ElytraFly() {
        super("Elytra Fly", Category.MOVEMENT);
    }

    private boolean pitchingDown = true, isUpPressed = false, isDownPressed = false;
    private int pitchField;

    @Override
    public void onEnable() {
        ItemStack is = mc.player.getEquippedStack(EquipmentSlot.CHEST);
        if (is.getItem() != Items.ELYTRA || !ElytraItem.isUsable(is)) {
            NotificationManager.add(new Notification(NotificationType.CLIENT, "На вас нет элитр!"), NotificationManager.NotifType.Error);
            this.toggle(false);
        }
        pitchField = 40;
        isUpPressed = false;
        isDownPressed = false;
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (pitchingDown && (mode.get().equals("Автоматически") ? mc.player.getY() <= minHeight.get() : isUpPressed)) {
            pitchingDown = false;
            isUpPressed = false;
        }

        else if (!pitchingDown && (mode.get().equals("Автоматически") ? mc.player.getY() >= maxHeight.get() : isDownPressed)) {
            pitchingDown = true;
            isDownPressed = false;
        }

        // Pitch upwards
        if (!pitchingDown && mc.player.pitch > -40) {
            pitchField -= rotationSpeed.get().intValue();

            if (pitchField < -40) pitchField = -40;
            // Pitch downwards
        } else if (pitchingDown && mc.player.pitch < 40) {
            pitchField += rotationSpeed.get().intValue();

            if (pitchField > 40) pitchField = 40;
        }

        mc.player.pitch = pitchField;
    }

    @Override
    public void onKeybindSetting(KeybindSettingEvent event) {
        if (event.action == InputUtils.Action.PRESS) {
            if (upBind.key(event.key, !event.mouse)) isUpPressed = true;
            else if (downBind.key(event.key, !event.mouse)) isDownPressed = true;
        }
    }
}
