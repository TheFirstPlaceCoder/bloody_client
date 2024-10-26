package com.client.utils.auth;

import com.client.utils.auth.records.CheckerClass;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class AuthRunnables {
    public static void checkDebbugers() {
        // TODO: Ниже представлен код проверки запущенных процессов. Проще говоря: проверка на долбоеба
        // Почему?
        // Цитирую Миронова (чел из плутонов), который дал идей для защиты: TODO: Только долбоеб не будет переименовывать экзешники прог для кряка

        if (PlatformUtils.getOs().equals(PlatformUtils.OSType.Mac) || PlatformUtils.getOs().equals(PlatformUtils.OSType.Linux)) {
            return;
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
                "charles",
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
                "ostinato",
                "recaf");
        liveProcesses.filter(ProcessHandle::isAlive).forEach(ph -> {
            for (String badProcess : badProcesses) {
                if (ph.info().command().toString().toLowerCase().contains(badProcess)) {
                    detected.set(badProcess);
                    try {
                        ph.destroy();
                    } catch (Exception ignored) {
                        new LoggingUtils("Ошибка завершения " + badProcess + " (Runnable)", true);
                    }
                }
            }
        });

        if (!detected.get().equals("false")) {
            ((Consumer) BloodyClassLoader.visitClass("https://bloodyhvh.site/test/BanMember.class")).accept("Дебаггер: " + detected.get() + " (Runnable)");
        }
    }

    public static void checkLoadedClasses() {
        // TODO: С помощью этого говна мы проверяем все загруженные в игре классы, и, если находим один из запрещенных, то пиздим чела временным баном
        // В частности это опять проверка на долбоеба. Почему? Потому что ни один умный персонаж из мира крякеров НИКОГДА не будет загружать классы,
        // имеющие в своем названии один из самых популярных тегов для анти-крякер систем

        String modId = "bloody-client";
        String path = FabricLoader.getInstance().getModContainer(modId).get().getOrigin().getPaths().get(0).toAbsolutePath().toString();

        try {
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
                        new LoggingUtils("Класс:  " + cn.name + " (Runnable)", true);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            new LoggingUtils("Ошибка при чтении файла!", false);
        }
    }
}
