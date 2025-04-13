package com.client.impl.hud;

import com.client.system.function.FunctionManager;
import com.client.system.hud.HudFunction;
import com.client.utils.color.ColorUtils;
import com.client.utils.math.animation.Animation;
import com.client.utils.math.animation.Direction;
import com.client.utils.math.animation.impl.EaseBackIn;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ArmorHud extends HudFunction {
    public ArmorHud() {
        super(new FloatRect(209, 60, 68, 18), "Armor-Hud");
    }

    private com.client.impl.function.hud.ArmorHud armorHud;
    private boolean dir;
    private List<ItemStack> stacks = new ArrayList<>();

    private final Animation animation = new EaseBackIn(400, 1, 1);
    private double sc;

    @Override
    public void tick() {
        if (armorHud == null) armorHud = FunctionManager.get(com.client.impl.function.hud.ArmorHud.class);
        dir = rect.intersect(new FloatRect(-100, 0, 150, mc.getWindow().getHeight() / 2)) || rect.intersect(new FloatRect(mc.getWindow().getWidth() / 2 - 150, 0, 170, mc.getWindow().getHeight() / 2));
        stacks = getArmor();

        if (dir) {
            rect.setW(26F);
            rect.setH(22f * stacks.size() + 4f);
        } else {
            rect.setW(22f * stacks.size() + 4f);
            rect.setH(26F);
        }

        animation.setDirection(isAirList(stacks) && !(mc.currentScreen instanceof ChatScreen) ? Direction.BACKWARDS : Direction.FORWARDS);

        sc = animation.getOutput();
    }

    @Override
    public void draw(float a) {
        if (sc <= 0.0f) return;
        startScale(sc);

        drawRectArmor(rect, a, stacks.size(), dir);

        for (int i = 0; i < stacks.size(); i++) {
            ItemStack stack = stacks.get(i);
            int x = dir ? 0 : i * 22;
            int y = dir ? i * 22 : 0;
            mc.getItemRenderer().renderInGui(stack, rect.getX().intValue() + 5 + x, rect.getY().intValue() + 5 + y);
            mc.getItemRenderer().renderGuiItemOverlay(mc.textRenderer, stack, rect.getX().intValue() + 5 + x, rect.getY().intValue() + 5 + y);
        }

        endScale();
    }

    private List<ItemStack> getArmor() {
        List<ItemStack> itemStacks = new ArrayList<>();
        boolean empty = true;
        for (int i = 3; i > -1; i--) {
            ItemStack stack = mc.player.inventory.getArmorStack(i);
            itemStacks.add(stack);
            if (!stack.isEmpty()) {
                empty = false;
            }
        }
        if (mc.currentScreen instanceof ChatScreen && empty) {
            itemStacks.clear();
            itemStacks.add(Items.IRON_HELMET.getDefaultStack());
            itemStacks.add(Items.GOLDEN_CHESTPLATE.getDefaultStack());
            itemStacks.add(Items.LEATHER_LEGGINGS.getDefaultStack());
            itemStacks.add(Items.NETHERITE_BOOTS.getDefaultStack());
        }
        return itemStacks;
    }

    public boolean isAirList(List<ItemStack> list) {
        boolean a = true;

        for (ItemStack itemStack : list) {
            if (itemStack.getItem() != Items.AIR) {
                a = false;
                break;
            }
        }

        return a;
    }

    private void drawRectArmor(FloatRect rect, float a, int count, boolean dir) {
        drawNewClientRect(rect);
        for (int i = 0; i < count; i++) {
            int x = dir ? 0 : i * 22;
            int y = dir ? i * 22 : 0;
            GL.drawQuad(new FloatRect(rect.getX() + 4 + x, rect.getY() + 4 + y, 18, 18), ColorUtils.injectAlpha(Color.WHITE, (int) (50f * a)));
            GL.drawOutline(new FloatRect(rect.getX() + 4 + x, rect.getY() + 4 + y, 18, 18), 1, Color.WHITE);
        }
    }
}