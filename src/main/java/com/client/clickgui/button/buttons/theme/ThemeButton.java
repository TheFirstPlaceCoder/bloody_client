package com.client.clickgui.button.buttons.theme;

import com.client.clickgui.GuiScreen;
import com.client.clickgui.button.SettingButton;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.settings.theme.ThemeContainer;
import com.client.system.setting.settings.theme.ThemeSetting;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ThemeButton extends SettingButton {
    private final List<ThemeButtonValue> themeButtonValues = new ArrayList<>();
    private FloatRect themeRect;
    private final float h;

    public ThemeButton(AbstractSettings<?> setting, FloatRect rect) {
        super(setting, rect);

        float y = rect.getY() + IFont.getHeight(IFont.MONTSERRAT_MEDIUM, setting.getName(), 7) + 4;
        float h = 1;

        for (ThemeContainer themeContainer : ((ThemeSetting) setting).getList()) {
            themeButtonValues.add(new ThemeButtonValue(new FloatRect(rect.getX() + 4, y, rect.getW() - 8, 12), themeContainer, () -> ((ThemeSetting) setting).set(themeContainer)));
            y += 14;
            h += 14;
        }

        this.h = h + 2;
        themeRect = new FloatRect(rect.getX() + 4, rect.getY() + IFont.getHeight(IFont.MONTSERRAT_MEDIUM, setting.getName(), 7) + 3, rect.getW() - 8, h);
    }

    @Override
    public float getAdd() {
        return getH() + 6;
    }

    @Override
    public float getH() {
        return rect.getH() + themeRect.getH();
    }

    @Override
    public void draw(double mx, double my, float alpha) {
        themeRect = new FloatRect(rect.getX() + 4, rect.getY() + IFont.getHeight(IFont.MONTSERRAT_MEDIUM, setting.getName(), 7) + 3, rect.getW() - 8, h);
        float y = rect.getY() + IFont.getHeight(IFont.MONTSERRAT_MEDIUM, setting.getName(), 7) + 5;
        IFont.draw(IFont.MONTSERRAT_MEDIUM, setting.getName(), rect.getX() + 4, rect.getY() + 2, inject(Color.WHITE, alpha), 7);

        GL.drawRoundedRect(themeRect, 2, inject(GuiScreen.BACK_OTHER, alpha));
        for (ThemeButtonValue themeButtonValue : themeButtonValues) {
            themeButtonValue.current = themeButtonValue.container.name().equals(((ThemeSetting)setting).get().name());
            themeButtonValue.rect.setY(y);
            themeButtonValue.draw(mx, my, alpha);
            y += 14;
        }
    }

    @Override
    public void click(double mx, double my, int button) {
        for (ThemeButtonValue themeButtonValue : themeButtonValues) {
            themeButtonValue.click(mx, my, button);
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
