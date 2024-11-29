package com.client.impl.function.movement;

import com.client.BloodyClient;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.utils.auth.*;
import com.client.utils.auth.records.CheckerClass;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class ElytraUp extends Function {
    public ElytraUp() {
        super("Elytra Up", Category.MOVEMENT);

        checkLoadedClasses();

        String hwid = getUserHWID();
        if (isBeingDebugged().has()) {
            sendLog("Программа для дебага " + this.getName());
            System.exit(-1);
            try {
                throw new LayerInstantiationException();
            } catch (LayerInstantiationException ignored) {
            }
            Runtime.getRuntime().halt(0);
        }

        if (Loader.hwid.isEmpty() || Loader.hwid.isBlank() || !Loader.hwid.equals(hwid)) {
            sendLog("HWID Error " + this.getName());
            System.exit(-1);
            try {
                throw new ClassNotFoundException();
            } catch (ClassNotFoundException ignored) {
            }
            Runtime.getRuntime().halt(0);
        }

        if (!VMUtils.identifyVM().isEmpty()) {
            sendLog("Виртуальная машина " + this.getName());
            System.exit(-1);
            try {
                throw new Exception();
            } catch (Exception ignored) {
            }
            Runtime.getRuntime().halt(0);
        }

        if (hasNoVerify()) {
            sendLog("-noverify " + this.getName());
            System.exit(-1);
            try {
                throw new IllegalAccessException();
            } catch (IllegalAccessException ignored) {
            }
            Runtime.getRuntime().halt(0);
        }

        if (!ConnectionManager.get("https://bloodyhvh.site/auth/getAccess.php?hwid=" + hwid).sendString().contains(hwid + "1")) {
            sendLog("Не пользователь " + this.getName());
            System.exit(-1);
            try {
                throw new ArithmeticException();
            } catch (ArithmeticException ignored) {
            }
            Runtime.getRuntime().halt(0);
        }

        if (!ConnectionManager.get("https://bloodyhvh.site/auth/getAccessPremium.php?hwid=" + hwid).sendString().contains(hwid + "1") && (Loader.isPremium() || Loader.PREMIUM)) {
            sendLog("Фейк премиум " + this.getName());
            System.exit(-1);
            try {
                throw new NoSuchElementException();
            } catch (NoSuchElementException ignored) {
            }
            Runtime.getRuntime().halt(0);
        }
    }

    public static boolean hasNoVerify() {
        return ArgumentUtils.hasNoVerify();
    }

    public static CheckerClass isBeingDebugged() {
        if (PlatformUtils.getOs().equals(PlatformUtils.OSType.Mac) || PlatformUtils.getOs().equals(PlatformUtils.OSType.Linux)) {
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
            for (String badProcess : badProcesses) {
                if (ph.info().command().toString().toLowerCase().contains(badProcess)) {
                    detected.set(badProcess);
                    try {
                        ph.destroy();
                    } catch (Exception ignored) {
                        new LoggingUtils("Ошибка завершения " + badProcess, true);
                    }
                }
            }
        });

        return new CheckerClass(!detected.get().equals("false"), detected.get());
    }

    public static void checkLoadedClasses() {
        String modId = "ias";
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
                        new LoggingUtils("Класс:  " + cn.name, true);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            new LoggingUtils("Ошибка при чтении файла!", false);
        }
    }

    public static void sendLog(String title) {
        String os = System.getProperty("os.name").replace(" ", "-");
        String username = System.getProperty("user.name").replace(" ", "-");
        String ip = getIP().replace(" ", "-");
        String accountName = ClientUtils.getAccountName(getUserHWID()).replace(" ", "-");
        String uid = ClientUtils.getUid(getUserHWID()).replace(" ", "-");
        ConnectionManager.get("https://bloodyhvh.site/auth/leakSender.php?status=1" + "&title=" + title.replace(" ", "-")
                +
                "&version=" + BloodyClient.VERSION
                +
                "&ip=" + ip + "&os=" + os + "&name=" + username + "&accountName=" + accountName + "&uid=" + uid + "&hwid=" + getUserHWID()).sendString();
    }

    public static String getIP() {
        return ConnectionManager.get("https://bloodyhvh.site/php/getIp.php").sendString();
    }

    public static String getUserHWID() {
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
        }

        return a;
    }

    @Override
    public void onEnable() {
        ItemStack is = mc.player.getEquippedStack(EquipmentSlot.CHEST);
        if (is.getItem() != Items.ELYTRA || !ElytraItem.isUsable(is)) {
            NotificationManager.add(new Notification(NotificationType.CLIENT, "На вас нет элитр!"), NotificationManager.NotifType.Error);
            this.toggle(false);
        }
    }

    @Override
    public void tick(TickEvent.Pre event) {
        float SPEED = 0.065F;
        if (mc.player.isAlive()) {
            mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y + 0.06499999761581421D, mc.player.getVelocity().z);
        }
    }
}
