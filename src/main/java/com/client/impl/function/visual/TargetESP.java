package com.client.impl.function.visual;

import com.client.event.events.Render3DEvent;
import com.client.impl.function.combat.aura.TargetHandler;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.game.entity.PlayerUtils;
import com.client.utils.math.animation.Direction;
import com.client.utils.math.animation.impl.SmoothStepAnimation;
import com.client.utils.misc.FunctionUtils;
import com.client.utils.render.wisetree.render.render2d.main.TextureGL;
import com.client.utils.render.wisetree.render.render3d.CircleRenderer;
import com.client.utils.render.wisetree.render.render3d.GhostRenderer;
import com.client.utils.render.wisetree.render.render3d.Renderer3D;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.util.List;

public class TargetESP extends Function {
    public TargetESP() {
        super("Target ESP", Category.VISUAL);
    }

    private final ListSetting mode = List().name("Режим").defaultValue("Круг").list(List.of("Квадрат", "Круг", "Новый")).build();

    private final SmoothStepAnimation animation = new SmoothStepAnimation(600, 1);
    private final CircleRenderer circleRenderer = new CircleRenderer();
    private final GhostRenderer ghostRenderer = new GhostRenderer();

    @Override
    public void onRender3D(Render3DEvent event) {
        Entity target = TargetHandler.getTarget();

        if (target == null) return;

        if (!FunctionManager.isEnabled("Attack Aura") || !PlayerUtils.isInRange(target, FunctionUtils.range)) {
            animation.setDirection(Direction.BACKWARDS);
        } else {
            animation.setDirection(Direction.FORWARDS);
        }

        float alpha = (float) animation.getOutput();

        if (alpha <= 0) return;

        MatrixStack stack = event.getMatrices();
        stack.push();

        switch (mode.get()) {
            case "Квадрат" -> {
                Vec3d pos = Renderer3D.getRenderPosition(Renderer3D.getSmoothPos(target)).add(0, target.getHeight() / 2, 0);
                stack.translate(pos.x, pos.y, pos.z);
                stack.scale(1.3F / 512F, 1.3F / 512F, 1.3F / 512F);
                stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-mc.gameRenderer.getCamera().getYaw()));
                stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(mc.gameRenderer.getCamera().getPitch()));
                stack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float) (Math.sin(System.currentTimeMillis() / 1000.0) * 360.0)));
                TextureGL.create().bind(new Identifier("bloody-client", "/client/auratexture.png"))
                        .draw(stack, new TextureGL.TextureRegion(512, 512), false,
                                ColorUtils.injectAlpha(Colors.getColor(0), (int) (alpha * 255)), ColorUtils.injectAlpha(Colors.getColor(90), (int) (alpha * 255)),
                                ColorUtils.injectAlpha(Colors.getColor(180), (int) (alpha * 255)), ColorUtils.injectAlpha(Colors.getColor(270), (int) (alpha * 255)));
            }

            case "Новый" -> ghostRenderer.draw(event.getMatrices(), target, alpha);
            case "Круг" -> circleRenderer.draw(target, alpha);
        }

        stack.pop();
    }
}