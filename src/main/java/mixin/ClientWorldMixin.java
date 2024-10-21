package mixin;

import com.client.event.events.EntityEvent;
import com.client.impl.function.client.Optimization;
import com.client.system.function.FunctionManager;
import com.client.utils.optimization.ConfigVariables;
import com.client.utils.optimization.EntityCullingBase;
import com.client.utils.optimization.interfaces.Cullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {
    @Shadow @Nullable public abstract Entity getEntityById(int id);

    private MinecraftClient mc = MinecraftClient.getInstance();

    @Inject(
            method = {"tickEntity(Lnet/minecraft/entity/Entity;)V"},
            at = {@At("HEAD")},
            cancellable = true
    )
    public void tickEntity(Entity entity, CallbackInfo info) {
        if (!FunctionManager.get(Optimization.class).isEnabled() || !FunctionManager.get(Optimization.class).rayTrace.get()) return;

        if (!ConfigVariables.tickCulling) {
            ++EntityCullingBase.instance.tickedEntities;
        } else if (entity != this.mc.player && entity != this.mc.cameraEntity && !entity.hasVehicle() && !entity.hasPassengers() && !(entity instanceof AbstractMinecartEntity)) {
            if (EntityCullingBase.instance.entityWhistelist.contains(entity.getType())) {
                ++EntityCullingBase.instance.tickedEntities;
            } else {
                if (entity instanceof Cullable) {
                    Cullable cull = (Cullable)entity;
                    if (cull.isCulled() || cull.isOutOfCamera()) {
                        this.basicTick(entity);
                        ++EntityCullingBase.instance.skippedEntityTicks;
                        info.cancel();
                        return;
                    }

                    cull.setOutOfCamera(true);
                }

                ++EntityCullingBase.instance.tickedEntities;
            }
        } else {
            ++EntityCullingBase.instance.tickedEntities;
        }
    }

    private void basicTick(Entity entity) {
        entity.resetPosition(entity.getX(), entity.getY(), entity.getZ());
        ++entity.age;
        if (entity instanceof LivingEntity) {
            ((LivingEntity)entity).tickMovement();
            if (((LivingEntity)entity).hurtTime > 0) {
                --((LivingEntity)entity).hurtTime;
            }
        }

    }

    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    private void addEntityPrivate(int id, Entity entity, CallbackInfo ci) {
        EntityEvent.Add add = new EntityEvent.Add(entity);
        add.post();
        if (add.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "removeEntity", at = @At("HEAD"), cancellable = true)
    private void removeEntity(int entityId, CallbackInfo ci) {
        EntityEvent.Remove remove = new EntityEvent.Remove(getEntityById(entityId));
        remove.post();
        if (remove.isCancelled()) {
            ci.cancel();
        }
    }
}