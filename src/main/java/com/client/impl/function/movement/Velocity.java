package com.client.impl.function.movement;

import com.client.BloodyClient;
import com.client.event.events.PacketEvent;
import com.client.event.events.TickEvent;
import com.client.interfaces.IEntityVelocityUpdateS2CPacket;
import com.client.interfaces.IExplosionS2CPacket;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.auth.*;
import com.client.utils.auth.records.CheckerClass;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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

public class Velocity extends Function {
    private final ListSetting mode = List().name("Режим").list(List.of("Matrix", "Grim", "New Grim", "HolyWorld", "Легитный", "Прыжок", "Ванильный")).defaultValue("Ванильный").build();
    public final BooleanSetting pauseInFluids = Boolean().name("Пауза в жидкостях").defaultValue(true).build();
    public final BooleanSetting fire = Boolean().name("Пауза в огне").defaultValue(true).build();

    public Velocity() {
        super("Velocity", Category.MOVEMENT);

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

    private boolean flag;
    private int grimTicks, ccCooldown;
    boolean damaged;

    @Override
    public void onEnable() {
        grimTicks = 0;
        damaged = false;
        ccCooldown = 0;
    }

    @Override
    public void onPacket(PacketEvent.Receive e) {
        if(mc.player != null && (mc.player.isTouchingWater() || mc.player.isSubmergedInWater() || mc.player.isInLava()) && pauseInFluids.get())
            return;

        if(mc.player != null && mc.player.isOnFire() && fire.get() && (mc.player.hurtTime > 0)){
            return;
        }

        if (ccCooldown > 0) {
            ccCooldown--;
            return;
        }

        if (e.packet instanceof EntityVelocityUpdateS2CPacket pac) {
            if (pac.getId() == mc.player.getEntityId()) {
                switch (mode.get()) {
                    case "Matrix" -> {
                        if (!flag) {
                            e.setCancelled(true);
                            flag = true;
                        } else {
                            flag = false;
                            ((IEntityVelocityUpdateS2CPacket) pac).setX(((int) ((double) pac.getVelocityX() * -0.1)));
                            ((IEntityVelocityUpdateS2CPacket) pac).setZ(((int) ((double) pac.getVelocityZ() * -0.1)));
                        }
                    }
                    case "Ванильный" -> {
                        e.setCancelled(true);
                    }
                    case "Grim" -> {
                        e.setCancelled(true);
                        grimTicks = 6;
                    }
                    case "New Grim" -> {
                        e.setCancelled(true);
                        flag = true;
                    }
                    case "Легитный" -> {
                        e.setCancelled(true);
                        flag = true;
                        mc.options.keySneak.setPressed(true);
                    }
                    case "Прыжок" -> {
                        //e.setCancelled(true);
                        mc.player.jump();
                        //mc.player.setVelocity(0, -1, 0);
                    }
                    case "HolyWorld" -> {
                        ((IEntityVelocityUpdateS2CPacket) pac).setX((int) ((double) pac.getVelocityX() * 0.666f));
                        ((IEntityVelocityUpdateS2CPacket) pac).setZ((int) ((double) pac.getVelocityZ() * 0.666f));
                    }
                }
            }
        }

        if (e.packet instanceof ExplosionS2CPacket explosion) {
            switch (mode.get()) {
                case "Ванильный" -> {
                    ((IExplosionS2CPacket) explosion).setVelocityX(0);
                    ((IExplosionS2CPacket) explosion).setVelocityY(0);
                    ((IExplosionS2CPacket) explosion).setVelocityZ(0);
                }
                case "Новый Grim" -> {
                    e.setCancelled(true);
                    flag = true;
                }
            }
        }

        if (mode.get().equals("Grim")) {
            if (e.packet instanceof QueryPingC2SPacket && grimTicks > 0) {
                e.setCancelled(true);
                grimTicks--;
            }
        }

        if (e.packet instanceof PlayerPositionLookS2CPacket) {
            if (mode.get().equals("Новый Grim")) ccCooldown = 5;
        }
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (mc.player != null && (mc.player.isTouchingWater() || mc.player.isSubmergedInWater()) && pauseInFluids.get())
            return;

        if (mode.get().equals("Matrix")) {
            if (mc.player.hurtTime > 0 && !mc.player.isOnGround()) {
                double var3 = mc.player.yaw * 0.017453292F;
                double var5 = Math.sqrt(mc.player.getVelocity().x * mc.player.getVelocity().x + mc.player.getVelocity().z * mc.player.getVelocity().z);
                mc.player.setVelocity(-Math.sin(var3) * var5, mc.player.getVelocity().y, Math.cos(var3) * var5);
                mc.player.setSprinting(mc.player.age % 2 != 0);
            }
        } else if (mode.get().equals("New Grim")) {
            if (flag) {
                if(ccCooldown <= 0) {
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Both(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.yaw, mc.player.pitch, mc.player.isOnGround()));
                    mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, new BlockPos(mc.player.getPos().x, mc.player.getPos().y, mc.player.getPos().z), Direction.DOWN));
                }
                flag = false;
            }
        } else if (mode.get().equals("Легитный")) {
            if (flag) {
                mc.options.keySneak.setPressed(false);
                flag = false;
            }
        }

        if (grimTicks > 0)
            grimTicks--;
    }
}