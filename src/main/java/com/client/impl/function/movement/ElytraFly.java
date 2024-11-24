package com.client.impl.function.movement;

import api.interfaces.EventHandler;
import com.client.event.events.KeybindSettingEvent;
import com.client.event.events.PlayerMoveEvent;
import com.client.event.events.TickEvent;
import com.client.interfaces.IVec3d;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.KeybindSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.game.inventory.FindItemResult;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.game.inventory.SlotUtils;
import com.client.utils.misc.InputUtils;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.FireworkItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElytraFly extends Function {
    private final ListSetting mode = List().name("Режим").list(List.of("Автоматически", "Бинды", "FunTime")).defaultValue("Автоматически").build();

    public final IntegerSetting minHeight = Integer().name("Высота подъема").defaultValue(100).min(0).max(360).visible(() -> mode.get().equals("Автоматически")).build();
    public final IntegerSetting maxHeight = Integer().name("Высота спуска").defaultValue(360).min(0).max(360).visible(() -> mode.get().equals("Автоматически")).build();
    public final KeybindSetting upBind = Keybind().name("Подъем").defaultValue(-1).visible(() -> mode.get().equals("Бинды")).build();
    public final KeybindSetting downBind = Keybind().name("Снижение").defaultValue(-1).visible(() -> mode.get().equals("Бинды")).build();

    public final DoubleSetting rotationSpeed = Double().name("Скорость").defaultValue(4.0).min(1).max(10).visible(() -> !mode.get().equals("FunTime")).build();

    public final IntegerSetting horizontal_wasp = Integer().name("Горизонтальная скорость").defaultValue(17).min(1).max(17).visible(() -> mode.get().equals("FunTime")).build();
    public final IntegerSetting vertical_wasp = Integer().name("Вертикальная скорость").defaultValue(17).min(1).max(18).visible(() -> mode.get().equals("FunTime")).build();
    public final IntegerSetting ti = Integer().name("Задержка фейерверка").defaultValue(10).min(0).max(15).visible(() -> mode.get().equals("FunTime")).build();


    public ElytraFly() {
        super("Elytra Fly", Category.MOVEMENT);
    }

    private boolean pitchingDown = true, isUpPressed = false, isDownPressed = false;
    private int pitchField;

    private boolean moving;
    private float yaw;
    private int fireworkTicks, prev;
    private final HashMap<Long, Runnable> callback = new HashMap<>();

    @Override
    public void onEnable() {
        pitchField = 40;
        isUpPressed = false;
        isDownPressed = false;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!mode.get().equals("FunTime") || !mc.player.isFallFlying()) return;

        updateWaspMovement();

        double cos = Math.cos(Math.toRadians(yaw + 90));
        double sin = Math.sin(Math.toRadians(yaw + 90));

        double x = moving ? cos * (horizontal_wasp.get() / 10d) : 0;
        double y = 0;
        double z = moving ? sin * (horizontal_wasp.get() / 10d) : 0;

        if (mc.options.keySneak.isPressed() && !mc.options.keyJump.isPressed()) {
            y = -(vertical_wasp.get() / 10d);
        }
        if (!mc.options.keySneak.isPressed() && mc.options.keyJump.isPressed()) {
            y = (vertical_wasp.get() / 10d);
        }

        ((IVec3d) event.movement).set(x, y, z);
    }

    private void updateWaspMovement() {
        float yaw = mc.player.yaw;

        float f = mc.player.input.movementForward;
        float s = mc.player.input.movementSideways;

        if (f > 0) {
            moving = true;
            yaw += s > 0 ? -45 : s < 0 ? 45 : 0;
        } else if (f < 0) {
            moving = true;
            yaw += s > 0 ? -135 : s < 0 ? 135 : 180;
        } else {
            moving = s != 0;
            yaw += s > 0 ? -90 : s < 0 ? 90 : 0;
        }
        this.yaw = yaw;
    }

    @Override
    public void tick(TickEvent.Pre event) {
        ItemStack is = mc.player.getEquippedStack(EquipmentSlot.CHEST);
        if (is.getItem() != Items.ELYTRA || !ElytraItem.isUsable(is)) {
            NotificationManager.add(new Notification(NotificationType.CLIENT, "На вас нет элитр!"), NotificationManager.NotifType.Error);
            this.toggle(false);
            return;
        }

        if (!InvUtils.find(itemStack -> itemStack.getItem() instanceof FireworkItem).found()) {
            NotificationManager.add(new Notification(NotificationType.CLIENT, "У вас нет фейерверков!"), NotificationManager.NotifType.Error);
            this.toggle(false);
            return;
        }

        for (Map.Entry<Long, Runnable> longRunnableEntry : callback.entrySet()) {
            if (System.currentTimeMillis() > longRunnableEntry.getKey())
                longRunnableEntry.getValue().run();
        }

        callback.entrySet().removeIf(a -> System.currentTimeMillis() > a.getKey());

        if (mode.get().equals("FunTime")) {
            if (mc.player.isFallFlying() && fireworkTicks <= 0) {
                FindItemResult firework = InvUtils.find(itemStack -> itemStack.getItem() instanceof FireworkItem);
                if (firework.found()) {
                    use(firework.slot());
                    try {
                        fireworkTicks = (mc.player.inventory.getStack(firework.slot()).getOrCreateSubTag("Fireworks").getByte("Flight") * 35) - ti.get();
                    } catch (Exception ex){}
                }
            } else if (mc.player.isFallFlying()) fireworkTicks--;

            return;
        }

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
        if (mode.get().equals("FunTime")) return;

        if (event.action == InputUtils.Action.PRESS) {
            if (upBind.key(event.key, !event.mouse)) isUpPressed = true;
            else if (downBind.key(event.key, !event.mouse)) isDownPressed = true;
        }
    }

    private void use(int slot) {
        if (slot == 45) {
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.OFF_HAND);
            mc.player.swingHand(Hand.OFF_HAND);
        } else if (slot == mc.player.inventory.selectedSlot) {
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);
        } else if (SlotUtils.isHotbar(slot)) {
            prev = mc.player.inventory.selectedSlot;
            mc.interactionManager.pickFromInventory(slot);
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);
            callback.put(150L, () -> mc.player.inventory.selectedSlot = prev);
        } else {
            boolean air = false;
            for (int i = 0; i < SlotUtils.MAIN_START; i++) {
                if (mc.player.inventory.getStack(i).getItem() == Items.AIR) {
                    air = true;
                    break;
                }
            }
            if (air) {
                prev = mc.player.inventory.selectedSlot;
                mc.interactionManager.pickFromInventory(slot);
                mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
                mc.player.swingHand(Hand.MAIN_HAND);
                callback.put(150L, () -> mc.player.inventory.selectedSlot = prev);
            } else {
                mc.interactionManager.pickFromInventory(slot);
                mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
                mc.player.swingHand(Hand.MAIN_HAND);
                mc.interactionManager.pickFromInventory(slot);
            }
        }
    }
}
