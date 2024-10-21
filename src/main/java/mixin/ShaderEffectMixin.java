package mixin;

import com.client.interfaces.IShaderEffect;
import mixin.accessor.PostProcessShaderAccessor;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostProcessShader;
import net.minecraft.client.gl.ShaderEffect;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(ShaderEffect.class)
public class ShaderEffectMixin implements IShaderEffect {
    @Unique
    private final List<String> fakedBufferNames = new ArrayList<>();
    @Shadow @Final private Map<String, Framebuffer> targetsByName;
    @Shadow @Final private List<PostProcessShader> passes;

    @Override
    public void addFakeTargetHook(String name, Framebuffer buffer) {
        Framebuffer previousFramebuffer = this.targetsByName.get(name);
        if (previousFramebuffer == buffer) {
            return;
        }
        if (previousFramebuffer != null) {
            for (PostProcessShader pass : this.passes) {
                if (pass.input == previousFramebuffer) ((PostProcessShaderAccessor) pass).setInput(buffer);
                if (pass.output == previousFramebuffer) ((PostProcessShaderAccessor) pass).setOutput(buffer);
            }
            this.targetsByName.remove(name);
            this.fakedBufferNames.remove(name);
        }

        this.targetsByName.put(name, buffer);
        this.fakedBufferNames.add(name);
    }

    @Inject(method = "close", at = @At("HEAD"))
    void deleteFakeBuffersHook(CallbackInfo ci) {
        for (String fakedBufferName : fakedBufferNames)
            targetsByName.remove(fakedBufferName);
    }
}