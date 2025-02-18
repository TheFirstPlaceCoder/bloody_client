package mixin;

import com.client.impl.function.client.CustomCape;
import com.client.system.function.FunctionManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListEntry.class)
public class PlayerListEntryMixin {
    @Final
    @Shadow
    private GameProfile profile;

    @Unique
    private Identifier customCapeTexture;
    private CustomCape customCape = FunctionManager.get(CustomCape.class);

    @Inject(
            method = {"getCapeTexture"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private void getCapeTextureHook(CallbackInfoReturnable<Identifier> cir) {
        if (customCape.isEnabled()) {
            this.getTexture(profile);
            if (customCapeTexture != null) cir.setReturnValue(customCapeTexture);
        }
    }

    @Inject(
            method = {"getElytraTexture"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private void getElytraTextureHook(CallbackInfoReturnable<Identifier> cir) {
        if (customCape.isEnabled()) {
            this.getTexture(profile);
            if (customCapeTexture != null) cir.setReturnValue(customCapeTexture);
        }
    }

    @Unique
    private void getTexture(GameProfile profile) {
        if (!customCape.loadedCapeTexture) {
            customCape.loadedCapeTexture = true;
            Util.getMainWorkerExecutor().execute(() -> {
                customCapeTexture = customCape.getCapeTexture(profile);
            });
        }
    }
}
