package mixin;

import com.client.interfaces.IClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClickSlotC2SPacket.class)
public class ClickSlotC2SPacketMixin implements IClickSlotC2SPacket {
    @Unique
    public int id;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }
}