package com.client.utils.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mixin.accessor.JsonArrayAccessor;
import mixin.accessor.JsonObjectAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class ItemsColor {
//	v4

    public static final Color black = new Color(0x000000);
    public static final Color dark_blue = new Color(0x0000AA);
    public static final Color dark_green = new Color(0x00AA00);
    public static final Color dark_aqua = new Color(0x00AAAA);
    public static final Color dark_red = new Color(0xAA0000);
    public static final Color dark_purple = new Color(0xAA00AA);
    public static final Color gold = new Color(0xFFAA00);
    public static final Color gray = new Color(0xAAAAAA);
    public static final Color dark_gray = new Color(0x555555);
    public static final Color blue = new Color(0x5555FF);
    public static final Color green = new Color(0x55FF55);
    public static final Color aqua = new Color(0x55FFFF);
    public static final Color red = new Color(0xFF5555);
    public static final Color light_purple = new Color(0xFF55FF);
    public static final Color yellow = new Color(0xFFFF55);
    public static final Color white  = new Color(0xFFFFFF);

    public final boolean hasRenameColor;
    public final boolean colorIsCustom;
    public final Color color; //name, if null → rarity
    public final @Nullable Color colorRename;
    public final Color colorRarity;

    public ItemsColor(boolean hasRenameColor, boolean colorIsCustom, Color color, @Nullable Color colorRename, Color colorRarity) {
        this.hasRenameColor = hasRenameColor;
        this.colorIsCustom = colorIsCustom;
        this.color = color;
        this.colorRename = colorRename;
        this.colorRarity = colorRarity;
    }

    public boolean is(Color color) {
        return this.color.equals(color);
    }

    public boolean isBlack() {
        return color == black;
    }

    public boolean isDarkBlue() {
        return color == dark_blue;
    }

    public boolean isDarkGreen() {
        return color == dark_green;
    }

    public boolean isDarkAqua() {
        return color == dark_aqua;
    }

    public boolean isDarkRed() {
        return color == dark_red;
    }

    public boolean isDarkPurple() {
        return color == dark_purple;
    }

    public boolean isGold() {
        return color == gold;
    }

    public boolean isGray() {
        return color == gray;
    }

    public boolean isDarkGray() {
        return color == dark_gray;
    }

    public boolean isBlue() {
        return color == blue;
    }

    public boolean isGreen() {
        return color == green;
    }

    public boolean isAqua() {
        return color == aqua;
    }

    public boolean isRed() {
        return color == red;
    }

    public boolean isLightPurple() {
        return color == light_purple;
    }

    public boolean isYellow() {
        return color == yellow;
    }

    public boolean isWhite() {
        return color == white;
    }


    public static ItemsColor itemStackGetDisplayColor(ItemStack stack) {
        @Nullable NbtCompound nbtCompound = stack.getSubTag("display");

        @Nullable String textColor = null;
        if (nbtCompound != null && nbtCompound.contains("Name", 8)) {
            JsonElement name = JsonHelper.deserialize(nbtCompound.getString("Name"));
            textColor = parseExtra(name);
        }

        boolean colorIsCustom = false;
        @Nullable Color rename = null;

        if (textColor != null) {
            if (textColor.startsWith("#")) {
                rename = new Color(Integer.parseInt(textColor.substring(1), 16));
                colorIsCustom = true;
            } else {
                try {
                    rename = (Color) ItemsColor.class.getField(textColor).get(null);
                } catch (Exception e) {
                    return null;
                }
            }
        }

        Color rarity = switch (stack.getRarity()) {
            case COMMON -> white;
            case UNCOMMON -> yellow;
            case RARE -> aqua;
            case EPIC -> light_purple;
        };

        return new ItemsColor(rename != null, colorIsCustom, rename == null ? rarity : rename, rename, rarity);
    }

    public static Color getPlayerColor(PlayerEntity player) {
        @Nullable String textColor = parseExtra(Text.Serializer.toJsonTree((player.getDisplayName())));
        @Nullable Color color = null;
        if (textColor != null) {
            if (textColor.startsWith("#")) {
                color = new Color(Integer.parseInt(textColor.substring(1), 16));
            } else {
                try {
                    color = (Color) ItemsColor.class.getField(textColor).get(null);
                } catch (Exception ignored) {}
            }
        }
        return color == null ? ItemsColor.white : color;
    }

    public static Color getPlayerColor(Text player) {
        @Nullable String textColor = parseExtra(Text.Serializer.toJsonTree((player)));
        @Nullable Color color = null;
        if (textColor != null) {
            if (textColor.startsWith("#")) {
                color = new Color(Integer.parseInt(textColor.substring(1), 16));
            } else {
                try {
                    color = (Color) ItemsColor.class.getField(textColor).get(null);
                } catch (Exception ignored) {}
            }
        }
        return color == null ? ItemsColor.white : color;
    }

    @Nullable
    private static String parseExtra(JsonElement element) {
        if (element instanceof JsonObject object) {
            if (((JsonObjectAccessor) (Object) object).getMembers().keySet().contains("color")) return object.get("color").getAsString();
            else if (((JsonObjectAccessor) (Object) object).getMembers().keySet().contains("extra")) {
                return parseExtra(object.get("extra"));
            }
        } else if (element instanceof JsonArray array) {
            for (JsonElement jsonElement : ((JsonArrayAccessor) (Object) array).getElements()) {
                String string = parseExtra(jsonElement);
                if (string != null) return string;
            }
        }

        return null;
    }

}