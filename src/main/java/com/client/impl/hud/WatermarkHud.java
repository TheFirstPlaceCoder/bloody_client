package com.client.impl.hud;

import com.client.system.function.FunctionManager;
import com.client.system.hud.HudFunction;
import com.client.system.hud.HudManager;
import com.client.system.textures.DownloadImage;
import com.client.utils.Utils;
import com.client.utils.auth.Loader;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.DrawMode;
import com.client.utils.render.wisetree.font.api.FontRenderer;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.main.TextureGL;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Formatting;

import java.awt.*;

public class WatermarkHud extends HudFunction {
    public WatermarkHud() {
        super(new FloatRect(5, 5, 100, 10), "Watermark-Hud");
        draggable = false;
    }

    public FloatRect rect2, rect3, rect4;

    public com.client.impl.function.hud.WatermarkHud watermarkHud;

    @Override
    public void draw(float alpha) {
        if (watermarkHud == null) {
            watermarkHud = FunctionManager.get(com.client.impl.function.hud.WatermarkHud.class);
        }

        drawClientInfo();

        float lastY = rect.getY2();

        if (watermarkHud.getGroup()) {
            rect2 = new FloatRect(rect.getX().floatValue(),
                    lastY + 2f,
                    15 + 2 + IFont.getWidth(IFont.MONTSERRAT_BOLD, "Group: " + getRole(), 7) + 5,
                    IFont.getHeight(IFont.MONTSERRAT_BOLD, "AAA123", 8) + 5);

            drawNewClientRect(rect2);
            postTask.add(() -> {
                HudManager.MB.begin(DrawMode.Triangles, VertexFormats.POSITION_COLOR_TEXTURE);
                HudManager.MB.texQuad(DownloadImage.getGlId(DownloadImage.GROUP), new TextureGL.TextureRegion(rect2.getX() + 2.5f, rect2.getCenteredY() - 6.25f, 12.5f, 12.5f), Color.WHITE);
                HudManager.MB.end();
            });

            IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, "Group: " + getRole(), rect2.getX() + 15 + 2, rect2.getCenteredY(), Color.WHITE, 7);

            lastY = rect2.getY2();
        }

        if (watermarkHud.getPremium()) {
            rect3 = new FloatRect(rect.getX().floatValue(),
                    lastY + 2f,
                    15 + 2 + IFont.getWidth(IFont.MONTSERRAT_BOLD, "Premium: " + getPremium(), 7) + 5,
                    IFont.getHeight(IFont.MONTSERRAT_BOLD, "AAA123", 8) + 5);

            drawNewClientRect(rect3);
            postTask.add(() -> {
                HudManager.MB.begin(DrawMode.Triangles, VertexFormats.POSITION_COLOR_TEXTURE);
                HudManager.MB.texQuad(DownloadImage.getGlId(DownloadImage.STAR), new TextureGL.TextureRegion(rect3.getX() + 2.5f, rect3.getCenteredY() - 6.25f, 12.5f, 12.5f), Color.WHITE);
                HudManager.MB.end();
            });

            IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, "Premium: " + getPremium(), rect3.getX() + 15 + 2, rect3.getCenteredY(), Color.WHITE, 7);

