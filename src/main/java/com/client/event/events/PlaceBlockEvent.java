package com.client.event.events;

import com.client.event.IEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class PlaceBlockEvent extends IEvent {
    public BlockPos pos;
    public Vec3d hit;
    public Direction direction;

    public PlaceBlockEvent(BlockPos pos, Vec3d hit, Direction direction) {
        this.pos = pos;
        this.hit = hit;
        this.direction = direction;
    }

    public static class Pre extends PlaceBlockEvent {

        public Pre(BlockPos pos, Vec3d hit, Direction direction) {
            super(pos, hit, direction);
        }
    }

    public static class Post extends PlaceBlockEvent {

        public Post(BlockPos pos, Vec3d hit, Direction direction) {
            super(pos, hit, direction);
        }
    }
}
