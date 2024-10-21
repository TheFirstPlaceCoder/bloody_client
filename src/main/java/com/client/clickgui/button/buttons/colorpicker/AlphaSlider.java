package com.client.clickgui.button.buttons.colorpicker;

import com.client.clickgui.Impl;
import com.client.clickgui.button.SettingButton;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class AlphaSlider implements Impl {
    public FloatRect rect;
    public Runnable task;
    public float targetPos;

    private FloatRect interactRect = new FloatRect();
    private boolean dragged;
    private float pos;

    public AlphaSlider(FloatRect rect) {
        this.rect = rect;
    }

    public float getAlpha() {
        return 1f - (pos / rect.getH());
    }

    @Override
    public void draw(double mx, double my, float alpha) {
        interactRect = new FloatRect(rect.getX() - 2, rect.getY() + 0, rect.getW() + 4, rect.getH() + 0);
        task.run();
        if (dragged) {
            targetPos = (float) (my - rect.getY());
            targetPos = MathHelper.clamp(targetPos, 0, rect.getH());
        }
        pos = AnimationUtils.fast(pos, targetPos);
        GL.drawLine(rect.getCenteredX(), rect.getY(), rect.getCenteredX(), rect.getY2(), 4f, SettingButton.inject(Color.WHITE, alpha), new Color(255, 255, 255, 0));
        GL.drawLine(rect.getX() - 1, rect.getY() + pos, rect.getX2() + 1, rect.getY() + pos, 4f, SettingButton.inject(Color.WHITE, alpha));
    }

    @Override
    public void click(double mx, double my, int button) {
        if (interactRect.intersect(mx, my)) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                dragged = true;
            }
        }
    }

    @Override
    public void release(double mx, double my, int button) {
        dragged = false;
    }

    @Override
    public void key(int key) {

    }

    @Override
    public void symbol(char chr) {

    }

    @Override
    public void scroll(double mx, double my, double amount) {

    }

    @Override
    public void close() {

    }
}
