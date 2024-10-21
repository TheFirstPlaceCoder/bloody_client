package com.client.impl.function.movement;

import com.client.event.events.PlayerMoveEvent;
import com.client.interfaces.IVec3d;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.game.movement.MovementUtils;
import com.client.utils.misc.FunctionUtils;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import java.util.ArrayList;
import java.util.List;

public class LiquidMovement extends Function {

    private final ListSetting mode = List().name("Режим").list(List.of("HolyWorld")).defaultValue("HolyWorld").build();
    public final BooleanSetting pack = Boolean().name("Обход ротации").defaultValue(true).build();

    public LiquidMovement() {
        super("Liquid Movement", Category.MOVEMENT);
    }

    public float m = 0;

    @Override
    public void onEnable() {
        m = 0.94F;

        for (ItemStack armorItem : mc.player.getArmorItems()) {
            if (armorItem.getTranslationKey().contains("boots")) {
                m = 1.0F;
            }
        }
    }

    public boolean shouldTrue() {
        return isEnabled() && FunctionUtils.playerSpeed <= getSpeed();
    }

    @Override
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (MovementUtils.isMoving() && MovementUtils.isInLiquid()) {
            if (pack.get()) mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(mc.player.yaw, mc.player.pitch, mc.player.isOnGround()));

            if (FunctionUtils.playerSpeed <= getSpeed()) {
                float const_ = 1.1009F * m;

                ((IVec3d) event.movement).set(
                        mc.player.getVelocity().getX() * const_,
                        mc.player.getVelocity().getY(),
                        mc.player.getVelocity().getZ() * const_
                );

                m += 0.01;
                event.cancel();
            } else m -= 0.02;
        }
    }

    public double getSpeed() {
        if (hasUnderWater()) {
            if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
                return switch (mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier()) {
                    case 0 -> 7.2;
                    case 1 -> 9;
                    default -> 9.75;
                };

            } else return 6.9;
        } else {
            if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
                return switch (mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier()) {
                    case 0 -> 4.35;
                    case 1 -> 4.8;
                    default -> 5.25;
                };

            } else return 3.75;
        }
    }

    public boolean hasUnderWater() {
        List<String> enchantments = new ArrayList<>();
        for (ItemStack stack : mc.player.getArmorItems()) {
            if (!stack.getTranslationKey().contains("boots")) continue;

            if (stack.hasEnchantments()) {
                for (NbtElement enchantment : stack.getEnchantments()) {
                    String tag = enchantment.toString().replace("{", "").replace("}", "");
                    StringBuilder lvl = new StringBuilder();
                    for (char c : tag.split(",")[0].toCharArray()) {
                        try {
                            lvl.append(Integer.parseInt(String.valueOf(c)));
                        } catch (Exception ignored) {
                        }
                    }
                    StringBuilder enchantName = new StringBuilder();
                    boolean targ = false;
                    for (char c : tag.split(",")[1].toCharArray()) {
                        if (c == '\"') {
                            if (!targ) {
                                targ = true;
                                continue;
                            } else {
                                break;
                            }
                        }
                        if (targ) {
                            enchantName.append(c);
                        }
                    }

                    enchantments.add(enchantName.toString().split(":")[1] + ":" + lvl);
                }
            }
        }

        return enchantments.contains("depth_strider:3");
    }
}
