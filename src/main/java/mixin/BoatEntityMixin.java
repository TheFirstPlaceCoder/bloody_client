package mixin;

import com.client.event.events.BoatMoveEvent;
import net.minecraft.entity.vehicle.BoatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BoatEntity.class, priority = 1050)
public class BoatEntityMixin {
    @Inject(method = "tick", at = @At(value = "TAIL", target = "Lnet/minecraft/entity/vehicle/BoatEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"))
    private void onTickInvokeMove(CallbackInfo info) {
        BoatMoveEvent boatMoveEvent = BoatMoveEvent.get((BoatEntity) (Object) this);
        boatMoveEvent.post();
    }
}