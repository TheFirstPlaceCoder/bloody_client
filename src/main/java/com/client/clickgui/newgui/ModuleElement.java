package com.client.clickgui.newgui;

import com.client.clickgui.Impl;
import com.client.clickgui.newgui.settings.*;
import com.client.clickgui.newgui.settings.color.ColorSettingElement;
import com.client.impl.function.client.ClickGui;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.manager.SettingManager;
import com.client.system.textures.DownloadImage;
import com.client.utils.Utils;
import com.client.utils.auth.Loader;
import com.client.utils.color.ColorUtils;
import com.client.utils.files.SoundManager;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.misc.CustomSoundInstance;
import com.client.utils.render.wisetree.font.api.FontRenderer;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.main.TextureGL;
import com.client.utils.render.wisetree.render.render2d.utils.ScissorUtils;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.client.BloodyClient.mc;

public class ModuleElement implements Impl {

    public float defaultHeight, selectedAlpha;

    public boolean open = false;

    public FloatRect rect, namedRect;
    public Function function;

    public final List<AbstractSettingElement> settingButtons = new ArrayList<>();

    public ModuleElement(FloatRect rect, Function function) {
        this.rect = rect;
        this.namedRect = rect;
        this.function = function;
        float y = rect.getY2();

        BindSettingElement bindButton = new BindSettingElement(function, new FloatRect(rect.getX(), y, rect.getW(), 16));
        bindButton.add = 17;
        settingButtons.add(bindButton);
        for (AbstractSettings<?> abstractSettings : SettingManager.getSettingsList(function)) {
            float add = 0;
            switch (abstractSettings.getType()) {
                case Boolean -> {
                    BooleanSettingElement booleanSetting = new BooleanSettingElement(abstractSettings, new FloatRect(rect.getX(), y, rect.getW(), 10));
                    booleanSetting.add = 11;
                    settingButtons.add(booleanSetting);
                    add = 11;
                }
                case String -> {
                    StringSettingElement booleanSetting = new StringSettingElement(abstractSettings, new FloatRect(rect.getX(), y, rect.getW(), 10));
                    booleanSetting.add = 11;
                    settingButtons.add(booleanSetting);
                    add = 11;
                }
                case Keybind -> {
                    KeybindSettingElement keybindButton = new KeybindSettingElement(abstractSettings, new FloatRect(rect.getX(), y, rect.getW(), 10));
                    keybindButton.add = 11;
                    settingButtons.add(keybindButton);
                    add = 11;
                }
                case Double -> {
                    DoubleSettingElement doubleButton = new DoubleSettingElement(abstractSettings, new FloatRect(rect.getX(), y, rect.getW(), 14));
                    doubleButton.add = 15;
                    settingButtons.add(doubleButton);
                    add = 15;
                }
                case Integer -> {
                    IntegerSettingElement integerButton = new IntegerSettingElement(abstractSettings, new FloatRect(rect.getX(), y, rect.getW(), 14));
                    integerButton.add = 15;
                    settingButtons.add(integerButton);
                    add = 15;
                }
                case List -> {
                    ListSettingElement listButton = new ListSettingElement(abstractSettings, new FloatRect(rect.getX(), y, rect.getW(), 10));
                    listButton.add = 11;
                    settingButtons.add(listButton);
                    add = 11;
                }
                case MultiBoolean -> {
                    MultiBooleanSettingElement multiBooleanButton = new MultiBooleanSettingElement(abstractSettings, new FloatRect(rect.getX(), y, rect.getW(), 10));
                    multiBooleanButton.add = 11;
                    settingButtons.add(multiBooleanButton);
                    add = 11;
                }
                case Color -> {
                    ColorSettingElement colorButton = new ColorSettingElement(abstractSettings, new FloatRect(rect.getX(), y, rect.getW(), 10));
                    colorButton.add = 11;
                    settingButtons.add(colorButton);
                    add = 11;
                }
                default -> add = 0;
            }
            y += add;
        }

        defaultHeight = rect.getH();
        namedRect.setH(defaultHeight);
    }

