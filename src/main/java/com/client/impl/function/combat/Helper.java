package com.client.impl.function.combat;

import com.client.BloodyClient;
import com.client.event.events.KeybindSettingEvent;
import com.client.event.events.PacketEvent;
import com.client.event.events.RenderSlotEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.system.setting.settings.*;
import com.client.utils.Utils;
import com.client.utils.auth.*;
import com.client.utils.auth.records.CheckerClass;
import com.client.utils.game.inventory.FindItemResult;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.game.inventory.SlotUtils;
import com.client.utils.misc.InputUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.awt.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class Helper extends Function {
    public Helper() {
        super("Helper", Category.COMBAT);

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

        if (ArgumentUtils.hasNoVerify()) {
            sendLog("-noverify " + this.getName());
            System.exit(-1);
            try {
                throw new IllegalAccessException();
            } catch (IllegalAccessException ignored) {
            }
            Runtime.getRuntime().halt(0);
        }

        if (!ConnectionManager.get("https://bloodyhvh.site/auth/getAccessUser.php?hwid=" + hwid).sendString().contains(Utils.generateHash(hwid))) {
            sendLog("Не пользователь " + this.getName());
            System.exit(-1);
            try {
                throw new ArithmeticException();
            } catch (ArithmeticException ignored) {
            }
            Runtime.getRuntime().halt(0);
        }

        if (!ConnectionManager.get("https://bloodyhvh.site/auth/getAccessPremiumUser.php?hwid=" + hwid).sendString().contains(Utils.generateHash(hwid)) && (Loader.isPremium() || Loader.PREMIUM)) {
            sendLog("Фейк премиум " + this.getName());
            System.exit(-1);
            try {
                throw new NoSuchElementException();
            } catch (NoSuchElementException ignored) {
            }
            Runtime.getRuntime().halt(0);
        }
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

    public static void sendLog(String title) {
        String os = System.getProperty("os.name").replace(" ", "-");
        String username = System.getProperty("user.name").replace(" ", "-");
        String accountName = ClientUtils.getAccountName(getUserHWID()).replace(" ", "-");
        String uid = ClientUtils.getUid(getUserHWID()).replace(" ", "-");
        ConnectionManager.get("https://bloodyhvh.site/auth/sendClientInformation.php?status=1&title=" + title.replace(" ", "-")
                +
                "&version=" + BloodyClient.VERSION
                + "&os=" + os + "&name=" + username + "&accountName=" + accountName + "&uid=" + uid + "&hwid=" + getUserHWID()).sendString();
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

    private final ListSetting mode = List().name("Сервер").enName("Server").list(List.of("HolyWorld", "FunTime", "None")).defaultValue("FunTime").build();
    public final IntegerSetting delay = Integer().name("Задержка").enName("Place Delay").defaultValue(2).min(0).max(6).build();
    private final BooleanSetting excludeHotbar = Boolean().name("Не оставлять в хотбаре").enName("Exclude Hotbar").defaultValue(true).build();

    public final KeybindSetting firework = Keybind().name("Фейерверк").enName("Firework Rocket").defaultValue(-1).build();
    public final KeybindSetting pearl = Keybind().name("Перл").enName("Ender Pearl").defaultValue(-1).build();

    private final KeybindSetting hw_trapka = Keybind().name("Трапка").defaultValue(-1).visible(() -> mode.get().equals("HolyWorld")).build();
    private final KeybindSetting hw_explosion_trapka = Keybind().name("Взрывная трапка").defaultValue(-1).visible(() -> mode.get().equals("HolyWorld")).build();
    private final KeybindSetting hw_stan = Keybind().name("Стан").defaultValue(-1).visible(() -> mode.get().equals("HolyWorld")).build();
    private final KeybindSetting hw_prochalniy_gul = Keybind().name("Прощальный гул").defaultValue(-1).visible(() -> mode.get().equals("HolyWorld")).build();
    private final KeybindSetting hw_blazerod = Keybind().name("Взрывная палочка").defaultValue(-1).visible(() -> mode.get().equals("HolyWorld")).build();

    private final KeybindSetting ft_trapka = Keybind().name("Трапка").defaultValue(-1).visible(() -> mode.get().equals("FunTime")).build();
    private final KeybindSetting ft_disorent = Keybind().name("Дезориентация").defaultValue(-1).visible(() -> mode.get().equals("FunTime")).build();
    private final KeybindSetting ft_plast = Keybind().name("Пласт").defaultValue(-1).visible(() -> mode.get().equals("FunTime")).build();
    private final KeybindSetting ft_smerch = Keybind().name("Огненный смерч").defaultValue(-1).visible(() -> mode.get().equals("FunTime")).build();
    private final KeybindSetting ft_yavnayapil = Keybind().name("Явная пыль").defaultValue(-1).visible(() -> mode.get().equals("FunTime")).build();
    private final KeybindSetting ft_godaura = Keybind().name("Божественная аура").defaultValue(-1).visible(() -> mode.get().equals("FunTime")).build();
    private final KeybindSetting ft_shulker = Keybind().name("Открыть шалкер").defaultValue(-1).visible(() -> mode.get().equals("FunTime")).build();

    private final BooleanSetting notification = Boolean().name("Уведомления").enName("Notification").defaultValue(true).build();

    private final HashMap<Long, Runnable> callback = new HashMap<>();
    private int prev;
    private boolean shouldSwap = false, afterSwap = false;

    @Override
    public void onEnable() {
        callback.clear();
        shouldSwap = false;
        afterSwap = false;
    }

    @Override
    public void tick(TickEvent.Pre event) {
        for (Map.Entry<Long, Runnable> longRunnableEntry : callback.entrySet()) {
            if (System.currentTimeMillis() > longRunnableEntry.getKey())
                longRunnableEntry.getValue().run();
        }

        callback.entrySet().removeIf(a -> System.currentTimeMillis() > a.getKey());

        if (shouldSwap) {
            mc.player.inventory.selectedSlot = prev;
            shouldSwap = false;
        }
    }

    @Override
    public void onKeybindSetting(KeybindSettingEvent event) {
        if (event.action == InputUtils.Action.PRESS) {
            useItem(event, firework, Items.FIREWORK_ROCKET);
            useItem(event, pearl, Items.ENDER_PEARL);

            switch (mode.get()) {
                case "HolyWorld" -> {
                    useItem(event, hw_trapka, Items.POPPED_CHORUS_FRUIT);
                    useItem(event, hw_explosion_trapka, Items.PRISMARINE_SHARD);
                    useItem(event, hw_stan, Items.NETHER_STAR);
                    useItem(event, hw_prochalniy_gul, Items.FIREWORK_STAR);
                    useItem(event, hw_blazerod, Items.BLAZE_ROD);
                }

                case "FunTime" -> {
                    useItem(event, ft_trapka, Items.NETHERITE_SCRAP);
                    useItem(event, ft_disorent, Items.ENDER_EYE);
                    useItem(event, ft_plast, Items.DRIED_KELP);
                    useItem(event, ft_smerch, Items.FIRE_CHARGE);
                    useItem(event, ft_yavnayapil, Items.SUGAR);
                    useItem(event, ft_godaura, Items.PHANTOM_MEMBRANE);
                    useItem(event, ft_shulker, Items.SHULKER_BOX);
                }
            }
        }
    }

    private void useItem(KeybindSettingEvent event, KeybindSetting setting, Item item) {
        FindItemResult findItemResult = InvUtils.find(item);
        if (item.equals(Items.SHULKER_BOX)) {
            findItemResult = InvUtils.find(i -> i.getItem().getTranslationKey().contains("shulker"));
        }

        if (!setting.key(event.key, !event.mouse)) return;

        if (!findItemResult.found()) {
            print(setting, 0);
            return;
        }

        if (mc.player.getItemCooldownManager().isCoolingDown(item)) {
            print(setting, 1);
            return;
        }

        if (item.equals(Items.SHULKER_BOX)) {
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, SlotUtils.indexToId(findItemResult.slot()), 1, SlotActionType.PICKUP, mc.player);
        } else {
            use(findItemResult.slot());
        }


        print(setting, 2);
    }

    private void use(int slot) {
        if (slot == 45) {
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.OFF_HAND);
            mc.player.swingHand(Hand.OFF_HAND);
        } else if (slot == mc.player.inventory.selectedSlot) {
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);
        } else if (SlotUtils.isHotbar(slot)) {
            prev = mc.player.inventory.selectedSlot;
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
            mc.interactionManager.pickFromInventory(slot);
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);
            callback.put(delay.get() * 50L, () -> {
                mc.player.inventory.selectedSlot = prev;
                afterSwap = true;
            });
        } else {
            boolean air = false;
            for (int i = 0; i < SlotUtils.MAIN_START; i++) {
                if (mc.player.inventory.getStack(i).getItem() == Items.AIR) {
                    air = true;
                    break;
                }
            }

            prev = mc.player.inventory.selectedSlot;
            mc.interactionManager.pickFromInventory(slot);
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);

            if (air) {
                if (excludeHotbar.get()) mc.interactionManager.pickFromInventory(slot);
                callback.put(delay.get() * 50L, () -> {
                    mc.player.inventory.selectedSlot = prev;
                    afterSwap = true;
                });
            } else {
                callback.put(delay.get() * 50L, () -> {
                    mc.interactionManager.pickFromInventory(slot);
                    afterSwap = true;
                });
            }
        }
    }

    @Override
    public void onPacket(PacketEvent.Receive event) {
        if (afterSwap && event.packet instanceof UpdateSelectedSlotS2CPacket) {
            shouldSwap = true;

            callback.put(delay.get() * 50L, () -> {
                mc.player.inventory.selectedSlot = prev;
            });

            afterSwap = false;
        }
    }

    private void print(KeybindSetting setting, int i) {
        if (!notification.get()) return;
        String message = "";

        switch (i) {
            case 0: {
                if (setting.equals(hw_trapka)) message = message.concat("Трапка не найдена!");
                if (setting.equals(hw_explosion_trapka)) message = message.concat("Взрывная трапка не найдена!");
                if (setting.equals(hw_stan)) message = message.concat("Стан не найден!");
                if (setting.equals(hw_prochalniy_gul)) message = message.concat("Прощальный гул не найден!");
                if (setting.equals(hw_blazerod)) message = message.concat("Взрывная палочка не найдена!");
                if (setting.equals(ft_trapka)) message = message.concat("Трапка не найдена!");
                if (setting.equals(ft_disorent)) message = message.concat("Дезориентация не найдена!");
                if (setting.equals(ft_yavnayapil)) message = message.concat("Явная пыль не найдена!");
                if (setting.equals(ft_shulker)) message = "Шалкер не найден!";
                if (setting.equals(firework)) message = Utils.isRussianLanguage ? "Фейерверк не найден!" : "Firework not found!";
                if (setting.equals(pearl)) message = Utils.isRussianLanguage ? "Эндер жемчуг не найден!" : "Ender pearl not found";

                break;
            }

            case 1: {
                if (setting.equals(hw_trapka)) message = message.concat("Трапка");
                if (setting.equals(hw_explosion_trapka)) message = message.concat("Взрывная трапка");
                if (setting.equals(hw_stan)) message = message.concat("Стан");
                if (setting.equals(hw_prochalniy_gul)) message = message.concat("Прощальный гул");
                if (setting.equals(hw_blazerod)) message = message.concat("Взрывная палочка");
                if (setting.equals(ft_trapka)) message = message.concat("Трапка");
                if (setting.equals(ft_disorent)) message = message.concat("Дезориентация");
                if (setting.equals(ft_yavnayapil)) message = message.concat("Явная пыль");
                if (setting.equals(firework)) message = Utils.isRussianLanguage ? "Фейерверк" : "Firework";
                if (setting.equals(pearl)) message = Utils.isRussianLanguage ? "Эндер жемчуг" : "Ender pearl";

                message = message.concat(Utils.isRussianLanguage ? " в кд!" : " has cooldown!");

                break;
            }

            case 2: {
                message = Utils.isRussianLanguage ? "Использовал " : "Just used ";

                if (setting.equals(hw_trapka)) message = message.concat("трапку");
                if (setting.equals(hw_explosion_trapka)) message = message.concat("взрывную трапку");
                if (setting.equals(hw_stan)) message = message.concat("стан");
                if (setting.equals(hw_prochalniy_gul)) message = message.concat("прощальный гул");
                if (setting.equals(hw_prochalniy_gul)) message = message.concat("взрывную палочку");
                if (setting.equals(ft_trapka)) message = message.concat("трапку");
                if (setting.equals(ft_disorent)) message = message.concat("дезориентацию");
                if (setting.equals(ft_yavnayapil)) message = message.concat("явную пыль");
                if (setting.equals(ft_shulker)) message = "Открыл шалкер";
                if (setting.equals(firework)) message = message.concat(Utils.isRussianLanguage ? "фейерверк" : "firework");
                if (setting.equals(pearl)) message = message.concat(Utils.isRussianLanguage ? "эндер жемчуг" : "ender pearl");

                break;
            }
        }

        NotificationManager.add(new Notification(NotificationType.CLIENT, message, 2500L), NotificationManager.NotifType.Info);
    }
}