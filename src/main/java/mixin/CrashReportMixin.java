package mixin;

import com.client.utils.files.TwilightTextTranslator;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CrashReport.class, priority = Integer.MAX_VALUE)
public class CrashReportMixin {
    //    fix class_123456789 to normal name
    @Inject(method = "asString", at = @At("RETURN"), cancellable = true)
    void antiChatClear(CallbackInfoReturnable<String> info) {
        info.setReturnValue(TwilightTextTranslator.translate(info.getReturnValue()));
    }

}