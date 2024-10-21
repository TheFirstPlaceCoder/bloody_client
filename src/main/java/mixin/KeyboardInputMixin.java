package mixin;


import com.client.event.events.KeyboardInputEvent;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin extends Input {
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 5, shift = At.Shift.AFTER))
    private void tick(boolean slowDown, CallbackInfo ci) {
        KeyboardInputEvent event = new KeyboardInputEvent(movementForward, movementSideways, jumping, sneaking, 0.3D,
                pressingForward, pressingBack, pressingLeft, pressingRight);
        event.post();

        if (event.isCancelled()) {
            movementForward = event.forward;
            movementSideways = event.sideways;
            jumping = event.jumping;
            sneaking = event.sneaking;
            pressingForward = event.pressingForward;
            pressingBack = event.pressingBack;
            pressingLeft = event.pressingLeft;
            pressingRight = event.pressingRight;
        }
    }
}
