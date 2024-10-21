package mixin;

import com.client.event.events.SoundEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {
    @Inject(
            method = {"play(Lnet/minecraft/client/sound/SoundInstance;)V"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private void onSoundPlayed(SoundInstance sound, CallbackInfo ci) {
        SoundEvent event = SoundEvent.get(sound, sound.getSoundSet(MinecraftClient.getInstance().getSoundManager()));
        event.post();
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}