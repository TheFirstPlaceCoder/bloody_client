package mixin;

import net.minecraft.client.ClientBrandRetriever;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ClientBrandRetriever.class, remap = false)
public abstract class ClientBrandRetrieverMixin {
}