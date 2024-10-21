package com.client.system.setting.settings.multiboolean;

import com.client.system.function.Function;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.api.IVisible;
import com.client.system.setting.api.SettingsType;
import com.client.system.setting.manager.SettingManager;
import com.client.utils.auth.Loader;

import java.util.List;
import java.util.function.Consumer;

public class MultiBooleanSetting extends AbstractSettings<List<MultiBooleanValue>> {
    private Consumer<List<MultiBooleanValue>> onChanged;

    public MultiBooleanSetting(Function function) {
        super(SettingsType.MultiBoolean, function);
        onChanged = null;
    }

    public boolean get(String name) {
        try {
            return get().stream().filter(multiBooleanSetting -> multiBooleanSetting.getName().equals(name)).toList().get(0).getValue();
        } catch (Exception exception) {
            return get().get(0).getValue();
        }
    }

    public boolean get(int index) {
        try {
            return get().get(index).getValue();
        } catch (Exception exception) {
            return get().get(0).getValue();
        }
    }

    public MultiBooleanValue getMultiBooleanValue(String name) {
        try {
            return get().stream().filter(multiBooleanSetting -> multiBooleanSetting.getName().equals(name)).toList().get(0);
        } catch (Exception exception) {
            return get().get(0);
        }
    }

    public MultiBooleanValue getMultiBooleanValue(int index) {
        try {
            return get().get(index);
        } catch (Exception exception) {
            return get().get(0);
        }
    }

    public List<MultiBooleanValue> get() {
        return value;
    }

    public List<MultiBooleanValue> getChecked() {
        return value.stream().filter(MultiBooleanValue::getValue).toList();
    }

    public String toggledCount() {
        String size = String.valueOf(value.size());
        int i = 0;

        for (MultiBooleanValue multiBooleanValue : value) {
            if (multiBooleanValue.getValue())
                i++;
        }

        return i + "/" + size;
    }

    public void set(List<MultiBooleanValue> value) {
        if (this.isPremium && !Loader.isPremium()) this.value = getDefaultValue();
        else this.value = value;
        onChanged();
    }

    private void onChanged() {
        if (onChanged != null) onChanged.accept(value);
    }

    public MultiBooleanSetting onChanged(Consumer<List<MultiBooleanValue>> value) {
        this.onChanged = value;
        return this;
    }

    public List<MultiBooleanValue> getDefaultValue() {
        return defaultValue;
    }

    public MultiBooleanSetting defaultValue(List<MultiBooleanValue> value) {
        this.defaultValue = value;
        set(value);
        return this;
    }

    public String getName() {
        return name;
    }

    public MultiBooleanSetting name(String name) {
        this.name = name;
        return this;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public MultiBooleanSetting setPremium(boolean premium) {
        this.isPremium = premium;
        return this;
    }

    public IVisible isVisible() {
        return visible;
    }

    public MultiBooleanSetting visible(IVisible visible) {
        this.visible = visible;
        return this;
    }

    public SettingsType getType() {
        return type;
    }

    public MultiBooleanSetting build() {
        SettingManager.register(this);
        return this;
    }

    public String toConfig() {
        StringBuilder stringBuilder = new StringBuilder();

        for (MultiBooleanValue multiBooleanValue : get()) {
            stringBuilder.append(multiBooleanValue.name).append(",").append(multiBooleanValue.value).append("|");
        }

        return getName().concat(":").concat(stringBuilder.toString());
    }
}
