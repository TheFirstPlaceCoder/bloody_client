package com.client.impl.function.visual;

import com.client.event.events.*;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.*;
import com.client.utils.color.Colors;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;

import java.awt.*;
import java.util.List;

public class Ambience extends Function {
    public final ListSetting fog = List().name("Туман").enName("Fog Mode").list(List.of("Оставить", "Убрать", "Изменить")).defaultValue("Убрать").build();
    public final ListSetting fogColorMode = List().name("Режим цвета тумана").enName("Fog Color Mode").list(List.of("Клиентский", "Свой")).visible(() -> fog.get().equals("Изменить")).defaultValue("Клиентский").build();
    public final ColorSetting colorSetting = Color().name("Цвет").enName("Fog Color").defaultValue(Color.CYAN).visible(() -> fog.get().equals("Изменить") && fogColorMode.get().equals("Свой")).build();
    public final DoubleSetting fogStart = Double().name("Начало тумана").enName("Fog Start").defaultValue(5.0).min(0).max(10).visible(() -> fog.get().equals("Изменить")).build();
    public final DoubleSetting end = Double().name("Размытие").enName("Fog Unsaturation").defaultValue(5.0).min(0).max(10).visible(() -> fog.get().equals("Изменить")).build();

    public final BooleanSetting sky = Boolean().name("Изменить небо").enName("Change Sky").defaultValue(true).build();
    public final ColorSetting skycolorSetting = Color().name("Цвет неба").enName("Sky Color").defaultValue(Color.CYAN).visible(sky::get).build();

    private final ListSetting mode = List().list(List.of("Оставить","Ночь", "Утро", "Заход", "День", "Свой")).defaultValue("Заход").name("Время").enName("World Time").build();
    private final IntegerSetting custom = Integer().name("Значение").enName("Custom Time").min(0).max(120).defaultValue(0).visible(() -> mode.get().equals("Свой")).build();

    public final ListSetting totemParticles = List().name("Партиклы тотема").enName("Totem Particles Mode").list(List.of("Оставить", "Убрать", "Изменить")).defaultValue("Убрать").build();
    public final ListSetting colorMode = List().name("Режим цвета партиклов").enName("Totem Color Mode").list(List.of("Клиентский", "Статичный")).defaultValue("Клиентский").visible(() -> totemParticles.get().equals("Изменить")).build();
    public final ColorSetting colorParticles = Color().name("Цвет партиклов").enName("Particles Color").defaultValue(Color.CYAN).visible(() -> totemParticles.get().equals("Изменить") && colorMode.get().equals("Статичный")).build();
    public final DoubleSetting particlesSize = Double().name("Размер").enName("Particles Size").min(0).max(5).defaultValue(0.5).visible(() -> totemParticles.get().equals("Изменить")).build();

    public Ambience() {
        super("Ambience", Category.VISUAL);
    }

    private long serverTime = -1;

    @Override
    public void onDisable() {
        mc.world.setTimeOfDay(serverTime);
    }

    @Override
    public void onPacket(PacketEvent.Receive event) {
        if (event.packet instanceof WorldTimeUpdateS2CPacket) {
            serverTime = ((WorldTimeUpdateS2CPacket)event.packet).getTime();

            event.setCancelled(true);
        }
    }

    @Override
    public void onParticleRenderEvent(ParticleRenderEvent event) {
        if (totemParticles.get().equals("Убрать") && event.particle.getType().equals(ParticleTypes.TOTEM_OF_UNDYING)) event.cancel();
    }

    @Override
    public void tick(TickEvent.Post event) {
        switch (mode.get()) {
            case "День" -> mc.world.setTimeOfDay(5000);
            case "Заход" -> mc.world.setTimeOfDay(13000);
            case "Утро" -> mc.world.setTimeOfDay(0);
            case "Ночь" -> mc.world.setTimeOfDay(17000);
            case "Оставить" -> mc.world.setTimeOfDay(serverTime);
            default -> mc.world.setTimeOfDay(custom.get().longValue() * 200L);
        }
    }

    @Override
    public void onFog(CustomFogEvent event) {
        if (fog.get().equals("Изменить"))
            event.color = fogColorMode.get().equals("Свой") ? colorSetting.get() : Colors.getColor(0);
    }

    @Override
    public void onFogDistance(CustomFogDistanceEvent event) {
        if (!fog.get().equals("Изменить")) return;

        RenderSystem.fogStart(((fogStart.get().floatValue() / 4f) * 100f));
        RenderSystem.fogEnd(1 + ((end.get().floatValue() / 4f) * 100f));
        RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
        RenderSystem.setupNvFogDistance();
    }

    public Integer getSkyColor() {
        String hex = Integer.toHexString(skycolorSetting.get().getRGB()).substring(2);
        return Integer.parseInt(hex, 16);
    }

    @Override
    public void onSky(CustomSkyEvent event) {
        if (sky.get())
            event.color = getSkyColor();
    }

    @Override
    public void onApplyFogEvent(ApplyFogEvent event) {
        if (fog.get().equals("Убрать")) {
            event.setCancelled(true);
        }
    }
}
