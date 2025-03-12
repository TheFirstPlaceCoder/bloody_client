package com.client.impl.function.client;

import com.client.event.events.PacketEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.utils.game.chat.ChatUtils;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.util.math.BlockPos;

public class TestModule extends Function {
    public TestModule() {
        super("TestModule", Category.CLIENT);
    }

    @Override
    public void onPacket(PacketEvent.Send send) {
        if (send.packet instanceof PlayerMoveC2SPacket || send.packet instanceof KeepAliveC2SPacket || send.packet.getClass().toString().contains("2809")) return;

        ChatUtils.info("Packet: " + send.packet.getClass().toString());
        if (send.packet instanceof ClickSlotC2SPacket clickSlotC2SPacket) {
            ChatUtils.info("clickSlotC2SPacket slot: " + clickSlotC2SPacket.getSlot());
            ChatUtils.info("clickSlotC2SPacket actionId: " + clickSlotC2SPacket.getActionId());
            ChatUtils.info("clickSlotC2SPacket stack: " + clickSlotC2SPacket.getStack());
            ChatUtils.info("clickSlotC2SPacket actionType: " + clickSlotC2SPacket.getActionType());
            ChatUtils.info("clickSlotC2SPacket button: " + clickSlotC2SPacket.getButton());
            ChatUtils.info("clickSlotC2SPacket syncId: " + clickSlotC2SPacket.getSyncId());
        } else if (send.packet instanceof PlayerActionC2SPacket playerActionC2SPacket) {
            ChatUtils.info("playerActionC2SPacket action: " + playerActionC2SPacket.getAction());
            ChatUtils.info("playerActionC2SPacket direction: " + playerActionC2SPacket.getDirection());
            ChatUtils.info("playerActionC2SPacket pos: " + playerActionC2SPacket.getPos().toShortString());
            ChatUtils.info("playerActionC2SPacket origin: " + (playerActionC2SPacket.getPos() == BlockPos.ORIGIN));
        } else if (send.packet instanceof PlayerInteractBlockC2SPacket playerInteractBlockC2SPacket) {
            ChatUtils.info("playerInteractBlockC2SPacket hand: " + playerInteractBlockC2SPacket.getHand());
            ChatUtils.info("playerInteractBlockC2SPacket blockpos: " + playerInteractBlockC2SPacket.getBlockHitResult().getBlockPos().toShortString());
            ChatUtils.info("playerInteractBlockC2SPacket pos: " + playerInteractBlockC2SPacket.getBlockHitResult().getPos().toString());
            ChatUtils.info("playerInteractBlockC2SPacket direction: " + playerInteractBlockC2SPacket.getBlockHitResult().getSide());
        }
    }
}
