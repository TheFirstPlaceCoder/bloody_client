package mixin;

import com.client.system.function.FunctionManager;
import com.client.utils.misc.FunctionUtils;
import com.client.utils.optimization.interfaces.EntityRendererInter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.client.BloodyClient.mc;


@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> implements EntityRendererInter<T> {
    public boolean shadowShouldShowName(T entity) {
        return this.hasLabel(entity);
    }

    public void shadowRenderNameTag(T entity, Text component, MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int light) {
        this.renderLabelIfPresent(entity, component, poseStack, multiBufferSource, light);
    }

    @Shadow
    protected abstract boolean hasLabel(T var1);
    @Shadow
    protected abstract void renderLabelIfPresent(T var1, Text var2, MatrixStack var3, VertexConsumerProvider var4, int var5);

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void renderLabelIfPresent(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (FunctionManager.get("Tags").isEnabled()) {
            boolean armorStandFlag = false;
            if (entity instanceof ArmorStandEntity armorStandEntity) {
                for (Entity en : mc.world.getEntities()) {
                    if (en instanceof ItemEntity || en instanceof PlayerEntity) {
                        if (armorStandEntity.getBoundingBox().intersects(en.getBoundingBox())) {
                            armorStandFlag = true;
                            break;
                        }
                    }
                }
            }
            if ((entity instanceof PlayerEntity || armorStandFlag || (FunctionUtils.isFilter && entity instanceof ItemEntity))) {
                ci.cancel();
            }
        }
    }
}