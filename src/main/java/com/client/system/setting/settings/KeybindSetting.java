package com.client.system.setting.settings;

import com.client.system.function.Function;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.api.IVisible;
import com.client.system.setting.api.SettingsType;
import com.client.system.setting.manager.SettingManager;
import com.client.utils.auth.Loader;

public class KeybindSetting extends AbstractSettings<Integer> {
    public KeybindSetting(Function function) {
        super(SettingsType.Keybind, function);
        defaultValue(-1);
    }

    public Integer get() {
        return value;
    }

    public void set(Integer value) {
        if (this.isPremium && !Loader.isPremium()) this.value = getDefaultValue();
        else this.value = value;
    }

    public boolean key(int key, boolean keyboard) {
        if (!keyboard && get() >= 90000) return get() - 90001 == key;
        return key == get();
    }

    public Integer getDefaultValue() {
        return defaultValue;
    }

    public KeybindSetting defaultValue(Integer value) {
        this.defaultValue = value;
        set(value);
        return this;
    }

    public String getName() {
        return name;
    }

    public KeybindSetting name(String name) {
        this.name = name;
        return this;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public KeybindSetting setPremium(boolean premium) {
        this.isPremium = premium;
        return this;
    }

    public IVisible isVisible() {
        return visible;
    }

    public KeybindSetting visible(IVisible visible) {
        this.visible = visible;
        return this;
    }

    public SettingsType getType() {
        return type;
    }

    public KeybindSetting build() {
        SettingManager.register(this);
        return this;
    }

    public String toConfig() {
        return getName().concat(":").concat(String.valueOf(get()));
    }
}
