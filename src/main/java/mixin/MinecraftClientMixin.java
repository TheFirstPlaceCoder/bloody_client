package mixin;

import api.main.EventUtils;
import com.client.BloodyClient;
import com.client.clickgui.screens.ShaderScreen;
import com.client.event.events.GameEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.movement.Timer;
import com.client.interfaces.IMinecraftClient;
import com.client.system.function.FunctionManager;
import com.client.system.hud.magnet.MagnetManager;
import com.client.utils.auth.Loader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.resource.ResourceReload;
import net.minecraft.text.Text;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin implements IMinecraftClient {
    @Shadow protected abstract void doItemUse();

    @Shadow protected abstract void doAttack();

    @Shadow
    public abstract Profiler getProfiler();

    @Shadow
    public abstract void updateWindowTitle();

    @Shadow private static int currentFps;

    @Shadow @Nullable public ClientWorld world;

    @Shadow @Final public GameOptions options;

    @Shadow @Nullable public ClientPlayerEntity player;
    @Unique private boolean doItemUseCalled;
    @Unique
    private boolean rightClick;
    @Shadow
    @Nullable
    public Screen currentScreen;
    @Shadow
    @Final
    public InGameHud inGameHud;
    @Shadow
    @Final
    public Mouse mouse;
    @Shadow
    @Final
    private Window window;
    @Shadow
    public boolean skipGameRender;
    @Shadow
    @Final
    private SoundManager soundManager;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(RunArgs args, CallbackInfo ci) {
        BloodyClient.onPostWindowInitialize();
    }

    @Inject(method = "isMultiplayerEnabled", at = @At("HEAD"), cancellable = true)
    private void isMultiplayerEnabled(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(method = "isOnlineChatEnabled", at = @At("HEAD"), cancellable = true)
    private void isOnlineChatEnabled(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(method = "onResolutionChanged", at = @At("TAIL"))
    private void onResolutionChanged(CallbackInfo ci) {
        MagnetManager.init();
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"), cancellable = true)
    private void onDisconnect(Screen screen, CallbackInfo info) {
        if (world != null) {
            GameEvent.Left left = new GameEvent.Left();
            left.post();
            if (left.isCancelled()) {
                info.cancel();
            }
        }
    }

    @Inject(method = "getGameVersion", at = @At("HEAD"), cancellable = true)
    void onGameVersion(CallbackInfoReturnable<String> cir) {
        if (Loader.unHook) {
            cir.setReturnValue("1.16.5");
        }
    }

    @Inject(method = "openScreen", at = @At("HEAD"), cancellable = true)
    public void openScreen(Screen screen, CallbackInfo ci) {
        if (!Loader.unHook) {
            if (this.currentScreen != null) {
                this.currentScreen.removed();
            }

            if (screen == null && this.world == null) {
                screen = ShaderScreen.getInstance();
            } else if (screen == null && this.player.isDead()) {
                if (this.player.showsDeathScreen()) {
                    screen = new DeathScreen((Text) null, this.world.getLevelProperties().isHardcore());
                } else {
                    this.player.requestRespawn();
                }
            }

            if (screen instanceof TitleScreen) screen = ShaderScreen.getInstance();

            if (screen instanceof ShaderScreen || screen instanceof MultiplayerScreen) {
                this.options.debugEnabled = false;
                this.inGameHud.getChatHud().clear(true);
            }

            this.currentScreen = (Screen) screen;
            if (screen != null) {
                this.mouse.unlockCursor();
                KeyBinding.unpressAll();
                ((Screen) screen).init((MinecraftClient) (Object) this, this.window.getScaledWidth(), this.window.getScaledHeight());
                this.skipGameRender = false;
                NarratorManager.INSTANCE.narrate(((Screen) screen).getNarrationMessage());
            } else {
                this.soundManager.resumeAll();
                this.mouse.lockCursor();
            }

            this.updateWindowTitle();

            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void onPreTick(CallbackInfo info) {
        if (options.gamma >= 1f) {
            options.gamma = 999f;
        }

        getProfiler().push("Tick_Pre");

        if (BloodyClient.canUpdate()) {
            doItemUseCalled = false;

            FunctionManager.get(Timer.class).update();
            EventUtils.post(TickEvent.Pre.get());

            if (rightClick && !doItemUseCalled) doItemUse();
            rightClick = false;
        }

        getProfiler().pop();
    }

    @Inject(method = "doItemUse", at = @At("HEAD"))
    private void onDoItemUse(CallbackInfo info) {
        doItemUseCalled = true;
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void onPostTick(CallbackInfo info) {
        getProfiler().push("Tick_Post");

        if (BloodyClient.canUpdate()) {
            EventUtils.post(TickEvent.Post.get());
        }

        getProfiler().pop();
    }

    @Override
    public int getFPS() {
        return currentFps;
    }

    @Override
    public void rightClick() {
        rightClick = true;
    }

    @Override
    public void attack() {
        doAttack();
    }
}