package com.client.impl.hud;

import com.client.impl.command.StaffCommand;
import com.client.impl.function.client.Hud;
import com.client.system.function.FunctionManager;
import com.client.system.hud.HudFunction;
import com.client.system.hud.HudManager;
import com.client.system.textures.DownloadImage;
import com.client.utils.color.ColorUtils;
import com.client.utils.math.Pair;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.DrawMode;
import com.client.utils.render.ItemsColor;
import com.client.utils.render.TagUtils;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.main.TextureGL;
import com.client.utils.render.wisetree.render.render2d.utils.PlayerHeadTexture;
import com.client.utils.render.wisetree.render.render2d.utils.ScissorUtils;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StaffHud extends HudFunction {
    public StaffHud() {
        super(new FloatRect(248, 4, 100, 16), "Staff-Hud");
    }

    private static final List<String> STAFF_TAG = new ArrayList<>() {{
        add("сотрудник");
        add("стажер");
        add("стажёр");
        add("стaжeр");
        add("moder");
        add("модер");
        add("спектатор");
        add("spectator");
        add("админ");
        add("admin");
        add("youtube");
        add("ютубер");
        add("yt");
        add("ют");
        add("helper");
        add("хелпер");
        add("куратор");
        add("помощник");
        add("зам.куратора");
        add("гл.админ");
    }};

    public FloatRect staffRect = new FloatRect();

    @Override
    public void draw(float alpha) {
        staffRect.setX(rect.getX());
        staffRect.setW(rect.getW());
        staffRect.setY(rect.getY() + 20);

        List<Pair<Pair<String, String>, Pair<PlayerListEntry, Color>>> staffList = getStaffList();

        staffRect.setH(AnimationUtils.fast(staffRect.getH(), staffList.isEmpty() ? 0 : staffList.size() * 12 + 2));
        rect.setH(16 + staffRect.getH() + 2);

        drawNewClientRect(new FloatRect(rect.getX(), rect.getY(), rect.getW(), 16));

        postTask.add(() -> {
            HudManager.MB.begin(DrawMode.Triangles, VertexFormats.POSITION_COLOR_TEXTURE);
            HudManager.MB.texQuad(DownloadImage.getGlId(DownloadImage.STAFF), new TextureGL.TextureRegion(rect.getX() + 1, rect.getY(), 16, 16), Color.WHITE);
            HudManager.MB.end();
        });

        IFont.drawCenteredXY(IFont.MONTSERRAT_BOLD, "Staff Statistic", rect.getCenteredX(), rect.getY() + 8, Color.WHITE, 9);

        if (staffRect.getH() > 0) drawNewClientRect(staffRect);

        ScissorUtils.enableScissor(staffRect);
        float y = staffRect.getY() + 1;
        double w = 0;
        for (Pair<Pair<String, String>, Pair<PlayerListEntry, Color>> e : staffList) {
            IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, (e.getA().getA().toUpperCase()), rect.getX() + 16, y + (float) 12 / 2, inject(ColorUtils.injectAlpha(ItemsColor.getPlayerColor(e.getB().getA().getDisplayName()), 255), 1f), 7);
            IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, e.getA().getB(), rect.getX() + 16 + IFont.getWidth(IFont.MONTSERRAT_BOLD, e.getA().getA().toUpperCase() + " ", 7), y + (float) 12 / 2, inject(ColorUtils.injectAlpha(Color.WHITE, 255), 1f), 7);

            float finalY = y;
            postTask.add(() -> {
                HudManager.MB.begin(DrawMode.Triangles, VertexFormats.POSITION_COLOR_TEXTURE);
                HudManager.MB.texQuad(DownloadImage.getGlId(DownloadImage.GLOW_CIRCLE), new TextureGL.TextureRegion(staffRect.getX2() - 8, finalY + 12 / 2f, 12, 12), inject(e.getB().getB(), 1f));
                HudManager.MB.end();
            });
            //TextureGL.create().bind(DownloadImage.getGlId(DownloadImage.GLOW_CIRCLE)).draw(stack, new TextureGL.TextureRegion(12, 12), true, inject(e.getB().getB(), 1f));

            GL11.glPushMatrix();
            GL11.glScalef(1f, 1f, 1f);
            GL11.glTranslatef(staffRect.getX() + 8, y + (float) 12 / 2, 0);
            GL.drawRoundedTexture(PlayerHeadTexture.resolve(e.getB().getA().getProfile().getId()), -5, -5, 10, 10, 4);
            GL11.glPopMatrix();

            double tempW = 7 + IFont.getWidth(IFont.MONTSERRAT_BOLD, e.getA().getA().toUpperCase() + " " + e.getA().getB(), 7) + 16;
            if (tempW > w) {
                w = tempW;
            }
            y += 12;
        }
        if (w > 100) {
            w = w + 10;
        } else {
            w = 100;
        }
        rect.setW(AnimationUtils.fast(rect.getW().floatValue(), (float) w));
        ScissorUtils.disableScissor();
    }

    public static List<Pair<Pair<String, String>, Pair<PlayerListEntry, Color>>> getStaffList() {
        List<Pair<Pair<String, String>, Pair<PlayerListEntry, Color>>> list = new ArrayList<>();
        if (mc.isInSingleplayer() || mc.getCurrentServerEntry() == null) return list;

        for (PlayerListEntry player : mc.getNetworkHandler().getPlayerList()) {
            if (player.getDisplayName() == null) continue;

            String prefix = "";

            try {
                prefix = TagUtils.replace(TagUtils.replaceFormattings(player.getScoreboardTeam().getPrefix().getString())).toLowerCase().replace(" ", "");
            } catch (Exception ignored) {
            }

            Color color = switch (player.getGameMode()) {
                case NOT_SET -> Color.WHITE;
                case SURVIVAL, ADVENTURE -> Color.GREEN;
                case CREATIVE -> Color.YELLOW;
                case SPECTATOR -> Color.RED;
            };

            if (StaffCommand.staff.contains(player.getProfile().getName())) {
                list.add(new Pair<>(new Pair<>(prefix, player.getProfile().getName() + " *"), new Pair<>(player, color)));
                continue;
            }

            if (prefix.isEmpty()) continue;

            boolean next = true;
            for (String s : STAFF_TAG) {
                if (prefix.toLowerCase().contains(s.replace(" ", ""))) {
                    next = false;
                    break;
                }
            }

            if (next) continue;

            list.add(new Pair<>(new Pair<>(prefix.toLowerCase(), player.getProfile().getName()), new Pair<>(player, color)));
        }
        return list;
    }

    public static List<String> getStaffNicknames() {
        List<String> list = new ArrayList<>();
        if (mc.isInSingleplayer() || mc.getCurrentServerEntry() == null) return list;

        for (PlayerListEntry player : mc.getNetworkHandler().getPlayerList()) {
            if (player.getDisplayName() == null) continue;

            String prefix = "";

            try {
                prefix = TagUtils.replace(TagUtils.replaceFormattings(player.getScoreboardTeam().getPrefix().getString())).toLowerCase().replace(" ", "");
            } catch (Exception ignored) {
            }

            if (StaffCommand.staff.contains(player.getProfile().getName())) {
                list.add(player.getProfile().getName());
                continue;
            }

            if (prefix.isEmpty()) continue;

            boolean next = true;
            for (String s : STAFF_TAG) {
                if (prefix.toLowerCase().contains(s.replace(" ", ""))) {
                    next = false;
                    break;
                }
            }

            if (next) continue;

            list.add(player.getProfile().getName());
        }

        return list;
    }
}
