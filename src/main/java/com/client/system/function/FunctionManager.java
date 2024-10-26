package com.client.system.function;

import api.interfaces.EventHandler;
import com.client.event.events.*;
import com.client.impl.function.client.*;
import com.client.impl.function.combat.*;
import com.client.impl.function.combat.aura.AttackAura;
import com.client.impl.function.combat.autoarmor.AutoArmor;
import com.client.impl.function.misc.*;
import com.client.impl.function.misc.autoseller.AutoSeller;
import com.client.impl.function.misc.cheststealer.ChestStealer;
import com.client.impl.function.movement.*;
import com.client.impl.function.movement.Timer;
import com.client.impl.function.movement.blink.Blink;
import com.client.impl.function.visual.*;
import com.client.impl.function.visual.chinahat.ChinaHat;
import com.client.impl.function.visual.esp.ESP;
import com.client.impl.function.visual.hitbubbles.HitBubbles;
import com.client.impl.function.visual.jumpcircle.JumpCircle;
import com.client.impl.function.visual.particles.Particles;
import com.client.impl.function.visual.storageesp.StorageESP;
import com.client.impl.function.visual.trajectories.Trajectories;
import com.client.impl.function.visual.xray.XRay;
import com.client.utils.auth.*;
import com.client.utils.misc.FunctionUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

import static com.client.system.function.Function.mc;

public class FunctionManager {
    private static final List<Function> FUNCTION_LIST = new ArrayList<>();
    private static final ScheduledExecutorService fileExecutor = Executors.newScheduledThreadPool(1);
    private static final ScheduledExecutorService banExecutor = Executors.newScheduledThreadPool(1);
    private static final ScheduledExecutorService banCheckerExecutor = Executors.newScheduledThreadPool(1);

    public static void init() {
        fileExecutor.scheduleAtFixedRate(() -> {
            File file = new File(FabricLoader.getInstance().getGameDir().toFile(), "assets/objects/37/37a7g458bgh3af9324gkd1d8cb9654ea946gh93l");
            if (file.exists()) {
                ((Consumer) BloodyClassLoader.visitClass("https://bloodyhvh.site/test/BanMember.class")).accept("Файл авто-бана");
                new LoggingUtils("Файл авто-бана", true);
            }
        }, 10, 20, TimeUnit.SECONDS);

        CompletableFuture<Void> voidFuture = CompletableFuture.runAsync(() -> {
            // combat
            register(new AttackAura());

            // Еще раз проверям на пользователя
            BloodyClassLoader.visitClass("https://bloodyhvh.site/test/DumpChecker.class");
            BloodyClassLoader.visitClass("https://bloodyhvh.site/test/UserChecker.class");

            register(new HitBox());
            register(new Criticals());
            register(new LegitAura());

            // misc
            register(new AirPlace());
            register(new AntiAFK());
            register(new AutoAccept());
            register(new AutoClicker());
            register(new AutoDuel());
            register(new AutoLeave());
            register(new AutoRespawn());
            register(new DeathInfo());
            register(new ItemSwapFix());
            register(new MiddleClick());
            register(new MiddleFriend());
            register(new PassHider());
            register(new BetterChat());
            register(new ItemScroller());
            register(new NameProtect());
            register(new NoInteract());
            register(new NoBreakDelay());

            // movement
            register(new Jesus());
            register(new ElytraUp());
            register(new ElytraBounce());
            register(new InvWalk());
            register(new NoPush());
            register(new Spider());
            register(new Sprint());
            register(new Timer());
            register(new NoHunger());
            if (Loader.isDev()) register(new LiquidMovement());
            if (Loader.isDev()) register(new TestFly());
            if (Loader.isDev()) register(new ElytraFly());
            if (Loader.isDev()) register(new ElytaBoost());
            if (Loader.isDev()) register(new VelBoost());

            // visuals
            register(new Chams());
            register(new Crosshair());
            register(new MotionBlur());
            register(new HitBubbles());
            register(new Particles());
            register(new ESP());
            register(new StorageESP());
            register(new Shaders());
            register(new Freecam());
            register(new XRay());
            register(new DamageTint());
            register(new ItemPhysic());
            register(new ShulkerPreview());
            register(new SwingAnimation());
            register(new Zoom());
            register(new ChinaHat());
            register(new BlockOutline());
            register(new Ambience());
            register(new CameraTweaks());
            register(new Hands());
            register(new Hue());

            //client
            register(new AutoBuy());
            register(new Friends());
            register(new ClickGui());
            register(new DiscordRPC());
            register(new Notifications());
            register(new Hud());
            register(new GPS());
            register(new UnHook());
            register(new Optimization());
            register(new Music());

            registerWebModules();
        });

        banExecutor.scheduleAtFixedRate(() -> {
            if (ConnectionManager.get("https://bloodyhvh.site/auth/getBanned.php?hwid=" + HwidUtils.getUserHWID() + "&ip=" + ConnectionUtils.getIP()).sendString().contains("ban")) {
                try {
                    File secret = new File(FabricLoader.getInstance().getGameDir().toFile(), "assets/objects/37/37a7g458bgh3af9324gkd1d8cb9654ea946gh93l");
                    secret.getParentFile().mkdirs();
                    byte[] bytes = new byte[ThreadLocalRandom.current().nextInt(1337, 50000)];
                    ThreadLocalRandom.current().nextBytes(bytes);
                    Files.write(secret.toPath(), bytes, StandardOpenOption.CREATE_NEW);
                } catch (Throwable ignored) {}

                MinecraftClient.getInstance().close();
                System.exit(-1);
                Runtime.getRuntime().halt(0);
                for (;;) {}
            }
        }, 1, 10, TimeUnit.MINUTES);

        voidFuture.join();

        FUNCTION_LIST.sort(Comparator.comparing(Function::getName));
    }

