package com.client.system.companion;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.LivingEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderer.geo.GeoEntityRenderer;

public class RenderFixHelper<T extends LivingEntity & IAnimatable> extends GeoEntityRenderer<T> {
    private static final Logger LOGGER = LogManager.getLogger();

    public RenderFixHelper(EntityRenderDispatcher renderManager, AnimatedGeoModel<T> modelProvider) {
        super(renderManager, modelProvider);
    }
}