package mixin.screens;

import com.client.system.textures.DownloadImage;
import com.client.utils.Utils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.utils.shader.shaders.BlurShader;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.*;

import static com.client.BloodyClient.mc;

@Mixin(EntryListWidget.class)
public abstract class EntryListWidgetMixin<E extends net.minecraft.client.gui.widget.EntryListWidget.Entry<E>> extends AbstractParentElement implements Drawable {
    @Shadow
    @Final
    protected MinecraftClient client;
    @Shadow
    protected int width;
    @Shadow
    protected int height;
    @Shadow
    protected int top;
    @Shadow
    protected int bottom;
    @Shadow
    protected int right;
    @Shadow
    protected int left;
    @Shadow
    private boolean renderHeader;

    @Shadow
    protected abstract void renderHeader(MatrixStack matrices, int x, int y, Tessellator tessellator);

    @Shadow
    protected abstract void renderDecorations(MatrixStack matrices, int mouseX, int mouseY);

    @Shadow
    protected abstract void renderList(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta);

    @Shadow
    protected abstract int getScrollbarPositionX();

    @Shadow
    public abstract int getRowLeft();

    @Shadow
    public abstract double getScrollAmount();

    @Shadow
    public abstract int getMaxScroll();

    @Shadow
    protected abstract int getMaxPosition();


    /**
     * @author
     * @reason
     */
    @Overwrite
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //this.renderBackground(matrices);
        int i = this.getScrollbarPositionX();
        int j = i + 6;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        Utils.rescaling(() -> {
            GL.prepare();
            GL.drawRoundedTexture(DownloadImage.getGlId(DownloadImage.DEFAULT_MENU), 0, 0, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight(), 0);
            GL.end();

            BlurShader.registerRenderCall(() -> {
                GL.drawRoundedRect(new FloatRect(0, 0, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight()), 0, Color.WHITE);
            });

            BlurShader.draw(8);
        });

        int k = this.getRowLeft();
        int l = this.top + 4 - (int)this.getScrollAmount();
        if (this.renderHeader) {
            this.renderHeader(matrices, k, l, tessellator);
        }

        this.renderList(matrices, k, l, mouseX, mouseY, delta);
        int o = this.getMaxScroll();
        if (o > 0) {
            RenderSystem.disableTexture();
            int m = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getMaxPosition());
            m = MathHelper.clamp(m, 32, this.bottom - this.top - 8);
            int n = (int)this.getScrollAmount() * (this.bottom - this.top - m) / o + this.top;
            if (n < this.top) {
                n = this.top;
            }

            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex((double)i, (double)this.bottom, 0.0D).texture(0.0F, 1.0F).color(0, 0, 0, 255).next();
            bufferBuilder.vertex((double)j, (double)this.bottom, 0.0D).texture(1.0F, 1.0F).color(0, 0, 0, 255).next();
            bufferBuilder.vertex((double)j, (double)this.top, 0.0D).texture(1.0F, 0.0F).color(0, 0, 0, 255).next();
            bufferBuilder.vertex((double)i, (double)this.top, 0.0D).texture(0.0F, 0.0F).color(0, 0, 0, 255).next();
            bufferBuilder.vertex((double)i, (double)(n + m), 0.0D).texture(0.0F, 1.0F).color(128, 128, 128, 255).next();
            bufferBuilder.vertex((double)j, (double)(n + m), 0.0D).texture(1.0F, 1.0F).color(128, 128, 128, 255).next();
            bufferBuilder.vertex((double)j, (double)n, 0.0D).texture(1.0F, 0.0F).color(128, 128, 128, 255).next();
            bufferBuilder.vertex((double)i, (double)n, 0.0D).texture(0.0F, 0.0F).color(128, 128, 128, 255).next();
            bufferBuilder.vertex((double)i, (double)(n + m - 1), 0.0D).texture(0.0F, 1.0F).color(192, 192, 192, 255).next();
            bufferBuilder.vertex((double)(j - 1), (double)(n + m - 1), 0.0D).texture(1.0F, 1.0F).color(192, 192, 192, 255).next();
            bufferBuilder.vertex((double)(j - 1), (double)n, 0.0D).texture(1.0F, 0.0F).color(192, 192, 192, 255).next();
            bufferBuilder.vertex((double)i, (double)n, 0.0D).texture(0.0F, 0.0F).color(192, 192, 192, 255).next();
            tessellator.draw();
        }

        this.renderDecorations(matrices, mouseX, mouseY);
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
    }
}
