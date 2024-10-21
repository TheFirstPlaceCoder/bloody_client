package com.client.impl.function.misc.cheststealer;

import com.client.clickgui.cheststealer.cheststealer.ChestStealerGui;
import com.client.event.events.KeybindSettingEvent;
import com.client.event.events.TickEvent;
import com.client.impl.command.rct.RctFunctionLite;
import com.client.system.cheststealer.ChestStealerManager;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.*;
import com.client.utils.game.entity.PlayerUtils;
import com.client.utils.game.entity.ServerUtils;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.misc.InputUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChestStealer extends Function {
    public ChestStealer() {
        super("Auto Myst", Category.MISC);
    }

    private final Widget openGui = Widget().name("Открыть меню").defaultValue(() -> mc.openScreen(ChestStealerGui.getInstance())).build();
    public final ListSetting bypassClick = List().name("Обход клика").list(List.of(
            "Обычный", "New"
    )).defaultValue("New").build();
    private final BooleanSetting funtime = Boolean().name("Обход FunTime").defaultValue(true).build();
    private final ListSetting sortMode = List().list(List.of("Приоритет", "Только", "Нет")).name("Сортировка").defaultValue("Только").build();
    private final IntegerSetting delay = Integer().name("Задержка (MS)").min(0).max(1000).defaultValue(100).build();
    private final KeybindSetting openShulker = Keybind().name("Открыть мистик").defaultValue(-1).build();
    private final DoubleSetting distance = Double().name("Дистанция").defaultValue(3.3).max(6).min(0).visible(() -> openShulker.get() != -1).build();

    private long time;

    @Override
    public void onEnable() {
        time = 0;
    }

    @Override
    public void onKeybindSetting(KeybindSettingEvent event) {
        if (event.action == InputUtils.Action.PRESS) {
            if (!openShulker.key(event.key, !event.mouse)) return;
            for (int i = -5; i < 5; i++) {
                for (int j = -5; j < 5; j++) {
                    for (int k = -5; k < 5; k++) {
                        BlockPos pos = mc.player.getBlockPos().add(i, j, k);
                        if (PlayerUtils.distanceTo(pos) > distance.get()) continue;

                        Block block = mc.world.getBlockState(pos).getBlock();
                        if (block.equals(Blocks.ENDER_CHEST) || block.equals(Blocks.CHEST) || block instanceof ShulkerBoxBlock) {
                            mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(
                                    Vec3d.ofCenter(pos),
                                    Direction.DOWN,
                                    pos,
                                    false
                            ));
                            mc.player.swingHand(Hand.MAIN_HAND);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (mc.player.currentScreenHandler == null || RctFunctionLite.rct) return;

        if (mc.currentScreen != null && (ServerUtils.isHolyWorld() && (mc.currentScreen.getTitle().equals(new TranslatableText("container.enderchest"))) || mc.currentScreen.getTitle().getString().contains("Аукцион") || mc.currentScreen.getTitle().getString().contains("Покупка предмета"))) return;

        ScreenHandler screen = mc.player.currentScreenHandler;

        if (screen instanceof GenericContainerScreenHandler containerScreen) {
            Inventory inventory = containerScreen.getInventory();

            if (inventory.isEmpty()) {
                mc.player.closeScreen();
                return;
            }

            move(inventory);
        }
    }

    private void move(Inventory inventory) {
        List<Integer> slots = ChestStealerManager.getInv(inventory, sortMode.get());

        if (!bypassClick.get().equals("New")) {
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        }

        if (funtime.get()) {
            slots = new ArrayList<>();
            List<Integer> has = new ArrayList<>();
            for (int i = 0; i < inventory.size(); i++) {
                if (inventory.getStack(i).getItem().equals(Items.AIR)) continue;
                has.add(i);
            }
            Random random = new Random();
            for (Integer i : has) {
                int slot = has.get(random.nextInt(has.size()));
                while (slots.contains(slot)) {
                    slot = has.get(random.nextInt(has.size()));
                }
                slots.add(slot);
            }
        }

        for (Integer slot : slots) {
            if (ServerUtils.isHolyWorld() && ServerUtils.getAnarchy().contains("Лайт") && inventory.getStack(slot).getItem().equals(Items.TOTEM_OF_UNDYING) && totemCount() >= 4) continue;
            if (System.currentTimeMillis() >= time) {
                if (bypassClick.get().equals("New")) {
                    int air = getAir();
                    if (air != -1) {
                        if (ServerUtils.isHolyWorld() && ServerUtils.getAnarchy().contains("Классик")) {
                            InvUtils.move().fromId(slot).toId(air);
                        } else {
                            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, air, SlotActionType.SWAP, mc.player);
                        }
                    }
                } else {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.QUICK_MOVE, mc.player);
                }
                time = System.currentTimeMillis() + delay.get();
            }
        }

        if (!bypassClick.get().equals("New")) {
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        }
    }

    private int getAir() {
        int slot = -1;
        for (int i = 8; i < mc.player.inventory.size(); i++) {
            if (mc.player.inventory.getStack(i).getItem().equals(Items.AIR)) {
                slot = i;
                break;
            }
        }
        if (slot == -1) {
            for (int i = 0; i <= 8; i++) {
                if (mc.player.inventory.getStack(i).getItem().equals(Items.AIR)) {
                    slot = i;
                    break;
                }
            }
        }
        return slot;
    }

    private int totemCount() {
        int i = 0;
        for (int i1 = 0; i1 < mc.player.inventory.size(); i1++) {
            if (mc.player.inventory.getStack(i1).getItem().equals(Items.TOTEM_OF_UNDYING)) i++;
        }
        return i;
    }
}