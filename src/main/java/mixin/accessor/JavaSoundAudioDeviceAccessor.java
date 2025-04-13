package mixin.accessor;

import javazoom.jl.player.JavaSoundAudioDevice;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.sound.sampled.SourceDataLine;

@Mixin(JavaSoundAudioDevice.class)
public interface JavaSoundAudioDeviceAccessor {
    @Accessor("source")
    SourceDataLine getSource();
}
