package com.client.clickgui.newgui;

import com.client.clickgui.newgui.settings.AbstractSettingElement;
import com.client.impl.function.client.ClickGui;
import com.client.system.function.Category;
import com.client.system.function.FunctionManager;
import com.client.utils.Utils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.DrawMode;
import com.client.utils.render.Renderer2D;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.client.BloodyClient.mc;

public class GuiScreen extends Screen {
    public GuiScreen() {
        super(Text.of("gui-screen"));
        init();
    }

    public static final List<CategoryElement> CATEGORY_ELEMENTS = new CopyOnWriteArrayList<>();
    private static GuiScreen instance;
    public static boolean closeInvoke = false;
    public static ClickGui clickGui;

    private float alpha = 0;

    public static GuiScreen getInstance() {
        if (instance == null) {
            instance = new GuiScreen();
        }
        if (clickGui == null) clickGui = FunctionManager.get(ClickGui.class);

        return instance;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX1, int mouseY1, float delta) {
        int mouseX = (int) (mc.mouse.getX() / 2);
        int mouseY = (int) (mc.mouse.getY() / 2);

        if (closeInvoke)
            alpha -= 20f / 255f;
        else
            alpha += 20f / 255f;

        alpha = MathHelper.clamp(alpha, 0f, 1f);

        if (clickGui.drawBackground.get()) {
            Renderer2D.COLOR.begin(DrawMode.Triangles, VertexFormats.POSITION_COLOR);
            Renderer2D.COLOR.quad(0, 0, mc.getWindow().getWidth(), mc.getWindow().getHeight(), AbstractSettingElement.inject(clickGui.backgroundColor.get(), alpha));
            Renderer2D.COLOR.end();
        }

        FloatRect rect = new FloatRect(CATEGORY_ELEMENTS.get(2).innerRect);
        rect.setY(rect.getY());

        Utils.rescaling(() -> {
            for (CategoryElement functionTab : CATEGORY_ELEMENTS) {
                functionTab.draw(mouseX, mouseY, alpha);
            }
        });

        if (closeInvoke && alpha <= 0) {
            mc.openScreen(null);
            closeInvoke = false;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (CategoryElement functionTab : CATEGORY_ELEMENTS) {
            functionTab.click((int) (mc.mouse.getX() / 2), (int) (mc.mouse.getY() / 2), button);
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (CategoryElement functionTab : CATEGORY_ELEMENTS) {
            functionTab.release((int) (mc.mouse.getX() / 2), (int) (mc.mouse.getY() / 2), button);
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (CategoryElement functionTab : CATEGORY_ELEMENTS) {
            functionTab.key(keyCode);
        }
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (CategoryElement functionTab : CATEGORY_ELEMENTS) {
            functionTab.symbol(chr);
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        for (CategoryElement functionTab : CATEGORY_ELEMENTS) {
            functionTab.scroll((int) (mc.mouse.getX() / 2), (int) (mc.mouse.getY() / 2), amount);

            functionTab.rect.setY(functionTab.rect.getY() + (float) amount * clickGui.scrollSpeed.get());
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (CategoryElement functionTab : CATEGORY_ELEMENTS) {
            functionTab.dragged((int) (mc.mouse.getX() / 2), (int) (mc.mouse.getY() / 2), deltaX, deltaY, button);
        }
        return false;
    }

    @Override
    public void onClose() {
        for (CategoryElement functionTab : CATEGORY_ELEMENTS) {
            functionTab.close();
        }
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        init();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void init() {
        closeInvoke = false;
        CATEGORY_ELEMENTS.clear();

        float x = 5, y = 10;

        for (Category cat : Category.values()) {
            CategoryElement categoryElement = new CategoryElement(new FloatRect(x, y, 110, 0), cat);
            CATEGORY_ELEMENTS.add(categoryElement);
            x += categoryElement.rect.getW() + 10;
        }
    }
}
