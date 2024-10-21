package mixin;

import api.main.EventUtils;
import com.client.clickgui.GuiScreen;
import com.client.event.events.KeyEvent;
import com.client.event.events.KeybindSettingEvent;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.impl.function.client.ClickGui;
import com.client.utils.auth.Loader;
import com.client.utils.misc.InputUtils;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.client.BloodyClient.mc;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKey(long window, int key, int scancode, int i, int modifiers, CallbackInfo ci) {
        if (mc.currentScreen == null) {
            KeybindSettingEvent keybindSettingEvent = new KeybindSettingEvent(false, key, InputUtils.Action.get(i));
            if (!Loader.unHook) EventUtils.post(keybindSettingEvent);

            if (keybindSettingEvent.isCancelled()) {
                ci.cancel();
                return;
            }
        }

        for (Function function : FunctionManager.getFunctionList()) {
            boolean a = mc.currentScreen == null || function instanceof ClickGui && mc.currentScreen instanceof GuiScreen;
            if (!Loader.unHook && a && function.getKeyCode() < 90000 && function.getKeyCode() == key && InputUtils.Action.get(i).equals(InputUtils.Action.PRESS)) {
                function.toggle();
            }
        }

        KeyEvent event = new KeyEvent(key, InputUtils.Action.get(i));
        if (!Loader.unHook) EventUtils.post(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
