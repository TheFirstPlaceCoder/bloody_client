package com.client.clickgui;

public interface Impl {
    void draw(double mx, double my, float alpha);

    void click(double mx, double my, int button);

    void release(double mx, double my, int button);

    void key(int key);

    void symbol(char chr);

    void scroll(double mx, double my, double amount);

    void close();
}
