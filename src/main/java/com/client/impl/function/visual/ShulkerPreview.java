package com.client.impl.function.visual;

import api.interfaces.EventHandler;
import com.client.event.events.GetTooltipEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

/**
 * __aaa__
 * 20.05.2024
 */
public class ShulkerPreview extends Function {
    public ShulkerPreview() {
        super("Shulker Preview", Category.VISUAL);
    }

    @EventHandler
    private void onGetTooltipEvent(GetTooltipEvent eventModify) {
        if (hasItems(eventModify.itemStack)) {
            for (int s = 0; s < eventModify.list.size(); ++s) eventModify.y -= 10;
            eventModify.y -= 4;
        }
    }

    public static boolean hasItems(ItemStack itemStack) {
        NbtCompound compoundTag = itemStack.getSubTag("BlockEntityTag");
        return compoundTag != null && compoundTag.contains("Items", 9);
    }
}
