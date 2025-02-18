package com.client.impl.function.player;

import api.interfaces.EventHandler;
import com.client.event.events.KeyEvent;
import com.client.event.events.SoundEvent;
import com.client.event.events.TickEvent;
import com.client.interfaces.IMinecraftClient;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.utils.game.inventory.InvUtils;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.item.Items;

public class AutoFish extends Function {
    public final BooleanSetting autoCast = Boolean().name("Автоматический режим").defaultValue(true).build();
    public final IntegerSetting ticksAutoCast = Integer().name("Задержка автоматического режима").defaultValue(10).min(0).max(60).build();
    public final IntegerSetting ticksCatch = Integer().name("Задержка в воде").defaultValue(6).min(0).max(60).build();
    public final IntegerSetting ticksThrow = Integer().name("Задержка перед броском").defaultValue(14).min(0).max(60).build();

    public AutoFish() {
        super("Auto Fish", Category.PLAYER);
    }

    private boolean ticksEnabled;
    private int ticksToRightClick;
    private int ticksData;

    private int autoCastTimer;
    private boolean autoCastEnabled;

    private int autoCastCheckTimer;

    @Override
    public void onEnable() {
        ticksEnabled = false;
        autoCastEnabled = false;
        autoCastCheckTimer = 0;
    }

    @EventHandler
    private void onPlaySound(SoundEvent event) {
        SoundInstance p = event.soundInstance;

        if (p.getId().getPath().equals("entity.fishing_bobber.splash")) {
            ticksEnabled = true;
            ticksToRightClick = ticksCatch.get();
            ticksData = 0;
        }
    }

    @Override
    public void tick(TickEvent.Post event) {
        // Auto cast
        if (autoCastCheckTimer <= 0) {
            autoCastCheckTimer = 30;

            if (autoCast.get() && !ticksEnabled && !autoCastEnabled && mc.player.fishHook == null && hasFishingRod()) {
                autoCastTimer = 0;
                autoCastEnabled = true;
            }
        } else {
            autoCastCheckTimer--;
        }

        // Check for auto cast timer
        if (autoCastEnabled) {
            autoCastTimer++;

            if (autoCastTimer > ticksAutoCast.get()) {
                autoCastEnabled = false;
                ((IMinecraftClient) mc).rightClick();
            }
        }

        // Handle logic
        if (ticksEnabled && ticksToRightClick <= 0) {
            if (ticksData == 0) {
                ((IMinecraftClient) mc).rightClick();
                ticksToRightClick = ticksThrow.get();
                ticksData = 1;
            }
            else if (ticksData == 1) {
                ((IMinecraftClient) mc).rightClick();
                ticksEnabled = false;
            }
        }

        ticksToRightClick--;
    }

    @EventHandler
    private void onKey(KeyEvent event) {
        if (mc.options.keyUse.isPressed() && hasFishingRod()) ticksEnabled = false;
    }

    private boolean hasFishingRod() {
        if (InvUtils.findInHotbar(itemStack -> itemStack.getItem() == Items.FISHING_ROD).found()) {
            InvUtils.swap(InvUtils.findInHotbar(itemStack -> itemStack.getItem() == Items.FISHING_ROD).slot(), false);
            return true;
        }
        return false;
    }
}
