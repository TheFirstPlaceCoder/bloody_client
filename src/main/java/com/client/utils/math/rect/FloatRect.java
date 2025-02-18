package com.client.utils.math.rect;

public class FloatRect extends Rect<Float> {
    public FloatRect() {
        super(0f, 0f, 0f, 0f);
    }

    public FloatRect(Rect<Float> rect) {
        super(rect.getX(), rect.getY(), rect.getW(), rect.getH());
    }

    public FloatRect(int x, int y, int w, int h) {
        super((float) x, (float) y, (float) w, (float) h);
    }

    public FloatRect(double x, double y, double w, double h) {
        super((float) x, (float) y, (float) w, (float) h);
    }

    public FloatRect(float x, float y, float w, float h) {
        super(x, y, w, h);
    }

    public FloatRect(Float x, Float y, Float w, Float h) {
        super(x, y, w, h);
    }

    @Override
    public FloatRect expand(Float x) {
        this.x -= x;
        this.y -= x;
        this.w += x * 2;
        this.h += x * 2;
        setup();
        return this;
    }

    @Override
    public FloatRect setX(Float x) {
        this.x = x;
        setup();
        return this;
    }

    @Override
    public FloatRect addX(Float x) {
        this.x += x;
        setup();
        return this;
    }

    @Override
    public FloatRect setY(Float y) {
        this.y = y;
        setup();
        return this;
    }

    @Override
    public FloatRect addY(Float y) {
        this.y += y;
        setup();
        return this;
    }

    @Override
    public FloatRect setW(Float w) {
        this.w = w;
        setup();
        return this;
    }

    @Override
    public FloatRect addW(Float w) {
        this.w += w;
        setup();
        return this;
    }

    @Override
    public FloatRect setH(Float h) {
        this.h = h;
        setup();
        return this;
    }

    @Override
    public FloatRect addH(Float h) {
        this.h += h;
        setup();
        return this;
    }

    @Override
    public FloatRect set(Float x, Float y, Float w, Float h) {
        setX(x).setY(y).setW(w).setH(h).setup();
        return this;
    }

    @Override
    public Float getCenteredX() {
        return getX() + getW() / 2;
    }

    @Override
    public Float getCenteredY() {
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
