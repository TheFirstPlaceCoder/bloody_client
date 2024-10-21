package com.client.utils.optimization.interfaces;

public interface Cullable {
    void setTimeout();

    boolean isForcedVisible();

    void setCulled(boolean var1);

    boolean isCulled();

    void setOutOfCamera(boolean var1);

    boolean isOutOfCamera();
}