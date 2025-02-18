package com.client.impl.function.misc;

import api.interfaces.EventHandler;
import com.client.event.events.MouseEvent;
import com.client.event.events.PacketEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.combat.aura.AttackAura;
import com.client.impl.function.combat.aura.rotate.RotationHandler;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.game.entity.SelfUtils;
import com.client.utils.game.inventory.FindItemResult;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.game.inventory.SlotUtils;
import com.client.utils.misc.InputUtils;
import com.client.utils.misc.TaskTransfer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * __aaa__
 * 22.05.2024
 * */
public class MiddleClick extends Function {
    public MiddleClick() {
        super("Middle Click", Category.MISC);
    }

    private final ListSetting item = List().name("Использовать").enName("Item").list(List.of("Жемчуг", "Фейерверк", "Оба")).defaultValue("Оба").build();
    public final IntegerSetting delay = Integer().name("Задержка").enName("Swap Delay").defaultValue(2).min(0).max(6).build();
    private final BooleanSetting excludeHotbar = Boolean().name("Не оставлять в хотбаре").enName("Exclude Hotbar").defaultValue(true).build();
    private final BooleanSetting bypass = Boolean().name("Обход ротации ауры").enName("Aura Rotation Bypass").defaultValue(true).build();

    private final TaskTransfer taskTransfer = new TaskTransfer();
    public int prev;
    private boolean shouldSwap = false, afterSwap = false;

    @Override
    public void onEnable() {
        shouldSwap = false;
        afterSwap = false;
    }

    public static boolean sendPacket = false;

    @EventHandler
    private void onMouseEvent(MouseEvent event) {
        if (event.button == GLFW.GLFW_MOUSE_BUTTON_3 && event.action == InputUtils.Action.PRESS) {
            FindItemResult i = getItem();

            if (!i.found() || mc.player.getItemCooldownManager().isCoolingDown(mc.player.inventory.getStack(i.slot()).getItem())) {
                return;
            }

            if (i.slot() == 45) {
                mc.interactionManager.interactItem(mc.player, mc.world, Hand.OFF_HAND);
                mc.player.swingHand(Hand.OFF_HAND);

                return;
            }

            if (bypass.get() && FunctionManager.get(AttackAura.class).isEnabled()) {
                RotationHandler.getHandler().getRotate().a = mc.player.yaw;
                RotationHandler.getHandler().getRotate().b = mc.player.pitch;
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(mc.player.yaw, mc.player.pitch, mc.player.isOnGround()));
                sendPacket = true;
            } else {
                sendPacket = false;
            }

            use(i.slot());
        }
    }

    @Override
    public void tick(TickEvent.Post event) {
        taskTransfer.handle();

        if (shouldSwap) {
            mc.player.inventory.selectedSlot = prev;
            shouldSwap = false;
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
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
            mc.interactionManager.pickFromInventory(slot);
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);
            taskTransfer.bind(() -> {
                mc.player.inventory.selectedSlot = prev;
                afterSwap = true;
            }, delay.get() * 50L);
        } else {
            boolean air = false;
            for (int i = 0; i < SlotUtils.MAIN_START; i++) {
                if (mc.player.inventory.getStack(i).getItem() == Items.AIR) {
                    air = true;
                    break;
                }
            }

            prev = mc.player.inventory.selectedSlot;
            mc.interactionManager.pickFromInventory(slot);
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);

            if (air) {
                if (excludeHotbar.get()) mc.interactionManager.pickFromInventory(slot);
                taskTransfer.bind(() -> {
                    mc.player.inventory.selectedSlot = prev;
                    afterSwap = true;
                }, delay.get() * 50L);
            } else {
                taskTransfer.bind(() -> {
                    mc.interactionManager.pickFromInventory(slot);
                    afterSwap = true;
                }, delay.get() * 50L);
            }
        }
    }

    @Override
    public void onPacket(PacketEvent.Receive event) {
        if (afterSwap && event.packet instanceof UpdateSelectedSlotS2CPacket) {
            shouldSwap = true;

            taskTransfer.bind(() -> {
                mc.player.inventory.selectedSlot = prev;
            }, delay.get() * 50L);

            afterSwap = false;
        }
    }

    private FindItemResult getItem() {
        FindItemResult result;
        Item a = item.get().equals("Жемчуг") ? Items.ENDER_PEARL : item.get().equals("Фейерверк") ? Items.FIREWORK_ROCKET : SelfUtils.hasElytra() ? Items.FIREWORK_ROCKET : Items.ENDER_PEARL;

        if (mc.player.getOffHandStack().getItem().equals(a))
            return new FindItemResult(45, mc.player.getOffHandStack().getCount());

        result = InvUtils.find(a);

        return result;
    }
}