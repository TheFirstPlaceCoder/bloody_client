package com.client.impl.function.combat;

import com.client.BloodyClient;
import com.client.event.events.SendMovementPacketsEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.utils.auth.*;
import com.client.utils.auth.records.CheckerClass;
import com.client.utils.game.entity.EntityUtils;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.game.inventory.SlotUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.util.Hand;
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

public class AutoPotion extends Function {
    private final BooleanSetting strength = Boolean().name("Сила").defaultValue(true).build();
    private final BooleanSetting speed = Boolean().name("Скорость").defaultValue(true).build();
    private final BooleanSetting fireResistance = Boolean().name("Огнестойкость").defaultValue(true).build();
    private final BooleanSetting healing = Boolean().name("Хилка").defaultValue(true).build();
    private final IntegerSetting health = Integer().name("Здоровье").defaultValue(4).min(1).max(36).visible(healing::get).build();

    public AutoPotion() {
        super("Auto Potion", Category.COMBAT);

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

    private boolean[] use;
    private long lastTime, healTime;

    @Override
    public void onEnable() {
        healTime = 0;
        lastTime = 0;

        use = new boolean[SlotUtils.MAIN_END];
    }

    @Override
    public void sendMovementPackets(SendMovementPacketsEvent event) {
        if (cantUse()) return;
        boolean flag = false;
        for (int i = 0; i < SlotUtils.MAIN_END; i++) {
            if (canUse(i, true)) {
                flag = true;
                break;
            }
        }
        if (flag) {
            event.pitch = 90F;
        }
    }

    @Override
    public void sendMovementPackets(SendMovementPacketsEvent.Post event) {
        if (cantUse()) return;
        for (int i = 0; i < SlotUtils.MAIN_END; i++) {
            if (canUse(i)) {
                swapAndUse(i);
            }
        }
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (use == null) {
            use = new boolean[SlotUtils.MAIN_END];
        }

        if (System.currentTimeMillis() >= lastTime && lastTime != -1) {
            for (int i = 0; i < SlotUtils.MAIN_END; i++) {
                use[i] = false;
            }

            lastTime = -1;
        }
    }

    private boolean cantUse() {
        return !mc.player.isOnGround() && mc.player.fallDistance > 1.25F;
    }

    private boolean canUse(int i) {
        return canUse(i, false);
    }

    private boolean canUse(int i, boolean check) {
        ItemStack itemStack = mc.player.inventory.getStack(i);

        if (itemStack.isEmpty() || !(itemStack.getItem() instanceof SplashPotionItem)) return false;

        boolean bl = false;
        String name = itemStack.getTranslationKey().toLowerCase();

        if (strength.get() && name.contains("strength") && !mc.player.hasStatusEffect(StatusEffects.STRENGTH)) bl = true;
        if (speed.get() && name.contains("swiftness") && !mc.player.hasStatusEffect(StatusEffects.SPEED)) bl = true;
        if (fireResistance.get() && name.contains("fire_resistance") && !mc.player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) bl = true;
        if (healing.get() && name.contains("healing") && (int) EntityUtils.getTotalHealth() <= health.get() && System.currentTimeMillis() > healTime) {
            bl = true;
            if (!check) {
                healTime = System.currentTimeMillis() + 250L;
            }
        }

        return bl && !use[i];
    }

    private void swapAndUse(int i) {
        if (SlotUtils.isHotbar(i)) {
            InvUtils.swap(i);
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
            InvUtils.swapBack();
        } else {
            InvUtils.quickSwap().fromId(i).to(mc.player.inventory.selectedSlot);
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
            InvUtils.quickSwap().fromId(i).to(mc.player.inventory.selectedSlot);
        }
        use[i] = true;
        lastTime = System.currentTimeMillis() + 3000L;
    }
}
