package mixin;

import com.client.impl.function.misc.BetterChat;
import com.client.system.function.FunctionManager;
import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SharedConstants.class)
public class SharedConstantsMixin {
    @Unique private static BetterChat betterChat;

    @Inject(method = "isValidChar", at = @At("HEAD"), cancellable = true)
    private static void coffee_yesThisIsAValidCharDoNotAtMe(char chr, CallbackInfoReturnable<Boolean> cir) {
        if (betterChat == null) betterChat = FunctionManager.get(BetterChat.class);

        if (betterChat.getFormatCodes() && chr == '§') {
            cir.setReturnValue(true);
        }
    }
}