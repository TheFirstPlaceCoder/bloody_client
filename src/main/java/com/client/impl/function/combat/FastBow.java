package com.client.impl.function.combat;

import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.utils.game.inventory.InvUtils;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Items;

public class FastBow extends Function {
    public final BooleanSetting onlyRClick = Boolean().name("Только с R-Click").enName("Only R-Click").defaultValue(true).build();
    public final IntegerSetting delay = Integer().name("Задержка при использовании").enName("Delay").defaultValue(5).min(0).max(20).build();

    public FastBow() {
        super("Fast Bow", Category.COMBAT);
    }

    private boolean wasBow = false;
    private boolean wasHoldingRightClick = false;

    @Override
    public void onEnable() {
        wasBow = false;
        wasHoldingRightClick = false;
    }

    @Override
    public void onDisable() {
        setPressed(false);
    }

    @Override
    public void tick(TickEvent.Post event) {
        if (!mc.player.abilities.creativeMode && !InvUtils.find(itemStack -> itemStack.getItem() instanceof ArrowItem).found())
            return;

        if (!onlyRClick.get() || mc.options.keyUse.isPressed()) {
            boolean isBow = mc.player.getMainHandStack().getItem() == Items.BOW;
            if (!isBow && wasBow) setPressed(false);

            wasBow = isBow;
            if (!isBow) return;

            if (mc.player.getItemUseTime() >= delay.get()) {
                mc.player.stopUsingItem();
                mc.interactionManager.stopUsingItem(mc.player);
            } else {
                setPressed(true);
            }

            wasHoldingRightClick = mc.options.keyUse.isPressed();
        } else {
            if (wasHoldingRightClick) {
                setPressed(false);
                wasHoldingRightClick = false;
            }
        }
    }

    private void setPressed(boolean pressed) {
        mc.options.keyUse.setPressed(pressed);
    }
}
