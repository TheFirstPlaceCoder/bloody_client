package mixin.accessor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(JsonArray.class)
public interface JsonArrayAccessor {
    @Accessor("elements")
    List<JsonElement> getElements();
}
