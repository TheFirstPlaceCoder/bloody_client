package com.client.utils.color;

import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class Colors {
    public static int speed;
    public static boolean isAstolfo = false, isRainbow = false;
    private static Color first = new Color(26, 232, 130, 255), second = new Color(41, 183, 213, 255);

    public static Color getFirst() {
        if (isAstolfo || isRainbow) return getColor(0);
        return first;
    }

    public static Color getSecond() {
        if (isAstolfo || isRainbow) return getColor(180);

        return second;
    }

    public static void setFirst(Color first) {
        Colors.first = first;
    }

    public static void setSecond(Color second) {
        Colors.second = second;
    }

    public static int getIndex(int i, int in) {
        return (int) (Math.max(i, 0.5f) * (270 / in));
    }

    public static Color getColor() {
        return getColor(false);
    }

    public static Color getColor(boolean two) {
        return getColor(two ? 270 : 0, 51 - speed);
    }

    public static Color getColor(int index) {
        return getColor(index, 51 - speed);
    }

    public static Color getColor(int index, int speed) {
        if (isRainbow) return rainbow(index, speed);
        if (isAstolfo) return astolfo(index, speed);
        return new Color(gradient(index, speed));
    }

    public static int gradient(int index, int speed) {
        return gradient(first.getRGB(), second.getRGB(), index, speed);
    }

    public static int gradient(int first, int second, int index, int speed) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        angle = (angle > 180 ? 360 - angle : angle) + 180;

        int color = interpolate(first, second, MathHelper.clamp(angle / 180f - 1, 0, 1));
        float[] hs = ColorUtils.rgba(color);
        float[] hsb = Color.RGBtoHSB((int) (hs[0] * 255), (int) (hs[1] * 255), (int) (hs[2] * 255), null);

        hsb[1] *= 1.5F;
        hsb[1] = Math.min(hsb[1], 1.0f);

        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2])).getRGB();
    }

    public static Color astolfo(int index, int speed) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        return Color.getHSBColor((double) ((float) ((angle %= 360) / 360.0)) < 0.5 ? -((float) (angle / 360.0)) : (float) (angle / 360.0), 0.5F, 1.0F);
    }

    public static Color rainbow(int index, int speed) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        float hue = angle / 360.0f;
        return Color.getHSBColor(hue, 1.0f, 1.0f);
    }

    private static int interpolate(int start, int end, float value) {
        float[] startColor = ColorUtils.rgba(start);
        float[] endColor = ColorUtils.rgba(end);

        return ColorUtils.rgba((int) interpolate(startColor[0] * 255, endColor[0] * 255, value),
                (int) interpolate(startColor[1] * 255, endColor[1] * 255, value),
                (int) interpolate(startColor[2] * 255, endColor[2] * 255, value),
                (int) interpolate(startColor[3] * 255, endColor[3] * 255, value));
    }

    private static double interpolate(double current, double old, double scale) {
        return old + (current - old) * scale;
    }
}