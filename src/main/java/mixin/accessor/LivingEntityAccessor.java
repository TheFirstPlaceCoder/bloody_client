package mixin.accessor;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor("lastAttackedTicks")
    int getLastAttackedTicks();

    @Accessor("jumpingCooldown")
    void setLastJumpCooldown(int val);

    @Accessor("jumpingCooldown")
    int getLastJumpCooldown();
}