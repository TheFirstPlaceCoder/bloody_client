package com.client.utils.auth;

import com.client.utils.auth.records.CheckerClass;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ArgumentUtils {
    public static final List<String> BLOCKED_ARGS = List.of("-agentlib:jdwp", "-Xdebug", "-Xrunjdwp", "-Xprof", "-Djava.security.debug", "-Dcom.sun.management.jmxremote", "-Dcom.sun.management.jmxremote.authenticate", "-Dcom.sun.management.jmxremote.ssl", "-agentpath:", "-javaagent:", "-Xcheck:jni", "-Xlint", "-Xss", "-Xcheck:all", "-Djava.compiler=NONE");

    public static CheckerClass hasBlockedArgs() {
        // TODO: Здесь мы проверяем все аргументы JVM при запуске
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        List<String> inputArgs = new ArrayList<>();

        try {
            inputArgs.addAll(runtimeMXBean.getInputArguments());
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        String has = "";
        // К сожалению, метод getInputArguments() не содержит аргумент -noverify
        // Поэтому здесь присутствует проверка на долбоеба, с помощью загрузки класса
        if (hasNoVerify()) return new CheckerClass(true, "-noverify");
        for (String string : inputArgs) {
            if (BLOCKED_ARGS.contains(string)) {
                has = string;
                break;
            }
        }

        return new CheckerClass(((Loader.argumentInt = 432) == 432) && !has.isEmpty(), has);
    }

    public static boolean hasNoVerify() {
        // TODO: Код ниже побайтово собирает класс, который ретурнит ошибку
        // Если -noverify есть, то после этой ошибки код продолжит выполняться, в нашем случае это flag = true
        boolean flag = false;
        try {
            Loader.argumentInt = 432;
            new ClassLoader() {{
                byte[] decode = Base64.getDecoder().decode("yv66vgAAAD0ADgEACWFlMS9UZXN0NQcAAQEAEGphdmEvbGFuZy9PYmplY3QHAAMBAAY8aW5pdD4BAAMoKVYMAAUABgoABAAHAQAEdGhpcwEAC0xhZTEvVGVzdDU7AQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQAhAAIABAAAAAAAAQABAAUABgABAAsAAAAwAAEAAQAAAAYqKrcACLEAAAACAAwAAAAGAAEAAAADAA0AAAAMAAEAAAAGAAkACgAAAAA");
                defineClass(null, decode, 0, decode.length).newInstance();
            }};
            flag = true;
        } catch (Throwable ignored) {
        }

        return flag;
    }
}