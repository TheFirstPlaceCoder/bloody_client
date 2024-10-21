package com.client.system.gps;

import com.client.impl.function.client.GPS;
import com.client.system.function.FunctionManager;
import com.client.utils.render.DrawMode;
import com.client.utils.render.MeshBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Map;

public class GpsPoint {
    private static final MeshBuilder MB;

    static {
        MB = new MeshBuilder(128);
        MB.texture = true;
    }

    public String icon = "gps";

    public int x, y, z;
    public String name;

    public GpsPoint(int x, int y, int z, String name) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
    }

    public BlockPos get() {
        return new BlockPos(x, y, z);
    }

    public void validateIcon() {
        Map<String, AbstractTexture> icons = GpsManager.icons;

        AbstractTexture texture = icons.get(icon);
        if (texture == null && !icons.isEmpty()) {
            icon = icons.keySet().iterator().next();
        }
    }

    public void renderIcon(double x, double y, double z, double a, double size) {
        validateIcon();

        AbstractTexture texture = GpsManager.icons.get(icon);
        if (texture == null) return;

        MB.begin(DrawMode.Triangles, VertexFormats.POSITION_TEXTURE_COLOR);

        Color color = FunctionManager.get(GPS.class).color.get();

        MB.pos(x, y, z).texture(0, 0).color(color).endVertex();
        MB.pos(x + size, y, z).texture(1, 0).color(color).endVertex();
        MB.pos(x + size, y + size, z).texture(1, 1).color(color).endVertex();

        MB.pos(x, y, z).texture(0, 0).color(color).endVertex();
        MB.pos(x + size, y + size, z).texture(1, 1).color(color).endVertex();
        MB.pos(x, y + size, z).texture(0, 1).color(color).endVertex();

        texture.bindTexture();
        MB.end();
    }

    private int findIconIndex() {
        int i = 0;
        for (String icon : GpsManager.icons.keySet()) {
            if (this.icon.equals(icon)) return i;
            i++;
        }

        return -1;
    }

    private int correctIconIndex(int i) {
        if (i < 0) return GpsManager.icons.size() + i;
        else if (i >= GpsManager.icons.size()) return i - GpsManager.icons.size();
        return i;
    }

    private String getIcon(int i) {
        i = correctIconIndex(i);

        int _i = 0;
        for (String icon : GpsManager.icons.keySet()) {
            if (_i == i) return icon;
            _i++;
        }

        return "Square";
    }

    public void prevIcon() {
        icon = getIcon(findIconIndex() - 1);
    }

    public void nextIcon() {
        icon = getIcon(findIconIndex() + 1);
    }
}