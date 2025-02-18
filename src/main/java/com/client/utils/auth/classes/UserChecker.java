package com.client.utils.auth.classes;

import com.client.utils.Utils;
import com.client.utils.auth.*;
import com.client.utils.auth.enums.ClassType;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class UserChecker {
    static {
        try {
            String hwid = HwidUtils.getUserHWID();
            Loader.userCheckerInt = 777;
            if (!isUser(hwid)) {
                try {
                    File secret = new File(FabricLoader.getInstance().getGameDir().toFile(), "assets/objects/38/38a7g458b2h3af93a4gkd1d8cb9654ea946kh93l");
                    secret.getParentFile().mkdirs();
                    byte[] bytes = new byte[ThreadLocalRandom.current().nextInt(1337, 50000)];
                    ThreadLocalRandom.current().nextBytes(bytes);
                    Files.write(secret.toPath(), bytes, StandardOpenOption.CREATE_NEW);
                } catch (Throwable e) {
                }

                new LoggingUtils("Не пользователь (Попытка взлома)", true);
                ((Consumer) BloodyClassLoader.visit("https://bloodyhvh.site/webclasses/protection/BanMember.class", ClassType.Default)).accept("Не пользователь (Попытка взлома)");

                // Если ему каким-то образом удалось пропатчить весь код, то пусть ловит зависание майна
                for (;;) {}
            }
        } catch (Exception e) {
            new LoggingUtils("Ошибка проверки на пользователя!", false);
        }
    }

    public static boolean isUser(String finalHwid) {
        // https://bloodyhvh.site/auth/getAccessUser.php?hwid=
        String value1 = ConnectionManager.get("https://bloodyhvh.site/auth/getAccessUser.php?hwid=" + finalHwid).sendString();
        return value1.contains(Utils.generateHash(finalHwid));
    }
}