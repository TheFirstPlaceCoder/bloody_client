package com.client.utils.render.text;

import com.client.utils.render.DrawMode;
import com.client.utils.render.MeshBuilder;
import net.minecraft.client.render.VertexFormats;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class CustomTextRenderer implements TextRenderer {
    private static final Color SHADOW_COLOR = new Color(60, 60, 60, 180);

    private final MeshBuilder mb = new MeshBuilder(16384, true);

    private final Font[] fonts;
    public Font font;

    private boolean building;
    private boolean scaleOnly;
    private double scale = 1;

    public CustomTextRenderer(InputStream file) {
        byte[] bytes = readBytes(file);
        ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length).put(bytes);

        fonts = new Font[5];
        for (int i = 0; i < fonts.length; i++) {
            ((Buffer) buffer).flip();
            fonts[i] = new Font(buffer, (int) Math.round(18 * ((i * 0.5) + 1)));
        }

        mb.texture = true;
    }

    @Override
    public void setAlpha(double a) {
        mb.alpha = a;
    }

    @Override
    public void begin(double scale, boolean scaleOnly, boolean big) {
        if (building) throw new RuntimeException("CustomTextRenderer.begin() called twice");

        if (!scaleOnly) mb.begin(DrawMode.Triangles, VertexFormats.POSITION_COLOR_TEXTURE);

        if (big) {
            this.font = fonts[fonts.length - 1];
        }
        else {
            double scaleA = Math.floor(scale * 10) / 10;

            int scaleI;
            if (scaleA >= 3) scaleI = 5;
            else if (scaleA >= 2.5) scaleI = 4;
            else if (scaleA >= 2) scaleI = 3;
            else if (scaleA >= 1.5) scaleI = 2;
            else scaleI = 1;

            font = fonts[scaleI - 1];
        }

        this.building = true;
        this.scaleOnly = scaleOnly;

        double fontScale = font.getHeight() / 18;
        this.scale = 1 + (scale - fontScale) / fontScale;
    }

    @Override
    public double getWidth(String text, int length) {
        Font font = building ? this.font : fonts[0];
        return font.getWidth(text, length) * scale;
    }

    @Override
    public double getHeight() {
        Font font = building ? this.font : fonts[0];
        return font.getHeight() * scale;
    }

    @Override
    public double render(String text, double x, double y, Color color, boolean shadow) {
        boolean wasBuilding = building;
        if (!wasBuilding) begin();

        double r;
        if (shadow) {
            r = font.render(mb, text, x + 1, y + 1, SHADOW_COLOR, scale, true);
            font.render(mb,text, x, y, color, scale, false);
        }
        else r = font.render(mb, text, x, y, color, scale, false);

        if (!wasBuilding) end();
        return r;
    }

    @Override
    public boolean isBuilding() {
        return building;
    }

    @Override
    public void end() {
        if (!building) throw new RuntimeException("CustomTextRenderer.end() called without calling begin()");

        if (!scaleOnly) {
            font.texture.bindTexture();
            mb.end();
        }

        building = false;
        scale = 1;
    }

    public byte[] readBytes(InputStream in) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            byte[] buffer = new byte[256];
            int read;
            while ((read = in.read(buffer)) > 0) out.write(buffer, 0, read);

            in.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }
}