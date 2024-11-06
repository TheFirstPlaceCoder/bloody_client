package com.client.system.companion;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;

public class CompanionRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES;
    public static final RegistryObject<EntityType<DumboOctopusEntity>> DUMBO_OCTOPUS;

    private static <E extends Entity> EntityType<E> create(EntityType.EntityFactory<E> entityFactory, SpawnGroup category, float width, float height) {
        return FabricEntityTypeBuilder.create(category, entityFactory).dimensions(EntityDimensions.changing(width, height)).build();
    }

    public static void onAttributeCreation() {
        FabricDefaultAttributeRegistry.register(DUMBO_OCTOPUS.get(), DumboOctopusEntity.createAttributes());
    }

    public static void registerEntityRenderers() {
        EntityRendererRegistry.INSTANCE.register(DUMBO_OCTOPUS.get(), (dispatcher, factory) -> new DumboOctopusRenderer(dispatcher));
    }

    static {
        ENTITIES = DeferredRegister.create(Registry.ENTITY_TYPE, "minecraft");
        DUMBO_OCTOPUS = ENTITIES.register("dumbo_octopus", () -> {
            return create(DumboOctopusEntity::new, SpawnGroup.CREATURE, 0.4F, 0.4F);
        });
    }
}
