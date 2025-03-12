package mixin;

import com.client.impl.function.visual.SwingAnimation;
import com.client.system.function.FunctionManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.client.BloodyClient.mc;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
    @Shadow @Final private MinecraftClient client;

    @Shadow protected abstract void renderArmHoldingItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress, Arm arm);

    @Shadow private ItemStack offHand;

    @Shadow protected abstract void renderMapInBothHands(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float pitch, float equipProgress, float swingProgress);

    @Shadow protected abstract void renderMapInOneHand(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, Arm arm, float swingProgress, ItemStack stack);

    @Shadow protected abstract void applyEquipOffset(MatrixStack matrices, Arm arm, float equipProgress);

    @Shadow protected abstract void applySwingOffset(MatrixStack matrices, Arm arm, float swingProgress);

    @Shadow public abstract void renderItem(LivingEntity entity, ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light);

    @Shadow protected abstract void applyEatOrDrinkTransformation(MatrixStack matrices, float tickDelta, Arm arm, ItemStack stack);

    @Shadow private ItemStack mainHand;

    @Unique private SwingAnimation animation;


    @Inject(method = "applyEatOrDrinkTransformation", at = @At("HEAD"), cancellable = true)
    private void applyEatOrDrinkTransformation(MatrixStack matrices, float tickDelta, Arm arm, ItemStack stack, CallbackInfo ci) {
        if (animation == null) animation = FunctionManager.get(SwingAnimation.class);
        boolean checkMain = mc.player.getMainHandStack().getItem().equals(Items.CROSSBOW) || mc.player.getMainHandStack().getItem().equals(Items.FILLED_MAP) || mc.player.getMainHandStack().getItem().equals(Items.BOW) || mc.player.getMainHandStack().getItem().equals(Items.TRIDENT);
        boolean checkOff = mc.player.getOffHandStack().getItem().equals(Items.CROSSBOW) || mc.player.getOffHandStack().getItem().equals(Items.FILLED_MAP) || mc.player.getOffHandStack().getItem().equals(Items.BOW) || mc.player.getOffHandStack().getItem().equals(Items.TRIDENT);
        if (animation.isEnabled() && animation.animation() && !checkOff && !checkMain) {
            ci.cancel();

            float f = (float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0F;
            float g = f / (float)stack.getMaxUseTime();
            float h;

            if (g < 0.8F) {
                h = MathHelper.abs(MathHelper.cos(f / 4.0F * 3.1415927F) * 0.1F);
                matrices.translate(0.0F, h / 4, 0.0F);
            }

            h = 1.0F - (float)Math.pow(g, 27.0);
            int i = arm == Arm.RIGHT ? 1 : -1;

            matrices.translate(h * 0.6F * (float)i, h * -0.5F, h * 0.0F);
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float)i * h * 90.0F));
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(h * 10.0F));
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float)i * h * 30.0F));
        }
    }

    @Inject(method = "applySwingOffset", at = @At("HEAD"), cancellable = true)
    private void applySwingOffset(MatrixStack matrices, Arm arm, float swingProgress, CallbackInfo ci) {
        if (animation == null) animation = FunctionManager.get(SwingAnimation.class);
        boolean checkMain = mc.player.getMainHandStack().getItem().equals(Items.CROSSBOW) || mc.player.getMainHandStack().getItem().equals(Items.FILLED_MAP) || mc.player.getMainHandStack().getItem().equals(Items.BOW) || mc.player.getMainHandStack().getItem().equals(Items.TRIDENT);
        boolean checkOff = mc.player.getOffHandStack().getItem().equals(Items.CROSSBOW) || mc.player.getOffHandStack().getItem().equals(Items.FILLED_MAP) || mc.player.getOffHandStack().getItem().equals(Items.BOW) || mc.player.getOffHandStack().getItem().equals(Items.TRIDENT);
        if (animation.isEnabled() && animation.animation() && !checkOff && !checkMain) {
            ci.cancel();
            if (arm == Arm.RIGHT) {
                int i = 1;
                float f = MathHelper.sin(swingProgress * swingProgress * 3.1415927F);
                float g = MathHelper.sin(MathHelper.sqrt(swingProgress) * 3.1415927F);

                switch (animation.swingAnimation.get()) {
                    case "Свайп" -> {
                        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(60));
                        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(0));
                        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
                        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-45 * g));
                    }

                    case "Строгий" -> matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(g * -80.0F));

                    case "Ванильный" -> {
                        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float)i * (45.0F + f * -20.0F)));
                        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float)i * g * -20.0F));
                        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(g * -80.0F));
                        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float)i * -45.0F));
                    }
                }
            }
        }
    }

    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"), cancellable = true)
    private void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (animation == null) animation = FunctionManager.get(SwingAnimation.class);
        boolean checkMain = player.getMainHandStack().getItem().equals(Items.CROSSBOW) || player.getMainHandStack().getItem().equals(Items.FILLED_MAP) || player.getMainHandStack().getItem().equals(Items.BOW) || player.getMainHandStack().getItem().equals(Items.TRIDENT);
        boolean checkOff = player.getOffHandStack().getItem().equals(Items.CROSSBOW) || player.getOffHandStack().getItem().equals(Items.FILLED_MAP) || player.getOffHandStack().getItem().equals(Items.BOW) || player.getOffHandStack().getItem().equals(Items.TRIDENT);
        if (animation.isEnabled() && animation.animation() && !checkOff && !checkMain) {
            ci.cancel();
            boolean bl = hand == Hand.MAIN_HAND;
            Arm arm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
            matrices.push();
            if (item.isEmpty()) {
                if (bl && !player.isInvisible()) {
                    this.renderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
                }
            } else if (item.getItem() == Items.FILLED_MAP) {
                if (bl && this.offHand.isEmpty()) {
                    this.renderMapInBothHands(matrices, vertexConsumers, light, pitch, equipProgress, swingProgress);
                } else {
                    this.renderMapInOneHand(matrices, vertexConsumers, light, equipProgress, arm, swingProgress, item);
                }
            } else {
                boolean bl4;
                float v;
                float w;
                float x;
                float y;
                if (item.getItem() == Items.CROSSBOW) {
                    bl4 = CrossbowItem.isCharged(item);
                    boolean bl3 = arm == Arm.RIGHT;
                    int i = bl3 ? 1 : -1;
                    if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
                        this.applyEquipOffset(matrices, arm, equipProgress);
                        matrices.translate((float) i * -0.4785682F, -0.0943870022892952, 0.05731530860066414);
                        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-11.935F));
                        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float) i * 65.3F));
                        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float) i * -9.785F));
                        v = (float) item.getMaxUseTime() - ((float) this.client.player.getItemUseTimeLeft() - tickDelta + 1.0F);
                        w = v / (float) CrossbowItem.getPullTime(item);
                        if (w > 1.0F) {
                            w = 1.0F;
                        }

                        if (w > 0.1F) {
                            x = MathHelper.sin((v - 0.1F) * 1.3F);
                            y = w - 0.1F;
                            float k = x * y;
                            matrices.translate(k * 0.0F, k * 0.004F, k * 0.0F);
                        }

                        matrices.translate(w * 0.0F, w * 0.0F, w * 0.04F);
                        matrices.scale(1.0F, 1.0F, 1.0F + w * 0.2F);
                        matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion((float) i * 45.0F));
                    } else {
                        v = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * 3.1415927F);
                        w = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgress) * 6.2831855F);
                        x = -0.2F * MathHelper.sin(swingProgress * 3.1415927F);
                        matrices.translate((float) i * v, w, x);
                        this.applyEquipOffset(matrices, arm, equipProgress);
                        this.applySwingOffset(matrices, arm, swingProgress);
                        if (bl4 && swingProgress < 0.001F) {
                            matrices.translate((float) i * -0.641864F, 0.0, 0.0);
                            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float) i * 10.0F));
                        }
                    }

                    this.renderItem(player, item, bl3 ? ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND, !bl3, matrices, vertexConsumers, light);
                } else {
                    bl4 = arm == Arm.RIGHT;
                    int o;
                    float u;
                    if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
                        o = bl4 ? 1 : -1;
                        switch (item.getUseAction()) {
                            case NONE:
                            case BLOCK:
                                this.applyEquipOffset(matrices, arm, equipProgress);
                                break;
                            case EAT:
                            case DRINK:
                                this.applyEatOrDrinkTransformation(matrices, tickDelta, arm, item);
                                this.applyEquipOffset(matrices, arm, equipProgress);
                                break;
                            case BOW:
                                this.applyEquipOffset(matrices, arm, equipProgress);
                                matrices.translate((float) o * -0.2785682F, 0.18344387412071228, 0.15731531381607056);
                                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-13.935F));
                                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float) o * 35.3F));
                                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float) o * -9.785F));
                                u = (float) item.getMaxUseTime() - ((float) this.client.player.getItemUseTimeLeft() - tickDelta + 1.0F);
                                v = u / 20.0F;
                                v = (v * v + v * 2.0F) / 3.0F;
                                if (v > 1.0F) {
                                    v = 1.0F;
                                }

                                if (v > 0.1F) {
                                    w = MathHelper.sin((u - 0.1F) * 1.3F);
                                    x = v - 0.1F;
                                    y = w * x;
                                    matrices.translate(y * 0.0F, y * 0.004F, y * 0.0F);
                                }

                                matrices.translate(v * 0.0F, v * 0.0F, v * 0.04F);
                                matrices.scale(1.0F, 1.0F, 1.0F + v * 0.2F);
                                matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion((float) o * 45.0F));
                                break;
                            case SPEAR:
                                this.applyEquipOffset(matrices, arm, equipProgress);
                                matrices.translate((float) o * -0.5F, 0.699999988079071, 0.10000000149011612);
                                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-55.0F));
                                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float) o * 35.3F));
                                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float) o * -9.785F));
                                u = (float) item.getMaxUseTime() - ((float) this.client.player.getItemUseTimeLeft() - tickDelta + 1.0F);
                                v = u / 10.0F;
                                if (v > 1.0F) {
                                    v = 1.0F;
                                }

                                if (v > 0.1F) {
                                    w = MathHelper.sin((u - 0.1F) * 1.3F);
                                    x = v - 0.1F;
                                    y = w * x;
                                    matrices.translate(y * 0.0F, y * 0.004F, y * 0.0F);
                                }

                                matrices.translate(0.0, 0.0, v * 0.2F);
                                matrices.scale(1.0F, 1.0F, 1.0F + v * 0.2F);
                                matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion((float) o * 45.0F));
                        }
                    } else if (player.isUsingRiptide()) {
                        this.applyEquipOffset(matrices, arm, equipProgress);
                        o = bl4 ? 1 : -1;
                        matrices.translate((float) o * -0.4F, 0.800000011920929, 0.30000001192092896);
                        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float) o * 65.0F));
                        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float) o * -85.0F));
                    } else {
                        float aa = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * 3.1415927F);
                        u = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgress) * 6.2831855F);
                        v = -0.2F * MathHelper.sin(swingProgress * 3.1415927F);
                        int ad = bl4 ? 1 : -1;
                        this.applyEquipOffset(matrices, arm, equipProgress);
                        this.applySwingOffset(matrices, arm, swingProgress);
                    }

                    this.renderItem(player, item, bl4 ? ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND, !bl4, matrices, vertexConsumers, light);
                }
            }
            matrices.pop();
        }
    }

    @Inject(method = "applyEquipOffset", at = @At("HEAD"), cancellable = true)
    private void applyEquipOffset(MatrixStack matrices, Arm arm, float equipProgress, CallbackInfo ci) {
        if (animation == null) animation = FunctionManager.get(SwingAnimation.class);
        boolean checkMain = mc.player.getMainHandStack().getItem().equals(Items.CROSSBOW) || mc.player.getMainHandStack().getItem().equals(Items.FILLED_MAP) || mc.player.getMainHandStack().getItem().equals(Items.BOW) || mc.player.getMainHandStack().getItem().equals(Items.TRIDENT);
        boolean checkOff = mc.player.getOffHandStack().getItem().equals(Items.CROSSBOW) || mc.player.getOffHandStack().getItem().equals(Items.FILLED_MAP) || mc.player.getOffHandStack().getItem().equals(Items.BOW) || mc.player.getOffHandStack().getItem().equals(Items.TRIDENT);
        if (animation.isEnabled() && animation.animation() && !checkOff && !checkMain) {
            ci.cancel();
            int i = arm == Arm.RIGHT ? 1 : -1;
            matrices.translate((float)i * 0.56F, -0.52F, -0.72F);
        }
    }

    @Inject(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", shift = At.Shift.BEFORE, ordinal = 0))
    public void offsetRightBefore(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light, CallbackInfo ci) {
        if (animation == null) animation = FunctionManager.get(SwingAnimation.class);
        if (animation.isEnabled() && animation.animation()) {
            matrices.push();
            matrices.translate(animation.x.floatValue(), animation.y.floatValue(), animation.z.floatValue());
            if (animation.shouldAnimate()) matrices.scale(animation.scale.floatValue(), animation.scale.floatValue(), animation.scale.floatValue());
        }
    }

    @Inject(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", shift = At.Shift.AFTER, ordinal = 0))
    public void offsetRightAfter(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light, CallbackInfo ci) {
        if (animation == null) animation = FunctionManager.get(SwingAnimation.class);
        if (animation.isEnabled() && animation.animation()) {
            matrices.pop();
        }
    }

    @Inject(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", shift = At.Shift.BEFORE, ordinal = 1))
    public void offsetLeftBefore(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light, CallbackInfo ci) {
        if (animation == null) animation = FunctionManager.get(SwingAnimation.class);
        if (animation.isEnabled() && animation.animation()) {
            matrices.push();
            matrices.translate(-animation.x.floatValue(), animation.y.floatValue(), animation.z.floatValue());
            if (animation.shouldAnimate()) matrices.scale(animation.scale.floatValue(), animation.scale.floatValue(), animation.scale.floatValue());
        }
    }

    @Inject(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", shift = At.Shift.AFTER, ordinal = 1))
    public void offsetLeftAfter(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light, CallbackInfo ci) {
        if (animation == null) animation = FunctionManager.get(SwingAnimation.class);
        if (animation.isEnabled() && animation.animation()) {
            matrices.pop();
        }
    }
}