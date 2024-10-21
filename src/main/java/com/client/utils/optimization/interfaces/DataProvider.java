package com.client.utils.optimization.interfaces;

import com.client.utils.optimization.Vector;

public interface DataProvider {
    boolean prepareChunk(int var1, int var2);

    boolean isOpaqueFullCube(int var1, int var2, int var3);

    default void cleanup() {
    }

    default void checkingPosition(Vector[] targetPoints, int size, Vector viewerPosition) {
    }
}