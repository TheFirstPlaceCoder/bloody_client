package com.client.clickgui;

import com.client.utils.math.animation.AnimationUtils;

import javax.swing.event.AncestorEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextAnimation {
    private final List<String> queue = new ArrayList<>();
    private String current;
    private float alpha = 0;
    private int direction = 1, i = 0;
    private long idleTime = 0;

    public TextAnimation(String current) {
        this.current = current;
    }

    public void addQueue(String... strings) {
        queue.addAll(Arrays.asList(strings));
    }

    public void tick() {
        if (System.currentTimeMillis() > idleTime) {
            alpha = AnimationUtils.fast(alpha, direction * 1f, 10);
            idleTime = 0;
        }

        if (!queue.isEmpty()) {
            if (getAlpha() <= 20) {
                current = queue.get(0);
                queue.remove(0);
                i++;
                direction = 1;
            } else if (getAlpha() >= 240) {
                if (idleTime <= 0 && i > 0) {
                    idleTime = System.currentTimeMillis() + 300L;
                }
                direction = 0;
            }
        } else {
            i = 0;
        }
    }

    public String getText() {
        return current;
    }

    public int getAlpha() {
        return (int) (alpha * 255);
    }
}