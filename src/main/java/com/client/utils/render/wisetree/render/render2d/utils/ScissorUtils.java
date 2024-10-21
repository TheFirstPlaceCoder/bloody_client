package com.client.utils.render.wisetree.render.render2d.utils;

import com.client.utils.math.rect.DoubleRect;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.math.rect.IntRect;
import com.client.utils.math.rect.Rect;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

import static com.client.BloodyClient.mc;

public class ScissorUtils {
    private static class State implements Cloneable {
        public boolean enabled;
        public int transX;
        public int transY;
        public int x;
        public int y;
        public int width;
        public int height;

        @Override
        public State clone() {
            try {
                return (State) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }
    }

    private static State state = new State();

    private static final List<State> stateStack = Lists.newArrayList();

    public static void enableScissor(IntRect rect) {
        push();
        setFromComponentCoordinates(rect.getX(), rect.getY(), rect.getW(), rect.getH());
    }

    public static void enableScissor(DoubleRect rect) {
        push();
        setFromComponentCoordinates(rect.getX(), rect.getY(), rect.getW(), rect.getH());
    }

    public static void enableScissor(FloatRect rect) {
        push();
        setFromComponentCoordinates(rect.getX(), rect.getY(), rect.getW(), rect.getH());
    }

    public static void disableScissor() {
        unset();
        pop();
    }

    public static void push() {
        stateStack.add(state.clone());
        GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
    }

    public static void pop() {
        state = stateStack.remove(stateStack.size() - 1);
        GL11.glPopAttrib();
    }

    public static void unset() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        state.enabled = false;
    }

    public static void setFromComponentCoordinates(int x, int y, int width, int height) {
        int scaleFactor = 2;

        int screenX = x * scaleFactor;
        int screenY = y * scaleFactor;
        int screenWidth = width * scaleFactor;
        int screenHeight = height * scaleFactor;
        screenY = MinecraftClient.getInstance().getWindow().getHeight() - screenY - screenHeight;
        set(screenX, screenY, screenWidth, screenHeight);
    }

    public static void setFromComponentCoordinates(double x, double y, double width, double height) {
        int scaleFactor = 2;

        int screenX = (int) (x * scaleFactor);
        int screenY = (int) (y * scaleFactor);
        int screenWidth = (int) (width * scaleFactor);
        int screenHeight = (int) (height * scaleFactor);
        screenY = MinecraftClient.getInstance().getWindow().getHeight() - screenY - screenHeight;
        set(screenX, screenY, screenWidth, screenHeight);
    }

    public static void setFromComponentCoordinates(double x, double y, double width, double height, double scale) {

        float animationValue = (float) scale;

        float halfAnimationValueRest = (1 - animationValue) / 2f;
        double testX = x + (width * halfAnimationValueRest);
        double testY = y + (height * halfAnimationValueRest);
        double testW = width * animationValue;
        double testH = height * animationValue;

        testX = testX * animationValue + ((MinecraftClient.getInstance().getWindow().getScaledWidth() - testW) *
                halfAnimationValueRest);

        float scaleFactor = 2;

        int screenX = (int) (testX * scaleFactor);
        int screenY = (int) (testY * scaleFactor);
        int screenWidth = (int) (testW * scaleFactor);
        int screenHeight = (int) (testH * scaleFactor);
        screenY = MinecraftClient.getInstance().getWindow().getHeight() - screenY - screenHeight;
        set(screenX, screenY, screenWidth, screenHeight);
    }

    public static void set(int x, int y, int width, int height) {
        Rectangle screen = new Rectangle(0, 0, MinecraftClient.getInstance().getWindow().getWidth(),
                MinecraftClient.getInstance().getWindow().getHeight());
        Rectangle current;
        if (state.enabled) {
            current = new Rectangle(state.x, state.y, state.width, state.height);
        } else {
            current = screen;
        }
        Rectangle target = new Rectangle(x + state.transX, y + state.transY, width, height);
        Rectangle result = current.intersection(target);
        result = result.intersection(screen);
        if (result.width < 0)
            result.width = 0;
        if (result.height < 0)
            result.height = 0;
        state.enabled = true;
        state.x = result.x;
        state.y = result.y;
        state.width = result.width;
        state.height = result.height;
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(result.x, result.y, result.width, result.height);
    }

    public static void translate(int x, int y) {
        state.transX = x;
        state.transY = y;
    }

    public static void translateFromComponentCoordinates(int x, int y) {
        Window res = MinecraftClient.getInstance().getWindow();
        int totalHeight = res.getScaledHeight();
        int scaleFactor = (int) res.getScaleFactor();

        int screenX = x * scaleFactor;
        int screenY = y * scaleFactor;
        screenY = (totalHeight * scaleFactor) - screenY;
        translate(screenX, screenY);
    }
}
