package com.client.impl.function.client;

import com.client.event.events.Render2DEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.hud.KeybindsHud;
import com.client.impl.function.hud.ModulesHud;
import com.client.impl.function.hud.PotionsHud;
import com.client.impl.hud.*;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.hud.HudManager;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.textures.DownloadImage;
import com.client.utils.render.DrawMode;
import com.client.utils.render.MeshBuilder;
import com.client.utils.render.wisetree.render.render2d.main.TextureGL;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.VertexFormats;

import java.awt.*;

public class Hud extends Function {
    public final BooleanSetting glow = Boolean().name("Свечение").enName("Glow").defaultValue(true).build();
    public final BooleanSetting blur = Boolean().name("Блюр").enName("Blur").defaultValue(true).build();
    public final BooleanSetting hotbar = Boolean().name("Хотбар").enName("Hotbar").defaultValue(true).build();

    @Override
    public void tick(TickEvent.Pre event) {
        HudManager.get(WatermarkHud.class).setEnabled(FunctionManager.get(com.client.impl.function.hud.WatermarkHud.class).isEnabled());
        HudManager.get(PotionHud.class).setEnabled(FunctionManager.get(PotionsHud.class).isEnabled());
        HudManager.get(KeybindHud.class).setEnabled(FunctionManager.get(KeybindsHud.class).isEnabled());
        HudManager.get(StaffHud.class).setEnabled(FunctionManager.get(com.client.impl.function.hud.StaffHud.class).isEnabled());
        HudManager.get(ArmorHud.class).setEnabled(FunctionManager.get(com.client.impl.function.hud.ArmorHud.class).isEnabled());
        HudManager.get(SpeedHud.class).setEnabled(FunctionManager.get(com.client.impl.function.hud.InfoHud.class).getSpeed());
        HudManager.get(PingHud.class).setEnabled(FunctionManager.get(com.client.impl.function.hud.InfoHud.class).getPing());
        HudManager.get(CoordsHud.class).setEnabled(FunctionManager.get(com.client.impl.function.hud.InfoHud.class).getCoords());
        HudManager.get(TpsHud.class).setEnabled(FunctionManager.get(com.client.impl.function.hud.InfoHud.class).getTps());
        HudManager.get(FunctionListHud.class).setEnabled(FunctionManager.get(ModulesHud.class).isEnabled());
        HudManager.get(TargetHud.class).setEnabled(FunctionManager.get(com.client.impl.function.hud.TargetHud.class).isEnabled());
        HudManager.get(MusicHud.class).setEnabled(FunctionManager.get(com.client.impl.function.hud.MusicHud.class).isEnabled());
    }

    public Hud() {
        super("Hud", Category.CLIENT);
    }

    public boolean drawHotbar() {
        return hotbar.get() && isEnabled();
    }
}
