package mixin;

import com.client.impl.function.misc.PassHider;
import com.client.interfaces.IChatScreen;
import com.client.system.command.Command;
import com.client.system.command.CommandManager;
import com.client.system.function.FunctionManager;
import com.client.system.hud.HudFunction;
import com.client.system.hud.HudManager;
import com.client.utils.auth.Loader;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.utils.shader.shaders.BlurShader;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen implements IChatScreen {
    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Shadow protected TextFieldWidget chatField;

    @Inject(method = "render", at = @At("TAIL"))
    private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        HudManager.handle(mouseX, mouseY);

        String string = chatField.getText().trim();

        if ((string.contains("/l") || string.contains("/login") || string.contains("/reg") || string.contains("/register") || string.contains("/д") || string.contains("/куп") || string.contains("/дщпшт") || string.contains("/купшыеук")) && FunctionManager.get(PassHider.class).isEnabled() && !Loader.unHook) {
            BlurShader.registerRenderCall(() -> GL.drawQuad(new FloatRect(0, client.getWindow().getScaledHeight() - 15, client.textRenderer.getTextHandler().getWidth(string) + 6, 15), Color.WHITE));
            BlurShader.draw(5);
        }

        if (string.startsWith(Command.getPrefix()) && !Loader.unHook) {
            String comm = string.replace(Command.getPrefix(), "");
            String split = "";
            try {
                split = comm.split(" ")[1];
            } catch (Exception ignore) {
            }
            List<String> commands = new ArrayList<>();
            int i = 0;
            Command c = null;
            if (!comm.isEmpty()) {
                for (Command command : CommandManager.getCommands()) {
                    for (String s : command.getFirstList()) {
                        String cn = comm;
                        try {
                            cn = comm.split(" ")[0];
                        } catch (Exception ignored) {
                        }
                        if (s.equals(cn)) {
                            c = command;
                            break;
                        }
                    }
                }
            }
            if (c != null) {
                for (String s : c.getSecondList()) {
                    if (i > 10) break;
                    if (split.isEmpty() || s.startsWith(split)) {
                        commands.add(s);
                        i++;
                    }
                }
            } else {
                for (Command command : CommandManager.getCommands()) {
                    if (i > 10) break;
                    for (String s : command.getFirstList()) {
                        if (comm.isEmpty() || s.startsWith(comm)) {
                            commands.add(s);
                            i++;
                        }
                    }
                }
            }
            float offset = 0;
            for (String command : commands) {
                offset += IFont.getHeight(IFont.COMFORTAAB, command, 8) + 4;
            }
            float x = 3;
            float y = client.getWindow().getScaledHeight() - 18 - offset;
            for (String command : commands) {
                command = "  " + command + "  ";
                FloatRect data = new FloatRect(x, y, IFont.getWidth(IFont.COMFORTAAB, command, 8) + 3, IFont.getHeight(IFont.COMFORTAAB, command, 8) + 1);
                HudFunction.drawRect(data, 1f);
                IFont.drawCenteredXY(IFont.COMFORTAAB, command, data.getCenteredX(), data.getCenteredY(), Color.WHITE, 8);
                y += IFont.getHeight(IFont.COMFORTAAB, command, 8) + 4;
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        HudManager.handleClick((int) mouseX, (int) mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        HudManager.handleRelease((int) mouseX, (int) mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        HudManager.handleClose();
        super.onClose();
    }

    @Override
    public String getChatField() {
        return chatField.getText();
    }
}