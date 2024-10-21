package com.client.impl.hud;

import com.client.impl.function.client.Hud;
import com.client.system.function.FunctionManager;
import com.client.system.hud.HudFunction;
import com.client.utils.auth.Loader;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.api.FontRenderer;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.utils.PlayerHeadTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import mixin.accessor.MinecraftClientAccessor;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class WatermarkHud extends HudFunction {
    public WatermarkHud() {
        super(new FloatRect(5, 5, 100, 10), "Watermark-Hud");
        draggable = false;
    }

    public Identifier star = new Identifier("bloody-client", "hud/star.png");
    public Identifier group = new Identifier("bloody-client", "hud/group.png");
    public Identifier planet = new Identifier("bloody-client", "hud/planet.png");
    public Identifier account = new Identifier("bloody-client", "hud/account.png");
    public Identifier computer = new Identifier("bloody-client", "hud/computer.png");

    @Override
    public void draw(float alpha) {
        drawClientInfo();

        FloatRect rect2 = new FloatRect(rect.getX().floatValue(), rect.getY2() + 2f, 15 + 2 + IFont.getWidth(IFont.MONTSERRAT_BOLD, "Group: " + getRole(), 7) + 5, IFont.getHeight(IFont.MONTSERRAT_BOLD, "AAA123", 8) + 5);
        drawNewClientRect(rect2);
        GL11.glPushMatrix();
        GL11.glScalef(1f, 1f, 1f);
        GL11.glTranslatef(rect2.getX() + 2.5f, rect2.getCenteredY() - 6.25f, 0);
        GL.drawRoundedTexture(group, 0, 0, 12.5, 12.5, 0);
        GL11.glPopMatrix();

        IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, "Group: " + getRole(), rect2.getX() + 15 + 2, rect2.getCenteredY(), Color.WHITE, 7);

        FloatRect rect4 = new FloatRect(rect.getX().floatValue(), rect2.getY2() + 2f, 15 + 2 + IFont.getWidth(IFont.MONTSERRAT_BOLD, "Premium: " + getPremium(), 7) + 5, IFont.getHeight(IFont.MONTSERRAT_BOLD, "AAA123", 8) + 5);
        drawNewClientRect(rect4);
        GL11.glPushMatrix();
        GL11.glScalef(1f, 1f, 1f);
        GL11.glTranslatef(rect4.getX() + 2.5f, rect4.getCenteredY() - 6.25f, 0);
        GL.drawRoundedTexture(star, 0, 0, 12.5, 12.5, 0);
        GL11.glPopMatrix();

        IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, "Premium: " + getPremium(), rect4.getX() + 15 + 2, rect4.getCenteredY(), Color.WHITE, 7);

        //if (!mc.isInSingleplayer() && mc.getCurrentServerEntry() != null) {
        FloatRect rect3 = new FloatRect(rect.getX().floatValue(), rect4.getY2() + 2f, IFont.getWidth(IFont.MONTSERRAT_BOLD, "  IP: " + ((!mc.isInSingleplayer() && mc.getCurrentServerEntry() != null) ? mc.getCurrentServerEntry().address : "Unknown") + "  ", 7) + 15, IFont.getHeight(IFont.MONTSERRAT_BOLD, "  IP: " + ((!mc.isInSingleplayer() && mc.getCurrentServerEntry() != null) ? mc.getCurrentServerEntry().address : "Unknown") + "  ", 8) + 5);
        drawNewClientRect(rect3);
        GL11.glPushMatrix();
        GL11.glScalef(1f, 1f, 1f);
        GL11.glTranslatef(rect3.getX() + 2.5f, rect3.getCenteredY() - 5, 0);
        GL.drawRoundedTexture(planet, 0, 0, 10, 10, 0);
        GL11.glPopMatrix();
        IFont.drawCenteredXY(IFont.MONTSERRAT_BOLD, "IP: " + ((!mc.isInSingleplayer() && mc.getCurrentServerEntry() != null) ? mc.getCurrentServerEntry().address : "Unknown"), rect3.getCenteredX() + 5, rect3.getCenteredY(), Color.WHITE, 7);
        //}
    }

    public void drawClientInfo() {
        String clientName = "Bloody-Client";
        String accountName = Loader.getAccountName();
        String uid = "UID: " + Loader.getUID();

        float width = (IFont.getWidth(IFont.MONTSERRAT_BOLD, "  ", 7) * 2) + IFont.getWidth(IFont.MONTSERRAT_BOLD, clientName, 8) + 5 + 12.5f + 2 + IFont.getWidth(IFont.MONTSERRAT_BOLD, accountName, 7) + 5 + 12.5f + 2 + IFont.getWidth(IFont.MONTSERRAT_BOLD, uid, 7) + 3;

        rect.setW(width);
        rect.setH(IFont.getHeight(IFont.MONTSERRAT_BOLD, "ABC123", 8) + 5);
        drawNewClientRect(rect);

        FontRenderer.color(true);
        IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, clientName, rect.getX() + IFont.getWidth(IFont.MONTSERRAT_BOLD, "  ", 7), rect.getCenteredY(), Color.WHITE, 8);
        FontRenderer.color(false);

        GL11.glPushMatrix();
        GL11.glScalef(1f, 1f, 1f);
        GL11.glTranslatef(rect.getX() + IFont.getWidth(IFont.MONTSERRAT_BOLD, "  ", 7) + IFont.getWidth(IFont.MONTSERRAT_BOLD, clientName, 8) + 5, rect.getCenteredY() - 6.25f, 0);
        GL.drawRoundedTexture(account, 0, 0, 12.5, 12.5, 0);
        GL11.glPopMatrix();

        IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, accountName, rect.getX() + IFont.getWidth(IFont.MONTSERRAT_BOLD, "  ", 7) + IFont.getWidth(IFont.MONTSERRAT_BOLD, clientName, 8) + 5 + 12.5f + 2, rect.getCenteredY(), Color.WHITE, 7);

        GL11.glPushMatrix();
        GL11.glScalef(1f, 1f, 1f);
        GL11.glTranslatef(rect.getX() + IFont.getWidth(IFont.MONTSERRAT_BOLD, "  ", 7) + IFont.getWidth(IFont.MONTSERRAT_BOLD, clientName, 8) + 5 + 12.5f + 2 + IFont.getWidth(IFont.MONTSERRAT_BOLD, accountName, 7) + 5, rect.getCenteredY() - 6.25f, 0);
        GL.drawRoundedTexture(computer, 0, 0, 12.5, 12.5, 0);
        GL11.glPopMatrix();

        IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, uid, rect.getX() + IFont.getWidth(IFont.MONTSERRAT_BOLD, "  ", 7) + IFont.getWidth(IFont.MONTSERRAT_BOLD, clientName, 8) + 5 + 12.5f + 2 + IFont.getWidth(IFont.MONTSERRAT_BOLD, accountName, 7) + 5 + 12.5f + 2, rect.getCenteredY(), Color.WHITE, 7);
    }

    public String getPremium() {
        if (Loader.isPremium()) return Formatting.GREEN + "Активен";
        else return Formatting.RED + "Неактивен";
    }

    public String getRole() {
        if (Loader.isDev()) return Formatting.RED + "Разработчик";
        if (Loader.isModer()) return Formatting.DARK_AQUA + "Модератор";
        if (Loader.isHelper()) return Formatting.GOLD + "Хелпер";
        if (Loader.isYouTube()) return Formatting.WHITE + "You" + Formatting.DARK_RED + "Tube";
        return Formatting.WHITE + "Пользователь";
    }
}
