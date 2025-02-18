package com.client.utils.auth.enums;

public enum SubType {
    DEV("Dev"),
    USER("Пользователь"),
    PREMIUM("Премиум"),
    YT("Youtube"),
    MODER("Moder"),
    HELPER("Helper"),
    UNKNOWN("");

    public boolean isPremium() {
        return this != USER && this != UNKNOWN;
    }

    public final String name;

    SubType(String name) {
        this.name = name;
    }
}