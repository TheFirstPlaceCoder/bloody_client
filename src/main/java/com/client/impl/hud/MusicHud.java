package com.client.impl.hud;

import com.client.system.function.FunctionManager;
import com.client.system.hud.HudFunction;
import com.client.system.hud.HudManager;
import com.client.system.textures.DownloadImage;
import com.client.utils.Utils;
import com.client.utils.color.Colors;
import com.client.utils.math.animation.Animation;
import com.client.utils.math.animation.Direction;
import com.client.utils.math.animation.impl.EaseBackIn;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.DrawMode;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.main.TextureGL;
import com.client.utils.render.wisetree.render.render2d.utils.ScissorUtils;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.render.VertexFormats;
import org.lwjgl.opengl.GL11;
import java.awt.*;

public class MusicHud extends HudFunction {
    public MusicHud() {
        super(new FloatRect(106, 22, 100, 36), "Music-Hud");
    }

    private com.client.impl.function.hud.MusicHud music;
    private final Animation animation = new EaseBackIn(400, 1, 1);
    private FloatRect sliderRect = new FloatRect(),
            sliderRect2 = new FloatRect(),
            backButtonRect = new FloatRect(),
            pauseButtonRect = new FloatRect(),
            nextButtonRect = new FloatRect(),
            songNameRect = new FloatRect();
    private float nameSongX = 0;

    @Override
    public void draw(float alpha) {
        if (music == null) music = FunctionManager.get(com.client.impl.function.hud.MusicHud.class);
        animation.setDirection(!music.isEnabled() && !(mc.currentScreen instanceof ChatScreen) ? Direction.BACKWARDS : Direction.FORWARDS);
        double sc = animation.getOutput();
        if (sc <= 0.0f) return;

        sliderRect = new FloatRect(rect.getX() + 37, rect.getY2() - 3 - 4, 59, 3);
        sliderRect2 = new FloatRect(rect.getX() + 37, rect.getY2() - 3 - 4, 0, 3);
        backButtonRect = new FloatRect(rect.getX() + 37, sliderRect.getY() - 2 - 12, 12, 12);
        pauseButtonRect = new FloatRect(rect.getX() + 37 + 12 + 2, sliderRect.getY() - 2 - 12, 12, 12);
        nextButtonRect = new FloatRect(rect.getX() + 37 + 12 + 2 + 12 + 2, sliderRect.getY() - 2 - 12, 12, 12);
        songNameRect = new FloatRect(rect.getX() + 37, rect.getY() + 2, rect.getW() - 40, pauseButtonRect.getY() - (rect.getY() + 2));

        float songNameWidth = IFont.getWidth(IFont.MONTSERRAT_BOLD, music.getSongName(), 7);

        if (songNameWidth > songNameRect.getW() - 5) {
            nameSongX -= 0.15;
        }

        if (nameSongX <= -songNameWidth) nameSongX = (rect.getW() - 37);

        if (music.maxTime != 0) {
            float delimiter = (float) music.totalTime / (float) music.maxTime;
            sliderRect2.setW(59f * delimiter);
        } else sliderRect2.setW(0f);

        startScale(sc);
        drawNewClientRect(rect);
        GL.drawRoundedTexture(DownloadImage.getGlId(DownloadImage.MUSIC_8), rect.getX() + 2.5f, rect.getY() + 2.5f, 31, 31, 6f);

        GL.drawRoundedRect(sliderRect, 1, new Color(42, 42, 42));
        GL.drawRoundedGradientRect(sliderRect2, 1, inject(Colors.getColor(90), alpha), inject(Colors.getColor(90), alpha), inject(Colors.getColor(270), alpha), inject(Colors.getColor(270), alpha));
        GL.drawCircle(sliderRect2.getX2(), sliderRect2.getCenteredY(), 2, 0, Color.WHITE);

        drawButton(backButtonRect.getX(), backButtonRect.getY(), DownloadImage.BACK);
        drawButton(pauseButtonRect.getX(), pauseButtonRect.getY(), music.player == null ? DownloadImage.PLAY : DownloadImage.STOP);
        drawButton(nextButtonRect.getX(), nextButtonRect.getY(), DownloadImage.NEXT);

        String time = Utils.formatDuration(music.totalTime);
        IFont.draw(IFont.MONTSERRAT_BOLD, time, sliderRect.getX2() - IFont.getWidth(IFont.MONTSERRAT_BOLD, time, 5), sliderRect.getY() - 1 - IFont.getHeight(IFont.MONTSERRAT_BOLD, time, 5), Color.WHITE, 5);

        ScissorUtils.enableScissor(songNameRect);
        IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, music.getSongName(), songNameRect.getX() + nameSongX, songNameRect.getCenteredY(), Color.WHITE, 7);
        ScissorUtils.disableScissor();

        endScale();
    }

    private void drawButton(float x, float y, int icon) {
        boolean isHovered = new FloatRect(x, y, 12, 12).intersect(mc.mouse.getX() / 2, mc.mouse.getY() / 2);
        Color backgroundColor = isHovered ? new Color(82, 82, 82) : new Color(42, 42, 42);
        GL.drawRoundedRect(x, y, 12, 12, 3, backgroundColor);
        postTask.add(() -> {
            HudManager.MB.begin(DrawMode.Triangles, VertexFormats.POSITION_COLOR_TEXTURE);
            HudManager.MB.texQuad(DownloadImage.getGlId(icon), new TextureGL.TextureRegion(x + 1, y + 1, 10, 10), Color.WHITE);
            HudManager.MB.end();
        });
    }

    @Override
    public boolean click(int mx, int my, int b) {
        if (music == null) music = FunctionManager.get(com.client.impl.function.hud.MusicHud.class);
        if (backButtonRect.intersect(mx, my)) {
            music.buttonBack();
            return true;
        } else if (pauseButtonRect.intersect(mx, my)) {
            if (music.player != null) music.stopRadio();
            else music.playRadio();
            return true;
        } else if (nextButtonRect.intersect(mx, my)) {
            music.buttonNext();
            return true;
        }
        return super.click(mx, my, b);
    }
}