package com.client.impl.function.visual;

import com.client.BloodyClient;
import com.client.event.events.Render3DEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.ColorSetting;
import com.client.utils.Utils;
import com.client.utils.auth.*;
import com.client.utils.auth.records.CheckerClass;
import com.client.utils.color.ColorUtils;
import com.client.utils.render.wisetree.render.render3d.Renderer3D;
import mixin.accessor.InteractionManagerAccessor;
import mixin.accessor.WorldRendererAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BlockBreakingInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
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

public class BreakIndicators extends Function {
    private final BooleanSetting alpha = Boolean().name("Плавное появление").defaultValue(false).build();
    private final BooleanSetting renderSelf = Boolean().name("Нынешняя позиция").defaultValue(false).build();
    public final ColorSetting colorSetting = Color().name("Цвет").defaultValue(Color.CYAN).build();

    public BreakIndicators() {
        super("Break Indicators", Category.VISUAL);

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

    @Override
    public void onRender3D(Render3DEvent event) {
        renderNormal(event);
    }

    private void renderNormal(Render3DEvent event) {
        Map<Integer, BlockBreakingInfo> blocks = ((WorldRendererAccessor) mc.worldRenderer).getBlockBreakingInfos();

        float ownBreakingStage = ((InteractionManagerAccessor) mc.interactionManager).getCurrentBreakingProgress();
        BlockPos ownBreakingPos = ((InteractionManagerAccessor) mc.interactionManager).getCurrentBreakingPos();

        if (renderSelf.get() && ownBreakingPos != null && ownBreakingStage > 0) {
            BlockState state = mc.world.getBlockState(ownBreakingPos);
            VoxelShape shape = state.getOutlineShape(mc.world, ownBreakingPos);
            if (shape == null || shape.isEmpty()) return;

            Box orig = shape.getBoundingBox();

            double shrinkFactor = 1d - ownBreakingStage;

            renderBlock(event, orig, ownBreakingPos, shrinkFactor);
        }

        blocks.values().forEach(info -> {
            BlockPos pos = info.getPos();
            int stage = info.getStage();
            if (pos.equals(ownBreakingPos)) return;

            BlockState state = mc.world.getBlockState(pos);
            VoxelShape shape = state.getOutlineShape(mc.world, pos);
            if (shape == null || shape.isEmpty()) return;

            Box orig = shape.getBoundingBox();

            double shrinkFactor = (9 - (stage + 1)) / 9d;

            renderBlock(event, orig, pos, shrinkFactor);
        });
    }

    private void renderBlock(Render3DEvent event, Box orig, BlockPos pos, double shrinkFactor) {
        Box box = orig.shrink(
                orig.getXLength() * shrinkFactor,
                orig.getYLength() * shrinkFactor,
                orig.getZLength() * shrinkFactor
        );

        double xShrink = (orig.getXLength() * shrinkFactor) / 2;
        double yShrink = (orig.getYLength() * shrinkFactor) / 2;
        double zShrink = (orig.getZLength() * shrinkFactor) / 2;

        double x1 = pos.getX() + box.minX + xShrink;
        double y1 = pos.getY() + box.minY + yShrink;
        double z1 = pos.getZ() + box.minZ + zShrink;
        double x2 = pos.getX() + box.maxX + xShrink;
        double y2 = pos.getY() + box.maxY + yShrink;
        double z2 = pos.getZ() + box.maxZ + zShrink;

        Renderer3D.prepare3d(false);

        Renderer3D.drawFilled(new Box(x1, y1, z1, x2, y2, z2), alpha.get() ? getColor(colorSetting.get(), 1 - shrinkFactor) : colorSetting.get());
        Renderer3D.drawOutline(new Box(x1, y1, z1, x2, y2, z2), alpha.get() ? getColor(ColorUtils.injectAlpha(colorSetting.get(), colorSetting.get().getAlpha() + 50), 1 - shrinkFactor) : ColorUtils.injectAlpha(colorSetting.get(), colorSetting.get().getAlpha() + 50));

        Renderer3D.end3d(false);
    }

    private Color getColor(Color color, double delta) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) Utils.clamp((int) Math.floor(color.getAlpha() * delta), 0, 255));
    }
}