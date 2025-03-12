package mixin;

import com.client.event.events.AddCooldownEvent;
import com.client.utils.game.inventory.CooldownManager;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemCooldownManager.class)
public class ItemCooldownManagerMixin {
    @Shadow
    private int tick;

    @Inject(method = "set", at = @At("HEAD"), cancellable = true)
    private void onAdd(Item item, int duration, CallbackInfo ci) {
        AddCooldownEvent event = AddCooldownEvent.get(item, tick, tick + duration, duration);
        event.post();
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void onRemove(Item item, CallbackInfo ci) {
        CooldownManager.coolingItems.remove(item);
    }
}