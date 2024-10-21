package com.client.impl.function.misc;

import com.client.event.events.PacketEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import net.minecraft.block.*;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class NoInteract extends Function {
    public NoInteract() {
        super("No Interact", Category.MISC);
    }

    private final MultiBooleanSetting filter = MultiBoolean().name("Работать на").defaultValue(List.of(
            new MultiBooleanValue(true, "Сундук"),
            new MultiBooleanValue(true, "Печки"),
            new MultiBooleanValue(true, "Наковальни"),
            new MultiBooleanValue(false, "Верстаки"),
            new MultiBooleanValue(false, "Воронки"),
            new MultiBooleanValue(false, "Э-Сундуки"),
            new MultiBooleanValue(false, "Кровати"),
            new MultiBooleanValue(true, "Якори"),
            new MultiBooleanValue(true, "Шалкеры")
    )).build();

    public boolean isValid(Block block) {
        return (block == Blocks.CHEST && filter.get(0))
                || (block == Blocks.FURNACE && filter.get(1))
                || (block instanceof AnvilBlock && filter.get(2))
                || (block == Blocks.CRAFTING_TABLE && filter.get(3))
                || (block == Blocks.HOPPER && filter.get(4))
                || (block == Blocks.ENDER_CHEST && filter.get(5))
                || (block instanceof BedBlock && filter.get(6))
                || (block == Blocks.RESPAWN_ANCHOR && filter.get(7))
                || (block instanceof ShulkerBoxBlock && filter.get(8));
    }

    @Override
    public void onPacket(PacketEvent.Send e) {
        if(!(e.packet instanceof PlayerInteractBlockC2SPacket)) return;

        BlockPos blockPos = ((PlayerInteractBlockC2SPacket) e.packet).getBlockHitResult().getBlockPos();

        if (isValid(mc.world.getBlockState(blockPos).getBlock())) e.setCancelled(true);
    }
}