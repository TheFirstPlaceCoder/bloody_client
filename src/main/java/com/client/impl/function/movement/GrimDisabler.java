package com.client.impl.function.movement;

import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.utils.game.inventory.InvUtils;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class GrimDisabler extends Function {
    public final IntegerSetting tridentDelay = Integer().name("trident Delay").defaultValue(5).min(0).max(20).build();
    public final BooleanSetting eatPause = Boolean().name("Пауза при еде").defaultValue(true).build();

    public GrimDisabler() {
        super("Grim Disabler", Category.MOVEMENT);
    }

    private int currentTick = 0;

    @Override
    public void onEnable() {
        currentTick = tridentDelay.get();
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (currentTick >= tridentDelay.get()){
            currentTick = 0;
            assert mc.player != null;
            int tridentSlot = InvUtils.findInHotbar(Items.TRIDENT).slot();
            int oldSlot = mc.player.inventory.selectedSlot;
            if (tridentSlot == -1 || (eatPause.get() && mc.player.isUsingItem())) return;
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(tridentSlot));
            mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(oldSlot));
        } else {
            currentTick++;
        }
    }
}
