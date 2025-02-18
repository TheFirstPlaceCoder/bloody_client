package com.client.impl.function.misc.nuker;

public class NukerThread extends Thread {
    public Runnable runnable = () -> {};

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        runnable.run();
    }
}