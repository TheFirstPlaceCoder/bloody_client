package com.client.impl.function.misc.nuker;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public record BlockData(BlockPos bp, Vec3d vec3d, Direction dir) {
}