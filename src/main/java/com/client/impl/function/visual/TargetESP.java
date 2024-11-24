package com.client.impl.function.visual;

import com.client.BloodyClient;
import com.client.event.events.Render3DEvent;
import com.client.impl.function.combat.aura.TargetHandler;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.auth.*;
import com.client.utils.auth.records.CheckerClass;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.game.entity.PlayerUtils;
import com.client.utils.math.animation.Direction;
import com.client.utils.math.animation.impl.SmoothStepAnimation;
import com.client.utils.misc.FunctionUtils;
import com.client.utils.render.wisetree.render.render2d.main.TextureGL;
import com.client.utils.render.wisetree.render.render3d.CircleRenderer;
import com.client.utils.render.wisetree.render.render3d.GhostRenderer;
import com.client.utils.render.wisetree.render.render3d.Renderer3D;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
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

public class TargetESP extends Function {
    public TargetESP() {
        super("Target ESP", Category.VISUAL);

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

    private final ListSetting mode = List().name("Режим").defaultValue("Круг").list(List.of("Квадрат", "Круг", "Новый")).build();

    private final SmoothStepAnimation animation = new SmoothStepAnimation(600, 1);
    private final CircleRenderer circleRenderer = new CircleRenderer();
    private final GhostRenderer ghostRenderer = new GhostRenderer();

    @Override
    public void onRender3D(Render3DEvent event) {
        Entity target = TargetHandler.getTarget();

        if (target == null) return;

        if (!FunctionManager.isEnabled("Attack Aura") || !PlayerUtils.isInRange(target, FunctionUtils.range)) {
            animation.setDirection(Direction.BACKWARDS);
        } else {
            animation.setDirection(Direction.FORWARDS);
        }

        float alpha = (float) animation.getOutput();

        if (alpha <= 0) return;

        MatrixStack stack = event.getMatrices();
        stack.push();

        switch (mode.get()) {
            case "Квадрат" -> {
                Vec3d pos = Renderer3D.getRenderPosition(Renderer3D.getSmoothPos(target)).add(0, target.getHeight() / 2, 0);
                stack.translate(pos.x, pos.y, pos.z);
                stack.scale(1.3F / 512F, 1.3F / 512F, 1.3F / 512F);
                stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-mc.gameRenderer.getCamera().getYaw()));
                stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(mc.gameRenderer.getCamera().getPitch()));
                stack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float) (Math.sin(System.currentTimeMillis() / 1000.0) * 360.0)));
                TextureGL.create().bind(new Identifier("bloody-client", "/client/auratexture.png"))
                        .draw(stack, new TextureGL.TextureRegion(512, 512), false,
                                ColorUtils.injectAlpha(Colors.getColor(0), (int) (alpha * 255)), ColorUtils.injectAlpha(Colors.getColor(90), (int) (alpha * 255)),
                                ColorUtils.injectAlpha(Colors.getColor(180), (int) (alpha * 255)), ColorUtils.injectAlpha(Colors.getColor(270), (int) (alpha * 255)));
            }

            case "Новый" -> ghostRenderer.draw(event.getMatrices(), target, alpha);
            case "Круг" -> circleRenderer.draw(target, alpha);
        }

        stack.pop();
    }
}