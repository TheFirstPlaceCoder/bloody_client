package com.client.impl.function.misc;

import com.client.event.events.Render3DEvent;
import com.client.event.events.SendMovementPacketsEvent;
import com.client.event.events.StartBreakingBlockEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.ColorSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.classes.MyBlock;
import com.client.utils.game.rotate.Rotations;
import com.client.utils.misc.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PacketMine extends Function {
    private final IntegerSetting delay = Integer().name("Задержка перед ломанием").enName("Break Delay").min(0).max(10).defaultValue(0).build();

    public final ListSetting color = List().name("Режим цвета").enName("Color Mode").list(List.of("Клиентский", "Статичный")).defaultValue("Клиентский").build();
    public final ColorSetting colorSetting = Color().name("Цвет").enName("Color").defaultValue(Color.CYAN).visible(() -> color.get().equals("Статичный")).build();
    private final IntegerSetting alpha = Integer().name("Яркость").enName("Brightness").defaultValue(128).min(16).max(255).build();

    public PacketMine() {
        super("Packet Mine", Category.MISC);
    }

    private final Pool<MyBlock> blockPool = new Pool<>(MyBlock::new);
    private final List<MyBlock> blocks = new ArrayList<>();

    @Override
    public void onDisable() {
        for (MyBlock block : blocks) blockPool.free(block);
        blocks.clear();
    }

    private boolean isMiningBlock(BlockPos pos) {
        for (MyBlock block : blocks) {
            if (block.blockPos.equals(pos)) return true;
        }

        return false;
    }

    @Override
    public void onBreakBlock(StartBreakingBlockEvent event) {
        event.cancel();

        if (mc.world.getBlockState(event.blockPos).getHardness(mc.world, event.blockPos) < 0) return;

        if (!isMiningBlock(event.blockPos)) {
            MyBlock block = blockPool.get();
            block.set(event, delay.get());
            blocks.add(block);
        }
    }

    @Override
    public void tick(TickEvent.Pre event) {
        blocks.removeIf(MyBlock::shouldRemove);

        if (!blocks.isEmpty()) blocks.get(0).mine();
    }

    @Override
    public void sendMovementPackets(SendMovementPacketsEvent event) {
        if (blocks.isEmpty() || blocks.get(0) == null || blocks.get(0).blockPos == null) return;

        Vec3d vec3d = new Vec3d(blocks.get(0).blockPos.getX() + 0.5, blocks.get(0).blockPos.getY() + 0.5, blocks.get(0).blockPos.getZ() + 0.5);
        event.both = true;
        event.yaw = (float) Rotations.getYaw(vec3d);
        event.pitch = (float) Rotations.getPitch(vec3d);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (blocks.isEmpty()) return;
        for (MyBlock block : blocks) block.render(color.get().equals("Статичный"), colorSetting.get(), alpha.get());
    }

    @Override
    public String getHudPrefix() {
        return blocks.isEmpty() ? "" : blocks.size() + "";
    }
}
