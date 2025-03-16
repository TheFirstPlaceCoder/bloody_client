package com.client.impl.function.movement.speedmodes.grim;

import com.client.event.events.PlayerTravelEvent;
import com.client.impl.function.combat.aura.rotate.RotationHandler;
import com.client.impl.function.movement.speedmodes.SpeedMode;
import com.client.system.companion.DumboOctopusEntity;
import com.client.utils.game.movement.MovementUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;

public class GrimCollide extends SpeedMode {
    @Override
    public void onTravel(PlayerTravelEvent e) {
        if (!e.pre && MovementUtils.isMoving()) {
            int collisions = 0;
            int otherCollisions = 0;
            for (Entity ent : mc.world.getEntities())
                if (ent != mc.player && !(ent instanceof DumboOctopusEntity) && (ent instanceof PlayerEntity || (ent instanceof LivingEntity && !(ent instanceof ArmorStandEntity) && settings.others.get()) || (ent instanceof ArmorStandEntity && settings.armorStands.get())) && mc.player.getBoundingBox().expand(settings.expand.get()).intersects(ent.getBoundingBox())) {
                    if (ent instanceof PlayerEntity) collisions++;
                    else otherCollisions++;
                }

            double[] motion = MovementUtils.forward(RotationHandler.serverYaw,(collisions > 0 ? settings.speed.get() / 100d : settings.speedAnimal.get() / 100d) * (collisions + otherCollisions));
            mc.player.addVelocity(motion[0], 0.0, motion[1]);
        }
    }
}
