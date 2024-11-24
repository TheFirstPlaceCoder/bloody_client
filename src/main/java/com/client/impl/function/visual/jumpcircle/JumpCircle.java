package com.client.impl.function.visual.jumpcircle;

import com.client.BloodyClient;
import com.client.event.events.PlayerJumpEvent;
import com.client.event.events.Render3DEvent;
import com.client.event.events.TickEvent;
import com.client.system.friend.FriendManager;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.auth.*;
import com.client.utils.auth.records.CheckerClass;
import com.client.utils.color.Colors;
import com.client.utils.game.entity.EntityUtils;
import com.client.utils.render.Renderer2D;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11C;
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

public class JumpCircle extends Function {
    public JumpCircle() {
        super("Jump Circle", Category.VISUAL);

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

    public final IntegerSetting scaleSpeed = Integer().name("Скорость").defaultValue(3).min(1).max(6).build();
    public final IntegerSetting lifetime = Integer().name("Время").defaultValue(1).min(1).max(10).build();
    public final MultiBooleanSetting filter = MultiBoolean().name("Отображать на").defaultValue(List.of(
            new MultiBooleanValue(true, "Игроков"),
            new MultiBooleanValue(true, "Себя"),
            new MultiBooleanValue(true, "Друзей"),
            new MultiBooleanValue(false, "Инвизов")
    )).build();

    public static List<Circle> circles = new ArrayList<>();
    private long time;

    @Override
    public void onEnable() {
        circles.clear();
    }

    @Override
    public void onJump(PlayerJumpEvent event) {
        if (time > System.currentTimeMillis() || !getEntity(event.entity)) return;
        circles.add(new Circle(new Vec3d(event.entity.getX(), event.entity.getY() + 0.06, event.entity.getZ())));
        time = System.currentTimeMillis() + 50L;
    }

    public boolean getEntity(Entity entity) {
        if (entity == null || !entity.isAlive()) return false;

        List<Entity> entities = new ArrayList<>();

        entities.addAll(mc.world.getPlayers().stream().filter(player -> {
            if (player == mc.player) return filter.get(1);
            if (player.isDead() || !EntityUtils.isInRenderDistance(player)) return false;
            if (EntityUtils.isBot(player)) return false;
            if (FriendManager.isFriend(player)) return filter.get(2);
            if (player.isInvisible()) return filter.get(3);
            return filter.get(0);
        }).toList());

        return entities.contains(entity);
    }

    @Override
    public void tick(TickEvent.Pre event) {
        circles.removeIf(e -> e.update(lifetime.get().longValue()));
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (circles.isEmpty()) return;
        Collections.reverse(circles);
        try {
            for (Circle c : circles) {
                float x = (float) ((float) c.position().x - mc.getEntityRenderDispatcher().camera.getPos().x);
                float y = (float) ((float) c.position().y - mc.getEntityRenderDispatcher().camera.getPos().y);
                float z = (float) ((float) c.position().z - mc.getEntityRenderDispatcher().camera.getPos().z);
                float k = (float) c.timer.getPassedTimeMs() / (float) (lifetime.get() * 1000);
                float start = k * (1.5f + (scaleSpeed.get().floatValue() - 1) / 10);
                float middle = (start + k) / 2;

                /*RenderSystem.enableBlend();
                RenderSystem.blendFunc(770, 771);
                GL11.glEnable(2848);
                GlStateManager._disableDepthTest();
                GL11.glDisable(2884);*/
                Renderer2D.COLOR_3D.begin(GL11C.GL_TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
                for (int i = 0; i <= 360; i += 5) {
                    int clr = getColor(i);
                    float v = (float) Math.sin(Math.toRadians(i));
                    float u = (float) Math.cos(Math.toRadians(i));
                    Renderer2D.COLOR_3D.pos((float) x + (u * start), (float) y, (float) z - (v * start)).color(injectAlpha(new Color(clr), 0)).endVertex();
                    Renderer2D.COLOR_3D.pos((float) x + (u * middle), (float) y, (float) z - (v * middle)).color(injectAlpha(new Color(clr), (int) (255 * (1.0F - (float) c.timer.getPassedTimeMs() / (float) (lifetime.get() * 1000))))).endVertex();
                }
                Renderer2D.COLOR_3D.end();

                Renderer2D.COLOR_3D.begin(GL11C.GL_TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
                for (int i = 0; i <= 360; i += 5) {
                    int clr = getColor(i);
                    float v = (float) Math.sin(Math.toRadians(i));
                    float u = (float) Math.cos(Math.toRadians(i));

                    Renderer2D.COLOR_3D.pos( (float) x + (u * middle), (float) y, (float) z - (v * middle)).color(injectAlpha(new Color(clr), (int) (255 * (1.0F - (float) c.timer.getPassedTimeMs() / (float) (lifetime.get() * 1000))))).endVertex();
                    Renderer2D.COLOR_3D.pos((float) x + (u * k), (float) y, (float) z - (v * k)).color(injectAlpha(new Color(clr), 0)).endVertex();
                }
                Renderer2D.COLOR_3D.end();

                Renderer2D.COLOR_3D.begin(GL11C.GL_TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
                for (int i = 0; i <= 360; i += 5) {
                    int clr = getColor(i);
                    float v = (float) Math.sin(Math.toRadians(i));
                    float u = (float) Math.cos(Math.toRadians(i));

                    Renderer2D.COLOR_3D.pos((float) x + (u * middle), (float) y, (float) z - (v * middle)).color(injectAlpha(new Color(clr), (int) (255 * (1.0F - (float) c.timer.getPassedTimeMs() / (float) (lifetime.get() * 1000))))).endVertex();
                    Renderer2D.COLOR_3D.pos((float) x + (u * (middle - 0.04f)), (float) y, (float) z - (v * (middle - 0.04f))).color(injectAlpha(new Color(clr), 0)).endVertex();
                }
                Renderer2D.COLOR_3D.end();
                /*GlStateManager._enableDepthTest();
                GL11.glDisable(2848);
                GL11.glEnable(2884);
                GlStateManager._disableBlend();*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.reverse(circles);
    }

    public int getColor(int stage) {
        return TwoColoreffect(Colors.getFirst(), Colors.getSecond(), 5 - 0.64, stage).getRGB();
    }

    public static Color injectAlpha(final Color color, final int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), MathHelper.clamp(alpha, 0, 255));
    }

    public static Color TwoColoreffect(Color cl1, Color cl2, double speed, double count) {
        int angle = (int) (((System.currentTimeMillis()) / speed + count) % 360);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return interpolateColorC(cl1, cl2, angle / 360f);
    }

    public static Color interpolateColorC(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        return new Color(interpolateInt(color1.getRed(), color2.getRed(), amount), interpolateInt(color1.getGreen(), color2.getGreen(), amount), interpolateInt(color1.getBlue(), color2.getBlue(), amount), interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

    public static double interpolate(double oldValue, double newValue, double interpolationValue) {
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue) {
        return (int) interpolate(oldValue, newValue, (float) interpolationValue);
    }
}