package com.client.utils.optimization;

import com.client.utils.optimization.interfaces.DataProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

public class Provider implements DataProvider {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private ClientWorld world = null;

    public boolean prepareChunk(int chunkX, int chunkZ) {
        this.world = this.client.world;
        return this.world != null;
    }

    public boolean isOpaqueFullCube(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        return this.world.getBlockState(pos).isOpaqueFullCube(this.world, pos);
    }

    public void cleanup() {
        this.world = null;
    }
}