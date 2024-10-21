package com.client.system.hud.magnet;

import java.util.ArrayList;
import java.util.List;

import static com.client.BloodyClient.mc;

public class MagnetManager {
    private static final List<MagnetLine> MAGNET_LINES = new ArrayList<>();

    public static MagnetLine INTERSECTED = null;

    public static void init() {
     // MAGNET_LINES.clear();

     // int x = mc.getWindow().getScaledWidth();
     // int y = mc.getWindow().getScaledHeight();

     // for (int i = y / 10; i < y; i += y / 10) {
     //     MAGNET_LINES.add(new MagnetLine(0, x, i, i));
     // }

     // for (int i = x / 20; i < x; i += x / 20) {
     //     MAGNET_LINES.add(new MagnetLine(i, i, 0, y));
     // }
    }

    public static MagnetLine intersecting(int mx, int my) {
        if (INTERSECTED != null && INTERSECTED.magnetized(mx, my))
            return INTERSECTED;

        try {
            return MAGNET_LINES.stream().filter(m -> m.magnetized(mx, my)).toList().get(0);
        } catch (Exception e) {
            return null;
        }
    }
}