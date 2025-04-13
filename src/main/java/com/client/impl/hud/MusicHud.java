package com.client.impl.hud;

import com.client.system.function.FunctionManager;
import com.client.system.hud.HudFunction;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.textures.DownloadImage;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.math.animation.Animation;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.math.animation.Direction;
import com.client.utils.math.animation.impl.EaseBackIn;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.utils.ScissorUtils;
import net.minecraft.client.gui.screen.ChatScreen;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Objects;

public class MusicHud extends HudFunction {
    public MusicHud() {
        super(new FloatRect(208, 23, 100, 25), "Music-Hud");
    }

    private final Animation animation = new EaseBackIn(400, 1, 1);
    private double sc;
    private FloatRect invisibleSliderRect = new FloatRect(),
            sliderRect = new FloatRect(),
            backButtonRect = new FloatRect(),
            pauseButtonRect = new FloatRect(),
            nextButtonRect = new FloatRect(),
            songNameRect = new FloatRect();
    //private String test = "Платина & дора - Сан Ларран (Official Audio)";
    private boolean settingBack = false;
    private static long lastSettingBackChangeTime = 0;
    private static final long PAUSE_DURATION = 3000;
    private com.client.impl.function.hud.MusicHud music;
    public static float nameSongX = 0;
    private float nextScale, pauseScale, backScale;


    @Override
    public void draw(float a) {
        if (music == null) music = FunctionManager.get(com.client.impl.function.hud.MusicHud.class);
        animation.setDirection(!music.isEnabled() && !(mc.currentScreen instanceof ChatScreen) ? Direction.BACKWARDS : Direction.FORWARDS);

        sc = animation.getOutput();
        if (sc <= 0) return;

        float mx = (float) (mc.mouse.getX() / 2f);
        float my = (float) (mc.mouse.getY() / 2f);

        nextButtonRect = new FloatRect(rect.getX2() - 3 - 12, rect.getY() + 3, 12, 12);
        pauseButtonRect = new FloatRect(rect.getX2() - 3 - 12 - 3 - 12, rect.getY() + 3, 12, 12);
        backButtonRect = new FloatRect(rect.getX2() - 3 - 12 - 3 - 12 - 3 - 12, rect.getY() + 3, 12, 12);
        songNameRect = new FloatRect(rect.getX() + 3, rect.getY() + 3, rect.getW() - 50, 12);
        invisibleSliderRect = new FloatRect(rect.getX() + 5, rect.getY2() - 3 - 3 - 1.5f, rect.getW() - 5 - 5, 5);
        sliderRect = new FloatRect(rect.getX() + 5, rect.getY2() - 3 - 3, 0, 2);

        nextScale = AnimationUtils.fast(nextScale, nextButtonRect.intersect(mx, my) ? 0.95f : 1, 10);
        pauseScale = AnimationUtils.fast(pauseScale, pauseButtonRect.intersect(mx, my) ? 0.95f : 1, 10);
        backScale = AnimationUtils.fast(backScale, backButtonRect.intersect(mx, my) ? 0.95f : 1, 10);

        float songNameWidth = IFont.getWidth(IFont.MONTSERRAT_BOLD, music.getCurrentTrackName(), 7);

        if (System.currentTimeMillis() - lastSettingBackChangeTime > PAUSE_DURATION) {
            if (songNameRect.getX() + nameSongX + songNameWidth < songNameRect.getX2()) {
                if (!settingBack) {
                    settingBack = true;
                    lastSettingBackChangeTime = System.currentTimeMillis();
                }
            } else if (songNameRect.getX() < songNameRect.getX() + nameSongX) {
                if (settingBack) {
                    settingBack = false;
                    lastSettingBackChangeTime = System.currentTimeMillis();
                }
            }
        }

        if (System.currentTimeMillis() - lastSettingBackChangeTime > PAUSE_DURATION) {
            if (settingBack)
                nameSongX = AnimationUtils.fast(nameSongX, 30, 0.7f);
            else nameSongX = AnimationUtils.fast(nameSongX, -songNameWidth + songNameRect.getW() - 30, 0.7f);
        }

        music.setFramesPlayed();
        int maxFrames = music.getMaxFrames();
        if (maxFrames > 0) {
            float delimiter = (float) music.getFramesPlayed() / (float) maxFrames;
            sliderRect.setW(invisibleSliderRect.getW() * delimiter);
        } else sliderRect.setW(0f);

        startScale(sc);

        drawNewClientRect(rect);

        startScaleCustom(nextButtonRect, nextScale);
        drawButton(nextButtonRect, DownloadImage.NEXT);
        endScale();

        startScaleCustom(pauseButtonRect, pauseScale);
        drawButton(pauseButtonRect, music.isPaused ? DownloadImage.PLAY : DownloadImage.PAUSE);
        endScale();

        startScaleCustom(backButtonRect, backScale);
        drawButton(backButtonRect, DownloadImage.BACK);
        endScale();

        GL.drawRoundedGlowRect(sliderRect, 0,0, ColorUtils.injectAlpha(Colors.getColor(0), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(90), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(270), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(180), (int) (255)));

//        IFont.draw(IFont.MONTSERRAT_BOLD, "getMaxFrames: " + music.getMaxFrames(), rect.getX(), rect.getY() - 10, Color.WHITE, 7);
//        IFont.draw(IFont.MONTSERRAT_BOLD, "getFrameRate: " + music.getFrameRate(), rect.getX(), rect.getY() - 20, Color.WHITE, 7);
//        IFont.draw(IFont.MONTSERRAT_BOLD, "getFrameSize: " + music.getFrameSize(), rect.getX(), rect.getY() - 30, Color.WHITE, 7);
//        IFont.draw(IFont.MONTSERRAT_BOLD, "getSampleRate: " + music.getSampleRate(), rect.getX(), rect.getY() - 40, Color.WHITE, 7);
//        IFont.draw(IFont.MONTSERRAT_BOLD, "getSampleSizeInBits: " + music.getSampleSizeInBits(), rect.getX(), rect.getY() - 50, Color.WHITE, 7);
//        IFont.draw(IFont.MONTSERRAT_BOLD, "getChannels: " + music.getChannels(), rect.getX(), rect.getY() - 60, Color.WHITE, 7);
//        IFont.draw(IFont.MONTSERRAT_BOLD, "getFramesPlayed: " + music.getFramesPlayed(), rect.getX(), rect.getY() - 70, Color.WHITE, 7);
//        IFont.draw(IFont.MONTSERRAT_BOLD, "getFramePosition: " + music.getFramePosition(), rect.getX(), rect.getY() - 80, Color.WHITE, 7);
//        IFont.draw(IFont.MONTSERRAT_BOLD, "getBufferSize: " + music.getBufferSize(), rect.getX(), rect.getY() - 90, Color.WHITE, 7);
//        IFont.draw(IFont.MONTSERRAT_BOLD, "getLongFramePosition: " + music.getLongFramePosition(), rect.getX(), rect.getY() - 100, Color.WHITE, 7);
//        IFont.draw(IFont.MONTSERRAT_BOLD, "getMicrosecondPosition: " + music.getMicrosecondPosition(), rect.getX(), rect.getY() - 110, Color.WHITE, 7);
//        IFont.draw(IFont.MONTSERRAT_BOLD, "getLevel: " + music.getLevel(), rect.getX(), rect.getY() - 120, Color.WHITE, 7);
//        IFont.draw(IFont.MONTSERRAT_BOLD, "available: " + music.available(), rect.getX(), rect.getY() - 130, Color.WHITE, 7);
//        IFont.draw(IFont.MONTSERRAT_BOLD, "skippedFrames: " + music.getSkippedFrames(), rect.getX(), rect.getY() - 140, Color.WHITE, 7);
//        IFont.draw(IFont.MONTSERRAT_BOLD, "getPosition: " + music.getPosition(), rect.getX(), rect.getY() - 150, Color.WHITE, 7);

        ScissorUtils.enableScissor(songNameRect);
        IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, music.getCurrentTrackName(), songNameRect.getX() + (Objects.equals(music.getCurrentTrackName(), "Отсутствует") ? 0 : nameSongX), songNameRect.getCenteredY(), Color.WHITE, 7);
        ScissorUtils.disableScissor();

