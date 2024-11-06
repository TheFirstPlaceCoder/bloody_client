package com.client.system.companion;

import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public record RegistryObject<T>(Identifier id, T value) implements Supplier<T> {
    @Override
    public T get() {
        return value;
    }
}