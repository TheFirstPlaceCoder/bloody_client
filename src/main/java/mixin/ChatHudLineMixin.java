package mixin;

import com.client.interfaces.IChatHudLine;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChatHudLine.class)
public class ChatHudLineMixin implements IChatHudLine {
    @Unique private float x = -1;

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public float getX() {
        return x;
    }
}