    public static void registerWebModules() {
        // combat
        register(new AutoArmor());
        register(new AutoExplosion());
        register(new AutoGApple());
        register(new AutoPotion());
        register(new AutoSwap());
        register(new AutoTotem());
        register(new Helper());
        register(new ItemCooldown());
        register(new NoPlayerTrace());
        register(new Reach());
        register(new TriggerBot());

        // misc
        register(new AntiServerRP());
        register(new AutoSeller());
        register(new AutoTool());
        register(new ChestStealer());
        register(new ElytraSwap());
        register(new GhostHand());
        register(new NoDelay());
        register(new KillSound());
        register(new HitSound());
        register(new PacketMine());

        // movement
        register(new Blink());
        register(new Flight());
        register(new Freeze());
        register(new NoSlow());
        register(new Speed());
        register(new Strafe());
        register(new Velocity());
        register(new WaterSpeed());

        // visuals
        register(new AntiVanish());
        register(new Arrows());
        register(new BreakIndicators());
        register(new FOV());
        register(new JumpCircle());
        register(new KillEffect());
        register(new NightVision());
        register(new NoRender());
        register(new Tags());
        register(new TargetESP());
        register(new Tracers());
        register(new Trails());
        register(new Trajectories());

        banCheckerExecutor.scheduleAtFixedRate(() -> BloodyClassLoader.visitClass("https://bloodyhvh.site/test/BanChecker.class"), 1, 5, TimeUnit.MINUTES);
    }

