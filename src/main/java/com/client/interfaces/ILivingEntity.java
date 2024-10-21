package com.client.interfaces;

import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public interface ILivingEntity {

    void setServerYaw(float yaw);
    void setServerHeadYaw(float yaw);
    void setServerPitch(float pitch);

    List<Pair<Long, Vec3d>> getMove();

}