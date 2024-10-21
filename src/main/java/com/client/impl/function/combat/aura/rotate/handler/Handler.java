package com.client.impl.function.combat.aura.rotate.handler;

import com.client.utils.math.vector.floats.V2F;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.List;

import static com.client.BloodyClient.mc;

public class Handler {
    public String name;

    public void tick(Entity target, double range) {}

    public void elytraTick(Entity target, double range) {}

    public V2F getBestPoint(Entity target, double range) {
        return new V2F(mc.player.yaw, mc.player.pitch);
    }

    public V2F getRotate() {
        return new V2F(mc.player.yaw, mc.player.pitch);
    }

    public Handler(String name) {
        this.name = name;
    }
}