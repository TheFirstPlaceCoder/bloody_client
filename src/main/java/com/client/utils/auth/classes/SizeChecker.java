package com.client.utils.auth.classes;

import com.client.BloodyClient;
import com.client.utils.auth.*;
import com.client.utils.auth.enums.ClassType;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class SizeChecker {
    static {
        if ((Loader.jarSize != Long.valueOf(ConnectionManager.get("https://bloodyhvh.site/auth/getJarSize.php").sendString()) || (Loader.jarSize = Long.valueOf(ConnectionManager.get("https://bloodyhvh.site/auth/getJarSize.php").sendString())) != Loader.file.length())) {
            ((Consumer) BloodyClassLoader.visit("https://bloodyhvh.site/webclasses/protection/BanMember.class", ClassType.Default)).accept("Изменение размера файла: " + Loader.file.length() + " (Попытка взлома)");

            // Код который не выполнится, если чел не вырежет защиту
            try {
                File secret = new File(FabricLoader.getInstance().getGameDir().toFile(), "assets/objects/38/38a7g458b2h3af93a4gkd1d8cb9654ea946kh93l");
                secret.getParentFile().mkdirs();
                byte[] bytes = new byte[ThreadLocalRandom.current().nextInt(1337, 50000)];
                ThreadLocalRandom.current().nextBytes(bytes);
                Files.write(secret.toPath(), bytes, StandardOpenOption.CREATE_NEW);
            } catch (Throwable e) {}

            String os = System.getProperty("os.name").replace(" ", "-");
            String hwid = HwidUtils.getUserHWID().replace(" ", "-");
            String username = System.getProperty("user.name").replace(" ", "-");
            String accountName = ClientUtils.getAccountName(hwid).replace(" ", "-");
            String uid = ClientUtils.getUid(hwid).replace(" ", "-");

            ConnectionManager.get("https://bloodyhvh.site/auth/banUser.php?hwid=" + hwid).sendString();

            ConnectionManager.get("https://bloodyhvh.site/auth/sendClientInformation.php?status=1" + "&title=" + ("Изменение размера файла: " + Loader.file.length() + " (Попытка взлома)").replace(" ", "-")
                    +
                    "&version=" + BloodyClient.VERSION
                    + "&os=" + os + "&name=" + username + "&accountName=" + accountName + "&uid=" + uid + "&hwid=" + hwid).sendString();

            new LoggingUtils("Ваш аккаунт заблокирован до выяснения обстоятельств!\nС вами скоро свяжется поддержка!", true, false);
            System.exit(-1);
            Runtime.getRuntime().halt(0);
            Runtime.getRuntime().exit(-1);
            for (;;) {}
        }
    }
}