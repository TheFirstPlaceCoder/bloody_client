package mixin.accessor;

import net.minecraft.entity.player.ItemCooldownManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemCooldownManager.class)
public interface ItemCooldownManagerAccessor {
    @Accessor("tick")
    int getTick();
}
