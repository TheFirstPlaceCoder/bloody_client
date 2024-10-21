package com.client.utils.game.inventory;

import net.minecraft.screen.slot.SlotActionType;

import static com.client.system.function.Function.mc;

public class Action {
    public SlotActionType type = null;
    public boolean two = false;
    public int from = -1;
    public int to = -1;
    public int data = 0;
    public int wId = -1;

    public boolean isRecursive = false;

    public Action() {}

    public Action id(int id) {
        this.wId = id;
        return this;
    }

    public Action fromId(int id) {
        from = id;
        return this;
    }

    public Action from(int index) {
        return fromId(SlotUtils.indexToId(index));
    }

    public Action fromHotbar(int i) {
        return from(SlotUtils.HOTBAR_START + i);
    }

    public Action fromOffhand() {
        return from(SlotUtils.OFFHAND);
    }

    public Action fromMain(int i) {
        return from(SlotUtils.MAIN_START + i);
    }

    public Action fromArmor(int i) {
        return from(SlotUtils.ARMOR_START + (3 - i));
    }

    // To

    public void toId(int id) {
        to = id;
        run();
    }

    public void to(int index) {
        toId(SlotUtils.indexToId(index));
    }

    public void toHotbar(int i) {
        to(SlotUtils.HOTBAR_START + i);
    }

    public void toOffhand() {
        to(SlotUtils.OFFHAND);
    }

    public void toMain(int i) {
        to(SlotUtils.MAIN_START + i);
    }

    public void toArmor(int i) {
        to(SlotUtils.ARMOR_START + (3 - i));
    }

    public void slotId(int id) {
        from = to = id;
        run();
    }

    public void slot(int index) {
        slotId(SlotUtils.indexToId(index));
    }

    public void slotHotbar(int i) {
        slot(SlotUtils.HOTBAR_START + i);
    }

    public void slotOffhand() {
        slot(SlotUtils.OFFHAND);
    }

    public void slotMain(int i) {
        slot(SlotUtils.MAIN_START + i);
    }

    public void slotArmor(int i) {
        slot(SlotUtils.ARMOR_START + (3 - i));
    }



    private void run() {
        boolean hadEmptyCursor = mc.player.inventory.getCursorStack().isEmpty();

        if (type == SlotActionType.SWAP) {
            data = from;
            from = to;
        }

        if (type != null && from != -1 && to != -1) {
            click(from);
            if (two) click(to);
        }

        SlotActionType preType = type;
        boolean preTwo = two;
        int preFrom = from;
        int preTo = to;

        type = null;
        two = false;
        from = -1;
        to = -1;
        data = 0;
        wId = -1;

        if (!isRecursive && hadEmptyCursor && preType == SlotActionType.PICKUP && preTwo && (preFrom != -1 && preTo != -1) && !mc.player.inventory.getCursorStack().isEmpty()) {
            isRecursive = true;
            InvUtils.click().slotId(preFrom);
            isRecursive = false;
        }
    }

    public void click(int id) {
        mc.interactionManager.clickSlot(wId != -1 ? wId : mc.player.currentScreenHandler.syncId, id, data, type, mc.player);
    }
}