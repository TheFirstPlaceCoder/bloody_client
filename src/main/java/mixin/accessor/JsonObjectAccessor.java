package mixin.accessor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(JsonObject.class)
public interface JsonObjectAccessor {
    @Accessor("members")
    LinkedTreeMap<String, JsonElement> getMembers();
}
