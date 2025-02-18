package com.client.clickgui.newgui.settings;

import com.client.clickgui.newgui.GuiScreen;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.settings.BooleanSetting;
import com.client.utils.Utils;
import com.client.utils.color.Colors;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class BooleanSettingElement extends AbstractSettingElement {
    private FloatRect interactRect = new FloatRect();
    public double anim = 0, ballX = 0;
    public String name;

    public BooleanSettingElement(AbstractSettings<?> settings, FloatRect rect) {
        super(settings, rect);
        this.name = parse(Utils.isRussianLanguage || settings.getEnName() == null || settings.getEnName().isEmpty() ? settings.getName() : settings.getEnName(), rect.getW() - 26, IFont.MONTSERRAT_BOLD, 6);
        rect.setH(Math.max(rect.getH(), IFont.getHeight(IFont.MONTSERRAT_BOLD, name, 6)));
    }

    @Override
    public float getAdd() {
        return rect.getH() + 2;
    }

    public void tickAnimations() {
        double delta = 0.1;
        if (!((BooleanSetting) setting).get()) {
            delta *= -1;
        }
        anim += delta;
        anim = MathHelper.clamp(anim, 0, 1);
    }

    @Override
    public void draw(double mx, double my, float alpha) {
        tickAnimations();

        interactRect = new FloatRect(rect.getX2() - 4 - 16, rect.getCenteredY() - 4, 16, 8);

        Color invactive = GuiScreen.clickGui.settingsTextColor.get();
        Color active = GuiScreen.clickGui.settingsElementsTextColor.get();

        IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, name, rect.getX() + 8, rect.getCenteredY(), inject(invactive, alpha), 6);

        GL.drawRoundedGradientRect(interactRect, 3, Utils.lerp(inject(Colors.getColor(0), alpha), inject(invactive, alpha), anim), Utils.lerp(inject(Colors.getColor(90), alpha), inject(invactive, alpha), anim), Utils.lerp(inject(Colors.getColor(270), alpha), inject(invactive, alpha), anim), Utils.lerp(inject(Colors.getColor(180), alpha), inject(invactive, alpha), anim));

        ballX = Utils.lerp(interactRect.getX() + 4, interactRect.getX2() - 2 - 2, anim);
        GL.drawCircle(ballX, interactRect.getCenteredY(), 2.5, 0, inject(active, alpha));
    }

    @Override
    public void click(double mx, double my, int button) {
        if (interactRect.intersect(mx, my)) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                ((BooleanSetting) setting).set(!((BooleanSetting) setting).get());
            }
            if (button == GLFW.GLFW_MOUSE_BUTTON_3) {
                ((BooleanSetting) setting).set(((BooleanSetting) setting).getDefaultValue());
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