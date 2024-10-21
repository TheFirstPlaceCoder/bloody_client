package com.client.utils.optimization;

import com.client.utils.optimization.interfaces.DataProvider;
import com.client.utils.optimization.interfaces.OcclusionCache;

import java.util.BitSet;

public class OcclusionCullingInstance {
    private final int reach;
    private final double aabbExpansion;
    private final DataProvider provider;
    private final OcclusionCache cache;
    private final BitSet skipList;
    private final boolean[] onFaceEdge;
    private final Vector[] targetPoints;
    private final Vector targetPos;
    private final int[] cameraPos;

    public OcclusionCullingInstance(int maxDistance, DataProvider provider) {
        this(maxDistance, provider, new ArrayOcclusionCache(maxDistance), 0.5D);
    }

    public OcclusionCullingInstance(int maxDistance, DataProvider provider, OcclusionCache cache, double aabbExpansion) {
        this.skipList = new BitSet();
        this.onFaceEdge = new boolean[6];
        this.targetPoints = new Vector[8];
        this.targetPos = new Vector(0.0D, 0.0D, 0.0D);
        this.cameraPos = new int[3];
        this.reach = maxDistance;
        this.provider = provider;
        this.cache = cache;
        this.aabbExpansion = aabbExpansion;

        for(int i = 0; i < this.targetPoints.length; ++i) {
            this.targetPoints[i] = new Vector(0.0D, 0.0D, 0.0D);
        }

    }

    public boolean isAABBVisible(Vector aabbMin, Vector aabbMax, Vector viewerPosition) {
        try {
            int maxX = MathUtilities.floor(aabbMax.x + this.aabbExpansion);
            int maxY = MathUtilities.floor(aabbMax.y + this.aabbExpansion);
            int maxZ = MathUtilities.floor(aabbMax.z + this.aabbExpansion);
            int minX = MathUtilities.floor(aabbMin.x - this.aabbExpansion);
            int minY = MathUtilities.floor(aabbMin.y - this.aabbExpansion);
            int minZ = MathUtilities.floor(aabbMin.z - this.aabbExpansion);
            this.cameraPos[0] = MathUtilities.floor(viewerPosition.x);
            this.cameraPos[1] = MathUtilities.floor(viewerPosition.y);
            this.cameraPos[2] = MathUtilities.floor(viewerPosition.z);
            OcclusionCullingInstance.Relative relX = OcclusionCullingInstance.Relative.from(minX, maxX, this.cameraPos[0]);
            OcclusionCullingInstance.Relative relY = OcclusionCullingInstance.Relative.from(minY, maxY, this.cameraPos[1]);
            OcclusionCullingInstance.Relative relZ = OcclusionCullingInstance.Relative.from(minZ, maxZ, this.cameraPos[2]);
            if (relX == OcclusionCullingInstance.Relative.INSIDE && relY == OcclusionCullingInstance.Relative.INSIDE && relZ == OcclusionCullingInstance.Relative.INSIDE) {
                return true;
            } else {
                this.skipList.clear();
                int id = 0;

                int x;
                int y;
                int z;
                for(x = minX; x <= maxX; ++x) {
                    for(y = minY; y <= maxY; ++y) {
                        for(z = minZ; z <= maxZ; ++z) {
                            int cachedValue = this.getCacheValue(x, y, z);
                            if (cachedValue == 1) {
                                return true;
                            }

                            if (cachedValue != 0) {
                                this.skipList.set(id);
                            }

                            ++id;
                        }
                    }
                }

                id = 0;

                for(x = minX; x <= maxX; ++x) {
                    this.onFaceEdge[0] = x == minX;
                    this.onFaceEdge[1] = x == maxX;

                    for(y = minY; y <= maxY; ++y) {
                        this.onFaceEdge[2] = y == minY;
                        this.onFaceEdge[3] = y == maxY;

                        for(z = minZ; z <= maxZ; ++z) {
                            this.onFaceEdge[4] = z == minZ;
                            this.onFaceEdge[5] = z == maxZ;
                            if (this.skipList.get(id)) {
                                ++id;
                            } else {
                                if (this.onFaceEdge[0] && relX == OcclusionCullingInstance.Relative.POSITIVE || this.onFaceEdge[1] && relX == OcclusionCullingInstance.Relative.NEGATIVE || this.onFaceEdge[2] && relY == OcclusionCullingInstance.Relative.POSITIVE || this.onFaceEdge[3] && relY == OcclusionCullingInstance.Relative.NEGATIVE || this.onFaceEdge[4] && relZ == OcclusionCullingInstance.Relative.POSITIVE || this.onFaceEdge[5] && relZ == OcclusionCullingInstance.Relative.NEGATIVE) {
                                    this.targetPos.set((double)x, (double)y, (double)z);
                                    if (this.isVoxelVisible(viewerPosition, this.targetPos, this.onFaceEdge)) {
                                        return true;
                                    }
                                }

                                ++id;
                            }
                        }
                    }
                }

                return false;
            }
        } catch (Throwable var18) {
            var18.printStackTrace();
            return true;
        }
    }

