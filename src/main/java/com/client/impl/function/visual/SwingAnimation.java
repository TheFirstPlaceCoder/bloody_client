package com.client.impl.function.visual;

import com.client.impl.function.combat.aura.AttackAura;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;

import java.util.List;

public class SwingAnimation extends Function {
    public final ListSetting swingAnimation = List().list(List.of("Свайп", "Строгий", "Ванильный")).name("Режим").defaultValue("Свайп").build();
    public final BooleanSetting onlyAura = Boolean().name("Только с Attack Aura").defaultValue(false).build();
    public final IntegerSetting swingPower = Integer().name("Сила взмаха").defaultValue(12).min(2).max(30).build();
    public final DoubleSetting scale = Double().name("Размер").defaultValue(1d).min(0.1).max(2).build();
    public final DoubleSetting x = Double().name("X").defaultValue(0d).min(-5).max(5).build();
    public final DoubleSetting y = Double().name("Y").defaultValue(0d).min(-5).max(5).build();
    public final DoubleSetting z = Double().name("Z").defaultValue(0d).min(-5).max(5).build();

    public SwingAnimation() {
        super("Swing Animation", Category.VISUAL);
    }

    public boolean animation() {
        return isEnabled() && !onlyAura.get() || FunctionManager.get(AttackAura.class).isEnabled() && FunctionManager.get(AttackAura.class).target != null;
    }
}