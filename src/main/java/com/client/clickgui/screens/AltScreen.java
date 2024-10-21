package com.client.clickgui.screens;

import com.client.BloodyClient;
import com.client.alt.Account;
import com.client.alt.Accounts;
import com.client.alt.AltElement;
import com.client.clickgui.FunctionButton;
import com.client.clickgui.GuiScreen;
import com.client.clickgui.Impl;
import com.client.utils.Utils;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.math.Timer;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.Matrices;
import com.client.utils.render.Renderer2D;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.utils.ScissorUtils;
import com.client.utils.render.wisetree.render.render2d.utils.shader.Shader;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11C;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.client.BloodyClient.mc;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

public class AltScreen extends Screen {
    private static AltScreen instance;

    public static AltScreen getInstance(Screen parent) {
        if (instance == null) {
            instance = new AltScreen(parent);
            instance.initka();
        }
        return instance;
    }

    public AltScreen(Screen parent) {
        super(Text.of(""));
        this.parent = parent;
    }

    private Shader shader;
    private Screen parent;
    private List<Impl> elementList = new ArrayList<>();
    private double oldWidth, oldHeight;

    private class TextRect implements Impl {
        public FloatRect rect = new FloatRect(0,0,0,0);
        public boolean isManager = false;
        public List<AltElement> accounts = new ArrayList<>();
        public int accCount = 0;
        public FloatRect scissorRect = new FloatRect(0, 0, 0, 0);
        private float scroll, targetScroll, amount;
        private TextField textField;

        public TextRect(float x, float y, float w, boolean isManager) {
            this.rect.setX(x);
            this.rect.setY(y);
            this.rect.setW(w);
            this.rect.setH(isManager ? 100f : 225f);

            this.scissorRect = new FloatRect(rect.getX() + 3,
                    rect.getY() + 3 + 23.25f,
                    226.5f,
                    195.75f);

            this.isManager = isManager;

            if (!isManager) {
                for (Account account : Accounts.getAccounts()) {
                    accounts.add(new AltElement(account, x + 6, y + 6 + 23.25f));
                }

                accCount = accounts.size();
            } else textField = new TextField(x + 6, y + 23.25f + 7.5f + IFont.getHeight(IFont.MONTSERRAT_MEDIUM,
                    "Введите ник:",
                    8) + 3, 130.5f);
        }

        public void updateAccounts() {
            if (!isManager && accCount != Accounts.getAccounts().size()) {
                accounts.clear();

                for (Account account : Accounts.getAccounts()) {
                    accounts.add(new AltElement(account, rect.getX() + 6, rect.getY() + 6 + 23.25f));
                }

                accCount = Accounts.getAccounts().size();
            }
        }

        private float getMaxHeight() {
            float h = 0;

            for (AltElement functionButton : accounts) {
                h += functionButton.rect.getH() * 1.2f;
            }

            return h;
        }

        private void scroll() {
            if (Accounts.getAccounts().isEmpty() || getMaxHeight() < scissorRect.getH()) {
                targetScroll = 0;
            } else {
                AltElement first = accounts.get(0);
                AltElement second = accounts.get(accounts.size() - 1);

                if (first.rect.getY() > scissorRect.getY() + 3) {
                    targetScroll = 0;
                } else if (second.rect.getY2() < scissorRect.getY2() - 3) {
                    targetScroll = -(getMaxHeight() - scissorRect.getH() + 2.25f);
                } else if (amount != 0) {
                    targetScroll += amount * 1.3;
                    amount = 0;
                }
            }

            scroll = AnimationUtils.fast(scroll, targetScroll);
        }

