package mixin;

import com.client.impl.function.misc.NameProtect;
import com.client.system.function.FunctionManager;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TextVisitFactory.class)
public abstract class TextVisitFactoryMixin {
    @Shadow
    public static boolean visitFormatted(String text, int startIndex, Style startingStyle, Style resetStyle, CharacterVisitor visitor) {
        return false;
    }

    /**
     * @author .
     * @reason .
     */
    @Overwrite
    public static boolean visitFormatted(String text, int startIndex, Style style, CharacterVisitor visitor) {
        if (FunctionManager.get(NameProtect.class) != null) {
            text = FunctionManager.get(NameProtect.class).replace(text);
        }
        return visitFormatted(text, startIndex, style, style, visitor);
    }
}