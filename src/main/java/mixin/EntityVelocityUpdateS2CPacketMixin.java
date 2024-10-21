package mixin;

import com.client.interfaces.IEntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityVelocityUpdateS2CPacket.class)
public abstract class EntityVelocityUpdateS2CPacketMixin implements IEntityVelocityUpdateS2CPacket {
    @Shadow private int velocityX;
    @Shadow private int velocityY;
    @Shadow private int velocityZ;

    @Override
    public void setX(int velocityX) {
        this.velocityX = velocityX;
    }

    @Override
    public void setY(int velocityY) {
        this.velocityY = velocityY;
    }

    @Override
    public void setZ(int velocityZ) {
        this.velocityZ = velocityZ;
    }
}