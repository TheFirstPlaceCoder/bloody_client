package com.client.impl.function.movement;

import com.client.event.events.SendMovementPacketsEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.game.world.BlockUtils;
import com.client.utils.math.MsTimer;
import net.minecraft.item.BlockItem;

import java.util.List;

public class Scaffold extends Function {
    public final ListSetting mode = List().name("Режим").list(List.of("FunTime")).defaultValue("FunTime").build();
    public final IntegerSetting delay = Integer().name("delay").defaultValue(5).min(0).max(20).build();

    public Scaffold() {
        super("Scaffold", Category.MOVEMENT);
    }

    public MsTimer timer = new MsTimer();

    @Override
    public void onEnable() {
        timer.reset();
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (timer.passedTicks(delay.get())) {
            BlockUtils.place(mc.player.getBlockPos(), InvUtils.findInHotbar(itemStack -> itemStack.getItem() instanceof BlockItem), true, false, true);

            timer.reset();
        }
    }

    @Override
    public void sendMovementPackets(SendMovementPacketsEvent event) {
        event.pitch = 90;
        event.both = false;
    }
}
