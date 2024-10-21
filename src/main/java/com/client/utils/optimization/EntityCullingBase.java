package com.client.utils.optimization;

import com.client.impl.function.client.Optimization;
import com.client.system.function.FunctionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.util.Formatting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.math.Box;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class EntityCullingBase {
    public static EntityCullingBase instance;
    public OcclusionCullingInstance culling;
    public Set<BlockEntityType<?>> blockEntityWhitelist = new HashSet();
    public Set<EntityType<?>> entityWhistelist = new HashSet();
    public CullTask cullTask;
    private Thread cullThread;
    private boolean configKeysLoaded = false;
    private Set<Function<BlockEntity, Boolean>> dynamicBlockEntityWhitelist = new HashSet();
    private Set<Function<Entity, Boolean>> dynamicEntityWhitelist = new HashSet();
    public int renderedBlockEntities = 0;
    public int skippedBlockEntities = 0;
    public int renderedEntities = 0;
    public int skippedEntities = 0;
    public int tickedEntities = 0;
    public int skippedEntityTicks = 0;

    public void onInitialize() {
        instance = this;

        this.culling = new OcclusionCullingInstance(ConfigVariables.tracingDistance, new Provider());
        this.cullTask = new CullTask(this.culling, this.blockEntityWhitelist, this.entityWhistelist);
        this.cullThread = new Thread(this.cullTask, "CullThread");
        this.cullThread.setUncaughtExceptionHandler((thread, ex) -> {
            System.out.println("The CullingThread has crashed! Please report the following stacktrace!");
            ex.printStackTrace();
        });
        this.cullThread.start();
        this.initModloader();
    }

    public void worldTick() {
        this.cullTask.requestCull = true;
    }

    public void clientTick() {
            if (!this.configKeysLoaded) {
                Iterator var1 = ConfigVariables.blockEntityWhitelist.iterator();

                String entityType;
                Optional entity;
                while (var1.hasNext()) {
                    entityType = (String) var1.next();
                    entity = Registry.BLOCK_ENTITY_TYPE.getOrEmpty(new Identifier(entityType));
                    entity.ifPresent((b) -> {
                        this.blockEntityWhitelist.add((BlockEntityType<?>) b);
                    });
                }

                var1 = ConfigVariables.tickCullingWhitelist.iterator();

                while (var1.hasNext()) {
                    entityType = (String) var1.next();
                    entity = Registry.ENTITY_TYPE.getOrEmpty(new Identifier(entityType));
                    entity.ifPresent((e) -> {
                        this.entityWhistelist.add((EntityType<?>) e);
                    });
                }

                var1 = ConfigVariables.entityWhitelist.iterator();

                while (var1.hasNext()) {
                    entityType = (String) var1.next();
                    entity = Registry.ENTITY_TYPE.getOrEmpty(new Identifier(entityType));
                    entity.ifPresent((e) -> {
                        this.entityWhistelist.add((EntityType<?>) e);
                    });
                }
            }

            this.cullTask.requestCull = true;
    }

    public void initModloader() {
        ClientTickEvents.START_WORLD_TICK.register((event) -> {
            if (!FunctionManager.get(Optimization.class).isEnabled() || !FunctionManager.get(Optimization.class).rayTrace.get()) return;
            this.worldTick();
        });
        ClientTickEvents.START_CLIENT_TICK.register((e) -> {
            if (!FunctionManager.get(Optimization.class).isEnabled() || !FunctionManager.get(Optimization.class).rayTrace.get()) return;
            this.clientTick();
        });
    }

    public Box setupAABB(BlockEntity entity, BlockPos pos) {
        return new Box(pos);
    }

    public boolean isDynamicWhitelisted(BlockEntity entity) {
        Iterator var2 = this.dynamicBlockEntityWhitelist.iterator();

        Function fun;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            fun = (Function)var2.next();
        } while(!(Boolean)fun.apply(entity));

        return true;
    }

    public boolean isDynamicWhitelisted(Entity entity) {
        Iterator var2 = this.dynamicEntityWhitelist.iterator();

        Function fun;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            fun = (Function)var2.next();
        } while(!(Boolean)fun.apply(entity));

        return true;
    }
}