            lastY = rect3.getY2();
        }

        if (watermarkHud.getIP()) {
            rect4 = new FloatRect(rect.getX().floatValue(),
                    lastY + 2f,
                    IFont.getWidth(IFont.MONTSERRAT_BOLD, "  IP: " + getServerIP() + "  ", 7) + 15,
                    IFont.getHeight(IFont.MONTSERRAT_BOLD, "  IP: " + getServerIP() + "  ", 8) + 5);

            drawNewClientRect(rect4);
            postTask.add(() -> {
                HudManager.MB.begin(DrawMode.Triangles, VertexFormats.POSITION_COLOR_TEXTURE);
                HudManager.MB.texQuad(DownloadImage.getGlId(DownloadImage.PLANET), new TextureGL.TextureRegion(rect4.getX() + 2.5f, rect4.getCenteredY() - 5, 10, 10), Color.WHITE);
                HudManager.MB.end();
            });
            IFont.drawCenteredXY(IFont.MONTSERRAT_BOLD, "IP: " + getServerIP(), rect4.getCenteredX() + 5, rect4.getCenteredY(), Color.WHITE, 7);
        }
    }

    public void drawClientInfo() {
        String clientName = "Bloody-Client";
        String accountName = Loader.getAccountName();
        String uid = "UID: " + Loader.getUID();

        float width = (IFont.getWidth(IFont.MONTSERRAT_BOLD, "  ", 7) * 2) + IFont.getWidth(IFont.MONTSERRAT_BOLD, clientName, 8) + getOffset(accountName, true) + getOffset(uid, false) + 3;

        rect.setW(width);
        rect.setH(IFont.getHeight(IFont.MONTSERRAT_BOLD, "ABC123", 8) + 5);
        drawNewClientRect(rect);

        FontRenderer.color(true);
        IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, clientName, rect.getX() + IFont.getWidth(IFont.MONTSERRAT_BOLD, "  ", 7), rect.getCenteredY(), Color.WHITE, 8);
        FontRenderer.color(false);

        if (watermarkHud.getAccountName()) {
            postTask.add(() -> {
                HudManager.MB.begin(DrawMode.Triangles, VertexFormats.POSITION_COLOR_TEXTURE);
                HudManager.MB.texQuad(DownloadImage.getGlId(DownloadImage.ACCOUNT), new TextureGL.TextureRegion(rect.getX() + IFont.getWidth(IFont.MONTSERRAT_BOLD, "  ", 7) + IFont.getWidth(IFont.MONTSERRAT_BOLD, clientName, 8) + 5, rect.getCenteredY() - 6.25f, 12.5f, 12.5f), Color.WHITE);
                HudManager.MB.end();
            });

            IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, accountName, rect.getX() + IFont.getWidth(IFont.MONTSERRAT_BOLD, "  ", 7) + IFont.getWidth(IFont.MONTSERRAT_BOLD, clientName, 8) + 5 + 12.5f + 2, rect.getCenteredY(), Color.WHITE, 7);
        }

        if (watermarkHud.getUid()) {
            postTask.add(() -> {
                HudManager.MB.begin(DrawMode.Triangles, VertexFormats.POSITION_COLOR_TEXTURE);
                HudManager.MB.texQuad(DownloadImage.getGlId(DownloadImage.COMPUTER), new TextureGL.TextureRegion(rect.getX() + IFont.getWidth(IFont.MONTSERRAT_BOLD, "  ", 7) + IFont.getWidth(IFont.MONTSERRAT_BOLD, clientName, 8) + getOffset(accountName, true) + 5, rect.getCenteredY() - 6.25f, 12.5f, 12.5f), Color.WHITE);
                HudManager.MB.end();
            });

            IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, uid, rect.getX() + IFont.getWidth(IFont.MONTSERRAT_BOLD, "  ", 7) + IFont.getWidth(IFont.MONTSERRAT_BOLD, clientName, 8) + getOffset(accountName, true) + 5 + 12.5f + 2, rect.getCenteredY(), Color.WHITE, 7);
        }
    }

    public String getPremium() {
        if (Loader.isPremium()) return Formatting.GREEN + (Utils.isRussianLanguage ? "Активен" : "Active");
        else return Formatting.RED + (Utils.isRussianLanguage ? "Неактивен" : "Not active");
    }

    public String getRole() {
        if (Loader.isDev()) return Formatting.RED + "Разработчик";
        if (Loader.isModer()) return Formatting.DARK_AQUA + "Модератор";
        if (Loader.isHelper()) return Formatting.GOLD + (Utils.isRussianLanguage ? "Хелпер" : "Helper");
        if (Loader.isYouTube()) return Formatting.WHITE + "You" + Formatting.DARK_RED + "Tube";
        return Formatting.WHITE + (Utils.isRussianLanguage ? "Пользователь" : "User");
    }

    public String getServerIP() {
        return ((!mc.isInSingleplayer() && mc.getCurrentServerEntry() != null) ? mc.getCurrentServerEntry().address : "Unknown");
    }

    public float getOffset(String str, boolean isName) {
        return isName ? (watermarkHud.getAccountName() ? 19.5f + IFont.getWidth(IFont.MONTSERRAT_BOLD, str, 7) : 0) : (watermarkHud.getUid() ? 19.5f + IFont.getWidth(IFont.MONTSERRAT_BOLD, str, 7) : 0);
    }
}
