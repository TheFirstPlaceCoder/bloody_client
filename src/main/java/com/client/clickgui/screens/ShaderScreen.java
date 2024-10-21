package com.client.clickgui.screens;

import com.client.BloodyClient;
import com.client.alt.Account;
import com.client.clickgui.Impl;
import com.client.utils.Utils;
import com.client.utils.changelog.ChangeLog;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.DrawMode;
import com.client.utils.render.Matrices;
import com.client.utils.render.Renderer2D;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.main.TextureGL;
import com.client.utils.render.wisetree.render.render2d.utils.shader.Shader;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsBridgeScreen;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static com.client.BloodyClient.mc;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

public class ShaderScreen extends Screen {
    private static ShaderScreen instance;

    public static ShaderScreen getInstance() {
        if (instance == null) {
            instance = new ShaderScreen();
            instance.initka();
        }
        return instance;
    }

    public ShaderScreen() {
        super(Text.of(""));
    }

    private Shader shader;
    private List<Button> buttonList = new ArrayList<>();
    private Identifier logo = new Identifier("bloody-client", "/client/menulogo.png");
    private double oldWidth, oldHeight;

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

        Utils.rescaling(() -> {
            GL11.glPushMatrix();
            GL11.glTranslated(0, 0, 100);
            ChangeLog.draw(mouseX, mouseY);
            GL11.glPopMatrix();

            TextureGL.create().bind(logo).draw(new TextureGL.TextureRegion(mc.getWindow().getWidth() / 4f - 40f, mc.getWindow().getHeight() / 4f - 11.2f - 120f, 80, 80), true,
                    Colors.getColor(0, 13),
                    Colors.getColor(90, 13),
                    Colors.getColor(180, 13),
                    Colors.getColor(270, 13)
            );

            buttonList.forEach(e -> e.draw(mouseX, mouseY, 1));
        });

        super.render(matrices, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX1, double mouseY1, int button) {
        double mouseX = (mc.mouse.getX() / 2);
        double mouseY = (mc.mouse.getY() / 2);

        ChangeLog.click((int) mouseX, (int) mouseY, button);

        for (Button buttons : buttonList) {
            buttons.click(mouseX, mouseY, button);
        }

        return false;
    }

    private class Button implements Impl {
        // Лого на 154
        public FloatRect rect = new FloatRect(0, 0, 0, 0);
        public String text;
        public Runnable runnable;
        public boolean isOneLine = false;

        public Button(double y, String text, Runnable run) {
            rect.setW(152f);
            rect.setH(22.4f);
            rect.setX(mc.getWindow().getWidth() / 4 - 76f);
            rect.setY(mc.getWindow().getHeight() / 4 + 22.4f + (float) y * 1.3f);
            this.text = text;
            this.runnable = run;
        }

        public Button(double y, String text, Runnable run, boolean isOneLine) {
            rect.setW(isOneLine ? 64f : 80f);
            rect.setH(22.4f);
            rect.setX(mc.getWindow().getWidth() / 4 - 76f + (isOneLine ? 88 : 0));
            rect.setY(mc.getWindow().getHeight() / 4 + 22.4f + (float) y * 1.3f);
            this.text = text;
            this.runnable = run;
            this.isOneLine = isOneLine;
        }

