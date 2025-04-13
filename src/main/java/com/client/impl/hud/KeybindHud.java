package com.client.impl.hud;

import com.client.impl.function.client.ClickGui;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.hud.HudFunction;
import com.client.system.hud.HudManager;
import com.client.system.textures.DownloadImage;
import com.client.utils.math.animation.Animation;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.math.animation.Direction;
import com.client.utils.math.animation.impl.EaseBackIn;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.misc.InputUtils;
import com.client.utils.render.DrawMode;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.main.TextureGL;
import com.client.utils.render.wisetree.render.render2d.utils.ScissorUtils;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Formatting;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class KeybindHud extends HudFunction {
    public KeybindHud() {
        super(new FloatRect(106, 61, 100, 16), "Keybind-Hud");
    }

    public FloatRect bindRect = new FloatRect();
    private List<Function> bindList = new CopyOnWriteArrayList<>();
    private List<Function> toDraw = new CopyOnWriteArrayList<>();
    private final Animation animation = new EaseBackIn(400, 1, 1);
    private double sc;

    @Override
    public void tick() {
        bindRect.setX(rect.getX());
        bindRect.setW(rect.getW());
        bindRect.setY(rect.getY() + 18);
        if (bindList.isEmpty()) bindList = FunctionManager.getFunctionList();

        toDraw = bindList.stream().filter(f -> !(f instanceof ClickGui) && f.isEnabled() && f.getKeyCode() != -1).toList();
        bindRect.setH(AnimationUtils.fast(bindRect.getH(), toDraw.isEmpty() ? 0 : toDraw.size() * 12 + 2));

        if (bindRect.getH() < 2 && toDraw.isEmpty()) bindRect.setH(0f);

        rect.setH(16 + bindRect.getH() + 2);
    }

    @Override
    public void draw(float alpha) {
        animation.setDirection(toDraw.isEmpty() && !(mc.currentScreen instanceof ChatScreen) ? Direction.BACKWARDS : Direction.FORWARDS);

        sc = animation.getOutput();
        if (sc <= 0.0f) return;

        startScale(sc);

        drawNewClientRect(new FloatRect(rect.getX(), rect.getY(), rect.getW(), 16));

        GL11.glPushMatrix();
        GL11.glScalef(1f, 1f, 1f);
        GL11.glTranslatef(rect.getX() + 2.5f, rect.getY(), 0);
        GL.drawRoundedTexture(DownloadImage.getGlId(DownloadImage.KEYBOARD), 0, 0, 16, 16, 0);
        GL11.glPopMatrix();

        IFont.drawCenteredXY(IFont.MONTSERRAT_BOLD, "Keybinds", rect.getCenteredX(), rect.getY() + 8, Color.WHITE, 9);

        if (bindRect.getH() > 0) drawNewClientRect(bindRect);

        ScissorUtils.enableScissor(bindRect);
        float y = bindRect.getY() + 1;
        double w = 0;
        for (Function function : toDraw) {
            IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, function.getName(), bindRect.getX() + 4, y + (float) 12 / 2, Color.WHITE, 7);
            String bind = Formatting.GRAY + "[" + Formatting.WHITE
                    + (function.getKeyCode() > 90000 ? InputUtils.getButtonName(function.getKeyCode() - 90001) : InputUtils.getKeyName(function.getKeyCode()))
                    + Formatting.GRAY + "]";
            IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, bind, bindRect.getX2() - 4 - IFont.getWidth(IFont.MONTSERRAT_BOLD, bind, 7), y + (float) 12 / 2, Color.WHITE, 7);

            double tempW = 14 +
                    IFont.getWidth(IFont.MONTSERRAT_BOLD,
                            function.getName(),
                            7)
                    + IFont.getWidth(IFont.MONTSERRAT_BOLD, "   " + bind, 7);

            if (tempW > w) {
                w = tempW;
            }
            y += 12;
        }
        if (w > 100) {
            w = w + 10;
        } else {
            w = 100;
        }
        rect.setW(AnimationUtils.fast(rect.getW().floatValue(), (float) w));
        ScissorUtils.disableScissor();

        endScale();
    }
}
