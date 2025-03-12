package mixin.screens;

import com.client.event.events.GetTooltipEvent;
import com.client.system.textures.DownloadImage;
import com.client.utils.Utils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.utils.shader.shaders.BlurShader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.awt.*;

import static com.client.BloodyClient.mc;

@Mixin(Screen.class)
public class ScreenMixin {
    @ModifyArgs(method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;II)V"))
    private void getList(Args args, MatrixStack matrixStack, ItemStack itemStack, int x, int y) {
        GetTooltipEvent event = new GetTooltipEvent(itemStack, args.get(1), matrixStack, x, y);
        event.post();
        args.set(0, event.matrixStack);
        args.set(1, event.list);
        args.set(2, event.x);
        args.set(3, event.y);
    }

    @Inject(method = "renderBackground(Lnet/minecraft/client/util/math/MatrixStack;)V", at = @At(value = "HEAD"), cancellable = true)
    public void renderBackground(MatrixStack matrices, CallbackInfo ci) {
        ci.cancel();

        Utils.rescaling(() -> {
            GL.prepare();
            GL.drawRoundedTexture(DownloadImage.getGlId(DownloadImage.DEFAULT_MENU), 0, 0, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight(), 0);
            GL.end();

            BlurShader.registerRenderCall(() -> {
                GL.drawRoundedRect(new FloatRect(0, 0, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight()), 0, Color.WHITE);
            });

            BlurShader.draw(8);
        });
    }
}