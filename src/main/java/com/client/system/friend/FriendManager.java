package com.client.system.friend;

import com.client.impl.function.client.Friends;
import com.client.system.cheststealer.ChestStealerItem;
import com.client.system.function.FunctionManager;
import com.client.utils.color.ColorUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.registry.Registry;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FriendManager {
    private static final ArrayList<String> friends = new ArrayList<>();

    public static boolean add(String friend) {
        if (isFriend(friend)) return false;
        friends.add(friend);
        return true;
    }

    public static boolean remove(String friend) {
        if (!isFriend(friend)) return false;
        friends.remove(friend);
        return true;
    }

    public static boolean isFriend(String friend) {
        return friends.contains(friend);
    }

    public static boolean isFriend(Entity friend) {
        return friends.contains(friend.getEntityName());
    }

    public static boolean isFriend(PlayerEntity friend) {
        return friends.contains(friend.getEntityName());
    }

    public static void clear() {
        friends.clear();
    }

    public static ArrayList<String> getFriends() {
        return friends;
    }

    public static Color getFriendsColor() {
        return ColorUtils.injectAlpha(FunctionManager.get(Friends.class).friendsColor.get(), 255);
    }

    public static Color getFriendsColorWithoutAlpha() {
        return FunctionManager.get(Friends.class).friendsColor.get();
    }

    public static boolean isAttackable(Entity entity) {
        return !isFriend(entity) || !FunctionManager.get(Friends.class).dontAttack.get();
    }

    public static void save(BufferedWriter writer) {
        try {
            writer.write("friendslist{\n");
            for (String friend : getFriends()) {
                writer.write(friend + "\n");
            }
            writer.write("}\n");
        } catch (IOException ignore) {
        }
    }

    public static void load(List<String> strings) {
        clear();
        boolean target = false;
        for (String string : strings) {
            if (string.startsWith("}") && target)
                break;
            if (target) {
                add(string);
            } else {
                if (string.startsWith("friendslist")) {
                    target = true;
                }
            }
        }
    }
}
