package com.client.impl.function.movement;

import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.ListSetting;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public class WaterSpeed extends Function {
    public WaterSpeed() {
        super("Water Speed", Category.MOVEMENT);
    }

    private final ListSetting mode = List().name("Режим").list(List.of("FunTime")).defaultValue("FunTime").build();

    @Override
    public void tick(TickEvent.Pre e) {
        String selectedType = mode.get();

        if (selectedType.equals("FunTime")) {
            WATER_FT();
        }
    }

    private void WATER_FT() {
        PlayerEntity player = mc.player;
        if (player != null && player.isSwimming()) {
            if (player.isSubmergedInWater()) {
                player.setVelocity(player.getVelocity().x * 1.0505, player.getVelocity().y, player.getVelocity().z * 1.0505);
            }
        }
    }

//    public float m = 0;
//    boolean shouldenable = false;
//
//    @Override
//    public void onEnable() {
//        shouldenable = true;
//    }
//
//    @Override
//    public void onPlayerMoveEvent(PlayerMoveEvent event) {
//        if (FunctionUtils.playerSpeed < 3.5) shouldenable = true;
//
//        if (mode.get().equals("HolyWorld")) {
//            if (shouldenable && FunctionUtils.playerSpeed > 3.5) {
//                m = 0.94F;
//
//                for (ItemStack armorItem : mc.player.getArmorItems()) {
//                    if (armorItem.getTranslationKey().contains("boots")) {
//                        m = 1.0F;
//                    }
//                }
//
//                shouldenable = false;
//            }
//
//            if (MovementUtils.isMoving() && MovementUtils.isInLiquid() && !shouldenable) {
//                if (pack.get()) mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(mc.player.yaw, mc.player.pitch, mc.player.isOnGround()));
//
//                if (FunctionUtils.playerSpeed <= getSpeed() && FunctionUtils.playerSpeed > 3.5) {
//                    float const_ = 1.1009F * m;
//
//                    ((IVec3d) event.movement).set(
//                            mc.player.getVelocity().getX() * const_,
//                            mc.player.getVelocity().getY(),
//                            mc.player.getVelocity().getZ() * const_
//                    );
//
//                    m += 0.01;
//                    event.cancel();
//                } else m -= 0.02;
//            }
//        } else {
//            if (MovementUtils.isMoving() && MovementUtils.isInLiquid()) {
//                float m = 0.94F;
//
//                for (ItemStack armorItem : mc.player.getArmorItems()) {
//                    if (armorItem.getTranslationKey().contains("boots")) {
//                        m = 1.0F;
//                    }
//                }
//
//                float const_ = 1.03F * m;
//
//                ((IVec3d) event.movement).set(
//                        mc.player.getVelocity().getX() * const_ * sp.get(),
//                        mc.player.getVelocity().getY(),
//                        mc.player.getVelocity().getZ() * const_ * sp.get()
//                );
//                event.cancel();
//            }
//        }
//    }
//
//    public double getSpeed() {
//        if (hasUnderWater()) {
//            if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
//                return switch (mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier()) {
//                    case 0 -> 9.6;
//                    case 1 -> 10.8;
//                    default -> 12.5;
//                };
//
//            } else return 8.1;
//        } else {
//            if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
//                return switch (mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier()) {
//                    case 0 -> 5.25;
//                    case 1 -> 6.2;
//                    default -> 6.75;
//                };
//
//            } else return 4.5;
//        }
//    }
//
//    public boolean hasUnderWater() {
//        List<String> enchantments = new ArrayList<>();
//        for (ItemStack stack : mc.player.getArmorItems()) {
//            if (!stack.getTranslationKey().contains("boots")) continue;
//
//            if (stack.hasEnchantments()) {
//                for (NbtElement enchantment : stack.getEnchantments()) {
//                    String tag = enchantment.toString().replace("{", "").replace("}", "");
//                    StringBuilder lvl = new StringBuilder();
//                    for (char c : tag.split(",")[0].toCharArray()) {
//                        try {
//                            lvl.append(Integer.parseInt(String.valueOf(c)));
//                        } catch (Exception ignored) {
//                        }
//                    }
//                    StringBuilder enchantName = new StringBuilder();
//                    boolean targ = false;
//                    for (char c : tag.split(",")[1].toCharArray()) {
//                        if (c == '\"') {
//                            if (!targ) {
//                                targ = true;
//                                continue;
//                            } else {
//                                break;
//                            }
//                        }
//                        if (targ) {
//                            enchantName.append(c);
//                        }
//                    }
//
//                    enchantments.add(enchantName.toString().split(":")[1] + ":" + lvl);
//                }
//            }
//        }
//
//        return enchantments.contains("depth_strider:3");
//    }
}