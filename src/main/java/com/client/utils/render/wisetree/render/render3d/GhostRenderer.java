package com.client.utils.render.wisetree.render.render3d;

import com.client.system.textures.DownloadImage;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.render.wisetree.render.render2d.main.TextureGL;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import static com.client.system.function.Function.mc;

public class GhostRenderer {
    private static final long initTime = System.currentTimeMillis();

    public void draw(MatrixStack matrix, Entity target, float alpha) {
        float height = target.getHeight() - 0.2f;
        Vec3d vec = Renderer3D.getRenderPosition(Renderer3D.getSmoothPos(target)).add(0, height / 3, 0);

        float time = (float) ((((System.currentTimeMillis() - initTime) / 1500F)) + (Math.sin((((System.currentTimeMillis() - initTime) / 1500F))) / 10f));
        float offset = 0;

        matrix.push();
        matrix.translate(vec.x, vec.y, vec.z);

        for (int stage = 0; stage < 3; stage++) {

            for (float i = time * 360; i < time * 360 + 90f; i += 2) {
                float f = i - time * 360;
                float mul = f / 90f;

                double radians = Math.toRadians(i);

                double width = target.getWidth() * 1.3d;
                double cos = Math.cos(radians) * width;
                double sin = -(Math.sin(radians) * width);

                float scale = MathHelper.clamp(stage == 1 ? 9f - (9f * mul) : 9f * mul, 2f, 9f);

                if (scale > 2) {
                    matrix.push();
                    matrix.translate(cos, offset + (Math.sin(radians * 1.2f) * 0.1f), sin);
                    matrix.scale(-0.07f, -0.07f, -0.07f);
                    matrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-mc.gameRenderer.getCamera().getYaw()));
                    matrix.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(mc.gameRenderer.getCamera().getPitch()));

                    TextureGL.create()
                            .bind(DownloadImage.getGlId(DownloadImage.GLOW_CIRCLE))
                            .draw(
                                    matrix, new TextureGL.TextureRegion(scale, scale), true,
                                    ColorUtils.injectAlpha(Colors.getColor((int) (90 * (f / (90f)))), (int) (alpha * 145))
                            );
                    matrix.pop();
                }
            }

            time *= -1.025f;
            offset += height / 3f;
        }

        matrix.pop();
    }
}