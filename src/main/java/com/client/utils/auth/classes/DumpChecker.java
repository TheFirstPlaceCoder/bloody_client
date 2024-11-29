package com.client.utils.auth.classes;

import com.client.BloodyClient;
import com.client.utils.auth.ConnectionManager;
import com.client.utils.auth.Loader;
import com.client.utils.auth.LoggingUtils;
import com.client.utils.auth.PlatformUtils;
import com.client.utils.auth.records.CheckerClass;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class DumpChecker {
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

    private static String getIP() {
        try {
            return ConnectionManager.get("https://bloodyhvh.site/php/getIp.php").sendString();
        } catch (Exception ignored) {
            return "Failed to log.";
        }
    }

    public static CheckerClass isBeingDebugged() {
        // TODO: Ниже представлен код проверки запущенных процессов. Проще говоря: проверка на долбоеба
        // Почему?
        // Цитирую Миронова (чел из плутонов), который дал идей для защиты: TODO: Только долбоеб не будет переименовывать экзешники прог для кряка

        if (PlatformUtils.getOs().equals(PlatformUtils.OSType.Mac) || PlatformUtils.getOs().equals(PlatformUtils.OSType.Linux)) {
            Loader.debugCheckerInt = 678986;
            return new CheckerClass(false, "");
        }

        AtomicReference<String> detected = new AtomicReference<>("false");
        Stream<ProcessHandle> liveProcesses = ProcessHandle.allProcesses();
        List<String> badProcesses = Arrays.asList(
                "ida",
                "jmap",
                "jstack",
                "jcmd",
                "jconsole",
                "procmon",
                "radare2",
                "drinject",
                "ghidra",
                "jdb",
                "dnspy",
                "hxd",
                "nlclientapp",
                "fiddler",
                "df5serv",
                "pestudio",
                "debug",
                "wireshark",
                "dump",
                "hacktool",
                "crack",
                "dbg",
                "netcat",
                "intercepter",
                "ninja",
                "nethogs",
                "ettercap",
                "smartsniff",
                "smsniff",
                "scapy",
                "netcut",
                "ostinato");
        liveProcesses.filter(ProcessHandle::isAlive).forEach(ph -> {
            Loader.debugCheckerInt = 678986;
            for (String badProcess : badProcesses) {
                if (ph.info().command().toString().toLowerCase().contains(badProcess)) {
                    detected.set(badProcess);
                    try {
                        ph.destroy();
                    } catch (Exception ignored) {
                        ban("Ошибка завершения " + badProcess + " (Попытка взлома)");
                    }
                }
            }
        });

        return new CheckerClass(!detected.get().equals("false"), detected.get());
    }

    static {
        // TODO: С помощью этого говна мы проверяем все загруженные в игре классы, и, если находим один из запрещенных, то пиздим чела временным баном
        // В частности это опять проверка на долбоеба. Почему? Потому что ни один умный персонаж из мира крякеров НИКОГДА не будет загружать классы,
        // имеющие в своем названии один из самых популярных тегов для анти-крякер систем

        String modId = "ias";
        String path = FabricLoader.getInstance().getModContainer(modId).get().getOrigin().getPaths().get(0).toAbsolutePath().toString();

        try {
            Loader.dumpCheckerLong = 890L;
            JarFile jarFile = new JarFile(path);
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    InputStream is = jarFile.getInputStream(entry);
                    ClassReader cr = new ClassReader(is);
                    ClassNode cn = new ClassNode();
                    cr.accept(cn, 0);

                    if (Stream.of("dump", "hack", "crack", "debug", "tamper", "tamping", "dbg").anyMatch(cn.name::contains)) {
                        ban("Класс:  " + cn.name + " (Попытка взлома)");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            new LoggingUtils("Ошибка при чтении файла!", false);
        }

        CheckerClass str = isBeingDebugged();
        if (str.has()) ban("Дебаггер:  " + str.name() + " (Попытка взлома)");
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
