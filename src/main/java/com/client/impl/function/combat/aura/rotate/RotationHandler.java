package com.client.impl.function.combat.aura.rotate;

import api.interfaces.EventHandler;
import com.client.event.events.*;
import com.client.impl.function.combat.aura.AttackAura;
import com.client.impl.function.combat.aura.rotate.handler.Handler;
import com.client.impl.function.combat.aura.rotate.handler.Handlers;
import com.client.impl.function.combat.aura.rotate.handler.handlers.FunTimeRotationsHandler;
import com.client.utils.game.movement.MovementUtils;
import com.client.utils.render.wisetree.font.main.IFont;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

import static com.client.system.function.Function.mc;

//сигма код
public class RotationHandler {
    private static AttackAura aura;

    public static float serverYaw, serverPitch;
    public static float prevServerYaw = 0, prevServerPitch = 0, diffYaw, diffPitch, maxDiffYaw;

    public static String currentPacket;

    public static boolean attack, successful;

    public static RotationTask task = RotationTask.IDLE;
    private static long set_time = 0;

    private static Handler handler = new Handler("HvH");

    @EventHandler
    private void onRender2DEvent(Render2DEvent event) {
//        if (aura.debug.get()) {
//            IFont.draw(IFont.MONTSERRAT_MEDIUM, task.name(), 5, 5, Color.WHITE, 8);
//            IFont.draw(IFont.MONTSERRAT_MEDIUM, "yaw: " + round(serverYaw) + " | client: " + round(mc.player.yaw) + " | diff: " + round(diffYaw) + " | max diff: " + round(maxDiffYaw), 5, 15, Color.WHITE, 8);
//            IFont.draw(IFont.MONTSERRAT_MEDIUM, "pitch: " + round(serverPitch) + " | diff: " + round(diffPitch), 5, 25, Color.WHITE, 8);
//            IFont.draw(IFont.MONTSERRAT_MEDIUM, "time: " + MathHelper.clamp(set_time - System.currentTimeMillis(), 0, 600L), 5, 35, Color.WHITE, 8);
//            IFont.draw(IFont.MONTSERRAT_MEDIUM, "packet: " + currentPacket, 5, 45, Color.WHITE, 8);
//            IFont.draw(IFont.MONTSERRAT_MEDIUM, "attack: " + successful, 5, 55, Color.WHITE, 8);
//        }
    }

    @EventHandler
    private void onPacketEvent(PacketEvent.Send event) {
        if (event.packet instanceof PlayerMoveC2SPacket.PositionOnly) {
            currentPacket = "position";
        }
        if (event.packet instanceof PlayerMoveC2SPacket.Both) {
            currentPacket = "both";
        }
        if (event.packet instanceof PlayerMoveC2SPacket.LookOnly) {
            currentPacket = "look";
        }
        if (event.packet instanceof PlayerInteractEntityC2SPacket packet) {
            if (packet.getType().equals(PlayerInteractEntityC2SPacket.InteractionType.ATTACK)) {
                attack = true;
            }

            successful = false;
        }
    }

    @EventHandler
    private void onReceiveChatMessage(ReceiveChatMessageEvent event) {
        if (event.message.contains("Хей")) {
            successful = true;
        }

        attack = false;
    }


    private float round(float i) {
        return (float) (Math.round(i * 10.0) / 10.0);
    }

    @EventHandler
    private void onTickEvent(TickEvent.Pre event) {
        if (aura.isEnabled()) {
            if (aura.target == null) {
                if (System.currentTimeMillis() > set_time && set_time != 0) {
                    task = RotationTask.IDLE;
                    return;
                }
                task = RotationTask.SET;
                if (set_time == 0) {
                    set_time = System.currentTimeMillis() + calculateTime();
                }
            } else {
                task = RotationTask.AIM;
                set_time = 0;
            }
        } else {
            if (handler instanceof FunTimeRotationsHandler p) p.incrementTicks = 0;
            if (System.currentTimeMillis() > set_time && set_time != 0) {
                task = RotationTask.IDLE;
                return;
            }
            task = RotationTask.SET;
            if (set_time == 0) {
                set_time = System.currentTimeMillis() + calculateTime();
            }
        }
    }

    @EventHandler(priority = 999)
    private void onSendMovementPacketsEvent(SendMovementPacketsEvent event) {
        if (!checkIdle()) {
            if ((mc.player.isFallFlying() && !aura.isAllowElytraPvp()) && mc.player.isUsingItem() && aura.target == null) return;

            event.both = true;
            event.yaw = serverYaw;
            event.pitch = MathHelper.clamp(serverPitch, -90.0F, 90.0F);

            if (aura.isAllowElytraPvp()) {
                mc.player.yaw = serverYaw;
                mc.player.pitch = MathHelper.clamp(serverPitch, -90.0F, 90.0F);
            }
        }
    }

    @EventHandler
    private void onKeyboardInputEvent(KeyboardInputEvent event) {
        if (!checkIdle()) {
            if (aura.moveFix.get().equals("Обычная") || aura.bypass.get().equals("ReallyWorld")) {
                MovementUtils.fixMovement(event, RotationHandler.serverYaw);
                event.cancel();
            }
        }
    }

    @EventHandler
    private void onUpdate(TickEvent.Pre event) {
        switch (aura.bypass.get()) {
            case "HolyWorld" -> handler = Handlers.get("HolyWorld");
            case "ReallyWorld" -> handler = Handlers.get("ReallyWorld");
            case "FunTime" -> handler = Handlers.get("FunTime");
            case "HvH" -> handler = Handlers.get("HvH");
        }

        if (checkIdle() || handler.name.equals("HvH")) {
            serverYaw = mc.player.yaw;
            serverPitch = mc.player.pitch;

            handler.getRotate().a = serverYaw;
            handler.getRotate().b = serverPitch;
            return;
        }

        if (handler instanceof FunTimeRotationsHandler p) p.tick1(aura.target == null ? mc.player : aura.target, (aura.moveFix.get().equals("Сфокусированная") ? aura.rangeFollow.get() : aura.range.get()), aura.isEnabled());

        for (int i = 0; i < 3; i++) {
            if (aura.isAllowElytraPvp()) handler.elytraTick(aura.target == null ? mc.player : aura.target, aura.elytraRange.get());
            else handler.tick(aura.target == null ? mc.player : aura.target, (aura.moveFix.get().equals("Сфокусированная") ? aura.rangeFollow.get() : aura.range.get()));
        }

        serverYaw = handler.getRotate().a;
        serverPitch = handler.getRotate().b;
    }

    public static Handler getHandler() {
        return handler;
    }

    public static long calculateTime() {
        return (long) MathHelper.clamp(Math.abs(serverYaw - mc.player.yaw), 400L, 600L);
    }

    public static boolean checkIdle() {
        return task.equals(RotationTask.IDLE) || handler.name.equals("HvH");
    }

    public static boolean checkMoveFix() {
        return (!aura.moveFix.get().equals("Нет") || aura.bypass.get().equals("ReallyWorld")) && !checkIdle();
    }

    public static void register(AttackAura attackAura) {
        RotationHandler.aura = attackAura;
    }
}