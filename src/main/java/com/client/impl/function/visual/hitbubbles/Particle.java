package com.client.impl.function.visual.hitbubbles;

import com.client.system.textures.DownloadImage;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.math.animation.Animation;
import com.client.utils.math.animation.Direction;
import com.client.utils.math.animation.impl.EaseBackIn;
import com.client.utils.render.wisetree.render.render2d.main.TextureGL;
import com.client.utils.render.wisetree.render.render3d.Renderer3D;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import static com.client.BloodyClient.mc;

public class Particle {
    public Vec3d pos;
    private final float yaw, pitch;
    private int fade;
    private boolean rev = false;
    private final Animation animation = new EaseBackIn(200, 1, 1);

    public Particle(Vec3d pos) {
        this.pos = pos;
        this.yaw = -mc.gameRenderer.getCamera().getYaw();
        this.pitch = mc.gameRenderer.getCamera().getPitch();
        fade = 0;
    }

    public void tick() {
        if (!rev) {
            animation.setDirection(Direction.FORWARDS);
        } else if (fade <= 150) {
            animation.setDirection(Direction.BACKWARDS);
            animation.setDuration(600);
        }
    }

    public void draw(MatrixStack matrix, String str) {
        tick();
        Vec3d fix = Renderer3D.getRenderPosition(pos);

        matrix.push();
        matrix.translate(fix.x, fix.y, fix.z);
        matrix.scale((float) (animation.getOutput() / 256), (float) (animation.getOutput() / 256), (float) (animation.getOutput() / 256));

        matrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(yaw));
        matrix.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(pitch));
        matrix.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion((float) Math.sin((System.currentTimeMillis() / 1000.0)) * 360.0F));

        TextureGL.create()
                .bind(str.equals("Обычный") ? DownloadImage.getGlId(DownloadImage.WHIRLWIND_FIRST) : DownloadImage.getGlId(DownloadImage.WHIRLWIND_SECOND))
                .draw(matrix, new TextureGL.TextureRegion(256, 256), true, ColorUtils.injectAlpha(Colors.getColor(0), fade));

        matrix.pop();
        tick();
    }

    public boolean update() {
        if (!rev) {
            if (fade < 255) {
                fade += 11;
            } else {
                rev = true;
            }
        } else {
            fade -= 8;
        }
        return fade <= 0 && rev;
    }
}