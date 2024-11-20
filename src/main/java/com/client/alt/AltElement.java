package com.client.alt;

import com.client.clickgui.Impl;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.DrawMode;
import com.client.utils.render.Matrices;
import com.client.utils.render.Renderer2D;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.main.TextureGL;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11C;

import java.awt.*;

import static com.client.BloodyClient.mc;

public class AltElement implements Impl {
    public Account account;
    public FloatRect rect = new FloatRect(0,0,0,0);
    public Identifier login = new Identifier("bloody-client", "client/yes.png");
    public Identifier remove = new Identifier("bloody-client", "client/no.png");

    public AltElement(Account account, float x, float y) {
        this.account = account;
        this.rect.setX(x);
        this.rect.setY(y);
        this.rect.setH(16.5f);
        this.rect.setW(220.5f);
    }

    @Override
    public void draw(double mx, double my, float alpha) {
        int alp = mc.getSession().getUsername().equals(this.account.name) ? 120 : 40;

        Matrices.push();
        Matrices.translate(0, 0, 0);

        Renderer2D.COLOR.begin(GL11C.GL_TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        Renderer2D.COLOR.roundedQuad(rect.getX(), rect.getY(), rect.getX2() - 43.5f, rect.getY2(), 3, 9,
                ColorUtils.injectAlpha(Colors.getColor(0, 21), alp),
                ColorUtils.injectAlpha(Colors.getColor(180, 21), alp));
        Renderer2D.COLOR.end();

        Renderer2D.COLOR.begin(GL11C.GL_TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        Renderer2D.COLOR.renderRoundedOutline(Colors.getColor(0, 21),
                Colors.getColor(180, 21),
                rect.getX() + 0.75f, rect.getY() + 0.75f, rect.getX2() - 0.75f - 43.5f, rect.getY2() - 0.75f, 3, 1);
        Renderer2D.COLOR.end();


        Renderer2D.COLOR.begin(GL11C.GL_TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        Renderer2D.COLOR.roundedQuad(rect.getX2() - 43.5f + 5.25f, rect.getY(), rect.getX2() - 43.5f + 5.25f + 16.5f, rect.getY2(), 3, 9,
                ColorUtils.injectAlpha(Colors.getColor(0, 16), alp),
                ColorUtils.injectAlpha(Colors.getColor(90, 16), alp));
        Renderer2D.COLOR.end();

        Renderer2D.COLOR.begin(GL11C.GL_TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        Renderer2D.COLOR.renderRoundedOutline(Colors.getColor(0, 16),
                Colors.getColor(90, 16),
                rect.getX2() - 43.5f + 5.25f + 0.75f, rect.getY() + 0.75f, rect.getX2() - 43.5f + 5.25f + 16.5f - 0.75f, rect.getY2() - 0.75f, 3, 1);
        Renderer2D.COLOR.end();


        Renderer2D.COLOR.begin(GL11C.GL_TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        Renderer2D.COLOR.roundedQuad(rect.getX2() - 43.5f + 5.25f + 16.5f + 5.25f, rect.getY(), rect.getX2(), rect.getY2(), 3, 9,
                ColorUtils.injectAlpha(Colors.getColor(0, 16), alp),
                ColorUtils.injectAlpha(Colors.getColor(90, 16), alp));
        Renderer2D.COLOR.end();

        Renderer2D.COLOR.begin(GL11C.GL_TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        Renderer2D.COLOR.renderRoundedOutline(Colors.getColor(0, 16),
                Colors.getColor(90, 16),
                rect.getX2() - 43.5f + 5.25f + 16.5f + 5.25f + 0.75f, rect.getY() + 0.75f, rect.getX2() - 43.5f + 5.25f + 16.5f + 5.25f + 16.5f - 0.75f, rect.getY2() - 0.75f, 3, 1);
        Renderer2D.COLOR.end();


        GL.prepare();
        GL.drawRoundedTexture(login, rect.getX2() - 43.5f + 5.25f, rect.getY() + 0.75f, 15.75f, 15.75f, 0f);
        GL.drawRoundedTexture(remove, rect.getX2() - 43.5f + 5.25f + 16.5f + 5.25f + 0.75f, rect.getY() + 0.75f, 15.75f, 15.75f, 0f);
        GL.end();

        Matrices.pop();

        IFont.drawCenteredY(IFont.COMFORTAA,
                account.name,
                rect.getX() + 7.5f,
                rect.getCenteredY(),
                Color.WHITE,
                10);
    }

    @Override
    public void click(double mx, double my, int button) {
        if (new FloatRect(rect.getX2() - 48 + 5.25f, rect.getY().floatValue(), 16.5f, 16.5f).intersect(mx, my)) {
            this.account.login();
        } else if (new FloatRect(rect.getX2() - 48 + 5.25f + 16.5f + 5.25f, rect.getY().floatValue(), 16.5f, 16.5f).intersect(mx, my)) {
            Accounts.remove(this.account);
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
