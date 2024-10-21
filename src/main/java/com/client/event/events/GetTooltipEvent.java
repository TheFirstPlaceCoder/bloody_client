package com.client.event.events;

import com.client.event.IEvent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class GetTooltipEvent extends IEvent {
    public ItemStack itemStack;
    public int x, y;
    public MatrixStack matrixStack;
    public List<Text> list;

    public GetTooltipEvent(ItemStack itemStack, List<Text> list, MatrixStack matrixStack, int x, int y) {
        this.itemStack = itemStack;
        this.x = x;
        this.y = y;
        this.matrixStack = matrixStack;
        this.list = list;
    }
}
