package com.client.system.function;

import api.main.EventUtils;
import com.client.BloodyClient;
import com.client.event.events.*;
import com.client.impl.function.client.ClickGui;
import com.client.impl.function.client.Notifications;
import com.client.system.hud.HudManager;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.system.setting.settings.*;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.theme.ThemeSetting;
import com.client.utils.Utils;
import com.client.utils.files.SoundManager;
import com.client.utils.game.chat.ChatUtils;
import com.client.utils.misc.CustomSoundInstance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Formatting;

import java.util.Objects;

public abstract class Function {
    public static final MinecraftClient mc = BloodyClient.mc;
    private final String name, description;
    private final Category category;
    private int keyCode;

    private boolean isPremium;
    public boolean toggled = false;

    public Function(String name, Category category) {
        this(name, "", category);
    }

    public Function(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.keyCode = -1;
    }

    public boolean isEnabled() {
        return toggled;
    }

    public void toggle() {
        toggle(true);
    }

    public void toggle(boolean notification) {
        toggled = !toggled;

        HudManager.onToggle(new ToggleEvent(this));

        if (toggled) {
            on();
        } else {
            off();
        }

        if (notification) {
            notification();
        }
    }

    public String getHudPrefix() {
        return "";
    }

    private void notification() {
        if (canUpdate()) {
            if (FunctionManager.get(Notifications.class).isEnabled()) {
                if (!FunctionManager.get(Notifications.class).mode.get().equals("Chat"))
                    NotificationManager.add(new Notification(
                        isEnabled() ? NotificationType.ENABLE : NotificationType.DISABLE,
                        isEnabled() ? (Utils.isRussianLanguage ? (getName() + " был " + Formatting.GREEN + "включен" + Formatting.RESET + "!") : (getName() + " toggled " + Formatting.GREEN + "on" + Formatting.RESET + "!")) : (Utils.isRussianLanguage ? (getName() + " был " + Formatting.RED + "выключен" + Formatting.RESET + "!") : (getName() + " toggled " + Formatting.RED + "off" + Formatting.RESET + "!")), 1000L));

                if (!FunctionManager.get(Notifications.class).mode.get().equals("Notification"))
                    if (Utils.isRussianLanguage) ChatUtils.sendMsg(this.hashCode(), Formatting.GRAY, "Модуль (highlight)%s(default) %s(default).", name, isEnabled() ? Formatting.GREEN + "включен" : Formatting.RED + "выключен");
                    else ChatUtils.sendMsg(this.hashCode(), Formatting.GRAY, "(highlight)%s(default) %s(default).", name, "toggled " + (isEnabled() ? Formatting.GREEN + "включен" : Formatting.RED + "выключен"));
            }

            if (FunctionManager.get(ClickGui.class).clientSound.get()) {
                CustomSoundInstance customSoundInstance = new CustomSoundInstance(isEnabled() ? SoundManager.ENABLE_EVENT : SoundManager.DISABLE_EVENT, SoundCategory.MASTER);
                customSoundInstance.setVolume(FunctionManager.get(ClickGui.class).volume.floatValue());
                mc.getSoundManager().play(customSoundInstance);
            }
        }
    }

    private void on() {
        EventUtils.register(this);

        if (!canUpdate()) return;
        onEnable();
    }

    public void onEnable() {
    }

    private void off() {
        EventUtils.unregister(this);

        if (!canUpdate()) return;
        onDisable();
    }

    public void onDisable() {
    }

    public void tick(TickEvent.Pre event) {}
    public void tick(TickEvent.Post event) {}
    public void onGameJoinEvent(GameEvent.Join event) {}
    public void onGameLeftEvent(GameEvent.Left event) {}
    public void placeBlock(PlaceBlockEvent.Pre event) {}
    public void placeBlock(PlaceBlockEvent.Post event) {}
    public void addEntity(EntityEvent.Add event) {}
    public void removeEntity(EntityEvent.Remove event) {}
    public void onRender3D(Render3DEvent event) {}
    public void onRender2D(Render2DEvent event) {}
    public void sendMovementPackets(SendMovementPacketsEvent event) {}
    public void sendMovementPackets(SendMovementPacketsEvent.Post event) {}
    public void onKeybindSetting(KeybindSettingEvent event) {}
    public void onFinishItemUse(FinishItemUseEvent event) {}
    public void onInteractItem(InteractItemEvent event) {}
    public void boundingBox(BoundingBoxEvent event) {}
    public void onRenderSlot(RenderSlotEvent event) {}
    public void onPlayerTrace(PlayerTraceEvent event) {}
    public void onReach(ReachEvent event) {}
    public void onPacket(PacketEvent.Receive event) {}
    public void onPacket(PacketEvent.Send event) {}
    public void onPacket(PacketEvent.Sent event) {}
    public void onBreakBlock(StartBreakingBlockEvent event) {}
    public void onMouseButton(MouseEvent event) {}
    public void onNoSlowEvent(NoSlowEvent event) {}
    public void onPlayerTravelEvent(PlayerTravelEvent e) {}
    public void onPlayerMoveEvent(PlayerMoveEvent event) {}
    public void onJump(PlayerJumpEvent event) {}
    public void onParticleRenderEvent(ParticleRenderEvent event) {}
    public void onScoreboardRenderEvent(ScoreboardRenderEvent event) {}
    public void onFloatingItemRenderEvent(FloatingItemRenderEvent event) {}
    public void onHurtCamRenderEvent(HurtCamRenderEvent event) {}
    public void onArmorRenderEvent(ArmorRenderEvent event) {}
    public void onApplyFogEvent(ApplyFogEvent event) {}
    public void onWeatherWorldRenderEvent(WeatherWorldRenderEvent event) {}
    public void onInvisibleEvent(InvisibleEvent event) {}
    public void onGlintRenderEvent(GlintRenderEvent event) {}
    public void onRenderOverlayEvent(RenderOverlayEvent event) {}
    public void onLostOfTotemEvent(LostOfTotemEvent event) {}
    public void onAttackEntityEvent(AttackEntityEvent.Post event) {}
    public void onAttackEntityEvent(AttackEntityEvent.Pre event) {}
    public void renderEntity(RenderEntityEvent event) {}
    public void onRenderESP(ESPRenderEvent event) {}
    public void onFog(CustomFogEvent event) {}
    public void onSky(CustomSkyEvent event) {}
    public void onFogDistance(CustomFogDistanceEvent event) {}
    public void onSetBlockState(SetBlockStateEvent event) {}
    public void onPlayerUpdate(PlayerUpdateEvent e) {}
    public void onBlockState(BlockShapeEvent event) {}

    public BooleanSetting Boolean() {
        return new BooleanSetting(this);
    }

    public IntegerSetting Integer() {
        return new IntegerSetting(this);
    }

    public DoubleSetting Double() {
        return new DoubleSetting(this);
    }

    public ListSetting List() {
        return new ListSetting(this);
    }

    public ColorSetting Color() {
        return new ColorSetting(this);
    }

    public KeybindSetting Keybind() {
        return new KeybindSetting(this);
    }

    public MultiBooleanSetting MultiBoolean() {
        return new MultiBooleanSetting(this);
    }

    public Widget Widget() {
        return new Widget(this);
    }

    public StringSetting String() {
        return new StringSetting(this);
    }

    public ThemeSetting Theme() {
        return new ThemeSetting(this);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public boolean canUpdate() {
        return BloodyClient.canUpdate();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
