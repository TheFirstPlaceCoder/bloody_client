package com.client.impl.function.combat.aura;

import com.client.system.companion.DumboOctopusEntity;
import com.client.system.friend.FriendManager;
import com.client.system.function.FunctionManager;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.utils.game.entity.EntityUtils;
import com.client.utils.game.entity.PlayerUtils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameMode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.client.BloodyClient.mc;
import static net.minecraft.util.math.MathHelper.wrapDegrees;

public class TargetHandler {
    private static final List<Entity> STACK = new ArrayList<>();
    private static Entity TARGET;

    public static void handle(MultiBooleanSetting setting, double range, boolean attackThroughWalls) {
        boolean playerFlag = setting.get("Игроки");
        boolean invisibleFlag = setting.get("Инвизы");
        boolean nakedsFlag = setting.get("Голые");
        boolean botsFlag = setting.get("Боты");
        boolean monsterFlag = setting.get("Монстры");
        boolean animalsFlag = setting.get("Животные");
        boolean allFlag = setting.get("Все");

        STACK.clear();

        List<AbstractClientPlayerEntity> players = mc.world.getPlayers().stream().filter(player -> player != mc.player && PlayerUtils.isInRange(player, range)).toList();

        if (!playerFlag || players.isEmpty()) {
            for (Entity entity : mc.world.getEntities()) {
                if (entity instanceof ArmorStandEntity || entity instanceof DumboOctopusEntity) continue;
                if (entity == mc.player) continue;

                if (!(entity instanceof LivingEntity) || PlayerUtils.distanceTo(entity) > range || ((LivingEntity) entity).isDead())
                    continue;

                if (!attackThroughWalls && !EntityUtils.canSee(entity))
                    continue;

                if (allFlag) {
                    STACK.add(entity);
                    continue;
                }

                if (monsterFlag && entity instanceof Monster) {
                    STACK.add(entity);
                }

                if (animalsFlag && entity instanceof AnimalEntity) {
                    STACK.add(entity);
                }
            }

            if (!STACK.isEmpty()) {
                STACK.sort(Comparator.comparing(PlayerUtils::distanceTo));
                TARGET = STACK.get(0);
            }

            return;
        }

        STACK.addAll(players.stream().filter(player -> {
            if (player.isDead()) return false;
            if (!botsFlag && EntityUtils.isBot(player)) return false;
            if (EntityUtils.getGameMode(player) == GameMode.CREATIVE) return false;
            if (!attackThroughWalls && !EntityUtils.canSee(player)) return false;
            if (!FriendManager.isAttackable(player)) return false;
            if (!invisibleFlag && player.hasStatusEffect(StatusEffects.INVISIBILITY) && !isNakeds(player)) return false;
            if (!nakedsFlag && isNakeds(player) && !player.hasStatusEffect(StatusEffects.INVISIBILITY)) return false;
            return PlayerUtils.isInRange(player, range);
        }).toList());

        if (!STACK.isEmpty()) {
            switch (FunctionManager.get(AttackAura.class).sortMode.get()) {
                case "FOV" -> STACK.sort(Comparator.comparing(TargetHandler::sortAngle));
                case "Дистанция" -> STACK.sort(Comparator.comparing(PlayerUtils::distanceTo));
                case "Здоровье" -> STACK.sort(Comparator.comparing(player -> PlayerUtils.getHealth((LivingEntity) player)));
                default -> STACK.sort(Comparator.comparing(TargetHandler::sortAngle).thenComparing(Comparator.comparing(PlayerUtils::distanceTo)).thenComparing(Comparator.comparing(player -> PlayerUtils.getHealth((LivingEntity) player))));
            }
            TARGET = STACK.get(0);
        }
    }

    private static boolean isNakeds(PlayerEntity p) {
        return p.getOffHandStack().isEmpty() && p.getMainHandStack().isEmpty()
                && p.inventory.armor.get(1).isEmpty() && p.inventory.armor.get(0).isEmpty()
                && p.inventory.armor.get(2).isEmpty() && p.inventory.armor.get(3).isEmpty();
    }

    private static float sortAngle(Entity e) {
        double difX = e.getX() - mc.player.getX();
        double difZ = e.getZ() - mc.player.getZ();
        float yaw = (float) wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0);
        return Math.abs(yaw - wrapDegrees(mc.player.yaw));
    }

    public static void set(Entity entity) {
        TARGET = entity;
    }

    public static Entity getTarget() {
        return getTarget(Double.MAX_VALUE);
    }

    public static Entity getTarget(double range) {
        if (TARGET == null || !TARGET.isAlive()) {
            return null;
        }

        if (TARGET instanceof PlayerEntity && FriendManager.isFriend(TARGET)) {
            TARGET = null;
        }

        return PlayerUtils.isInRange(TARGET, range) ? TARGET : null;
    }
}
