package com.client.impl.function.combat;

import com.client.BloodyClient;
import com.client.event.events.PlayerUpdateEvent;
import com.client.event.events.Render3DEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.combat.aura.TargetHandler;
import com.client.interfaces.IGameRenderer;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.Utils;
import com.client.utils.auth.*;
import com.client.utils.auth.records.CheckerClass;
import com.client.utils.game.entity.SelfUtils;
import com.client.utils.game.inventory.FindItemResult;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.game.rotate.Rotations;
import com.client.utils.math.MsTimer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static net.minecraft.util.math.MathHelper.wrapDegrees;

public class LegitAura extends Function {
    public final BooleanSetting testing = Boolean().name("Обход ротации").enName("Rotation Bypass").defaultValue(true).build();
    public final IntegerSetting aimStrength = Integer().name("Сила по горизонтали").enName("Horizontal Speed").max(200).min(1).defaultValue(190).build();
    public final IntegerSetting pitchStrength = Integer().name("Сила по вертикали").enName("Vertical Speed").max(200).min(1).defaultValue(124).build();

    public final IntegerSetting minAimBoost = Integer().name("Минимальное ускорение").enName("Min Aim Boost").max(300).min(150).defaultValue(295).build();
    public final IntegerSetting maxAimBoost = Integer().name("Максимальное ускорение").enName("Max Aim Boost").max(300).min(150).defaultValue(300).build();
    public final IntegerSetting delayBoost = Integer().name("Тики ускорения").enName("Boost Ticks").max(50).min(1).defaultValue(3).build();

    public final DoubleSetting range = Double().name("Дистанция").enName("Attack Range").defaultValue(3.0).min(1).max(6).build();

    private final MultiBooleanSetting targets = MultiBoolean().name("Цели").enName("Targets").defaultValue(List.of(
            new MultiBooleanValue(true, "Игроки"),
            new MultiBooleanValue(false, "Инвизы"),
            new MultiBooleanValue(false, "Голые"),
            new MultiBooleanValue(false, "Боты"),
            new MultiBooleanValue(false, "Монстры"),
            new MultiBooleanValue(false, "Животные"),
            new MultiBooleanValue(false, "Все")
    )).build();

    private final BooleanSetting criticals = Boolean().name("Только криты").enName("Only Crits").defaultValue(true).build();
    private final BooleanSetting smartCriticals = Boolean().name("Умные криты").enName("Smart Crits").defaultValue(true).visible(criticals::get).build();

    private final ListSetting shield = List().name("Щит").enName("Shield Mode").defaultValue("Ломать").list(List.of("Ломать", "Ждать", "Игнорировать")).build();

    private final BooleanSetting weapon = Boolean().name("Только с оружием").enName("Only Weapon").defaultValue(true).build();
    private final BooleanSetting wallsAttack = Boolean().name("Бить через стены").enName("Walls Attack").defaultValue(false).build();
    private final BooleanSetting pressingShield = Boolean().name("Отжимать щит").enName("Unpress Shield").defaultValue(true).build();
    private final BooleanSetting pauseOnUse = Boolean().name("Ждать при использовании").enName("Pause On Use").defaultValue(false).build();

