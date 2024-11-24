package com.client.impl.function.client;

import com.client.event.events.TickEvent;
import com.client.system.companion.CompanionRegistry;
import com.client.system.companion.DumboOctopusEntity;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.ColorSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.IntegerSetting;

import java.awt.*;

public class Companion extends Function {
    public final IntegerSetting teleportDistance = Integer().name("Дистанция для тп").min(20).max(100).defaultValue(50).build();
    public final BooleanSetting glow = Boolean().name("Glow").defaultValue(true).build();
    public final ColorSetting glowColor = Color().name("Цвет").defaultValue(new Color(255, 255, 255, 40)).visible(glow::get).build();

    public Companion() {
        super("Companion", Category.CLIENT);
    }

    public DumboOctopusEntity entity;
    public int yaw = 50;
    public int yawDelay = 30;
    public int delayBetween = 20;
    public int moveTicks = 30;
    public float speed = 0.02f;
    public double verSpeed = 0.05;

    @Override
    public void onEnable() {
        this.entity = new DumboOctopusEntity(CompanionRegistry.DUMBO_OCTOPUS.get(), mc.world);
        this.entity.setPosition(mc.player.getX(), mc.player.getY() + 1, mc.player.getZ());
        this.entity.setAiDisabled(false);

        mc.world.addEntity(999, entity);
    }

    @Override
    public void onDisable() {
        if (this.entity != null) {
            entity.kill();
            entity.remove();
            mc.world.removeEntity(entity.getEntityId());
            this.entity = null;
        }
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (this.entity != null) {
            this.entity.setWorld(mc.player.getEntityWorld());
            this.entity.tickMovement();
        }
    }
}
