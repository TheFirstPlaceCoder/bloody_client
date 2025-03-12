package com.client.impl.function.combat;

import com.client.event.events.EntityEvent;
import com.client.event.events.PlaceBlockEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.IntegerSetting;
import com.client.utils.game.entity.PlayerUtils;
import com.client.utils.game.inventory.FindItemResult;
import com.client.utils.game.inventory.InvUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class AutoExplosion extends Function {
    private final IntegerSetting delay = Integer().name("Задержка").enName("Delay").defaultValue(1).min(1).max(5).build();

    public AutoExplosion() {
        super("Auto Explosion", Category.COMBAT);
    }

    private BlockPos last;
    private int id;
    private int time;

    @Override
    public void tick(TickEvent.Pre event) {
        if (time > 0) time--;
        else {
            if (id != -1 && mc.world.getEntityById(id) != null) {
                Entity entity = mc.world.getEntityById(id);
                if (!PlayerUtils.isInRange(entity, mc.interactionManager.getReachDistance())) return;
                mc.interactionManager.attackEntity(mc.player, entity);
                mc.player.swingHand(Hand.MAIN_HAND);
                id = -1;
            }
        }
    }

    @Override
    public void placeBlock(PlaceBlockEvent.Post event) {
        last = event.pos;
        place(event.pos, event.hit, event.direction);
    }

    @Override
    public void addEntity(EntityEvent.Add event) {
        if (event.entity instanceof EndCrystalEntity) {
            if (event.entity.getBlockPos().down().equals(last)) {
                id = event.entity.getEntityId();
                time = delay.get();
            }
        }
    }

    private void place(BlockPos pos, Vec3d hit, Direction dir) {
        if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.OBSIDIAN)) return;
        FindItemResult crystal = InvUtils.findInHotbar(Items.END_CRYSTAL);
        if (!crystal.found()) return;
        InvUtils.swap(crystal);
        mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(hit, dir, pos, false));
        mc.player.swingHand(Hand.MAIN_HAND);
        InvUtils.swapBack();
    }
}
