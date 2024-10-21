package com.client.interfaces;

import com.client.utils.math.vector.Vec4;

public interface IMatrix4f {
    void multiplyMatrix(Vec4 v, Vec4 out);
}