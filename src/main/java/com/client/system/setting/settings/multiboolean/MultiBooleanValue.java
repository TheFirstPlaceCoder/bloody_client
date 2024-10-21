package com.client.system.setting.settings.multiboolean;

public class MultiBooleanValue {
    public boolean value;
    public String name;

    public MultiBooleanValue() {
        value = false;
        name = "";
    }

    public MultiBooleanValue(boolean value, String name) {
        this.value = value;
        this.name = name;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
