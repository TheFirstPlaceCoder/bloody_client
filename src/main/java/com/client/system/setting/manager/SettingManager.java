package com.client.system.setting.manager;

import com.client.system.function.Function;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.api.IVisible;
import com.client.system.setting.api.SettingsType;
import com.client.system.setting.settings.BooleanSetting;

import java.util.ArrayList;
import java.util.List;

public class SettingManager {
    public static final AbstractSettings<?> EMPTY_SETTING = new AbstractSettings<>(SettingsType.EMPTY, null) {
        @Override
        public Object get() {
            return "";
        }

        @Override
        public void set(Object value) {
        }

        @Override
        public Object getDefaultValue() {
            return "";
        }

        @Override
        public Object defaultValue(Object value) {
            return "";
        }

        @Override
        public String getName() {
            return "";
        }

        @Override
        public Object name(String name) {
            return "";
        }

        @Override
        public String getEnName() {
            return "";
        }

        @Override
        public IVisible isVisible() {
            return () -> true;
        }

        @Override
        public Object visible(IVisible visible) {
            return "";
        }

        @Override
        public SettingsType getType() {
            return SettingsType.EMPTY;
        }

        @Override
        public Object build() {
            return "";
        }

        @Override
        public String toConfig() {
            return "";
        }
    };

    private static final List<AbstractSettings<?>> SETTINGS_LIST = new ArrayList<>();

    public static void register(AbstractSettings<?> abstractSettings) {
        SETTINGS_LIST.add(abstractSettings);
    }

    public static List<AbstractSettings<?>> getSettingsList(SettingsType settingsType) {
        return SETTINGS_LIST.stream().filter(abstractSettings -> abstractSettings.getType().equals(settingsType)).toList();
    }

    public static List<AbstractSettings<?>> getSettingsList(Function function) {
        return SETTINGS_LIST.stream().filter(abstractSettings -> abstractSettings.function.equals(function)).toList();
    }

    public static List<AbstractSettings<?>> getSettingsList() {
        return SETTINGS_LIST;
    }
}
