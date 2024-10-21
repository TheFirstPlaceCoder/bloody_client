package mixin;

import com.client.event.events.KeybindSettingEvent;
import com.client.event.events.MouseEvent;
import com.client.event.events.MouseScrollEvent;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.utils.auth.Loader;
import com.client.utils.misc.InputUtils;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.client.BloodyClient.mc;

@Mixin(Mouse.class)
public class MouseMixin {
    @Shadow private double x;

    @Shadow private double y;

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (mc.currentScreen == null) {
            KeybindSettingEvent keybindSettingEvent = new KeybindSettingEvent(true, button, InputUtils.Action.get(action));
            if (!Loader.unHook) keybindSettingEvent.post();
            if (keybindSettingEvent.isCancelled()) {
                ci.cancel();
                return;
            }

            MouseEvent event = new MouseEvent(button, InputUtils.Action.get(action), x, y);
            if (!Loader.unHook) event.post();

            for (Function function : FunctionManager.getFunctionList()) {
                if (function.getKeyCode() < 90000 || Loader.unHook) continue;
                if (function.getKeyCode() - 90001 == button && InputUtils.Action.get(action).equals(InputUtils.Action.PRESS)) {
                    function.toggle();
                }
            }

            if (event.isCancelled()) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo info) {
        MouseScrollEvent event = MouseScrollEvent.get(vertical);
        event.post();

        if (event.isCancelled()) info.cancel();
    }
}
