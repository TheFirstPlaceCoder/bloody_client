package com.client.clickgui.autobuy;

import com.client.system.autobuy.HistoryItem;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static com.client.system.function.Function.mc;

public class HistoryItemButton {
    public double x, y, w, h;
    public HistoryItem historyItem;

    public HistoryItemButton(double x, double y, double w, double h, HistoryItem historyItem) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.historyItem = historyItem;
    }

    public void render(int mx, int my) {
        GL.drawRoundedGradientRect(new FloatRect(x + 4, y, w - 8, h), 3.5, ColorUtils.injectAlpha(Colors.getColor(0), 60), ColorUtils.injectAlpha(Colors.getColor(90), 60), ColorUtils.injectAlpha(Colors.getColor(270), (int) (60)), ColorUtils.injectAlpha(Colors.getColor(180), (int) (60)));
        GL.drawRoundedGradientOutline(new FloatRect(x + 4, y, w - 8, h), 3.5, 0.5d, ColorUtils.injectAlpha(Colors.getColor(0), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(90), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(270), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(180), (int) (255)));

        //GL.drawRoundedRect(x + 4, y, w - 8, h, 2, new Color(16, 15, 15, 255));
        IFont.drawCenteredY(IFont.MONTSERRAT_MEDIUM, clampName() + " | " + AutoBuyButton.replace(historyItem.price + "") + "$ | " + (historyItem.purchased ? "§aкуплено" : "§cне куплено"), (float) (x + 28), (float) (y + h / 2), Color.WHITE, 8);
        GL11.glPushMatrix();
        GL11.glTranslated(x + 8, y + h / 2 - 8, 0);
        mc.getItemRenderer().renderInGui(historyItem.stack, 0, 0);
        mc.getItemRenderer().renderGuiItemOverlay(mc.textRenderer, historyItem.stack, 0, 0);
        GL11.glPopMatrix();
    }

    public String clampName() {
        StringBuilder s = new StringBuilder();
        double tw = 0;

        for (char c : historyItem.stack.getName().getString().toCharArray()) {
            s.append(c);
            tw += IFont.getWidth(IFont.MONTSERRAT_MEDIUM, c + "", 8);
            if (tw > 50 && c != historyItem.stack.getName().getString().toCharArray()[historyItem.stack.getName().getString().toCharArray().length - 1]) {
                s.append("...");
                break;
            }
        }

        return s.toString();
    }
}
