package com.client.impl.function.movement;

import com.client.event.events.Render3DEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class TestFly extends Function {
    public final BooleanSetting renderEvent = Boolean().name("renderEvent").defaultValue(true).build();
    public final BooleanSetting interact = Boolean().name("interact").defaultValue(true).build();
    public final BooleanSetting swing = Boolean().name("Swing").defaultValue(true).build();
    public final BooleanSetting doubleH = Boolean().name("doubleH").defaultValue(true).build();

    public TestFly() {
        super("Test Fly", Category.MOVEMENT);
    }

    public boolean isSecBlock = false;

    @Override
    public void onEnable() {
        isSecBlock = false;
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (renderEvent.get()) return;

        if (!isSecBlock) {
            if (interact.get())
                mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(
                        new Vec3d(mc.player.getPos().x, 89, mc.player.getPos().z),
                        Direction.UP,
                        new BlockPos(mc.player.getBlockPos().getX(), 88, mc.player.getBlockPos().getZ()),
                        //mc.player.getBlockPos().down(2),
                        false
                ));
            else mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND,
                    new BlockHitResult(
                            new Vec3d(mc.player.getPos().x, 89, mc.player.getPos().z),
                            Direction.UP,
                            new BlockPos(mc.player.getBlockPos().getX(), 88, mc.player.getBlockPos().getZ()),
                            //mc.player.getBlockPos().down(2),
                            false
                    )));

            if (swing.get()) mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));

            isSecBlock = true;
        } else {
            if (doubleH.get()) {
                if (interact.get())
                    mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(
                            new Vec3d(mc.player.getPos().x, 90, mc.player.getPos().z),
                            Direction.UP,
                            new BlockPos(mc.player.getBlockPos().getX(), 89, mc.player.getBlockPos().getZ()),
                            false
                    ));
                else mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND,
                        new BlockHitResult(
                                new Vec3d(mc.player.getPos().x, 90, mc.player.getPos().z),
                                Direction.UP,
                                new BlockPos(mc.player.getBlockPos().getX(), 89, mc.player.getBlockPos().getZ()),
                                false
                        )));

                if (swing.get()) mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            }

            isSecBlock = false;
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (!renderEvent.get()) return;

        if (!isSecBlock) {
            if (interact.get())
                mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(
                        new Vec3d(mc.player.getPos().x, 89, mc.player.getPos().z),
                        Direction.UP,
                        new BlockPos(mc.player.getBlockPos().getX(), 88, mc.player.getBlockPos().getZ()),
                        //mc.player.getBlockPos().down(2),
                        false
                ));
            else mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND,
                    new BlockHitResult(
                            new Vec3d(mc.player.getPos().x, 89, mc.player.getPos().z),
                            Direction.UP,
                            new BlockPos(mc.player.getBlockPos().getX(), 88, mc.player.getBlockPos().getZ()),
                            //mc.player.getBlockPos().down(2),
                            false
                    )));

            if (swing.get()) mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));

            isSecBlock = true;
        } else {
            if (doubleH.get()) {
                if (interact.get())
                    mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(
                            new Vec3d(mc.player.getPos().x, 90, mc.player.getPos().z),
                            Direction.UP,
                            new BlockPos(mc.player.getBlockPos().getX(), 89, mc.player.getBlockPos().getZ()),
                            false
                    ));
                else mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND,
                        new BlockHitResult(
                                new Vec3d(mc.player.getPos().x, 90, mc.player.getPos().z),
                                Direction.UP,
                                new BlockPos(mc.player.getBlockPos().getX(), 89, mc.player.getBlockPos().getZ()),
                                false
                        )));

                if (swing.get()) mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            }

            isSecBlock = false;
        }
    }
}