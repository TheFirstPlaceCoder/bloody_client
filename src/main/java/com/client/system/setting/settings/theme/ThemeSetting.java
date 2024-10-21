package com.client.system.setting.settings.theme;

import com.client.system.function.Function;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.api.IVisible;
import com.client.system.setting.api.SettingsType;
import com.client.system.setting.manager.SettingManager;
import com.client.utils.auth.Loader;

import java.util.ArrayList;
import java.util.List;

public class ThemeSetting extends AbstractSettings<ThemeContainer> {
    private List<ThemeContainer> colorList = new ArrayList<>();

    public ThemeSetting(Function function) {
        super(SettingsType.Theme, function);
    }

    public ThemeSetting setList(List<ThemeContainer> colorList) {
        this.colorList = colorList;
        return this;
    }

    public List<ThemeContainer> getList() {
        return colorList;
    }

    @Override
    public ThemeContainer get() {
        return value;
    }

    @Override
    public void set(ThemeContainer value) {
        if (this.isPremium && !Loader.isPremium()) this.value = getDefaultValue();
        else this.value = value;
    }

    @Override
    public ThemeContainer getDefaultValue() {
        return defaultValue;
    }

    @Override
    public ThemeSetting defaultValue(ThemeContainer value) {
        this.defaultValue = value;
        set(value);
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ThemeSetting name(String name) {
        this.name = name;
        return this;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public ThemeSetting setPremium(boolean premium) {
        this.isPremium = premium;
        return this;
    }

    @Override
    public IVisible isVisible() {
        return visible;
    }

    @Override
    public ThemeSetting visible(IVisible visible) {
        this.visible = visible;
        return this;
    }

    @Override
    public SettingsType getType() {
        return type;
    }

    @Override
    public ThemeSetting build() {
        SettingManager.register(this);
        return this;
    }

    @Override
    public String toConfig() {
        return getName().concat(":").concat(get().name());
    }
}
