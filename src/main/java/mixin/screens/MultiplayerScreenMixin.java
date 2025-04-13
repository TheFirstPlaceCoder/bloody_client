package mixin.screens;

import com.client.utils.auth.Loader;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.api.FontRenderer;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.*;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {
    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerScreen;drawCenteredText(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V"
            )
    )
    public void renderText(MatrixStack matrixStack, TextRenderer textRenderer, Text text, int i, int i1, int i2) {
        if (!Loader.unHook) {
            GL.prepare();
            GL.drawRoundedRect(new FloatRect(this.width / 2f - IFont.getWidth(IFont.MONTSERRAT_BOLD, this.title.getString(), 13) / 2 - 10, 12, IFont.getWidth(IFont.MONTSERRAT_BOLD, this.title.getString(), 13) + 20, IFont.getHeight(IFont.MONTSERRAT_BOLD, this.title.getString(), 13) + 6), 6, new Color(28, 30, 35, 210));
            GL.end();

            FontRenderer.color(true);
            IFont.drawCenteredX(IFont.MONTSERRAT_BOLD, this.title.getString(), this.width / 2f, 15, new Color(162, 162, 162).brighter(), 13);
            FontRenderer.color(false);
        } else {
            drawCenteredText(matrixStack, this.textRenderer, this.title, this.width / 2, 20, 16777215);
        }
    }
}
