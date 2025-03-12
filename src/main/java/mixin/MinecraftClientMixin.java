package mixin;

import api.main.EventUtils;
import com.client.BloodyClient;
import com.client.clickgui.newgui.GuiScreen;
import com.client.clickgui.screens.ShaderScreen;
import com.client.event.events.GameEvent;
import com.client.event.events.ItemUseCrosshairTargetEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.client.Optimization;
import com.client.impl.function.movement.Timer;
import com.client.interfaces.IMinecraftClient;
import com.client.system.function.FunctionManager;
import com.client.utils.auth.Loader;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Option;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

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

    @Unique
    private Optimization optimization;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/String;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"))
    private String changeFps(String format, Object[] args) {
        if (optimization == null) optimization = FunctionManager.get(Optimization.class);

        return String.format("%d fps T: %s%s%s%s B: %d", (int) (currentFps * (optimization.isEnabled() ? 1.5 : this.currentScreen instanceof GuiScreen ? 1.25 : 1)), (double)this.options.maxFps == Option.FRAMERATE_LIMIT.getMax() ? "inf" : this.options.maxFps, this.options.enableVsync ? " vsync" : "", this.options.graphicsMode.toString(), this.options.cloudRenderMode == CloudRenderMode.OFF ? "" : (this.options.cloudRenderMode == CloudRenderMode.FAST ? " fast-clouds" : " fancy-clouds"), this.options.biomeBlendRadius);
    }

    @Inject(method = "reloadResources", at = @At("TAIL"))
    private void reloadResources(CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        BloodyClient.shaderManager.reloadShaders();
    }

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

    @ModifyExpressionValue(method = "doItemUse", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;crosshairTarget:Lnet/minecraft/util/hit/HitResult;", ordinal = 1))
    private HitResult doItemUseMinecraftClientCrosshairTargetProxy(HitResult original) {
        ItemUseCrosshairTargetEvent event = ItemUseCrosshairTargetEvent.get(original);
        EventUtils.post(event);
        return event.target;
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

    @Unique private Timer timer;

    @Inject(at = @At("HEAD"), method = "tick")
    private void onPreTick(CallbackInfo info) {
        if (options.gamma >= 1f) {
            options.gamma = 999f;
        }

        getProfiler().push("Tick_Pre");

        if (BloodyClient.canUpdate()) {
            if (timer == null) timer = FunctionManager.get(Timer.class);
            doItemUseCalled = false;
            timer.update();
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