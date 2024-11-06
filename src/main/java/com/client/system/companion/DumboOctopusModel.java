package com.client.system.companion;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class DumboOctopusModel extends AnimatedGeoModel<DumboOctopusEntity> {
    private static final Identifier MODEL = new Identifier("minecraft", "geo/dumbo_octopus.geo.json");
    private static final Identifier[] TEXTURES = new Identifier[] {
            new Identifier("minecraft", "textures/entity/dumbo_octopus_1.png"),
            new Identifier("minecraft", "textures/entity/dumbo_octopus_2.png"),
            new Identifier("minecraft", "textures/entity/dumbo_octopus_3.png"),
            new Identifier("minecraft", "textures/entity/dumbo_octopus_4.png")
    };
    private static final Identifier ANIMATION = new Identifier("minecraft", "animations/dumbo_octopus.animation.json");

    public Identifier getModelLocation(DumboOctopusEntity object) {
        return MODEL;
    }

    public Identifier getTextureLocation(DumboOctopusEntity object) {
        return TEXTURES[object.getVariant()];
    }

    public Identifier getAnimationFileLocation(DumboOctopusEntity animatable) {
        return ANIMATION;
    }
}