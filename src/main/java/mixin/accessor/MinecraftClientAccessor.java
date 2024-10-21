package mixin.accessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {
    @Accessor("itemUseCooldown")
    void setItemUseCooldown(int set);

    @Mutable
    @Accessor("session")
    void setSession(Session session);

    @Accessor("framebuffer")
    void setFramebuffer(Framebuffer framebuffer);

    @Accessor("currentFps")
    int getFps();
}