package com.client.utils.math.rect;

public abstract class Rect<T> {
    public T x, y, w, h, x2, y2;

    public Rect(T x, T y, T w, T h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        setup();
    }

    public T getX() {
        return x;
    }

    public T getY() {
        return y;
    }

    public T getW() {
        return w;
    }

    public T getH() {
        return h;
    }

    public T getX2() {
        return x2;
    }

    public T getY2() {
        return y2;
    }

    @Override
    public String toString() {
        return "x:" + x + ", y:" + y + ", w:" + w + ", h:" + h;
    }

    public abstract Rect<?> setX(T x);
    public abstract Rect<?> addX(T x);
    public abstract Rect<?> setY(T y);
    public abstract Rect<?> addY(T y);
    public abstract Rect<?> setW(T w);
    public abstract Rect<?> addW(T w);
    public abstract Rect<?> setH(T h);
    public abstract Rect<?> addH(T h);
    public abstract Rect<?> set(T x, T y, T w, T h);

    public abstract T getCenteredX();
    public abstract T getCenteredY();

    public abstract boolean intersect(float x, float y);
    public abstract boolean intersect(int x, int y);
    public abstract boolean intersect(double x, double y);

    public abstract boolean intersect(FloatRect rect);
    public abstract boolean intersect(DoubleRect rect);
    public abstract boolean intersect(IntRect rect);

    public abstract void setup();
}