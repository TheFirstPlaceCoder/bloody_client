package com.client.impl.function.visual;

import com.client.event.events.Render2DEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.ColorSetting;
import com.client.utils.render.DrawMode;
import com.client.utils.render.Renderer2D;
import net.minecraft.client.render.VertexFormats;

import java.awt.*;

public class Hue extends Function {
    public final ColorSetting color = Color().name("Цвет").defaultValue(new Color(255, 255, 255, 40)).build();

    public Hue() {
        super("HUE", Category.VISUAL);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        Renderer2D.COLOR.begin(DrawMode.Triangles, VertexFormats.POSITION_COLOR);
        Renderer2D.COLOR.quad(0, 0, mc.getWindow().getWidth(), mc.getWindow().getHeight(), color.get());
        Renderer2D.COLOR.end();
    }
}
