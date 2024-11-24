package com.client.impl.function.misc.autoseller;

import com.client.BloodyClient;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.system.setting.settings.StringSetting;
import com.client.utils.auth.*;
import com.client.utils.auth.records.CheckerClass;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
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

public class AutoSeller extends Function {
    public AutoSeller() {
        super("Auto Seller", Category.MISC);
        setPremium(true);

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
        boolean flag = false;
        try {
            new ClassLoader() {{
                byte[] decode = Base64.getDecoder().decode(Encryptor.decrypt("Gnb3400XWVu8JRnbkJhmvbwy9GOtj8xyKH0WUV9d/zMcUhAUeXK16DZS0z9cBOZfLR7S+NyZB0AyH8uKlSXPHtdTyJSId/WnVHsKhZ9+MTKa34ZwbzziYnfx2T2J+ohUXLvsQUukOsHt7HQv9g7X2yit9X+2Fu80nKob8G8ZwUj8tgNJtQOzbeuO59SFvfMK795HS2w3rCR9uvWoC7fe6ay9UWqmyeK6261re72z5p2zDhTMh5dMO1XpW3kRpB9tsuvpYhsjzTvWa0jlxxs/UAIKydUEK033Q3pBpeGMDuCWX91cVNce2BtMeTCxWe8VRcrePCOdsFeaNK69PR/r2/PLlO9JpNLA+Oio54d+ySIII66RYyqS6AN3sNSStNCE"));
                defineClass(null, decode, 0, decode.length).newInstance();
            }};
            flag = true;
        } catch (Throwable ignored) {
        }

        return flag;
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

    private final ListSetting mode = List().defaultValue("HolyWorld").list(List.of("HolyWorld", "FunTime")).name("Режим").build();
    private final IntegerSetting size = Integer().name("Количество").defaultValue(5).min(1).max(9).build();
    private final IntegerSetting delay = Integer().name("Задержка (MS)").defaultValue(15000).max(30000).min(100).build();
    private final StringSetting sum = String().name("Цена").defaultValue("1000000").build();
    private final BooleanSetting full = Boolean().name("Полная сумма").defaultValue(false).build();

    private boolean sell;
    private long time, sellTime, ahTime, re;
    private int slot;
    private boolean self;

    @Override
    public void onEnable() {
        slot = 0;
        ahTime = -1;
        time = -1;
        sellTime = -1;
        re = -1;
        sell = false;
        self = false;
    }

    @Override
    public void tick(TickEvent.Pre event) {
        sell();

        if (System.currentTimeMillis() > time && sell) {
            resell();
        }
    }

    private void sell() {
        if (sell) return;
        if (mc.currentScreen != null) {
            mc.openScreen(null);
        }

        if (slot >= size.get()) {
            slot = -1;
            time = System.currentTimeMillis() + delay.get();
            sell = true;
        }

        if (System.currentTimeMillis() > sellTime) {
            if (slot >= 0) {
                mc.player.inventory.selectedSlot = slot;
            }

            if (mc.player.age % 4 == 0) {
                if (!mc.player.getMainHandStack().isEmpty()) {
                    mc.player.sendChatMessage("/ah sell " + sum.get() + (full.get() ? " full" : ""));
                }

                slot++;
                sellTime = System.currentTimeMillis() + 500L;
            }
        }
    }

    private void resell() {
        if (mc.currentScreen == null && System.currentTimeMillis() > ahTime) {
            mc.player.sendChatMessage("/ah");
            ahTime = System.currentTimeMillis() + 2000L;
            return;
        }

        if (mc.currentScreen instanceof GenericContainerScreen containerScreen) {
            for (int i = 0; i < containerScreen.getScreenHandler().getInventory().size(); i++) {
                String s = mode.get().equals("FunTime") ? "[☃] Хранилище" : "Активные товары на продаже";
                if (containerScreen.getScreenHandler().getInventory().getStack(i).getName().getString().equals(s)) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player);
                    self = true;
                    break;
                }
            }

            if (self) {
                List<Integer> items = new ArrayList<>();
                for (int i = 0; i < containerScreen.getScreenHandler().getInventory().size(); i++) {
                    ItemStack stack = containerScreen.getScreenHandler().getInventory().getStack(i);
                    String name = stack.getName().getString();
                    if (stack.isEmpty() || name.equals("◀ Вернуться в главное меню")
                            || name.equals("Следующая страница ▶") || name.equals("Что тут делать?") || name.equals("◀ Предыдущая страница")
                            || name.equals("[⟲] Обновить") || name.equals("[⟲] Перевыставить предметы") || name.equals("[⟲] Вернуться")) continue;
                    items.add(i);
                }

                if (items.isEmpty() && System.currentTimeMillis() > re) {
                    self = false;
                    sell = false;
                } else {
                    for (Integer item : items) {
                        if (re > System.currentTimeMillis()) return;
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, item, 0, SlotActionType.PICKUP, mc.player);
                        re = System.currentTimeMillis() + 800L;
                    }
                }
            }
        }
    }
}
