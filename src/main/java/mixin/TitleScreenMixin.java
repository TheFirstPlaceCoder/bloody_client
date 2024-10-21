package mixin;

import com.client.utils.auth.Loader;
import com.client.utils.changelog.ChangeLog;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (Loader.unHook) return;

        GL11.glPushMatrix();
        GL11.glTranslated(0, 0, 100);
        ChangeLog.draw(mouseX, mouseY);
        GL11.glPopMatrix();
    }

    @Inject(method = "mouseClicked", at = @At("TAIL"))
    private void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        ChangeLog.click((int) mouseX, (int) mouseY, button);
    }
}