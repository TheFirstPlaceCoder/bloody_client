package mixin;

import com.client.event.events.ParticleRenderEvent;
import com.client.event.events.WeatherWorldRenderEvent;
import com.client.impl.function.client.Optimization;
import com.client.impl.function.visual.BlockOutline;
import com.client.impl.function.visual.Freecam;
import com.client.impl.function.visual.Shaders;
import com.client.system.function.FunctionManager;
import com.client.utils.game.chat.ChatUtils;
import com.client.utils.optimization.ConfigVariables;
import com.client.utils.optimization.EntityCullingBase;
import com.client.utils.optimization.interfaces.Cullable;
import com.client.utils.optimization.interfaces.EntityRendererInter;
import com.client.utils.render.Outlines;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

import static com.client.BloodyClient.mc;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Shadow protected abstract void renderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers);
    @Shadow @Nullable private Framebuffer entityOutlinesFramebuffer;
    @Shadow private ClientWorld world;

    @Unique private float time = 0;
    @Unique private final Shaders shaders = FunctionManager.get(Shaders.class);

    @Final
    @Shadow
    private EntityRenderDispatcher entityRenderDispatcher;

    @Inject(
            at = {@At("HEAD")},
            method = {"renderEntity"},
            cancellable = true
    )
    private void renderEntity1(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo info) {
        if (!FunctionManager.get(Optimization.class).isEnabled() || !FunctionManager.get(Optimization.class).rayTrace.get()) return;

        Cullable cullable = (Cullable)entity;
        if (!cullable.isForcedVisible() && cullable.isCulled()) {
            EntityRenderer<Entity> entityRenderer = (EntityRenderer<Entity>) this.entityRenderDispatcher.getRenderer(entity);
            EntityRendererInter<Entity> entityRendererInter = (EntityRendererInter)entityRenderer;
            if (ConfigVariables.renderNametagsThroughWalls && matrices != null && vertexConsumers != null && entityRendererInter.shadowShouldShowName(entity)) {
                double x = MathHelper.lerp((double)tickDelta, entity.lastRenderX, entity.getX()) - cameraX;
                double y = MathHelper.lerp((double)tickDelta, entity.lastRenderY, entity.getY()) - cameraY;
                double z = MathHelper.lerp((double)tickDelta, entity.lastRenderZ, entity.getZ()) - cameraZ;
                Vec3d vec3d = entityRenderer.getPositionOffset(entity, tickDelta);
                double d = x + vec3d.x;
                double e = y + vec3d.y;
                double f = z + vec3d.z;
                matrices.push();
                matrices.translate(d, e, f);
                entityRendererInter.shadowRenderNameTag(entity, entity.getDisplayName(), matrices, vertexConsumers, this.entityRenderDispatcher.getLight(entity, tickDelta));
                matrices.pop();
            }

            ++EntityCullingBase.instance.skippedEntities;
            info.cancel();
        } else {
            ++EntityCullingBase.instance.renderedEntities;
            cullable.setOutOfCamera(false);
        }
    }

    @Inject(
            method = {"drawBlockOutline"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private void drawBlockOutline(MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (FunctionManager.get(BlockOutline.class).isEnabled()) {
            Color g = FunctionManager.get(BlockOutline.class).getFogColor();
            float R = (float) g.getRed() / 255.0F;
            float G = (float) g.getGreen() / 255.0F;
            float B = (float) g.getBlue() / 255.0F;
            float alpha = (float) g.getAlpha() / 255.0F;
            drawer(matrices, vertexConsumer, state.getOutlineShape(this.world, pos, ShapeContext.of(entity)), (double)pos.getX() - cameraX, (double)pos.getY() - cameraY, (double)pos.getZ() - cameraZ, R, G, B, alpha);
            ci.cancel();
        }
    }

    private void drawer(MatrixStack matrices, VertexConsumer vertexConsumer, VoxelShape voxelShape, double d, double e, double f, float g, float h, float i, float j) {
        Matrix4f matrix4f = matrices.peek().getModel();
        voxelShape.forEachEdge((k, l, m, n, o, p) -> {
            vertexConsumer.vertex(matrix4f, (float)(k + d), (float)(l + e), (float)(m + f)).color(g, h, i, j).next();
            vertexConsumer.vertex(matrix4f, (float)(n + d), (float)(o + e), (float)(p + f)).color(g, h, i, j).next();
        });
    }

    @Inject(method = "loadEntityOutlineShader", at = @At("TAIL"))
    private void onLoadEntityOutlineShader(CallbackInfo info) {
        Outlines.load();
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderHead(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo info) {
        ChatUtils.update();
        Outlines.beginRender();
    }

    @Inject(method = "renderEntity", at = @At("HEAD"))
    private void renderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo info) {
        if (vertexConsumers == Outlines.vertexConsumerProvider) return;

        if (shaders.isEnabled() && shaders.shouldDraw(entity)) {
            Color c0 = shaders.getColore(entity);

            Framebuffer prevBuffer = this.entityOutlinesFramebuffer;
            this.entityOutlinesFramebuffer = Outlines.outlinesFbo;

            Outlines.setUniform("resolution", (float) mc.getWindow().getScaledWidth(), (float) mc.getWindow().getScaledHeight());
            Outlines.setUniform("radius", shaders.lineWidth.get().floatValue());
            Outlines.setUniform("fillOpacity", (shaders.getColor(entity).getAlpha() / 255F));
            Outlines.setUniform("time", time);
            Outlines.setUniform("renderMode", shaders.getIndexOfMode());
            Outlines.setUniform("glowMode", shaders.getGlow());
            Outlines.setUniform("power", shaders.glowPower.get() / 10f);
            time += (shaders.speed.floatValue() / 1000);

            Outlines.vertexConsumerProvider.setColor(c0.getRed(), c0.getGreen(), c0.getBlue(), c0.getAlpha());
            renderEntity(entity, cameraX, cameraY, cameraZ, tickDelta, matrices, Outlines.vertexConsumerProvider);

            this.entityOutlinesFramebuffer = prevBuffer;
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/OutlineVertexConsumerProvider;draw()V"))
    private void onRender(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo info) {
        Outlines.endRender(tickDelta);
    }

    @Inject(method = "drawEntityOutlinesFramebuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/Framebuffer;draw(IIZ)V"))
    private void onDrawEntityOutlinesFramebuffer(CallbackInfo info) {
        Outlines.renderFbo();
    }

    @Inject(method = "onResized", at = @At("HEAD"))
    private void onResized(int i, int j, CallbackInfo info) {
        Outlines.onResized(i, j);
    }

    @Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true)
    private void renderWeather(LightmapTextureManager manager, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        WeatherWorldRenderEvent event = new WeatherWorldRenderEvent();
        event.post();
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;ZZDDDDDD)V", at = @At("HEAD"), cancellable = true)
    private void addParticle(ParticleEffect parameters, boolean shouldAlwaysSpawn, boolean important, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfo ci) {
        ParticleRenderEvent event = new ParticleRenderEvent(parameters);
        event.post();
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;setupTerrain(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/Frustum;ZIZ)V"), index = 4)
    private boolean renderSetupTerrainModifyArg(boolean spectator) {
        return FunctionManager.get(Freecam.class).isEnabled() || spectator;
    }

    @Inject(method = "checkEmpty", at = @At("HEAD"), cancellable = true)
    private void onCheckEmpty(MatrixStack matrixStack, CallbackInfo info) {
        info.cancel();
    }
}
