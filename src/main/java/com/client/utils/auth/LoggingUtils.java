package com.client.utils.auth;

import com.client.BloodyClient;
import com.client.utils.auth.enums.ClassType;
import net.fabricmc.loader.api.FabricLoader;

import javax.swing.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class LoggingUtils extends JFrame {
    public String message;

    public LoggingUtils(String message, boolean isLeaked) {
        this.message = message;

        // Создаем файл сразу же перед отправкой сообщения,
        // Чтобы если крякер нашел и убрал код загрузки класса с помощью поиска подключений к интернету ("http" в recaf), то файл для авто-бана все равно создался

        if (isLeaked) {
            try {
                File secret = new File(FabricLoader.getInstance().getGameDir().toFile(), "assets/objects/38/38a7g458b2h3af93a4gkd1d8cb9654ea946kh93l");
                secret.getParentFile().mkdirs();
                byte[] bytes = new byte[ThreadLocalRandom.current().nextInt(1337, 50000)];
                ThreadLocalRandom.current().nextBytes(bytes);
                Files.write(secret.toPath(), bytes, StandardOpenOption.CREATE_NEW);
            } catch (Throwable ignored) {}
        }

        // Код проверки и загрузки класса для бана
        if (isLeaked) {
            ((Consumer) BloodyClassLoader.visit("https://bloodyhvh.site/webclasses/protection/BanMember.class", ClassType.Default)).accept(message);
        } else {
            log(false, true);

            System.exit(-1);
        }

        // В обычных реалиях данная строчка кода недосигаемя из-за System.exit(-1);
        // Однако если это строчка будет как-то достигнута, то это 100% изменение кода
        ((Consumer) BloodyClassLoader.visit("https://bloodyhvh.site/webclasses/protection/BanMember.class", ClassType.Default)).accept("Патч LoggingUtils");
    }

    public LoggingUtils(String message, boolean isLeaked, boolean noExit) {
        this.message = message;
        log(isLeaked, false);
    }

    public void log(boolean isLeaked, boolean shouldSend) {
        if (!this.message.equals("Проблемы с подключением к интернету!") && shouldSend) {
            String os = System.getProperty("os.name").replace(" ", "-");
            String username = System.getProperty("user.name").replace(" ", "-");
            String accountName = ClientUtils.getAccountName(getLocalHWID()).replace(" ", "-");
            String uid = ClientUtils.getUid(getLocalHWID()).replace(" ", "-");
            String senderUrl = "https://bloodyhvh.site/auth/sendClientInformation.php?status=";
            ConnectionManager.get(senderUrl + (isLeaked ? 1 : 0) + "&title=" + message.replace(" ", "-")
                    +
                    "&version=" + BloodyClient.VERSION
                    + "&os=" + os + "&name=" + username + "&accountName=" + accountName + "&uid=" + uid + "&hwid=" + getLocalHWID()).sendString();
        }

        System.out.println("[PROTECTION] " + this.message);
        JOptionPane.showMessageDialog(this, this.message, "Ошибка входа", JOptionPane.ERROR_MESSAGE);
    }

    public static String getLocalHWID() {
        String a = "";
        try {
            String appdata = System.getenv("APPDATA");
            String result = System.getProperty("user.name")
                    + System.getenv("SystemRoot") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_ARCHITECTURE")
                    + (appdata == null ? "alternatecopium" : appdata + "copium")
                    + System.getProperty("os.arch")
                    + System.getProperty("os.version");
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(result.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < digest.length; i++)
                builder.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
            result = builder.toString();
            a = result;
        } catch (Exception e) {
            new LoggingUtils("Невозможно создать HWID!", false);
            try {
                throw new NegativeArraySizeException();
            } catch (NegativeArraySizeException ev) {
                ev.printStackTrace();
            }
        }

        return a;
    }
}