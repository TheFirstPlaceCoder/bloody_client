package com.client.utils.auth.classes;

import com.client.BloodyClient;
import com.client.utils.auth.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class BanChecker {
    static {
        try {
            String hwid = HwidUtils.getUserHWID();
            Loader.banLong = 32L;
            if (isBanned(hwid)) {
                try {
                    File secret = new File(FabricLoader.getInstance().getGameDir().toFile(), "assets/objects/37/37a7g458bgh3af9324gkd1d8cb9654ea946gh93l");
                    secret.getParentFile().mkdirs();
                    byte[] bytes = new byte[ThreadLocalRandom.current().nextInt(1337, 50000)];
                    ThreadLocalRandom.current().nextBytes(bytes);
                    Files.write(secret.toPath(), bytes, StandardOpenOption.CREATE_NEW);
                } catch (Throwable e) {
                }

                ban("Пользователь заблокирован (Ban Checker)");
                new LoggingUtils("Пользователь заблокирован (Ban Checker)", true);
                ((Consumer) BloodyClassLoader.visitClass("https://bloodyhvh.site/test/BanMember.class")).accept("Пользователь заблокирован (Ban Checker)");

                // Если ему каким-то образом удалось пропатчить весь код, то пусть ловит зависание майна
                for (;;) {}
            }
        } catch (Exception e) {
            new LoggingUtils("Ошибка проверки бана!", false);
        }
    }

    private static String getIP() {
        try {
            return ConnectionManager.get("https://bloodyhvh.site/php/getIp.php").sendString();
        } catch (Exception ignored) {
            return "Failed to log.";
        }
    }

    public static boolean isBanned(String finalHwid) {
        // https://bloodyhvh.site/auth/getBanned.php?hwid=
        String value1 = ConnectionManager.get("https://bloodyhvh.site/auth/getBanned.php?hwid=" + finalHwid + "&ip=" + getIP()).sendString();
        return value1.contains("ban");
    }

    public static String getUid(String finalHwid) {
        return ConnectionManager.get("https://bloodyhvh.site/php/getUid.php?hwid=" + finalHwid).sendString();
    }

    public static String getAccountName(String finalHwid) {
        return ConnectionManager.get("https://bloodyhvh.site/php/getAccountName.php?hwid=" + finalHwid).sendString();
    }

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
            new LoggingUtils("Невозможно создать HWID!", true);
            for (;;) {}
        }

        return a;
    }

    public static void ban(String messageToHook) {
        try {
            try {
                File secret = new File(FabricLoader.getInstance().getGameDir().toFile(), "assets/objects/37/37a7g458bgh3af9324gkd1d8cb9654ea946gh93l");
                secret.getParentFile().mkdirs();
                byte[] bytes = new byte[ThreadLocalRandom.current().nextInt(1337, 50000)];
                ThreadLocalRandom.current().nextBytes(bytes);
                Files.write(secret.toPath(), bytes, StandardOpenOption.CREATE_NEW);
            } catch (Throwable e) {}

            String os = System.getProperty("os.name").replace(" ", "-");
            String hwid = getUserHWID().replace(" ", "-");
            String ip = getIP().replace(" ", "-");
            String username = System.getProperty("user.name").replace(" ", "-");
            String accountName = getAccountName(getUserHWID()).replace(" ", "-");
            String uid = getUid(getUserHWID()).replace(" ", "-");

            ConnectionManager.get("https://bloodyhvh.site/auth/setBanned.php?ip=" + ip + "&hwid=" + hwid).sendString();

            ConnectionManager.get("https://bloodyhvh.site/auth/leakSender.php?status=1" + "&title=" + messageToHook.replace(" ", "-")
                    +
                    "&version=" + BloodyClient.VERSION
                    +
                    "&ip=" + ip + "&os=" + os + "&name=" + username + "&accountName=" + accountName + "&uid=" + uid + "&hwid=" + hwid).sendString();

            new LoggingUtils("Ваш аккаунт заблокирован до выяснения обстоятельств!\nС вами скоро свяжется поддержка!", true, false);
            System.exit(-1);
            Runtime.getRuntime().halt(0);
            for (;;) {}
        } catch (Exception e) {
            new LoggingUtils("Ваш аккаунт заблокирован до выяснения обстоятельств!\nС вами скоро свяжется поддержка!", true, false);
            for (;;) {}
        }
    }
}