    public LegitAura() {
        super("Legit Aura", Category.COMBAT);

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

    public Entity target, oldTarget;
    private long shieldWait, shieldMessWait, attackTime;
    public double oldY = -1;
    private float rotationYaw, rotationPitch, assistAcceleration, pitchAcceleration;
    private int aimTicks = 0;
    private MsTimer visibleTime = new MsTimer();

    @Override
    public void onEnable() {
        target = null;
        oldTarget = null;
        rotationYaw = mc.player.yaw;
        rotationPitch = mc.player.pitch;
        oldY = -1;
    }

    @Override
    public void onDisable() {
        target = null;
        oldTarget = null;
    }

    public double gerRadius() {
        return range.get();
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (rotationYaw != -9999)
            mc.player.yaw = ((float) Utils.lerp(mc.player.yaw, rotationYaw, assistAcceleration));

        if (rotationPitch != -9999)
            mc.player.pitch = ((float) Utils.lerp(mc.player.pitch, rotationPitch, pitchAcceleration));
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (mc.crosshairTarget.getType() == HitResult.Type.ENTITY)
            aimTicks++;
        else
            aimTicks = 0;

        if (aimTicks >= 1 || (target != null && oldTarget != null && target != oldTarget)) {
            assistAcceleration = 0;
            pitchAcceleration = 0;
            return;
        }

        assistAcceleration += (mc.player.age % delayBoost.get() == 0 ? Utils.random(minAimBoost.get(), maxAimBoost.get()) : aimStrength.get()) / 10000f;
        pitchAcceleration += pitchStrength.get() / 10000f;

        if (target != null) {
            oldTarget = target;
            if (!mc.player.canSee(target)) {
                if (!wallsAttack.get())
                    visibleTime.reset();
            }

            if (!visibleTime.passedMs(40)) {
                rotationYaw = -9999;
                rotationPitch = -9999;
                return;
            }

            if (rotationYaw == -9999)
                rotationYaw = mc.player.yaw;

            if (rotationPitch == -9999)
                rotationPitch = mc.player.pitch;

            if (testing.get()) {
                rotationYaw = (float) Rotations.getYaw(target.getPos().add(0, target.getHeight() * 0.8, 0));
            } else {
                float delta_yaw = wrapDegrees((float) wrapDegrees(Math.toDegrees(Math.atan2(target.getPos().add(0, target.getEyeHeight(target.getPose()), 0).z - mc.player.getZ(), (target.getPos().add(0, target.getEyeHeight(target.getPose()), 0).x - mc.player.getX()))) - 90) - rotationYaw);
                if (delta_yaw > 180)
                    delta_yaw = delta_yaw - 180;
                float deltaYaw = MathHelper.clamp(MathHelper.abs(delta_yaw), -180, 180);
                float newYaw = rotationYaw + (delta_yaw > 0 ? deltaYaw : -deltaYaw);
                double gcdFix = (Math.pow(mc.options.mouseSensitivity * 0.6 + 0.2, 3.0)) * 1.2;
                rotationYaw = (float) (newYaw - (newYaw - rotationYaw) % gcdFix);
            }

            rotationPitch = (float) Rotations.getPitch(target.getPos().add(0, oldY == -1 || mc.player.age % 20 == 0 ? oldY = (target.getY() > mc.player.getY() ? Utils.random(0, target.getHeight() / 2) : Utils.random(target.getHeight() / 2, target.getHeight())) : oldY, 0));
        } else {
            rotationYaw = -9999;
            rotationPitch = -9999;
        }
    }

    @Override
    public void onPlayerUpdate(PlayerUpdateEvent event) {
        double radius = gerRadius();
        TargetHandler.handle(targets, radius, wallsAttack.get());
        target = TargetHandler.getTarget(radius);

        if (target == null || (!testHand() || shield())) return;

        if (canAttack() && System.currentTimeMillis() > attackTime) {
            attack();
        }
    }

    private void attack() {
        boolean bl = mc.player.isSprinting();
        boolean bl2 = false;

        if (pressingShield.get() && SelfUtils.hasItem(Items.SHIELD) && mc.player.isUsingItem()) {
            mc.interactionManager.stopUsingItem(mc.player);
        }

        if (bl) {
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
            mc.player.setSprinting(false);
            bl2 = true;
        }

        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);

        if (!(mc.player.getMainHandStack().getItem() instanceof AxeItem || mc.player.getMainHandStack().getItem() instanceof SwordItem))
            attackTime = System.currentTimeMillis() + 500L;

        if (bl2) {
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
            mc.player.setSprinting(true);
        }
    }

    public boolean canAttack() {
        return canAttack(true);
    }

    public boolean canAttack(boolean raytrace) {
        if (target == null || mc.player.isDead()) return false;
        if (pauseOnUse.get() && mc.player.isUsingItem()) return false;
        if ((((IGameRenderer) mc.gameRenderer).getTarget(mc.player.yaw, mc.player.pitch) != target) && raytrace && !wallsAttack.get()) return false;
        if (mc.player.distanceTo(target) > range.get()) return false;
        if (target instanceof PlayerEntity && ((PlayerEntity) target).isBlocking() && shield.get().equals("Ломать") && !raytrace) return true;

        boolean attack = mc.player.hasStatusEffect(StatusEffects.BLINDNESS) || mc.player.isClimbing()
                || mc.player.isSubmergedInWater() && mc.world.getBlockState(mc.player.getBlockPos()).getBlock() instanceof FluidBlock || mc.player.isRiding()
                || mc.player.abilities.flying || mc.player.isFallFlying();

        if (mc.player.getAttackCooldownProgress(1.5F) < 0.92) return false;

        boolean jump = !smartCriticals.get() || mc.options.keyJump.isPressed();

        if (!attack && criticals.get() && jump) {
            return !mc.player.isOnGround() && mc.player.fallDistance > 0.0F;
        }

        return true;
    }

    public boolean testHand() {
        return !weapon.get() || mc.player.getMainHandStack().getItem() instanceof AxeItem || mc.player.getMainHandStack().getItem() instanceof SwordItem;
    }

    private boolean shield() {
        if (target == null) return false;
        if (!(target instanceof PlayerEntity)) return false;

        if (shield.get().equals("Игнорировать")) return false;

        boolean bl = ((PlayerEntity) target).isBlocking();
        if (bl && shield.get().equals("Ждать")) return true;

        if (bl) {
            FindItemResult axe = InvUtils.find(itemStack -> itemStack.getItem() instanceof AxeItem);
            if (!axe.found() || shieldWait > System.currentTimeMillis()) return false;

            if (axe.isHotbar()) {
                mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(axe.slot()));
            } else {
                InvUtils.quickSwap().fromId(axe.slot()).to(mc.player.inventory.selectedSlot);
            }

            mc.interactionManager.attackEntity(mc.player, target);

            if (System.currentTimeMillis() > shieldMessWait) {
                NotificationManager.add(new Notification(NotificationType.CLIENT,  "Сломал щит игроку " + target.getEntityName(), 1000L), NotificationManager.NotifType.Info);
                shieldMessWait = System.currentTimeMillis() + 3000L;
            }

            if (axe.isHotbar()) {
                mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            } else {
                InvUtils.quickSwap().fromId(axe.slot()).to(mc.player.inventory.selectedSlot);
            }

            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.inventory.selectedSlot));
            shieldWait = System.currentTimeMillis() + 100L;
        }

        return false;
    }
}
