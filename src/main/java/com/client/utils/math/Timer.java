package com.client.utils.math;

public class Timer {
    private int time;

    public Timer() {
    }

    public Timer(int time) {
        this.time = time;
    }

    public void tick() {
        tick(1);
    }

    public void tick(int speed) {
        time+=speed;
    }

    public boolean passed(int v) {
        return time() >= v;
    }

    public void resetIfPassed(int v) {
        resetIfPassed$Task(v, null);
    }

    public void resetIfPassed$Task(int v, Runnable runnable) {
        if (passed(v)) {
            if (runnable != null) {
                runnable.run();
            }
            reset();
        }
    }

    public void multiply(int m) {
        time *= m;
    }

    public void reset() {
        time = 0;
    }

    public int time() {
        return time;
    }
}
