package com.client.clickgui.newgui.settings.color;

import com.client.clickgui.newgui.settings.AbstractSettingElement;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.settings.ColorSetting;
import com.client.utils.Utils;
import com.client.utils.color.ColorUtils;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.utils.ScissorUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

import static com.client.BloodyClient.mc;

public class ColorSettingElement extends AbstractSettingElement {
    private FloatRect interactRect = new FloatRect(), namedRect = new FloatRect();
    private FloatRect colorRect;
    private final HueSlider hueSlider;
    private final AlphaSlider alphaSlider;
    private boolean dragged, intersect;
    private double saturation, brightness;
    public double anim = 0;
    public boolean expanded = false;
    public String name;

    public ColorSettingElement(AbstractSettings<?> setting, FloatRect rect) {
        super(setting, rect);
        colorRect = new FloatRect(rect.getX() + 8, rect.getY() + IFont.getHeight(IFont.MONTSERRAT_BOLD, Utils.isRussianLanguage || setting.getEnName() == null || setting.getEnName().isEmpty() ? setting.getName() : setting.getEnName(), 9), 64, 64);
        hueSlider = new HueSlider(new FloatRect());
        alphaSlider = new AlphaSlider(new FloatRect());
        hueSlider.task = this::setColor;
        alphaSlider.task = () -> ((ColorSetting) setting).set(ColorUtils.injectAlpha(((ColorSetting) setting).get(), (int) (alphaSlider.getAlpha() * 255)));
        Color color = ((ColorSetting) setting).get();
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        alphaSlider.targetPos = 64 - ((float) ((ColorSetting) setting).get().getAlpha() / 255f) * 64;
        hueSlider.value = hsb[0];
        saturation = hsb[1];
        brightness = hsb[2];
        this.name = Utils.isRussianLanguage || setting.getEnName() == null || setting.getEnName().isEmpty() ? setting.getName() : setting.getEnName();
    }

    public void setNameHeight(double height) {
        this.name = parse(Utils.isRussianLanguage || setting.getEnName() == null || setting.getEnName().isEmpty() ? setting.getName() : setting.getEnName(), rect.getW() - height, IFont.MONTSERRAT_BOLD, 6);
        //rect.setH(Math.max(rect.getH(), IFont.getHeight(IFont.MONTSERRAT_BOLD, name, 6)));
    }

    @Override
    public float getAdd() {
        return rect.getH() + 2;
    }

    public void tickAnimations() {
        double delta = 0.06;
        if (!expanded) {
            delta *= -1;
        }
        anim += delta;
        anim = MathHelper.clamp(anim, 0, 1);
    }

    @Override
    public void draw(double mx, double my, float alpha) {
        tickAnimations();
        rect.setH(AnimationUtils.fast(rect.getH(), expanded ? 80 : namedRect.getH(), 10));

        setNameHeight(interactRect.getW() + 6);

        namedRect = new FloatRect(rect.getX().floatValue(), rect.getY().floatValue(), rect.getW().floatValue(),IFont.getHeight(IFont.MONTSERRAT_BOLD, name, 6) + 2);
        interactRect = new FloatRect(namedRect.getX2() - 4 - 12, namedRect.getCenteredY() - 4, 12, 8);

        IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, name, namedRect.getX() + 8, namedRect.getCenteredY(), inject(Color.WHITE.darker(), alpha), 6);

        GL.drawRoundedRect(interactRect, 3, inject(((ColorSetting) setting).get(), alpha));

