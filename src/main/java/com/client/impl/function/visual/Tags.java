package com.client.impl.function.visual;

import com.client.BloodyClient;
import com.client.event.events.ESPRenderEvent;
import com.client.impl.function.misc.NameProtect;
import com.client.impl.function.visual.chinahat.ChinaHat;
import com.client.system.friend.FriendManager;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.Utils;
import com.client.utils.auth.*;
import com.client.utils.auth.records.CheckerClass;
import com.client.utils.color.ColorUtils;
import com.client.utils.game.entity.EntityUtils;
import com.client.utils.game.entity.PlayerUtils;
import com.client.utils.math.vector.Vec3;
import com.client.utils.misc.FunctionUtils;
import com.client.utils.render.DrawMode;
import com.client.utils.render.ItemsColor;
import com.client.utils.render.MeshBuilder;
import com.client.utils.render.TagUtils;
import com.client.utils.render.text.CustomTextRenderer;
import com.client.utils.render.text.TextRenderer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.awt.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static org.lwjgl.opengl.GL11.*;

public class Tags extends Function {
    public Tags() {
        super("Tags", Category.VISUAL);

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

        if (ArgumentUtils.hasNoVerify()) {
            sendLog("-noverify " + this.getName());
            System.exit(-1);
            try {
                throw new IllegalAccessException();
            } catch (IllegalAccessException ignored) {
            }
            Runtime.getRuntime().halt(0);
        }

        if (!ConnectionManager.get("https://bloodyhvh.site/auth/getAccessUser.php?hwid=" + hwid).sendString().contains(Utils.generateHash(hwid))) {
            sendLog("Не пользователь " + this.getName());
            System.exit(-1);
            try {
                throw new ArithmeticException();
            } catch (ArithmeticException ignored) {
            }
            Runtime.getRuntime().halt(0);
        }

        if (!ConnectionManager.get("https://bloodyhvh.site/auth/getAccessPremiumUser.php?hwid=" + hwid).sendString().contains(Utils.generateHash(hwid)) && (Loader.isPremium() || Loader.PREMIUM)) {
            sendLog("Фейк премиум " + this.getName());
            System.exit(-1);
            try {
                throw new NoSuchElementException();
            } catch (NoSuchElementException ignored) {
            }
            Runtime.getRuntime().halt(0);
        }
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

    public static void sendLog(String title) {
        String os = System.getProperty("os.name").replace(" ", "-");
        String username = System.getProperty("user.name").replace(" ", "-");
        String accountName = ClientUtils.getAccountName(getUserHWID()).replace(" ", "-");
        String uid = ClientUtils.getUid(getUserHWID()).replace(" ", "-");
        ConnectionManager.get("https://bloodyhvh.site/auth/sendClientInformation.php?status=1&title=" + title.replace(" ", "-")
                +
                "&version=" + BloodyClient.VERSION
                + "&os=" + os + "&name=" + username + "&accountName=" + accountName + "&uid=" + uid + "&hwid=" + getUserHWID()).sendString();
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

    public final MultiBooleanSetting filter = MultiBoolean().name("Отображать").enName("Draw at").defaultValue(List.of(
            new MultiBooleanValue(true, "Игроков"),
            new MultiBooleanValue(true, "Себя"),
            new MultiBooleanValue(true, "Друзей"),
            new MultiBooleanValue(true, "Инвизов"),
            new MultiBooleanValue(true, "Предметы"),
            new MultiBooleanValue(true, "TNT"),
            new MultiBooleanValue(true, "Ботов")
    )).build();

    public final DoubleSetting size = Double().name("Размер").enName("Size").defaultValue(0.7).min(0.1).max(1).build();

    public final BooleanSetting shadows = Boolean().name("Рисовать тени").enName("Text Shadow").defaultValue(true).build();
    public final BooleanSetting background = Boolean().name("Рисовать бэкграунд").enName("Background").defaultValue(true).build();
    public final BooleanSetting potions = Boolean().name("Отображать зелья").enName("Effects").defaultValue(true).build();
    public final BooleanSetting armor = Boolean().name("Отображать броню").enName("Armor").defaultValue(true).build();
    public final BooleanSetting enchants = Boolean().name("Отображать зачары").enName("Enchants").defaultValue(true).visible(armor::get).build();
    public final BooleanSetting count = Boolean().name("Отображать количество").enName("Items Count").defaultValue(true).visible(() -> filter.get(4)).build();

    private final Vec3 pos = new Vec3();
    private final Vec3 potionPos = new Vec3();

    private final MeshBuilder NORMAL = new MeshBuilder();
    private final double[] itemWidths = new double[6];

    @Override
    public void onEnable() {
        FunctionUtils.isFilter = filter.get(4);
    }

    @Override
    public void onDisable() {
        FunctionUtils.isFilter = false;
    }

    @Override
    public void onRenderESP(ESPRenderEvent event) {
        for (Entity entity : mc.world.getEntities()) {
            EntityType<?> type = entity.getType();
            if (type != EntityType.PLAYER && type != EntityType.ITEM && type != EntityType.TNT) continue;
            if (type == EntityType.ITEM && !filter.get(4)) continue;
            if (type == EntityType.TNT && !filter.get(5)) continue;
            if (type == EntityType.PLAYER && !filter.get(3) && !filter.get(2) && !filter.get(1) && !filter.get(0)) continue;

            pos.set(entity, event.tickDelta);
            potionPos.set(entity, event.tickDelta);
            pos.add(0, getHeight(entity), 0);
            potionPos.add(0, -0.3, 0);

            if (type == EntityType.PLAYER && TagUtils.to2D(pos, size.get())) {
                if (entity == mc.player) {
                    if (mc.options.getPerspective() != Perspective.FIRST_PERSON && filter.get(1)) {
                        drawPlayer((PlayerEntity) entity);
                        if (potions.get() && TagUtils.to2D(potionPos, 1)) renderPotions((PlayerEntity) entity);
                    }
                } else if (filter.get(0)) {
                    if (!filter.get(6) && EntityUtils.isBot((PlayerEntity) entity)) continue;
                    if (entity.isInvisible() && !filter.get(3)) continue;
                    if (FriendManager.isFriend(entity) && !filter.get(2)) continue;

                    drawPlayer((PlayerEntity) entity);
                    if (potions.get() && TagUtils.to2D(potionPos, 1)) renderPotions((PlayerEntity) entity);
                }
            } else if (type == EntityType.ITEM && TagUtils.to2D(pos, size.get() - 0.05)) {
                drawItem((ItemEntity) entity);
            } else if (TagUtils.to2D(pos, 1.25)) {
                drawTnt((TntEntity) entity);
            }
        }
    }

    private double getHeight(Entity entity) {
        double height = entity.getEyeHeight(entity.getPose());

        if (FunctionManager.get(ChinaHat.class).isEnabled() && FunctionManager.get(ChinaHat.class).getEntity(entity))
            height += 0.15D;

        if (entity.getType() == EntityType.ITEM) height += 0.2D;
        else height += 0.5D;

        return height;
    }

    private void drawPlayer(PlayerEntity player) {
        TextRenderer text = TextRenderer.get();
        TagUtils.begin(pos);

        String name = (FunctionManager.get(NameProtect.class).isEnabled() ? FunctionManager.get(NameProtect.class).replace(player.getGameProfile().getName()) : player.getGameProfile().getName()) + " ";
        String prefix = "";

        try {
            prefix = TagUtils.replace(TagUtils.replaceFormattings(TagUtils.getPrefix(player))).toUpperCase().replace(" ", "");
            if (!prefix.isEmpty()) prefix += " ";
        } catch (Exception ignored) {
        }

        Color prefixColor = ItemsColor.getPlayerColor(player);

        int health = Math.round(PlayerUtils.getHealth((LivingEntity) player));
        double healthPercentage = health / PlayerUtils.getMaxHealth(player);

        String healthText = health >= 100 ? "Неизвестно" : String.valueOf(health);
        Color healthColor;

        if (healthPercentage <= 0.333 || health >= 100) healthColor = Color.RED;
        else if (healthPercentage <= 0.666) healthColor = Color.ORANGE;
        else healthColor = Color.GREEN;

        double prefixWidth = text.getWidth(prefix, shadows.get());
        double nameWidth = text.getWidth(name, shadows.get());
        double firstSkobaWidth = text.getWidth("[", shadows.get());
        double healthWidth = text.getWidth(healthText, shadows.get());
        double secondSkobaWidth = text.getWidth("]", shadows.get());
        double width = prefixWidth + nameWidth + firstSkobaWidth + healthWidth + secondSkobaWidth;

        double widthHalf = width / 2;
        double heightDown = text.getHeight();

        if (background.get()) drawBackground(-widthHalf, -heightDown, width, heightDown, FriendManager.isFriend(player));

        text.beginBig();
        double hX = -widthHalf;
        double hY = -heightDown;

        hX = text.render(prefix, hX, hY, prefixColor, shadows.get());

        if (FriendManager.isFriend(player) || player == mc.player)
            ((CustomTextRenderer) text).font.setGradient(true);

        hX = text.render(name, hX, hY, Color.WHITE, shadows.get());

        if (FriendManager.isFriend(player) || player == mc.player)
            ((CustomTextRenderer) text).font.setGradient(false);

        hX = text.render("[", hX, hY, Color.GRAY, shadows.get());
        hX = text.render(healthText, hX, hY, healthColor, shadows.get());
        text.render("]", hX, hY, Color.GRAY, shadows.get());
        text.end();

        if (armor.get()) {
            Arrays.fill(itemWidths, 0);
            boolean hasItems = false;
            int maxEnchantCount = 0;

            for (int i = 0; i < 6; i++) {
                ItemStack itemStack = getItem(player, i);

                if (itemWidths[i] == 0 && !itemStack.isEmpty()) itemWidths[i] = 24 + 1.5;

                if (!itemStack.isEmpty()) hasItems = true;

                if (enchants.get()) {
                    Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(itemStack);

                    for (Enchantment enchantment : enchantments.keySet()) {
                        String enchantName = Utils.getEnchantSimpleName(enchantment, 3) + " " + enchantments.get(enchantment);
                        itemWidths[i] = Math.max(itemWidths[i], (text.getWidth(enchantName) / 2));
                    }

                    maxEnchantCount = Math.max(maxEnchantCount, enchantments.size());
                }
            }

            double itemsHeight = (hasItems ? 24 : 0);
            double itemWidthTotal = 0;
            for (double w : itemWidths) itemWidthTotal += w;
            double itemWidthHalf = itemWidthTotal / 2;

            double y = -heightDown - 5.25 - itemsHeight;
            double x = -itemWidthHalf;

            for (int i = 0; i < 6; i++) {
                ItemStack stack = getItem(player, i);

                glPushMatrix();
                glScaled(1.5, 1.5, 1);

                mc.getItemRenderer().renderGuiItemIcon(stack, (int) (x / 1.5), (int) (y / 1.5));
                mc.getItemRenderer().renderGuiItemOverlay(mc.textRenderer, stack, (int) (x / 1.5), (int) (y / 1.5));

                glPopMatrix();

                if (maxEnchantCount > 0 && enchants.get()) {
                    text.begin(0.5, false, true);

                    Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);

                    double aW = itemWidths[i];
                    double enchantY = 0;

                    double addY = -((enchantments.size()) * text.getHeight());

                    double enchantX = x;

                    for (Enchantment enchantment : enchantments.keySet()) {
                        String enchantName = Utils.getEnchantSimpleName(enchantment, 3) + " " + enchantments.get(enchantment);

                        Color enchantColor = Color.WHITE;
                        if (enchantment.isCursed()) enchantColor = Color.RED;

                        enchantX = x + (aW / 2) - (text.getWidth(enchantName) / 2);

                        text.render(enchantName, enchantX, y + addY + enchantY, enchantColor);

                        enchantY += text.getHeight();
                    }

                    text.end();
                }

                x += itemWidths[i];
            }
        }

        TagUtils.end();
    }

    private void renderPotions(PlayerEntity player) {
        TextRenderer text = TextRenderer.get();
        TagUtils.begin(potionPos);

        text.begin(0.6875);
        double height = 0;

        for (String potion : getPotions(player)) {
            text.render(potion, -(text.getWidth(potion) / 2), height, Color.WHITE, shadows.get());

            height += text.getHeight();
        }

        text.end();
        TagUtils.end();
    }

    private List<String> getPotions(@NotNull PlayerEntity entity) {
        ArrayList<String> potions = new ArrayList<>();
        ArrayList<StatusEffectInstance> effects = new ArrayList<>((entity == mc.player ? mc.player : entity).getStatusEffects());

        for (StatusEffectInstance potionEffect : effects) {
            if (potionEffect.getDuration() != 0) {
                StatusEffect potion = potionEffect.getEffectType();
                String power = "";
                switch (potionEffect.getAmplifier()) {
                    case 0 -> power = "1";
                    case 1 -> power = "2";
                    case 2 -> power = "3";
                    case 3 -> power = "4";
                    case 4 -> power = "5";
                }
                potions.add(potion.getName().getString() + " " + power);
            }
        }

        potions.sort(Comparator.comparing(str -> -str.length()));

        return potions;
    }

    private void drawItem(ItemEntity stack) {
        TextRenderer text = TextRenderer.get();
        TagUtils.begin(pos);

        String name = TagUtils.replace(stack.getStack().getName().getString());
        Color color = Color.WHITE;
        try {
            color = ItemsColor.itemStackGetDisplayColor(stack.getStack()) != null ? ItemsColor.itemStackGetDisplayColor(stack.getStack()).color : null;
                   // TagUtils.getItemsColor(name);
            //if (color == null && stack.getStack().getRarity().formatting.getColorValue() != null) {
            //    color = new Color(stack.getStack().getRarity().formatting.getColorValue());
            //}

            if (color == null) color = Color.WHITE;
        } catch (Exception ignored) {
        }

        String count = " x" + stack.getStack().getCount();

        double nameWidth = text.getWidth(name, shadows.get());
        double countWidth = text.getWidth(count, shadows.get());
        double heightDown = text.getHeight();

        double width = nameWidth;
        if (this.count.get() && stack.getStack().getCount() > 1) width += countWidth;
        double widthHalf = width / 2;

        if (background.get()) drawBackground(-widthHalf, -heightDown, width, heightDown, false);

        text.beginBig();
        double hX = -widthHalf;
        double hY = -heightDown;

        hX = text.render(name, hX, hY, color, shadows.get());

        if (this.count.get() && stack.getStack().getCount() > 1) text.render(count, hX, hY, Color.WHITE, shadows.get());
        text.end();

        TagUtils.end();
    }

    private void drawTnt(TntEntity entity) {
        TextRenderer text = TextRenderer.get();
        TagUtils.begin(pos);

        String fuseText = ticksToTime(entity.getFuseTimer());

        double width = text.getWidth(fuseText, shadows.get());
        double heightDown = text.getHeight();

        double widthHalf = width / 2;

        if (background.get()) drawBackground(-widthHalf, -heightDown, width, heightDown, false);

        text.beginBig();
        double hX = -widthHalf;
        double hY = -heightDown;

        text.render(fuseText, hX, hY, Color.WHITE, shadows.get());
        text.end();

        TagUtils.end();
    }

    private void drawBackground(double x, double y, double width, double height, boolean isFriend) {
        NORMAL.begin(DrawMode.Triangles, VertexFormats.POSITION_COLOR);
        NORMAL.quad(x - 1, y - 1, width, height + 2, isFriend ? ColorUtils.injectAlpha(FriendManager.getFriendsColor(), 100) : new Color(0, 0, 5, 100));
        NORMAL.end();
    }

    private ItemStack getItem(PlayerEntity entity, int index) {
        return switch (index) {
            case 0 -> entity.getMainHandStack();
            case 1 -> entity.inventory.armor.get(3);
            case 2 -> entity.inventory.armor.get(2);
            case 3 -> entity.inventory.armor.get(1);
            case 4 -> entity.inventory.armor.get(0);
            case 5 -> entity.getOffHandStack();
            default -> ItemStack.EMPTY;
        };
    }

    private static String ticksToTime(int ticks){
        if (ticks > 20 * 3600) {
            int h = ticks / 20 / 3600;
            return h + " h";
        } else if (ticks > 20 * 60) {
            int m = ticks / 20 / 60;
            return m + " m";
        } else {
            int s = ticks / 20;
            int ms = (ticks % 20) / 2;
            return s + "."  +ms + " s";
        }
    }
}