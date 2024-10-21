package com.client.utils.render;

import net.minecraft.util.math.Vec2f;

public class Rectangle {
    private double x, y, x1, y1;

    public Rectangle(double x, double y, double x1, double y1) {
        this.x = x;
        this.y = y;
        this.x1 = x1;
        this.y1 = y1;
    }

    public boolean contains(double x, double y) {
        return x >= this.x && x < this.x1 && y >= this.y && y < this.y1;
    }

    public double getWidth() {
        return x1 - x;
    }

    public double getHeight() {
        return y1 - y;
    }

    public Rectangle multiplyWidthHeight(Vec2f multiplier) {
        double w = getWidth() * multiplier.x;
        double h = getHeight() * multiplier.y;
        setX1(getX() + w);
        setY1(getY() + h);
        return this;
    }

    public double getX() {
        return x;
    }

    public double getX1() {
        return x1;
    }

    public double getY() {
        return y;
    }

    public double getY1() {
        return y1;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setX1(double x1) {
        this.x1 = x1;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setY1(double y1) {
        this.y1 = y1;
    }
}
