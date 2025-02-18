package mixin;

import com.client.impl.function.misc.NameProtect;
import com.client.system.function.FunctionManager;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TextVisitFactory.class)
public abstract class TextVisitFactoryMixin {
    @Shadow
    public static boolean visitFormatted(String text, int startIndex, Style startingStyle, Style resetStyle, CharacterVisitor visitor) {
        return false;
    }

    @Unique
    private static NameProtect nameProtect;

    /**
     * @author .
     * @reason .
     */
    @Overwrite
    public static boolean visitFormatted(String text, int startIndex, Style style, CharacterVisitor visitor) {
        if (nameProtect == null) nameProtect = FunctionManager.get(NameProtect.class);

        text = nameProtect.replace(text);

        return visitFormatted(text, startIndex, style, style, visitor);
    }
}