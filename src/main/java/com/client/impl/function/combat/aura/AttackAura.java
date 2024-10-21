package com.client.impl.function.combat.aura;

import api.interfaces.EventHandler;
import api.main.EventUtils;
import com.client.BloodyClient;
import com.client.event.events.PlayerUpdateEvent;
import com.client.impl.function.combat.aura.rotate.RotationHandler;
import com.client.impl.function.combat.aura.rotate.handler.Handlers;
import com.client.interfaces.IGameRenderer;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.Utils;
import com.client.utils.auth.*;
import com.client.utils.game.entity.SelfUtils;
import com.client.utils.game.inventory.FindItemResult;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.misc.FunctionUtils;
import com.sun.jna.Platform;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * __aaa__
 * 03.06.2024
 * */
public class AttackAura extends Function {
    public AttackAura() {
        super("Attack Aura", Category.COMBAT);
        Handlers.init();
        EventUtils.register(new RotationHandler());
        RotationHandler.register(this);
    }

    public final DoubleSetting range = Double().name("Дистанция").defaultValue(3.0).min(1).max(6).build();

    public final ListSetting bypass = List().name("Обход").list(List.of(
            "FunTime", "HolyWorld", "ReallyWorld", "HvH"
    )).defaultValue("FunTime").build();

    public final IntegerSetting tick = Integer().name("Тик ротации").max(5).min(1).defaultValue(3).visible(() -> bypass.get().equals("ReallyWorld")).build();

    public final ListSetting moveFix = List().name("Корекция").list(List.of("Обычная", "Сфокусированная", "Нет")).defaultValue("Обычная").visible(() -> !bypass.get().equals("ReallyWorld")).build();
    public final DoubleSetting rangeFollow = Double().name("Дистанция преследования").defaultValue(3.0).min(1).max(8).visible(() -> moveFix.get().equals("Сфокусированная")).build();

    private final BooleanSetting elytraPvp = Boolean().name("Элитра таргет").defaultValue(true).build();
    public final DoubleSetting elytraRange = Double().name("Дистанция в полете").defaultValue(30.0).min(1).max(100).visible(elytraPvp::get).build();

    public final ListSetting sortMode = List().name("Сортировка").list(List.of("Дистанция", "Здоровье", "FOV", "Всему")).defaultValue("Дистанция").build();

    private final MultiBooleanSetting targets = MultiBoolean().name("Цели").defaultValue(List.of(
            new MultiBooleanValue(true, "Игроки"),
            new MultiBooleanValue(false, "Инвизы"),
            new MultiBooleanValue(false, "Голые"),
            new MultiBooleanValue(false, "Монстры"),
            new MultiBooleanValue(false, "Животные"),
            new MultiBooleanValue(false, "Все")
    )).build();

    private final BooleanSetting criticals = Boolean().name("Только криты").defaultValue(true).build();
    private final BooleanSetting smartCriticals = Boolean().name("Умные криты").defaultValue(true).visible(criticals::get).build();

    private final ListSetting shield = List().name("Щит").defaultValue("Ломать").list(List.of("Ломать", "Ждать", "Игнорировать")).build();

    private final BooleanSetting weapon = Boolean().name("Только с оружием").defaultValue(true).build();
    public final BooleanSetting wallsAttack = Boolean().name("Бить через стены").defaultValue(false).build();
    private final BooleanSetting pressingShield = Boolean().name("Отжимать щит").defaultValue(true).build();
    private final BooleanSetting pauseOnUse = Boolean().name("Ждать при использовании").defaultValue(false).build();

    //public final BooleanSetting debug = Boolean().name("Debug").defaultValue(false).visible(Loader::isDev).build();

    private final Pattern ROTATION_PATTERN = Pattern.compile("[rotate-entity]=>log(sin(yaw))*log(cos(pitch))");

    public Entity target;
    private long shieldWait, shieldMessWait, attackTime;

    @Override
    public void onEnable() {
        target = null;
    }

    @Override
    public void onDisable() {
        target = null;
    }

    @EventHandler
    private void onTickEvent(PlayerUpdateEvent event) {
        double radius = gerRadius();
        FunctionUtils.range = radius;
        TargetHandler.handle(targets, radius, wallsAttack.get());
        target = TargetHandler.getTarget(radius);

        if (target == null || (!testHand() || shield())) return;

        if (canAttack() && System.currentTimeMillis() > attackTime) {
            attack();
        }
    }

