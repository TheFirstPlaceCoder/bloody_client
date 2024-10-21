package com.client.clickgui.button.buttons.colorpicker;

import com.client.clickgui.button.SettingButton;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.settings.ColorSetting;
import com.client.utils.color.ColorUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

import static com.client.BloodyClient.mc;

public class ColorPickerButton extends SettingButton {
    private FloatRect colorRect;
    private final HueSlider hueSlider;
    private final AlphaSlider alphaSlider;
    private boolean dragged, intersect;
    private double saturation, brightness;

    public ColorPickerButton(AbstractSettings<?> setting, FloatRect rect) {
        super(setting, rect);
        colorRect = new FloatRect(rect.getX() + 4, rect.getY() + 16, 64, 64);
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
    }

    @Override
    public void draw(double mx, double my, float alpha) {
        buildRect();
        intersect = colorRect.intersect(mx, my);

        if (dragged) {
            saturation = MathHelper.clamp((mx - colorRect.getX()) / colorRect.getW(), 0, 1);
            brightness = MathHelper.clamp(1f - (my - colorRect.getY()) / colorRect.getH(), 0, 1);
            setColor();
        }

        IFont.draw(IFont.MONTSERRAT_MEDIUM, setting.getName(), rect.getX() + 4, rect.getY() + 8 - IFont.getHeight(IFont.MONTSERRAT_MEDIUM, setting.getName(), 7) / 2, inject(Color.WHITE, alpha), 7);
        GL.drawRoundedBlurredRect(new FloatRect(rect.getX2() - 12, rect.getY() + 4, 8, 8), 4, 1, inject(((ColorSetting) setting).get(), alpha));

        GL.drawRoundedGradientRect(colorRect, 4, inject(Color.WHITE, alpha), inject(Color.BLACK, alpha), inject(Color.getHSBColor((float) hueSlider.value, 1f, 1f), alpha), inject(Color.BLACK, alpha));

        FloatRect dot = new FloatRect(colorRect.getX() + (colorRect.getW() * saturation), colorRect.getY() + (colorRect.getH() - (colorRect.getH() * brightness)), 0, 0);
        GL.drawLine(dot.getX() - 2, dot.getY(), dot.getX() + 2, dot.getY(), 2f, inject(Color.WHITE, alpha));
        GL.drawLine(dot.getX(), dot.getY() - 2, dot.getX(), dot.getY() + 2, 2f, inject(Color.WHITE, alpha));

        hueSlider.draw(mx, my, alpha);
        alphaSlider.draw(mx, my, alpha);
    }

    private void setColor() {
        ((ColorSetting) setting).set(ColorUtils.injectAlpha(Color.getHSBColor((float) hueSlider.value, (float) saturation, (float) brightness), ((ColorSetting) setting).get().getAlpha()));
    }

    private void buildRect() {
        colorRect = new FloatRect(rect.getX() + 4, rect.getY() + 16, 64, 64);
        float w = (rect.getX2() - colorRect.getX2()) / 2;

        hueSlider.rect = new FloatRect(colorRect.getX2() + w / 2, colorRect.getY(), 4, colorRect.getH());
        alphaSlider.rect = new FloatRect(colorRect.getX2() + w + w / 2, hueSlider.rect.getY(), 4, hueSlider.rect.getH());
    }

    @Override
    public void click(double mx, double my, int button) {
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

    @Override
    public void release(double mx, double my, int button) {
        dragged = false;
        hueSlider.release(mx, my, button);
        alphaSlider.release(mx, my, button);
    }

    @Override
    public void key(int key) {
        if (intersect && Screen.hasControlDown()) {
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