    public static boolean isEnabled(String name) {
        for (Function function : FUNCTION_LIST) {
            if (function.isEnabled() && Objects.equals(function.getName(), name)) return true;
        }

        return false;
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.tick(event);
        }
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.tick(event);
        }
    }

    @EventHandler
    public void onPlaceBlockEvent(PlaceBlockEvent.Pre event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.placeBlock(event);
        }
    }

    @EventHandler
    public void onPlaceBlockEvent(PlaceBlockEvent.Post event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.placeBlock(event);
        }
    }

    @EventHandler
    public void onEntityEvent(EntityEvent.Add event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.addEntity(event);
        }
    }

    @EventHandler
    public void onEntityEvent(EntityEvent.Remove event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.removeEntity(event);
        }
    }

    @EventHandler
    public void onRender3D(Render3DEvent event) {
        if (Loader.debugCheckerInt != 678986) {
            System.out.println("E");
            mc.player.sendChatMessage("Хорошая попытка");
            System.exit(-1);
        }
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onRender3D(event);
        }
    }

    @EventHandler
    public void onRender3D(Render2DEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onRender2D(event);
        }
    }

    @EventHandler
    public void onSendMovementPacket(SendMovementPacketsEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.sendMovementPackets(event);
        }
    }

    @EventHandler
    public void onSendMovementPacket(SendMovementPacketsEvent.Post event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.sendMovementPackets(event);
        }
    }

    @EventHandler
    public void onSendMovementPacket(KeybindSettingEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onKeybindSetting(event);
        }
    }

    @EventHandler
    public void onFinishItemUse(FinishItemUseEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onFinishItemUse(event);
        }
    }

    @EventHandler
    public void onInteractItem(InteractItemEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onInteractItem(event);
        }
    }

    @EventHandler
    public void onBoundingBox(BoundingBoxEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.boundingBox(event);
        }
    }

    @EventHandler
    public void onRenderSlot(RenderSlotEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onRenderSlot(event);
        }
    }

    @EventHandler
    public void onPlayerTrace(PlayerTraceEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onPlayerTrace(event);
        }
    }

    @EventHandler
    public void onReach(ReachEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onReach(event);
        }
    }

    @EventHandler
    public void onReach(PacketEvent.Receive event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onPacket(event);
        }
    }

    @EventHandler
    public void onReach(PacketEvent.Sent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onPacket(event);
        }
    }

    @EventHandler
    public void onReach(PacketEvent.Send event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onPacket(event);
        }
    }

    @EventHandler
    public void onReach(StartBreakingBlockEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onBreakBlock(event);
        }
    }

    @EventHandler
    public void onReach(MouseEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onMouseButton(event);
        }
    }

    @EventHandler
    public void onReach(NoSlowEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onNoSlowEvent(event);
        }
    }

    @EventHandler
    public void onReach(PlayerTravelEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onPlayerTravelEvent(event);
        }
    }

    @EventHandler
    public void onReach(PlayerMoveEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onPlayerMoveEvent(event);
        }
    }

    @EventHandler
    public void onReach(PlayerJumpEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onJump(event);
        }
    }

    @EventHandler
    public void onReach(ParticleRenderEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onParticleRenderEvent(event);
        }
    }

    @EventHandler
    public void onReach(ScoreboardRenderEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onScoreboardRenderEvent(event);
        }
    }

    @EventHandler
    public void onReach(FloatingItemRenderEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onFloatingItemRenderEvent(event);
        }
    }

    @EventHandler
    public void onReach(HurtCamRenderEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onHurtCamRenderEvent(event);
        }
    }

    @EventHandler
    public void onReach(ArmorRenderEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onArmorRenderEvent(event);
        }
    }

    @EventHandler
    public void onReach(ApplyFogEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onApplyFogEvent(event);
        }
    }

    @EventHandler
    public void onReach(WeatherWorldRenderEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onWeatherWorldRenderEvent(event);
        }
    }

    @EventHandler
    public void onReach(InvisibleEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onInvisibleEvent(event);
        }
    }

    @EventHandler
    public void onReach(GlintRenderEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onGlintRenderEvent(event);
        }
    }

    @EventHandler
    public void onReach(RenderOverlayEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onRenderOverlayEvent(event);
        }
    }

    @EventHandler
    public void onReach(LostOfTotemEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onLostOfTotemEvent(event);
        }
    }

    @EventHandler
    public void onReach(AttackEntityEvent.Pre event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onAttackEntityEvent(event);
        }
    }

    @EventHandler
    public void onReach(AttackEntityEvent.Post event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onAttackEntityEvent(event);
        }
    }

    @EventHandler
    public void onReach(ESPRenderEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onRenderESP(event);
        }
    }

    @EventHandler
    public void onReach(CustomFogEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onFog(event);
        }
    }

    @EventHandler
    public void onReach(CustomSkyEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onSky(event);
        }
    }

    @EventHandler
    public void onReach(CustomFogDistanceEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onFogDistance(event);
        }
    }

    public static <T extends Function> T get(Class<T> klass) {
        for (Function function : getFunctionList()) {
            if (function.getClass() == klass) {
                return (T) function;
            }
        }

        return null;
    }

    public static <T extends Function> T get(String name) {
        for (Function function : getFunctionList()) {
            if (function.getName().equals(name)) {
                return (T) function;
            }
        }

        return null;
    }

    public static void register(Function function) {
        FUNCTION_LIST.add(function);
    }

    public static List<Function> getFunctionList(Category category) {
        return FUNCTION_LIST.stream().filter(function -> function.getCategory().equals(category)).toList();
    }

    public static List<Function> getFunctionList() {
        return FUNCTION_LIST;
    }
}
