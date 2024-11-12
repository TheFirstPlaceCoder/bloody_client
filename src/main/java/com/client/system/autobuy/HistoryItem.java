package com.client.system.autobuy;

import net.minecraft.item.Item;

public class HistoryItem {
    public int price, count;
    public Item stack;
    public String name;
    public boolean purchased = true;

    public HistoryItem(int price, Item stack, String name) {
        this.price = price;
        this.stack = stack;
        this.name = name;
    }
}