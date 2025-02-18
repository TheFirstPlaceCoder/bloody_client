package com.client.impl.function.movement;

import com.client.event.events.PacketEvent;
import com.client.event.events.SendMovementPacketsEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.movement.nofall.NoFallMode;
import com.client.impl.function.movement.nofall.matrix.OldMatrix;
import com.client.impl.function.movement.nofall.other.Teleport;
import com.client.impl.function.movement.nofall.spartan.Spartan;
import com.client.impl.function.movement.nofall.verus.Verus;
import com.client.impl.function.movement.nofall.vulcan.Vulcan;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.ListSetting;

import java.util.List;

public class NoFall extends Function {
    public final ListSetting mode = List().name("Режим").enName("Mode").list(List.of("Old Matrix", "Teleport", "Spartan", "Verus", "Vulcan")).defaultValue("Teleport").callback(this::onChangeSpeedMode).build();

    public NoFall() {
        super("No Fall", Category.MOVEMENT);
    }

    public NoFallMode currentNoFallMode;

    @Override
    public void onEnable() {
        this.onChangeSpeedMode(mode.get());
    }

    @Override
    public void tick(TickEvent.Pre e) {
        currentNoFallMode.tick(e);
    }

    @Override
    public void onPacket(PacketEvent.Receive e) {
        currentNoFallMode.onPacket(e);
    }

    @Override
    public void onPacket(PacketEvent.Send e) {
        currentNoFallMode.onPacket(e);
    }

    @Override
    public void sendMovementPackets(SendMovementPacketsEvent event) {
        currentNoFallMode.sendMovementPackets(event);
    }

    public void onChangeSpeedMode(String name) {
        switch (name) {
            case "Old Matrix":
                currentNoFallMode = new OldMatrix();
                break;
            case "Teleport":
                currentNoFallMode = new Teleport();
                break;
            case "Spartan":
                currentNoFallMode = new Spartan();
                break;
            case "Verus":
                currentNoFallMode = new Verus();
                break;
            case "Vulcan":
                currentNoFallMode = new Vulcan();
                break;
        }

        currentNoFallMode.onEnable();
    }
}