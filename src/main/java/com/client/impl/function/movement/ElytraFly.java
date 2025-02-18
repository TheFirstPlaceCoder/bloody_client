package com.client.impl.function.movement;

import com.client.BloodyClient;
import com.client.event.events.KeybindSettingEvent;
import com.client.event.events.PacketEvent;
import com.client.event.events.PlayerMoveEvent;
import com.client.event.events.TickEvent;
import com.client.interfaces.IVec3d;
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
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.FireworkItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
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

public class ElytraFly extends Function {
    private final ListSetting mode = List().name("Режим").enName("Mode").list(List.of("Автоматически", "Бинды", "Control")).defaultValue("Автоматически").build();

    public final IntegerSetting minHeight = Integer().name("Высота подъема").enName("Up Y").defaultValue(100).min(0).max(360).visible(() -> mode.get().equals("Автоматически")).build();
    public final IntegerSetting maxHeight = Integer().name("Высота спуска").enName("Down Y").defaultValue(360).min(0).max(360).visible(() -> mode.get().equals("Автоматически")).build();
    public final KeybindSetting upBind = Keybind().name("Подъем").enName("Up").defaultValue(-1).visible(() -> mode.get().equals("Бинды")).build();
    public final KeybindSetting downBind = Keybind().name("Снижение").enName("Down").defaultValue(-1).visible(() -> mode.get().equals("Бинды")).build();

    public final DoubleSetting rotationSpeed = Double().name("Скорость").enName("Rotation Speed").defaultValue(4.0).min(1).max(10).visible(() -> !mode.get().equals("Control")).build();

    public final IntegerSetting horizontal_wasp = Integer().name("Горизонтальная скорость").enName("Horizontal Speed").defaultValue(17).min(1).max(17).visible(() -> mode.get().equals("Control")).build();
    public final IntegerSetting vertical_wasp = Integer().name("Вертикальная скорость").enName("Vertical Speed").defaultValue(17).min(1).max(18).visible(() -> mode.get().equals("Control")).build();
    public final IntegerSetting ti = Integer().name("Задержка фейерверка").enName("Firework Delay").defaultValue(10).min(0).max(15).visible(() -> mode.get().equals("Control")).build();
    public final IntegerSetting delay = Integer().name("Задержка свапа").enName("Swap Delay").defaultValue(2).min(0).max(6).visible(() -> mode.get().equals("Control")).build();
    private final BooleanSetting excludeHotbar = Boolean().name("Не оставлять в хотбаре").enName("Exclude Hotbar").defaultValue(true).visible(() -> mode.get().equals("Control")).build();

    public ElytraFly() {
        super("Elytra Fly", Category.MOVEMENT);

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

    private boolean pitchingDown = true, isUpPressed = false, isDownPressed = false;
    private int pitchField;

    private boolean moving, shouldSwap = false, afterSwap = false;
    private float yaw;
    private int fireworkTicks, prev;
    private final HashMap<Long, Runnable> callback = new HashMap<>();

    @Override
    public void onEnable() {
        pitchField = 40;
        isUpPressed = false;
        isDownPressed = false;
        shouldSwap = false;
        afterSwap = false;
        callback.clear();
        fireworkTicks = 10;
    }

    @Override
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (!mode.get().equals("Control") || !mc.player.isFallFlying()) return;

        updateWaspMovement();

        double cos = Math.cos(Math.toRadians(yaw + 90));
        double sin = Math.sin(Math.toRadians(yaw + 90));

        double x = moving ? cos * (horizontal_wasp.get() / 10d) : 0;
        double y = 0;
        double z = moving ? sin * (horizontal_wasp.get() / 10d) : 0;

        if (mc.options.keySneak.isPressed() && !mc.options.keyJump.isPressed()) {
            y = -(vertical_wasp.get() / 10d);
        }
        if (!mc.options.keySneak.isPressed() && mc.options.keyJump.isPressed()) {
            y = (vertical_wasp.get() / 10d);
        }

        ((IVec3d) event.movement).set(x, y, z);
    }

    private void updateWaspMovement() {
        float yaw = mc.player.yaw;

        float f = mc.player.input.movementForward;
        float s = mc.player.input.movementSideways;

        if (f > 0) {
            moving = true;
            yaw += s > 0 ? -45 : s < 0 ? 45 : 0;
        } else if (f < 0) {
            moving = true;
            yaw += s > 0 ? -135 : s < 0 ? 135 : 180;
        } else {
            moving = s != 0;
            yaw += s > 0 ? -90 : s < 0 ? 90 : 0;
        }
        this.yaw = yaw;
    }

    @Override
    public void tick(TickEvent.Pre event) {
        ItemStack is = mc.player.getEquippedStack(EquipmentSlot.CHEST);
        if (is.getItem() != Items.ELYTRA || !ElytraItem.isUsable(is)) {
            NotificationManager.add(new Notification(NotificationType.CLIENT, "На вас нет элитр!"), NotificationManager.NotifType.Error);
            this.toggle(false);
            return;
        }

        if (!InvUtils.find(itemStack -> itemStack.getItem() instanceof FireworkItem).found()) {
            NotificationManager.add(new Notification(NotificationType.CLIENT, "У вас нет фейерверков!"), NotificationManager.NotifType.Error);
            this.toggle(false);
            return;
        }

        for (Map.Entry<Long, Runnable> longRunnableEntry : callback.entrySet()) {
            if (System.currentTimeMillis() > longRunnableEntry.getKey())
                longRunnableEntry.getValue().run();
        }

        callback.entrySet().removeIf(a -> System.currentTimeMillis() > a.getKey());

        if (shouldSwap) {
            mc.player.inventory.selectedSlot = prev;
            shouldSwap = false;
        }

        if (mode.get().equals("Control")) {
            if (mc.player.isFallFlying() && fireworkTicks <= 0) {
                FindItemResult firework = InvUtils.find(itemStack -> itemStack.getItem() instanceof FireworkItem);
                if (firework.found()) {
                    use(firework.slot());
                    try {
                        fireworkTicks = (mc.player.inventory.getStack(firework.slot()).getOrCreateSubTag("Fireworks").getByte("Flight") * 35) - ti.get();
                    } catch (Exception ex){}
                }
            } else if (mc.player.isFallFlying()) fireworkTicks--;

            return;
        }

        if (pitchingDown && (mode.get().equals("Автоматически") ? mc.player.getY() <= minHeight.get() : isUpPressed)) {
            pitchingDown = false;
            isUpPressed = false;
        }

        else if (!pitchingDown && (mode.get().equals("Автоматически") ? mc.player.getY() >= maxHeight.get() : isDownPressed)) {
            pitchingDown = true;
            isDownPressed = false;
        }

        // Pitch upwards
        if (!pitchingDown && mc.player.pitch > -40) {
            pitchField -= rotationSpeed.get().intValue();

            if (pitchField < -40) pitchField = -40;
            // Pitch downwards
        } else if (pitchingDown && mc.player.pitch < 40) {
            pitchField += rotationSpeed.get().intValue();

            if (pitchField > 40) pitchField = 40;
        }

        mc.player.pitch = pitchField;
    }

    @Override
    public void onKeybindSetting(KeybindSettingEvent event) {
        if (mode.get().equals("Control")) return;

        if (event.action == InputUtils.Action.PRESS) {
            if (upBind.key(event.key, !event.mouse)) isUpPressed = true;
            else if (downBind.key(event.key, !event.mouse)) isDownPressed = true;
        }
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

    @Override
    public String getHudPrefix() {
        return mode.get();
    }
}
