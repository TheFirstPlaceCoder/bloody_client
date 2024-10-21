package com.client.utils.math.vector.longs;

import com.client.utils.math.vector.doubles.V3D;
import com.client.utils.math.vector.floats.V3F;
import com.client.utils.math.vector.integers.V3I;

public class V3L {
    public long x, y, z;

    public V3L(long x, long y, long z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public V3L(V3I v3I) {
        this.x = v3I.x;
        this.y = v3I.y;
        this.z = v3I.z;
    }

    public V3L(V3F v3F) {
        this.x = (long) v3F.x;
        this.y = (long) v3F.y;
        this.z = (long) v3F.z;
    }

    public V3L(V3D v3D) {
        this.x = (long) v3D.x;
        this.y = (long) v3D.y;
        this.z = (long) v3D.z;
    }

    public long getX() {
        return x;
    }

    public long getY() {
        return y;
    }

    public long getZ() {
        return z;
    }

    public void setX(long x) {
        this.x = x;
    }

    public void setY(long y) {
        this.y = y;
    }

    public void setZ(long z) {
        this.z = z;
    }
}
