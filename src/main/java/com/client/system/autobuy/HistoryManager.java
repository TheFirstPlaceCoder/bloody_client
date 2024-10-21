package com.client.system.autobuy;

import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private static final List<HistoryItem> historyItem = new ArrayList<>();

    public static void add(HistoryItem item) {
        historyItem.add(item);
    }

    public static List<HistoryItem> getHistoryItem() {
        return historyItem;
    }
}