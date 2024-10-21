package com.client.clickgui.button.buttons.multiboolean;

import com.client.clickgui.button.SettingButton;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.color.ColorUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.TextureGL;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MultiBooleanButton extends SettingButton {
    private final List<MultiBooleanButtonValue> booleanButtonValues = new ArrayList<>();
    private float offset = 0;

    public MultiBooleanButton(AbstractSettings<?> setting, FloatRect rect) {
        super(setting, rect);

        for (MultiBooleanValue value : ((MultiBooleanSetting) setting).get()) {
            booleanButtonValues.add(new MultiBooleanButtonValue(new FloatRect(), value));
        }

        try {
            booleanButtonValues.sort(Comparator.comparing(s -> IFont.getWidth(IFont.MONTSERRAT_MEDIUM, s.value.getName(), 7)));
        } catch (Exception ignored) {
        }

        float x = rect.getX() + 4;
        float y = rect.getY2() + 2;
        float h = 0;

        for (MultiBooleanButtonValue value : booleanButtonValues) {
            String name = value.value.getName();
            float nw = IFont.getWidth(IFont.MONTSERRAT_MEDIUM, name, 7) + 4;
            float nh = IFont.getHeight(IFont.MONTSERRAT_MEDIUM, name, 7) + 2;
            h = nh;

            if (x + nw > rect.getX2() - 4) {
                offset += nh + 2;
                y += nh + 2;
                x = rect.getX() + 4;
            }

            value.rect.setX(x).setY(y).setW(nw).setH(nh);
            x += nw + 2;
        }

        offset += h + 2;
    }

    @Override
    public float getH() {
        return getHeight();
    }

    public float getHeight() {
        return rect.getH() + offset;
    }

    @Override
    public void draw(double mx, double my, float alpha) {
        IFont.draw(IFont.MONTSERRAT_MEDIUM, setting.getName(), rect.getX() + 4, rect.getY(), inject(Color.WHITE, alpha), 7);

        IFont.draw(IFont.MONTSERRAT_MEDIUM, ((MultiBooleanSetting) setting).toggledCount(), rect.getX2() - 4 - IFont.getWidth(IFont.MONTSERRAT_MEDIUM, ((MultiBooleanSetting) setting).toggledCount(), 7), rect.getY(), inject(Color.WHITE, alpha), 7);
        if (setting.isPremium) TextureGL.create().bind(new Identifier("bloody-client", "/client/crown.png")).draw(new TextureGL.TextureRegion(rect.getX() + 6 + IFont.getWidth(IFont.MONTSERRAT_MEDIUM, setting.name, 7), rect.getY(), rect.getH(), rect.getH()), true, ColorUtils.injectAlpha(new Color(255, 174, 0), (int) (alpha * 255)));

        float x = rect.getX() + 4;
        float y = rect.getY2() + 2;

        for (MultiBooleanButtonValue booleanButtonValue : booleanButtonValues) {
            String name = booleanButtonValue.value.getName();
            float nw = IFont.getWidth(IFont.MONTSERRAT_MEDIUM, name, 7) + 4;
            float nh = IFont.getHeight(IFont.MONTSERRAT_MEDIUM, name, 7) + 2;

            if (x + nw > rect.getX2() - 4) {
                y += nh + 2;
                x = rect.getX() + 4;
            }

            booleanButtonValue.rect.setX(x).setY(y).setW(nw);
            booleanButtonValue.drawValue(mx, my, alpha);
            x += nw + 2;
        }
    }

    @Override
    public void click(double mx, double my, int button) {
        for (MultiBooleanButtonValue booleanButtonValue : booleanButtonValues) {
            booleanButtonValue.click(mx, my, button);
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
