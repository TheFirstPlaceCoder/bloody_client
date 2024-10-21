package com.client.clickgui.autobuy;

import com.client.system.autobuy.AutoBuyItem;
import com.client.system.autobuy.AutoBuyManager;
import com.client.system.autobuy.CustomAutoBuyItem;
import com.client.system.config.ConfigSystem;
import com.client.system.hud.HudFunction;
import com.client.utils.Utils;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.utils.ScissorUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.client.BloodyClient.mc;

public class AutoBuyGui extends Screen {
    private static AutoBuyGui instance;

    public static AutoBuyGui getInstance() {
        if (instance == null) {
            instance = new AutoBuyGui();
        }
        return instance;
    }

    public double x, y, w = 200, h = 200;
    public ConcurrentLinkedQueue<AutoBuyButton> autoBuyButtons = new ConcurrentLinkedQueue<>();

    public SelectWindow selectWindow;
    public HistoryScreen historyScreen;

    public String search = "";
    public boolean write;
    public double scroll = 0, targetScroll;
    public int dot = 0;

    public AutoBuyGui() {
        super(Text.of("ab"));

        x = (double) mc.getWindow().getWidth() / 4 - w / 2;
        y = (double) mc.getWindow().getHeight() / 4 - h / 2;

        selectWindow = new SelectWindow(x + 210, y, w, h);
        historyScreen = new HistoryScreen(x - 210, y, w, h, this);

        double y2 = y + 26;
        for (AutoBuyItem item : AutoBuyManager.getItems()) {
            autoBuyButtons.add(new AutoBuyButton(x, y2, w, 18, item, item instanceof CustomAutoBuyItem ? ((CustomAutoBuyItem) item).name : item.item.getName().getString()));
            y2 += 20;
        }
    }

    public void open() {
        historyScreen.open();
        selectWindow.open();
    }

    public void addItem(AutoBuyItem item) {
        double y2 = y + 26;
        for (AutoBuyButton a : autoBuyButtons) {
            y2 += 20;
        }
        autoBuyButtons.add(new AutoBuyButton(x, y2, w, 18, item, item instanceof CustomAutoBuyItem ? ((CustomAutoBuyItem) item).name : item.item.getName().getString()));
    }

    public double calcH() {
        return autoBuyButtons.stream().filter(f -> f.name.toLowerCase().contains(search.toLowerCase())).toList().size() * 20 + 4;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX1, int mouseY1, float partialTicks) {
        int mouseX = (int) (mc.mouse.getX() / 2);
        int mouseY = (int) (mc.mouse.getY() / 2);

        targetScroll = MathHelper.clamp(targetScroll, -(calcH() - (h - 24)), 0);
        if (calcH() - (h - 24) < 0) {
            targetScroll = 0;
        }
        scroll = AnimationUtils.fast(scroll, targetScroll);

        Utils.rescaling(() -> {
            HudFunction.drawRectGui(new FloatRect(x, y, w, h), 1);
            //GL.drawRoundedRect(x, y, w, h, 4, new Color(39, 37, 37, 255));

            if (dot > 20) dot = 0;
            if (write) dot++;

            GL11.glPushMatrix();
            GL11.glTranslatef(0, 0, 300);
            GL.drawRoundedRect(x + 4, y + 4, w - 8, 20, 2, new Color(16, 15, 15, 255));
            IFont.drawCenteredY(IFont.MONTSERRAT_MEDIUM, search.isEmpty() && !write ? "введите текст..." : search + (write ? dot > 10 ? "_" : "" : ""), (float) (x + 8), (float) (y + 14), search.isEmpty() ? Color.GRAY : Color.WHITE, 8);
            String reset = "очистить";
            GL.drawRoundedRect(x + w - 4 - IFont.getWidth(IFont.MONTSERRAT_MEDIUM, reset, 8) - 6, y + 4 + 10 - IFont.getHeight(IFont.MONTSERRAT_MEDIUM, reset, 8) / 2 - 1, IFont.getWidth(IFont.MONTSERRAT_MEDIUM, reset, 8) + 4, IFont.getHeight(IFont.MONTSERRAT_MEDIUM, reset, 8) + 2, 2, new Color(43, 44, 44, 255));
            IFont.drawCenteredXY(IFont.MONTSERRAT_MEDIUM, reset, (float) (x + w - 4 - IFont.getWidth(IFont.MONTSERRAT_MEDIUM, reset, 8) / 2 - 4), (float) (y + 14), Color.WHITE, 8);
            GL11.glPopMatrix();

            double y2 = y + 26 + scroll;
            ScissorUtils.enableScissor(new FloatRect(x, y + 24, w, h - 27));
            for (AutoBuyButton autoBuyButton : autoBuyButtons) {
                if (!autoBuyButton.name.toLowerCase().contains(search.toLowerCase())) continue;
                autoBuyButton.y = y2;
                y2 += 20;
                if (autoBuyButton.y < y || autoBuyButton.y > y + h) continue;
                autoBuyButton.render(mouseX, mouseY);
            }
            ScissorUtils.disableScissor();

            selectWindow.render(mouseX, mouseY);
            historyScreen.render(mouseX, mouseY);
        });
    }

