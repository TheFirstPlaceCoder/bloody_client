package com.client.clickgui.cheststealer.cheststealer;

import com.client.BloodyClient;
import com.client.clickgui.Impl;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import net.minecraft.item.Item;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class CsItemButton implements Impl {
    public Item item;
    public FloatRect data;
    public Runnable callback;
    public int priority;
    public float velocity;

    public CsItemButton(Item item, FloatRect data) {
        this.item = item;
        this.data = data;
        priority = 0;
    }

    @Override
    public void draw(double mouseX, double mouseY, float a) {
        GL.drawRoundedGradientRect(data, 3.5, ColorUtils.injectAlpha(Colors.getColor(0), 60), ColorUtils.injectAlpha(Colors.getColor(90), 60), ColorUtils.injectAlpha(Colors.getColor(270), (int) (60)), ColorUtils.injectAlpha(Colors.getColor(180), (int) (60)));
        GL.drawRoundedGradientOutline(data, 3.5, 0.5d, ColorUtils.injectAlpha(Colors.getColor(0), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(90), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(270), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(180), (int) (255)));

//        GL.drawGlow(data, 3, new Color(73, 73, 73, 255).darker());
//        GL.drawQuad(data, new Color(73, 73, 73, 255));
//        GL.drawOutlineQuad(data, new Color(73, 73, 73, 255).darker());
        GL11.glPushMatrix();
        GL11.glTranslated(data.getX() + 2, data.getCenteredY() - 8f, 0);
        BloodyClient.mc.getItemRenderer().renderInGui(item.getDefaultStack(), 0, 0);
        GL11.glPopMatrix();
        String s = item.getDefaultStack().getName().getString();
        FloatRect textData = new FloatRect(data.getX() + 22, data.getCenteredY() - IFont.getHeight(IFont.MONTSERRAT_BOLD, s, 8) / 2, IFont.getWidth(IFont.MONTSERRAT_BOLD, s, 8) + 6, IFont.getHeight(IFont.MONTSERRAT_BOLD, s, 8) + 1);
        GL.drawGlow(textData, 4, new Color(23, 22, 22, 255));
        GL.drawRoundedRect(textData, 1, new Color(23, 22, 22, 255));
        IFont.drawCenteredXY(IFont.MONTSERRAT_BOLD, s, textData.getCenteredX(), textData.getCenteredY(), Color.WHITE, 8);
        IFont.drawCenteredY(IFont.MONTSERRAT_MEDIUM, " + ", (float) (data.getX2() - 8 - IFont.getWidth(IFont.MONTSERRAT_MEDIUM, " + ", 8)), data.getCenteredY(), Color.GREEN, 8);
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

    @Override
    public void click(double mouseX, double mouseY, int button) {
        if (new FloatRect(data.getX2() - 32, data.getY(), 32, data.getH()).intersect(mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_1)
            callback.run();
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
