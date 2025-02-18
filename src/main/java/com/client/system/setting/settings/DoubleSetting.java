package com.client.system.setting.settings;

import com.client.impl.function.client.ClickGui;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.api.IVisible;
import com.client.system.setting.api.SettingsType;
import com.client.system.setting.manager.SettingManager;
import com.client.utils.auth.Loader;
import com.client.utils.files.SoundManager;
import com.client.utils.misc.CustomSoundInstance;
import net.minecraft.sound.SoundCategory;

import java.util.Objects;
import java.util.function.Consumer;

import static com.client.BloodyClient.mc;

public class DoubleSetting extends AbstractSettings<Double> {
    private double min, max;
    private int c = 1;
    private Consumer<Double> onChanged;

    public DoubleSetting(Function function) {
        super(SettingsType.Double, function);
        onChanged = null;
    }

    public Double get() {
        return Math.round(value * 100.0) / 100.0;
    }

    public float floatValue() {
        return (float) (Math.round(value * 100.0) / 100.0);
    }

    public void set(Double value) {
        boolean shouldSound = !Objects.equals(value, this.value);

        if (this.isPremium && !Loader.isPremium()) this.value = getDefaultValue();
        else this.value = value;
        onChanged();

        if (shouldSound && mc.world != null && mc.player != null && FunctionManager.get(ClickGui.class).clientSound.get()) {
            CustomSoundInstance customSoundInstance = new CustomSoundInstance(SoundManager.BUTTON_EVENT, SoundCategory.MASTER);
            customSoundInstance.setVolume(FunctionManager.get(ClickGui.class).volume.floatValue());
            mc.getSoundManager().play(customSoundInstance);
        }
    }

    private void onChanged() {
        if (onChanged != null) onChanged.accept(value);
    }

    public DoubleSetting onChanged(Consumer<Double> value) {
        this.onChanged = value;
        return this;
    }

    public double getMax() {
        return Math.round(max * 100.0) / 100.0;
    }

    public DoubleSetting max(double max) {
        this.max = max;
        return this;
    }

    public double getMin() {
        return Math.round(min * 100.0) / 100.0;
    }

    public DoubleSetting min(double min) {
        this.min = min;
        return this;
    }

    public int getC() {
        return c;
    }

    public DoubleSetting c(int c) {
        this.c = c;
        return this;
    }

    public Double getDefaultValue() {
        return defaultValue;
    }

    public DoubleSetting defaultValue(Double value) {
        this.defaultValue = value;
        set(value);
        return this;
    }

    public String getName() {
        return name;
    }

    public DoubleSetting name(String name) {
        this.name = name;
        return this;
    }

    public String getEnName() {
        return enName;
    }

    public DoubleSetting enName(String name) {
        this.enName = name;
        return this;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public DoubleSetting setPremium(boolean premium) {
        this.isPremium = premium;
        return this;
    }

    public IVisible isVisible() {
        return visible;
    }

    public DoubleSetting visible(IVisible visible) {
        this.visible = visible;
        return this;
    }

    public SettingsType getType() {
        return type;
    }

    public DoubleSetting build() {
        SettingManager.register(this);
        return this;
    }

    public String toConfig() {
        return getName().concat(":").concat(String.valueOf(get()));
    }
}
