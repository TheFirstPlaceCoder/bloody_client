package com.client.system.setting.api;

public enum ConfigManual {
    DEFAULT(":");

    public final String split;

    ConfigManual(String split) {
        this.split = split;
    }
}
