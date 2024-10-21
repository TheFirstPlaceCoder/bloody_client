package com.client.clickgui.cheststealer.cheststealer;

import com.client.clickgui.Impl;
import com.client.clickgui.PrintBar;
import com.client.system.cheststealer.ChestStealerItem;
import com.client.system.cheststealer.ChestStealerManager;
import com.client.system.hud.HudFunction;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.utils.ScissorUtils;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AddItemWindow implements Impl {
    private final FloatRect data;
    private final List<CsItemButton> buttons = new ArrayList<>();
    private final PrintBar printBar;
    public Runnable callback, addCallback;

    public AddItemWindow(FloatRect data) {
        this.data = data;
        printBar = new PrintBar(new FloatRect(data.getX() + 3, data.getY() + 3, data.getW() - 6, 15), this::rebuild);
        rebuild();
    }

    private void rebuild() {
        buttons.clear();
        float y = data.getY() + 22;
        for (Item item : Registry.ITEM) {
            if (item.equals(Items.AIR)) continue;
            if (!printBar.isEmpty() && !item.getName().getString().toLowerCase().startsWith(printBar.getSearch().toLowerCase())) continue;
            CsItemButton button = new CsItemButton(item, new FloatRect(data.getX() + 3, y, data.getW() - 6, 18));
            button.callback = () -> {
                ChestStealerManager.add(new ChestStealerItem(button.item, button.priority));
                addCallback.run();
            };
            buttons.add(button);
            y += 20;
        }
    }

    @Override
    public void draw(double mouseX, double mouseY, float a) {
        HudFunction.drawRectGui(data, 1);
        //GL.drawGlow(data, 15, new Color(23, 22, 22, 255));
        //GL.drawRoundedRect(data, 4, new Color(23, 22, 22, 255));
        ScissorUtils.enableScissor(new FloatRect(data.getX().floatValue(), data.getY() + 21, data.getW().floatValue(), data.getH() - 22));
        for (CsItemButton button : buttons) {
            button.tick();
            if (button.data.getY() < data.getY() - 50 || button.data.getY2() > data.getY2() + 50) continue;
            button.draw(mouseX, mouseY, a);
        }
        ScissorUtils.disableScissor();
        printBar.draw(mouseX, mouseY, a);
    }

    @Override
    public void click(double mouseX, double mouseY, int button) {
        if (data.intersect(mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_3) {
            callback.run();
        }
        for (CsItemButton b  : buttons) {
            if (b.data.getY() < data.getY() + 22 || b.data.getY2() > data.getY2()) continue;
            b.click(mouseX, mouseY, button);
        }
        printBar.click(mouseX, mouseY, button);
    }

    @Override
    public void release(double mouseX, double mouseY, int button) {

    }

    @Override
    public void key(int key) {
        printBar.key(key);
    }

    @Override
    public void scroll(double screenX, double screenY, double amount) {
        if (new FloatRect(data.getX(), data.getY(), data.getW(), data.getH()).intersect(screenX, screenY)) {
            for (CsItemButton itemButton : buttons) {
                if (amount > 0) {
                    itemButton.velocity += 3;
                }
                if (amount < 0) {
                    itemButton.velocity -= 3;
                }
            }
        }
    }

    @Override
    public void symbol(char chr) {
        printBar.symbol(chr);
    }

    @Override
    public void close() {

    }
}
