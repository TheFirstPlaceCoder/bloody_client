package com.client.impl.function.misc.cheststealer;

import com.client.BloodyClient;
import com.client.clickgui.cheststealer.cheststealer.ChestStealerGui;
import com.client.event.events.KeybindSettingEvent;
import com.client.event.events.TickEvent;
import com.client.impl.command.rct.RctFunctionLite;
import com.client.system.cheststealer.ChestStealerManager;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.*;
import com.client.utils.auth.*;
import com.client.utils.auth.records.CheckerClass;
import com.client.utils.game.entity.PlayerUtils;
import com.client.utils.game.entity.ServerUtils;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.misc.InputUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
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

public class ChestStealer extends Function {
    public ChestStealer() {
        super("Auto Myst", Category.MISC);

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

    private final Widget openGui = Widget().name("Открыть меню").defaultValue(() -> mc.openScreen(ChestStealerGui.getInstance())).build();
    public final ListSetting bypassClick = List().name("Обход клика").list(List.of(
            "Обычный", "New"
    )).defaultValue("New").build();
    private final BooleanSetting funtime = Boolean().name("Обход FunTime").defaultValue(true).build();
    private final ListSetting sortMode = List().list(List.of("Приоритет", "Только", "Нет")).name("Сортировка").defaultValue("Только").build();
    private final IntegerSetting delay = Integer().name("Задержка (MS)").min(0).max(1000).defaultValue(100).build();
    private final KeybindSetting openShulker = Keybind().name("Открыть мистик").defaultValue(-1).build();
    private final DoubleSetting distance = Double().name("Дистанция").defaultValue(3.3).max(6).min(0).visible(() -> openShulker.get() != -1).build();

    private long time;

    @Override
    public void onEnable() {
        time = 0;
    }

    @Override
    public void onKeybindSetting(KeybindSettingEvent event) {
        if (event.action == InputUtils.Action.PRESS) {
            if (!openShulker.key(event.key, !event.mouse)) return;
            for (int i = -5; i < 5; i++) {
                for (int j = -5; j < 5; j++) {
                    for (int k = -5; k < 5; k++) {
                        BlockPos pos = mc.player.getBlockPos().add(i, j, k);
                        if (PlayerUtils.distanceTo(pos) > distance.get()) continue;

                        Block block = mc.world.getBlockState(pos).getBlock();
                        if (block.equals(Blocks.ENDER_CHEST) || block.equals(Blocks.CHEST) || block instanceof ShulkerBoxBlock) {
                            mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(
                                    Vec3d.ofCenter(pos),
                                    Direction.DOWN,
                                    pos,
                                    false
                            ));
                            mc.player.swingHand(Hand.MAIN_HAND);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (mc.player.currentScreenHandler == null || RctFunctionLite.rct) return;

        if (mc.currentScreen != null && (ServerUtils.isHolyWorld() && (mc.currentScreen.getTitle().equals(new TranslatableText("container.enderchest"))) || mc.currentScreen.getTitle().getString().contains("Аукцион") || mc.currentScreen.getTitle().getString().contains("Покупка предмета"))) return;

        ScreenHandler screen = mc.player.currentScreenHandler;

        if (screen instanceof GenericContainerScreenHandler containerScreen) {
            Inventory inventory = containerScreen.getInventory();

            if (inventory.isEmpty()) {
                mc.player.closeScreen();
                return;
            }

            move(inventory);
        }
    }

    private void move(Inventory inventory) {
        List<Integer> slots = ChestStealerManager.getInv(inventory, sortMode.get());

        if (!bypassClick.get().equals("New")) {
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        }

        if (funtime.get()) {
            slots = new ArrayList<>();
            List<Integer> has = new ArrayList<>();
            for (int i = 0; i < inventory.size(); i++) {
                if (inventory.getStack(i).getItem().equals(Items.AIR)) continue;
                has.add(i);
            }
            Random random = new Random();
            for (Integer i : has) {
                int slot = has.get(random.nextInt(has.size()));
                while (slots.contains(slot)) {
                    slot = has.get(random.nextInt(has.size()));
                }
                slots.add(slot);
            }
        }

        for (Integer slot : slots) {
            if (ServerUtils.isHolyWorld() && ServerUtils.getAnarchy().contains("Лайт") && inventory.getStack(slot).getItem().equals(Items.TOTEM_OF_UNDYING) && totemCount() >= 4) continue;
            if (System.currentTimeMillis() >= time) {
                if (bypassClick.get().equals("New")) {
                    int air = getAir();
                    if (air != -1) {
                        if (ServerUtils.isHolyWorld() && ServerUtils.getAnarchy().contains("Классик")) {
                            InvUtils.move().fromId(slot).toId(air);
                        } else {
                            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, air, SlotActionType.SWAP, mc.player);
                        }
                    }
                } else {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.QUICK_MOVE, mc.player);
                }
                time = System.currentTimeMillis() + delay.get();
            }
        }

        if (!bypassClick.get().equals("New")) {
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        }
    }

    private int getAir() {
        int slot = -1;
        for (int i = 8; i < mc.player.inventory.size(); i++) {
            if (mc.player.inventory.getStack(i).getItem().equals(Items.AIR)) {
                slot = i;
                break;
            }
        }
        if (slot == -1) {
            for (int i = 0; i <= 8; i++) {
                if (mc.player.inventory.getStack(i).getItem().equals(Items.AIR)) {
                    slot = i;
                    break;
                }
            }
        }
        return slot;
    }

    private int totemCount() {
        int i = 0;
        for (int i1 = 0; i1 < mc.player.inventory.size(); i1++) {
            if (mc.player.inventory.getStack(i1).getItem().equals(Items.TOTEM_OF_UNDYING)) i++;
        }
        return i;
    }
}