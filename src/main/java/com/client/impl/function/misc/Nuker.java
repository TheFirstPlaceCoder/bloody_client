package com.client.impl.function.misc;

import com.client.event.events.*;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.ColorSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.game.entity.EntityUtils;
import com.client.utils.game.entity.PlayerUtils;
import com.client.utils.math.MsTimer;
import com.client.utils.render.wisetree.render.render3d.Renderer3D;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.GameMode;
import net.minecraft.world.RaycastContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Nuker extends Function {
    private final ListSetting mode = List().name("Режим").list(List.of("Обычный", "Быстрый")).defaultValue("Обычный").build();
    public final DoubleSetting radius_hor = Double().name("Радиус по горизонтали").defaultValue(4.5).min(0).max(7).build();
    public final DoubleSetting radius_ver = Double().name("Радиус по вертикали").defaultValue(2.0).min(0).max(7).build();
    public final DoubleSetting radius_mine = Double().name("Радиус ломания").defaultValue(4.5).min(0).max(6).build();
    public final IntegerSetting delay = Integer().name("Задержка ломания").defaultValue(10).min(0).max(200).visible(() -> mode.get().equals("Быстрый")).build();
    private final MultiBooleanSetting settings = MultiBoolean().name("Настройки").defaultValue(List.of(
            new MultiBooleanValue(true, "Игнорировать стены"),
            new MultiBooleanValue(false, "Избегать лаву"),
            new MultiBooleanValue(true, "На одном уровне"),
            new MultiBooleanValue(true, "Зачар Бульдозер/Бур")
    )).build();

    public final ListSetting color = List().name("Режим цвета").list(List.of("Клиентский", "Статичный")).defaultValue("Клиентский").build();
    public final ColorSetting colorSetting = Color().name("Цвет").defaultValue(Color.CYAN).visible(() -> color.get().equals("Статичный")).build();
    private final IntegerSetting alpha = Integer().name("Яркость").defaultValue(128).min(16).max(255).build();

    private BlockData blockData;
    private MsTimer breakTimer = new MsTimer();

    private NukerThread nukerThread = new NukerThread();
    private float rotationYaw, rotationPitch;

    public Nuker() {
        super("Nuker", Category.MISC);
    }

    @Override
    public void onEnable() {
        nukerThread = new NukerThread();
        nukerThread.setName("NukerThread");
        nukerThread.setDaemon(true);
        nukerThread.start();
    }

    @Override
    public void onDisable() {
        nukerThread.interrupt();
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (!nukerThread.isAlive()) {
            nukerThread = new NukerThread();
            nukerThread.setName("NukerThread");
            nukerThread.setDaemon(true);
            nukerThread.start();
        }
    }

    @Override
    public void onSetBlockState(SetBlockStateEvent e) {
        if (blockData != null && e.pos == blockData.bp && e.state.isAir()) {
            blockData = null;
            new Thread(() -> {
                if (!mc.options.keyAttack.isPressed() && blockData == null) {
                    blockData = getNukerBlockPos();
                }
            }).start();
        }
    }

    @Override
    public void sendMovementPackets(SendMovementPacketsEvent e) {
        if(rotationYaw != -999) {
            e.yaw = (rotationYaw);
            e.pitch = (rotationPitch);
            e.both = true;
            rotationYaw = -999;
        }
    }


    @Override
    public void onPlayerUpdate(PlayerUpdateEvent e) {
        if (blockData != null) {
            if ((PlayerUtils.distanceTo(blockData.bp) > radius_mine.get())
                    || mc.world.isAir(blockData.bp))
                blockData = null;
        }

        if (blockData == null || mc.options.keyAttack.isPressed()) return;

        float[] angle = PlayerUtils.calculateAngle(blockData.vec3d);
        rotationYaw = (angle[0]);
        rotationPitch = (angle[1]);

        if (mode.get().equals("Обычный")) {
            breakBlock();
        }
    }

    public synchronized void breakBlock() {
        if (blockData == null || mc.options.keyAttack.isPressed()) return;
        BlockPos cache = blockData.bp;
        mc.interactionManager.updateBlockBreakingProgress(blockData.bp, blockData.dir);
        mc.player.swingHand(Hand.MAIN_HAND);
        if (EntityUtils.getGameMode(mc.player) == GameMode.CREATIVE)
            mc.interactionManager.breakBlock(cache);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        BlockPos renderBp = null;

        if (blockData != null && blockData.bp != null)
            renderBp = blockData.bp;

        if (renderBp != null) {
            render(renderBp, color.get().equals("Статичный"), colorSetting.get(), alpha.get());
        }

        if (mode.get().equals("Быстрый") && breakTimer.passedMs(delay.get())) {
            breakBlock();
            breakTimer.reset();
        }
    }

    public BlockData getNukerBlockPos() {
        //int horRange = (int) (Math.floor(radius_hor.get()) + 1);
        //int verRange = (int) (Math.floor(radius_ver.get()) + 1);
        List<BlockPos> blocks_ = getCubePoses();

        for (BlockPos b : blocks_) {
            if (settings.get("На одном уровне") && ((b.getY() < mc.player.getY()) || (settings.get("Зачар Бульдозер") && b.getY() <= mc.player.getY())))
                continue;
            if (PlayerUtils.distanceTo(b) <= radius_mine.get()) {
                if (settings.get("Избегать лаву") && checkLava(b))
                    continue;
                if (isAllowed(b)) {
                    if (settings.get("Игнорировать стены")) {
                        BlockHitResult result = PlayerUtils.rayCastBlock(new RaycastContext(PlayerUtils.getEyesPos(mc.player), new Vec3d(b.getX() + 0.5, b.getY() + 0.5, b.getZ() + 0.5), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player), b);
                        if(result != null)
                            return new BlockData(b, result.getPos(), result.getSide());
                    } else {
                        for (float x1 = 0f; x1 <= 1f; x1 += 0.2f) {
                            for (float y1 = 0f; y1 <= 1; y1 += 0.2f) {
                                for (float z1 = 0f; z1 <= 1; z1 += 0.2f) {
                                    Vec3d p = new Vec3d(b.getX() + x1, b.getY() + y1, b.getZ() + z1);
                                    BlockHitResult bhr = mc.world.raycast(new RaycastContext(PlayerUtils.getEyesPos(mc.player), p, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player));
                                    if (bhr != null && bhr.getType() == HitResult.Type.BLOCK && bhr.getBlockPos().equals(b))
                                        return new BlockData(b, p, bhr.getSide());
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private List<BlockPos> getCubePoses() {
        List<BlockPos> poses = new ArrayList<>();

        int px = mc.player.getBlockPos().getX();
        int py = mc.player.getBlockPos().getY();
        int pz = mc.player.getBlockPos().getZ();

        for (int x = px - radius_hor.get().intValue(); x <= px + radius_hor.get().intValue(); x++) {
            for (int z = pz - radius_hor.get().intValue(); z <= pz + radius_hor.get().intValue(); z++) {
                for (int y = py - radius_ver.get().intValue(); y <= py + radius_ver.get().intValue() - 1; y++) {
                    BlockPos breakPos = new BlockPos(x, y, z);

                    poses.add(breakPos);
                }
            }
        }

        poses.sort(Comparator.comparingDouble(PlayerUtils::distanceTo));
        return poses;
    }

    private boolean checkLava(BlockPos base) {
        for (Direction dir : Direction.values())
            if (mc.world.getBlockState(base.offset(dir)).getBlock() == Blocks.LAVA)
                return true;
        return false;
    }

    public class NukerThread extends Thread {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (mc.world != null && mc.player != null) {
                        if (!mc.options.keyAttack.isPressed() && blockData == null) {
                            blockData = getNukerBlockPos();
                        }
                    } else {
                        Thread.yield();
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    private boolean isAllowed(BlockPos block) {
        return mc.world.getBlockState(block).getHardness(mc.world, block) > 0 && !(mc.world.getBlockState(block).getBlock() instanceof FluidBlock);
    }


    public void render(BlockPos blockPos, boolean isStatic, Color color, int alpha) {
        VoxelShape shape = mc.world.getBlockState(blockPos).getOutlineShape(mc.world, blockPos);

        double x1 = blockPos.getX();
        double y1 = blockPos.getY();
        double z1 = blockPos.getZ();
        double x2 = blockPos.getX() + 1;
        double y2 = blockPos.getY() + 1;
        double z2 = blockPos.getZ() + 1;

        if (!shape.isEmpty()) {
            x1 = blockPos.getX() + shape.getMin(Direction.Axis.X);
            y1 = blockPos.getY() + shape.getMin(Direction.Axis.Y);
            z1 = blockPos.getZ() + shape.getMin(Direction.Axis.Z);
            x2 = blockPos.getX() + shape.getMax(Direction.Axis.X);
            y2 = blockPos.getY() + shape.getMax(Direction.Axis.Y);
            z2 = blockPos.getZ() + shape.getMax(Direction.Axis.Z);
        }

        Box box = new Box(x1, y1, z1, x2, y2, z2);

        Renderer3D.prepare3d(false);

        Renderer3D.drawFilled(box, ColorUtils.injectAlpha(isStatic ? color : Colors.getColor(0), alpha));
        Renderer3D.drawOutline(box, ColorUtils.injectAlpha(isStatic ? color : Colors.getColor(0), alpha + 50));

        Renderer3D.end3d(false);
    }

    public record BlockData(BlockPos bp, Vec3d vec3d, Direction dir) {
    }
}