package com.client.interfaces;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;

public interface IClientPlayerInteractionManager {
    void click(int syncId, int slotId, int clickData, SlotActionType actionType, PlayerEntity player, int id);
}
