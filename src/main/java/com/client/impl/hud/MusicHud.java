package com.client.impl.hud;

import com.client.impl.function.client.Music;
import com.client.system.function.FunctionManager;
import com.client.system.hud.HudFunction;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class MusicHud extends HudFunction {
    public MusicHud() {
        super(new FloatRect(106, 22, 100, 36), "Music-Hud");
    }

    public Identifier play = new Identifier("bloody-client", "hud/play.png");
    public Identifier stop = new Identifier("bloody-client", "hud/stop.png");
    public Identifier next = new Identifier("bloody-client", "hud/next.png");
    public Identifier back = new Identifier("bloody-client", "hud/back.png");

    @Override
    public void draw(float alpha) {
        drawNewClientRect(rect);
        String songName = getInvertedName(FunctionManager.get(Music.class).getSongName());

        boolean button1 = new FloatRect(rect.getCenteredX() - 7 - 3 - 14, rect.getCenteredY(), 14, 14).intersect(mc.mouse.getX() / 2, mc.mouse.getY() / 2);
        boolean button2 = new FloatRect(rect.getCenteredX() - 7, rect.getCenteredY(), 14, 14).intersect(mc.mouse.getX() / 2, mc.mouse.getY() / 2);
        boolean button3 = new FloatRect(rect.getCenteredX() + 7 + 3, rect.getCenteredY(), 14, 14).intersect(mc.mouse.getX() / 2, mc.mouse.getY() / 2);

        IFont.drawCenteredX(IFont.MONTSERRAT_BOLD, songName, rect.getCenteredX(), rect.getY() + 3, Color.WHITE, 8);

        GL.drawRoundedRect(rect.getCenteredX() - 7 - 3 - 14, rect.getCenteredY(), 14, 14, 3, button1 ? new Color(82, 82, 82) : new Color(42, 42, 42));
        GL11.glPushMatrix();
        GL11.glScalef(1f, 1f, 1f);
        GL11.glTranslatef(rect.getCenteredX() - 7 - 3 - 13, rect.getCenteredY() + 1, 0);
        GL.drawRoundedTexture(back, 0, 0, 12, 12, 0);
        GL11.glPopMatrix();

        GL.drawRoundedRect(rect.getCenteredX() - 7, rect.getCenteredY(), 14, 14, 3, button2 ? new Color(82, 82, 82) : new Color(42, 42, 42));
        GL11.glPushMatrix();
        GL11.glScalef(1f, 1f, 1f);
        GL11.glTranslatef(rect.getCenteredX() - (FunctionManager.get(Music.class).player == null ? 5.5f : 6f), rect.getCenteredY() + 1, 0);
        GL.drawRoundedTexture(FunctionManager.get(Music.class).player == null ? play : stop, 0, 0, 12, 12, 0);
        GL11.glPopMatrix();

        GL.drawRoundedRect(rect.getCenteredX() + 7 + 3, rect.getCenteredY(), 14, 14, 3, button3 ? new Color(82, 82, 82) : new Color(42, 42, 42));
        GL11.glPushMatrix();
        GL11.glScalef(1f, 1f, 1f);
        GL11.glTranslatef(rect.getCenteredX() + 7 + 4, rect.getCenteredY() + 1, 0);
        GL.drawRoundedTexture(next, 0, 0, 12, 12, 0);
        GL11.glPopMatrix();
    }

    public String getInvertedName(String s) {
        String finalName = "";

        double width = 0;
        for (int i = 0; i < s.length(); i++) {
            if (width < 80) {
                finalName += s.charAt(i);
                width = IFont.getWidth(IFont.MONTSERRAT_BOLD, finalName, 8);
            } else {
                finalName.substring(0, finalName.length() - 4);
                finalName += "...";
                break;
            }
        }

        return finalName;
    }

    @Override
    public boolean click(int mx, int my, int b) {
        FloatRect button1 = new FloatRect(rect.getCenteredX() - 7 - 3 - 14, rect.getCenteredY(), 14, 14);
        FloatRect button2 = new FloatRect(rect.getCenteredX() - 7, rect.getCenteredY(), 14, 14);
        FloatRect button3 = new FloatRect(rect.getCenteredX() + 7 + 3, rect.getCenteredY(), 14, 14);
        Music music = FunctionManager.get(Music.class);

        if (button1.intersect(mx, my)) {
            music.buttonBack();
            return true;
        }
        else if (button2.intersect(mx, my)) {
            if (music.player != null) music.stopRadio();
            else music.playRadio();

            return true;
        }
        else if (button3.intersect(mx, my)) {
            music.buttonNext();
            return true;
        }


        return super.click(mx, my, b);
    }
}