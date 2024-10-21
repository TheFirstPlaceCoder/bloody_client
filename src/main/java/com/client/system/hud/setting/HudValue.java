package com.client.system.hud.setting;

import com.client.system.setting.api.ConfigManual;
import com.client.system.setting.api.IConfig;

public class HudValue implements IConfig {
    private final String name;
    private boolean value;
    private Runnable callback = null;

    public HudValue(String name, boolean value) {
        this.name = name;
        this.value = value;
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

    public void callback() {
        if (callback != null) {
            callback.run();
        }
    }

    public String getName() {
        return name;
    }

    public boolean get() {
        return value;
    }

    public void set(boolean value) {
        this.value = value;
    }

    @Override
    public String toCfg() {
        return getName() + ConfigManual.DEFAULT.split + get();
    }

    @Override
    public void load(String in, ConfigManual manual) {
        if (in.split(manual.split)[0].equals(getName())) {
            set(Boolean.parseBoolean(in.split(manual.split)[1]));
        }
    }
}