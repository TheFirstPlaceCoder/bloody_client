package com.client.utils.math.vector.floats;

import com.client.utils.math.vector.doubles.V2D;
import com.client.utils.math.vector.integers.V2I;
import com.client.utils.math.vector.longs.V2L;

public class V2F {
    public float a, b;

    public V2F(float a, float b) {
        this.a = a;
        this.b = b;
    }

    public V2F(V2I v2I) {
        this.a = v2I.a;
        this.b = v2I.b;
    }

    public V2F(V2D v2D) {
        this.a = (float) v2D.a;
        this.b = (float) v2D.b;
    }

    public V2F(V2L v2L) {
        this.a = v2L.a;
        this.b = v2L.b;
    }

    public float getA() {
        return a;
    }

    public float getB() {
        return b;
    }

    public void setA(float a) {
        this.a = a;
    }

    public void setB(float b) {
        this.b = b;
    }
}