        @Override
        public void draw(double mx, double my, float alpha) {
            int alphach = rect.intersect(mx, my) ? 140 : 110;
//            GL.drawRoundedGradientRect(rect, 3, new Color(33, 91, 23, alphach),
//                    new Color(180, 229, 172, alphach),
//                    new Color(33, 91, 23, alphach),
//                    new Color(180, 229, 172, alphach));

            GL.prepare();
            GL.drawRoundedGradientRect(rect, 3, ColorUtils.injectAlpha(Colors.getColor(0, 13), alphach),
                    ColorUtils.injectAlpha(Colors.getColor(90, 13), alphach),
                    ColorUtils.injectAlpha(Colors.getColor(0, 13), alphach),
                    ColorUtils.injectAlpha(Colors.getColor(90, 13), alphach));

            GL.drawRoundedGradientOutline(rect, 3, 1, ColorUtils.injectAlpha(Colors.getColor(0, 13), 255),
                    ColorUtils.injectAlpha(Colors.getColor(90, 13), 255),
                    ColorUtils.injectAlpha(Colors.getColor(0, 13), 255),
                    ColorUtils.injectAlpha(Colors.getColor(90, 13), 255));

            GL.end();
//            Matrices.push();
//            Matrices.translate(0, 0, 0);
//
//            Renderer2D.COLOR.begin(GL11C.GL_TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
//            Renderer2D.COLOR.roundedQuad(rect.getX(), rect.getY(), rect.getX2(), rect.getY2(), 3, 9,
//                    ColorUtils.injectAlpha(Colors.getColor(0, 13), alphach),
//                    ColorUtils.injectAlpha(Colors.getColor(90, 13), alphach));
//            Renderer2D.COLOR.end();
//
//            Renderer2D.COLOR.begin(GL11C.GL_TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
//            Renderer2D.COLOR.renderRoundedOutline(Colors.getColor(0, 13),
//                    Colors.getColor(90, 13),
//                    rect.getX() + 1, rect.getY() + 1, rect.getX2() - 1, rect.getY2() - 1, 3, 1);
//            Renderer2D.COLOR.end();
//
//            Matrices.pop();

            IFont.drawCenteredXY(IFont.MONTSERRAT_MEDIUM, text, rect.getCenteredX(), rect.getCenteredY(), Color.WHITE, 12);
        }

        @Override
        public void click(double mx1, double my1, int button) {
            double mx = BloodyClient.mc.mouse.getX() / 2;
            double my = BloodyClient.mc.mouse.getY() / 2;

            if (rect.intersect(mx, my) && runnable != null) runnable.run();
        }

        @Override
        public void release(double mx, double my, int button) {
        }

        @Override
        public void key(int key) {
        }

        @Override
        public void symbol(char chr) {
        }

        @Override
        public void scroll(double mx, double my, double amount) {
        }

        @Override
        public void close() {
        }
    }

    @Override
    public void init() {
        this.shader = BloodyClient.shader;
        //new Account("Vasya_ggwp").login();
//        Window window = mc.getWindow();
//        this.addButton(new ButtonWidget(window.getScaledWidth() - 80, window.getScaledHeight() - 30, 70, 20, Text.of("Close"),
//                button -> this.onClose()));
    }

    public void initka() {
        buttonList.clear();
        oldWidth = mc.getWindow().getWidth();
        oldHeight = mc.getWindow().getHeight();
        Button one = new Button(0, "Одиночная игра", () -> {
            this.client.openScreen(new SelectWorldScreen(getInstance()));
        });

        Button two = new Button(22.4, "Сетевая игра", () -> {
            Screen screen = this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(getInstance()) : new MultiplayerWarningScreen(getInstance());
            this.client.openScreen(screen);
        });

        Button three = new Button(44.8, "Minecraft Realms", () -> {
            RealmsBridgeScreen realmsBridgeScreen = new RealmsBridgeScreen();
            realmsBridgeScreen.switchToRealms(getInstance());
        });

        Button four = new Button(67.2, "Аккаунты", () -> {
            this.client.openScreen(AltScreen.getInstance(getInstance()));
        });

        Button five = new Button(89.6, "Настройки", () -> {
            this.client.openScreen(new OptionsScreen(getInstance(), this.client.options));
        }, false);

        Button six = new Button(89.6, "Выйти", () -> {
            this.client.scheduleStop();
        }, true);

        buttonList.add(one);
        buttonList.add(two);
        buttonList.add(three);
        buttonList.add(four);
        buttonList.add(five);
        buttonList.add(six);
    }

    @Override
    public void onClose() {}
}
