package com.client.system.setting.api;

import com.client.system.function.Function;

public abstract class AbstractSettings<T> implements ISettings<T> {
    public T value, defaultValue;
    public String name;
    public SettingsType type;
    public IVisible visible;
    public Function function;
    public boolean isPremium;

    public AbstractSettings(SettingsType type, Function function) {
        this.type = type;
        this.function = function;
        this.visible = () -> true;
        this.isPremium = false;
    }
}