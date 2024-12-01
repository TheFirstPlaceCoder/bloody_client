package com.client.utils.auth;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VMUtils {
    public static String identifyVM() {
        return "";
    }

    public static boolean isOnVM() {
        //TODO: Открепить после исправления совместимости с Линуксом
        String vm = identifyVM();
        return !vm.isEmpty();
    }
}