    public float getSettingsHeight() {
        float f = defaultHeight;

        for (AbstractSettingElement settingButton : settingButtons) {
            if (!settingButton.setting.isVisible().visible()) continue;
            f += settingButton.getAdd();
        }

        return f;
    }

    private boolean shouldPlay = true;

    @Override
    public void draw(double mx, double my, float alpha) {
        rect.setH(AnimationUtils.fast(rect.getH(), open ? getSettingsHeight() : defaultHeight, 10));
        selectedAlpha = AnimationUtils.fast(selectedAlpha, rect.intersect(mx, my) ? 255 : 0, rect.intersect(mx, my) ? 10 : 5);
        if (!rect.intersect(mx, my)) shouldPlay = true;
        else if (shouldPlay && selectedAlpha > 25) {
            if (FunctionManager.get(ClickGui.class).clientSound.get()) {
                CustomSoundInstance customSoundInstance = new CustomSoundInstance(SoundManager.BUTTON_EVENT, SoundCategory.MASTER);
                customSoundInstance.setVolume(FunctionManager.get(ClickGui.class).volume.floatValue());
                mc.getSoundManager().play(customSoundInstance);
            }

            shouldPlay = false;
        }

        GL.drawQuad(rect, Utils.lerp(ColorUtils.injectAlpha(GuiScreen.clickGui.selectedModulesColor.get(), (int) (alpha * GuiScreen.clickGui.selectedModulesColor.get().getAlpha())), new Color(0, 0, 0, 0), selectedAlpha / 255));

        FontRenderer.color(function.isEnabled());

        float textureHeight = rect.getH() != defaultHeight ? defaultHeight / 1.5f : rect.getH() / 1.5f;

        switch (GuiScreen.clickGui.modulesMode.get()) {
            case "Лево" -> {
                IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, function.getName(), rect.getX() + 4, rect.getH() != defaultHeight ? rect.getY() + defaultHeight / 2 : rect.getCenteredY(), ColorUtils.injectAlpha(Utils.lerp(GuiScreen.clickGui.selectedModulesTextColor.get(), GuiScreen.clickGui.modulesTextColor.get(), selectedAlpha / 255), (int) (alpha * 255)), 9);

                if (function.isPremium())
                    TextureGL.create()
                        .bind(DownloadImage.getGlId(DownloadImage.STAR))
                        .draw(new TextureGL.TextureRegion(rect.getX() + 4 + 3 + IFont.getWidth(IFont.MONTSERRAT_BOLD, function.getName(), 9), rect.getH() != defaultHeight ? rect.getY() + defaultHeight / 2 - textureHeight / 2 : rect.getCenteredY() - textureHeight / 2, textureHeight, textureHeight), false, ColorUtils.injectAlpha(Color.ORANGE, (int) (alpha * 255)));
            }
            case "Центр" -> {
                IFont.drawCenteredXY(IFont.MONTSERRAT_BOLD, function.getName(), rect.getCenteredX(), rect.getH() != defaultHeight ? rect.getY() + defaultHeight / 2 : rect.getCenteredY(), ColorUtils.injectAlpha(Utils.lerp(GuiScreen.clickGui.selectedModulesTextColor.get(), GuiScreen.clickGui.modulesTextColor.get(), selectedAlpha / 255), (int) (alpha * 255)), 9);

                if (function.isPremium())
                    TextureGL.create()
                        .bind(DownloadImage.getGlId(DownloadImage.STAR))
                        .draw(new TextureGL.TextureRegion(rect.getCenteredX() + 3 + IFont.getWidth(IFont.MONTSERRAT_BOLD, function.getName(), 9) / 2, rect.getH() != defaultHeight ? rect.getY() + defaultHeight / 2 - textureHeight / 2 : rect.getCenteredY() - textureHeight / 2, textureHeight, textureHeight), false, ColorUtils.injectAlpha(Color.ORANGE, (int) (alpha * 255)));
            }
            default -> {
                IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, function.getName(), rect.getX2() - 4 - 3 - (rect.getH() / 1.5f) - IFont.getWidth(IFont.MONTSERRAT_BOLD, function.getName(), 9), rect.getH() != defaultHeight ? rect.getY() + defaultHeight / 2 : rect.getCenteredY(), ColorUtils.injectAlpha(Utils.lerp(GuiScreen.clickGui.selectedModulesTextColor.get(), GuiScreen.clickGui.modulesTextColor.get(), selectedAlpha / 255), (int) (alpha * 255)), 9);

                if (function.isPremium())
                    TextureGL.create()
                        .bind(DownloadImage.getGlId(DownloadImage.STAR))
                        .draw(new TextureGL.TextureRegion(rect.getX2() - 4 - (function.isPremium() ? (textureHeight / 1.5f) : 0), rect.getH() != defaultHeight ? rect.getY() + defaultHeight / 2 - textureHeight / 2 : rect.getCenteredY() - textureHeight / 2, textureHeight, textureHeight), false, ColorUtils.injectAlpha(Color.ORANGE, (int) (alpha * 255)));
            }
        };

        FontRenderer.color(false);
    }

    public void postRender(double mx, double my, float alpha) {
        ScissorUtils.push();
        ScissorUtils.setFromComponentCoordinates(rect.getX(), rect.getY(), rect.getW(), rect.getH());
        float y = rect.getY() + defaultHeight;
        for (AbstractSettingElement settingButton : settingButtons) {
            settingButton.rect.setX(rect.getX());
            settingButton.rect.setY(y);
            if (!settingButton.setting.isVisible().visible()) continue;
            settingButton.draw(mx, my, alpha);
            y += settingButton.getAdd();
        }
        ScissorUtils.unset();
        ScissorUtils.pop();
    }

    @Override
    public void click(double mx, double my, int button) {
        namedRect = new FloatRect(rect.getX().floatValue(), rect.getY().floatValue(), rect.getW().floatValue(), defaultHeight);

        if (namedRect.intersect(mx, my)) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1 && (!function.isPremium() || Loader.isPremium())) {
                if (function instanceof ClickGui) {
                    GuiScreen.closeInvoke = true;
                    return;
                }

                function.toggle();
            }
            if (button == GLFW.GLFW_MOUSE_BUTTON_2 && !settingButtons.isEmpty()) {
                open = !open;
            }
        }

        if (open) {
            for (AbstractSettingElement settingButton : settingButtons) {
                if (!settingButton.setting.isVisible().visible() || (settingButton.setting.isPremium && !Loader.isPremium())) continue;
                settingButton.click(mx, my, button);
            }
        }
    }

    @Override
    public void release(double mx, double my, int button) {
        if (open) {
            for (AbstractSettingElement settingButton : settingButtons) {
                if (!settingButton.setting.isVisible().visible() || (settingButton.setting.isPremium && !Loader.isPremium())) continue;
                settingButton.release(mx, my, button);
            }
        }
    }

    @Override
    public void key(int key) {
        if (open) {
            for (AbstractSettingElement settingButton : settingButtons) {
                if (!settingButton.setting.isVisible().visible() || (settingButton.setting.isPremium && !Loader.isPremium())) continue;
                settingButton.key(key);
            }
        }
    }

    @Override
    public void symbol(char chr) {
        if (open) {
            for (AbstractSettingElement settingButton : settingButtons) {
                if (!settingButton.setting.isVisible().visible() || (settingButton.setting.isPremium && !Loader.isPremium())) continue;
                settingButton.symbol(chr);
            }
        }
    }

    @Override
    public void scroll(double mx, double my, double amount) {
        if (open) {
            for (AbstractSettingElement settingButton : settingButtons) {
                if (!settingButton.setting.isVisible().visible() || (settingButton.setting.isPremium && !Loader.isPremium())) continue;
                settingButton.scroll(mx, my, amount);
            }
        }
    }

    @Override
    public void close() {

    }
}
