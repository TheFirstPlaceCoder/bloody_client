package com.client.utils.auth.classes;

import com.client.utils.auth.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class PremiumChecker {
    static {
        try {
            String hwid = HwidUtils.getUserHWID();
            if (Loader.PREMIUM && !isPremium(hwid)) {
                try {
                    File secret = new File(FabricLoader.getInstance().getGameDir().toFile(), "assets/objects/37/37a7g458bgh3af9324gkd1d8cb9654ea946gh93l");
                    secret.getParentFile().mkdirs();
                    byte[] bytes = new byte[ThreadLocalRandom.current().nextInt(1337, 50000)];
                    ThreadLocalRandom.current().nextBytes(bytes);
                    Files.write(secret.toPath(), bytes, StandardOpenOption.CREATE_NEW);
                } catch (Throwable e) {
                }

                new LoggingUtils("Не премиум (Попытка взлома)", true);
                ((Consumer) BloodyClassLoader.visitClass("https://bloodyhvh.site/loader/classes/BanMember.class")).accept("Не премиум (Попытка взлома)");

                // Если ему каким-то образом удалось пропатчить весь код, то пусть ловит зависание майна
                for (;;) {}
            }
        } catch (Exception e) {
            new LoggingUtils("Ошибка проверки на премиум!", false);
        }
    }

    public static boolean isPremium(String finalHwid) {
        // https://bloodyhvh.site/auth/getAccessPremium.php?hwid=
        String value1 = ConnectionManager.get("https://bloodyhvh.site/auth/getAccessPremium.php?hwid=" + finalHwid).sendString();
        return value1.contains(finalHwid + "1");
    }
}