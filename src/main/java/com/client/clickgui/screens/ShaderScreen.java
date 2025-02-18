package com.client.clickgui.screens;

import com.client.BloodyClient;
import com.client.clickgui.Impl;
import com.client.impl.function.client.ClickGui;
import com.client.system.function.FunctionManager;
import com.client.system.textures.DownloadImage;
import com.client.utils.Utils;
import com.client.utils.auth.Loader;
import com.client.utils.files.SoundManager;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.misc.CustomSoundInstance;
import com.client.utils.render.wisetree.font.api.FontRenderer;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.utils.shader.shaders.BlurShader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.client.BloodyClient.mc;

public class ShaderScreen extends Screen {
    private static ShaderScreen instance;

    public static ShaderScreen getInstance() {
        if (instance == null) {
            instance = new ShaderScreen();
            instance.initka();
            if (!Objects.equals(Loader.debugString, "Checked")) {
                BloodyClient.LOGGER.info("D");
                throw new ArithmeticException();
            }
        }

        instance.animation = 0;

        return instance;
    }

    public ShaderScreen() {
        super(Text.of(""));
    }

    private List<Button> buttonList = new ArrayList<>();
    private double oldWidth, oldHeight, animation;
    private FloatRect mainRect = new FloatRect(0, 0, 0, 0);
    private Button one, two, three, four, five;
    private boolean closing = false;
    private Runnable postTask = () -> {};