    private boolean isVoxelVisible(Vector viewerPosition, Vector position, boolean[] faceEdgeData) {
        int targetSize = 0;
        if (faceEdgeData[0] || faceEdgeData[4] || faceEdgeData[2]) {
            this.targetPoints[targetSize++].setAdd(position, 0.05D, 0.05D, 0.05D);
        }

        if (faceEdgeData[1]) {
            this.targetPoints[targetSize++].setAdd(position, 0.95D, 0.05D, 0.05D);
        }

        if (faceEdgeData[3]) {
            this.targetPoints[targetSize++].setAdd(position, 0.05D, 0.95D, 0.05D);
        }

        if (faceEdgeData[5]) {
            this.targetPoints[targetSize++].setAdd(position, 0.05D, 0.05D, 0.95D);
        }

        if (faceEdgeData[4] && faceEdgeData[1] && faceEdgeData[3] || faceEdgeData[1] && faceEdgeData[3]) {
            this.targetPoints[targetSize++].setAdd(position, 0.95D, 0.95D, 0.05D);
        }

        if (faceEdgeData[0] && faceEdgeData[5] && faceEdgeData[3] || faceEdgeData[5] && faceEdgeData[3]) {
            this.targetPoints[targetSize++].setAdd(position, 0.05D, 0.95D, 0.95D);
        }

        if (faceEdgeData[5] && faceEdgeData[1]) {
            this.targetPoints[targetSize++].setAdd(position, 0.95D, 0.05D, 0.95D);
        }

        if (faceEdgeData[1] && faceEdgeData[3] && faceEdgeData[5]) {
            this.targetPoints[targetSize++].setAdd(position, 0.95D, 0.95D, 0.95D);
        }

        return this.isVisible(viewerPosition, this.targetPoints, targetSize);
    }

    private boolean isVisible(Vector start, Vector[] targets, int size) {
        int x = this.cameraPos[0];
        int y = this.cameraPos[1];
        int z = this.cameraPos[2];

        for(int v = 0; v < size; ++v) {
            Vector target = targets[v];
            double relativeX = start.x - target.getX();
            double relativeY = start.y - target.getY();
            double relativeZ = start.z - target.getZ();
            double dimensionX = Math.abs(relativeX);
            double dimensionY = Math.abs(relativeY);
            double dimensionZ = Math.abs(relativeZ);
            double dimFracX = 1.0D / dimensionX;
            double dimFracY = 1.0D / dimensionY;
            double dimFracZ = 1.0D / dimensionZ;
            int intersectCount = 1;
            byte x_inc;
            double t_next_x;
            if (dimensionX == 0.0D) {
                x_inc = 0;
                t_next_x = dimFracX;
            } else if (target.x > start.x) {
                x_inc = 1;
                intersectCount += MathUtilities.floor(target.x) - x;
                t_next_x = (double)((float)(((double)(x + 1) - start.x) * dimFracX));
            } else {
                x_inc = -1;
                intersectCount += x - MathUtilities.floor(target.x);
                t_next_x = (double)((float)((start.x - (double)x) * dimFracX));
            }

            byte y_inc;
            double t_next_y;
            if (dimensionY == 0.0D) {
                y_inc = 0;
                t_next_y = dimFracY;
            } else if (target.y > start.y) {
                y_inc = 1;
                intersectCount += MathUtilities.floor(target.y) - y;
                t_next_y = (double)((float)(((double)(y + 1) - start.y) * dimFracY));
            } else {
                y_inc = -1;
                intersectCount += y - MathUtilities.floor(target.y);
                t_next_y = (double)((float)((start.y - (double)y) * dimFracY));
            }

            byte z_inc;
            double t_next_z;
            if (dimensionZ == 0.0D) {
                z_inc = 0;
                t_next_z = dimFracZ;
            } else if (target.z > start.z) {
                z_inc = 1;
                intersectCount += MathUtilities.floor(target.z) - z;
                t_next_z = (double)((float)(((double)(z + 1) - start.z) * dimFracZ));
            } else {
                z_inc = -1;
                intersectCount += z - MathUtilities.floor(target.z);
                t_next_z = (double)((float)((start.z - (double)z) * dimFracZ));
            }

            boolean finished = this.stepRay(start, x, y, z, dimFracX, dimFracY, dimFracZ, intersectCount, x_inc, y_inc, z_inc, t_next_y, t_next_x, t_next_z);
            this.provider.cleanup();
            if (finished) {
                this.cacheResult(targets[0], true);
                return true;
            }
        }

        this.cacheResult(targets[0], false);
        return false;
    }

