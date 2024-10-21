package com.client.system.setting.settings;

import com.client.system.function.Function;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.api.IVisible;
import com.client.system.setting.api.SettingsType;
import com.client.system.setting.manager.SettingManager;
import com.client.utils.auth.Loader;

import java.util.function.Consumer;

public class DoubleSetting extends AbstractSettings<Double> {
    private double min, max;
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
        if (this.isPremium && !Loader.isPremium()) this.value = getDefaultValue();
        else this.value = value;
        onChanged();
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
