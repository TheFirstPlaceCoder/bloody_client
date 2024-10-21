package com.client.system.setting.settings;

import com.client.system.function.Function;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.api.IVisible;
import com.client.system.setting.api.SettingsType;
import com.client.system.setting.manager.SettingManager;
import com.client.utils.auth.Loader;

import java.util.function.Consumer;

public class BooleanSetting extends AbstractSettings<Boolean> {
    private Consumer<Boolean> callback = null;

    public BooleanSetting(Function function) {
        super(SettingsType.Boolean, function);
    }

    public Boolean get() {
        return value;
    }

    public void set(Boolean value) {
        if (this.isPremium && !Loader.isPremium()) this.value = getDefaultValue();
        else this.value = value;
        callback();
    }

    public Boolean getDefaultValue() {
        return defaultValue;
    }

    public BooleanSetting defaultValue(Boolean value) {
        this.defaultValue = value;
        this.value = value;
        return this;
    }

    public String getName() {
        return name;
    }

    public BooleanSetting name(String name) {
        this.name = name;
        return this;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public BooleanSetting setPremium(boolean premium) {
        this.isPremium = premium;
        return this;
    }

    public IVisible isVisible() {
        return visible;
    }

    public BooleanSetting visible(IVisible visible) {
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

    public BooleanSetting callback(Consumer<Boolean> callback) {
        this.callback = callback;
        return this;
    }

    public BooleanSetting build() {
        SettingManager.register(this);
        return this;
    }

    public String toConfig() {
        return getName().concat(":").concat(String.valueOf(get()));
    }
}