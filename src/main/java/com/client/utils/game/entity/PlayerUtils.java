package com.client.utils.game.entity;

import com.client.impl.function.movement.Timer;
import com.client.system.function.Function;
import com.client.utils.misc.FunctionUtils;
import com.mojang.datafixers.types.Func;
import mixin.accessor.LivingEntityAccessor;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.client.BloodyClient.mc;

public class PlayerUtils {
    private static final double diagonal = 1 / Math.sqrt(2);
    private static final Vec3d horizontalVelocity = new Vec3d(0, 0, 0);

    public static boolean isInRange(Entity entity, double dist) {
        if (entity == null) return false;
        double width = entity.getWidth() / 2;

        double x = entity.getX();
        double z = entity.getZ();

        List<Vec3d> list = new ArrayList<>() {{
            add(new Vec3d(x + width, entity.getY(), z + width));
            add(new Vec3d(x + width, entity.getY(), z - width));
            add(new Vec3d(x - width, entity.getY(), z + width));
            add(new Vec3d(x - width, entity.getY(), z - width));
            add(new Vec3d(x + width, entity.getY() + entity.getHeight(), z + width));
            add(new Vec3d(x + width, entity.getY() + entity.getHeight(), z - width));
            add(new Vec3d(x - width, entity.getY() + entity.getHeight(), z + width));
            add(new Vec3d(x - width, entity.getY() + entity.getHeight(), z - width));
        }};

        list.removeIf(vec -> distanceTo(vec) > dist);

        return !list.isEmpty();
    }

    public static String getBps() {
        return String.format("%.1f", getBPS());
    }

    public static double getBPS() {
        double tX = Math.abs(mc.player.getX() - mc.player.prevX);
        double tZ = Math.abs(mc.player.getZ() - mc.player.prevZ);
        double length = Math.sqrt(tX * tX + tZ * tZ);
        FunctionUtils.playerSpeed = length * 20;

        return length * 20;
    }

    public static double distanceToCamera(double x, double y, double z) {
        Camera camera = mc.gameRenderer.getCamera();
        return Math.sqrt(camera.getPos().squaredDistanceTo(x, y, z));
    }

    public static boolean isBot(PlayerEntity ent) {
        boolean flag = false;
        if (!mc.isInSingleplayer() && mc.getNetworkHandler() != null && !mc.getNetworkHandler().getPlayerList().isEmpty()) {
            flag = (new ArrayList<>(mc.getNetworkHandler().getPlayerList()).get(0).getGameMode().getName().equals(ent.getEntityName()));
        }
        return flag || !ent.getUuid().equals(UUID.nameUUIDFromBytes(("OfflinePlayer:" + ent.getName().getString()).getBytes(StandardCharsets.UTF_8))) && ent instanceof OtherClientPlayerEntity || EntityUtils.getGameMode(ent) == null;
    }

    public static float getMaxHealth(PlayerEntity entity) {
        return 20;
    }

    public static float getHealth(LivingEntity livingEntity) {
        if (livingEntity instanceof PlayerEntity player) {
            if (player == mc.player) return SelfUtils.getHealth();

            for (Map.Entry<ScoreboardObjective, ScoreboardPlayerScore> entry : mc.world.getScoreboard().getPlayerObjectives(player.getName().getString()).entrySet()) {
                ScoreboardPlayerScore score = entry.getValue();

                return score.getScore();
            }
        }

        return livingEntity.getHealth() + livingEntity.getAbsorptionAmount();
    }

    public static double getEyeY(PlayerEntity player) {
        return player.getY() + player.getEyeHeight(player.getPose());
    }

    public static boolean hasItem(PlayerEntity entity, Item items) {
        return entity.getMainHandStack().getItem().equals(items) || entity.getOffHandStack().getItem().equals(items);
    }

    public static boolean shouldPause(boolean ifBreaking, boolean ifEating, boolean ifDrinking) {
        if (ifBreaking && mc.interactionManager.isBreakingBlock()) return true;
        if (ifEating && (mc.player.isUsingItem() && (mc.player.getMainHandStack().getItem().isFood() || mc.player.getOffHandStack().getItem().isFood()))) return true;
        return ifDrinking && (mc.player.isUsingItem() && (mc.player.getMainHandStack().getItem() instanceof PotionItem || mc.player.getOffHandStack().getItem() instanceof PotionItem));
    }

    public static boolean hasElytra(PlayerEntity e) {
        if (e == null) return false;
        return e.inventory.getArmorStack(2).getItem().equals(Items.ELYTRA);
    }

    public static double distanceTo(Entity entity) {
        return distanceTo(entity.getX(), entity.getY(), entity.getZ());
    }

    public static double distanceTo(BlockPos blockPos) {
        return distanceTo(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static double distanceTo(Vec3d vec3d) {
        return distanceTo(vec3d.getX(), vec3d.getY(), vec3d.getZ());
    }

    public static double distanceTo(double x, double y, double z) {
        float f = (float) (mc.player.getX() - x);
        float g = (float) (mc.player.getY() - y);
        float h = (float) (mc.player.getZ() - z);
        return MathHelper.sqrt(f * f + g * g + h * h);
    }

    public static float getAttackCooldownProgressPerTick() {
        return (float) (1.0 / mc.player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED) * (20.0 * Timer.override));
    }

    public static float getAttackCooldown() {
        return MathHelper.clamp(((float) ((LivingEntityAccessor) mc.player).getLastAttackedTicks() + 0.5f) / getAttackCooldownProgressPerTick(), 0.0F, 1.0F);
    }
}
