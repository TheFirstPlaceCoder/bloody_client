package com.client.utils.math.rect;

public class DoubleRect extends Rect<Double> {
    public DoubleRect() {
        super(0d, 0d, 0d, 0d);
    }

    public DoubleRect(Rect<Double> rect) {
        super(rect.getX(), rect.getY(), rect.getW(), rect.getH());
    }

    public DoubleRect(int x, int y, int w, int h) {
        super((double) x, (double) y, (double) w, (double) h);
    }

    public DoubleRect(float x, float y, float w, float h) {
        super((double) x, (double) y, (double) w, (double) h);
    }

    public DoubleRect(double x, double y, double w, double h) {
        super(x, y, w, h);
    }

    public DoubleRect(Double x, Double y, Double w, Double h) {
        super(x, y, w, h);
    }

    @Override
    public DoubleRect setX(Double x) {
        this.x = x;
        setup();
        return this;
    }

    @Override
    public DoubleRect addX(Double x) {
        this.x += x;
        setup();
        return this;
    }

    @Override
    public DoubleRect setY(Double y) {
        this.y = y;
        setup();
        return this;
    }

    @Override
    public DoubleRect addY(Double y) {
        this.y += y;
        setup();
        return this;
    }

    @Override
    public DoubleRect setW(Double w) {
        this.w = w;
        setup();
        return this;
    }

    @Override
    public DoubleRect addW(Double w) {
        this.w += w;
        setup();
        return this;
    }

    @Override
    public DoubleRect setH(Double h) {
        this.h = h;
        setup();
        return this;
    }

    @Override
    public DoubleRect addH(Double h) {
        this.h += h;
        setup();
        return this;
    }

    @Override
    public DoubleRect set(Double x, Double y, Double w, Double h) {
        setX(x).setY(y).setW(w).setH(h).setup();
        return this;
    }

    @Override
    public Double getCenteredX() {
        return getX() + getW() / 2;
    }

    @Override
    public Double getCenteredY() {
        return getY() + getH() / 2;
    }

    @Override
    public boolean intersect(float x, float y) {
        return x > getX() && x < getX2() && y > getY() && y < getY2();
    }

    @Override
    public boolean intersect(int x, int y) {
        return x > getX() && x < getX2() && y > getY() && y < getY2();
    }

    @Override
    public boolean intersect(double x, double y) {
        return x > getX() && x < getX2() && y > getY() && y < getY2();
    }

    @Override
    public boolean intersect(FloatRect rect) {
        return x > rect.getX() && x < rect.getX2() && y > rect.getY() && y < rect.getY2();
    }

    @Override
    public boolean intersect(DoubleRect rect) {
        return x > rect.getX() && x < rect.getX2() && y > rect.getY() && y < rect.getY2();
    }

    @Override
    public boolean intersect(IntRect rect) {
        return x > rect.getX() && x < rect.getX2() && y > rect.getY() && y < rect.getY2();
    }

    @Override
    public void setup() {
        this.x2 = getX() + getW();
        this.y2 = getY() + getH();
    }
}
