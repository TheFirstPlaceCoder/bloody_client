package com.client.impl.function.visual;

import com.client.event.events.PacketEvent;
import com.client.event.events.Render3DEvent;
import com.client.event.events.StartBreakingBlockEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.color.ColorUtils;
import com.client.utils.math.MsTimer;
import com.client.utils.render.wisetree.render.render3d.Renderer3D;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class XRay extends Function {
    private final MultiBooleanSetting targetBlocks = MultiBoolean().name("Блоки").enName("Blocks").defaultValue(List.of(
            new MultiBooleanValue(false, "Уголь"),
            new MultiBooleanValue(true, "Железо"),
            new MultiBooleanValue(true, "Золото"),
            new MultiBooleanValue(false, "Редстоун"),
            new MultiBooleanValue(false, "Лазурит"),
            new MultiBooleanValue(true, "Алмазы"),
            new MultiBooleanValue(false, "Изумруд"),
            new MultiBooleanValue(false, "Кварц"),
            new MultiBooleanValue(true, "Древние обломки")
    )).build();

    public final BooleanSetting packetMode = Boolean().name("Сканирование ответов сервера").enName("Packet Mode").defaultValue(true).build();
    public final BooleanSetting scanMode = Boolean().name("Сканировать территорию").enName("Scan Mode").defaultValue(true).build();
    private final IntegerSetting radius = Integer().name("Радиус сканирования").enName("Radius").defaultValue(128).min(0).max(128).visible(scanMode::get).build();
    private final IntegerSetting timeBeforeUpdate = Integer().name("Задержка обновления").enName("Update Timer").defaultValue(1).min(0).max(5).visible(scanMode::get).build();

    private final IntegerSetting alpha = Integer().name("Яркость").enName("Brightness").defaultValue(128).min(0).max(255).build();

    public XRay() {
        super("X-RAY", Category.VISUAL);
        oreColors.put(Blocks.COAL_ORE, new Color(47, 44, 54));
        oreColors.put(Blocks.COAL_BLOCK, new Color(47, 44, 54));
        oreColors.put(Blocks.IRON_ORE, new Color(236, 173, 119));
        oreColors.put(Blocks.IRON_BLOCK, new Color(236, 173, 119));
        oreColors.put(Blocks.GOLD_ORE, new Color(247, 229, 30));
        oreColors.put(Blocks.GOLD_BLOCK, new Color(247, 229, 30));
        oreColors.put(Blocks.REDSTONE_ORE, new Color(245, 7, 23));
        oreColors.put(Blocks.REDSTONE_BLOCK, new Color(245, 7, 23));
        oreColors.put(Blocks.LAPIS_ORE, new Color(8, 26, 189));
        oreColors.put(Blocks.LAPIS_BLOCK, new Color(8, 26, 189));
        oreColors.put(Blocks.DIAMOND_ORE, new Color(33, 244, 255));
        oreColors.put(Blocks.DIAMOND_BLOCK, new Color(33, 244, 255));
        oreColors.put(Blocks.EMERALD_ORE, new Color(27, 209, 45));
        oreColors.put(Blocks.EMERALD_BLOCK, new Color(27, 209, 45));
        oreColors.put(Blocks.NETHER_QUARTZ_ORE, new Color(205, 205, 205));
        oreColors.put(Blocks.NETHER_GOLD_ORE, new Color(247, 229, 30));
        oreColors.put(Blocks.ANCIENT_DEBRIS, new Color(209, 27, 245));
        oreColors.put(Blocks.NETHERITE_BLOCK, new Color(209, 27, 245));
    }

    private final HashMap<Block, Color> oreColors = new HashMap<>();
    private final List<BlockToDisplay> poses = new CopyOnWriteArrayList<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private MsTimer clearTimer = new MsTimer();

    @Override
    public void onEnable() {
        poses.clear();
        clearTimer.reset();
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (!scanMode.get()) return;

        if (clearTimer.passedS(timeBeforeUpdate.get())) {
            poses.removeIf(b -> !isChecked(mc.world.getBlockState(b.pos).getBlock()));
            clearTimer.reset();
        }

        executorService.execute(() -> {
            int radius = this.radius.get();
            for (int x = -radius; x < radius; x++) {
                for (int z = -radius; z < radius; z++) {
                    for (int y = 0; y < 256; y++) {
                        BlockPos pos = new BlockPos(x, y, z);

                        Block block = mc.world.getBlockState(pos).getBlock();
                        if (isChecked(block) && poses.stream().noneMatch(e -> e.pos.equals(pos))) poses.add(new BlockToDisplay(pos, block));
                    }
                }
            }
        });
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        poses.forEach(b -> {
            Renderer3D.prepare3d(false);

            Renderer3D.drawFilled(b.pos, ColorUtils.injectAlpha(oreColors.get(b.block), alpha.get()));
            Renderer3D.drawOutline(b.pos, ColorUtils.injectAlpha(oreColors.get(b.block), alpha.get() + 50));

            Renderer3D.end3d(false);
        });
    }

    @Override
    public void onPacket(PacketEvent.Receive event) {
        if (packetMode.get() && event.packet instanceof BlockUpdateS2CPacket e && isChecked(e.getState().getBlock()) && poses.stream().noneMatch(a -> a.pos.equals(e.getPos())))
            poses.add(new BlockToDisplay(e.getPos(), e.getState().getBlock()));
    }

    public boolean isChecked(Block block) {
        return ((block == Blocks.COAL_ORE || block == Blocks.COAL_BLOCK) && targetBlocks.get(0))
                || ((block == Blocks.IRON_ORE || block == Blocks.IRON_BLOCK) && targetBlocks.get(1))
                || ((block == Blocks.GOLD_ORE || block == Blocks.GOLD_BLOCK || block == Blocks.NETHER_GOLD_ORE) && targetBlocks.get(2))
                || ((block == Blocks.REDSTONE_ORE || block == Blocks.REDSTONE_BLOCK) && targetBlocks.get(3))
                || ((block == Blocks.LAPIS_ORE || block == Blocks.LAPIS_BLOCK) && targetBlocks.get(4))
                || ((block == Blocks.DIAMOND_ORE || block == Blocks.DIAMOND_BLOCK) && targetBlocks.get(5))
                || ((block == Blocks.EMERALD_ORE || block == Blocks.EMERALD_BLOCK) && targetBlocks.get(6))
                || (block == Blocks.NETHER_QUARTZ_ORE && targetBlocks.get(7))
                || ((block == Blocks.ANCIENT_DEBRIS || block == Blocks.NETHERITE_BLOCK) && targetBlocks.get(8));
    }

    public record BlockToDisplay(BlockPos pos, Block block) {}
}