package com.client.impl.function.player;

import com.client.BloodyClient;
import com.client.event.events.KeybindSettingEvent;
import com.client.event.events.PacketEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.KeybindSetting;
import com.client.utils.Utils;
import com.client.utils.auth.*;
import com.client.utils.auth.records.CheckerClass;
import com.client.utils.game.inventory.FindItemResult;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.game.inventory.SlotUtils;
import com.client.utils.misc.InputUtils;
import com.client.utils.misc.TaskTransfer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * __aaa__
 * 25.05.2024
 * */
public class ElytraHelper extends Function {
    public ElytraHelper() {
        super("Elytra Helper", Category.PLAYER);

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

    private final KeybindSetting swap = Keybind().name("Свап").enName("Swap").defaultValue(-1).build();
    private final KeybindSetting firewowrk = Keybind().name("Фейерверк").enName("Firework").defaultValue(-1).build();
    private final BooleanSetting autoStart = Boolean().name("Авто полет").enName("Auto Start").defaultValue(false).build();
    private final BooleanSetting useFirework = Boolean().name("Использовать фейерверк сразу").enName("Use Start Firework").defaultValue(false).visible(autoStart::get).build();

    public final IntegerSetting delay = Integer().name("Задержка").enName("Swap Delay").defaultValue(2).min(0).max(6).visible(() -> firewowrk.get() != -1 || (autoStart.get() && useFirework.get())).build();
    private final BooleanSetting excludeHotbar = Boolean().name("Не оставлять в хотбаре").enName("Exclude Hotbar").defaultValue(true).visible(() -> firewowrk.get() != -1 || (autoStart.get() && useFirework.get())).build();

    private final TaskTransfer taskTransfer = new TaskTransfer(), equipTaskTransfer = new TaskTransfer();
    private boolean shouldSwap = false, afterSwap = false;
    public int prev;

    @Override
    public void onEnable() {
        shouldSwap = false;
        afterSwap = false;
    }

    @Override
    public void onKeybindSetting(KeybindSettingEvent event) {
        if (event.action != InputUtils.Action.PRESS) return;

        if (swap.key(event.key, !event.mouse)) swap();
        else if (firewowrk.key(event.key, !event.mouse)) useFirework();
    }

    @Override
    public void tick(TickEvent.Post event) {
        equipTaskTransfer.handle();
        taskTransfer.handle();

        if (shouldSwap) {
            mc.player.inventory.selectedSlot = prev;
            shouldSwap = false;
        }
    }

    @Override
    public void onPacket(PacketEvent.Receive event) {
        if (afterSwap && event.packet instanceof UpdateSelectedSlotS2CPacket) {
            shouldSwap = true;

            taskTransfer.bind(() -> {
                mc.player.inventory.selectedSlot = prev;
            }, delay.get() * 50L);

            afterSwap = false;
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
            taskTransfer.bind(() -> {
                mc.player.inventory.selectedSlot = prev;
                afterSwap = true;
            }, delay.get() * 50L);
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
                taskTransfer.bind(() -> {
                    mc.player.inventory.selectedSlot = prev;
                    afterSwap = true;
                }, delay.get() * 50L);
            } else {
                taskTransfer.bind(() -> {
                    mc.interactionManager.pickFromInventory(slot);
                    afterSwap = true;
                }, delay.get() * 50L);
            }
        }
    }

    private void swap() {
        ItemStack slot = mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem().getDefaultStack();
        int equip;
        boolean chestplateFlag = false;

        if (slot.isEmpty() || slot.getItem().equals(Items.ELYTRA)) {
            equip = getSlot(true);
            chestplateFlag = true;

            if (equip == -1 && slot.isEmpty()) {
                equip = getSlot(false);
                chestplateFlag = false;
            }
        } else {
            equip = getSlot(false);
        }

        if (equip == -1) return;

        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 6, 0, SlotActionType.QUICK_MOVE, mc.player);

        if (SlotUtils.isHotbar(equip)) {
            InvUtils.swap(equip);
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
            InvUtils.swapBack();
        } else {
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, equip, 0, SlotActionType.QUICK_MOVE, mc.player);
        }

        NotificationManager.add(new Notification(NotificationType.CLIENT, "Изменил на " + (chestplateFlag ? "нагрудник" : "элитры") + ".", 2000L), NotificationManager.NotifType.Info);

        if (autoStart.get() && !chestplateFlag) {
            if (mc.player.isOnGround()) {
                mc.player.jump();
            }

            equipTaskTransfer.bind(() -> {
                mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                if (useFirework.get()) useFirework();
            }, 70);
        }
    }

    public void useFirework() {
        FindItemResult i = getItem();

        if (!i.found() || mc.player.getItemCooldownManager().isCoolingDown(mc.player.inventory.getStack(i.slot()).getItem())) {
            return;
        }

        if (i.slot() == 45) {
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.OFF_HAND);
            mc.player.swingHand(Hand.OFF_HAND);

            return;
        }

        use(i.slot());
    }

    private int getSlot(boolean chestplate) {
        long hash = 0;
        int index = 0;

        for (int i = 0; i < mc.player.inventory.size(); i++) {
            ItemStack stack = mc.player.inventory.getStack(i);

            if (!chestplate && stack.getItem().equals(Items.ELYTRA)) {
                return i;
            }

            if (stack.getTranslationKey().contains("chestplate")) {
                long tempHash = InvUtils.getId(stack);

                if (tempHash > hash) {
                    index = i;
                    hash = tempHash;
                }
            }

        }
        return index;
    }

    private FindItemResult getItem() {
        FindItemResult result;

        if (mc.player.getOffHandStack().getItem().equals(Items.FIREWORK_ROCKET))
            return new FindItemResult(45, mc.player.getOffHandStack().getCount());

        result = InvUtils.find(Items.FIREWORK_ROCKET);

        return result;
    }
}