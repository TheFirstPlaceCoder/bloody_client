package com.client.system.function;

import api.interfaces.EventHandler;
import com.client.event.events.*;
import com.client.impl.function.client.*;
import com.client.impl.function.combat.*;
import com.client.impl.function.combat.aura.AttackAura;
import com.client.impl.function.combat.autoarmor.AutoArmor;
import com.client.impl.function.hud.*;
import com.client.impl.function.misc.*;
import com.client.impl.function.misc.auctionhelper.AuctionHelper;
import com.client.impl.function.misc.autoseller.AutoSeller;
import com.client.impl.function.misc.cheststealer.ChestStealer;
import com.client.impl.function.misc.nuker.Nuker;
import com.client.impl.function.movement.*;
import com.client.impl.function.movement.Timer;
import com.client.impl.function.movement.blink.Blink;
import com.client.impl.function.player.*;
import com.client.impl.function.visual.*;
import com.client.impl.function.visual.chinahat.ChinaHat;
import com.client.impl.function.visual.esp.ESP;
import com.client.impl.function.visual.hitbubbles.HitBubbles;
import com.client.impl.function.visual.jumpcircle.JumpCircle;
import com.client.impl.function.visual.particles.Particles;
import com.client.impl.function.visual.storageesp.StorageESP;
import com.client.impl.function.visual.trajectories.Trajectories;
import com.client.impl.function.visual.XRay;

import java.util.*;
import java.util.concurrent.*;

public class FunctionManager {
    private static final List<Function> FUNCTION_LIST = new ArrayList<>();

    public static void init() {

        CompletableFuture<Void> voidFuture = CompletableFuture.runAsync(() -> {
            //combat
            register(new AttackAura());
            register(new AntiBot());
            register(new AutoArmor());
            register(new AutoExplosion());
            register(new AutoGApple());
            register(new AutoPotion());
            register(new AutoSwap());
            register(new AutoTotem());
            register(new Criticals());
            register(new FastBow());
            register(new FastProjectile());
            register(new Helper());
            register(new HitBox());
            register(new ItemCooldown());
            register(new LegitAura());
            register(new NoPlayerTrace());
            register(new Reach());
            register(new TriggerBot());

            // misc
            register(new AuctionHelper());
            register(new AutoSeller());
            register(new ChestStealer());
            register(new Nuker());

            register(new AirPlace());
            register(new AntiAFK());
            register(new AntiServerRP());
            register(new AutoAccept());
            register(new AutoClicker());
            register(new AutoLeave());
            register(new BetterChat());
            register(new BetterTab());
            register(new DeathInfo());
            register(new HitSound());
            register(new ItemScroller());
            register(new ItemSwapFix());
            register(new KillSound());
            register(new MiddleFriend());
            register(new NameProtect());
            register(new NoBreakDelay());
            register(new PacketMine());
            register(new PassHider());
            register(new PingSpoof());
            register(new TPSSync());

            // player
            register(new AutoEat());
            register(new AutoFarm());
            register(new AutoFish());
            register(new AutoRespawn());
            register(new AutoTool());
            register(new ElytraHelper());
            register(new FastUse());
            register(new GhostHand());
            register(new GodBridge());
            register(new NoDelay());
            register(new NoInteract());
            register(new PortalGUI());
            register(new SpeedMine());

            // movement
            register(new Blink());
            register(new DamageBoost());
            register(new ElytraBounce());
            register(new ElytraFly());
            register(new Flight());
            register(new Freeze());
            register(new InvWalk());
            register(new NoPush());
            register(new NoSlow());
            register(new SafeWalk());
            register(new Speed());
            register(new Spider());
            register(new Sprint());
            register(new Strafe());
            register(new Timer());
            register(new Velocity());
            register(new WaterSpeed());

            // visual
            register(new Ambience());
            register(new AntiVanish());
            register(new Arrows());
            register(new BlockOutline());
            register(new BreakIndicators());
            register(new CameraTweaks());
            register(new Chams());
            register(new ChinaHat());
            register(new Crosshair());
            register(new DamageTint());
            register(new ESP());
            register(new FireFly());
            register(new FOV());
            register(new Freecam());
            register(new Fullbright());
            register(new Hands());
            register(new HitBubbles());
            register(new Hue());
            register(new ItemPhysic());
            register(new JumpCircle());
            register(new KillEffect());
            register(new MotionBlur());
            register(new NightVision());
            register(new NoRender());
            register(new Particles());
            register(new Shaders());
            register(new ShulkerPreview());
            register(new StorageESP());
            register(new SwingAnimation());
            register(new Tags());
            register(new TargetESP());
            register(new Tracers());
            register(new Trails());
            register(new Trajectories());
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
            //register(new TestModule());

            // hud
            register(new ArmorHud());
            register(new CooldownsHud());
            register(new InfoHud());
            register(new KeybindsHud());
            register(new ModulesHud());
            register(new MusicHud());
            register(new PotionsHud());
            register(new StaffHud());
            register(new TargetHud());
            register(new WatermarkHud());
        });

        voidFuture.join();

        FUNCTION_LIST.sort(Comparator.comparing(Function::getName));
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
