package com.client.impl.function.player;

import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class AutoFarm extends Function {
    public final MultiBooleanSetting items = MultiBoolean().name("Предметы для фарма").enName("Farm items").defaultValue(List.of(
            new MultiBooleanValue(true, "Морковь"),
            new MultiBooleanValue(true, "Картошка"),
            new MultiBooleanValue(true, "Пшеница")
    )).build();

    public AutoFarm() {
        super("Auto Farm", Category.PLAYER);
    }

    private final Set<BlockPos> brokenBlocks = new HashSet<>();
    private final Map<BlockPos, Long> blockBreakingTimes = new HashMap<>();
    private boolean running = false;
    private Thread farmThread;

    @Override
    public void onEnable() {
        this.running = true;
        this.farmThread = new Thread(() -> {
            while (this.running) {
                if (mc == null || mc.player == null || mc.world == null) continue;
                this.nukeBlocks();
                this.clearBrokenBlocks();
                try {
                    Thread.sleep(1L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        this.farmThread.start();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.running = false;
        if (this.farmThread != null) {
            this.farmThread.interrupt();
        }
    }

    private void clearBrokenBlocks() {
        long currentTime = System.currentTimeMillis();
        brokenBlocks.removeIf(pos -> currentTime - blockBreakingTimes.getOrDefault(pos, currentTime) >= 400);
    }

    private double getDistanceSquared(BlockPos pos1, BlockPos pos2) {
        double dx = pos1.getX() - pos2.getX();
        double dy = pos1.getY() - pos2.getY();
        double dz = pos1.getZ() - pos2.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    private void nukeBlocks() {
        if (mc != null && mc.world != null && mc.player != null) {
            BlockPos playerPos = new BlockPos(mc.player.getPos());
            int rangeValue = 4;
            List<BlockPos> blockPositions = new ArrayList<>();


            for (int x = -rangeValue; x <= rangeValue; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -rangeValue; z <= rangeValue; z++) {
                        BlockPos blockPos = playerPos.add(x, y, z);
                        Block block = mc.world.getBlockState(blockPos).getBlock();


                        if ((items.get("Морковь") && block == Blocks.CARROTS) || (items.get("Картошка") && block == Blocks.POTATOES) || (items.get("Пшеница") && block == Blocks.WHEAT)) {

                            Block blockBelow = mc.world.getBlockState(blockPos.down()).getBlock();
                            if (blockBelow == Blocks.FARMLAND) {
                                blockPositions.add(blockPos);
                            }
                        }
                    }
                }
            }


            blockPositions.removeIf(this.brokenBlocks::contains);
            blockPositions.sort(Comparator.comparingDouble(pos -> this.getDistanceSquared(pos, playerPos)));


            List<BlockPos> blocksToBreak = blockPositions.subList(0, Math.min(1, blockPositions.size()));


            blocksToBreak.forEach(blockToBreak -> {
                if (!this.brokenBlocks.contains(blockToBreak)) {
                    try {
                        mc.player.networkHandler.sendPacket(
                                new PlayerInteractBlockC2SPacket(
                                        Hand.OFF_HAND,
                                        new BlockHitResult(new Vec3d(blockToBreak.getX(), blockToBreak.getY(), blockToBreak.getZ()), Direction.UP, blockToBreak, true)
                                )
                        );


                        Thread.sleep(15);


                        for (int i = 0; i < 3; i++) {
                            mc.player.networkHandler.sendPacket(
                                    new PlayerInteractBlockC2SPacket(
                                            Hand.MAIN_HAND,
                                            new BlockHitResult(new Vec3d(blockToBreak.getX(), blockToBreak.getY(), blockToBreak.getZ()), Direction.UP, blockToBreak, true)
                                    )
                            );
                            Thread.sleep(5);
                        }


                        Thread.sleep(40);


                        mc.player.networkHandler.sendPacket(
                                new PlayerActionC2SPacket(
                                        PlayerActionC2SPacket.Action.START_DESTROY_BLOCK,
                                        blockToBreak,
                                        Direction.UP
                                )
                        );


                        Thread.sleep(3);

                        mc.player.networkHandler.sendPacket(
                                new PlayerInteractBlockC2SPacket(
                                        Hand.OFF_HAND,
                                        new BlockHitResult(new Vec3d(blockToBreak.getX(), blockToBreak.getY(), blockToBreak.getZ()), Direction.UP, blockToBreak, true)
                                )
                        );

                        this.brokenBlocks.add(blockToBreak);
                        this.blockBreakingTimes.put(blockToBreak, System.currentTimeMillis());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}