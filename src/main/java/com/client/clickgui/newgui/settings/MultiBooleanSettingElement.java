package com.client.clickgui.newgui.settings;

import com.client.clickgui.newgui.GuiScreen;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.Utils;
import com.client.utils.color.Colors;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.api.FontRenderer;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.utils.ScissorUtils;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

public class MultiBooleanSettingElement extends AbstractSettingElement {
    private FloatRect interactRect = new FloatRect(), namedRect = new FloatRect();
    public boolean expanded = false;
    public double anim = 0;
    public String name;

    public MultiBooleanSettingElement(AbstractSettings<?> setting, FloatRect rect) {
        super(setting, rect);
        this.name = Utils.isRussianLanguage || setting.getEnName() == null || setting.getEnName().isEmpty() ? setting.getName() : setting.getEnName();
    }

    public void setNameHeight(double height) {
        this.name = parse(Utils.isRussianLanguage || setting.getEnName() == null || setting.getEnName().isEmpty() ? setting.getName() : setting.getEnName(), rect.getW() - height, IFont.MONTSERRAT_BOLD, 6);
        //rect.setH(Math.max(rect.getH(), IFont.getHeight(IFont.MONTSERRAT_BOLD, name, 6)));
    }

    public void tickAnimations() {
        double d = 0.06;
        if (!expanded) {
            d *= -1;
        }
        anim += d;
        anim = MathHelper.clamp(anim, 0, 1);
    }

    @Override
    public float getAdd() {
        return rect.getH() + 2;
    }

    public void updateRectHeight() {
        AtomicReference<Float> elementsHeight = new AtomicReference<>((float) 0);

        if (expanded)
            ((MultiBooleanSetting) setting).get().forEach(e -> {
                elementsHeight.updateAndGet(v -> (v + IFont.getHeight(IFont.MONTSERRAT_BOLD, e.getName(), 7) + 2));
            });

        rect.setH(AnimationUtils.fast(rect.getH(), expanded ? namedRect.getH() + elementsHeight.get() : namedRect.getH(), 10));

        namedRect.setX(rect.getX());
        namedRect.setY(rect.getY());
    }

    @Override
    public void draw(double mx, double my, float alpha) {
        updateRectHeight();
        tickAnimations();

        setNameHeight(interactRect.getW() + 10);

        String mode = ((MultiBooleanSetting)setting).getChecked().size() + "/" + ((MultiBooleanSetting)setting).get().size();
        namedRect = new FloatRect(rect.getX().floatValue(), rect.getY().floatValue(), rect.getW().floatValue(), Math.max(IFont.getHeight(IFont.MONTSERRAT_BOLD, mode, 6), IFont.getHeight(IFont.MONTSERRAT_BOLD, name, 6)) + 2);
        interactRect = new FloatRect(namedRect.getX2() - 4 - IFont.getWidth(IFont.MONTSERRAT_BOLD, mode, 6), namedRect.getCenteredY() - IFont.getHeight(IFont.MONTSERRAT_BOLD, mode, 6) / 2 - 1, IFont.getWidth(IFont.MONTSERRAT_BOLD, mode, 6), IFont.getHeight(IFont.MONTSERRAT_BOLD, mode, 6) + 2);

        Color invactive = GuiScreen.clickGui.settingsTextColor.get();
        Color active = GuiScreen.clickGui.settingsElementsTextColor.get();

        GL.drawRoundedGradientRect(new FloatRect(interactRect.getX() - 1, interactRect.getY().floatValue(), interactRect.getW() + 2, interactRect.getH().floatValue()), 1, inject(Colors.getColor(0), alpha), inject(Colors.getColor(90), alpha), inject(Colors.getColor(270), alpha), inject(Colors.getColor(180), alpha));

        IFont.drawCenteredXY(IFont.MONTSERRAT_BOLD, mode, interactRect.getCenteredX(), interactRect.getCenteredY(), inject(active, alpha), 6);
        IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, name, rect.getX() + 8, namedRect.getCenteredY(), inject(invactive, alpha), 6);

        if (anim > 0) {
            ScissorUtils.push();
            ScissorUtils.setFromComponentCoordinates(rect.getX(), rect.getY(), rect.getW(), rect.getH());
            float y = namedRect.getY2() + 2;
            for (MultiBooleanValue listElement : ((MultiBooleanSetting) setting).get()) {

                if (listElement.getValue()) FontRenderer.color(true);
                IFont.drawCenteredX(IFont.MONTSERRAT_BOLD, listElement.getName(), rect.getCenteredX(), y, inject(active, alpha), 7);
                FontRenderer.color(false);

                y += IFont.getHeight(IFont.MONTSERRAT_BOLD, listElement.getName(), 7) + 2;
            }
            ScissorUtils.unset();
            ScissorUtils.pop();
        }
    }

    @Override
    public void click(double mx, double my, int button) {
        if (namedRect.intersect(mx, my)) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1 || button == GLFW.GLFW_MOUSE_BUTTON_2) {
                expanded = !expanded;
            }
            if (button == GLFW.GLFW_MOUSE_BUTTON_3) {
                ((MultiBooleanSetting) setting).set(((MultiBooleanSetting) setting).getDefaultValue());
            }
        } else if (rect.intersect(mx, my)) {
            float y = namedRect.getY2() + 2;
            for (MultiBooleanValue listElement : ((MultiBooleanSetting) setting).get()) {
                float height = IFont.getHeight(IFont.MONTSERRAT_BOLD, listElement.getName(), 7) + 2;

                if (new FloatRect(rect.getX().floatValue(), y, rect.getW().floatValue(), height).intersect(mx, my)) {
                    listElement.setValue(!listElement.getValue());
                }

                y += height;
            }
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