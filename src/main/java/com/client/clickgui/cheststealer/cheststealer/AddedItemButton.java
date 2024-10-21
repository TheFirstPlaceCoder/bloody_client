package com.client.clickgui.cheststealer.cheststealer;

import com.client.BloodyClient;
import com.client.clickgui.Impl;
import com.client.clickgui.autobuy.AutoBuyGui;
import com.client.impl.function.client.AutoBuy;
import com.client.impl.function.misc.cheststealer.ChestStealer;
import com.client.system.autobuy.AutoBuyManager;
import com.client.system.cheststealer.ChestStealerItem;
import com.client.system.cheststealer.ChestStealerManager;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class AddedItemButton implements Impl {
    public FloatRect data;
    public ChestStealerItem chestStealerItem;
    public Runnable callback;
    public float velocity;

    private FloatRect plusData, minusData;
    public double offset = 0;
    public int alpha;

    public AddedItemButton(FloatRect data, ChestStealerItem chestStealerItem) {
        this.data = data;
        this.chestStealerItem = chestStealerItem;
        buildData();
    }

    private void buildData() {
        float x = (float) (data.getX2() - 3);
        String a1 = " + ";
        String a2 = " - ";
        x -= IFont.getWidth(IFont.MONTSERRAT_BOLD, a1, 8);
        plusData = new FloatRect(x, data.getCenteredY() - IFont.getHeight(IFont.MONTSERRAT_BOLD, a1, 8) / 2, IFont.getWidth(IFont.MONTSERRAT_BOLD, a1, 8), IFont.getHeight(IFont.MONTSERRAT_BOLD, a1, 8) + 1);
        x -= IFont.getWidth(IFont.MONTSERRAT_BOLD, a2, 8) + 5;
        minusData = new FloatRect(x, data.getCenteredY() - IFont.getHeight(IFont.MONTSERRAT_BOLD, a2, 8) / 2, IFont.getWidth(IFont.MONTSERRAT_BOLD, a2, 8), IFont.getHeight(IFont.MONTSERRAT_BOLD, a2, 8) + 1);
    }

    @Override
    public void draw(double mouseX, double mouseY, float a) {
        if (AutoBuyGui.isHover(data.getX(), data.getY(), 32, 20, mouseX, mouseY)) {
            offset = AnimationUtils.fast(offset, 8);
            if (alpha < 255) alpha += 15;
        } else {
            if (alpha > 0) alpha -= 15;
            offset = AnimationUtils.fast(offset, 0);
        }

        buildData();
        Item item = chestStealerItem.item;
        GL.drawRoundedGradientRect(data, 3.5, ColorUtils.injectAlpha(Colors.getColor(0), 60), ColorUtils.injectAlpha(Colors.getColor(90), 60), ColorUtils.injectAlpha(Colors.getColor(270), (int) (60)), ColorUtils.injectAlpha(Colors.getColor(180), (int) (60)));
        GL.drawRoundedGradientOutline(data, 3.5, 0.5d, ColorUtils.injectAlpha(Colors.getColor(0), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(90), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(270), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(180), (int) (255)));
        if (alpha > 0) {
            IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, " - ", (float) (data.getX() + 2), (float) (data.getCenteredY()), new Color(255, 0, 0, MathHelper.clamp(alpha, 0, 255)), 8);
        }
//        GL.drawGlow(data, 3, new Color(73, 73, 73, 255).darker());
//        GL.drawQuad(data, new Color(73, 73, 73, 255));
//        GL.drawOutlineQuad(data, new Color(73, 73, 73, 255).darker());
        GL11.glPushMatrix();
        GL11.glTranslated(data.getX() + 2 + offset, data.getCenteredY() - 8f, 0);
        BloodyClient.mc.getItemRenderer().renderInGui(item.getDefaultStack(), 0, 0);
        GL11.glPopMatrix();
        String s = round(item.getDefaultStack().getName().getString());
        FloatRect textData = new FloatRect(data.getX() + 22 + offset, data.getCenteredY() - IFont.getHeight(IFont.MONTSERRAT_BOLD, s, 8) / 2, IFont.getWidth(IFont.MONTSERRAT_BOLD, s, 8) + 6, IFont.getHeight(IFont.MONTSERRAT_BOLD, s, 8) + 1);
        GL.drawGlow(textData, 4, new Color(23, 22, 22, 255));
        GL.drawRoundedRect(textData, 1, new Color(23, 22, 22, 255));
        IFont.drawCenteredXY(IFont.MONTSERRAT_BOLD, s, textData.getCenteredX(), textData.getCenteredY(), Color.WHITE, 8);
        drawPriority();
    }

    public void tick() {
        if (velocity > 0) {
            velocity--;
        }
        if (velocity < 0) {
            velocity++;
        }
        data.addY(velocity);
    }

    private void drawPriority() {
        String a1 = " + ";
        String a2 = " - ";
        String priority = " " + chestStealerItem.priority + " ";
        GL.drawGlow(plusData, 4, new Color(23, 22, 22, 255));
        GL.drawRoundedRect(plusData, 1, new Color(23, 22, 22, 255));
        GL.drawGlow(minusData, 4, new Color(23, 22, 22, 255));
        GL.drawRoundedRect(minusData, 1, new Color(23, 22, 22, 255));
        FloatRect priorityData = new FloatRect(minusData.getX() - (IFont.getWidth(IFont.MONTSERRAT_BOLD, priority, 8) + 5), data.getCenteredY() - IFont.getHeight(IFont.MONTSERRAT_BOLD, priority, 8) / 2, IFont.getWidth(IFont.MONTSERRAT_BOLD, priority, 8), IFont.getHeight(IFont.MONTSERRAT_BOLD, priority, 8) + 1);
        GL.drawGlow(priorityData, 4, new Color(23, 22, 22, 255));
        GL.drawRoundedRect(priorityData, 1, new Color(23, 22, 22, 255));
        IFont.drawCenteredXY(IFont.MONTSERRAT_BOLD, a1, plusData.getCenteredX(), plusData.getCenteredY(), Color.WHITE, 8);
        IFont.drawCenteredXY(IFont.MONTSERRAT_BOLD, a2, minusData.getCenteredX(), minusData.getCenteredY(), Color.WHITE, 8);
        IFont.drawCenteredXY(IFont.MONTSERRAT_BOLD, priority, priorityData.getCenteredX(), priorityData.getCenteredY(), Color.WHITE, 8);
    }

    private String round(String in) {
        int a = 0;
        StringBuilder s = new StringBuilder();
        for (char c : in.toCharArray()) {
            if (a > 16) {
                s.append("...");
                break;
            }
            s.append(c);
            a++;
        }
        return s.toString();
    }

    @Override
    public void click(double mouseX, double mouseY, int button) {
        if (AutoBuyGui.isHover(data.getX(), data.getY(), 32, 20, mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_1) {
            callback.run();
        }
        if (plusData.intersect(mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_1) {
            chestStealerItem.priority++;
        }
        if (minusData.intersect(mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_1 && chestStealerItem.priority > 0) {
            chestStealerItem.priority--;
        }
    }

    @Override
    public void release(double mouseX, double mouseY, int button) {

    }

    @Override
    public void key(int key) {

    }

    @Override
    public void scroll(double screenX, double screenY, double amount) {

    }

    @Override
    public void symbol(char chr) {

    }

    @Override
    public void close() {

    }
}
