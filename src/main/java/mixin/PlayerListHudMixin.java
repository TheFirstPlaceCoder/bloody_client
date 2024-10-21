package mixin;

import com.client.interfaces.IPlayerListHud;
import com.client.utils.math.animation.impl.SmoothStepAnimation;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.Iterator;
import java.util.List;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin extends DrawableHelper implements IPlayerListHud {

    @Unique private final SmoothStepAnimation animation = new SmoothStepAnimation(500, 1);
    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private static Ordering<PlayerListEntry> ENTRY_ORDERING;

    @Shadow public abstract Text getPlayerName(PlayerListEntry entry);

    @Shadow private Text header;
    @Shadow private Text footer;

    @Shadow protected abstract void renderScoreboardObjective(ScoreboardObjective objective, int i, String string, int j, int k, PlayerListEntry entry, MatrixStack matrices);

    @Shadow protected abstract void renderLatencyIcon(MatrixStack matrices, int i, int j, int k, PlayerListEntry entry);

    @Override
    public SmoothStepAnimation getAnimation() {
        return animation;
    }

    /**
     * @author .
     * @reason .
     */
    @Overwrite
    public void render(MatrixStack matrices, int i, Scoreboard scoreboard, @Nullable ScoreboardObjective objective) {
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
        List<PlayerListEntry> list = ENTRY_ORDERING.sortedCopy(clientPlayNetworkHandler.getPlayerList());
        int j = 0;
        int k = 0;
        Iterator var9 = list.iterator();

        int l;
        while (var9.hasNext()) {
            PlayerListEntry playerListEntry = (PlayerListEntry) var9.next();
            l = this.client.textRenderer.getWidth(this.getPlayerName(playerListEntry));
            j = Math.max(j, l);
            if (objective != null && objective.getRenderType() != ScoreboardCriterion.RenderType.HEARTS) {
                l = this.client.textRenderer.getWidth(" " + scoreboard.getPlayerScore(playerListEntry.getProfile().getName(), objective).getScore());
                k = Math.max(k, l);
            }
        }

        list = list.subList(0, Math.min(list.size(), 80));
        int m = list.size();
        int n = m;

        for (l = 1; n > 20; n = (m + l - 1) / l) {
            ++l;
        }

        boolean bl = this.client.isInSingleplayer() || this.client.getNetworkHandler().getConnection().isEncrypted();
        int o;
        if (objective != null) {
            if (objective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
                o = 90;
            } else {
                o = k;
            }
        } else {
            o = 0;
        }

        int p = Math.min(l * ((bl ? 9 : 0) + j + o + 13), i - 50) / l;
        int q = i / 2 - (p * l + (l - 1) * 5) / 2;
        int r = 10;
        int s = p * l + (l - 1) * 5;
        List<OrderedText> list2 = null;
        if (this.header != null) {
            list2 = this.client.textRenderer.wrapLines(this.header, i - 50);

            OrderedText orderedText;
            for (Iterator var19 = list2.iterator(); var19.hasNext(); s = Math.max(s, this.client.textRenderer.getWidth(orderedText))) {
                orderedText = (OrderedText) var19.next();
            }
        }

        List<OrderedText> list3 = null;
        OrderedText orderedText2;
        Iterator var37;
        if (this.footer != null) {
            list3 = this.client.textRenderer.wrapLines(this.footer, i - 50);

            for (var37 = list3.iterator(); var37.hasNext(); s = Math.max(s, this.client.textRenderer.getWidth(orderedText2))) {
                orderedText2 = (OrderedText) var37.next();
            }
        }

        int var10001;
        int var10002;
        int var10003;
        int var10005;
        int t;
        if (list2 != null) {
            var10001 = i / 2 - s / 2 - 1;
            var10002 = r - 1;
            var10003 = i / 2 + s / 2 + 1;
            var10005 = list2.size();
            this.client.textRenderer.getClass();
            fill(matrices, var10001, var10002, var10003, r + var10005 * 9, Integer.MIN_VALUE);

            for (var37 = list2.iterator(); var37.hasNext(); r += 9) {
                orderedText2 = (OrderedText) var37.next();
                t = this.client.textRenderer.getWidth(orderedText2);
                this.client.textRenderer.drawWithShadow(matrices, orderedText2, (float) (i / 2 - t / 2), (float) r, -1);
                this.client.textRenderer.getClass();
            }

            ++r;
        }

        fill(matrices, i / 2 - s / 2 - 1, r - 1, i / 2 + s / 2 + 1, r + n * 9, Integer.MIN_VALUE);
        int u = this.client.options.getTextBackgroundColor(553648127);

        int w;
        for (int v = 0; v < m; ++v) {
            t = v / n;
            w = v % n;
            int x = q + t * p + t * 5;
            int y = r + w * 9;
            fill(matrices, x, y, x + p, y + 8, u);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableAlphaTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            if (v < list.size()) {
                PlayerListEntry playerListEntry2 = (PlayerListEntry) list.get(v);
                GameProfile gameProfile = playerListEntry2.getProfile();
                if (bl) {
                    PlayerEntity playerEntity = this.client.world.getPlayerByUuid(gameProfile.getId());
                    boolean bl2 = playerEntity != null && playerEntity.isPartVisible(PlayerModelPart.CAPE) && ("Dinnerbone".equals(gameProfile.getName()) || "Grumm".equals(gameProfile.getName()));
                    this.client.getTextureManager().bindTexture(playerListEntry2.getSkinTexture());
                    int z = 8 + (bl2 ? 8 : 0);
                    int aa = 8 * (bl2 ? -1 : 1);
                    DrawableHelper.drawTexture(matrices, x, y, 8, 8, 8.0F, (float) z, 8, aa, 64, 64);
                    if (playerEntity != null && playerEntity.isPartVisible(PlayerModelPart.HAT)) {
                        int ab = 8 + (bl2 ? 8 : 0);
                        int ac = 8 * (bl2 ? -1 : 1);
                        DrawableHelper.drawTexture(matrices, x, y, 8, 8, 40.0F, (float) ab, 8, ac, 64, 64);
                    }

                    x += 9;
                }

                this.client.textRenderer.drawWithShadow(matrices, this.getPlayerName(playerListEntry2), (float) x, (float) y, playerListEntry2.getGameMode() == GameMode.SPECTATOR ? -1862270977 : -1);
                if (objective != null && playerListEntry2.getGameMode() != GameMode.SPECTATOR) {
                    int ad = x + j + 1;
                    int ae = ad + o;
                    if (ae - ad > 5) {
                        this.renderScoreboardObjective(objective, y, gameProfile.getName(), ad, ae, playerListEntry2, matrices);
                    }
                }

                this.renderLatencyIcon(matrices, p, x - (bl ? 9 : 0), y, playerListEntry2);
            }
        }

        if (list3 != null) {
            r += n * 9 + 1;
            var10001 = i / 2 - s / 2 - 1;
            var10002 = r - 1;
            var10003 = i / 2 + s / 2 + 1;
            var10005 = list3.size();
            this.client.textRenderer.getClass();
            fill(matrices, var10001, var10002, var10003, r + var10005 * 9, Integer.MIN_VALUE);

            for (Iterator var40 = list3.iterator(); var40.hasNext(); r += 9) {
                OrderedText orderedText3 = (OrderedText) var40.next();
                w = this.client.textRenderer.getWidth(orderedText3);
                this.client.textRenderer.drawWithShadow(matrices, orderedText3, (float) (i / 2 - w / 2), (float) r, -1);
                this.client.textRenderer.getClass();
            }
        }
    }
}
