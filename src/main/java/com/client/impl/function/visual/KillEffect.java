package com.client.impl.function.visual;

import com.client.event.events.PacketEvent;
import com.client.event.events.Render2DEvent;
import com.client.event.events.SendMovementPacketsEvent;
import com.client.impl.function.movement.Timer;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.math.MsTimer;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.List;

public class KillEffect extends Function {
    public KillEffect() {
        super("Kill Effect", Category.VISUAL);
    }

    private final ListSetting mode = List().name("Режим").list(List.of("Молния", "Клиентский", "Огненные частицы")).defaultValue("Молния").build();

    private final MsTimer timerHelper = new MsTimer();

    private boolean timerForce2;
    private boolean died;
    private boolean hasChanged;

    @Override
    public void onEnable() {
        timerForce2 = false;
        died = false;
        hasChanged = false;
    }

    @Override
    public void onDisable() {
        timerForce2 = false;
    }

    @Override
    public void onPacket(PacketEvent.Receive event) {
        if (event.packet instanceof EntityStatusS2CPacket p && p.getEntity(mc.world) instanceof PlayerEntity player) {
            if (player != mc.player && p.getStatus() == 3 && mc.player.distanceTo(player) < 7) {
                if (mode.get().equals("Молния")) {
                    double x = player.getX();
                    double y = player.getY();
                    double z = player.getZ();

                    LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, mc.world);

                    lightning.updatePosition(x, y, z);
                    lightning.refreshPositionAfterTeleport(x, y, z);
                    mc.world.addEntity(lightning.getEntityId(), lightning);
                } else if (mode.get().equals("Огненные частицы")) {
                    for (int i = 0; i < 360; i+=5) {
                        double sin = Math.sin(Math.toRadians(i)) * player.getWidth() * 1.2;
                        double cos = Math.cos(Math.toRadians(i)) * player.getWidth() * 1.2;

                        mc.world.addParticle(ParticleTypes.LAVA, player.getX() + cos, player.getY() + player.getHeight() * 0.75F, player.getZ() + sin, 0, 0, 0);
                    }
                } else {
                    died = true;
                    timerHelper.reset();
                }
            }
        }
    }

    @Override
    public void sendMovementPackets(SendMovementPacketsEvent event) {
        if (!mode.get().equals("Клиентский")) return;

        if (timerForce2) {
            Timer.setOverride(0.2F);
            timerForce2 = false;
        }

        if (died && !timerHelper.passedMs(1500)) {
            getMemoryTrigger();
        } else if (hasChanged) {
            Timer.setOverride(Timer.OFF);
            hasChanged = false;
            died = false;
        }
    }

    private void getMemoryTrigger() {
        Timer.setOverride(0.075F);
        timerForce2 = true;
        hasChanged = true;
    }

    public float getStrikeEffectFovModifyPC() {
        return getGlobalEffectAnimation();
    }

    private float getGlobalEffectAnimation() {
        if (!died || timerHelper.passedMs(1500)) return 1F;

        if (!timerHelper.passedMs(1000)) {
            return (1500 - timerHelper.getTimeMs()) / 1000f;
        } else {
            return 1 - ((1500 - timerHelper.getTimeMs()) / 1500f);
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        if (!mode.get().equals("Клиенсткий")) return;

        if (died && !timerHelper.passedMs(1500)) drawLightVignette(getStrikeEffectFovModifyPC());
    }

    private void drawLightVignette(float alpha) {
        GL.drawQuad(0, 0, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight(), new Color(255, 255, 255, (int) MathHelper.clamp(140 * (1 - alpha), 0, 255)));
    }
}