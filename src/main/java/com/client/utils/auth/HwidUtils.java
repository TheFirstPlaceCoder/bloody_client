package com.client.utils.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HwidUtils {
    public static String getUserHWID() {
        // TODO: Ниже представлен код генерации уникального HWID-ключа для проверки компа юзера

        String a = "";
        try {
            String appdata = System.getenv("APPDATA");

            // TODO: складываем хвид из следующих частей:
            // - юзернейм пользователя
            // - архитектура компа
            // - проверка на существование апдаты
            // - os.arch (не помню что за хуйня)
            // - версия операционки
            String result = System.getProperty("user.name")
                    + System.getenv("SystemRoot") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_ARCHITECTURE")
                    + (appdata == null ? "alternatecopium" : appdata + "copium")
                    + System.getProperty("os.arch")
                    + System.getProperty("os.version");

            // Хешируем все это добро
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(result.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < digest.length; i++)
                builder.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));

            // Та-дам, у нас есть хвид челикса
            result = builder.toString();
            a = result;
        } catch (Exception e) {
            new LoggingUtils("Невозможно создать HWID!", false);
        }

        return a;
    }
}