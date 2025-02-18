package com.client.utils.render.wisetree.render.render2d.utils;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.client.BloodyClient.mc;

public class PlayerHeadTexture {
    public static final NativeImageBackedTexture EMPTY = new NativeImageBackedTexture(1, 1, false);
    public static final HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
    public static final Map<UUID, Identifier> texturesThatHaveBeenResolved = new ConcurrentHashMap<>();
    public static final Map<String, Identifier> texturesThatHaveBeenResolvedByName = new ConcurrentHashMap<>();

    public static void clear() {
        for (Map.Entry<UUID, Identifier> uuidTextureEntry : texturesThatHaveBeenResolved.entrySet()) {
            mc.getTextureManager().destroyTexture(uuidTextureEntry.getValue());
        }

        texturesThatHaveBeenResolved.clear();

        for (Map.Entry<String, Identifier> uuidTextureEntry : texturesThatHaveBeenResolvedByName.entrySet()) {
            mc.getTextureManager().destroyTexture(uuidTextureEntry.getValue());
        }

        texturesThatHaveBeenResolvedByName.clear();
    }

    public static Identifier resolve(UUID name) {
        return texturesThatHaveBeenResolved.computeIfAbsent(name, uuid1 -> {
            Identifier a = new Identifier("assets", "assets/temp" + new Random().nextInt(10000) + ".png");
            resolve(uuid1, a);
            return a;
        });
    }

    public static Identifier resolve(String name) {
        return texturesThatHaveBeenResolvedByName.computeIfAbsent(name, uuid1 -> {
            Identifier a = new Identifier("assets", "assets/temp" + new Random().nextInt(10000) + ".png");
            resolveString(uuid1, a);
            return a;
        });
    }

    private static void resolve(UUID uuid, Identifier texture) {
        mc.execute(() -> mc.getTextureManager().registerTexture(texture, EMPTY));
        URI u = URI.create("https://mc-heads.net/avatar/" + uuid.toString());
        HttpRequest hr = HttpRequest.newBuilder().uri(u).header("user-agent", "assets/minecraft").build();
        client.sendAsync(hr, HttpResponse.BodyHandlers.ofByteArray()).thenAccept(httpResponse -> {
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ImageIO.write(ImageIO.read(new ByteArrayInputStream(httpResponse.body())), "png", stream);
                byte[] bytes = stream.toByteArray();

                ByteBuffer data = BufferUtils.createByteBuffer(bytes.length).put(bytes);
                data.flip();
                NativeImage img = NativeImage.read(data);
                NativeImageBackedTexture nib = new NativeImageBackedTexture(img);

                mc.execute(() -> mc.getTextureManager().registerTexture(texture, nib));
            } catch (Exception ignored) {
            }
        }).exceptionally(throwable -> null);
    }

    private static void resolveString(String name, Identifier texture) {
        mc.execute(() -> mc.getTextureManager().registerTexture(texture, EMPTY));
        URI u = URI.create("https://mc-heads.net/avatar/" + name);
        HttpRequest hr = HttpRequest.newBuilder().uri(u).header("user-agent", "assets/minecraft").build();
        client.sendAsync(hr, HttpResponse.BodyHandlers.ofByteArray()).thenAccept(httpResponse -> {
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ImageIO.write(ImageIO.read(new ByteArrayInputStream(httpResponse.body())), "png", stream);
                byte[] bytes = stream.toByteArray();

                ByteBuffer data = BufferUtils.createByteBuffer(bytes.length).put(bytes);
                data.flip();
                NativeImage img = NativeImage.read(data);
                NativeImageBackedTexture nib = new NativeImageBackedTexture(img);

                mc.execute(() -> mc.getTextureManager().registerTexture(texture, nib));
            } catch (Exception ignored) {
            }
        }).exceptionally(throwable -> null);
    }
}