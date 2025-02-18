package mixin.accessor;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerMoveC2SPacket.class)
public interface PlayerMoveC2SPacketAccessor {
    @Accessor("x")
    void setX(double x);

    @Accessor("x")
    double getX();

    @Accessor("y")
    void setY(double y);

    @Accessor("y")
    double getY();

    @Accessor("z")
    void setZ(double z);

    @Accessor("z")
    double getZ();

    @Accessor("pitch")
    void setPitch(float y);

    @Accessor("yaw")
    void setYaw(float y);

    @Accessor("onGround")
    void setOnGround(boolean onGround);

    @Accessor("onGround")
    boolean getOnGround();

    @Accessor("changeLook")
    void setChangleLook(boolean onGround);

    @Accessor("changeLook")
    boolean isChangedLook();
}