package com.client.impl.function.movement;

import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public class WaterSpeed extends Function {
    public WaterSpeed() {
        super("Water Speed", Category.MOVEMENT);
    }

    private final ListSetting mode = List().name("Режим").list(List.of("Velocity", "Effect", "Attribute", "Dolphin", "Speed", "Test")).defaultValue("Velocity").build();
    public final IntegerSetting speedVel = Integer().name("speedVel").max(100).min(0).defaultValue(3).visible(() -> mode.get().equals("Velocity")).build();
    public final IntegerSetting effectLevel = Integer().name("effectLevel").max(6).min(0).defaultValue(1).visible(() -> mode.get().equals("Effect")).build();
    public final DoubleSetting attributePower = Double().name("attributePower").defaultValue(0.55).min(0).max(1).visible(() -> mode.get().equals("Attribute")).build();
    public final IntegerSetting dolphinLevel = Integer().name("dolphinLevel").max(2).min(0).defaultValue(1).visible(() -> mode.get().equals("Dolphin")).build();
    public final IntegerSetting speedLvl = Integer().name("speedLvl").max(2).min(0).defaultValue(1).visible(() -> mode.get().equals("Speed")).build();
    public final IntegerSetting slowFalling = Integer().name("slowFalling").max(2).min(0).defaultValue(1).visible(() -> mode.get().equals("Test")).build();

    @Override
    public void onEnable() {
        if (mode.get().equals("Test"))
            mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 2000, slowFalling.get(), false, true));
    }

    @Override
    public void onDisable() {
        mc.player.removeStatusEffect(StatusEffects.SLOW_FALLING);
    }

    @Override
    public void tick(TickEvent.Pre e) {
        if (!mc.player.isSwimming() && !mc.player.isSubmergedInWater()) return;
        String selectedType = mode.get();

        if (selectedType.equals("Velocity")) {
            WATER_FT();
        } else if (selectedType.equals("Effect")) {
            mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 200, effectLevel.get(), false, true));
        } else if (selectedType.equals("Attribute")) {
            mc.player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(attributePower.get());
        } else if (selectedType.equals("Dolphin")) {
            mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 200, dolphinLevel.get(), false, true));
        } else if (selectedType.equals("Speed")) {
            mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 200, speedLvl.get(), false, true));
        }
    }

    private void WATER_FT() {
        mc.player.setVelocity(mc.player.getVelocity().x * (1 + speedVel.get() / 100), mc.player.getVelocity().y, mc.player.getVelocity().z * (1 + speedVel.get() / 100));
    }
}