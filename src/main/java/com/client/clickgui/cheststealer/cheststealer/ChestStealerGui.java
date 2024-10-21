package com.client.clickgui.cheststealer.cheststealer;

import com.client.BloodyClient;
import com.client.system.cheststealer.ChestStealerItem;
import com.client.system.cheststealer.ChestStealerManager;
import com.client.system.hud.HudFunction;
import com.client.utils.Utils;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.utils.ScissorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.concurrent.ConcurrentLinkedDeque;

import static com.client.system.function.Function.mc;

public class ChestStealerGui extends Screen {
    private static ChestStealerGui instance;

    public static ChestStealerGui getInstance() {
        if (instance == null) {
            instance = new ChestStealerGui();
        }
        return instance;
    }

    public final ConcurrentLinkedDeque<AddedItemButton> addedItemButtons = new ConcurrentLinkedDeque<>();
    private AddItemWindow addItemWindow;
    private FloatRect addButton;
    private FloatRect data;
    private boolean rebuild;

    public ChestStealerGui() {
        super(Text.of("."));

        float x = (float) mc.getWindow().getWidth() / 4;
        float y = (float) mc.getWindow().getHeight() / 4;
        float w = 180;
        float h = 180;

        data = new FloatRect(x - w / 2, y - h / 2, w, h);
        addButton = new FloatRect(data.getX() + 3, data.getY() + 3, data.getW() - 6, 20);
        rebuild();
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
//        float x = (float) client.getWindow().getWidth() / 4;
//        float y = (float) client.getWindow().getHeight() / 4;
//        float w = 180;
//        float h = 180;
//        data = new FloatRect(x - w / 2, y - h / 2, w, h);
//        addButton = new FloatRect(data.getX() + 3, data.getY() + 3, data.getW() - 6, 15);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX1, int mouseY1, float delta) {
        int mouseX = (int) (BloodyClient.mc.mouse.getX() / 2);
        int mouseY = (int) (BloodyClient.mc.mouse.getY() / 2);

        if (rebuild) {
            rebuild();
            rebuild = false;
        }
        Utils.rescaling(() -> {
            HudFunction.drawRectGui(data, 1);
            //GL.drawGlow(data, 4, new Color(23, 22, 22, 255));
            //GL.drawRoundedRect(data, 4, new Color(23, 22, 22, 255));
            if (addItemWindow != null) {
                addItemWindow.draw(mouseX, mouseY, 1f);
            }
            ScissorUtils.enableScissor(new FloatRect(data.getX().floatValue(), data.getY() + 23, data.getW().floatValue(), data.getH() - 24));
            for (AddedItemButton addedItemButton : addedItemButtons) {
                addedItemButton.tick();
                if (addedItemButton.data.getY() < data.getY() - 50 || addedItemButton.data.getY2() > data.getY2() + 50)
                    continue;
                addedItemButton.draw(mouseX, mouseY, 1f);
            }
            ScissorUtils.disableScissor();

            GL.drawRoundedGradientRect(addButton, 3.5, ColorUtils.injectAlpha(Colors.getColor(0), 60), ColorUtils.injectAlpha(Colors.getColor(90), 60), ColorUtils.injectAlpha(Colors.getColor(270), (int) (60)), ColorUtils.injectAlpha(Colors.getColor(180), (int) (60)));
            GL.drawRoundedGradientOutline(addButton, 3.5, 1d, ColorUtils.injectAlpha(Colors.getColor(0), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(90), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(270), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(180), (int) (255)));

//        GL.drawGlow(addButton, 3, new Color(73, 73, 73, 255).darker());
//        GL.drawQuad(addButton, new Color(73, 73, 73, 255));
//        GL.drawOutlineQuad(addButton, new Color(73, 73, 73, 255).darker());
            String addText = "Добавить";
            IFont.drawWithShadowCenteredXY(IFont.MONTSERRAT_BOLD, addText, addButton.getCenteredX(), addButton.getCenteredY(), Color.WHITE, 10);
        });
    }

    public void rebuild() {
        addedItemButtons.clear();
        float y = data.getY() + 24;
        for (ChestStealerItem chestStealerItem : ChestStealerManager.getChestStealerItem()) {
            AddedItemButton addedItemButton = new AddedItemButton(new FloatRect(data.getX() + 3, y, data.getW() - 6, 20), chestStealerItem);
            addedItemButton.callback = () -> {
                ChestStealerManager.remove(chestStealerItem);
                rebuild = true;
            };
            addedItemButtons.add(addedItemButton);
            y += 22;
        }
    }

    private float totalH() {
        float f = 0;
        for (AddedItemButton addedItemButton : addedItemButtons) {
            f += addedItemButton.data.getH() + 2;
        }
        return f;
    }

    @Override
    public boolean mouseClicked(double mouseX1, double mouseY1, int button) {
        int mouseX = (int) (BloodyClient.mc.mouse.getX() / 2);
        int mouseY = (int) (BloodyClient.mc.mouse.getY() / 2);

        if (addButton.intersect(mouseX, mouseY) && addItemWindow == null) {
            addItemWindow = new AddItemWindow(new FloatRect(data.getX2().floatValue() + 20f, data.getY().floatValue(), data.getW().floatValue(), data.getH().floatValue()));
            addItemWindow.callback = () -> addItemWindow = null;
            addItemWindow.addCallback = this::rebuild;
        }
        if (addItemWindow != null) {
            addItemWindow.click((int) mouseX, (int) mouseY, button);
        }
        if (!rebuild) {
            for (AddedItemButton addedItemButton : addedItemButtons) {
                if (addedItemButton.data.getY2() < data.getY() + 22 || addedItemButton.data.getY() > data.getY2()) continue;
                addedItemButton.click((int) mouseX, (int) mouseY, button);
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX1, double mouseY1, double amount) {
        int mouseX = (int) (BloodyClient.mc.mouse.getX() / 2);
        int mouseY = (int) (BloodyClient.mc.mouse.getY() / 2);

        if (!rebuild) {
            if (new FloatRect(data.getX(), data.getY(), data.getW(), data.getH()).intersect(mouseX, mouseY) && totalH() > data.getH() - 23) {
                for (AddedItemButton itemButton : addedItemButtons) {
                    if (amount > 0) {
                        itemButton.velocity += 3;
                    }
                    if (amount < 0) {
                        itemButton.velocity -= 3;
                    }
                }
            }
        }
        if (addItemWindow != null) {
            addItemWindow.scroll((int) mouseX, (int) mouseY, amount);
        }
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (addItemWindow != null) {
            addItemWindow.key(keyCode);
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (addItemWindow != null) {
            addItemWindow.symbol(chr);
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}