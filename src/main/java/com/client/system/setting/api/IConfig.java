package com.client.system.setting.api;

public interface IConfig {
    String toCfg();

    void load(String in, ConfigManual manual);
}
