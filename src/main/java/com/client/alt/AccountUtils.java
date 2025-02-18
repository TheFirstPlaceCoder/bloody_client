package com.client.alt;

import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.List;

public class AccountUtils {
    public static boolean shouldUpdate = false;
    private static final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_";
    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomAccount() {
        int length = random.nextInt(7) + 10; // Длина от 1 до 16
        StringBuilder sb = new StringBuilder(length);

        // Генерируем первый символ
        sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));

        for (int i = 1; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        // Проверяем, не стоит ли _ в начале
        if (sb.charAt(0) == '_' && sb.charAt(sb.length() - 1) != '_') {
            sb.setCharAt(0, CHARACTERS.charAt(random.nextInt(CHARACTERS.length() - 2))); // Заменяем на другой символ
        }

        // Проверяем, не стоит ли _ в конце
        if (sb.charAt(sb.length() - 1) == '_' && sb.charAt(0) != '_') {
            sb.setCharAt(sb.length() - 1, CHARACTERS.charAt(random.nextInt(CHARACTERS.length() - 2))); // Заменяем на другой символ
        }

        return sb.toString();
    }

    public static void setBaseUrl(YggdrasilMinecraftSessionService service, String url) {
        try {
            Field field = service.getClass().getDeclaredField("baseUrl");
            field.setAccessible(true);
            field.set(service, url);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void setJoinUrl(YggdrasilMinecraftSessionService service, String url) {
        try {
            Field field = service.getClass().getDeclaredField("joinUrl");
            field.setAccessible(true);
            field.set(service, new URL(url));
        } catch (IllegalAccessException | NoSuchFieldException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void setCheckUrl(YggdrasilMinecraftSessionService service, String url) {
        try {
            Field field = service.getClass().getDeclaredField("checkUrl");
            field.setAccessible(true);
            field.set(service, new URL(url));
        } catch (IllegalAccessException | NoSuchFieldException | MalformedURLException e) {
            e.printStackTrace();
        }
    }
}