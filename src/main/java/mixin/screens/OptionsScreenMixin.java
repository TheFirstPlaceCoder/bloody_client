package mixin.screens;

import com.client.system.textures.DownloadImage;
import com.client.utils.Utils;
import com.client.utils.auth.Loader;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.api.FontRenderer;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.utils.shader.shaders.BlurShader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.awt.*;

import static com.client.BloodyClient.mc;

@Mixin(OptionsScreen.class)
public class OptionsScreenMixin extends Screen {
    protected OptionsScreenMixin(Text title) {
        super(title);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!Loader.unHook) {
            Utils.rescaling(() -> {
                GL.prepare();
                GL.drawRoundedTexture(DownloadImage.getGlId(DownloadImage.DEFAULT_MENU), 0, 0, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight(), 0);
                GL.end();

                BlurShader.registerRenderCall(() -> {
                    GL.drawRoundedRect(new FloatRect(0, 0, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight()), 0, Color.WHITE);
                });

                BlurShader.draw(8);
            });

            FontRenderer.color(true);
            IFont.drawCenteredX(IFont.MONTSERRAT_BOLD, this.title.getString(), this.width / 2f, 15, new Color(162, 162, 162).brighter(), 13);
            FontRenderer.color(false);
        } else {
            this.renderBackground(matrices);
            drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 16777215);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }
}
