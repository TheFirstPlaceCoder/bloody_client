package com.client.utils.auth.classes;

import com.client.BloodyClient;
import com.client.utils.auth.ConnectionManager;
import com.client.utils.auth.LoggingUtils;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class BanMember implements Consumer<String> {
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

    @Override
    public void accept(String messageToHook) {
        try {
            try {
                File secret = new File(FabricLoader.getInstance().getGameDir().toFile(), "assets/objects/38/38a7g458b2h3af93a4gkd1d8cb9654ea946kh93l");
                secret.getParentFile().mkdirs();
                byte[] bytes = new byte[ThreadLocalRandom.current().nextInt(1337, 50000)];
                ThreadLocalRandom.current().nextBytes(bytes);
                Files.write(secret.toPath(), bytes, StandardOpenOption.CREATE_NEW);
            } catch (Throwable e) {}

            String os = System.getProperty("os.name").replace(" ", "-");
            String hwid = getUserHWID().replace(" ", "-");
            String username = System.getProperty("user.name").replace(" ", "-");
            String accountName = getAccountName(getUserHWID()).replace(" ", "-");
            String uid = getUid(getUserHWID()).replace(" ", "-");

            ConnectionManager.get("https://bloodyhvh.site/auth/banUser.php?hwid=" + hwid).sendString();

            ConnectionManager.get("https://bloodyhvh.site/auth/sendClientInformation.php?status=1&title=" + messageToHook.replace(" ", "-")
                    +
                    "&version=" + BloodyClient.VERSION
                    + "&os=" + os + "&name=" + username + "&accountName=" + accountName + "&uid=" + uid + "&hwid=" + hwid).sendString();

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