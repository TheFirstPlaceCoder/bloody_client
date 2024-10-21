package com.client.utils.misc;

public class TaskTransfer {
    private Runnable task;
    private long delay;

    public TaskTransfer() {
        this(null, 1L);
    }

    public TaskTransfer(Runnable task, long delay) {
        this.task = task;
        this.delay = System.currentTimeMillis() + delay;
    }

    public void bind(Runnable task, long delay) {
        this.task = task;
        this.delay = System.currentTimeMillis() + delay;
    }

    public void handle() {
        if (task == null) return;

        if (System.currentTimeMillis() > delay) {
            task.run();
            task = null;
        }
    }

    public boolean passed() {
        return System.currentTimeMillis() > delay && task == null;
    }
}
