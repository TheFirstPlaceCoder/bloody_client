package mixin;

import com.client.impl.function.movement.SafeWalk;
import com.client.interfaces.IKeyBinding;
import com.client.system.function.FunctionManager;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.client.BloodyClient.mc;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin implements IKeyBinding {
    @Shadow private InputUtil.Key boundKey;

    @Shadow public abstract boolean equals(KeyBinding other);
    @Shadow public abstract boolean isPressed();

    @Inject(method = "isPressed",at = @At("HEAD"),cancellable = true)
    private void pressHook(CallbackInfoReturnable<Boolean> cir){
        if (     this.equals(mc.options.keySneak)
                && mc.player != null
                && mc.world != null
                && FunctionManager.get(SafeWalk.class).isEnabled()
                && mc.player.isOnGround() && mc.world.getBlockState(new BlockPos((int) Math.floor(mc.player.getPos().getX()), (int) Math.floor(mc.player.getPos().getY()) - 1, (int) Math.floor(mc.player.getPos().getZ()))).isAir()
        ) {
            cir.setReturnValue(true);
        }
    }

    @Override
    public InputUtil.Key getBoundKey() {
        return boundKey;
    }
}