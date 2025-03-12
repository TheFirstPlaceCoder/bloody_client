package com.client.impl.function.combat;

import com.client.event.events.PacketEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;

import java.util.List;

public class FastProjectile extends Function {
    public final MultiBooleanSetting listSetting = MultiBoolean().name("Работать на").enName("Items to spoof").defaultValue(List.of(
            new MultiBooleanValue(true, "Перлы"),
            new MultiBooleanValue(true, "Лук"),
            new MultiBooleanValue(true, "Арбалет")
    )).build();

    public final IntegerSetting timeOut = Integer().name("Задержка после использования").enName("Cooldown").defaultValue(3000).min(1500).max(7000).build();
    public final IntegerSetting spoofs = Integer().name("Сила").enName("Power").defaultValue(10).min(1).max(25).build();
    public final BooleanSetting bypass = Boolean().name("Обход").enName("Bypass").defaultValue(true).build();

    public FastProjectile() {
        super("Fast Projectile", Category.COMBAT);
    }

    private long lastShootTime;

    @Override
    public void onEnable() {
        lastShootTime = System.currentTimeMillis();
    }

    @Override
    public void onPacket(PacketEvent.Sent event) {
        if (event.packet instanceof PlayerActionC2SPacket packet) {
            if (packet.getAction() == PlayerActionC2SPacket.Action.RELEASE_USE_ITEM) {
                ItemStack handStack = mc.player.getStackInHand(Hand.MAIN_HAND);

                if (!handStack.isEmpty() && handStack.getItem() != null && ((handStack.getItem() instanceof BowItem && listSetting.get("Лук")) || (handStack.getItem() instanceof CrossbowItem && listSetting.get("Арбалет")))) {
                    doSpoofs();
                }
            }

        } else if (event.packet instanceof PlayerInteractItemC2SPacket packet2) {
            if (packet2.getHand() == Hand.MAIN_HAND) {
                ItemStack handStack = mc.player.getStackInHand(Hand.MAIN_HAND);

                if (!handStack.isEmpty() && handStack.getItem() != null) {
                    if (handStack.getItem() instanceof EnderPearlItem && listSetting.get("Перлы")) {
                        doSpoofs();
                    }
                }
            }
        }
    }

    private void doSpoofs() {
        if (System.currentTimeMillis() - lastShootTime >= timeOut.get()) {
            lastShootTime = System.currentTimeMillis();

            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));

            for (int i = 0; i < spoofs.get(); i++) {
                if (bypass.get()) {
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(mc.player.getX(), mc.player.getY() + 1e-10, mc.player.getZ(), false));
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(mc.player.getX(), mc.player.getY() - 1e-10, mc.player.getZ(), true));
                } else {
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(mc.player.getX(), mc.player.getY() - 1e-10, mc.player.getZ(), true));
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(mc.player.getX(), mc.player.getY() + 1e-10, mc.player.getZ(), false));
                }
            }
        }
    }
}
