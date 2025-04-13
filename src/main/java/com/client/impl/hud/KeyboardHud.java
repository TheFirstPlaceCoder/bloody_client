package com.client.impl.hud;

import com.client.system.hud.HudFunction;
import com.client.utils.Utils;
import com.client.utils.color.Colors;
import com.client.utils.math.animation.Animation;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.math.animation.Direction;
import com.client.utils.math.animation.impl.EaseBackIn;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import net.minecraft.client.gui.screen.ChatScreen;

import java.awt.*;

public class KeyboardHud extends HudFunction {
    public KeyboardHud() {
        super(new FloatRect(106, 22, 66, 66), "Keyboard-Hud");
    }

    private float alphaForward = 0,
            alphaBack = 0,
            alphaRight = 0,
            alphaLeft = 0,
            alphaJump = 0;

    private FloatRect forwardRect = new FloatRect(0, 0, 20, 20),
            backRect = new FloatRect(0, 0, 20, 20),
            rightRect = new FloatRect(0, 0, 20, 20),
            leftRect = new FloatRect(0, 0, 20, 20),
            jumpRect = new FloatRect(0, 0, 60 + 3 + 3, 20);

    private final Animation animation = new EaseBackIn(400, 1, 1);
    private double sc;

    @Override
    public void tick() {
        animation.setDirection(!isEnabled() && !(mc.currentScreen instanceof ChatScreen) ? Direction.BACKWARDS : Direction.FORWARDS);

        sc = animation.getOutput();
    }

    @Override
    public void draw(float alpha) {
        if (sc <= 0) return;

        startScale(sc);

        forwardRect.setX(rect.getX() + 20 + 3);
        forwardRect.setY(rect.getY());

        backRect.setX(rect.getX() + 20 + 3);
        backRect.setY(rect.getY() + 20 + 3);

        rightRect.setX(rect.getX() + 20 + 3 + 20 + 3);
        rightRect.setY(rect.getY() + 20 + 3);

        leftRect.setX(rect.getX());
        leftRect.setY(rect.getY() + 20 + 3);

        jumpRect.setX(rect.getX());
        jumpRect.setY(rect.getY() + 20 + 3 + 20 + 3);

        alphaForward = AnimationUtils.fast(alphaForward, mc.options.keyForward.isPressed() ? 255 : 0, mc.options.keyForward.isPressed() ? 10 : 5);
        alphaBack = AnimationUtils.fast(alphaBack, mc.options.keyBack.isPressed() ? 255 : 0, mc.options.keyBack.isPressed() ? 10 : 5);
        alphaRight = AnimationUtils.fast(alphaRight, mc.options.keyRight.isPressed() ? 255 : 0, mc.options.keyRight.isPressed() ? 10 : 5);
        alphaLeft = AnimationUtils.fast(alphaLeft, mc.options.keyLeft.isPressed() ? 255 : 0, mc.options.keyLeft.isPressed() ? 10 : 5);
        alphaJump = AnimationUtils.fast(alphaJump, mc.options.keyJump.isPressed() ? 255 : 0, mc.options.keyJump.isPressed() ? 10 : 5);

        drawButton(forwardRect, alphaForward, "W");
        drawButton(backRect, alphaBack, "S");
        drawButton(rightRect, alphaRight, "D");
        drawButton(leftRect, alphaLeft, "A");
        drawButton(jumpRect, alphaJump, "SPACE");

        endScale();
    }

    public void drawButton(FloatRect rect, float selectedAlpha, String name) {
        GL.drawRoundedGlowRect(rect, 5,4, Colors.getColor(0), Colors.getColor(90), Colors.getColor(270), Colors.getColor(180));
        GL.drawRoundedRect(rect, 5, Utils.lerp(new Color(70, 70, 70, 150), new Color(15, 15, 15, 150), selectedAlpha / 255));

        IFont.drawCenteredXY(IFont.MONTSERRAT_BOLD, name, rect.getCenteredX(), rect.getCenteredY(), Color.WHITE, 8);
    }
}
