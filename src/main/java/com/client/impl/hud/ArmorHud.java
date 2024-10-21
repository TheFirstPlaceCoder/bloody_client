package com.client.impl.hud;

import com.client.system.hud.HudFunction;
import com.client.system.hud.setting.HudValue;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.utils.ScissorUtils;
import com.client.utils.render.wisetree.render.render3d.Renderer3D;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ArmorHud extends HudFunction {
    public ArmorHud() {
        super(new FloatRect(209, 60, 68, 18), "Armor-Hud");
    }

    private final HudValue mainhand = create("Основная рука", false);
    private final HudValue offhand = create("Левая рука", false);

    @Override
    public void draw(float a) {
        boolean dir = rect.intersect(new FloatRect(-100, 0, 150, mc.getWindow().getHeight() / 2)) || rect.intersect(new FloatRect(mc.getWindow().getWidth() / 2 - 150, 0, 170, mc.getWindow().getHeight() / 2));

        List<ItemStack> stacks = getArmor();

        if (dir) {
            rect.setW(26F);
            rect.setH(22f * stacks.size() + 4f);
        } else {
            rect.setW(22f * stacks.size() + 4f);
            rect.setH(26F);
        }

        drawRectArmor(rect, a, stacks.size(), dir);

        if (a < 1) return;

        ScissorUtils.enableScissor(rect);

        int x = 0, y = 0;

        for (ItemStack stack : stacks) {
            GL11.glPushMatrix();
            GL11.glTranslated(rect.getX(), rect.getY(), 0);
            mc.getItemRenderer().renderInGui(stack, 5 + x, 5 + y);
            mc.getItemRenderer().renderGuiItemOverlay(mc.textRenderer, stack, 5 + x, 5 + y);
            GL11.glPopMatrix();
            if (dir) y += 22;
            else x += 22;
        }

        ScissorUtils.disableScissor();
    }

    private List<ItemStack> getArmor() {
        List<ItemStack> itemStacks = new ArrayList<>();
        boolean empty = true;

        if (mainhand.get()) {
            itemStacks.add(mc.player.getMainHandStack());
            if (!mc.player.getMainHandStack().isEmpty()) {
                empty = false;
            }
        }

        for (int i = 3; i > -1; i--) {
            ItemStack stack = mc.player.inventory.getArmorStack(i);
            itemStacks.add(stack);
            if (!stack.isEmpty()) {
                empty = false;
            }
        }

        if (offhand.get()) {
            itemStacks.add(mc.player.getOffHandStack());
            if (!mc.player.getOffHandStack().isEmpty()) {
                empty = false;
            }
        }

        if (mc.currentScreen instanceof ChatScreen && empty) {
            itemStacks.clear();
            if (mainhand.get())
                itemStacks.add(Items.DIAMOND_SWORD.getDefaultStack());
            itemStacks.add(Items.IRON_HELMET.getDefaultStack());
            itemStacks.add(Items.GOLDEN_CHESTPLATE.getDefaultStack());
            itemStacks.add(Items.LEATHER_LEGGINGS.getDefaultStack());
            itemStacks.add(Items.NETHERITE_BOOTS.getDefaultStack());
            if (offhand.get())
                itemStacks.add(Items.TOTEM_OF_UNDYING.getDefaultStack());
        }

        return itemStacks;
    }

    public void drawRectArmor(FloatRect rect, float a, int count, boolean dir) {
        drawNewClientRect(rect);

        int x = 0, y = 0;

        for (int i = 0; i < count; i++) {
            GL.drawQuad(new FloatRect(rect.getX() + 4 + x, rect.getY() + 4 + y, 18, 18),
                    ColorUtils.injectAlpha(Color.WHITE, (int) (50f * a))
            );

            GL.drawOutline(new FloatRect(rect.getX() + 4 + x, rect.getY() + 4 + y, 18, 18),
                    1,
                    Color.WHITE
            );

            if (dir) y += 22;
            else x += 22;
        }
    }
}
