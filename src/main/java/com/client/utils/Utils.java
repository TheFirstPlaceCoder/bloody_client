package com.client.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import static com.client.BloodyClient.mc;

public class Utils {
    public static boolean isRussianLanguage = true;
    public static final Random random = new Random();
    public static int random(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    public static double random(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    public static float random(float min, float max) {
        return (float) (Math.random() * (max - min) + min);
    }

    public static Vec3d getCenterPos(BlockPos pos) {
        return new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    public static Color getFromRGB(int color, boolean hasAlpha) {
        return new Color((color >> 16) & 0x000000FF, (color >> 8) & 0x000000FF, (color) & 0x000000FF, hasAlpha ? (color >> 24) & 0x000000FF : 255);
    }

    public static int fromRGBA(Color color) {
        return (color.getRed() << 16) + (color.getGreen() << 8) + (color.getBlue()) + (color.getAlpha() << 24);
    }

    public static String getEnchantSimpleName(Enchantment enchantment, int length) {
        return enchantment.getName(0).getString().substring(0, length);
    }

    public static int getWindowWidth() {
        return mc.getWindow().getFramebufferWidth();
    }

    public static int getWindowHeight() {
        return mc.getWindow().getFramebufferHeight();
    }

    public static void unscaledProjection() {
        RenderSystem.matrixMode(5889);
        RenderSystem.loadIdentity();
        RenderSystem.ortho(0.0D, mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(), 0.0D, 1000.0D, 3000.0D);
        RenderSystem.matrixMode(5888);
        RenderSystem.loadIdentity();
        RenderSystem.translatef(0.0F, 0.0F, -2000.0F);
    }

    public static void scaledProjection() {
        RenderSystem.matrixMode(5889);
        RenderSystem.loadIdentity();
        RenderSystem.ortho(0.0D, mc.getWindow().getFramebufferWidth() / mc.getWindow().getScaleFactor(), mc.getWindow().getFramebufferHeight() / mc.getWindow().getScaleFactor(), 0.0D, 1000.0D, 3000.0D);
        RenderSystem.matrixMode(5888);
        RenderSystem.loadIdentity();
        RenderSystem.translatef(0.0F, 0.0F, -2000.0F);
    }

    public static void rescaling(Runnable task) {
        RenderSystem.pushMatrix();
        setScale(mc.getWindow().calculateScaleFactor(2, mc.forcesUnicodeFont()));
        //onResolutionChanged(true);
        task.run();
        //onResolutionChanged(false);
        setScale((float) mc.getWindow().getScaleFactor());
        RenderSystem.popMatrix();
    }

    public static void setScale(float m) {
        RenderSystem.matrixMode(5889);
        RenderSystem.loadIdentity();
        RenderSystem.ortho(0, mc.getWindow().getFramebufferWidth() / m, mc.getWindow().getFramebufferHeight() / m, 0, 1000, 3000);
        RenderSystem.matrixMode(5888);
        RenderSystem.loadIdentity();
        RenderSystem.translatef(0, 0, -2000);
    }

    public static void onResolutionChanged(boolean start) {
        int i = mc.getWindow().calculateScaleFactor(start ? 2 : mc.options.guiScale, mc.forcesUnicodeFont());
        mc.getWindow().setScaleFactor((double)i);
        if (mc.currentScreen != null) {
            mc.currentScreen.resize(mc, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight());
        }

        Framebuffer framebuffer = mc.getFramebuffer();
        framebuffer.resize(mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(), MinecraftClient.IS_SYSTEM_MAC);
//        mc.gameRenderer.onResized(mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight());
//        mc.mouse.onResolutionChanged();
    }

    public static double clamp(double value, double min, double max) {
        if (value < min) return min;
        return Math.min(value, max);
    }

    public static int lerp(int o, int i, double p) {
        return (int) Math.floor(i + (o - i) * MathHelper.clamp(p, 0, 1));
    }

    public static double lerp(double i, double o, double p) {
        return (i + (o - i) * MathHelper.clamp(p, 0, 1));
    }

    public static Color lerp(Color a, Color b, double c) {
        return new Color(lerp(a.getRed(), b.getRed(), c),
                lerp(a.getGreen(), b.getGreen(), c),
                lerp(a.getBlue(), b.getBlue(), c),
                lerp(a.getAlpha(), b.getAlpha(), c));
    }

    public static float lerpCircular(float a, float b, float t) {
        // 1. Calculate the shortest distance between the angles:
        a = normalizeAngle(a);
        b = normalizeAngle(b);

        float diff = b - a;

        // 2. Handle crossing the +/- 180 boundary:
        if (diff > 180f) {
            diff -= 360f;
        } else if (diff < -180f) {
            diff += 360f;
        }

        // 3. Perform the linear interpolation:
        float interpolatedAngle = a + diff * t;

        // 4. Normalize the angle to the range -180 to 180:
        return normalizeAngle(interpolatedAngle);
    }

    private static float normalizeAngle(float angle) {
        while (angle > 180f) {
            angle -= 360f;
        }
        while (angle < -180.0) {
            angle += 360f;
        }
        return angle;
    }

    public static double roundToDecimal(double n, int point) {
        if (point == 0) {
            return java.lang.Math.floor(n);
        }
        double factor = java.lang.Math.pow(10, point);
        return java.lang.Math.round(n * factor) / factor;
    }

    public static Color injectAlpha(Color color, int alp) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alp);
    }

    public static String getStringIgnoreLastChar(String str) {
        StringBuilder s = new StringBuilder();

        for (int i = 0; i < str.toCharArray().length - 1; i++) {
            s.append(str.toCharArray()[i]);
        }

        return s.toString();
    }

    public static String formatDuration(float seconds) {
        int minutes = (int) seconds / 60;
        int secs = (int) seconds % 60;
        return String.format("%d:%02d", minutes, secs);
    }

    public static String generateHash(String text) {
        String a = "SfG123+H" + text + "1389HGA1";
        return sha256(a);
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}