    @Override
    public boolean mouseClicked(double mouseX1, double mouseY1, int button) {
        int mouseX = (int) (mc.mouse.getX() / 2);
        int mouseY = (int) (mc.mouse.getY() / 2);

        selectWindow.click(mouseX, mouseY, button);
        if (isHover(x, y + 24, w, h - 24, mouseX, mouseY)) {
            for (AutoBuyButton autoBuyButton : autoBuyButtons) {
                if (!autoBuyButton.name.toLowerCase().contains(search.toLowerCase())) continue;
                autoBuyButton.click((int) mouseX, (int) mouseY, button);
            }
        }
        if (isHover(x, y + 4, w, 20, mouseX, mouseY)) {
            PrintStack.setCallback(this.getClass(), () -> write = false);
            write = true;
        }
        if (isHover(x + w - 8 - IFont.getWidth(IFont.MONTSERRAT_MEDIUM, "очистить", 8) - 6, y + 4, IFont.getWidth(IFont.MONTSERRAT_MEDIUM, "очистить", 8) + 8, 20, mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_1) {
            write = false;
            search = "";
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        selectWindow.key(keyCode);
        for (AutoBuyButton autoBuyButton : autoBuyButtons) {
            if (!autoBuyButton.name.toLowerCase().contains(search.toLowerCase())) continue;
            autoBuyButton.key(keyCode);
        }
        if (write) {
            if (keyCode == GLFW.GLFW_KEY_ENTER) {
                write = false;
            }
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                search = getStringIgnoreLastChar(search);
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public static String getStringIgnoreLastChar(String str) {
        StringBuilder s = new StringBuilder();

        for (int i = 0; i < str.toCharArray().length - 1; i++) {
            s.append(str.toCharArray()[i]);
        }

        return s.toString();
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        selectWindow.charTyped(codePoint);
        for (AutoBuyButton autoBuyButton : autoBuyButtons) {
            if (!autoBuyButton.name.toLowerCase().contains(search.toLowerCase())) continue;
            autoBuyButton.charTyped(codePoint);
        }
        if (write) {
            search += codePoint;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX1, double mouseY1, double delta) {
        int mouseX = (int) (mc.mouse.getX() / 2);
        int mouseY = (int) (mc.mouse.getY() / 2);

        historyScreen.scroll(mouseX, mouseY, delta);
        selectWindow.scroll(mouseX, mouseY, delta);
        if (isHover(x, y + 24, w, h - 24, mouseX, mouseY) && (calcH() - (h - 24) > 0)) {
            if (delta < 0) {
                if (targetScroll > -(calcH() - (h - 24))) {
                    targetScroll -= 15;
                }
                targetScroll = MathHelper.clamp(targetScroll, -(calcH() - (h - 24)), 0);
            }
            if (delta > 0) {
                if (targetScroll < 0) {
                    targetScroll += 15;
                }
                targetScroll = MathHelper.clamp(targetScroll, -(calcH() - (h - 24)), 0);
            }
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        ConfigSystem.save();
        super.onClose();
    }

    public static boolean isHover(double X, double Y, double W, double H, double mX, double mY) {
        return mX >= X&& mX <= (X + W)  && mY >= Y  && mY <= (Y + H) ;
    }
}
