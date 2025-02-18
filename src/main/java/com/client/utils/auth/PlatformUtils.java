package com.client.utils.auth;

import com.sun.jna.Platform;

public class PlatformUtils {
    // TODO: думаю код ниже не нуждается в каких-либо комментариях

    public enum OSType {
        Windows,
        Linux,
        Mac,
        Unsupported
    }

    public static OSType getOs() {
        if (Platform.isWindows()) return OSType.Windows;
        if (Platform.isLinux()) return OSType.Linux;
        if (Platform.isMac()) return OSType.Mac;
        return OSType.Unsupported;
    }
}