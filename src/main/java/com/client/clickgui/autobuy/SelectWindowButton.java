package com.client.clickgui.autobuy;

import com.client.impl.function.client.AutoBuy;
import com.client.system.autobuy.AutoBuyItem;
import com.client.system.autobuy.AutoBuyManager;
import com.client.system.autobuy.CustomAutoBuyItem;
import com.client.system.autobuy.DefaultAutoBuyItem;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import net.minecraft.item.Item;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Objects;

import static com.client.system.function.Function.mc;


public class SelectWindowButton {
    public double x, y, w, h;
    public Item item;
    public String name;
    public CustomAutoBuyItem customAutoBuyItem = null;

    public SelectWindowButton(double x, double y, double w, double h, Item item, String name, CustomAutoBuyItem customAutoBuyItem) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.item = item;
        this.name = name;
        this.customAutoBuyItem = customAutoBuyItem;
    }

    public void render(double mx, double my) {
        GL.drawRoundedGradientRect(new FloatRect(x + 4, y, w - 8, h), 3.5, ColorUtils.injectAlpha(Colors.getColor(0), 60), ColorUtils.injectAlpha(Colors.getColor(90), 60), ColorUtils.injectAlpha(Colors.getColor(270), (int) (60)), ColorUtils.injectAlpha(Colors.getColor(180), (int) (60)));
        GL.drawRoundedGradientOutline(new FloatRect(x + 4, y, w - 8, h), 3.5, 0.5d, ColorUtils.injectAlpha(Colors.getColor(0), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(90), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(270), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(180), (int) (255)));

        //GL.drawRoundedRect(x + 4, y, w - 8, h, 2, new Color(16, 15, 15, 255));
        IFont.drawCenteredY(IFont.MONTSERRAT_MEDIUM, name, (float) (x + 28), (float) (y + h / 2), Color.WHITE, 8);
        IFont.drawCenteredY(IFont.MONTSERRAT_MEDIUM, " + ", (float) (x + w - 8 - IFont.getWidth(IFont.MONTSERRAT_MEDIUM, " + ", 8)), (float) (y + h / 2), Color.GREEN, 8);
        GL11.glPushMatrix();
        GL11.glTranslated(x + 8, y + h / 2 - 8, 0);
        mc.getItemRenderer().renderInGui(item.getDefaultStack(), 0, 0);
        GL11.glPopMatrix();
    }

    public void click(double mx, double my, int b) {
        if (AutoBuyGui.isHover(x + w - 32, y, 32, h, mx, my) && b == GLFW.GLFW_MOUSE_BUTTON_1) {
            AutoBuyItem newItem;
            boolean shouldAdd = false;
            if (customAutoBuyItem != null) {
                newItem = new CustomAutoBuyItem(item, 1000, customAutoBuyItem.isFTItem);
                ((CustomAutoBuyItem) newItem).name = customAutoBuyItem.name;
                ((CustomAutoBuyItem) newItem).strings = customAutoBuyItem.strings;
                ((CustomAutoBuyItem) newItem).enchantments = customAutoBuyItem.enchantments;
                ((CustomAutoBuyItem) newItem).strictCheck = customAutoBuyItem.strictCheck;
                shouldAdd = AutoBuy.abGui.autoBuyButtons.stream().filter(e -> e.autoBuyItem instanceof CustomAutoBuyItem).noneMatch(e ->
                                ((CustomAutoBuyItem) e.autoBuyItem).name.equals(((CustomAutoBuyItem) newItem).name)
                                        && ((CustomAutoBuyItem) e.autoBuyItem).strings == ((CustomAutoBuyItem) newItem).strings
                                        && ((CustomAutoBuyItem) e.autoBuyItem).enchantments == ((CustomAutoBuyItem) newItem).enchantments
                                        && ((CustomAutoBuyItem) e.autoBuyItem).strictCheck == ((CustomAutoBuyItem) newItem).strictCheck
                        );
            } else {
                newItem = new DefaultAutoBuyItem(item, 1000);
                shouldAdd = AutoBuy.abGui.autoBuyButtons.stream().filter(e -> e.autoBuyItem instanceof DefaultAutoBuyItem).noneMatch(e -> e.autoBuyItem.item == newItem.item);
            }

            if (shouldAdd) {
                AutoBuyManager.addItem(newItem);
                AutoBuy.abGui.addItem(newItem);
            }
        }
    }
}