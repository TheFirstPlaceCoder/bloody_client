package com.client.utils.game.inventory;

import api.interfaces.EventHandler;
import com.client.event.events.KeyboardInputEvent;
import com.client.utils.auth.Loader;
import com.client.utils.game.entity.SelfUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.function.Predicate;

import static com.client.BloodyClient.mc;

public class InvUtils {
    public static final Action ACTION = new Action();
    private static int PREVENT_SLOT = -1;
    private static int LAST_UPDATE = -1;

    public static boolean swap(FindItemResult itemResult) {
        return swap(itemResult.slot(), false);
    }

    public static boolean swap(int slot) {
        return swap(slot, false);
    }

    public static boolean swap(FindItemResult itemResult, boolean packet) {
        return swap(itemResult.slot(), packet);
    }

    public static boolean swap(int slot, boolean packet) {
        if (slot > SlotUtils.HOTBAR_END || slot < SlotUtils.HOTBAR_START) return false;
        PREVENT_SLOT = SelfUtils.getSelectedSlot();
        if (packet) {
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
        } else {
            SelfUtils.setCurrentSlot(slot);
        }
        LAST_UPDATE = SelfUtils.getSelectedSlot();
        return true;
    }

    public static boolean swapBack() {
        return swapBack(false);
    }

    public static boolean swapBack(boolean packet) {
        if (PREVENT_SLOT == -1) return false;
        if (packet) {
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(PREVENT_SLOT));
        } else {
            SelfUtils.setCurrentSlot(PREVENT_SLOT);
        }
        if (LAST_UPDATE != PREVENT_SLOT) {
            LAST_UPDATE = -1;
        }
        PREVENT_SLOT = -1;
        return true;
    }

    public static int getId(ItemStack stack) {
        int id = 0;

        for (char aByte : stack.getItem().getTranslationKey().toCharArray()) {
            id++;
        }

        for (Text text : stack.getTooltip(mc.player, TooltipContext.Default.ADVANCED)) {
            for (char aByte : text.getString().toCharArray()) {
                id++;
            }
        }

        return id;
    }

    public static void moveItem(int from, int to, boolean air) {
        if (from == to) return;
        pickupItem(from, 0);
        pickupItem(to, 0);
        if (air) pickupItem(from, 0);
    }

    public static void pickupItem(int slot, int button) {
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, button, SlotActionType.PICKUP, mc.player);
    }

    public static void updateSlot(int slot) {
        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
    }

    public static int getEmptySlot() {
        for (int i = 0; i < SlotUtils.MAIN_END; i++) {
            if (mc.player.inventory.getStack(i).isEmpty()) return i;
        }
        return -1;
    }

    private static long MOVE_ITEM = 0;

    public static void grimSwap(Runnable action) {
        MOVE_ITEM = System.currentTimeMillis() + 50L;
        mc.player.input.movementSideways = 0f;
        mc.player.input.movementForward = 0f;
        action.run();
        mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
    }

    private void unpressed() {
        mc.options.keyForward.setPressed(false);
        mc.options.keyBack.setPressed(false);
        mc.options.keyRight.setPressed(false);
        mc.options.keyLeft.setPressed(false);
        mc.options.keyJump.setPressed(false);
        mc.options.keySneak.setPressed(false);
    }

    @EventHandler
    private void onKeyboardInputEvent(KeyboardInputEvent event) {
        if (MOVE_ITEM > System.currentTimeMillis()) {
            event.pressingBack = false;
            event.pressingForward = false;
            event.pressingRight = false;
            event.pressingLeft = false;
            event.forward = 0.0F;
            event.sideways = 0.0F;
            event.cancel();
        }
    }

    public static FindItemResult findInHotbar(Predicate<ItemStack> items) {
        if (items.test(mc.player.getOffHandStack())) {
            return new FindItemResult(SlotUtils.OFFHAND, mc.player.getOffHandStack().getCount());
        }

        if (items.test(mc.player.getMainHandStack())) {
            return new FindItemResult(mc.player.inventory.selectedSlot, mc.player.getMainHandStack().getCount());
        }

        return find(items, SlotUtils.HOTBAR_END);
    }

    public static FindItemResult findInHotbar(Item... items) {
        return find(i -> Arrays.stream(items).filter(it -> it.equals(i.getItem())).toArray().length > 0, SlotUtils.HOTBAR_END);
    }

    public static FindItemResult find(Predicate<ItemStack> items) {
        if (items.test(mc.player.getOffHandStack())) {
            return new FindItemResult(SlotUtils.OFFHAND, mc.player.getOffHandStack().getCount());
        }

        if (items.test(mc.player.getMainHandStack())) {
            return new FindItemResult(mc.player.inventory.selectedSlot, mc.player.getMainHandStack().getCount());
        }

        return find(items, mc.player.inventory.size());
    }

    public static FindItemResult find(Item... items) {
        return find(i -> Arrays.stream(items).filter(it -> it.equals(i.getItem())).toArray().length > 0, mc.player.inventory.size());
    }

    private static FindItemResult find(Predicate<ItemStack> items, int end) {
        if (items == null) return new FindItemResult(-1, -1);

        int id = -1, count = 0;

        for (int i = 0; i < end; i++) {
            ItemStack stack = mc.player.inventory.getStack(i);
            if (stack.isEmpty() || !items.test(stack)) continue;

            if (id < 0) {
                id = i;
            }

            count += stack.getCount();
        }

        return new FindItemResult(id, count);

    }

    public static FindItemResult findFastestTool(BlockState state) {
        float bestScore = -1;
        int slot = -1;

        for (int i = 0; i < 9; i++) {
            float score = mc.player.inventory.getStack(i).getMiningSpeedMultiplier(state);
            if (score > bestScore) {
                bestScore = score;
                slot = i;
            }
        }

        return new FindItemResult(slot, 1);
    }

    public static Action quickSwap() {
        ACTION.type = SlotActionType.SWAP;
        return ACTION;
    }

    public static Action shiftClick() {
        ACTION.type = SlotActionType.QUICK_MOVE;
        return ACTION;
    }

    public static Action drop() {
        ACTION.type = SlotActionType.THROW;
        ACTION.data = 1;
        return ACTION;
    }

    public static Action move() {
        ACTION.type = SlotActionType.PICKUP;
        ACTION.two = true;
        return ACTION;
    }

    public static Action click() {
        ACTION.type = SlotActionType.PICKUP;
        return ACTION;
    }
}
