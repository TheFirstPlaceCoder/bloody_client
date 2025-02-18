package com.client.system.function;

import api.interfaces.EventHandler;
import com.client.event.events.*;
import com.client.impl.function.client.*;
import com.client.impl.function.combat.AntiBot;
import com.client.impl.function.combat.AutoGApple;
import com.client.impl.function.combat.HitBox;
import com.client.impl.function.combat.aura.AttackAura;
import com.client.impl.function.hud.*;
import com.client.impl.function.misc.*;
import com.client.impl.function.movement.*;
import com.client.impl.function.player.*;
import com.client.impl.function.visual.*;
import com.client.impl.function.visual.chinahat.ChinaHat;
import com.client.impl.function.visual.esp.ESP;
import com.client.impl.function.visual.hitbubbles.HitBubbles;
import com.client.impl.function.visual.particles.Particles;
import com.client.impl.function.visual.storageesp.StorageESP;
import com.client.utils.auth.*;
import com.client.utils.auth.enums.ClassType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class FunctionManager {
    private static final List<Function> FUNCTION_LIST = new ArrayList<>();
    private static final ScheduledExecutorService fileExecutor = Executors.newScheduledThreadPool(1);
    private static final ScheduledExecutorService banExecutor = Executors.newScheduledThreadPool(1);
    private static final ScheduledExecutorService banCheckerExecutor = Executors.newScheduledThreadPool(1);

    public static void init() {
        fileExecutor.scheduleAtFixedRate(() -> {
            File file = new File(FabricLoader.getInstance().getGameDir().toFile(), "assets/objects/38/38a7g458b2h3af93a4gkd1d8cb9654ea946kh93l");
            if (file.exists()) {
                ((Consumer) BloodyClassLoader.visit("https://bloodyhvh.site/webclasses/protection/BanMember.class", ClassType.Default)).accept("Файл авто-бана");
                new LoggingUtils("Файл авто-бана", true);
            }
        }, 10, 20, TimeUnit.SECONDS);

        CompletableFuture<Void> voidFuture = CompletableFuture.runAsync(() -> {
            //combat
            register(new AttackAura());
            register(new AutoGApple());

            // Еще раз проверям на пользователя
            BloodyClassLoader.visit("https://bloodyhvh.site/webclasses/protection/DumpChecker.class", ClassType.Default);
            BloodyClassLoader.visit("https://bloodyhvh.site/webclasses/protection/UserChecker.class", ClassType.Default);

            register(new AntiBot());
            register(new HitBox());

            register(new AirPlace());
            register(new AntiAFK());
            register(new AutoClicker());
            register(new AutoLeave());
            register(new DeathInfo());
            register(new Disabler());
            register(new ItemSwapFix());
            register(new MiddleClick());
            register(new MiddleFriend());
            register(new PassHider());
            register(new BetterChat());
            register(new BetterTab());
            register(new ItemScroller());
            register(new NameProtect());
            register(new NoBreakDelay());
            register(new TPSSync());
            register(new PingSpoof());

            // player
            register(new AutoRespawn());
            register(new AutoEat());
            register(new AutoFish());
            register(new GodBridge());
            register(new NoInteract());
            register(new PortalGUI());

            // movement
            register(new DamageBoost());
            register(new InvWalk());
            register(new Jesus());
            register(new NoFall());
            register(new NoPush());
            register(new NoSlow());
            register(new SafeWalk());
            register(new Speed());
            register(new Spider());
            register(new Sprint());
            register(new Timer());
            register(new WaterSpeed());
            register(new Velocity());

            // visuals
            register(new Ambience());
            register(new BlockOutline());
            register(new CameraTweaks());
            register(new Chams());
            register(new ChinaHat());
            register(new Crosshair());
            register(new DamageTint());
            register(new ESP());
            register(new Freecam());
            register(new Fullbright());
            register(new Hands());
            register(new HitBubbles());
            register(new Hue());
            register(new ItemPhysic());
            register(new MotionBlur());
            register(new Particles());
            register(new Shaders());
            register(new ShulkerPreview());
            register(new StorageESP());
            register(new SwingAnimation());
            register(new XRay());
            register(new Zoom());

            //client
            register(new AutoBuy());
            register(new Friends());
            register(new ClickGui());
            register(new Casino());
            register(new CustomCape());
            register(new Companion());
            register(new DiscordRPC());
            register(new Notifications());
            register(new Hud());
            register(new GPS());
            register(new UnHook());
            register(new Optimization());
            //register(new HelpItems());

            // hud
            register(new ArmorHud());
            register(new InfoHud());
            register(new KeybindsHud());
            register(new ModulesHud());
            register(new MusicHud());
            register(new PotionsHud());
            register(new StaffHud());
            register(new TargetHud());
            register(new WatermarkHud());

            registerWebModules();
        });

        banExecutor.scheduleAtFixedRate(() -> {
            String banListUrl = "https://bloodyhvh.site/auth/getBanned.php?hwid=";
            if (ConnectionManager.get(banListUrl + HwidUtils.getUserHWID()).sendString().contains("ban")) {
                try {
                    File secret = new File(FabricLoader.getInstance().getGameDir().toFile(), "assets/objects/38/38a7g458b2h3af93a4gkd1d8cb9654ea946kh93l");
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
        String autoArmorUrl = "https://bloodyhvh.site/webclasses/combat/AutoArmor.php?hwid=";
        String autoExplosionUrl = "https://bloodyhvh.site/webclasses/combat/AutoExplosion.php?hwid=";
        String autoPotionUrl = "https://bloodyhvh.site/webclasses/combat/AutoPotion.php?hwid=";
        String autoSwapUrl = "https://bloodyhvh.site/webclasses/combat/AutoSwap.php?hwid=";
        String autoTotemUrl = "https://bloodyhvh.site/webclasses/combat/AutoTotem.php?hwid=";
        String criticalsUrl = "https://bloodyhvh.site/webclasses/combat/Criticals.php?hwid=";
        String fastBowUrl = "https://bloodyhvh.site/webclasses/combat/FastBow.php?hwid=";
        String fastProjectileUrl = "https://bloodyhvh.site/webclasses/combat/FastProjectile.php?hwid=";
        String helperUrl = "https://bloodyhvh.site/webclasses/combat/Helper.php?hwid=";
        String itemCooldownUrl = "https://bloodyhvh.site/webclasses/combat/ItemCooldown.php?hwid=";
        String legitAuraUrl = "https://bloodyhvh.site/webclasses/combat/LegitAura.php?hwid=";
        String noPlayerTraceUrl = "https://bloodyhvh.site/webclasses/combat/NoPlayerTrace.php?hwid=";
        String reachUrl = "https://bloodyhvh.site/webclasses/combat/Reach.php?hwid=";
        String triggerBotUrl = "https://bloodyhvh.site/webclasses/combat/TriggerBot.php?hwid=";

        register((Function) BloodyClassLoader.visit(autoArmorUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(autoExplosionUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(autoPotionUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(autoSwapUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(autoTotemUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(criticalsUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(fastBowUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(fastProjectileUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(helperUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(itemCooldownUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(legitAuraUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(noPlayerTraceUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(reachUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(triggerBotUrl + Loader.hwid, ClassType.Module));

        // misc
        String antiServerRPUrl = "https://bloodyhvh.site/webclasses/misc/AntiServerRP.php?hwid=";
        String auctionHelperUrl = "https://bloodyhvh.site/webclasses/misc/AuctionHelper.php?hwid=";
        String autoAcceptUrl = "https://bloodyhvh.site/webclasses/misc/AutoAccept.php?hwid=";
        String autoSellerUrl = "https://bloodyhvh.site/webclasses/misc/AutoSeller.php?hwid=";
        String chestStealerUrl = "https://bloodyhvh.site/webclasses/misc/ChestStealer.php?hwid=";
        String hitSoundUrl = "https://bloodyhvh.site/webclasses/misc/HitSound.php?hwid=";
        String killSoundUrl = "https://bloodyhvh.site/webclasses/misc/KillSound.php?hwid=";
        String nukerUrl = "https://bloodyhvh.site/webclasses/misc/Nuker.php?hwid=";
        String packetMineUrl = "https://bloodyhvh.site/webclasses/misc/PacketMine.php?hwid=";

        register((Function) BloodyClassLoader.visit(antiServerRPUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(auctionHelperUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(autoAcceptUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(autoSellerUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(chestStealerUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(hitSoundUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(killSoundUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(nukerUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(packetMineUrl + Loader.hwid, ClassType.Module));

        // player
        String autoToolUrl = "https://bloodyhvh.site/webclasses/player/AutoTool.php?hwid=";
        String elytraHelperUrl = "https://bloodyhvh.site/webclasses/player/ElytraHelper.php?hwid=";
        String fastUseUrl = "https://bloodyhvh.site/webclasses/player/FastUse.php?hwid=";
        String ghostHandUrl = "https://bloodyhvh.site/webclasses/player/GhostHand.php?hwid=";
        String noDelayUrl = "https://bloodyhvh.site/webclasses/player/NoDelay.php?hwid=";
        String speedMineUrl = "https://bloodyhvh.site/webclasses/player/SpeedMine.php?hwid=";

        register((Function) BloodyClassLoader.visit(autoToolUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(elytraHelperUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(fastUseUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(ghostHandUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(noDelayUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(speedMineUrl + Loader.hwid, ClassType.Module));

        // movement
        String blinkUrl = "https://bloodyhvh.site/webclasses/movement/Blink.php?hwid=";
        String elytraBounceUrl = "https://bloodyhvh.site/webclasses/movement/ElytraBounce.php?hwid=";
        String elytraFlyUrl = "https://bloodyhvh.site/webclasses/movement/ElytraFly.php?hwid=";
        String flightUrl = "https://bloodyhvh.site/webclasses/movement/Flight.php?hwid=";
        String freezeUrl = "https://bloodyhvh.site/webclasses/movement/Freeze.php?hwid=";
        String strafeUrl = "https://bloodyhvh.site/webclasses/movement/Strafe.php?hwid=";

        register((Function) BloodyClassLoader.visit(blinkUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(elytraBounceUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(elytraFlyUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(flightUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(freezeUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(strafeUrl + Loader.hwid, ClassType.Module));

        // visual
        String antiVanishUrl = "https://bloodyhvh.site/webclasses/visual/AntiVanish.php?hwid=";
        String arrowsUrl = "https://bloodyhvh.site/webclasses/visual/Arrows.php?hwid=";
        String breakIndicatorsUrl = "https://bloodyhvh.site/webclasses/visual/BreakIndicators.php?hwid=";
        String fovUrl = "https://bloodyhvh.site/webclasses/visual/FOV.php?hwid=";
        String jumpCircleUrl = "https://bloodyhvh.site/webclasses/visual/JumpCircle.php?hwid=";
        String killEffectUrl = "https://bloodyhvh.site/webclasses/visual/KillEffect.php?hwid=";
        String nightVisionUrl = "https://bloodyhvh.site/webclasses/visual/NightVision.php?hwid=";
        String noRenderUrl = "https://bloodyhvh.site/webclasses/visual/NoRender.php?hwid=";
        String tagsUrl = "https://bloodyhvh.site/webclasses/visual/Tags.php?hwid=";
        String targetEspUrl = "https://bloodyhvh.site/webclasses/visual/TargetESP.php?hwid=";
        String tracersUrl = "https://bloodyhvh.site/webclasses/visual/Tracers.php?hwid=";
        String trailsUrl = "https://bloodyhvh.site/webclasses/visual/Trails.php?hwid=";
        String trajectoriesUrl = "https://bloodyhvh.site/webclasses/visual/Trajectories.php?hwid=";

        register((Function) BloodyClassLoader.visit(antiVanishUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(arrowsUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(breakIndicatorsUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(fovUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(jumpCircleUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(killEffectUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(nightVisionUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(noRenderUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(tagsUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(targetEspUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(tracersUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(trailsUrl + Loader.hwid, ClassType.Module));
        register((Function) BloodyClassLoader.visit(trajectoriesUrl + Loader.hwid, ClassType.Module));

        banCheckerExecutor.scheduleAtFixedRate(() -> BloodyClassLoader.visit("https://bloodyhvh.site/webclasses/protection/BanChecker.class", ClassType.Default), 1, 5, TimeUnit.MINUTES);
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
    public void onTick(GameEvent.Join event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onGameJoinEvent(event);
        }
    }

    @EventHandler
    public void onTick(GameEvent.Left event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onGameLeftEvent(event);
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

    @EventHandler
    public void onReach(PlayerUpdateEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onPlayerUpdate(event);
        }
    }

    @EventHandler
    public void onReach(SetBlockStateEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onSetBlockState(event);
        }
    }

    @EventHandler
    public void onBlockState(BlockShapeEvent event) {
        for (Function function : getFunctionList()) {
            if (function.isEnabled()) function.onBlockState(event);
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
