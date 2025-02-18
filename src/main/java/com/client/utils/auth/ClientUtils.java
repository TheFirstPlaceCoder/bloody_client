package com.client.utils.auth;

import com.client.utils.Utils;

public class ClientUtils {
    public static boolean isUser(String finalHwid) {
        // https://bloodyhvh.site/auth/getAccessUser.php?hwid=
        String isUserUrl = "https://bloodyhvh.site/auth/getAccessUser.php?hwid=";
        String value1 = ConnectionManager.get(isUserUrl + finalHwid).sendString();
        return (Loader.userInt = -3458673) == -3458673 && value1.contains(Utils.generateHash(finalHwid));
    }

    public static boolean isPremium(String finalHwid) {
        // https://bloodyhvh.site/auth/getAccessPremiumUser.php?hwid=
        String isPremiumUrl = "https://bloodyhvh.site/auth/getAccessPremiumUser.php?hwid=";
        String value1 = ConnectionManager.get(isPremiumUrl + finalHwid).sendString();
        return value1.contains(Utils.generateHash(finalHwid));
    }

    public static boolean isYouTube(String finalHwid) {
        // https://bloodyhvh.site/auth/getAccessYouTube.php?hwid=
        String isYouTubeUrl = "https://bloodyhvh.site/auth/getAccessYouTube.php?hwid=";
        String value1 = ConnectionManager.get(isYouTubeUrl + finalHwid).sendString();
        return value1.contains(finalHwid + "1");
    }

    public static boolean isHelper(String finalHwid) {
        // https://bloodyhvh.site/auth/getAccessHelper.php?hwid=
        String isHelperUrl = "https://bloodyhvh.site/auth/getAccessHelper.php?hwid=";
        String value1 = ConnectionManager.get(isHelperUrl + finalHwid).sendString();
        return value1.contains(finalHwid + "1");
    }

    public static boolean isModer(String finalHwid) {
        // https://bloodyhvh.site/auth/getAccessModer.php?hwid=
        String isModerUrl = "https://bloodyhvh.site/auth/getAccessModer.php?hwid=";
        String value1 = ConnectionManager.get(isModerUrl + finalHwid).sendString();
        return value1.contains(finalHwid + "1");
    }

    public static boolean isDev(String finalHwid) {
        // https://bloodyhvh.site/auth/getAccessDev.php?hwid=
        String isDevUrl = "https://bloodyhvh.site/auth/getAccessDev.php?hwid=";
        String value1 = ConnectionManager.get(isDevUrl + finalHwid).sendString();
        return value1.contains(finalHwid + "1");
    }

    public static String getUid(String finalHwid) {
        // https://bloodyhvh.site/php/getUid.php?hwid=
        String getUidUrl = "https://bloodyhvh.site/php/getUid.php?hwid=";
        return ConnectionManager.get(getUidUrl + finalHwid).sendString();
    }

    public static String getAccountName(String finalHwid) {
        // https://bloodyhvh.site/php/getAccountName.php?hwid=
        String getAccountNameUrl = "https://bloodyhvh.site/php/getAccountName.php?hwid=";
        return ConnectionManager.get(getAccountNameUrl + finalHwid).sendString();
    }

    public static String getVersion() {
        // https://bloodyhvh.site/auth/getVersion.php
        return ConnectionManager.get("https://bloodyhvh.site/auth/getVersion.php").sendString();
    }

    public static long getJarSize() {
        Loader.getJarSizeLong = 43387L;
        // https://bloodyhvh.site/auth/getJarSize.php
        return Long.valueOf(ConnectionManager.get("https://bloodyhvh.site/auth/getJarSize.php").sendString());
    }

    public static boolean isBanned(String finalHwid) {
        Loader.banInt = 36458;
        // https://bloodyhvh.site/auth/getBanned.php?hwid=
        String banListUrl = "https://bloodyhvh.site/auth/getBanned.php?hwid=";
        String value1 = ConnectionManager.get(banListUrl + finalHwid).sendString();
        return value1.contains("ban");
    }
}