        @Override
        public void draw(double mx, double my, float alpha) {
            if (!isManager) {
                updateAccounts();
                scroll();
            }

            Matrices.push();
            Matrices.translate(0, 0, 0);

            Renderer2D.COLOR.begin(GL11C.GL_TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
            Renderer2D.COLOR.roundedQuad(rect.getX(), rect.getY(), rect.getX2(), rect.getY() + 17.25f, 3, 9,
                    ColorUtils.injectAlpha(Colors.getColor(0, 13), 110),
                    ColorUtils.injectAlpha(Colors.getColor(90, 13), 110));
            Renderer2D.COLOR.end();

            Renderer2D.COLOR.begin(GL11C.GL_TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
            Renderer2D.COLOR.renderRoundedOutline(Colors.getColor(0, 13),
                    Colors.getColor(90, 13),
                    rect.getX() + 0.75f, rect.getY() + 0.75f, rect.getX2() - 0.75f, rect.getY() + 17.25f - 0.75f, 3, 1);
            Renderer2D.COLOR.end();


            Renderer2D.COLOR.begin(GL11C.GL_TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
            Renderer2D.COLOR.roundedQuad(rect.getX(), rect.getY() + 23.25f, rect.getX2(), rect.getY2(), 3, 9,
                    ColorUtils.injectAlpha(Colors.getColor(0, 13), 110),
                    ColorUtils.injectAlpha(Colors.getColor(90, 13), 110));
            Renderer2D.COLOR.end();

            Renderer2D.COLOR.begin(GL11C.GL_TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
            Renderer2D.COLOR.renderRoundedOutline(Colors.getColor(0, 13),
                    Colors.getColor(90, 13),
                    rect.getX() + 0.75f, rect.getY() + 23.25f + 0.75f, rect.getX2() - 0.75f, rect.getY2() - 0.75f, 3, 1);
            Renderer2D.COLOR.end();

            if (isManager) {
                Renderer2D.COLOR.begin(GL11C.GL_TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
                Renderer2D.COLOR.roundedQuad(rect.getX() + rect.getW() / 2 - 35.625f, rect.getY2() - 6 - 16.5, rect.getX() + rect.getW() / 2 + 35.625f, rect.getY2() - 6, 3, 9,
                        ColorUtils.injectAlpha(Colors.getColor(0, 13), 70),
                        ColorUtils.injectAlpha(Colors.getColor(90, 13), 70));
                Renderer2D.COLOR.end();

                Renderer2D.COLOR.begin(GL11C.GL_TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
                Renderer2D.COLOR.renderRoundedOutline(Colors.getColor(0, 16),
                        Colors.getColor(90, 13),
                        rect.getX() + rect.getW() / 2 - 35.625f + 0.75f, rect.getY2() - 6 - 16.5f + 0.75f, rect.getX() + rect.getW() / 2 + 35.625f - 0.75f, rect.getY2() - 6 - 0.75f, 3, 1);
                Renderer2D.COLOR.end();
            }

            Matrices.pop();

            IFont.drawCenteredXY(IFont.MONTSERRAT_MEDIUM,
                    isManager ? "Менеджер аккаунтов" : "Ваши аккаунты",
                    rect.getCenteredX(),
                    new FloatRect(rect.getX(), rect.getY(), rect.getW(), new Float(17.25f)).getCenteredY(),
                    Color.WHITE,
                    10);

            if (!isManager) {
                ScissorUtils.push();
                ScissorUtils.setFromComponentCoordinates(scissorRect.getX(),
                        scissorRect.getY(),
                        scissorRect.getW(),
                        scissorRect.getH()
                );

                float y = scissorRect.getY() + 3 + scroll;

                for (AltElement altElement : accounts) {
                    altElement.rect.setY(y);
                    y += altElement.rect.getH() * 1.2f;
                    if (altElement.rect.getY2() < scissorRect.getY() || altElement.rect.getY() > scissorRect.getY2()) continue;
                    altElement.draw(mx, my, alpha);
                }

                //accounts.forEach(e -> e.draw(mx, my, alpha));

                ScissorUtils.unset();
                ScissorUtils.pop();
            } else {
                IFont.drawCenteredXY(IFont.MONTSERRAT_MEDIUM,
                        "Создать",
                        new FloatRect(rect.getX() + rect.getW() / 2 - 35.625f, rect.getY2() - 22.5f, 71.25f, 17.25f).getCenteredX(),
                        new FloatRect(rect.getX() + rect.getW() / 2 - 35.625f, rect.getY2() - 22.5f, 71.25f, 17.25f).getCenteredY(),
                        Color.WHITE,
                        10);

                IFont.draw(IFont.MONTSERRAT_MEDIUM,
                        "Введите ник:",
                        rect.getX() + 7.5f,
                        rect.getY() + 23.25f + 7.5f,
                        Color.WHITE,
                        8);

                textField.draw(mx, my, alpha);
            }
        }

        @Override
        public void click(double mx, double my, int button) {
            if (isManager && new FloatRect(rect.getX() + rect.getW() / 2 - 35.625f, rect.getY2() - 22.5f , 71.25f, 17.25f).intersect(mx, my)) {
                Accounts.add(textField.string);
            }

            accounts.forEach(e -> e.click(mx, my, button));
            if (isManager)
            textField.click(mx, my, button);
        }

        @Override
        public void release(double mx, double my, int button) {
        }

        @Override
        public void key(int key) {
            if (isManager)
            textField.key(key);
        }

        @Override
        public void symbol(char chr) {
            if (isManager)
            textField.symbol(chr);
        }

        @Override
        public void scroll(double mx, double my, double amount) {
            if (scissorRect.intersect(mx, my) && getMaxHeight() > scissorRect.getH()) {
                this.amount = (float) amount;
            }
        }

        @Override
        public void close() {
        }
    }

    private class TextField implements Impl {
        @Getter
        @Setter
        public String string = "TestNickname123";

        public FloatRect rect = new FloatRect(0, 0, 0, 0);
        public boolean isActive = false;
        public boolean isError = false;
        public long error = 0;
        private final Timer dot = new Timer();

        public TextField(float x, float y, float w) {
            this.rect.setX(x);
            this.rect.setY(y);
            this.rect.setW(w);
            this.rect.setH(IFont.getHeight(IFont.MONTSERRAT_MEDIUM, "ABC", 9) + 3);
        }

        @Override
        public void draw(double mx, double my, float alpha) {
            if (isError && System.currentTimeMillis() - error > 1000) {
                isError = false;
            }
            dot.tick();
            dot.resetIfPassed(20);

            Matrices.push();
            Matrices.translate(0, 0, 0);

            Renderer2D.COLOR.begin(GL11C.GL_TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
            Renderer2D.COLOR.roundedQuad(rect.getX(), rect.getY(), rect.getX2(), rect.getY2(), 3, 9,
                    ColorUtils.injectAlpha(Colors.getColor(0, 13), 110),
                    ColorUtils.injectAlpha(Colors.getColor(90, 13), 110));
            Renderer2D.COLOR.end();

            Renderer2D.COLOR.begin(GL11C.GL_TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
            Renderer2D.COLOR.renderRoundedOutline(Colors.getColor(0, 13),
                    Colors.getColor(90, 13),
                    rect.getX() + 0.75f, rect.getY() + 0.75f, rect.getX2() - 0.75f, rect.getY2() - 0.75f, 3, 1);
            Renderer2D.COLOR.end();

            Matrices.pop();

            if (isError) IFont.drawCenteredY(IFont.MONTSERRAT_MEDIUM, "Ошибка!",rect.getX() + 2.5f, rect.getCenteredY(), Color.RED, 9);
            else IFont.drawCenteredY(IFont.MONTSERRAT_MEDIUM, string + (isActive && dot.passed(10) && string.length() < 16 ? "." : ""),rect.getX() + 2.5f, rect.getCenteredY(), Color.WHITE, 9);
        }

        @Override
        public void click(double mx, double my, int button) {
            isActive = rect.intersect(mx, my);
        }

        @Override
        public void release(double mx, double my, int button) {

        }

        @Override
        public void key(int key) {
            if (isActive && key == GLFW.GLFW_KEY_BACKSPACE && !string.isEmpty()) {
                string = GuiScreen.getStringIgnoreLastChar(string);
            } else if (isActive && key == GLFW.GLFW_KEY_ENTER) {
                isActive = false;
            }
        }

        @Override
        public void symbol(char chr) {
            if (!isActive || string.length() >= 16) return;

            if ((isLatinLetter(chr) || Character.isDigit(chr) || chr == '_')) string += chr;
            else {
                isError = true;
                error = System.currentTimeMillis();
            }
        }

        public boolean isLatinLetter(char c) {
            return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
        }

        @Override
        public void scroll(double mx, double my, double amount) {

        }

        @Override
        public void close() {

        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX1, int mouseY1, float partialTicks) {
        if (oldWidth != mc.getWindow().getWidth() || oldHeight != mc.getWindow().getHeight()) initka();

        int mouseX = (int) (BloodyClient.mc.mouse.getX() / 2);
        int mouseY = (int) (BloodyClient.mc.mouse.getY() / 2);

        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        shader.load();
        shader.drawMainMenu();
        shader.unload();

        RenderSystem.defaultBlendFunc();
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();

        Utils.rescaling(() -> elementList.forEach(e -> e.draw(mouseX, mouseY, 1)));

        super.render(matrices, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        elementList.forEach(e -> e.click(BloodyClient.mc.mouse.getX() / 2, BloodyClient.mc.mouse.getY() / 2, button));

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        elementList.forEach(e -> e.scroll(BloodyClient.mc.mouse.getX() / 2, BloodyClient.mc.mouse.getY() / 2, amount));

        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            onClose();
            return false;
        }

        elementList.forEach(e -> e.key(keyCode));

        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        elementList.forEach(e -> e.symbol(chr));

        return false;
    }

    @Override
    public void onClose() {
        mc.openScreen(this.parent);
    }

    @Override
    public void init() {
        this.shader = BloodyClient.shader;
//        Window window = mc.getWindow();
//        this.addButton(new ButtonWidget(window.getScaledWidth() - 80, window.getScaledHeight() - 30, 70, 20, Text.of("Close"),
//                button -> this.onClose()));
    }

    public void initka() {
        elementList.clear();
        oldWidth = mc.getWindow().getWidth();
        oldHeight = mc.getWindow().getHeight();
        TextRect rect = new TextRect(
                mc.getWindow().getWidth() / 4 - 195 - 3.75f,
                mc.getWindow().getHeight() / 4 - 112.5f,
                142.5f, true
        );

        TextRect rect2 = new TextRect(
                mc.getWindow().getWidth() / 4 + 157.5f - 195 - 3.75f,
                mc.getWindow().getHeight() / 4 - 112.5f,
                232.5f, false
        );

        elementList.add(rect);
        elementList.add(rect2);
    }
}
