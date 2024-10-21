package mixin;

import com.client.BloodyClient;
import com.client.event.events.ESPRenderEvent;
import com.client.event.events.Render2DEvent;
import com.client.event.events.RenderOverlayEvent;
import com.client.event.events.ScoreboardRenderEvent;
import com.client.impl.function.client.Hud;
import com.client.impl.function.visual.DamageTint;
import com.client.interfaces.IInGameHud;
import com.client.system.function.FunctionManager;
import com.client.system.hud.HudFunction;
import com.client.system.hud.HudManager;
import com.client.system.notification.NotificationManager;
import com.client.utils.Utils;
import com.client.utils.auth.Loader;
import com.client.utils.game.entity.ServerUtils;
import com.client.utils.game.inventory.SlotUtils;
import com.client.utils.math.animation.Direction;
import com.client.utils.math.animation.impl.SmoothStepAnimation;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.client.BloodyClient.mc;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper implements IInGameHud {
    @Shadow
    @Final
    private static Identifier VIGNETTE_TEXTURE;

    @Shadow
    private int scaledWidth;
    @Shadow
    private int scaledHeight;

    @Shadow
    public abstract TextRenderer getFontRenderer();

    @Shadow
    protected abstract PlayerEntity getCameraPlayer();

    @Shadow
    private long heartJumpEndTick;
    @Shadow
    private int ticks;
    @Shadow
    private int lastHealthValue;
    @Shadow
    private long lastHealthCheckTime;
    @Shadow
    private int renderHealthValue;
    @Shadow
    @Final
    private Random random;

    @Shadow
    protected abstract LivingEntity getRiddenEntity();

    @Shadow
    protected abstract int getHeartCount(LivingEntity entity);

    @Shadow
    protected abstract int getHeartRows(int heartCount);

    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    @Final
    private PlayerListHud playerListHud;
    @Shadow @Final private static Identifier WIDGETS_TEXTURE;

    @Shadow protected abstract void renderHotbarItem(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack);

    @Shadow private int titleFadeInTicks;
    @Unique
    private final SmoothStepAnimation animation = new SmoothStepAnimation(300, 1);
    @Unique
    private float y = 0;

    @Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;interactionManager:Lnet/minecraft/client/network/ClientPlayerInteractionManager;", ordinal = 0))
    private void renderVignette(MatrixStack context, float tickDelta, CallbackInfo ci) {
        FunctionManager.get(DamageTint.class).draw();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderStatusEffectOverlay(Lnet/minecraft/client/util/math/MatrixStack;)V", shift = At.Shift.BEFORE))
    private void render(MatrixStack stack, float tickDelta, CallbackInfo ci) {
        if (BloodyClient.canUpdate()) {
            RenderSystem.pushMatrix();
            Utils.unscaledProjection();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.lineWidth(1);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            ESPRenderEvent espRenderEvent = new ESPRenderEvent(tickDelta);
            espRenderEvent.post();
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            RenderSystem.lineWidth(1);
            Utils.scaledProjection();
            RenderSystem.popMatrix();

            Render2DEvent event = new Render2DEvent(tickDelta);
            event.post();

            if (!Loader.unHook) {
                for (HudFunction hudFunction : HudManager.getHudFunctions()) {
                    if (!(mc.currentScreen instanceof ChatScreen)) {
                        boolean b = !FunctionManager.get(Hud.class).isEnabled() || !hudFunction.isEnabled();
                        Utils.rescaling(() -> {
                            if (!b) {
                                hudFunction.draw(1f);
                            } else {
                                hudFunction.alpha_anim.setDirection(Direction.BACKWARDS);
                                float a = hudFunction.getAlpha();
                                if (a > 0) hudFunction.draw(a);
                            }
                        });
                    }

                    Utils.rescaling(() -> {
                        hudFunction.drawPanel();
                    });
                }
            }

            if (!Loader.unHook) NotificationManager.draw();
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;popMatrix()V", ordinal = 4, shift = At.Shift.BEFORE), cancellable = true)
    private void drawTab(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
     //   ci.cancel();
     //   Scoreboard scoreboard = this.client.world.getScoreboard();
     //   ScoreboardObjective scoreboardObjective2 = scoreboard.getObjectiveForSlot(0);
     //   if (!this.client.options.keyPlayerList.isPressed() || this.client.isInSingleplayer() && this.client.player.networkHandler.getPlayerList().size() <= 1 && scoreboardObjective2 == null) {
     //       this.playerListHud.tick(false);
     //       ((IPlayerListHud) playerListHud).getAnimation().setDirection(Direction.FORWARDS);
     //   } else {
     //       this.playerListHud.tick(true);
     //       ((IPlayerListHud) playerListHud).getAnimation().setDirection(Direction.BACKWARDS);
     //   }
//
     //   if (((IPlayerListHud) playerListHud).getAnimation().getOutput() < 1) {
     //       this.playerListHud.render(matrices, this.scaledWidth, scoreboard, scoreboardObjective2);
     //   }
//
     //   RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
     //   RenderSystem.enableAlphaTest();
    }

    @Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true)
    private void renderPortalOverlay(float nauseaStrength, CallbackInfo ci) {
        RenderOverlayEvent event = new RenderOverlayEvent(RenderOverlayEvent.Type.PORTAL);
        event.post();
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderVignetteOverlay", at = @At("HEAD"), cancellable = true)
    private void renderVignetteOverlay(Entity entity, CallbackInfo ci) {
        RenderOverlayEvent event = new RenderOverlayEvent(RenderOverlayEvent.Type.VIGNETTE);
        event.post();
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderStatusEffectOverlay(CallbackInfo info) {
        RenderOverlayEvent event = new RenderOverlayEvent(RenderOverlayEvent.Type.EFFECTS);
        event.post();
        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(method = "renderPumpkinOverlay", at = @At(value = "HEAD"), cancellable = true)
    private void onRenderPumpkinOverlay(CallbackInfo ci) {
        RenderOverlayEvent event = new RenderOverlayEvent(RenderOverlayEvent.Type.PUMPKIN);
        event.post();
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void onRenderCrosshair(CallbackInfo ci) {
        RenderOverlayEvent event = new RenderOverlayEvent(RenderOverlayEvent.Type.CROSSHAIR);
        event.post();
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderHeldItemTooltip", at = @At("HEAD"), cancellable = true)
    private void onRenderHeldItemTooltip(CallbackInfo ci) {
        RenderOverlayEvent event = new RenderOverlayEvent(RenderOverlayEvent.Type.HELDITEMNAME);
        event.post();
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void renderScoreboardSidebar(MatrixStack matrices, ScoreboardObjective objective, CallbackInfo ci) {
        ScoreboardRenderEvent event = new ScoreboardRenderEvent();
        event.post();
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Unique
    protected void renderHotbar(float tickDelta, MatrixStack matrices) {
        PlayerEntity playerEntity = this.getCameraPlayer();
        if (playerEntity != null) {
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0, -y, 0);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.client.getTextureManager().bindTexture(WIDGETS_TEXTURE);
            ItemStack itemStack = playerEntity.getOffHandStack();
            Arm arm = playerEntity.getMainArm().getOpposite();
            int i = this.scaledWidth / 2;
            int j = this.getZOffset();
            this.setZOffset(-90);
            this.drawTexture(matrices, i - 91, this.scaledHeight - 22, 0, 0, 182, 22);
            this.drawTexture(matrices, i - 91 - 1 + playerEntity.inventory.selectedSlot * 20, this.scaledHeight - 22 - 1, 0, 22, 24, 22);
            if (!itemStack.isEmpty()) {
                if (arm == Arm.LEFT) {
                    this.drawTexture(matrices, i - 91 - 29, this.scaledHeight - 23, 24, 22, 29, 24);
                } else {
                    this.drawTexture(matrices, i + 91, this.scaledHeight - 23, 53, 22, 29, 24);
                }
            }

            this.setZOffset(j);
            RenderSystem.enableRescaleNormal();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            int m;
            int n;
            int o;
            for (m = 0; m < 9; ++m) {
                n = i - 90 + m * 20 + 2;
                o = this.scaledHeight - 16 - 3;
                this.renderHotbarItem(n, o, tickDelta, playerEntity, (ItemStack) playerEntity.inventory.main.get(m));
            }

            if (!itemStack.isEmpty()) {
                m = this.scaledHeight - 16 - 3;
                if (arm == Arm.LEFT) {
                    this.renderHotbarItem(i - 91 - 26, m, tickDelta, playerEntity, itemStack);
                } else {
                    this.renderHotbarItem(i + 91 + 10, m, tickDelta, playerEntity, itemStack);
                }
            }

            if (this.client.options.attackIndicator == AttackIndicator.HOTBAR) {
                float f = this.client.player.getAttackCooldownProgress(0.0F);
                if (f < 1.0F) {
                    n = this.scaledHeight - 20;
                    o = i + 91 + 6;
                    if (arm == Arm.RIGHT) {
                        o = i - 91 - 22;
                    }

                    this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
                    int p = (int) (f * 19.0F);
                    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                    this.drawTexture(matrices, o, n, 0, 94, 18, 18);
                    this.drawTexture(matrices, o, n + 18 - p, 18, 112 - p, 18, p);
                }
            }

            RenderSystem.disableRescaleNormal();
            RenderSystem.disableBlend();
            RenderSystem.popMatrix();
        }
    }

    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
     private void renderHotbar(float tickDelta, MatrixStack matrices, CallbackInfo ci) {
        if (client.currentScreen instanceof ChatScreen) {
            animation.setDirection(Direction.FORWARDS);
        } else {
            animation.setDirection(Direction.BACKWARDS);
        }

        y = (float) animation.getOutput() * 15;

        ci.cancel();

        if (!FunctionManager.get(Hud.class).drawHotbar() || Loader.unHook) {
            renderHotbar(titleFadeInTicks, matrices);
            return;
        }

        float w = 9 * 20;
        float x = (float) client.getWindow().getScaledWidth() / 2;
        float y = client.getWindow().getScaledHeight() - 22 - this.y;

        FloatRect rect = new FloatRect(x - w / 2, y, w, 20);
        HudFunction.drawRectHotbar(rect);

        boolean bl = !client.player.getOffHandStack().isEmpty();
        boolean right = client.player.getMainArm().equals(Arm.RIGHT);
        float xI = x - w / 2;
        float yI = y + 10;

        for (int i = 0; i < SlotUtils.HOTBAR_END + 1; i++) {
            ItemStack stack = client.player.inventory.getStack(i);
            boolean selected = i == client.player.inventory.selectedSlot;
            GL11.glPushMatrix();
            GL11.glTranslated(xI + 2, yI - 8, 0);
            if (selected) {
                GL.drawQuad(new FloatRect(0, 0, 16, 16), new Color(255, 255, 255, 50));
                GL.drawOutline(new FloatRect(0, 0, 16, 16), 3, new Color(255, 255, 255, 50));
            }
            client.getItemRenderer().renderInGui(stack, 0, 0);
            client.getItemRenderer().renderGuiItemOverlay(client.textRenderer, stack, 0, 0);
            GL11.glPopMatrix();
            xI += 20;
        }

        if (bl) {
            float xOffhand = right ? x - w / 2 - 30 : x + w / 2 + 10;
            FloatRect offhand = new FloatRect(xOffhand, y, 20, 20);
            HudFunction.drawRectHotbar(offhand);
            GL11.glPushMatrix();
            GL11.glTranslated(xOffhand + 2, y + 2, 0);
            client.getItemRenderer().renderInGui(client.player.getOffHandStack(), 0, 0);
            client.getItemRenderer().renderGuiItemOverlay(client.textRenderer, client.player.getOffHandStack(), 0, 0);
            GL11.glPopMatrix();
        }
    }

    /**
     * @author
     * @reason
     */
    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    public void renderExperienceBar(MatrixStack matrices, int x, CallbackInfo ci) {
        ci.cancel();
        GL11.glPushMatrix();
        GL11.glTranslated(0, -y, 0);
        this.client.getProfiler().push("expBar");
        this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
        int i = this.client.player.getNextLevelExperience();
        int k;
        int l;
        if (i > 0) {
            k = (int) (this.client.player.experienceProgress * 183.0F);
            l = this.scaledHeight - 32 + 3;
            this.drawTexture(matrices, x, l, 0, 64, 182, 5);
            if (k > 0) {
                this.drawTexture(matrices, x, l, 0, 69, k, 5);
            }
        }

        this.client.getProfiler().pop();
        if (this.client.player.experienceLevel > 0) {
            this.client.getProfiler().push("expLevel");
            String string = "" + this.client.player.experienceLevel;
            k = (int) ((this.scaledWidth - this.getFontRenderer().getWidth(string)) / 2);
            l = this.scaledHeight - 31 - 4;
            this.getFontRenderer().draw(matrices, string, (float) (k + 1), (float) l, 0);
            this.getFontRenderer().draw(matrices, string, (float) (k - 1), (float) l, 0);
            this.getFontRenderer().draw(matrices, string, (float) k, (float) (l + 1), 0);
            this.getFontRenderer().draw(matrices, string, (float) k, (float) (l - 1), 0);
            this.getFontRenderer().draw(matrices, string, (float) k, (float) l, 8453920);
            this.client.getProfiler().pop();
        }
        GL11.glPopMatrix();
    }

    /**
     * @author .
     * @reason .
     */
    @Overwrite
    private void renderScoreboardSidebar(MatrixStack matrices, ScoreboardObjective objective) {
        ScoreboardRenderEvent event = new ScoreboardRenderEvent();
        event.post();
        Scoreboard scoreboard = objective.getScoreboard();
        Collection<ScoreboardPlayerScore> collection = scoreboard.getAllPlayerScores(objective);
        java.util.List<ScoreboardPlayerScore> list = collection.stream().filter((scoreboardPlayerScorex) -> scoreboardPlayerScorex.getPlayerName() != null && !scoreboardPlayerScorex.getPlayerName().startsWith("#")).collect(Collectors.toList());
        if (list.size() > 15) {
            collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
        } else {
            collection = list;
        }
        List<Pair<ScoreboardPlayerScore, Text>> list2 = Lists.newArrayListWithCapacity(collection.size());
        Text text = objective.getDisplayName();
        int i = getFontRenderer().getWidth(text);
        int j = i;
        int k = getFontRenderer().getWidth(": ");
        ScoreboardPlayerScore scoreboardPlayerScore;
        MutableText text2;
        for (Iterator<?> var11 = ((Collection<?>) collection).iterator(); var11.hasNext(); j = Math.max(j, getFontRenderer().getWidth(text2) + k + getFontRenderer().getWidth(Integer.toString(scoreboardPlayerScore.getScore())))) {
            scoreboardPlayerScore = (ScoreboardPlayerScore) var11.next();
            Team team = scoreboard.getPlayerTeam(scoreboardPlayerScore.getPlayerName());
            text2 = Team.decorateName(team, new LiteralText(scoreboardPlayerScore.getPlayerName()));
            list2.add(Pair.of(scoreboardPlayerScore, text2));
        }
        int var10000 = collection.size();
        int l = var10000 * 9;
        int m = scaledHeight / 2 + l / 3;
        int o = scaledWidth - j - 3;
        int p = 0;
        int q = client.options.getTextBackgroundColor(0.3F);
        int r = client.options.getTextBackgroundColor(0.4F);
        for (Pair<ScoreboardPlayerScore, Text> scoreboardPlayerScoreTextPair : list2) {
            ++p;
            ScoreboardPlayerScore scoreboardPlayerScore2 = (ScoreboardPlayerScore) ((Pair<?, ?>) scoreboardPlayerScoreTextPair).getFirst();
            Text text3 = (Text) ((Pair<?, ?>) scoreboardPlayerScoreTextPair).getSecond();
            String string = Formatting.RED + "" + scoreboardPlayerScore2.getScore();
            if (ServerUtils.isHolyWorld()) {
                if (ServerUtils.checkServer(text3.getString())) {
                    try {
                        for (String s : ServerUtils.getIdList()) {
                            if (text3.getString().contains(s)) {
                                ServerUtils.setAnarchy(s);
                                break;
                            }
                        }
                        ServerUtils.setAnarchyID(Integer.parseInt(text3.getString().split("#")[1].split(" ")[0]));
                    } catch (Exception ignore) {
                    }
                }
                if (text3.getString().contains("Монеток:")) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (char c : text3.getString().toCharArray()) {
                        try {
                            stringBuilder.append(Integer.parseInt(String.valueOf(c)));
                        }catch (Exception ignore){
                        }
                    }
                    ServerUtils.setBalance(Integer.parseInt(stringBuilder.toString().replace(" ", "")));
                }
            }
            int t = m - p * 9;
            int u = this.scaledWidth - 3 + 2;
            int var10001 = o - 2;
            if (!event.isCancelled()) {
                fill(matrices, var10001, t, u, t + 9, q);
                this.getFontRenderer().draw(matrices, text3, (float) o, (float) t, -1);
                this.getFontRenderer().draw(matrices, string, (float) (u - this.getFontRenderer().getWidth(string)), (float) t, -1);
            }
            if (p == collection.size()) {
                var10001 = o - 2;
                if (!event.isCancelled()) {
                    fill(matrices, var10001, t - 9 - 1, u, t - 1, r);
                    fill(matrices, o - 2, t - 1, u, t, q);
                    TextRenderer var31 = this.getFontRenderer();
                    float var10003 = (float) (o + j / 2 - i / 2);
                    var31.draw(matrices, text, var10003, (float) (t - 9), -1);
                }
            }
        }
    }

    @Inject(method = "renderStatusBars", at = @At("HEAD"), cancellable = true)
    private void renderStatusBars(MatrixStack matrices, CallbackInfo ci) {
        ci.cancel();
        PlayerEntity playerEntity = this.getCameraPlayer();
        if (playerEntity != null) {
            GL11.glPushMatrix();
            GL11.glTranslated(0, -y, 0);
            int i = MathHelper.ceil(playerEntity.getHealth());
            boolean bl = this.heartJumpEndTick > (long) this.ticks && (this.heartJumpEndTick - (long) this.ticks) / 3L % 2L == 1L;
            long l = Util.getMeasuringTimeMs();
            if (i < this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
                this.lastHealthCheckTime = l;
                this.heartJumpEndTick = (long) (this.ticks + 20);
            } else if (i > this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
                this.lastHealthCheckTime = l;
                this.heartJumpEndTick = (long) (this.ticks + 10);
            }

            if (l - this.lastHealthCheckTime > 1000L) {
                this.lastHealthValue = i;
                this.renderHealthValue = i;
                this.lastHealthCheckTime = l;
            }

            this.lastHealthValue = i;
            int j = this.renderHealthValue;
            this.random.setSeed((long) (this.ticks * 312871));
            HungerManager hungerManager = playerEntity.getHungerManager();
            int k = hungerManager.getFoodLevel();
            int m = this.scaledWidth / 2 - 91;
            int n = this.scaledWidth / 2 + 91;
            int o = this.scaledHeight - 39;
            float f = (float) playerEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
            int p = MathHelper.ceil(playerEntity.getAbsorptionAmount());
            int q = MathHelper.ceil((f + (float) p) / 2.0F / 10.0F);
            int r = Math.max(10 - (q - 2), 3);
            int s = o - (q - 1) * r - 10;
            int t = o - 10;
            int u = p;
            int v = playerEntity.getArmor();
            int w = -1;
            if (playerEntity.hasStatusEffect(StatusEffects.REGENERATION)) {
                w = this.ticks % MathHelper.ceil(f + 5.0F);
            }

            this.client.getProfiler().push("armor");

            int x;
            int y;
            for (x = 0; x < 10; ++x) {
                if (v > 0) {
                    y = m + x * 8;
                    if (x * 2 + 1 < v) {
                        this.drawTexture(matrices, y, s, 34, 9, 9, 9);
                    }

                    if (x * 2 + 1 == v) {
                        this.drawTexture(matrices, y, s, 25, 9, 9, 9);
                    }

                    if (x * 2 + 1 > v) {
                        this.drawTexture(matrices, y, s, 16, 9, 9, 9);
                    }
                }
            }

            this.client.getProfiler().swap("health");

            int z;
            int aa;
            int ab;
            int ac;
            int ad;
            for (x = MathHelper.ceil((f + (float) p) / 2.0F) - 1; x >= 0; --x) {
                y = 16;
                if (playerEntity.hasStatusEffect(StatusEffects.POISON)) {
                    y += 36;
                } else if (playerEntity.hasStatusEffect(StatusEffects.WITHER)) {
                    y += 72;
                }

                z = 0;
                if (bl) {
                    z = 1;
                }

                aa = MathHelper.ceil((float) (x + 1) / 10.0F) - 1;
                ab = m + x % 10 * 8;
                ac = o - aa * r;
                if (i <= 4) {
                    ac += this.random.nextInt(2);
                }

                if (u <= 0 && x == w) {
                    ac -= 2;
                }

                ad = 0;
                if (playerEntity.world.getLevelProperties().isHardcore()) {
                    ad = 5;
                }

                this.drawTexture(matrices, ab, ac, 16 + z * 9, 9 * ad, 9, 9);
                if (bl) {
                    if (x * 2 + 1 < j) {
                        this.drawTexture(matrices, ab, ac, y + 54, 9 * ad, 9, 9);
                    }

                    if (x * 2 + 1 == j) {
                        this.drawTexture(matrices, ab, ac, y + 63, 9 * ad, 9, 9);
                    }
                }

                if (u > 0) {
                    if (u == p && p % 2 == 1) {
                        this.drawTexture(matrices, ab, ac, y + 153, 9 * ad, 9, 9);
                        --u;
                    } else {
                        this.drawTexture(matrices, ab, ac, y + 144, 9 * ad, 9, 9);
                        u -= 2;
                    }
                } else {
                    if (x * 2 + 1 < i) {
                        this.drawTexture(matrices, ab, ac, y + 36, 9 * ad, 9, 9);
                    }

                    if (x * 2 + 1 == i) {
                        this.drawTexture(matrices, ab, ac, y + 45, 9 * ad, 9, 9);
                    }
                }
            }

            LivingEntity livingEntity = this.getRiddenEntity();
            y = this.getHeartCount(livingEntity);
            if (y == 0) {
                this.client.getProfiler().swap("food");

                for (z = 0; z < 10; ++z) {
                    aa = o;
                    ab = 16;
                    ac = 0;
                    if (playerEntity.hasStatusEffect(StatusEffects.HUNGER)) {
                        ab += 36;
                        ac = 13;
                    }

                    if (playerEntity.getHungerManager().getSaturationLevel() <= 0.0F && this.ticks % (k * 3 + 1) == 0) {
                        aa += this.random.nextInt(3) - 1;
                    }

                    ad = n - z * 8 - 9;
                    this.drawTexture(matrices, ad, aa, 16 + ac * 9, 27, 9, 9);
                    if (z * 2 + 1 < k) {
                        this.drawTexture(matrices, ad, aa, ab + 36, 27, 9, 9);
                    }

                    if (z * 2 + 1 == k) {
                        this.drawTexture(matrices, ad, aa, ab + 45, 27, 9, 9);
                    }
                }

                t -= 10;
            }

            this.client.getProfiler().swap("air");
            z = playerEntity.getMaxAir();
            aa = Math.min(playerEntity.getAir(), z);
            if (playerEntity.isSubmergedIn(FluidTags.WATER) || aa < z) {
                ab = this.getHeartRows(y) - 1;
                t -= ab * 10;
                ac = MathHelper.ceil((double) (aa - 2) * 10.0 / (double) z);
                ad = MathHelper.ceil((double) aa * 10.0 / (double) z) - ac;

                for (int ae = 0; ae < ac + ad; ++ae) {
                    if (ae < ac) {
                        this.drawTexture(matrices, n - ae * 8 - 9, t, 16, 18, 9, 9);
                    } else {
                        this.drawTexture(matrices, n - ae * 8 - 9, t, 25, 18, 9, 9);
                    }
                }
            }

            this.client.getProfiler().pop();
            GL11.glPopMatrix();
        }
    }

    @Override
    public Identifier getVignette() {
        return VIGNETTE_TEXTURE;
    }
}
