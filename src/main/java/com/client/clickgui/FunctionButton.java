package com.client.clickgui;

import com.client.clickgui.button.SettingButton;
import com.client.clickgui.button.buttons.*;
import com.client.clickgui.button.buttons.colorpicker.ColorPickerButton;
import com.client.clickgui.button.buttons.list.ListButton;
import com.client.clickgui.button.buttons.multiboolean.MultiBooleanButton;
import com.client.clickgui.button.buttons.theme.ThemeButton;
import com.client.impl.function.client.ClickGui;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.manager.SettingManager;
import com.client.utils.auth.Loader;
import com.client.utils.color.ColorTransfusion;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.misc.InputUtils;
import com.client.utils.render.wisetree.font.api.FontRenderer;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.main.TextureGL;
import com.client.utils.render.wisetree.render.render2d.utils.ScissorUtils;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FunctionButton implements Impl {
    private final Identifier arrow = new Identifier("bloody-client", "/client/function_arrow.png");
    public final List<SettingButton> settingButtons = new ArrayList<>();

    private boolean empty = true;
    private float rotate = 0;
    public final float defaultHeight;
    private final TextAnimation textAnimation;

    public boolean open = false;
    private boolean binding, intersect;

    public FloatRect rect, familyRect;
    public final FloatRect interactRect;
    public Function function;

    private final ColorTransfusion arrow_color = new ColorTransfusion(Color.GRAY);

    public FunctionButton(FloatRect rect, FloatRect familyRect, Function function) {
        this.rect = rect;
        this.interactRect = new FloatRect(rect.getX(), rect.getY(), rect.getW(), rect.getH());
        this.defaultHeight = rect.getH().intValue();
        this.function = function;
        this.familyRect = familyRect;
        this.textAnimation = new TextAnimation(function.getName());

        float y = rect.getY2();
        BindButton bindButton = new BindButton(function, new FloatRect(rect.getX(), y, rect.getW(), 16));
        bindButton.add = 17;
        settingButtons.add(bindButton);
        y += 17;
        for (AbstractSettings<?> abstractSettings : SettingManager.getSettingsList(function)) {
            float add = 0;
            switch (abstractSettings.getType()) {
                case Boolean -> {
                    BooleanButton booleanSetting = new BooleanButton(abstractSettings, new FloatRect(rect.getX(), y, rect.getW(), 16));
                    booleanSetting.add = 17;
                    settingButtons.add(booleanSetting);
                    add = 17;
                }
                case Keybind -> {
                    KeybindButton keybindButton = new KeybindButton(abstractSettings, new FloatRect(rect.getX(), y, rect.getW(), 16));
                    keybindButton.add = 17;
                    settingButtons.add(keybindButton);
                    add = 17;
                }
                case Integer -> {
                    IntegerButton integerButton = new IntegerButton(abstractSettings, new FloatRect(rect.getX(), y, rect.getW(), 22));
                    integerButton.add = 24;
                    settingButtons.add(integerButton);
                    add = 24;
                }
                case Double -> {
                    DoubleButton doubleButton = new DoubleButton(abstractSettings, new FloatRect(rect.getX(), y, rect.getW(), 22));
                    doubleButton.add = 24;
                    settingButtons.add(doubleButton);
                    add = 24;
                }
                case MultiBoolean -> {
                    MultiBooleanButton button = new MultiBooleanButton(abstractSettings, new FloatRect(rect.getX(), y, rect.getW(), 10));
                    button.add = button.getHeight() + 3;
                    settingButtons.add(button);
                    add = button.getHeight() + 3;
                }
                case List -> {
                    ListButton button = new ListButton(abstractSettings, new FloatRect(rect.getX(), y, rect.getW(), 10));
                    button.add = button.getHeight() + 3;
                    settingButtons.add(button);
                    add = button.getHeight() + 3;
                }
                case Color -> {
                    ColorPickerButton colorPickerButton = new ColorPickerButton(abstractSettings, new FloatRect(rect.getX(), y, rect.getW(), 80));
                    colorPickerButton.add = 86;
                    settingButtons.add(colorPickerButton);
                    add = 86;
                }
                case Widget -> {
                    WidgetButton widgetButton = new WidgetButton(abstractSettings, new FloatRect(rect.getX(), y, rect.getW(), 18));
                    widgetButton.add = 20;
                    settingButtons.add(widgetButton);
                    add = 20;
                }
                case String -> {
                    StringButton stringButton = new StringButton(abstractSettings, new FloatRect(rect.getX(), y, rect.getW(), 10));
                    stringButton.add = 12;
                    settingButtons.add(stringButton);
                    add = 12;
                }
                case Theme -> {
                    ThemeButton themeButton = new ThemeButton(abstractSettings, new FloatRect(rect.getX(), y, rect.getW(), 10));
                    themeButton.add = 12;
                    settingButtons.add(themeButton);
                    add = 12;
                }
            }
            y += add;
        }
        if (settingButtons.size() > 1)
            empty = false;
    }

    public float getMaxHeight() {
        float f = defaultHeight;

        for (SettingButton settingButton : settingButtons) {
            if (!settingButton.setting.isVisible().visible()) continue;
            f += settingButton.getAdd();
        }

        return f;
    }

    public void setY(float y) {
        rect.setY(y);
        interactRect.setY(y);
    }

    public void tick() {
        rect.setH(AnimationUtils.fast(rect.getH(), open ? getMaxHeight() : defaultHeight, 10));
        rotate = AnimationUtils.fast(rotate, open ? 90 : 0, 10);
    }

    @Override
    public void draw(double mx, double my, float alpha) {
        if (function.isEnabled() && function.isPremium() && !Loader.isPremium())
            function.toggle();

        textAnimation.tick();
        intersect = interactRect.intersect(mx, my) && familyRect.intersect(mx, my);
        if (function.isEnabled() && !FunctionManager.get(ClickGui.class).enabledMode.get().equals("Текст")) GL.drawRoundedGradientRect(rect, 3.5, ColorUtils.injectAlpha(Colors.getColor(0), (int) (alpha * 210)), ColorUtils.injectAlpha(Colors.getColor(90), (int) (alpha * 210)), ColorUtils.injectAlpha(Colors.getColor(270), (int) (alpha * 210)), ColorUtils.injectAlpha(Colors.getColor(180), (int) (alpha * 210)));
        else GL.drawRoundedGradientRect(rect, 3.5, ColorUtils.injectAlpha(Colors.getColor(0), (int) (alpha * 60)), ColorUtils.injectAlpha(Colors.getColor(90), (int) (alpha * 60)), ColorUtils.injectAlpha(Colors.getColor(270), (int) (alpha * 60)), ColorUtils.injectAlpha(Colors.getColor(180), (int) (alpha * 60)));
        GL.drawRoundedGradientOutline(rect, 3.5, 1d, ColorUtils.injectAlpha(Colors.getColor(0), (int) (alpha * 255)), ColorUtils.injectAlpha(Colors.getColor(90), (int) (alpha * 255)), ColorUtils.injectAlpha(Colors.getColor(270), (int) (alpha * 255)), ColorUtils.injectAlpha(Colors.getColor(180), (int) (alpha * 255)));
        //GL.drawRoundedGradientRect(rect, 3.5, SettingButton.inject(interactRect.intersect(mx, my) && familyRect.intersect(mx, my) && !open ? GuiScreen.BACK.brighter() : GuiScreen.BACK, alpha));
        //GL.drawRoundedRect(rect, 4, SettingButton.inject(interactRect.intersect(mx, my) && familyRect.intersect(mx, my) && !open ? GuiScreen.BACK.brighter() : GuiScreen.BACK, alpha));

        if (open) arrow_color.animate(Color.WHITE, 10);
        else arrow_color.animate(Color.GRAY, 10);

        FontRenderer.color(function.isEnabled() && !FunctionManager.get(ClickGui.class).enabledMode.get().equals("Обводка"));
        IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, textAnimation.getText(), interactRect.getX() + 4, Math.round(interactRect.getCenteredY() * 10.0f) / 10.0f, ColorUtils.injectAlpha(Color.WHITE, (int) (alpha * textAnimation.getAlpha())), 9);
        FontRenderer.color(false);

        if (function.isPremium()) {
            TextureGL.create().bind(new Identifier("bloody-client", "/client/crown.png")).draw(new TextureGL.TextureRegion(rect.getX() + 8 + IFont.getWidth(IFont.MONTSERRAT_BOLD, textAnimation.getText(), 9), interactRect.getCenteredY() - 5, 10, 10), true, ColorUtils.injectAlpha(new Color(255, 174, 0), (int) (alpha * 255)));
        }

        //GL.drawShadowRect(new FloatRect(rect.getX2() - 30, rect.getY() + 2, 23, rect.getH() - 4), GL.Direction.RIGHT, SettingButton.inject(interactRect.intersect(mx, my) && familyRect.intersect(mx, my) && !open ? GuiScreen.BACK.brighter() : GuiScreen.BACK, alpha));

        if (!empty) {
            GL11.glPushMatrix();
            GL11.glTranslatef(interactRect.getX2() - 10, interactRect.getCenteredY(), 0);
            GL11.glScalef(1F, 1F, 1F);
            GL11.glRotatef(rotate, 0, 0, 1);

            TextureGL.create().bind(arrow).draw(new TextureGL.TextureRegion(10, 10), false, arrow_color.getColor((int) (alpha * 255)));

            GL11.glPopMatrix();
        }
    }

    public void postRender(double mx, double my, float alpha) {
        FloatRect scissorRect = new FloatRect(rect);
        float m = 0;
        if (scissorRect.getY() < familyRect.getY()) {
            m = familyRect.getY() - scissorRect.getY();
            scissorRect.setY(familyRect.getY());
        }
        scissorRect.addH(-m);
        if (scissorRect.getY2() > familyRect.getY2()) {
            scissorRect.addH(-(scissorRect.getY2() - familyRect.getY2()));
        }
        ScissorUtils.push();
        ScissorUtils.setFromComponentCoordinates(scissorRect.getX(), scissorRect.getY(), scissorRect.getW(), scissorRect.getH());
        float y = rect.getY() + defaultHeight;
        for (SettingButton settingButton : settingButtons) {
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
        if (binding) {
            function.setKeyCode(90001 + button);
            textAnimation.addQueue(InputUtils.getButtonName(button), function.getName());
            return;
        }
        if (interactRect.intersect(mx, my) && familyRect.intersect(mx, my)) {
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
            if (button == GLFW.GLFW_MOUSE_BUTTON_3 && (!function.isPremium() || Loader.isPremium())) {
                textAnimation.addQueue("...");
                binding = true;
            }
        }
        if (familyRect.intersect(mx, my) && open) {
            for (SettingButton settingButton : settingButtons) {
                if (!settingButton.setting.isVisible().visible() || (settingButton.setting.isPremium && !Loader.isPremium())) continue;
                settingButton.click(mx, my, button);
            }
        }
    }

    @Override
    public void release(double mx, double my, int button) {
        if (open) {
            for (SettingButton settingButton : settingButtons) {
                if (!settingButton.setting.isVisible().visible() || (settingButton.setting.isPremium && !Loader.isPremium())) continue;
                settingButton.release(mx, my, button);
            }
        }
    }

    @Override
    public void key(int key) {
        if (binding) {
            if (key == GLFW.GLFW_KEY_DELETE) {
                function.setKeyCode(function instanceof ClickGui ? GLFW.GLFW_KEY_RIGHT_SHIFT : -1);
                textAnimation.addQueue("Бинд сброшен", function.getName());
            } else {
                function.setKeyCode(key);
                textAnimation.addQueue(InputUtils.getKeyName(key), function.getName());
            }
            binding = false;
            return;
        }
        if (intersect && key == GLFW.GLFW_KEY_DELETE) {
            function.setKeyCode(function instanceof ClickGui ? GLFW.GLFW_KEY_RIGHT_SHIFT : -1);
            textAnimation.addQueue("Бинд сброшен", function.getName());
        }
        if (open) {
            for (SettingButton settingButton : settingButtons) {
                if (!settingButton.setting.isVisible().visible() || (settingButton.setting.isPremium && !Loader.isPremium())) continue;
                settingButton.key(key);
            }
        }
    }

    @Override
    public void symbol(char chr) {
        if (open) {
            for (SettingButton settingButton : settingButtons) {
                if (!settingButton.setting.isVisible().visible() || (settingButton.setting.isPremium && !Loader.isPremium())) continue;
                settingButton.symbol(chr);
            }
        }
    }

    @Override
    public void scroll(double mx, double my, double amount) {
        if (familyRect.intersect(mx, my) && open) {
            for (SettingButton settingButton : settingButtons) {
                if (!settingButton.setting.isVisible().visible() || (settingButton.setting.isPremium && !Loader.isPremium())) continue;
                settingButton.scroll(mx, my, amount);
            }
        }
    }

    @Override
    public void close() {
        for (SettingButton settingButton : settingButtons) {
            settingButton.close();
        }
    }
}
