package com.client.utils.optimization;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import com.client.impl.function.client.Optimization;
import com.client.system.function.FunctionManager;
import com.client.utils.optimization.interfaces.Cullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.client.MinecraftClient;

public class CullTask implements Runnable {
    public boolean requestCull = false;
    private final OcclusionCullingInstance culling;
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final int hitboxLimit;
    private final Set<BlockEntityType<?>> blockEntityWhitelist;
    private final Set<EntityType<?>> entityWhistelist;
    public long lastTime;
    private Vector lastPos;
    private Vector aabbMin;
    private Vector aabbMax;

    public CullTask(OcclusionCullingInstance culling, Set<BlockEntityType<?>> blockEntityWhitelist, Set<EntityType<?>> entityWhistelist) {
        this.hitboxLimit = 50;
        this.lastTime = 0L;
        this.lastPos = new Vector(0.0D, 0.0D, 0.0D);
        this.aabbMin = new Vector(0.0D, 0.0D, 0.0D);
        this.aabbMax = new Vector(0.0D, 0.0D, 0.0D);
        this.culling = culling;
        this.blockEntityWhitelist = blockEntityWhitelist;
        this.entityWhistelist = entityWhistelist;
    }

    public void run() {
        while(this.client.isRunning()) {
            try {
                Thread.sleep(ConfigVariables.sleepDelay);
                if (FunctionManager.get(Optimization.class).isEnabled() && FunctionManager.get(Optimization.class).rayTrace.get() && this.client.world != null && this.client.player != null && this.client.player.age > 10) {
                    Vec3d cameraMC = this.client.gameRenderer.getCamera().getPos();
                    if (this.requestCull || cameraMC.x != this.lastPos.x || cameraMC.y != this.lastPos.y || cameraMC.z != this.lastPos.z) {
                        long start = System.currentTimeMillis();
                        this.requestCull = false;
                        this.lastPos.set(cameraMC.x, cameraMC.y, cameraMC.z);
                        Vector camera = this.lastPos;
                        this.culling.resetCache();
                        boolean spectator = this.client.player.isSpectator();

                        for(int x = -8; x <= 8; ++x) {
                            label163:
                            for(int z = -8; z <= 8; ++z) {
                                WorldChunk chunk = this.client.world.getChunk(this.client.player.chunkX + x, this.client.player.chunkZ + z);
                                Iterator iterator = chunk.getBlockEntities().entrySet().iterator();

                                while(true) {
                                    while(true) {
                                        Entry entry;
                                        Cullable cullable;
                                        do {
                                            do {
                                                do {
                                                    if (!iterator.hasNext()) {
                                                        continue label163;
                                                    }

                                                    try {
                                                        entry = (Entry)iterator.next();
                                                    } catch (ConcurrentModificationException | NullPointerException var16) {
                                                        continue label163;
                                                    }
                                                } while(this.blockEntityWhitelist.contains(((BlockEntity)entry.getValue()).getType()));
                                            } while(EntityCullingBase.instance.isDynamicWhitelisted((BlockEntity)entry.getValue()));

                                            cullable = (Cullable)entry.getValue();
                                        } while(cullable.isForcedVisible());

                                        if (spectator) {
                                            cullable.setCulled(false);
                                        } else {
                                            BlockPos pos = (BlockPos)entry.getKey();
                                            if (pos.isWithinDistance(cameraMC, 64.0D)) {
                                                Box boundingBox = EntityCullingBase.instance.setupAABB((BlockEntity)entry.getValue(), pos);
                                                if (boundingBox.getXLength() <= (double)this.hitboxLimit && boundingBox.getYLength() <= (double)this.hitboxLimit && boundingBox.getZLength() <= (double)this.hitboxLimit) {
                                                    this.aabbMin.set(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
                                                    this.aabbMax.set(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
                                                    boolean visible = this.culling.isAABBVisible(this.aabbMin, this.aabbMax, camera);
                                                    cullable.setCulled(!visible);
                                                } else {
                                                    cullable.setCulled(false);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Entity entity = null;
                        Iterator iterable = this.client.world.getEntities().iterator();

                        while(iterable.hasNext()) {
                            try {
                                entity = (Entity)iterable.next();
                            } catch (ConcurrentModificationException | NullPointerException var15) {
                                break;
                            }

                            if (entity != null && entity instanceof Cullable && !this.entityWhistelist.contains(entity.getType()) && !EntityCullingBase.instance.isDynamicWhitelisted(entity)) {
                                Cullable cullable = (Cullable)entity;
                                if (!cullable.isForcedVisible()) {
                                    if (!spectator && !entity.isGlowing() && !this.isSkippableArmorstand(entity)) {
                                        if (!entity.getPos().isInRange(cameraMC, (double)ConfigVariables.tracingDistance)) {
                                            cullable.setCulled(false);
                                        } else {
                                            Box boundingBox = entity.getVisibilityBoundingBox();
                                            if (boundingBox.getXLength() <= (double)this.hitboxLimit && boundingBox.getYLength() <= (double)this.hitboxLimit && boundingBox.getZLength() <= (double)this.hitboxLimit) {
                                                this.aabbMin.set(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
                                                this.aabbMax.set(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
                                                boolean visible = this.culling.isAABBVisible(this.aabbMin, this.aabbMax, camera);
                                                cullable.setCulled(!visible);
                                            } else {
                                                cullable.setCulled(false);
                                            }
                                        }
                                    } else {
                                        cullable.setCulled(false);
                                    }
                                }
                            }
                        }

                        this.lastTime = System.currentTimeMillis() - start;
                    }
                }
            } catch (Exception var17) {
                var17.printStackTrace();
            }
        }

        System.out.println("Shutting down culling task!");
    }

    private boolean isSkippableArmorstand(Entity entity) {
        if (!ConfigVariables.skipMarkerArmorStands) {
            return false;
        } else {
            return entity instanceof ArmorStandEntity && ((ArmorStandEntity)entity).isMarker();
        }
    }
}