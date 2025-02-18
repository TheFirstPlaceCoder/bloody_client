package com.client.system.setting.settings;

import com.client.system.function.Function;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.api.IVisible;
import com.client.system.setting.api.SettingsType;
import com.client.system.setting.manager.SettingManager;
import com.client.utils.auth.Loader;

public class Widget extends AbstractSettings<Runnable> {
    public Widget(Function function) {
        super(SettingsType.Widget, function);
    }

    @Override
    public Runnable get() {
        return value;
    }

    @Override
    public void set(Runnable value) {
        if (this.isPremium && !Loader.isPremium()) this.value = getDefaultValue();
        else this.value = value;
    }

    @Override
    public Runnable getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Widget defaultValue(Runnable value) {
        this.defaultValue = value;
        set(value);
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Widget name(String name) {
        this.name = name;
        return this;
    }

    public String getEnName() {
        return enName;
    }

    public Widget enName(String name) {
        this.enName = name;
        return this;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public Widget setPremium(boolean premium) {
        this.isPremium = premium;
        return this;
    }

    @Override
    public IVisible isVisible() {
        return visible;
    }

    @Override
    public Widget visible(IVisible visible) {
        this.visible = visible;
        return this;
    }

    @Override
    public SettingsType getType() {
        return type;
    }

    @Override
    public Widget build() {
        SettingManager.register(this);
        return this;
    }

    @Override
    public String toConfig() {
        return "";
    }
}
