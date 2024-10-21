package mixin;

import com.client.impl.function.visual.Ambience;
import com.client.system.function.FunctionManager;
import com.client.utils.color.Colors;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.TotemParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

@Mixin(TotemParticle.class)
public abstract class TotemParticleMixin extends AnimatedParticle {
    protected TotemParticleMixin(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider, float upwardsAcceleration) {
        super(world, x, y, z, spriteProvider, upwardsAcceleration);
    }

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void onPop(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider, CallbackInfo ci) {
        Ambience customPops = Objects.requireNonNull(FunctionManager.get(Ambience.class));
        if (customPops.isEnabled() && customPops.totemParticles.get().equals("Изменить")) {
            this.scale(customPops.particlesSize.get().floatValue());
            Color color = customPops.colorMode.get().equals("Статичный") ? customPops.colorParticles.get() : Colors.getColor(new Random().nextInt(360));
            this.setColor(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
        }
    }
}