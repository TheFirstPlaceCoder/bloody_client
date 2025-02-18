package com.client.impl.function.visual;

import api.interfaces.EventHandler;
import com.client.event.events.GameEvent;
import com.client.event.events.KeyEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.DoubleSetting;
import com.client.utils.misc.InputUtils;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * __aaa__
 * 21.05.2024
 * */
public class Freecam extends Function {
    public Freecam() {
        super("Freecam", Category.VISUAL);
    }

    private final DoubleSetting speedXZ = Double().name("Скорость XZ").enName("Horizontal Speed").defaultValue(0.3).min(0).max(5).build();
    private final DoubleSetting speedY = Double().name("Скорость Y").enName("Vertical Speed").defaultValue(0.3).min(0).max(5).build();

    public Vec3d pos = new Vec3d(0, 0, 0);
    public Vec3d prevPos = new Vec3d(0, 0, 0);

    private Perspective perspective;
    public float yaw, pitch;

    public float prevYaw, prevPitch;

    private boolean forward, backward, right, left, up, down;

    @Override
    public void onEnable() {
        if (!canUpdate()) {
            toggle();
            return;
        }

        yaw = mc.player.yaw;
        pitch = mc.player.pitch;

        perspective = mc.options.getPerspective();

        pos = mc.gameRenderer.getCamera().getPos();
        prevPos = mc.gameRenderer.getCamera().getPos();

        prevYaw = yaw;
        prevPitch = pitch;

        forward = false;
        backward = false;
        right = false;
        left = false;
        up = false;
        down = false;

        unpress();
    }

    @Override
    public void onDisable() {
        if (canUpdate()) {
            mc.options.setPerspective(perspective);
        }
    }

    private void unpress() {
        mc.options.keyForward.setPressed(false);
        mc.options.keyBack.setPressed(false);
        mc.options.keyRight.setPressed(false);
        mc.options.keyLeft.setPressed(false);
        mc.options.keyJump.setPressed(false);
        mc.options.keySneak.setPressed(false);
    }

    @Override
    public void tick(TickEvent.Post event){
        if (perspective == null || mc.options.getPerspective() == null) return;
        if (mc.cameraEntity.isInsideWall()) mc.getCameraEntity().noClip = true;
        if (!perspective.isFirstPerson()) mc.options.setPerspective(Perspective.FIRST_PERSON);

        Vec3d forward = Vec3d.fromPolar(0, yaw);
        Vec3d right = Vec3d.fromPolar(0, yaw + 90);
        double velX = 0;
        double velY = 0;
        double velZ = 0;

        double s = 0.5;
        if (mc.options.keySprint.isPressed()) s = 1;

        boolean a = false;

        double speedValueXZ = speedXZ.get();
        double speedValueY = speedY.get();

        if (this.forward) {
            velX += forward.x * s * speedValueXZ;
            velZ += forward.z * s * speedValueXZ;
            a = true;
        }
        if (this.backward) {
            velX -= forward.x * s * speedValueXZ;
            velZ -= forward.z * s * speedValueXZ;
            a = true;
        }

        boolean b = false;
        if (this.right) {
            velX += right.x * s * speedValueXZ;
            velZ += right.z * s * speedValueXZ;
            b = true;
        }
        if (this.left) {
            velX -= right.x * s * speedValueXZ;
            velZ -= right.z * s * speedValueXZ;
            b = true;
        }

        if (a && b) {
            double diagonal = 1 / Math.sqrt(2);
            velX *= diagonal;
            velZ *= diagonal;
        }

        if (this.up) {
            velY += s * speedValueY;
        }
        if (this.down) {
            velY -= s * speedValueY;
        }

        prevPos = pos;
        pos = new Vec3d(pos.x + velX, pos.y + velY, pos.z + velZ);
    }

    @EventHandler
    private void onKeyEvent(KeyEvent event) {
        if (mc.currentScreen instanceof ChatScreen) return;

        boolean cancel = true;

        if (mc.options.keyForward.matchesKey(event.key, 0) || mc.options.keyForward.matchesMouse(event.key)) {
            forward = event.action != InputUtils.Action.RELEASE;
            mc.options.keyForward.setPressed(false);
        } else if (mc.options.keyBack.matchesKey(event.key, 0) || mc.options.keyBack.matchesMouse(event.key)) {
            backward = event.action != InputUtils.Action.RELEASE;
            mc.options.keyBack.setPressed(false);
        } else if (mc.options.keyRight.matchesKey(event.key, 0) || mc.options.keyRight.matchesMouse(event.key)) {
            right = event.action != InputUtils.Action.RELEASE;
            mc.options.keyRight.setPressed(false);
        } else if (mc.options.keyLeft.matchesKey(event.key, 0) || mc.options.keyLeft.matchesMouse(event.key)) {
            left = event.action != InputUtils.Action.RELEASE;
            mc.options.keyLeft.setPressed(false);
        } else if (mc.options.keyJump.matchesKey(event.key, 0) || mc.options.keyJump.matchesMouse(event.key)) {
            up = event.action != InputUtils.Action.RELEASE;
            mc.options.keyJump.setPressed(false);
        } else if (mc.options.keySneak.matchesKey(event.key, 0) || mc.options.keySneak.matchesMouse(event.key)) {
            down = event.action != InputUtils.Action.RELEASE;
            mc.options.keySneak.setPressed(false);
        } else {
            cancel = false;
        }

        if (cancel) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onGameLeft(GameEvent.Left event) {
        toggle();
    }

    public void changeLookDirection(double deltaX, double deltaY) {
        prevYaw = yaw;
        prevPitch = pitch;

        yaw += (float) deltaX;
        pitch += (float) deltaY;

        pitch = MathHelper.clamp(pitch, -90, 90);
    }

    public double getX(float tickDelta) {
        return MathHelper.lerp(tickDelta, prevPos.x, pos.x);
    }
    public double getY(float tickDelta) {
        return MathHelper.lerp(tickDelta, prevPos.y, pos.y);
    }
    public double getZ(float tickDelta) {
        return MathHelper.lerp(tickDelta, prevPos.z, pos.z);
    }

    public double getYaw(float tickDelta) {
        return MathHelper.lerp(tickDelta, prevYaw, yaw);
    }
    public double getPitch(float tickDelta) {
        return MathHelper.lerp(tickDelta, prevPitch, pitch);
    }
}
