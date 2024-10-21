package mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobVisibilityCache;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

@Mixin({MobVisibilityCache.class})
public class MobVisibilityCacheMixin {
    @Shadow
    @Final
    private MobEntity owner;
    private static Profiler prof = null;
    private final HashMap<Entity, Byte> entityVisibility = new HashMap();

    @Inject(
            at = {@At("HEAD")},
            method = {"clear"},
            cancellable = true
    )
    public void clear(CallbackInfo ci) {
        this.entityVisibility.clear();
        ci.cancel();
    }

    @Inject(
            at = {@At("HEAD")},
            method = {"canSee"},
            cancellable = true
    )
    public void canSee(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity == null) {
            cir.setReturnValue(false);
            cir.cancel();
        } else {
            if (prof == null) {
                prof = this.owner.world.getProfiler();
            }

            prof.push("canSee");
            byte visible = (Byte)this.entityVisibility.getOrDefault(entity, (byte)0);
            if (visible == 1) {
                prof.pop();
                cir.setReturnValue(true);
                cir.cancel();
            } else if (visible == 2) {
                prof.pop();
                cir.setReturnValue(false);
                cir.cancel();
            } else {
                boolean bl = this.owner.canSee(entity);
                if (bl) {
                    this.entityVisibility.put(entity, (byte)2);
                } else {
                    this.entityVisibility.put(entity, (byte)1);
                }

                prof.pop();
                cir.setReturnValue(bl);
                cir.cancel();
            }

        }
    }
}