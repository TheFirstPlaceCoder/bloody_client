package mixin;

import com.client.utils.auth.Loader;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.*;

@Mixin(ClickableWidget.class)
public abstract class ClickableWidgetMixin extends DrawableHelper implements Drawable, Element {

    @Shadow
    protected abstract int getYImage(boolean hovered);

    @Shadow
    public abstract boolean isHovered();

    @Shadow
    @Final
    public static Identifier WIDGETS_TEXTURE;

    @Shadow
    protected float alpha;

    @Shadow
    protected int width;

    @Shadow
    protected int height;

    @Shadow
    public int x;

    @Shadow
    public int y;

    @Shadow
    public boolean active;

    @Shadow
    protected abstract void renderBackground(MatrixStack matrices, MinecraftClient client, int mouseX, int mouseY);

    @Shadow
    public abstract Text getMessage();

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;
        if (!Loader.unHook) {
            FloatRect rect = new FloatRect(this.x, this.y, this.width, this.height);
            int alphach = rect.intersect(mouseX, mouseY) ? 200 : 110;
            GL.prepare();
            GL.drawRoundedGradientRect(rect, 3, ColorUtils.injectAlpha(Colors.getColor(0, 13), alphach),
                    ColorUtils.injectAlpha(Colors.getColor(90, 13), alphach),
                    ColorUtils.injectAlpha(Colors.getColor(270, 13), alphach),
                    ColorUtils.injectAlpha(Colors.getColor(180, 13), alphach));

            GL.drawRoundedGradientOutline(rect, 3, 1, ColorUtils.injectAlpha(Colors.getColor(0, 13), 255),
                    ColorUtils.injectAlpha(Colors.getColor(90, 13), 255),
                    ColorUtils.injectAlpha(Colors.getColor(270, 13), 255),
                    ColorUtils.injectAlpha(Colors.getColor(180, 13), 255));

            GL.end();

            int j = this.active ? 16777215 : 10526880;
            IFont.drawCenteredXY(IFont.NEVERLOSE, this.getMessage().getString(), this.x + this.width / 2, this.y + this.height / 2, new Color(j | MathHelper.ceil(this.alpha * 255.0F) << 24), 10);
            //drawCenteredText(matrices, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
        } else {
            minecraftClient.getTextureManager().bindTexture(WIDGETS_TEXTURE);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
            int i = this.getYImage(this.isHovered());
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            this.drawTexture(matrices, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
            this.drawTexture(matrices, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            this.renderBackground(matrices, minecraftClient, mouseX, mouseY);
            int j = this.active ? 16777215 : 10526880;
            drawCenteredText(matrices, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }
    }
}
