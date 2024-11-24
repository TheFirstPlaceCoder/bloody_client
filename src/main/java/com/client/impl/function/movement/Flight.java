package com.client.impl.function.movement;

import com.client.BloodyClient;
import com.client.event.events.PacketEvent;
import com.client.event.events.SendMovementPacketsEvent;
import com.client.event.events.TickEvent;
import com.client.interfaces.IVec3d;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.auth.*;
import com.client.utils.auth.records.CheckerClass;
import com.client.utils.game.movement.MovementUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
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

public class Flight extends Function {
    private final ListSetting mode = List().name("Тип полета").list(List.of("Ванильный", "Матрикс", "Скольжение", "Лодка")).defaultValue("Ванильный").build();
    public final DoubleSetting horizontalSpeed = Double().name("Скорость").defaultValue(0.5).min(0).max(5).build();
    public final DoubleSetting verticalSpeed = Double().name("Скорость по Y").defaultValue(0.5).min(0).max(5).build();

    public Flight() {
        super("Flight", Category.MOVEMENT);

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

    private boolean start = false;
    private int timer;
    private int currentTick = 0;

    private boolean sprintFlag, flyFlag, groundFlag, set;

    @Override
    public void onEnable() {
        start = false;
        timer = 0;

        groundFlag = false;
        flyFlag = false;
        sprintFlag = false;
    }

    @Override
    public void onDisable() {
        Timer.setOverride(Timer.OFF);
        if (!mc.player.isSpectator()) {
            mc.player.abilities.flying = false;
            mc.player.abilities.setFlySpeed(0.05f);
            if (mc.player.abilities.creativeMode) return;
            mc.player.abilities.allowFlying = false;
        }
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null)
            return;

        switch (mode.get()) {
            case "Ванильный" -> {
                updatePlayerMotion();
            }

            case "Матрикс" -> {
                if (mc.player.isOnGround())
                    mc.player.jump();
                else {
                    MovementUtils.setMotion(Math.min(horizontalSpeed.get(), 1.97f));
                    double y = 0;
                    if (mc.options.keyJump.isPressed()) y += verticalSpeed.get();
                    if (mc.options.keySneak.isPressed()) y -= verticalSpeed.get();

                    ((IVec3d) mc.player.getVelocity()).setY(y);
                }
            }

            case "Скольжение" -> {
                mc.player.setVelocity(Vec3d.ZERO);
                MovementUtils.setMotion(horizontalSpeed.get());
                mc.player.setVelocity(mc.player.getVelocity().x, -0.003, mc.player.getVelocity().z);
            }

            default -> { // GrimAC
            }
        }
    }

    private float y = 0;

    @Override
    public void onPacket(PacketEvent.Receive e) {
        if (mc.player == null || mc.world == null)
            return;

        if (mode.get().equals("Матрикс")) {
            if (e.packet instanceof PlayerPositionLookS2CPacket p) {
                if (mc.player == null)
                    toggle();
                mc.player.setPosition(p.getX(), p.getY(), p.getZ());
                mc.getNetworkHandler().sendPacket(new TeleportConfirmC2SPacket(p.getTeleportId()));
                e.setCancelled(true);
                toggle();
            }
        }
    }

    @Override
    public void sendMovementPackets(SendMovementPacketsEvent event) {
       if (mode.get().equals("Лодка")) {
            if (hasBoat()) {
                start = true;
                timer = 10;
                groundFlag = mc.player.isOnGround();
            }

            if (start) {
                timer--;

                if (timer <= 10) {
                    start = false;
                }

                event.pitch = -20;
                Vec3d motion = new Vec3d(0, 0, getSpeed(timer)).rotateX(-(float) Math.toRadians(-20)).rotateY(-(float) Math.toRadians(event.yaw));

                if (mc.options.keyLeft.isPressed()) motion = motion.rotateY((float) Math.toRadians(90));
                if (mc.options.keyRight.isPressed()) motion = motion.rotateY((float) -Math.toRadians(90));

                flyFlag = mc.player.abilities.flying;
                mc.player.abilities.flying = true;

                set = true;

                if (mc.player.isSprinting()) {
                    mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
                    sprintFlag = true;
                }

                mc.player.setVelocity(motion);
            }
        }
    }

    @Override
    public void sendMovementPackets(SendMovementPacketsEvent.Post event) {
        if (mode.get().equals("Лодка")) {
            if (set) {
                mc.player.abilities.flying = flyFlag;

                if (sprintFlag) {
                    mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
                    sprintFlag = false;
                }

                set = false;
            }
        }
    }

    private double getSpeed(int div) {
        boolean flag = mc.player.age % 2 == 0;
        return (flag ? 1.5F : 1.5F + new Random().nextDouble() * (groundFlag ? 0.13D : 0.084D)) * (div / 10F * 1D);
    }

    private boolean hasBoat() {
        return mc.world.getEntityCollisions(mc.player, mc.player.getBoundingBox().expand(0.4f), entity -> entity instanceof BoatEntity).findAny().isPresent();
    }

    private void updatePlayerMotion() {
        double motionY = getMotionY();

        MovementUtils.setMotion(horizontalSpeed.get());
        ((IVec3d) mc.player.getVelocity()).setY(motionY);
    }

    private double getMotionY() {
        return mc.options.keySneak.isPressed() ? -verticalSpeed.get()
                : mc.options.keyJump.isPressed() ? verticalSpeed.get() : 0;
    }
}