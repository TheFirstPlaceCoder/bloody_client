package com.client.impl.function.combat;

import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.DoubleSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class HitBox extends Function {
    public final DoubleSetting size = Double().name("Размер").defaultValue(1.0).min(0).max(2).build();

    public HitBox() {
        super("Hit Box", Category.COMBAT);
    }

    public double getEntityValue(Entity entity) {
        if (!isEnabled() || !(entity instanceof PlayerEntity p) || p == mc.player) return 0;
        return size.get();
    }
}