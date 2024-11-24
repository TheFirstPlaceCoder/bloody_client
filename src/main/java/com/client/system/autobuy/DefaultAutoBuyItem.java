package com.client.system.autobuy;

import com.client.impl.function.client.AutoBuy;
import com.client.system.function.FunctionManager;
import com.client.utils.game.entity.ServerUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class DefaultAutoBuyItem extends AutoBuyItem {
    public DefaultAutoBuyItem(Item item, int price) {
        this.item = item;
        this.price = price;
    }

    public boolean tryBuy(ItemStack stack, int price) {
        return stack.getItem().equals(item) && price / stack.getCount() <= this.price && price <= ServerUtils.getBalance() && this.price <= ServerUtils.getBalance();
    }
}