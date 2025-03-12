package mixin.screens;

import com.client.utils.Utils;
import com.client.utils.auth.Loader;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.math.animation.AnimationUtils;
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
import org.spongepowered.asm.mixin.*;

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

    @Unique
    private float selectedAlpha;

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
            selectedAlpha = AnimationUtils.fast(selectedAlpha, rect.intersect(mouseX, mouseY) ? 255 : 0, rect.intersect(mouseX, mouseY) ? 10 : 5);

            GL.prepare();
            GL.drawRoundedRect(rect, 5, Utils.lerp(new Color(40, 40, 40, 200), new Color(15, 15, 15, 200), selectedAlpha / 255));
            GL.end();

            int j = this.active ? 16777215 : 10526880;

            IFont.drawCenteredXY(IFont.Greycliff, this.getMessage().getString(), this.x + this.width / 2, this.y + this.height / 2, new Color(j | MathHelper.ceil(this.alpha * 255.0F) << 24), 10);

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
