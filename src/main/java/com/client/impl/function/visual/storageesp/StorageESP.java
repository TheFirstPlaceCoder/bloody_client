package com.client.impl.function.visual.storageesp;

import com.client.event.events.Render3DEvent;
import com.client.impl.function.visual.Freecam;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.color.ColorUtils;
import com.client.utils.math.DistanceUtils;
import com.client.utils.render.wisetree.render.render3d.Renderer3D;
import net.minecraft.block.entity.*;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * __aaa__
 * 26.05.2024
 * */
public class StorageESP extends Function {
    public StorageESP() {
        super("Storage ESP", Category.VISUAL);
    }

    private final IntegerSetting max = Integer().name("Кол-во").defaultValue(512).min(64).max(1024).build();
    private final BooleanSetting tracers = Boolean().name("Трейсер").defaultValue(true).build();
    private final IntegerSetting alpha = Integer().name("Яркость").defaultValue(128).min(0).max(255).build();
    private final MultiBooleanSetting target = MultiBoolean().name("Цель").defaultValue(List.of(
            new MultiBooleanValue(true, "Сундук"),
            new MultiBooleanValue(true, "Эндер сундук"),
            new MultiBooleanValue(false, "Сундук ловушка"),
            new MultiBooleanValue(false, "Бочка"),
            new MultiBooleanValue(true, "Шалкер"),
            new MultiBooleanValue(false, "Воронка"),
            new MultiBooleanValue(false, "Раздатчик"),
            new MultiBooleanValue(false, "Выбрасыватель"),
            new MultiBooleanValue(true, "Энд портал")
    )).build();

    @Override
    public void onRender3D(Render3DEvent event) {
        List<BlockEntity> entities = new ArrayList<>(mc.world.blockEntities);

        if (!entities.isEmpty()) {
            entities.sort(Comparator.comparing(b -> DistanceUtils.distanceTo(b.getPos())));

            while (entities.size() > max.get()) {
                entities.remove(entities.size() - 1);
            }

            for (BlockEntity blockEntity : entities) {
                StorageType storageType = getType(blockEntity);
                if (storageType.equals(StorageType.UNKNOWN)) continue;

                Color color = storageType.color;

                if (blockEntity instanceof ShulkerBoxBlockEntity) {
                    try {
                        color = new Color(((ShulkerBoxBlockEntity) blockEntity).getColor().getMapColor().color);
                    } catch (Exception ignore) {
                    }
                }

                draw(color, blockEntity);
            }
        }
    }

    private void draw(Color color, BlockEntity entity) {
        Vec3d entityPos = Renderer3D.getRenderPosition(Vec3d.of(entity.getPos()).add(0.5, 0.5, 0.5));
        Vec3d eyePos = new Vec3d(0, 0, 150).rotateX((float) -(Math.toRadians(mc.cameraEntity.pitch))).rotateY((float) -Math.toRadians(mc.cameraEntity.yaw));

        if (FunctionManager.get(Freecam.class).isEnabled()) {
            eyePos = new Vec3d(0, 0, 150).rotateX((float) -(Math.toRadians(FunctionManager.get(Freecam.class).pitch))).rotateY((float) -Math.toRadians(FunctionManager.get(Freecam.class).yaw));
        }

        Renderer3D.prepare3d(false);

        Renderer3D.drawFilled(entity.getPos(), ColorUtils.injectAlpha(color, alpha.get()));
        Renderer3D.drawOutline(entity.getPos(), ColorUtils.injectAlpha(color, alpha.get() + 50));

        if (tracers.get()) {
            Renderer3D.drawLine(eyePos, entityPos, ColorUtils.injectAlpha(color, Math.min(150, alpha.get())), 1.5F);
        }

        Renderer3D.end3d(false);
    }

    private StorageType getType(BlockEntity entity) {
        if (entity instanceof ChestBlockEntity && target.get("Сундук")) {
            return StorageType.CHEST;
        }
        if (entity instanceof EnderChestBlockEntity && target.get("Эндер сундук")) {
            return StorageType.ENDER_CHEST;
        }
        if (entity instanceof BarrelBlockEntity && target.get("Бочка")) {
            return StorageType.BARREL;
        }
        if (entity instanceof ShulkerBoxBlockEntity && target.get("Шалкер")) {
            return StorageType.SHULKER;
        }
        if (entity instanceof EndPortalBlockEntity && target.get("Энд портал")) {
            return StorageType.END_PORTAL;
        }
        if (entity instanceof DispenserBlockEntity && target.get("Раздатчик")) {
            return StorageType.DISPENSER;
        }
        if (entity instanceof DropperBlockEntity && target.get("Выбрасыватель")) {
            return StorageType.DROPPER;
        }
        if (entity instanceof TrappedChestBlockEntity && target.get("Сундук ловушка")) {
            return StorageType.TRAPPED_CHEST;
        }
        if (entity instanceof HopperBlockEntity && target.get("Воронка")) {
            return StorageType.HOPPER;
        }
        return StorageType.UNKNOWN;
    }
}