    @Override
    public void render(MatrixStack matrices, int mouseX1, int mouseY1, float partialTicks) {
        if (oldWidth != mc.getWindow().getWidth() || oldHeight != mc.getWindow().getHeight()) initka();

        if (animation != 1000 && !closing) animation = AnimationUtils.fast(animation, 1000);
        else if (animation != 2000 && closing) animation = AnimationUtils.fast(animation, 2000);

        if (animation >= 1800 && closing) {
            closing = false;
            postTask.run();
        }

        mainRect.setX((float) (mc.getWindow().getWidth() / 4 - 75 + 1000 - animation));

        buttonList.forEach(e -> e.rect.setX((float) (mc.getWindow().getWidth() / 4 + (e.text.equals(Utils.isRussianLanguage ? "Выйти" : "Quit") ? 2.5 : - 62.5) + 1000 - animation)));

        int mouseX = (int) (BloodyClient.mc.mouse.getX() / 2);
        int mouseY = (int) (BloodyClient.mc.mouse.getY() / 2);

        Utils.rescaling(() -> {
            GL.prepare();
            GL.drawRoundedTexture(DownloadImage.getGlId(DownloadImage.CHRISTMAS_MENU), 0, 0, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight(), 0);
            GL.end();

            BlurShader.registerRenderCall(() -> {
                GL.drawRoundedRect(new FloatRect(0, 0, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight()), 0, Color.WHITE);
            });

            BlurShader.draw(8);

            GL.prepare();
            GL.drawRoundedRect(mainRect, 7, new Color(28, 30, 35, 175));
            GL.end();

            FontRenderer.color(true);
            IFont.drawCenteredX(IFont.MONTSERRAT_BOLD, "Bloody Client", mainRect.getCenteredX(), mainRect.getY() + 5, new Color(162, 162, 162).brighter(), 13);
            IFont.drawCenteredX(IFont.MONTSERRAT_BOLD, "v" + BloodyClient.VERSION, mainRect.getCenteredX(), mainRect.getY() + 5 + IFont.getHeight(IFont.MONTSERRAT_BOLD, "ABC123", 13), new Color(162, 162, 162).brighter(), 7);
            FontRenderer.color(false);

            float w = IFont.getWidth(IFont.MONTSERRAT_MEDIUM, (Utils.isRussianLanguage ? "Вы авторизованы как " : "You're loggined as ") + mc.getSession().getUsername(), 8);
            float h = IFont.getHeight(IFont.MONTSERRAT_MEDIUM, "A", 9);
            IFont.draw(IFont.MONTSERRAT_MEDIUM, (Utils.isRussianLanguage ? "Вы авторизованы как " : "You're loggined as "), mc.getWindow().getWidth() / 4 - w / 2, mc.getWindow().getHeight() / 2 - h, new Color(162, 162, 162).brighter(), 8);

            FontRenderer.color(true);
            FontRenderer.shouldRename(false);
            IFont.draw(IFont.MONTSERRAT_MEDIUM, mc.getSession().getUsername(), mc.getWindow().getWidth() / 4 - w / 2 + IFont.getWidth(IFont.MONTSERRAT_MEDIUM, (Utils.isRussianLanguage ? "Вы авторизованы как " : "You're loggined as "), 8), mc.getWindow().getHeight() / 2 - h, new Color(162, 162, 162).brighter(), 8);
            FontRenderer.color(false);
            FontRenderer.shouldRename(true);

            buttonList.forEach(e -> e.draw(mouseX, mouseY, 1));
        });

        super.render(matrices, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX1, double mouseY1, int button) {
        double mouseX = (mc.mouse.getX() / 2);
        double mouseY = (mc.mouse.getY() / 2);

        for (Button buttons : buttonList) {
            buttons.click(mouseX, mouseY, button);
        }

        return false;
    }

    private class Button implements Impl {
        public FloatRect rect = new FloatRect(0, 0, 0, 0);
        public String text;
        public Runnable runnable;
        public boolean isOneLine = false;
        public float selectedAlpha;

        public Button(FloatRect rect, String text, Runnable run) {
            this.rect = rect;
            this.text = text;
            this.runnable = run;
        }

        public Button(FloatRect rect, String text, Runnable run, boolean isOneLine) {
            this.rect = rect;
            this.text = text;
            this.runnable = run;
            this.isOneLine = isOneLine;
        }

        @Override
        public void draw(double mx, double my, float alpha) {
            selectedAlpha = AnimationUtils.fast(selectedAlpha, rect.intersect(mx, my) ? 255 : 0, rect.intersect(mx, my) ? 10 : 5);

            GL.prepare();
            GL.drawRoundedRect(rect, 5, Utils.lerp(new Color(40, 40, 40, 200), new Color(15, 15, 15, 200), selectedAlpha / 255));
            GL.end();

            IFont.drawCenteredX(IFont.Greycliff, text, rect.getCenteredX(), rect.getCenteredY() - IFont.getHeight(IFont.Greycliff, "a", 9) / 2, Color.WHITE, 9);
        }

        @Override
        public void click(double mx1, double my1, int button) {
            double mx = BloodyClient.mc.mouse.getX() / 2;
            double my = BloodyClient.mc.mouse.getY() / 2;

            if (rect.intersect(mx, my) && runnable != null) {
                if (FunctionManager.get(ClickGui.class).clientSound.get()) {
                    CustomSoundInstance customSoundInstance = new CustomSoundInstance(SoundManager.BUBBLE_EVENT, SoundCategory.MASTER);
                    customSoundInstance.setVolume(FunctionManager.get(ClickGui.class).volume.floatValue());
                    mc.getSoundManager().play(customSoundInstance);
                }

                runnable.run();
            }
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

    public void initka() {
        buttonList.clear();
        oldWidth = mc.getWindow().getWidth();
        oldHeight = mc.getWindow().getHeight();

        // mainRect = new FloatRect(mc.getWindow().getWidth() / 4 - 75, mc.getWindow().getHeight() / 4, 150, 145);
        mainRect = new FloatRect(mc.getWindow().getWidth() / 4 - 75 + 1000, mc.getWindow().getHeight() / 4, 150, 145);

        one = new Button(new FloatRect(mc.getWindow().getWidth() / 4 - 62.5 + 1000, mc.getWindow().getHeight() / 4 + 10 + 30, 125, 20), (Utils.isRussianLanguage ? "Одиночная игра" : "Singleplayer"), () -> {
            this.client.openScreen(new SelectWorldScreen(getInstance()));
        });

        two = new Button(new FloatRect(mc.getWindow().getWidth() / 4 - 62.5 + 1000, mc.getWindow().getHeight() / 4 + 10 + 30 + 25, 125, 20), (Utils.isRussianLanguage ? "Сетевая игра" : "Multiplayer"), () -> {
            Screen screen = this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(getInstance()) : new MultiplayerWarningScreen(getInstance());
            this.client.openScreen(screen);
        });

        three = new Button(new FloatRect(mc.getWindow().getWidth() / 4 - 62.5 + 1000, mc.getWindow().getHeight() / 4 + 10 + 30 + 50, 125, 20), (Utils.isRussianLanguage ? "Аккаунты" : "Accounts"), () -> {
            closing = true;
            postTask = () -> this.client.openScreen(AltScreen.getInstance());
        });

        four = new Button(new FloatRect(mc.getWindow().getWidth() / 4 - 62.5 + 1000, mc.getWindow().getHeight() / 4 + 10 + 30 + 75, 60, 20), (Utils.isRussianLanguage ? "Настройки" : "Settings"), () -> {
            this.client.openScreen(new OptionsScreen(getInstance(), this.client.options));
        }, false);

        five = new Button(new FloatRect(mc.getWindow().getWidth() / 4 + 2.5 + 1000, mc.getWindow().getHeight() / 4 + 10 + 30 + 75, 60, 20), (Utils.isRussianLanguage ? "Выйти" : "Quit"), () -> {
            this.client.scheduleStop();
        }, true);

        buttonList.add(one);
        buttonList.add(two);
        buttonList.add(three);
        buttonList.add(four);
        buttonList.add(five);
    }

    @Override
    public void onClose() {
    }
}
