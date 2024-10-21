package com.client.utils.render;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardEvent {
    public static List<Runnable> runnables = new ArrayList();

    public static void trigger() {
        if (runnables.size() > 0) {
            runnables.parallelStream().forEach((i) -> {
                i.run();
            });
        }
    }

    public static void subscribe(Runnable action) {
        runnables.add(action);
    }

    public static void unSubscribe(Runnable action) {
        runnables.remove(action);
    }
}
