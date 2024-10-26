package com.client.utils.auth;

public class ClientUtils {
    public static boolean isUser(String finalHwid) {
        // https://bloodyhvh.site/auth/getAccess.php?hwid=
        String value1 = ConnectionManager.get("https://bloodyhvh.site/auth/getAccess.php?hwid=" + finalHwid).sendString();
        boolean b = (Loader.userInt = -3458673) == -3458673;
        return b && value1.contains(finalHwid + "1");
    }

    public static boolean isPremium(String finalHwid) {
        // https://bloodyhvh.site/auth/getAccessPremium.php?hwid=
        String value1 = ConnectionManager.get("https://bloodyhvh.site/auth/getAccessPremium.php?hwid=" + finalHwid).sendString();
        return value1.contains(finalHwid + "1");
    }

    public static boolean isYouTube(String finalHwid) {
        // https://bloodyhvh.site/auth/getAccessYouTube.php?hwid=
        String value1 = ConnectionManager.get("https://bloodyhvh.site/auth/getAccessYouTube.php?hwid=" + finalHwid).sendString();
        return value1.contains(finalHwid + "1");
    }

    public static boolean isHelper(String finalHwid) {
        // https://bloodyhvh.site/auth/getAccessHelper.php?hwid=
        String value1 = ConnectionManager.get("https://bloodyhvh.site/auth/getAccessHelper.php?hwid=" + finalHwid).sendString();
        return value1.contains(finalHwid + "1");
    }

    public static boolean isModer(String finalHwid) {
        // https://bloodyhvh.site/auth/getAccessModer.php?hwid=
        String value1 = ConnectionManager.get("https://bloodyhvh.site/auth/getAccessModer.php?hwid=" + finalHwid).sendString();
        return value1.contains(finalHwid + "1");
    }

    public static boolean isDev(String finalHwid) {
        // https://bloodyhvh.site/auth/getAccessDev.php?hwid=
        String value1 = ConnectionManager.get("https://bloodyhvh.site/auth/getAccessDev.php?hwid=" + finalHwid).sendString();
        return value1.contains(finalHwid + "1");
    }

    public static String getUid(String finalHwid) {
        // https://bloodyhvh.site/php/getUid.php?hwid=
        return ConnectionManager.get("https://bloodyhvh.site/php/getUid.php?hwid=" + finalHwid).sendString();
    }

    public static String getAccountName(String finalHwid) {
        // https://bloodyhvh.site/php/getAccountName.php?hwid=
        return ConnectionManager.get("https://bloodyhvh.site/php/getAccountName.php?hwid=" + finalHwid).sendString();
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
        String value1 = ConnectionManager.get("https://bloodyhvh.site/auth/getBanned.php?hwid=" + finalHwid + "&ip=" + ConnectionUtils.getIP()).sendString();
        return value1.contains("ban");
    }
}
