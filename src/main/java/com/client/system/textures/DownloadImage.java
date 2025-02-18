package com.client.system.textures;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static com.client.BloodyClient.mc;

public class DownloadImage {
    public static final HashMap<Integer, Pair<Integer, Identifier>> cache = new HashMap<>();
    public static boolean init;
    public static final int STAR = hash("https://imgur.com/star");
    public static final int HEART = hash("https://imgur.com/heart");
    public static final int SNOW = hash("https://imgur.com/snow");
    public static final int CIRCLE = hash("https://imgur.com/circle");
    public static final int REMOVE = hash("https://imgur.com/remove");
    public static final int ROUNDED_PLUS = hash("https://imgur.com/rounded_plus");
    public static final int REFRESH = hash("https://imgur.com/refresh");
    public static final int DEFAULT_MENU = hash("https://imgur.com/default_menu");
    public static final int CHRISTMAS_MENU = hash("https://imgur.com/christmas_menu");
    public static final int ARROWS = hash("https://imgur.com/arrows");
    public static final int GLOW_CIRCLE = hash("https://imgur.com/glow_circle");
    public static final int TRIANGLE_GLOW = hash("https://imgur.com/triangle_glow");
    public static final int WHIRLWIND_FIRST = hash("https://imgur.com/whirlwind");
    public static final int WHIRLWIND_SECOND = hash("https://imgur.com/whirlwind2");
    public static final int NAZI_CROSSHAIR = hash("https://imgur.com/nazzi_crosshair");
    public static final int KEYBOARD = hash("https://imgur.com/keyboard");
    public static final int PLAY = hash("https://imgur.com/play");
    public static final int STOP = hash("https://imgur.com/stop");
    public static final int NEXT = hash("https://imgur.com/next");
    public static final int BACK = hash("https://imgur.com/back");
    public static final int POTION = hash("https://imgur.com/potion");
    public static final int STAFF = hash("https://imgur.com/staff");
    public static final int GROUP = hash("https://imgur.com/group");
    public static final int PLANET = hash("https://imgur.com/planet");
    public static final int ACCOUNT = hash("https://imgur.com/account");
    public static final int COMPUTER = hash("https://imgur.com/computer");
    public static final int CONTAINER = hash("https://imgur.com/container");
    public static final int AURA_TEXTURE = hash("https://imgur.com/auratexture");
    public static final int CAPE_1 = hash("https://imgur.com/cape_1");
    public static final int CAPE_2 = hash("https://imgur.com/cape_2");
    public static final int CAPE_3 = hash("https://imgur.com/cape_3");
    public static final int MUSIC_8 = hash("https://imgur.com/music8");

    public static void clear() {
        for (Map.Entry<Integer, Pair<Integer, Identifier>> integerPairEntry : cache.entrySet()) {
            mc.getTextureManager().destroyTexture(integerPairEntry.getValue().getRight());
        }
        cache.clear();
        init = false;
        init();
    }

    public static void init() {
        if (!init) {
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/star");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/heart");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/snow");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/circle");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/remove");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/rounded_plus");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/refresh");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/default_menu");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/christmas_menu");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/arrows");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/glow_circle");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/triangle_glow");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/whirlwind");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/whirlwind2");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/nazzi_crosshair");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/keyboard");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/play");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/stop");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/next");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/back");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/potion");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/staff");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/group");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/planet");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/account");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/computer");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/container");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/auratexture");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/cape_1");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/cape_2");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/cape_3");
            download("https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/music8");
            init = true;
        }
    }

    public static int hash(String url) {
        return Objects.hash(url.replace("https://imgur.com/", "https://raw.githubusercontent.com/TheFirstPlaceCoder/files/main/") + ".png");
    }

    public static int getGlId(int id) {
        try {
            return cache.get(id).getLeft();
        } catch (Exception e) {
            return 1;
        }
    }

    public static Identifier getIdentifier(int id) {
        try {
            return cache.get(id).getRight();
        } catch (Exception e) {
            return new Identifier("sodium-extra", "icon.png");
        }
    }

    public static void download(String url) {
        url = url + ".png";
        int identifier = Objects.hash(url);
        if (!cache.containsKey(identifier)) {
            BufferedImage bi;

            try {
                bi = ImageIO.read(new URL(url));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Identifier id = new Identifier("assets", "temp" + new Random().nextInt(10000) + ".png");
            registerBufferedImageTexture(id, bi);
            cache.put(identifier, new Pair<>(loadTexture(bi), id));
        }
    }

    private static void registerBufferedImageTexture(Identifier i, BufferedImage bi) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(bi, "png", out);
            byte[] bytes = out.toByteArray();

            ByteBuffer data = BufferUtils.createByteBuffer(bytes.length).put(bytes);
            data.flip();
            NativeImageBackedTexture tex = new NativeImageBackedTexture(NativeImage.read(data));
            mc.execute(() -> mc.getTextureManager().registerTexture(i, tex));
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    private static int loadTexture(BufferedImage image) {
        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        ByteBuffer buffer = BufferUtils.createByteBuffer(pixels.length * 4);

        try {
            for (int pixel : pixels) {
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
            buffer.flip();
        } catch (BufferOverflowException | ReadOnlyBufferException ex) {
            ex.fillInStackTrace();
            return -1;
        }

        int textureID = GlStateManager.genTextures();
        GlStateManager.bindTexture(textureID);
        GlStateManager.texParameter(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
        GlStateManager.texParameter(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
        GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, buffer);
        GlStateManager.bindTexture(0);
        return textureID;
    }
}
