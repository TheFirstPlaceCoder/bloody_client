package com.client.utils.render.text;

import com.client.utils.color.Colors;
import com.client.utils.render.MeshBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.texture.AbstractTexture;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.*;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Font {
    public final AbstractTexture texture;

    private final int height;
    private final float scale;
    private final float ascent;
    private final static int size = 15000;
    private final Int2ObjectOpenHashMap<CharData> charMap = new Int2ObjectOpenHashMap<>();
    public boolean isGradient = false;

    public Font(ByteBuffer buffer, int height) {
        this.height = height;

        STBTTFontinfo fontInfo = STBTTFontinfo.create();
        STBTruetype.stbtt_InitFont(fontInfo, buffer);

        // Allocate buffers
        ByteBuffer bitmap = BufferUtils.createByteBuffer(size * size);
        STBTTPackedchar.Buffer[] cdata = {
                STBTTPackedchar.create(129535),//, // Basic Latin
                STBTTPackedchar.create(127),//, // Basic Latin
                STBTTPackedchar.create(239)//, // Basic Latin
                //STBTTPackedchar.create(65533),//, // Basic Latin
                //STBTTPackedchar.create(65533)//, // Basic Latin
                //STBTTPackedchar.create(255) // Basic Latin
        };

        // create and initialise packing context
        STBTTPackContext packContext = STBTTPackContext.create();
        STBTruetype.stbtt_PackBegin(packContext, bitmap, size, size, 0 ,1);

        // create the pack range, populate with the specific packing ranges
        STBTTPackRange.Buffer packRange = STBTTPackRange.create(cdata.length);
        packRange.put(STBTTPackRange.create().set(height, 0, null, 129535, cdata[0], (byte) 2, (byte) 2)); //32-127
        packRange.put(STBTTPackRange.create().set(height, 917504, null, 127, cdata[1], (byte) 2, (byte) 2)); //32-127
        packRange.put(STBTTPackRange.create().set(height, 917760, null, 239, cdata[2], (byte) 2, (byte) 2)); //32-127
        //packRange.put(STBTTPackRange.create().set(height, 983040, null, 65533, cdata[1], (byte) 2, (byte) 2)); //32-127
        //packRange.put(STBTTPackRange.create().set(height, 1048576, null, 65533, cdata[1], (byte) 2, (byte) 2)); //32-127
        //packRange.put(STBTTPackRange.create().set(height, 129280, null, 255, cdata[1], (byte) 2, (byte) 2)); //128-255

        //packRange.put(STBTTPackRange.create().set(height, 160, null, 96, cdata[1], (byte) 2, (byte) 2));
        //packRange.put(STBTTPackRange.create().set(height, 256, null, 128, cdata[2], (byte) 2, (byte) 2));
        //packRange.put(STBTTPackRange.create().set(height, 880, null, 144, cdata[3], (byte) 2, (byte) 2));
        //packRange.put(STBTTPackRange.create().set(height, 1024, null, 256, cdata[5], (byte) 2, (byte) 2));
        //packRange.put(STBTTPackRange.create().set(height, 8734, null, 1, cdata[6], (byte) 2, (byte) 2)); // lol
        packRange.flip();

        // write and finish
        STBTruetype.stbtt_PackFontRanges(packContext, buffer, 0, packRange);
        STBTruetype.stbtt_PackEnd(packContext);

        // Create texture object and get font scale
        texture = new ByteTexture(size, size, bitmap, ByteTexture.Format.A, ByteTexture.Filter.Linear, ByteTexture.Filter.Linear);
        scale = STBTruetype.stbtt_ScaleForPixelHeight(fontInfo, height);

        // Get font vertical ascent
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer ascent = stack.mallocInt(1);
            STBTruetype.stbtt_GetFontVMetrics(fontInfo, ascent, null, null);
            this.ascent = ascent.get(0);
        }

        for (int i = 0; i < cdata.length; i++) {
            STBTTPackedchar.Buffer cbuf = cdata[i];
            int offset = packRange.get(i).first_unicode_codepoint_in_range();

            for (int j = 0; j < cbuf.capacity(); j++) {
                STBTTPackedchar packedChar = cbuf.get(j);

                float ipw = 1f / size; // pixel width and height
                float iph = 1f / size;

                charMap.put(j + offset, new CharData(
                        packedChar.xoff(),
                        packedChar.yoff(),
                        packedChar.xoff2(),
                        packedChar.yoff2(),
                        packedChar.x0() * ipw,
                        packedChar.y0() * iph,
                        packedChar.x1() * ipw,
                        packedChar.y1() * iph,
                        packedChar.xadvance()
                ));
            }
        }
    }

    public void setGradient(boolean gradient) {
        this.isGradient = gradient;
    }

    public double getWidth(String string, int length) {
        double width = 0;

        for (int i = 0; i < length; i++) {
            int cp = string.charAt(i);
            CharData c = charMap.get(cp);
            if (c == null) c = charMap.get(32);

            width += c.xAdvance;
        }

        return width;
    }

    public double getHeight() {
        return height;
    }

    public double render(MeshBuilder mb, String string, double x, double y, Color color, double scale, boolean isShadow) {
        y += ascent * this.scale * scale;

        for (int i = 0; i < string.length(); i++) {
            int cp = string.charAt(i);
            CharData c = charMap.get(cp);
            if (c == null) c = charMap.get(32);
            Color c2 = null;
            if (isGradient) {
                c2 = Colors.getColor(Colors.getIndex(string.length() - i, string.length() * 3));
            }

            mb.pos(x + c.x0 * scale, y + c.y0 * scale, 0).color(isGradient && !isShadow ? c2 : color).texture(c.u0, c.v0).endVertex();
            mb.pos(x + c.x1 * scale, y + c.y0 * scale, 0).color(isGradient && !isShadow ? c2 : color).texture(c.u1, c.v0).endVertex();
            mb.pos(x + c.x1 * scale, y + c.y1 * scale, 0).color(isGradient && !isShadow ? c2 : color).texture(c.u1, c.v1).endVertex();

            mb.pos(x + c.x0 * scale, y + c.y0 * scale, 0).color(isGradient && !isShadow ? c2 : color).texture(c.u0, c.v0).endVertex();
            mb.pos(x + c.x1 * scale, y + c.y1 * scale, 0).color(isGradient && !isShadow ? c2 : color).texture(c.u1, c.v1).endVertex();
            mb.pos(x + c.x0 * scale, y + c.y1 * scale, 0).color(isGradient && !isShadow ? c2 : color).texture(c.u0, c.v1).endVertex();

            x += c.xAdvance * scale;
        }

        return x;
    }

    private record CharData(float x0, float y0, float x1, float y1, float u0, float v0, float u1, float v1, float xAdvance) {}
}