package com.client.system.setting.settings;

import com.client.system.function.Function;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.api.IVisible;
import com.client.system.setting.api.SettingsType;
import com.client.system.setting.manager.SettingManager;
import com.client.utils.auth.Loader;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ListSetting extends AbstractSettings<String> {
    private List<String> list = new ArrayList<>();
    private Consumer<String> callback = null;

    public ListSetting(Function function) {
        super(SettingsType.List, function);
    }

    public String get() {
        return value;
    }

    public void set(String value) {
        if (this.isPremium && !Loader.isPremium()) this.value = getDefaultValue();
        else this.value = value;
        callback();
    }

    public ListSetting list(List<String> list) {
        this.list = list;
        return this;
    }

    public List<String> getList() {
        return list;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public ListSetting defaultValue(String value) {
        this.defaultValue = value;
        set(value);
        return this;
    }

    public String getName() {
        return name;
    }

    public ListSetting name(String name) {
        this.name = name;
        return this;
    }

    public String getEnName() {
        return enName;
    }

    public ListSetting enName(String name) {
        this.enName = name;
        return this;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public ListSetting setPremium(boolean premium) {
        this.isPremium = premium;
        return this;
    }

    public IVisible isVisible() {
        return visible;
    }

    public ListSetting visible(IVisible visible) {
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

    public ListSetting callback(Consumer<String> callback) {
        this.callback = callback;
        return this;
    }

    public ListSetting build() {
        SettingManager.register(this);
        return this;
    }


    public String toConfig() {
        return getName().concat(":").concat(get());
    }
}
