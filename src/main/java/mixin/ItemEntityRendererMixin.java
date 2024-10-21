package mixin;

import com.client.impl.function.client.Optimization;
import com.client.impl.function.visual.ItemPhysic;
import com.client.system.function.FunctionManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin extends EntityRenderer<ItemEntity> {
    @Shadow
    @Final
    private Random random;
    @Shadow @Final private ItemRenderer itemRenderer;

    protected ItemEntityRendererMixin(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Shadow protected abstract int getRenderedAmount(ItemStack stack);

    /**
     * @author .
     * @reason .
     */
    @Overwrite
    public void render(ItemEntity itemEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if (!FunctionManager.get(Optimization.class).isEnabled() || FunctionManager.get(Optimization.class).getItemEntities().contains(itemEntity)) {
            boolean itemPhysic = FunctionManager.get(ItemPhysic.class).isEnabled();

            matrixStack.push();
            ItemStack itemStack = itemEntity.getStack();
            int j = itemStack.isEmpty() ? 187 : Item.getRawId(itemStack.getItem()) + itemStack.getDamage();
            this.random.setSeed(j);
            BakedModel bakedModel = this.itemRenderer.getHeldItemModel(itemStack, itemEntity.world, null);
            boolean bl = bakedModel.hasDepth();
            int k = this.getRenderedAmount(itemStack);
            float h = 0.25F;
            float l = MathHelper.sin(((float) itemEntity.getItemAge() + g) / 10.0F + itemEntity.uniqueOffset) * 0.1F + 0.1F;
            float m = bakedModel.getTransformation().getTransformation(ModelTransformation.Mode.GROUND).scale.getY();
            if (!itemPhysic) matrixStack.translate(0.0, l + 0.25F * m, 0.0);
            float n = itemEntity.method_27314(g);
            if (!itemPhysic) matrixStack.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(n));
            if (itemPhysic) {
                matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(itemEntity.isOnGround() ? 90 : n * 300));
            }
            float o = bakedModel.getTransformation().ground.scale.getX();
            float p = bakedModel.getTransformation().ground.scale.getY();
            float q = bakedModel.getTransformation().ground.scale.getZ();
            float s;
            float t;
            if (!bl) {
                float r = -0.0F * (float) (k - 1) * 0.5F * o;
                s = -0.0F * (float) (k - 1) * 0.5F * p;
                t = -0.09375F * (float) (k - 1) * 0.5F * q;
                matrixStack.translate(r, s, t);
            }

            for (int u = 0; u < k; ++u) {
                matrixStack.push();
                if (u > 0) {
                    if (bl) {
                        s = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        t = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float v = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        matrixStack.translate(s, t, v);
                    } else {
                        s = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                        t = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                        matrixStack.translate(s, t, 0.0);
                    }
                }

                this.itemRenderer.renderItem(itemStack, ModelTransformation.Mode.GROUND, false, matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV, bakedModel);
                matrixStack.pop();
                if (!bl) {
                    matrixStack.translate(0.0F * o, 0.0F * p, 0.09375F * q);
                }
            }

            matrixStack.pop();
            super.render(itemEntity, f, g, matrixStack, vertexConsumerProvider, i);
        }
    }
}