        endScale();
    }

    @Override
    public boolean click(int mx, int my, int b) {
        if (music == null) music = FunctionManager.get(com.client.impl.function.hud.MusicHud.class);
        if (backButtonRect.intersect(mx, my)) {
            music.playPreviousTrack();
            return true;
        } else if (pauseButtonRect.intersect(mx, my)) {
            music.pauseResume();
            return true;
        } else if (nextButtonRect.intersect(mx, my)) {
            music.playNextTrack();
            return true;
        } else if (invisibleSliderRect.intersect(mx, my)) {

            float clickPosition =  Math.max(0, Math.min(1, (mx - invisibleSliderRect.getX()) / invisibleSliderRect.getW()));
            int targetTime = (int) ((float) (clickPosition * music.getMaxFrames()));
            music.seekTo(targetTime);

            return true;
        }
        return super.click(mx, my, b);
    }

    private void drawButton(FloatRect rect, int icon) {
        GL.drawRoundedGlowRect(rect, 3,2, ColorUtils.injectAlpha(Colors.getColor(0), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(90), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(270), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(180), (int) (255)));
        GL.drawRoundedRect(rect, 3, new Color(15, 15, 15, (int) (150)));

        GL11.glPushMatrix();
        GL11.glScalef(1f, 1f, 1f);
        GL11.glTranslatef(rect.getX() + 2, rect.getY() + 2, 0);
        GL.drawRoundedTexture(DownloadImage.getGlId(icon), 0, 0, 8, 8, 0);
        GL11.glPopMatrix();
    }
}