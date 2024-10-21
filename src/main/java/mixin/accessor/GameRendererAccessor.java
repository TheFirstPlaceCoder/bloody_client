package mixin.accessor;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
    @Accessor("zoom")
    void setZoom(float zoom);

    @Accessor("zoom")
    float getZoom();

    @Invoker("renderHand")
    void renderHand(MatrixStack matrices, Camera camera, float tickDelta);
}