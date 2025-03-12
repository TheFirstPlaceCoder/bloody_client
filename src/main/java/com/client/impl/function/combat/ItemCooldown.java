package com.client.impl.function.combat;

import com.client.event.events.FinishItemUseEvent;
import com.client.event.events.InteractItemEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.game.inventory.CooldownManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

import java.util.List;

public class ItemCooldown extends Function {
    public final MultiBooleanSetting listSetting = MultiBoolean().name("Предметы").enName("Items").defaultValue(List.of(
            new MultiBooleanValue(true, "Гэплы"),
            new MultiBooleanValue(true, "Чарки"),
            new MultiBooleanValue(true, "Перлы")
    )).build();

    public final ListSetting mode = List().name("Режим").enName("Mode").list(List.of("ReallyWorld", "Задержка")).defaultValue("ReallyWorld").build();
    public final IntegerSetting delay = Integer().name("Задержка").enName("Delay").defaultValue(4).min(0).max(10).visible(() -> !mode.get().equals("ReallyWorld")).build();

    public ItemCooldown() {
        super("Items Cooldown", Category.COMBAT);
    }

    @Override
    public void onFinishItemUse(FinishItemUseEvent event) {
        ItemStack itemStack = event.itemStack;

        if (mc.player.getItemCooldownManager().isCoolingDown(itemStack.getItem())) return;

        if ((listSetting.get(0) && itemStack.getItem() == Items.GOLDEN_APPLE) || (listSetting.get(1) && itemStack.getItem() == Items.ENCHANTED_GOLDEN_APPLE))
            mc.player.getItemCooldownManager().set(itemStack.getItem(), mode.get().equals("ReallyWorld") ? 100 : delay.get() * 20);
    }

    @Override
    public void onInteractItem(InteractItemEvent event) {
        Hand hand = event.hand;
        ItemStack itemStack = hand == Hand.MAIN_HAND ? mc.player.getMainHandStack() : mc.player.getOffHandStack();

        if (listSetting.get(2) && itemStack.getItem() == Items.ENDER_PEARL) {
            mc.player.getItemCooldownManager().set(itemStack.getItem(), mode.get().equals("ReallyWorld") ? 100 : delay.get() * 20);
            return;
        }

        if (mc.player.getItemCooldownManager().isCoolingDown(itemStack.getItem())) {
            mc.options.keyUse.setPressed(false);
        }
    }
}