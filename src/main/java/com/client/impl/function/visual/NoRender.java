package com.client.impl.function.visual;

import com.client.event.events.*;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.misc.FunctionUtils;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;

import java.util.List;

/**
 * __aaa__
 * 22.05.2024
 * */
public class NoRender extends Function {
    public NoRender() {
        super("No Render", Category.VISUAL);
    }

    public final MultiBooleanSetting remove = MultiBoolean().name("Убирать").enName("Remove").defaultValue(List.of(
            new MultiBooleanValue(true, "Эффект тотема"),
            new MultiBooleanValue(true, "Эффект портала"),
            new MultiBooleanValue(true, "Эффект свечения"),
            new MultiBooleanValue(true, "Тряску камеры"),
            new MultiBooleanValue(true, "Невидимость"),
            new MultiBooleanValue(false, "Броня"),
            new MultiBooleanValue(false, "Скорборд"),
            new MultiBooleanValue(true, "Погода"),
            new MultiBooleanValue(true, "Взрыв кристалла"),
            new MultiBooleanValue(true, "Другие партиклы")
    )).build();

    private final MultiBooleanSetting overlays = MultiBoolean().name("Оверлеи").enName("Overlays").defaultValue(List.of(
            new MultiBooleanValue(false, "Название предмета"),
            new MultiBooleanValue(true, "Огонь"),
            new MultiBooleanValue(true, "Эффекты"),
            new MultiBooleanValue(true, "Тыква"),
            new MultiBooleanValue(true, "Блоки"),
            new MultiBooleanValue(true, "Виньетка"),
            new MultiBooleanValue(false, "Прицел"),
            new MultiBooleanValue(true, "Жидкости")
    )).build();

    private final MultiBooleanSetting effects = MultiBoolean().name("Эффекты").enName("Effects").defaultValue(List.of(
            new MultiBooleanValue(true, "Тошнота"),
            new MultiBooleanValue(true, "Слепота")
    )).build();

    @Override
    public void onEnable() {
        FunctionUtils.isRemovedArmor = remove.get("Броня");
    }

    @Override
    public void onDisable() {
        FunctionUtils.isRemovedArmor = false;
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (effects.get(0)) mc.player.removeStatusEffect(StatusEffects.NAUSEA);
        if (effects.get(1)) mc.player.removeStatusEffect(StatusEffects.BLINDNESS);
    }

    @Override
    public void onParticleRenderEvent(ParticleRenderEvent event) {
        if (remove.get("Взрыв кристалла") && (event.particle.getType().equals(ParticleTypes.EXPLOSION_EMITTER) || event.particle.getType().equals(ParticleTypes.EXPLOSION_EMITTER))) event.cancel();
        else if (remove.get("Другие партиклы") && !event.particle.getType().equals(ParticleTypes.EXPLOSION_EMITTER) && !event.particle.getType().equals(ParticleTypes.EXPLOSION_EMITTER)) event.cancel();
    }

    @Override
    public void onScoreboardRenderEvent(ScoreboardRenderEvent event) {
        if (remove.get("Скорборд")) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onFloatingItemRenderEvent(FloatingItemRenderEvent event) {
        if (event.stack.getItem() == Items.TOTEM_OF_UNDYING && remove.get("Эффект тотема")) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onHurtCamRenderEvent(HurtCamRenderEvent event) {
        if (remove.get("Тряску камеры")) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onArmorRenderEvent(ArmorRenderEvent event) {
        if (remove.get("Броня")) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onWeatherWorldRenderEvent(WeatherWorldRenderEvent event) {
        if (remove.get("Погода")) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onInvisibleEvent(InvisibleEvent event) {
        if (remove.get("Инвизы")) {
            event.cancel();
        }
    }

    @Override
    public void onGlintRenderEvent(GlintRenderEvent event) {
        if (remove.get("Эффект свечения")) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onRenderOverlayEvent(RenderOverlayEvent event) {
        switch (event.type.toString()) {
            case "FIRE" -> event.setCancelled(overlays.get("Огонь"));
            case "HELDITEMNAME" -> event.setCancelled(overlays.get("Название предмета"));
            case "PUMPKIN" -> event.setCancelled(overlays.get("Тыква"));
            case "EFFECTS" -> event.setCancelled(overlays.get("Эффекты"));
            case "VIGNETTE" -> event.setCancelled(overlays.get("Виньетка"));
            case "WATER" -> event.setCancelled(overlays.get("Жидкости"));
            case "BLOCK" -> event.setCancelled(overlays.get("Блоки"));
            case "CROSSHAIR" -> {
                if (overlays.get("Прицел")) event.cancel();
            }
            default -> event.setCancelled(remove.get("Эффект портала"));
        }
    }
}