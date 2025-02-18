package com.client.clickgui.screens;

import com.client.alt.Account;
import com.client.alt.Accounts;
import com.client.clickgui.Impl;
import com.client.system.textures.DownloadImage;
import com.client.utils.color.ColorTransfusion;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.api.FontRenderer;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.main.TextureGL;
import com.client.utils.render.wisetree.render.render2d.utils.PlayerHeadTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

import static com.client.BloodyClient.mc;

public class AltElement implements Impl {
    public FloatRect rect = new FloatRect(0, 0,0,0),
            iconRect = new FloatRect(0, 0,0,0),
            favoriteButton = new FloatRect(0, 0,0,0),
            deleteButton = new FloatRect(0, 0,0,0);

    public Account account;

    public float animate = 0;
    public int alpha;
    public ColorTransfusion colorTransfusion;
    public boolean isDeleting = false;
    public float deleteOffset = 0, realX;

    public AltElement(FloatRect rect, Account account) {
        this.rect = rect;
        this.realX = this.rect.getX();
        this.account = account;
        colorTransfusion = new ColorTransfusion(this.account.isFavorite ? Color.ORANGE : Color.WHITE);
    }

    @Override
    public void draw(double mx, double my, float a) {
        if (iconRect.intersect(mx, my)) {
            if (alpha < 255) alpha += 7.5;
            alpha = MathHelper.clamp(alpha, 0, 255);

            animate = AnimationUtils.fast(animate, 18);
        } else {
            if (alpha > 0) alpha -= 30;
            alpha = MathHelper.clamp(alpha, 0, 255);

            animate = AnimationUtils.fast(animate, 0);
        }

        if (isDeleting) {
            deleteOffset = AnimationUtils.fast(deleteOffset, 400);
            this.rect.setX(realX + deleteOffset);
            if (rect.getX() > realX + 250) Accounts.remove(this.account);
        }

        if (this.account.isFavorite) colorTransfusion.animate(Color.ORANGE, 11);
        else colorTransfusion.animate(Color.WHITE, 11);

        this.iconRect = new FloatRect(this.rect.getX() + 2.5, this.rect.getY() + 2.5, this.rect.getH() - 5, this.rect.getH() - 5);
        this.favoriteButton = new FloatRect(this.rect.getX2() - 2.5 - 16, this.rect.getY() + 4.5, 16, 16);
        this.deleteButton = new FloatRect(this.iconRect.getCenteredX() - 5, this.iconRect.getCenteredY() - 5, 10, 10);

        GL.prepare();
        GL.drawRoundedRect(rect, 4, new Color(24, 26, 33, 235));
        GL.drawRoundedTexture(PlayerHeadTexture.resolve(account.name), new FloatRect(iconRect.getX() + animate, iconRect.getY().floatValue(), iconRect.getW().floatValue(), iconRect.getH().floatValue()), 4);
        GL.end();

        TextureGL.create()
                .bind(DownloadImage.getGlId(DownloadImage.STAR))
                .draw(new TextureGL.TextureRegion(favoriteButton.getX(), favoriteButton.getY(), favoriteButton.getW(), favoriteButton.getH()), false, colorTransfusion.getColor());

        TextureGL.create()
                .bind(DownloadImage.getGlId(DownloadImage.REMOVE))
                .draw(new TextureGL.TextureRegion(deleteButton.getX(), deleteButton.getY(), deleteButton.getW(), deleteButton.getH()), false, new Color(255, 0, 0, MathHelper.clamp(alpha, 0, 255)));

        if (mc.getSession().getUsername().equals(account.name)) FontRenderer.color(true);
        FontRenderer.shouldRename(false);
        IFont.drawCenteredY(IFont.MONTSERRAT_MEDIUM, account.name, iconRect.getX2() + 7.5f + animate, iconRect.getCenteredY(), new Color(162, 162, 162), 8);
        FontRenderer.shouldRename(true);
        FontRenderer.color(false);
    }

    public void startDeleting() {
        isDeleting = true;
    }

    @Override
    public void click(double mx, double my, int button) {
        if (rect.intersect(mx, my) && !favoriteButton.intersect(mx, my) && !deleteButton.expand(8f).intersect(mx, my) && !mc.getSession().getUsername().equals(account.name)) this.account.login();
        else if (rect.intersect(mx, my) && favoriteButton.intersect(mx, my) && !deleteButton.expand(8f).intersect(mx, my)) this.account.isFavorite = !this.account.isFavorite;
        else if (rect.intersect(mx, my) && !favoriteButton.intersect(mx, my) && deleteButton.expand(8f).intersect(mx, my) && alpha >= 70) startDeleting();
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
