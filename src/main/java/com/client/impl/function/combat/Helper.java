package com.client.impl.function.combat;

import com.client.event.events.KeybindSettingEvent;
import com.client.event.events.PacketEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.combat.aura.AttackAura;
import com.client.impl.function.combat.aura.rotate.RotationHandler;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.KeybindSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.Utils;
import com.client.utils.game.inventory.FindItemResult;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.game.inventory.SlotUtils;
import com.client.utils.misc.InputUtils;
import com.client.utils.misc.TaskTransfer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Helper extends Function {
    public Helper() {
        super("Helper", Category.COMBAT);
    }

    private final ListSetting mode = List().name("Сервер").enName("Server").list(List.of("HolyWorld", "FunTime", "None")).defaultValue("FunTime").build();
    public final IntegerSetting delay = Integer().name("Задержка").enName("Place Delay").defaultValue(2).min(0).max(6).build();
    private final BooleanSetting matrixBypass = Boolean().name("Обход Matrix").enName("Matrix Bypass").defaultValue(false).build();
    private final BooleanSetting excludeHotbar = Boolean().name("Не оставлять в хотбаре").enName("Exclude Hotbar").defaultValue(true).build();

    public final KeybindSetting pearl = Keybind().name("Перл").enName("Ender Pearl").defaultValue(-1).build();
    private final BooleanSetting bypass = Boolean().name("Обход ротации ауры").enName("Aura Rotation Bypass").defaultValue(true).visible(() -> pearl.get() != -1).build();

    private final KeybindSetting hw_trapka = Keybind().name("Трапка").defaultValue(-1).visible(() -> mode.get().equals("HolyWorld")).build();
    private final KeybindSetting hw_explosion_trapka = Keybind().name("Взрывная трапка").defaultValue(-1).visible(() -> mode.get().equals("HolyWorld")).build();
    private final KeybindSetting hw_stan = Keybind().name("Стан").defaultValue(-1).visible(() -> mode.get().equals("HolyWorld")).build();
    private final KeybindSetting hw_prochalniy_gul = Keybind().name("Прощальный гул").defaultValue(-1).visible(() -> mode.get().equals("HolyWorld")).build();
    private final KeybindSetting hw_blazerod = Keybind().name("Взрывная палочка").defaultValue(-1).visible(() -> mode.get().equals("HolyWorld")).build();

    private final KeybindSetting ft_trapka = Keybind().name("Трапка").defaultValue(-1).visible(() -> mode.get().equals("FunTime")).build();
    private final KeybindSetting ft_disorent = Keybind().name("Дезориентация").defaultValue(-1).visible(() -> mode.get().equals("FunTime")).build();
    private final KeybindSetting ft_plast = Keybind().name("Пласт").defaultValue(-1).visible(() -> mode.get().equals("FunTime")).build();
    private final KeybindSetting ft_smerch = Keybind().name("Огненный смерч").defaultValue(-1).visible(() -> mode.get().equals("FunTime")).build();
    private final KeybindSetting ft_yavnayapil = Keybind().name("Явная пыль").defaultValue(-1).visible(() -> mode.get().equals("FunTime")).build();
    private final KeybindSetting ft_godaura = Keybind().name("Божественная аура").defaultValue(-1).visible(() -> mode.get().equals("FunTime")).build();
    private final KeybindSetting ft_shulker = Keybind().name("Открыть шалкер").defaultValue(-1).visible(() -> mode.get().equals("FunTime")).build();

    private final BooleanSetting notification = Boolean().name("Уведомления").enName("Notification").defaultValue(true).build();

    private final TaskTransfer taskTransfer = new TaskTransfer();
    private int prev;
    private boolean shouldSwap = false, afterSwap = false;
    public static boolean sendPacket = false;

    @Override
    public void onEnable() {
        shouldSwap = false;
        afterSwap = false;
    }

    @Override
    public void tick(TickEvent.Pre event) {
        taskTransfer.handle();

        if (shouldSwap) {
            mc.player.inventory.selectedSlot = prev;
            shouldSwap = false;
        }
    }

    @Override
    public void onKeybindSetting(KeybindSettingEvent event) {
        if (event.action == InputUtils.Action.PRESS) {
            useItem(event, pearl, Items.ENDER_PEARL);

            switch (mode.get()) {
                case "HolyWorld" -> {
                    useItem(event, hw_trapka, Items.POPPED_CHORUS_FRUIT);
                    useItem(event, hw_explosion_trapka, Items.PRISMARINE_SHARD);
                    useItem(event, hw_stan, Items.NETHER_STAR);
                    useItem(event, hw_prochalniy_gul, Items.FIREWORK_STAR);
                    useItem(event, hw_blazerod, Items.BLAZE_ROD);
                }

                case "FunTime" -> {
                    useItem(event, ft_trapka, Items.NETHERITE_SCRAP);
                    useItem(event, ft_disorent, Items.ENDER_EYE);
                    useItem(event, ft_plast, Items.DRIED_KELP);
                    useItem(event, ft_smerch, Items.FIRE_CHARGE);
                    useItem(event, ft_yavnayapil, Items.SUGAR);
                    useItem(event, ft_godaura, Items.PHANTOM_MEMBRANE);
                    useItem(event, ft_shulker, Items.SHULKER_BOX);
                }
            }
        }
    }

    private void useItem(KeybindSettingEvent event, KeybindSetting setting, Item item) {
        FindItemResult findItemResult = InvUtils.find(item);
        if (item.equals(Items.SHULKER_BOX)) {
            findItemResult = InvUtils.find(i -> i.getItem().getTranslationKey().contains("shulker"));
        }

        if (!setting.key(event.key, !event.mouse)) return;

        if (!findItemResult.found()) {
            print(setting, 0);
            return;
        }

        if (mc.player.getItemCooldownManager().isCoolingDown(item)) {
            print(setting, 1);
            return;
        }

        if (item.equals(Items.SHULKER_BOX)) {
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, SlotUtils.indexToId(findItemResult.slot()), 1, SlotActionType.PICKUP, mc.player);
        } else {
            if (item.equals(Items.ENDER_PEARL)) {
                if (bypass.get() && FunctionManager.get(AttackAura.class).isEnabled()) {
                    RotationHandler.getHandler().getRotate().a = mc.player.yaw;
                    RotationHandler.getHandler().getRotate().b = mc.player.pitch;
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(mc.player.yaw, mc.player.pitch, mc.player.isOnGround()));
                    sendPacket = true;
                } else {
                    sendPacket = false;
                }
            }

            use(findItemResult.slot());
        }


        print(setting, 2);
    }

    private void use(int slot) {
        if (slot == 45) {
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.OFF_HAND);
            mc.player.swingHand(Hand.OFF_HAND);
        } else if (slot == mc.player.inventory.selectedSlot) {
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);
        } else if (SlotUtils.isHotbar(slot)) {
            if (matrixBypass.get()) {
                int slotToSwap = slot + 36;

                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slotToSwap, mc.player.inventory.selectedSlot, SlotActionType.SWAP, mc.player);

                mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
                mc.player.swingHand(Hand.MAIN_HAND);

                taskTransfer.bind(() -> {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slotToSwap, mc.player.inventory.selectedSlot, SlotActionType.SWAP, mc.player);

                    mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));

