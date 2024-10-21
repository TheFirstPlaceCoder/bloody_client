package com.client.utils.render.wisetree.font.main;

import com.client.utils.render.wisetree.font.api.FontRenderer;
import com.client.utils.render.wisetree.font.utils.InstallUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FontContainer {
    private final HashMap<Integer, FontRenderer> MAP = new HashMap<>();
    private final String link;

    public FontContainer(String link) {
        this.link = link;

        Executors.newSingleThreadExecutor().execute(() -> {
            for (int i = 0; i < 10; i++) {
                MAP.put(i, new FontRenderer(new Font[]{InstallUtils.installFont(link, i)}, i));
            }
        });
    }

    public FontRenderer get(int size) {
        return MAP.computeIfAbsent(size, m -> new FontRenderer(new Font[]{InstallUtils.installFont(link, size)}, size));
    }
}