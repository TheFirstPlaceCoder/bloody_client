package com.client.system.companion;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;

public class DeferredRegister<T> {
    private final Registry<T> registry;
    private final String modId;
    private final List<RegistryObject<? extends T>> entries = new ArrayList();

    public DeferredRegister(Registry<T> registry, String modId) {
        this.registry = registry;
        this.modId = modId;
    }

    public static <T> DeferredRegister<T> create(Registry<T> registry, String modId) {
        return new DeferredRegister(registry, modId);
    }

    public <R extends T> RegistryObject<R> register(String name, Supplier<R> supplier) {
        Identifier id = new Identifier(this.modId, name);
        RegistryObject<R> o = new RegistryObject(id, supplier.get());
        this.entries.add(o);
        return o;
    }

    public void register() {
        this.entries.forEach((ro) -> {
            Registry.register(this.registry, ro.id(), ro.get());
        });
    }
}
