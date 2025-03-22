package com.client.utils.game.entity;

import com.client.BloodyClient;
import com.client.impl.function.combat.AntiBot;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.RaycastContext;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;

import static com.client.BloodyClient.mc;


public class EntityUtils {
    public static boolean canSee(Vec3d vec) {
        if (!BloodyClient.canUpdate()) return false;
        Vec3d vec1 = new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getStandingEyeHeight(), mc.player.getZ());
        return mc.world.raycast(new RaycastContext(vec1, vec, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player)).getType() == HitResult.Type.MISS;
    }

    public static boolean canSee(Entity entity) {
        return canSee(entity, true);
    }

    public static boolean canSee(Entity entity, boolean eyes) {
        Vec3d vec1 = new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getStandingEyeHeight(), mc.player.getZ());
        Vec3d vec2 = new Vec3d(entity.getX(), entity.getY(), entity.getZ());

        boolean canSeeFeet = mc.world.raycast(new RaycastContext(vec1, vec2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player)).getType() == HitResult.Type.MISS;

        vec2 = new Vec3d(entity.getX(), entity.getY() + entity.getStandingEyeHeight(), entity.getZ());
        boolean canSeeEyes = mc.world.raycast(new RaycastContext(vec1, vec2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player)).getType() == HitResult.Type.MISS;

        return canSeeFeet || (eyes && canSeeEyes);
    }

    public static boolean isBot(PlayerEntity ent) {
        boolean flag = false;
        if (!mc.isInSingleplayer() && mc.getNetworkHandler() != null && !mc.getNetworkHandler().getPlayerList().isEmpty()) {
            flag = (new ArrayList<>(mc.getNetworkHandler().getPlayerList()).get(0).getGameMode().getName().equals(ent.getEntityName()));
        }

        int health = Math.round(PlayerUtils.getHealth((LivingEntity) ent));
        double healthPercentage = health / PlayerUtils.getMaxHealth(ent);

        return flag || healthPercentage == 0 || !ent.getUuid().equals(PlayerEntity.getOfflinePlayerUuid(ent.getName().getString())) || !ent.getUuid().equals(UUID.nameUUIDFromBytes(("OfflinePlayer:" + ent.getName().getString()).getBytes(StandardCharsets.UTF_8))) && ent instanceof OtherClientPlayerEntity || getGameMode(ent) == null;
    }

    public static float getTotalHealth() {
        return getTotalHealth(mc.player);
    }

    public static float getTotalHealth(LivingEntity target) {
        return target.getHealth() + target.getAbsorptionAmount();
    }

    public static float getTotalHealth(PlayerEntity target) {
        return target.getHealth() + target.getAbsorptionAmount();
    }

    public static int getPing(PlayerEntity player) {
        if (mc.getNetworkHandler() == null) return 0;

        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
        if (playerListEntry == null) return 0;
        return playerListEntry.getLatency();
    }

    public static GameMode getGameMode(PlayerEntity player) {
        if (player == null) return GameMode.SURVIVAL;
        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
        if (playerListEntry == null) return GameMode.SURVIVAL;
        return playerListEntry.getGameMode();
    }

    public static String getName(Entity entity) {
        if (entity == null) return null;
        if (entity instanceof PlayerEntity) return entity.getEntityName();
        return entity.getType().getName().getString();
    }

    public static boolean isInRenderDistance(Entity entity) {
        if (entity == null) return false;
        return isInRenderDistance(entity.getX(), entity.getZ());
    }

    public static boolean isInRenderDistance(BlockEntity entity) {
        if (entity == null) return false;
        return isInRenderDistance(entity.getPos().getX(), entity.getPos().getZ());
    }

    public static boolean isInRenderDistance(BlockPos pos) {
        if (pos == null) return false;
        return isInRenderDistance(pos.getX(), pos.getZ());
    }

    public static boolean isInRenderDistance(double posX, double posZ) {
        double x = Math.abs(mc.gameRenderer.getCamera().getPos().x - posX);
        double z = Math.abs(mc.gameRenderer.getCamera().getPos().z - posZ);
        double d = (mc.options.viewDistance + 1) * 16;

        return x < d && z < d;
    }
}