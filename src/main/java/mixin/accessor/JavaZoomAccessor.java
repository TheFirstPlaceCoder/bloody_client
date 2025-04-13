package mixin.accessor;


import javazoom.jl.decoder.Bitstream;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AdvancedPlayer.class)
public interface JavaZoomAccessor {
    @Accessor("lastPosition")
    int getLastPosition();

    @Accessor("bitstream")
    Bitstream getBitStream();

    @Accessor("audio")
    AudioDevice getAudio();
}
