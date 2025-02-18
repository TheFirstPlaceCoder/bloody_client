package com.client.system.setting.settings;

import com.client.system.config.ConfigSystem;
import com.client.system.function.Function;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.api.IVisible;
import com.client.system.setting.api.SettingsType;
import com.client.system.setting.manager.SettingManager;
import com.client.utils.auth.Loader;

import java.awt.*;
import java.util.function.Consumer;

public class ColorSetting extends AbstractSettings<Color> {
    private Consumer<Color> callback = null;

    public ColorSetting(Function function) {
        super(SettingsType.Color, function);
    }

    public Color get() {
        return value;
    }

    public void set(Color value) {
        if (this.isPremium && !Loader.isPremium()) this.value = getDefaultValue();
        else this.value = value;
        callback();
    }

    public Color getDefaultValue() {
        return defaultValue;
    }

    public ColorSetting defaultValue(Color value) {
        this.defaultValue = value;
        set(value);
        return this;
    }

    public String getName() {
        return name;
    }

    public ColorSetting name(String name) {
        this.name = name;
        return this;
    }

    public String getEnName() {
        return enName;
    }

    public ColorSetting enName(String name) {
        this.enName = name;
        return this;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public ColorSetting setPremium(boolean premium) {
        this.isPremium = premium;
        return this;
    }

    public IVisible isVisible() {
        return visible;
    }

    public ColorSetting visible(IVisible visible) {
        this.visible = visible;
        return this;
    }

    public SettingsType getType() {
        return type;
    }

    public void callback() {
        if (callback != null) {
            callback.accept(this.get());
        }
    }

    public ColorSetting callback(Consumer<Color> callback) {
        this.callback = callback;
        return this;
    }

    public ColorSetting build() {
        SettingManager.register(this);
        return this;
    }

    public String toConfig() {
        return getName().concat(":").concat(String.valueOf(ConfigSystem.fromRGBA(get())));
    }
}