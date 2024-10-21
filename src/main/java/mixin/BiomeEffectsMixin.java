package mixin;

import com.client.event.events.CustomFogEvent;
import com.client.event.events.CustomSkyEvent;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.biome.BiomeEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin({BiomeEffects.class})
public abstract class BiomeEffectsMixin {
    @ModifyReturnValue(
            method = {"getSkyColor"},
            at = {@At("RETURN")}
    )
    private int getSkyColor(int original) {
        CustomSkyEvent event = new CustomSkyEvent();
        event.color = -1;
        event.post();

        return event.color != -1 ? event.color : original;
    }

//    @ModifyReturnValue(
//            method = {"getFogColor"},
//            at = {@At("RETURN")}
//    )
//    private int getFogColor(int original) {
//        CustomFogEvent event = new CustomFogEvent();
//        event.color = -1;
//        event.post();
//
//        return event.color != -1 ? event.color : original;
//    }
}