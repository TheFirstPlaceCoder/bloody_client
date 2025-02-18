package com.client.impl.function.combat;

import com.client.event.events.Render3DEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import net.minecraft.item.Items;

import java.util.List;

public class AutoGApple extends Function {
    public final MultiBooleanSetting listSetting = MultiBoolean().name("Предметы").enName("Items").defaultValue(List.of(
            new MultiBooleanValue(true, "Гэплы"),
            new MultiBooleanValue(true, "Чарки")
    )).build();

    public final IntegerSetting health = Integer().name("Здоровье").enName("Health").defaultValue(10).min(0).max(36).build();
    public final BooleanSetting absortion = Boolean().name("Золотые сердца").enName("Golden Points").defaultValue(true).build();

    public AutoGApple() {
        super("Auto GApple", Category.COMBAT);
    }

    private boolean isEating;

    private boolean shouldEatHealth() {
        double health = mc.player.getHealth() + (absortion.get() ? mc.player.getAbsorptionAmount() : 0);
        return health <= this.health.get().intValue();
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (this.canEat()) {
            this.startEating();
        } else if (this.isEating) {
            this.stopEating();
        }
    }

    public boolean canEat() {
        boolean a = mc.player.getOffHandStack().getItem() == Items.GOLDEN_APPLE && listSetting.get(0) || mc.player.getOffHandStack().getItem() == Items.ENCHANTED_GOLDEN_APPLE && listSetting.get(1);
        return mc.player.isAlive() && a && shouldEatHealth() && !mc.player.getItemCooldownManager().isCoolingDown(mc.player.getOffHandStack().getItem());
    }

    public void startEating() {
        if (!mc.options.keyUse.isPressed()) {
            mc.options.keyUse.setPressed(true);
            this.isEating = true;
        }
    }

    public void stopEating() {
        mc.options.keyUse.setPressed(false);
        this.isEating = false;
    }

    public boolean isEating() {
        return isEnabled() && isEating;
    }
}