        if (anim > 0) {
            ScissorUtils.push();
            ScissorUtils.setFromComponentCoordinates(rect.getX(), rect.getY(), rect.getW(), rect.getH());

            buildRect();
            intersect = colorRect.intersect(mx, my);

            if (dragged) {
                saturation = MathHelper.clamp((mx - colorRect.getX()) / colorRect.getW(), 0, 1);
                brightness = MathHelper.clamp(1f - (my - colorRect.getY()) / colorRect.getH(), 0, 1);
                setColor();
            }

            GL.drawRoundedGradientRect(colorRect, 4, inject(Color.WHITE, alpha), inject(Color.BLACK, alpha), inject(Color.getHSBColor((float) hueSlider.value, 1f, 1f), alpha), inject(Color.BLACK, alpha));

            FloatRect dot = new FloatRect(colorRect.getX() + (colorRect.getW() * saturation), colorRect.getY() + (colorRect.getH() - (colorRect.getH() * brightness)), 0, 0);
            GL.drawLine(dot.getX() - 2, dot.getY(), dot.getX() + 2, dot.getY(), 2f, inject(Color.WHITE, alpha));
            GL.drawLine(dot.getX(), dot.getY() - 2, dot.getX(), dot.getY() + 2, 2f, inject(Color.WHITE, alpha));

            hueSlider.draw(mx, my, alpha);
            alphaSlider.draw(mx, my, alpha);

            ScissorUtils.unset();
            ScissorUtils.pop();
        }
    }

    private void setColor() {
        ((ColorSetting) setting).set(ColorUtils.injectAlpha(Color.getHSBColor((float) hueSlider.value, (float) saturation, (float) brightness), ((ColorSetting) setting).get().getAlpha()));
    }

    private void buildRect() {
        colorRect = new FloatRect(rect.getX() + 8, rect.getY() + IFont.getHeight(IFont.MONTSERRAT_BOLD, name, 9), 64, 64);
        float w = (rect.getX2() - colorRect.getX2()) / 2;

        hueSlider.rect = new FloatRect(colorRect.getX2() + w / 2, colorRect.getY(), 4, colorRect.getH());
        alphaSlider.rect = new FloatRect(colorRect.getX2() + w + w / 2 - 4, hueSlider.rect.getY(), 4, hueSlider.rect.getH());
    }

    @Override
    public void click(double mx, double my, int button) {
        if (interactRect.intersect(mx, my)) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1 || button == GLFW.GLFW_MOUSE_BUTTON_2) {
                expanded = !expanded;
            }
            if (button == GLFW.GLFW_MOUSE_BUTTON_3) {
                ((ColorSetting) setting).set(((ColorSetting) setting).getDefaultValue());
            }
        }

        if (expanded) {
            if (colorRect.intersect(mx, my) && button == GLFW.GLFW_MOUSE_BUTTON_1) {
                dragged = true;
            }

            if (rect.intersect(mx, my) && button == GLFW.GLFW_MOUSE_BUTTON_3) {
                ((ColorSetting) setting).set(((ColorSetting) setting).getDefaultValue());
                Color color = ((ColorSetting) setting).get();
                float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
                alphaSlider.targetPos = 64 - ((float) color.getAlpha() / 255f) * 64;
                hueSlider.value = hsb[0];
                saturation = hsb[1];
                brightness = hsb[2];
            }

            hueSlider.click(mx, my, button);
            alphaSlider.click(mx, my, button);
        }
    }

    @Override
    public void release(double mx, double my, int button) {
        if (expanded) {
            dragged = false;
            hueSlider.release(mx, my, button);
            alphaSlider.release(mx, my, button);
        }
    }

    @Override
    public void key(int key) {
        if (expanded && intersect && Screen.hasControlDown()) {
            if (key == GLFW.GLFW_KEY_V) {
                String clipboard = mc.keyboard.getClipboard();
                if (clipboard.isEmpty()) return;
                int rgba;
                try {
                    rgba = Integer.parseInt(clipboard);
                } catch (Exception e) {
                    return;
                }
                ((ColorSetting) setting).set(new Color(rgba));
                Color color = ((ColorSetting) setting).get();
                float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
                alphaSlider.targetPos = 64 - ((float) color.getAlpha() / 255f) * 64;
                hueSlider.value = hsb[0];
                saturation = hsb[1];
                brightness = hsb[2];
                NotificationManager.add(new Notification(NotificationType.CLIENT, "Цвет был загружен"), NotificationManager.NotifType.Info);
            }
            if (key == GLFW.GLFW_KEY_C) {
                mc.keyboard.setClipboard("" + ((ColorSetting) setting).get().getRGB());
                NotificationManager.add(new Notification(NotificationType.CLIENT, "Цвет был скопирован"), NotificationManager.NotifType.Info);
            }
        }
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
