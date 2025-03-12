package com.client.impl.function.combat;

import com.client.event.events.TickEvent;
import com.client.impl.function.combat.aura.TargetHandler;
import com.client.system.friend.FriendManager;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.game.entity.EntityUtils;
import com.client.utils.game.entity.SelfUtils;
import com.client.utils.game.inventory.FindItemResult;
import com.client.utils.game.inventory.InvUtils;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.world.GameMode;

import java.util.List;

public class TriggerBot extends Function {
    public TriggerBot() {
        super("Trigger Bot", Category.COMBAT);
    }

    private final BooleanSetting criticals = Boolean().name("Только криты").enName("Only Crits").defaultValue(true).build();
    private final BooleanSetting smartCriticals = Boolean().name("Умные криты").enName("Smart Crits").defaultValue(true).visible(criticals::get).build();

    private final ListSetting shield = List().name("Щит").enName("Shield Mode").defaultValue("Ломать").list(List.of("Ломать", "Ждать", "Игнорировать")).build();
    private final BooleanSetting legit = Boolean().name("Ломать легитно").enName("Legit Break").defaultValue(true).visible(() -> shield.get().equals("Ломать")).build();
    private final BooleanSetting weapon = Boolean().name("Только с оружием").enName("Only Weapon").defaultValue(true).build();
    private final BooleanSetting pressingShield = Boolean().name("Отжимать щит").enName("Unpress Shield").defaultValue(true).build();

    private final BooleanSetting pauseOnUse = Boolean().name("Ждать при использовании").enName("Pause On USe").defaultValue(false).build();

    private long shieldWait, shieldMessWait;
    private boolean flag;

    @Override
    public void onEnable() {
        flag = false;
    }

    @Override
    public void onDisable() {
        flag = false;
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (mc.targetedEntity instanceof PlayerEntity player && mc.player.distanceTo(player) <= mc.interactionManager.getReachDistance()) {
            if (!FriendManager.isAttackable(player)) return;
            if (player.isDead()) return;
            if (EntityUtils.getGameMode(player) == GameMode.CREATIVE) return;
            if (!testHand()) return;
            if (!canAttack()) return;

            if (flag) {
                InvUtils.swapBack(true);
                flag = false;
                return;
            }

            TargetHandler.set(player);

            boolean bl = player.isBlocking();
            FindItemResult axe = InvUtils.findInHotbar(itemStack -> itemStack.getItem() instanceof AxeItem);

            if (bl && shield.get().equals("Ждать"))
                return;

            if (bl && shield.get().equals("Ломать")) {
                if (axe.found() && shieldWait > System.currentTimeMillis()) {
                    if (!(mc.player.getMainHandStack().getItem() instanceof AxeItem)) {
                        InvUtils.swap(axe, true);
                    }

                    attack(player);

                    if (!legit.get()) {
                        InvUtils.swapBack(true);
                    } else {
                        flag = true;
                    }

                    if (System.currentTimeMillis() > shieldMessWait) {
                        NotificationManager.add(new Notification(NotificationType.CLIENT,  "Сломал щит игроку " + player.getEntityName(), 1000L), NotificationManager.NotifType.Info);
                        shieldMessWait = System.currentTimeMillis() + 3000L;
                    }

                    shieldWait = System.currentTimeMillis() + 100L;
                }
            }

            if (!flag && (!bl || shield.get().equals("Игнорировать"))) {
                attack(player);
            }
        }
    }

    private void attack(PlayerEntity player) {
        if (pressingShield.get() && SelfUtils.hasItem(Items.SHIELD) && mc.player.isUsingItem()) {
            mc.interactionManager.stopUsingItem(mc.player);
        }

        mc.interactionManager.attackEntity(mc.player, player);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private boolean testHand() {
        return !weapon.get() || mc.player.getMainHandStack().getItem() instanceof AxeItem || mc.player.getMainHandStack().getItem() instanceof SwordItem;
    }

    private boolean canAttack() {
        if (mc.player.isDead()) return false;
        if (pauseOnUse.get() && mc.player.isUsingItem()) return false;

        boolean attack = mc.player.hasStatusEffect(StatusEffects.BLINDNESS) || mc.player.isClimbing()
                || mc.player.isSubmergedInWater() && mc.world.getBlockState(mc.player.getBlockPos()).getBlock() instanceof FluidBlock || mc.player.isRiding()
                || mc.player.abilities.flying || mc.player.isFallFlying();

        if (mc.player.getAttackCooldownProgress(1.5F) < 0.92F) return false;

        boolean jump = !smartCriticals.get() || mc.options.keyJump.isPressed();

        if (!attack && criticals.get() && jump) {
            return !mc.player.isOnGround() && mc.player.fallDistance > 0.0F;
        }

        return true;
    }
}