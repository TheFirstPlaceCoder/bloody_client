package com.client.impl.function.visual.trajectories;

import com.client.event.events.ESPRenderEvent;
import com.client.event.events.Render3DEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * __aaa__
 * 21.05.2024
 * */
public class Trajectories extends Function {
    public Trajectories() {
        super("Trajectories", Category.VISUAL);
    }

    private final BooleanSetting timeTo = Boolean().name("Время к приземлению").defaultValue(false).build();
    private final BooleanSetting pearl = Boolean().name("Жемчуг").defaultValue(true).build();
    private final BooleanSetting trident = Boolean().name("Трезубец").defaultValue(false).build();
    private final BooleanSetting arrows = Boolean().name("Стрелы").defaultValue(false).build();

    private final HashMap<Entity, Path> paths = new HashMap<>();

    @Override
    public void onEnable() {
        paths.clear();
    }

    @Override
    public void tick(TickEvent.Pre event) {
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof EnderPearlEntity enderPearlEntity && pearl.get() && !paths.containsKey(entity)) {
                paths.put(entity, new Path(enderPearlEntity));
            }
            if (entity instanceof TridentEntity tridentEntity && trident.get() && !paths.containsKey(entity)) {
                if (tridentEntity.getX() != tridentEntity.prevX || tridentEntity.getY() != tridentEntity.prevY || tridentEntity.getZ() != tridentEntity.prevZ) {
                    paths.put(entity, new Path(tridentEntity));
                }
            }
            if (entity instanceof ArrowEntity arrowEntity && arrows.get() && !paths.containsKey(entity)) {
                if (arrowEntity.getX() != arrowEntity.prevX || arrowEntity.getY() != arrowEntity.prevY || arrowEntity.getZ() != arrowEntity.prevZ) {
                    paths.put(entity, new Path(arrowEntity));
                }
            }
        }

        paths.entrySet().removeIf(a -> a.getValue().points.isEmpty() || mc.world.getEntityById(a.getValue().id) == null || !a.getKey().isAlive() || (a.getKey().getX() == a.getKey().prevX && a.getKey().getY() == a.getKey().prevY && a.getKey().getZ() == a.getKey().prevZ));
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        for (Map.Entry<Entity, Path> entityPathEntry : paths.entrySet()) {
            entityPathEntry.getValue().build(entityPathEntry.getKey());
            entityPathEntry.getValue().draw3d();
        }
    }

    @Override
    public void onRenderESP(ESPRenderEvent event) {
        if (timeTo.get()) {
            for (Map.Entry<Entity, Path> entityPathEntry : paths.entrySet()) {
                entityPathEntry.getValue().draw2d();
            }
        }
    }
}
