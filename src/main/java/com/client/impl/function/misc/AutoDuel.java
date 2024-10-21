package com.client.impl.function.misc;

import com.client.event.events.PacketEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import net.minecraft.item.*;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import java.util.Arrays;
import java.util.List;

public class AutoDuel extends Function {
    public AutoDuel() {
        super("Auto Duel", Category.MISC);
    }

    private final ListSetting duel = List().name("Режим дуэли").list(List.of("Шары", "Щит", "Шипы 3", "Незеритка", "Читерский рай", "Лук", "Классик", "Тотемы", "Нодебафф")).defaultValue("Незеритка").build();
    private final IntegerSetting delay = Integer().name("Задержка (MS)").defaultValue(1000).min(300).max(1000).build();

    private int currentId = 1;
    private boolean isWaitingDuel = false;
    private long time;

    @Override
    public void onEnable() {
        isWaitingDuel = false;
        time = 0;
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (isWaitingDuel) return;

        if (System.currentTimeMillis() > time) {
            int fullWidth = mc.player.networkHandler.getPlayerList().stream().toList().size() - 1;
            if (fullWidth <= 1) return;

            if (currentId > fullWidth) currentId = 1;
            String name = Arrays.asList(mc.player.networkHandler.getPlayerList().stream().toList().get(currentId).getScoreboardTeam().getPlayerList().toArray()).toString().replace("[", "").replace("]", "");
            mc.player.sendChatMessage("/duel " + name);
            currentId++;
            time = System.currentTimeMillis() + delay.get();
            isWaitingDuel = true;
        }
    }

    @Override
    public void tick(TickEvent.Post event) {
        if (mc.player.currentScreenHandler instanceof GenericContainerScreenHandler chest) {
            if (mc.currentScreen.getTitle().getString().toLowerCase().contains("выбор") && mc.currentScreen.getTitle().getString().toLowerCase().contains("набора")) {
                for (int i = 0; i < chest.getInventory().size(); i++) {
                    Slot slot = chest.getSlot(i);
                    if (slot.hasStack() && isDuelItem(slot.getStack(), i)) {
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.QUICK_MOVE, mc.player);
                        break;
                    }
                }

                return;
            }

            if (mc.currentScreen.getTitle().getString().toLowerCase().contains("настройка") && mc.currentScreen.getTitle().getString().toLowerCase().contains("поединка")) {
                for (int i = 0; i < chest.getInventory().size(); i++) {
                    Slot slot = chest.getSlot(i);
                    if (slot.hasStack() && slot.getStack().getItem() == Items.LIME_STAINED_GLASS_PANE) {
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.QUICK_MOVE, mc.player);
                        isWaitingDuel = false;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onPacket(PacketEvent.Receive event) {
        if (event.packet instanceof GameMessageS2CPacket packet) {
            String message = packet.getMessage().getString().toLowerCase();

            if (message.contains("уже") || message.contains("невозможно") || (message.contains("игрок") && message.contains("найден")) || message.contains("отключил")) {
                isWaitingDuel = false;
            }

            if (message.contains("начало") && message.contains("через") && message.contains("секунд") || message.equals("во время поединка запрещено использовать команды")) {
                toggle();
            }
        }
    }

    private boolean isDuelItem(ItemStack itemStack, int swordSlot) {
        return switch (duel.get()) {
            case "Шары" -> itemStack.getItem() instanceof SkullItem;
            case "Щит" -> itemStack.getItem() instanceof ShieldItem;
            case "Шипы 3" -> itemStack.getItem() == Items.NETHERITE_CHESTPLATE;
            case "Незеритка" -> itemStack.getItem() == Items.NETHERITE_SWORD;
            case "Читерский рай" -> itemStack.getItem() == Items.ENCHANTED_GOLDEN_APPLE;
            case "Лук" -> itemStack.getItem() == Items.BOW;
            case "Классик" -> itemStack.getItem() == Items.NETHERITE_SWORD && swordSlot != 3;
            case "Тотемы" -> itemStack.getItem() == Items.TOTEM_OF_UNDYING;
            default -> itemStack.getItem() instanceof SplashPotionItem;
        };
    }
}