//                    taskTransfer.bind(() -> {
//                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, mc.player.inventory.selectedSlot + 36, mc.player.inventory.selectedSlot, SlotActionType.SWAP, mc.player);
//                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, mc.player.inventory.selectedSlot + 36, mc.player.inventory.selectedSlot, SlotActionType.SWAP, mc.player);
//
//                        mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
//                    }, delay.get() * 50L);
                }, delay.get() * 50L);
            } else {
                prev = mc.player.inventory.selectedSlot;
                mc.player.inventory.selectedSlot = slot;
                mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.inventory.selectedSlot));
                mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
                mc.player.swingHand(Hand.MAIN_HAND);
                taskTransfer.bind(() -> {
                    mc.player.inventory.selectedSlot = prev;
                    afterSwap = true;
                }, delay.get() * 50L);
            }
        } else {
            if (matrixBypass.get()) {
                int slotToSwap = slot >= 36 ? slot - 36 : slot;

                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slotToSwap, mc.player.inventory.selectedSlot, SlotActionType.SWAP, mc.player);

                mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
                mc.player.swingHand(Hand.MAIN_HAND);

                taskTransfer.bind(() -> {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slotToSwap, mc.player.inventory.selectedSlot, SlotActionType.SWAP, mc.player);

                    mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
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


    private void print(KeybindSetting setting, int i) {
        if (!notification.get()) return;
        String message = "";

        switch (i) {
            case 0: {
                if (setting.equals(hw_trapka)) message = message.concat("Трапка не найдена!");
                if (setting.equals(hw_explosion_trapka)) message = message.concat("Взрывная трапка не найдена!");
                if (setting.equals(hw_stan)) message = message.concat("Стан не найден!");
                if (setting.equals(hw_prochalniy_gul)) message = message.concat("Прощальный гул не найден!");
                if (setting.equals(hw_blazerod)) message = message.concat("Взрывная палочка не найдена!");
                if (setting.equals(ft_trapka)) message = message.concat("Трапка не найдена!");
                if (setting.equals(ft_disorent)) message = message.concat("Дезориентация не найдена!");
                if (setting.equals(ft_yavnayapil)) message = message.concat("Явная пыль не найдена!");
                if (setting.equals(ft_shulker)) message = "Шалкер не найден!";
                if (setting.equals(pearl)) message = Utils.isRussianLanguage ? "Эндер жемчуг не найден!" : "Ender pearl not found";

                break;
            }

            case 1: {
                if (setting.equals(hw_trapka)) message = message.concat("Трапка");
                if (setting.equals(hw_explosion_trapka)) message = message.concat("Взрывная трапка");
                if (setting.equals(hw_stan)) message = message.concat("Стан");
                if (setting.equals(hw_prochalniy_gul)) message = message.concat("Прощальный гул");
                if (setting.equals(hw_blazerod)) message = message.concat("Взрывная палочка");
                if (setting.equals(ft_trapka)) message = message.concat("Трапка");
                if (setting.equals(ft_disorent)) message = message.concat("Дезориентация");
                if (setting.equals(ft_yavnayapil)) message = message.concat("Явная пыль");
                if (setting.equals(pearl)) message = Utils.isRussianLanguage ? "Эндер жемчуг" : "Ender pearl";

                message = message.concat(Utils.isRussianLanguage ? " в кд!" : " has cooldown!");

                break;
            }

            case 2: {
                message = Utils.isRussianLanguage ? "Использовал " : "Just used ";

                if (setting.equals(hw_trapka)) message = message.concat("трапку");
                if (setting.equals(hw_explosion_trapka)) message = message.concat("взрывную трапку");
                if (setting.equals(hw_stan)) message = message.concat("стан");
                if (setting.equals(hw_prochalniy_gul)) message = message.concat("прощальный гул");
                if (setting.equals(hw_prochalniy_gul)) message = message.concat("взрывную палочку");
                if (setting.equals(ft_trapka)) message = message.concat("трапку");
                if (setting.equals(ft_disorent)) message = message.concat("дезориентацию");
                if (setting.equals(ft_yavnayapil)) message = message.concat("явную пыль");
                if (setting.equals(ft_shulker)) message = "Открыл шалкер";
                if (setting.equals(pearl)) message = message.concat(Utils.isRussianLanguage ? "эндер жемчуг" : "ender pearl");

                break;
            }
        }

        NotificationManager.add(new Notification(NotificationType.CLIENT, message, 2500L), NotificationManager.NotifType.Info);
    }
}