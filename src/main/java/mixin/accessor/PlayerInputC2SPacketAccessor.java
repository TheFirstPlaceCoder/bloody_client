package mixin.accessor;

import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerInputC2SPacket.class)
public interface PlayerInputC2SPacketAccessor {
    @Accessor("sideways")
    void setSideways(float s);

    @Accessor("forward")
    void setForward(float f);
}
