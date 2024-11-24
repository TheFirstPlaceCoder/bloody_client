package com.client.impl.function.visual;

import com.client.BloodyClient;
import com.client.event.events.Render2DEvent;
import com.client.system.friend.FriendManager;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.ColorSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.Utils;
import com.client.utils.auth.*;
import com.client.utils.auth.records.CheckerClass;
import com.client.utils.color.Colors;
import com.client.utils.game.entity.EntityUtils;
import com.client.utils.game.movement.MovementUtils;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.render.wisetree.render.render2d.main.TextureGL;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
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

import static com.client.utils.math.MathUtils.getRotations;

/**
 * __aaa__
 * 22.05.2024
 * */
public class Arrows extends Function {
    public final MultiBooleanSetting filter = MultiBoolean().name("Отображать").defaultValue(List.of(
            new MultiBooleanValue(true, "Игроков"),
            new MultiBooleanValue(true, "Друзей"),
            new MultiBooleanValue(false, "Инвизов"),
            new MultiBooleanValue(false, "Предметы")
    )).build();

    public final ListSetting color = List().name("Режим цвета").list(List.of("Клиентский", "Статичный")).defaultValue("Клиентский").build();
    public final ColorSetting playerColor = Color().name("Цвет игроков").defaultValue(Color.CYAN).visible(() -> color.get().equals("Статичный")).build();
    public final ColorSetting invisColor = Color().name("Цвет инвизов").defaultValue(Color.GREEN).visible(() -> color.get().equals("Статичный")).build();
    public final ColorSetting itemsColor = Color().name("Цвет предметов").defaultValue(Color.RED).visible(() -> color.get().equals("Статичный")).build();

    public final IntegerSetting zazor = Integer().name("Зазор").defaultValue(70).min(0).max(70).build();

    public Arrows() {
        super("Arrows", Category.VISUAL);

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

    private final Identifier arrow = new Identifier("bloody-client", "/client/arrows.png");
    private float yaw, step, offsetX, offsetY;
    private float size;

    @Override
    public void onRender2D(Render2DEvent event) {
        float size = 30 + zazor.get();

        if (mc.currentScreen instanceof InventoryScreen) {
            size = 150;
        }

        if (MovementUtils.isMoving()) {
            size += 10;
        }

        offsetX = AnimationUtils.fast(offsetX, (mc.player.input.movementSideways) * 3, 5);
        offsetY = AnimationUtils.fast(offsetY, (mc.player.input.movementForward) * 3, 5);

        step = AnimationUtils.fast(step, size);
        yaw = AnimationUtils.fast(yaw, mc.player.renderYaw);

        Utils.rescaling(() -> {
            for (Entity entity : getEntities()) {
                if (entity != mc.player && entity.isAlive()) {
                    drawArrow(entity);
                }
            }
        });

        this.size = size;
    }

    private void drawArrow(Entity entity) {
        float x = (float) mc.getWindow().getWidth() / 4;
        float y = (float) mc.getWindow().getHeight() / 4;

        double look = mc.options.getPerspective().equals(Perspective.THIRD_PERSON_FRONT) ? getRotations(entity) + yaw : getRotations(entity) - yaw;
        double rad = Math.toRadians(look);
        double sin = Math.sin(rad) * (step);
        double cos = Math.cos(rad) * (step);

        GL11.glPushMatrix();
        GL11.glTranslated(x + sin + offsetX, y - cos + offsetY, 0);
        GL11.glScalef(17.5f / 128f, 17.5f / 128f, 17.5f / 128f);
        GL11.glRotated(getRotations(x, y, (float) (x + sin), (float) (y - cos)), 0, 0, 1);

        Color[] colors = new Color[4];

        if (FriendManager.isFriend(entity)) {
            colors[0] = FriendManager.getFriendsColor();
            colors[1] = FriendManager.getFriendsColor();
            colors[2] = FriendManager.getFriendsColor();
            colors[3] = FriendManager.getFriendsColor();
        } else {
            colors[0] = getColor(entity, Colors.getColor((int) (270f * (MathHelper.clamp(((x + sin + offsetX) - (x - sin / 2)) * size, 0F, 1F))), 50));
            colors[1] = getColor(entity, Colors.getColor((int) (270f * (MathHelper.clamp(((x + sin + offsetX) - (x - sin / 2)) * size, 0F, 1F))), 50));
            colors[2] = getColor(entity, Colors.getColor((int) (270f * (MathHelper.clamp(((x + sin + offsetX) - (x - sin / 2)) * size, 0F, 1F))), 50));
            colors[3] = getColor(entity, Colors.getColor((int) (270f * (MathHelper.clamp(((x + sin + offsetX) - (x - sin / 2)) * size, 0F, 1F))), 50));
        }

        TextureGL.create().bind(arrow).draw(new TextureGL.TextureRegion(128, 128), true, colors[0], colors[1], colors[2], colors[3]);
        GL11.glPopMatrix();
    }

    private Color getColor(Entity entity, Color color) {
        if (this.color.get().equals("Клиентский")) return color;

        if (entity instanceof ItemEntity) return itemsColor.get();
        if (entity.isInvisible()) return invisColor.get();
        return playerColor.get();
    }

    private List<Entity> getEntities() {
        List<Entity> entities = new ArrayList<>();

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof ItemEntity && filter.get(3) && EntityUtils.isInRenderDistance(entity)) entities.add(entity);
        }

        entities.addAll(mc.world.getPlayers().stream().filter(player -> {
            if (player == mc.player) return false;
            if (player.isDead() || !EntityUtils.isInRenderDistance(player)) return false;
            if (EntityUtils.isBot(player)) return false;
            if (!filter.get(1) && FriendManager.isFriend(player)) return false;
            if (!filter.get(2) && player.isInvisible()) return false;
            return filter.get(0);
        }).toList());

        return entities;
    }
}