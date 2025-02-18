package mixin;

import api.main.EventUtils;
import com.client.BloodyClient;
import com.client.event.events.*;
import com.client.impl.function.visual.Freecam;
import com.client.interfaces.IGameRenderer;
import com.client.interfaces.IVec3d;
import com.client.system.function.FunctionManager;
import com.client.utils.render.Matrices;
import com.client.utils.render.TagUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import mixin.accessor.GameRendererAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements IGameRenderer {
    @Shadow @Final private MinecraftClient client;

    @Unique private boolean freecamSet;
    @Unique private boolean a = false;

    /**
     * @author .
     * @reason .
     */
    @Overwrite
    public void updateTargetedEntity(float tickDelta) {
        Entity entity = this.client.getCameraEntity();
        if (entity != null) {
            if (this.client.world != null) {
                this.client.getProfiler().push("pick");
                this.client.targetedEntity = null;
                double d = (double) this.client.interactionManager.getReachDistance();
                this.client.crosshairTarget = entity.raycast(d, tickDelta, false);
                Vec3d vec3d = entity.getCameraPosVec(tickDelta);
                boolean bl = false;
                double e = d;
                if (this.client.interactionManager.hasExtendedReach()) {
                    e = 6.0;
                    d = e;
                } else {
                    if (e > 3.0) {
                        bl = true;
                    }

                    d = e;
                }

                e *= e;
                if (this.client.crosshairTarget != null) {
                    e = this.client.crosshairTarget.getPos().squaredDistanceTo(vec3d);
                }

                Vec3d vec3d2 = entity.getRotationVec(1.0F);
                Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
                Box box = entity.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0, 1.0, 1.0);
                EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, vec3d, vec3d3, box, (entityx) -> !entityx.isSpectator() && entityx.collides(), e);
                if (entityHitResult != null) {
                    Entity entity2 = entityHitResult.getEntity();
                    Vec3d vec3d4 = entityHitResult.getPos();
                    double g = vec3d.squaredDistanceTo(vec3d4);
                    if (bl && g > 9.0) {
                        this.client.crosshairTarget = BlockHitResult.createMissed(vec3d4, Direction.getFacing(vec3d2.x, vec3d2.y, vec3d2.z), new BlockPos(vec3d4));
                    } else if (g < e || this.client.crosshairTarget == null) {
                        this.client.crosshairTarget = entityHitResult;
                        if (entity2 instanceof LivingEntity || entity2 instanceof ItemFrameEntity) {
                            this.client.targetedEntity = entity2;
                        }
                    }
                }

                this.client.getProfiler().pop();
            }
        }
    }

    @Override
    public Entity getTarget(float yaw, float pitch) {
        if (client.getCameraEntity() == null) return null;
        Entity cameraE = client.getCameraEntity();
        Entity entity;

        float prevYaw = cameraE.yaw;
        float prevPitch = cameraE.pitch;

        cameraE.yaw = yaw;
        cameraE.pitch = pitch;

        updateTargetedEntity(client.getTickDelta());
        entity = client.targetedEntity;

        cameraE.yaw = prevYaw;
        cameraE.pitch = prevPitch;
        return entity;
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderHead(float tickDelta, long startTime, boolean tick, CallbackInfo info) {
        a = false;
    }

    @Inject(method = "renderWorld", at = @At("HEAD"))
    private void onRenderWorldHead(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo info) {
        Matrices.begin(matrix);
        Matrices.push();
        RenderSystem.pushMatrix();

        a = true;
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = { "ldc=hand" }), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onRenderWorld(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo info, boolean shouldRenderBlockOutline, Camera camera, MatrixStack newMatrixStack, float distortionEffectScale, Matrix4f positionMatrix) {
        if (!BloodyClient.canUpdate()) return;

        GpsRenderEvent event = GpsRenderEvent.get(matrix, tickDelta, camera.getPos().x, camera.getPos().y, camera.getPos().z);

        TagUtils.onRender(matrix, positionMatrix);
        event.post();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clear(IZ)V", ordinal = 0))
    private void onRenderBeforeGuiRender(float tickDelta, long startTime, boolean tick, CallbackInfo info) {
        if (a) {
            Matrices.pop();
            RenderSystem.popMatrix();
        }
    }

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z", opcode = Opcodes.GETFIELD, ordinal = 0), method = "renderWorld", cancellable = true)
    private void render(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
        Render3DEvent event = new Render3DEvent(matrices, tickDelta);
        event.post();
        if (event.isCancelled()) {
            ci.cancel();
        }

        BloodyClient.shaderManager.renderShader(()-> ((GameRendererAccessor) client.gameRenderer).renderHand(matrices, client.gameRenderer.getCamera(), client.getTickDelta()));
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;renderHand(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/Camera;F)V", shift = At.Shift.AFTER))
    public void postRender3dHook(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        if (BloodyClient.shaderManager.fullNullCheck()) return;

        BloodyClient.shaderManager.renderShaders();
    }

    @Inject(method = "bobViewWhenHurt", at = @At("HEAD"), cancellable = true)
    public void bobViewWhenHurt(CallbackInfo ci) {
        HurtCamRenderEvent event = new HurtCamRenderEvent();
        event.post();
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    public void bobView(CallbackInfo ci) {
        HurtCamRenderEvent event = new HurtCamRenderEvent();
        event.post();
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "showFloatingItem", at = @At("HEAD"), cancellable = true)
    private void onShowFloatingItem(ItemStack floatingItem, CallbackInfo info) {
        FloatingItemRenderEvent event = new FloatingItemRenderEvent(floatingItem);
        event.post();
        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileUtil;raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;"), cancellable = true)
    private void onUpdateTargetedEntity(float tickDelta, CallbackInfo info) {
        PlayerTraceEvent event = new PlayerTraceEvent();
        EventUtils.post(event);

        if (event.isCancelled() && client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            info.cancel();
        }
    }

    @Unique
    private Freecam freecam;

    @Inject(method = "updateTargetedEntity", at = @At("HEAD"), cancellable = true)
    private void updateTargetedEntityInvoke(float tickDelta, CallbackInfo info) {
        if (freecam == null) freecam = FunctionManager.get(Freecam.class);

        if ((freecam.isEnabled()) && client.getCameraEntity() != null && !freecamSet) {
            info.cancel();
            Entity cameraE = client.getCameraEntity();

            double x = cameraE.getX();
            double y = cameraE.getY();
            double z = cameraE.getZ();
            double prevX = cameraE.prevX;
            double prevY = cameraE.prevY;
            double prevZ = cameraE.prevZ;
            float yaw = cameraE.yaw;
            float pitch = cameraE.pitch;
            float prevYaw = cameraE.prevYaw;
            float prevPitch = cameraE.prevPitch;

            ((IVec3d) cameraE.getPos()).set(freecam.pos.x, freecam.pos.y - cameraE.getEyeHeight(cameraE.getPose()), freecam.pos.z);
            cameraE.prevX = freecam.prevPos.x;
            cameraE.prevY = freecam.prevPos.y - cameraE.getEyeHeight(cameraE.getPose());
            cameraE.prevZ = freecam.prevPos.z;
            cameraE.yaw = (freecam.yaw);
            cameraE.pitch = (freecam.pitch);
            cameraE.prevYaw = freecam.prevYaw;
            cameraE.prevPitch = freecam.prevPitch;

            freecamSet = true;
            updateTargetedEntity(tickDelta);
            freecamSet = false;

            ((IVec3d) cameraE.getPos()).set(x, y, z);
            cameraE.prevX = prevX;
            cameraE.prevY = prevY;
            cameraE.prevZ = prevZ;
            cameraE.yaw = (yaw);
            cameraE.pitch = (pitch);
            cameraE.prevYaw = prevYaw;
            cameraE.prevPitch = prevPitch;
        }
    }
}