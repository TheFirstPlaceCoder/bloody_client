package com.client.impl.function.visual;

import com.client.BloodyClient;
import com.client.event.events.PacketEvent;
import com.client.event.events.Render2DEvent;
import com.client.event.events.SendMovementPacketsEvent;
import com.client.impl.function.movement.Timer;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.auth.*;
import com.client.utils.auth.records.CheckerClass;
import com.client.utils.math.MsTimer;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.awt.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class KillEffect extends Function {
    public KillEffect() {
        super("Kill Effect", Category.VISUAL);

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

    private final ListSetting mode = List().name("Режим").list(List.of("Молния", "Клиентский", "Огненные частицы")).defaultValue("Молния").build();

    private final MsTimer timerHelper = new MsTimer();

    private boolean timerForce2;
    private boolean died;
    private boolean hasChanged;

    @Override
    public void onEnable() {
        timerForce2 = false;
        died = false;
        hasChanged = false;
    }

    @Override
    public void onDisable() {
        timerForce2 = false;
    }

    @Override
    public void onPacket(PacketEvent.Receive event) {
        if (event.packet instanceof EntityStatusS2CPacket p && p.getEntity(mc.world) instanceof PlayerEntity player) {
            if (player != mc.player && p.getStatus() == 3 && mc.player.distanceTo(player) < 7) {
                if (mode.get().equals("Молния")) {
                    double x = player.getX();
                    double y = player.getY();
                    double z = player.getZ();

                    LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, mc.world);

                    lightning.updatePosition(x, y, z);
                    lightning.refreshPositionAfterTeleport(x, y, z);
                    mc.world.addEntity(lightning.getEntityId(), lightning);
                } else if (mode.get().equals("Огненные частицы")) {
                    for (int i = 0; i < 360; i+=5) {
                        double sin = Math.sin(Math.toRadians(i)) * player.getWidth() * 1.2;
                        double cos = Math.cos(Math.toRadians(i)) * player.getWidth() * 1.2;

                        mc.world.addParticle(ParticleTypes.LAVA, player.getX() + cos, player.getY() + player.getHeight() * 0.75F, player.getZ() + sin, 0, 0, 0);
                    }
                } else {
                    died = true;
                    timerHelper.reset();
                }
            }
        }
    }

    @Override
    public void sendMovementPackets(SendMovementPacketsEvent event) {
        if (!mode.get().equals("Клиентский")) return;

        if (timerForce2) {
            Timer.setOverride(0.2F);
            timerForce2 = false;
        }

        if (died && !timerHelper.passedMs(1500)) {
            getMemoryTrigger();
        } else if (hasChanged) {
            Timer.setOverride(Timer.OFF);
            hasChanged = false;
            died = false;
        }
    }

    private void getMemoryTrigger() {
        Timer.setOverride(0.075F);
        timerForce2 = true;
        hasChanged = true;
    }

    public float getStrikeEffectFovModifyPC() {
        return getGlobalEffectAnimation();
    }

    private float getGlobalEffectAnimation() {
        if (!died || timerHelper.passedMs(1500)) return 1F;

        if (!timerHelper.passedMs(1000)) {
            return (1500 - timerHelper.getTimeMs()) / 1000f;
        } else {
            return 1 - ((1500 - timerHelper.getTimeMs()) / 1500f);
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        if (!mode.get().equals("Клиенсткий")) return;

        if (died && !timerHelper.passedMs(1500)) drawLightVignette(getStrikeEffectFovModifyPC());
    }

    private void drawLightVignette(float alpha) {
        GL.drawQuad(0, 0, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight(), new Color(255, 255, 255, (int) MathHelper.clamp(140 * (1 - alpha), 0, 255)));
    }
}