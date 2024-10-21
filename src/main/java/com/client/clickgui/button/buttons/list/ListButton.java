package com.client.clickgui.button.buttons.list;

import com.client.clickgui.button.SettingButton;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.auth.Loader;
import com.client.utils.color.ColorUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.TextureGL;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListButton extends SettingButton {
    private final List<ListButtonValue> listButtonValues = new ArrayList<>();
    private float offset = 0;

    public ListButton(AbstractSettings<?> setting, FloatRect rect) {
        super(setting, rect);

        for (String value : ((ListSetting) setting).getList()) {
            listButtonValues.add(new ListButtonValue(new FloatRect(), () -> ((ListSetting)setting).set(value), value));
        }

        try {
            listButtonValues.sort(Comparator.comparing(s -> IFont.getWidth(IFont.MONTSERRAT_MEDIUM, s.text, 7)));
        } catch (Exception ignored) {
        }

        float x = rect.getX() + 4;
        float y = rect.getY2() + 2;
        float h = 0;

        for (ListButtonValue value : listButtonValues) {
            String name = value.text;
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
        if (setting.isPremium) TextureGL.create().bind(new Identifier("bloody-client", "/client/crown.png")).draw(new TextureGL.TextureRegion(rect.getX() + 6 + IFont.getWidth(IFont.MONTSERRAT_MEDIUM, setting.name, 7), rect.getY(), rect.getH(), rect.getH()), true, ColorUtils.injectAlpha(new Color(255, 174, 0), (int) (alpha * 255)));

        float x = rect.getX() + 4;
        float y = rect.getY2() + 2;

        for (ListButtonValue listButtonValue : listButtonValues) {
            String name = listButtonValue.text;
            float nw = IFont.getWidth(IFont.MONTSERRAT_MEDIUM, name, 7) + 4;
            float nh = IFont.getHeight(IFont.MONTSERRAT_MEDIUM, name, 7) + 2;

            if (x + nw > rect.getX2() - 4) {
                y += nh + 2;
                x = rect.getX() + 4;
            }

            listButtonValue.rect.setX(x).setY(y).setW(nw);
            listButtonValue.draw(mx, my, alpha, ((ListSetting) setting).get());
            x += nw + 2;
        }
    }

    @Override
    public void click(double mx, double my, int button) {
        if (setting.isPremium && !Loader.isPremium()) return;
        for (ListButtonValue listButtonValue : listButtonValues) {
            listButtonValue.click(mx, my, button);
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
