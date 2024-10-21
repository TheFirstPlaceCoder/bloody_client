package com.client.impl.command;

import com.client.system.command.Command;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.game.inventory.SlotUtils;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

import java.util.List;

public class DropCommand extends Command {
    public DropCommand() {
        super("Drop", List.of("drop"), List.of());
    }

    @Override
    public void command(String[] args) {
        drop();
    }

    public void drop() {
        mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));

        for (Integer index : SlotUtils.INDEX_LIST) {
            InvUtils.drop().slot(index);
        }

        mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
    }

    @Override
    public void error() {

    }
}
