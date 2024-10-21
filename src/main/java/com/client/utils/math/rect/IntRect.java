package com.client.utils.math.rect;

public class IntRect extends Rect<Integer> {
    public IntRect() {
        super(0, 0, 0, 0);
    }

    public IntRect(Rect<Integer> rect) {
        super(rect.getX(), rect.getY(),  rect.getW(),  rect.getH());
    }

    public IntRect(double x, double y, double w, double h) {
        super((int) x, (int) y, (int) w, (int) h);
    }

    public IntRect(float x, float y, float w, float h) {
        super((int) x, (int) y, (int) w, (int) h);
    }

    public IntRect(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    public IntRect(Integer x, Integer y, Integer w, Integer h) {
        super(x, y, w, h);
    }

    @Override
    public IntRect setX(Integer x) {
        this.x = x;
        setup();
        return this;
    }

    @Override
    public IntRect addX(Integer x) {
        this.x += x;
        setup();
        return this;
    }

    @Override
    public IntRect setY(Integer y) {
        this.y = y;
        setup();
        return this;
    }

    @Override
    public IntRect addY(Integer y) {
        this.y += y;
        setup();
        return this;
    }

    @Override
    public IntRect setW(Integer w) {
        this.w = w;
        setup();
        return this;
    }

    @Override
    public IntRect addW(Integer w) {
        this.w += w;
        setup();
        return this;
    }

    @Override
    public IntRect setH(Integer h) {
        this.h = h;
        setup();
        return this;
    }

    @Override
    public IntRect addH(Integer h) {
        this.h += h;
        setup();
        return this;
    }

    @Override
    public IntRect set(Integer x, Integer y, Integer w, Integer h) {
        setX(x).setY(y).setW(w).setH(h).setup();
        return this;
    }

    @Override
    public Integer getCenteredX() {
        return getX() + getW() / 2;
    }

    @Override
    public Integer getCenteredY() {
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
