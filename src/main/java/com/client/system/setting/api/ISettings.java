package com.client.system.setting.api;

public interface ISettings<T> {
    T get();

    void set(T value);

    T getDefaultValue();

    Object defaultValue(T value);

    String getName();

    Object name(String name);

    IVisible isVisible();

    Object visible(IVisible visible);

    SettingsType getType();

    Object build();

    String toConfig();
}