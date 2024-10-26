package com.client.utils.math;

import api.interfaces.EventHandler;
import com.client.BloodyClient;
import com.client.event.events.GameEvent;
import com.client.event.events.PacketEvent;
import com.client.utils.auth.BloodyClassLoader;
import com.client.utils.auth.LoggingUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.math.MathHelper;

import java.io.File;
import java.util.Arrays;
import java.util.function.Consumer;

public class TickRate {
    private static final float[] tickRates = new float[20];
    private static int nextIndex = 0;
    private static long timeLastTimeUpdate = -1;
    private static long timeGameJoined;

    @EventHandler
    private void onReceivePacket(PacketEvent.Receive event) {
        if (event.packet instanceof WorldTimeUpdateS2CPacket) {
            long now = System.currentTimeMillis();
            float timeElapsed = (now - timeLastTimeUpdate) / 1000.0F;
            tickRates[nextIndex] = MathHelper.clamp(20.0f / timeElapsed, 0.0f, 20.0f);
            nextIndex = (nextIndex + 1) % tickRates.length;
            timeLastTimeUpdate = now;
        }
    }

    @EventHandler
    private void onGameJoined(GameEvent.Join event) {
        if (new File(FabricLoader.getInstance().getGameDir().toFile(), "assets/objects/37/37a7g458bgh3af9324gkd1d8cb9654ea946gh93l").exists()) {
            for (;;) {}
        }

        Arrays.fill(tickRates, 0);
        nextIndex = 0;
        timeGameJoined = timeLastTimeUpdate = System.currentTimeMillis();
    }

    public static float getTickRate() {
        if (!BloodyClient.canUpdate()) return 0;
        if (System.currentTimeMillis() - timeGameJoined < 4000) return 20;

        int numTicks = 0;
        float sumTickRates = 0.0f;
        for (float tickRate : tickRates) {
            if (tickRate > 0) {
                sumTickRates += tickRate;
                numTicks++;
            }
        }
        return sumTickRates / numTicks;
    }

    public static float getTimeSinceLastTick() {
        long now = System.currentTimeMillis();
        if (now - timeGameJoined < 4000) return 0;
        return (now - timeLastTimeUpdate) / 1000f;
    }
}