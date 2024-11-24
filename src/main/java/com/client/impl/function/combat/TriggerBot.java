package com.client.impl.function.combat;

import com.client.BloodyClient;
import com.client.event.events.TickEvent;
import com.client.impl.function.combat.aura.TargetHandler;
import com.client.system.friend.FriendManager;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.auth.*;
import com.client.utils.auth.records.CheckerClass;
import com.client.utils.game.entity.EntityUtils;
import com.client.utils.game.entity.SelfUtils;
import com.client.utils.game.inventory.FindItemResult;
import com.client.utils.game.inventory.InvUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.world.GameMode;
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

public class TriggerBot extends Function {
    public TriggerBot() {
        super("Trigger Bot", Category.COMBAT);

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

    private final BooleanSetting criticals = Boolean().name("Только криты").defaultValue(true).build();
    private final BooleanSetting smartCriticals = Boolean().name("Умные криты").defaultValue(true).visible(criticals::get).build();

    private final ListSetting shield = List().name("Щит").defaultValue("Ломать").list(List.of("Ломать", "Ждать", "Игнорировать")).build();
    private final BooleanSetting legit = Boolean().name("Ломать легитно").defaultValue(true).visible(() -> shield.get().equals("Ломать")).build();
    private final BooleanSetting weapon = Boolean().name("Только с оружием").defaultValue(true).build();
    private final BooleanSetting pressingShield = Boolean().name("Отжимать щит").defaultValue(true).build();

    private final BooleanSetting pauseOnUse = Boolean().name("Ждать при использовании").defaultValue(false).build();

    private long shieldWait, shieldMessWait;
    private boolean flag;

    @Override
    public void onEnable() {
        flag = false;
    }

    @Override
    public void onDisable() {
        flag = false;
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (mc.targetedEntity instanceof PlayerEntity player && mc.player.distanceTo(player) <= mc.interactionManager.getReachDistance()) {
            if (!FriendManager.isAttackable(player)) return;
            if (player.isDead()) return;
            if (EntityUtils.getGameMode(player) == GameMode.CREATIVE) return;
            if (!testHand()) return;
            if (!canAttack()) return;

            if (flag) {
                InvUtils.swapBack(true);
                flag = false;
                return;
            }

            TargetHandler.set(player);

            boolean bl = player.isBlocking();
            FindItemResult axe = InvUtils.findInHotbar(itemStack -> itemStack.getItem() instanceof AxeItem);

            if (bl && shield.get().equals("Ждать"))
                return;

            if (bl && shield.get().equals("Ломать")) {
                if (axe.found() && shieldWait > System.currentTimeMillis()) {
                    if (!(mc.player.getMainHandStack().getItem() instanceof AxeItem)) {
                        InvUtils.swap(axe, true);
                    }

                    attack(player);

                    if (!legit.get()) {
                        InvUtils.swapBack(true);
                    } else {
                        flag = true;
                    }

                    if (System.currentTimeMillis() > shieldMessWait) {
                        NotificationManager.add(new Notification(NotificationType.CLIENT,  "Сломал щит игроку " + player.getEntityName(), 1000L), NotificationManager.NotifType.Info);
                        shieldMessWait = System.currentTimeMillis() + 3000L;
                    }

                    shieldWait = System.currentTimeMillis() + 100L;
                }
            }

            if (!flag && (!bl || shield.get().equals("Игнорировать"))) {
                attack(player);
            }
        }
    }

    private void attack(PlayerEntity player) {
        if (pressingShield.get() && SelfUtils.hasItem(Items.SHIELD) && mc.player.isUsingItem()) {
            mc.interactionManager.stopUsingItem(mc.player);
        }

        mc.interactionManager.attackEntity(mc.player, player);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private boolean testHand() {
        return !weapon.get() || mc.player.getMainHandStack().getItem() instanceof AxeItem || mc.player.getMainHandStack().getItem() instanceof SwordItem;
    }

    private boolean canAttack() {
        if (mc.player.isDead()) return false;
        if (pauseOnUse.get() && mc.player.isUsingItem()) return false;

        boolean attack = mc.player.hasStatusEffect(StatusEffects.BLINDNESS) || mc.player.isClimbing()
                || mc.player.isSubmergedInWater() && mc.world.getBlockState(mc.player.getBlockPos()).getBlock() instanceof FluidBlock || mc.player.isRiding()
                || mc.player.abilities.flying || mc.player.isFallFlying();

        if (mc.player.getAttackCooldownProgress(1.5F) < 0.92F) return false;

        boolean jump = !smartCriticals.get() || mc.options.keyJump.isPressed();

        if (!attack && criticals.get() && jump) {
            return !mc.player.isOnGround() && mc.player.fallDistance > 0.0F;
        }

        return true;
    }
}