package com.client.clickgui.newgui.settings.color;

import com.client.clickgui.Impl;
import com.client.clickgui.newgui.settings.AbstractSettingElement;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render3d.Renderer3D;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class HueSlider implements Impl {
    public FloatRect rect;
    public Runnable task;
    public double value;

    private FloatRect interactRect = new FloatRect();
    private boolean dragged;
    private float pos;

    public HueSlider(FloatRect rect) {
        this.rect = rect;
    }

    @Override
    public void draw(double mx, double my, float alpha) {
        interactRect = new FloatRect(rect.getX() - 2, rect.getY() + 0, rect.getW() + 4, rect.getH() + 0);
        if (dragged) {
            value = MathHelper.clamp((my - rect.getY()) / (rect.getH()), 0, 1);
            task.run();
        }
        pos = AnimationUtils.fast(pos, (float) (value * rect.getH()));
        GL.prepare();
        GL11.glLineWidth(4f);
        Renderer3D.begin(GL11.GL_LINE_STRIP);
        for (float i = 0; i < rect.getH(); i++) {
            Renderer3D.color(AbstractSettingElement.inject(Color.getHSBColor(i / rect.getH(), 1f, 1f), alpha));
            GL11.glVertex2d(rect.getCenteredX(), rect.getY() + i);
        }
        Renderer3D.end();
        GL.end();
        GL.drawLine(rect.getX() - 1, rect.getY() + pos, rect.getX2() + 1, rect.getY() + pos, 4f, AbstractSettingElement.inject(Color.WHITE, alpha));
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
