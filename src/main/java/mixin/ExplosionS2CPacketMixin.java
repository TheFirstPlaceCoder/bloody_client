package mixin;

import com.client.interfaces.IExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ExplosionS2CPacket.class)
public class ExplosionS2CPacketMixin implements IExplosionS2CPacket {
    @Shadow private float playerVelocityX;

    @Shadow private float playerVelocityY;

    @Shadow private float playerVelocityZ;

    @Override
    public void setVelocityX(float velocity) {
        playerVelocityX = velocity;
    }

    @Override
    public void setVelocityY(float velocity) {
        playerVelocityY = velocity;
    }

    @Override
    public void setVelocityZ(float velocity) {
        playerVelocityZ = velocity;
    }
}