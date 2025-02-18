package com.client.system.function;

public enum Category {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    MISC("Misc"),
    VISUAL("Visual"),
    CLIENT("Client"),
    HUD("Hud");

    private String name;
    Category(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}