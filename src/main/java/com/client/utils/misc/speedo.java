package com.client.utils.misc;

public class speedo {
    public double speed, factor;

    public speedo(double speed, double factor) {
        this.speed = speed;
        this.factor = factor;
    }

    public double getSpeed() {
        return speed;
    }

    public double getFactor() {
        return factor;
    }
}