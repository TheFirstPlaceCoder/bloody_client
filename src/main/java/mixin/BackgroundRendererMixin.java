package mixin;

import com.client.event.events.ApplyFogEvent;
import com.client.event.events.CustomFogDistanceEvent;
import com.client.event.events.CustomFogEvent;
import com.client.system.function.FunctionManager;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
    @Shadow
    private static float red;

    @Shadow
    private static float blue;

    @Shadow
    private static float green;

    /**
     * Checks if we should change the fog color to whatever the skybox set it to, and sets it.
     */
    @Inject(method = "render(Lnet/minecraft/client/render/Camera;FLnet/minecraft/client/world/ClientWorld;IF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/BackgroundRenderer;lastWaterFogColorUpdateTime:J", ordinal = 5))
    private static void modifyColors(Camera camera, float tickDelta, ClientWorld world, int i, float f, CallbackInfo ci) {
            CustomFogEvent event = new CustomFogEvent();
            event.color = null;
            event.post();
            if (event.color != null) {
                red = event.color.getRed();
                green = event.color.getGreen();
                blue = event.color.getBlue();
            }
    }

    @Inject(method = "applyFog", at = @At("TAIL"))
    private static void onApplyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo ci) {
        ApplyFogEvent event = new ApplyFogEvent();
        event.post();
        if (event.isCancelled()) {
            if (fogType == BackgroundRenderer.FogType.FOG_TERRAIN) {
                RenderSystem.fogStart(viewDistance * 4f);
                RenderSystem.fogEnd(viewDistance * 4.25f);
                RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
                RenderSystem.setupNvFogDistance();
            }
        } else {
            CustomFogDistanceEvent event1 = new CustomFogDistanceEvent();
            event1.post();
        }
//        } else {
//            if (FunctionManager.get(FogChanger.class).isEnabled()) {
//                FunctionManager.get(FogChanger.class).modifyFog();
//            }
//        }
    }

//    @Inject(method = "render", at = @At("TAIL"))
//    private static void onRender(Camera camera, float tickDelta, ClientWorld world, int i, float f, CallbackInfo ci) {
//        if (FunctionManager.get(FogChanger.class).isEnabled()) FunctionManager.get(FogChanger.class).t();
//    }
}
