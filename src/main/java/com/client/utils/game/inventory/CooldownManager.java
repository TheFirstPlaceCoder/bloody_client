package com.client.utils.game.inventory;

import api.interfaces.EventHandler;
import com.client.event.events.AddCooldownEvent;
import mixin.accessor.ItemCooldownManagerAccessor;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;

import static com.client.BloodyClient.mc;

public class CooldownManager {
    public static Map<Item, Integer> coolingItems = new HashMap<>();

    @EventHandler
    public void onAddCooldown(AddCooldownEvent event) {
        coolingItems.put(event.item, event.endTick);
    }

    public static int getTickTime(Item item) {
        return coolingItems.getOrDefault(item, -1) - ((ItemCooldownManagerAccessor) mc.player.getItemCooldownManager()).getTick();
    }
}