    private boolean stepRay(Vector start, int currentX, int currentY, int currentZ, double distInX, double distInY, double distInZ, int n, int x_inc, int y_inc, int z_inc, double t_next_y, double t_next_x, double t_next_z) {
        for(; n > 1; --n) {
            int cVal = this.getCacheValue(currentX, currentY, currentZ);
            if (cVal == 2) {
                return false;
            }

            if (cVal == 0) {
                int chunkX = currentX >> 4;
                int chunkZ = currentZ >> 4;
                if (!this.provider.prepareChunk(chunkX, chunkZ)) {
                    return false;
                }

                if (this.provider.isOpaqueFullCube(currentX, currentY, currentZ)) {
                    this.cache.setLastHidden();
                    return false;
                }

                this.cache.setLastVisible();
            }

            if (t_next_y < t_next_x && t_next_y < t_next_z) {
                currentY += y_inc;
                t_next_y += distInY;
            } else if (t_next_x < t_next_y && t_next_x < t_next_z) {
                currentX += x_inc;
                t_next_x += distInX;
            } else {
                currentZ += z_inc;
                t_next_z += distInZ;
            }
        }

        return true;
    }

    private int getCacheValue(int x, int y, int z) {
        x -= this.cameraPos[0];
        y -= this.cameraPos[1];
        z -= this.cameraPos[2];
        return Math.abs(x) <= this.reach - 2 && Math.abs(y) <= this.reach - 2 && Math.abs(z) <= this.reach - 2 ? this.cache.getState(x + this.reach, y + this.reach, z + this.reach) : -1;
    }

    private void cacheResult(int x, int y, int z, boolean result) {
        int cx = x - this.cameraPos[0] + this.reach;
        int cy = y - this.cameraPos[1] + this.reach;
        int cz = z - this.cameraPos[2] + this.reach;
        if (result) {
            this.cache.setVisible(cx, cy, cz);
        } else {
            this.cache.setHidden(cx, cy, cz);
        }

    }

    private void cacheResult(Vector vector, boolean result) {
        int cx = MathUtilities.floor(vector.x) - this.cameraPos[0] + this.reach;
        int cy = MathUtilities.floor(vector.y) - this.cameraPos[1] + this.reach;
        int cz = MathUtilities.floor(vector.z) - this.cameraPos[2] + this.reach;
        if (result) {
            this.cache.setVisible(cx, cy, cz);
        } else {
            this.cache.setHidden(cx, cy, cz);
        }

    }

    public void resetCache() {
        this.cache.resetCache();
    }

    private static enum Relative {
        INSIDE,
        POSITIVE,
        NEGATIVE;

        public static OcclusionCullingInstance.Relative from(int min, int max, int pos) {
            if (max > pos && min > pos) {
                return POSITIVE;
            } else {
                return min < pos && max < pos ? NEGATIVE : INSIDE;
            }
        }
    }
}