    public double gerRadius() {
        return isAllowElytraPvp() ? elytraRange.get() : (moveFix.get().equals("Сфокусированная") ? rangeFollow.get() : range.get());
    }

    public boolean isAllowElytraPvp() {
        return elytraPvp.get() && mc.player.isFallFlying() && mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA;
    }

    private void attack() {
        boolean bl = mc.player.isSprinting();
        boolean bl2 = false;

//        if (bypass.get().equals("FunTime") && (checkPattern(target, RotationHandler.serverYaw, RotationHandler.serverPitch)))
//            return;

        if (pressingShield.get() && SelfUtils.hasItem(Items.SHIELD) && mc.player.isUsingItem()) {
            mc.interactionManager.stopUsingItem(mc.player);
        }

        if (bl) {
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
            mc.player.setSprinting(false);
            bl2 = true;
        }

        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);

        if (!(mc.player.getMainHandStack().getItem() instanceof AxeItem || mc.player.getMainHandStack().getItem() instanceof SwordItem))
            attackTime = System.currentTimeMillis() + 500L;

        if (bl2) {
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
            mc.player.setSprinting(true);
        }
    }

    public boolean canAttack() {
        return canAttack(true);
    }

    public boolean canAttack(boolean raytrace) {
        if (target == null || mc.player.isDead()) return false;
        if (pauseOnUse.get() && mc.player.isUsingItem()) return false;
        if ((((IGameRenderer) mc.gameRenderer).getTarget(RotationHandler.serverYaw, RotationHandler.serverPitch) != target && !bypass.get().equals("HvH")) && raytrace && !wallsAttack.get()) return false;
        if (mc.player.distanceTo(target) > range.get()) return false;
        if (target instanceof PlayerEntity && ((PlayerEntity) target).isBlocking() && shield.get().equals("Ломать") && !raytrace) return true;

        boolean attack = mc.player.hasStatusEffect(StatusEffects.BLINDNESS) || mc.player.isClimbing()
                || mc.player.isSubmergedInWater() && mc.world.getBlockState(mc.player.getBlockPos()).getBlock() instanceof FluidBlock || mc.player.isRiding()
                || mc.player.abilities.flying || mc.player.isFallFlying();

        if (mc.player.getAttackCooldownProgress(1.5F) < 0.92) return false;

        boolean jump = !smartCriticals.get() || mc.options.keyJump.isPressed();

        if (!attack && criticals.get() && jump) {
            return !mc.player.isOnGround() && mc.player.fallDistance > 0.0F;
        }

        return true;
    }

    public boolean testHand() {
        return !weapon.get() || mc.player.getMainHandStack().getItem() instanceof AxeItem || mc.player.getMainHandStack().getItem() instanceof SwordItem;
    }

    private boolean shield() {
        if (target == null) return false;
        if (!(target instanceof PlayerEntity)) return false;

        if (shield.get().equals("Игнорировать")) return false;

        boolean bl = ((PlayerEntity) target).isBlocking();
        if (bl && shield.get().equals("Ждать")) return true;

        if (bl) {
            FindItemResult axe = InvUtils.find(itemStack -> itemStack.getItem() instanceof AxeItem);
            if (!axe.found() || shieldWait > System.currentTimeMillis()) return false;

            if (axe.isHotbar()) {
                mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(axe.slot()));
            } else {
                InvUtils.quickSwap().fromId(axe.slot()).to(mc.player.inventory.selectedSlot);
            }

            mc.interactionManager.attackEntity(mc.player, target);

            if (System.currentTimeMillis() > shieldMessWait) {
                NotificationManager.add(new Notification(NotificationType.CLIENT,  "Сломал щит игроку " + target.getEntityName(), 1000L), NotificationManager.NotifType.Info);
                shieldMessWait = System.currentTimeMillis() + 3000L;
            }

            if (axe.isHotbar()) {
                mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            } else {
                InvUtils.quickSwap().fromId(axe.slot()).to(mc.player.inventory.selectedSlot);
            }

            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.inventory.selectedSlot));
            shieldWait = System.currentTimeMillis() + 100L;
        }

        return false;
    }

    private boolean checkPattern(Entity target, float yaw, float pitch) {
        String patternStr = "positionDelta;%s".formatted(target.getPos().subtract(mc.player.getPos()).add(mc.player.getCameraPosVec(1.0F)).toString()) + ";" + "sin:" + yaw + ";" + "cos:" + pitch;

        Pattern p = Pattern.compile(patternStr);
        Matcher m = p.matcher(ROTATION_PATTERN.pattern());

        return m.matches();
    }
}
