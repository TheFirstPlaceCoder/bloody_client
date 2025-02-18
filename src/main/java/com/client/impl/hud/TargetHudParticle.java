package com.client.impl.hud;

import com.client.impl.function.visual.particles.Particles;
import com.client.system.function.FunctionManager;
import com.client.system.textures.DownloadImage;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.math.MathUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.math.vector.doubles.V2D;
import com.client.utils.misc.FunctionUtils;
import com.client.utils.render.wisetree.render.render2d.main.TextureGL;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TargetHudParticle {
    private final V2D pos;
    private final V2D vector;
    private int alpha;
    private boolean remove;
    private float div;
    private final int color;
    private final float angle;
    private int texture;
    private static Particles particles = FunctionManager.get(Particles.class);

    public TargetHudParticle(FloatRect data) {
        vector = new V2D(MathUtils.offset(1.25f), MathUtils.offset(1.25f));
        pos = new V2D(data.getX() + 18 + MathUtils.offset(10), data.getY() + 18 + MathUtils.offset(10));
        angle = MathUtils.RANDOM.nextFloat() * 360F;
        color = (int) (MathUtils.RANDOM.nextFloat() * 360F);
        div = 0.8f;

        List<Integer> id = new ArrayList<>();

        if (particles.type.get(0)) id.add(DownloadImage.getGlId(DownloadImage.STAR));
        if (particles.type.get(1)) id.add(DownloadImage.getGlId(DownloadImage.HEART));
        if (particles.type.get(2)) id.add(DownloadImage.getGlId(DownloadImage.SNOW));
        if (particles.type.get(3)) id.add(DownloadImage.getGlId(DownloadImage.TRIANGLE_GLOW));
        if (particles.type.get(4)) id.add(DownloadImage.getGlId(DownloadImage.CIRCLE));

        if (id.isEmpty()) {
            texture = -1;
            return;
        }

        this.texture = id.get(new Random().nextInt(id.size()));
    }

    public void draw() {
        if (texture == -1) return;
        alpha = MathHelper.clamp(alpha, 0, 255);

        pos.a += vector.a * MathHelper.clamp(div, 0.4f, 0.8f);
        pos.b += vector.b * MathHelper.clamp(div, 0.4f, 0.8f);

        MatrixStack stack = new MatrixStack();
        stack.translate(pos.a, pos.b, 0);
        stack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(angle));
        stack.scale(8f / 512f, 8f / 512f, 8f / 512f);

        TextureGL.create().bind(texture).draw(stack, new TextureGL.TextureRegion(512), false,
                ColorUtils.injectAlpha(Colors.getColor(color), alpha),
                ColorUtils.injectAlpha(Colors.getColor(color), alpha),
                ColorUtils.injectAlpha(Colors.getColor(color), alpha),
                ColorUtils.injectAlpha(Colors.getColor(color), alpha)
        );

        div -= 0.01f;
    }

    public boolean removeIf() {
        if (alpha >= 255) remove = true;
        if (alpha < 255 && !remove)
            alpha += 7;
        else alpha -= 11;
        return alpha <= 0 && remove;
    }
}