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
                byte[] decode = Base64.getDecoder().decode(Encryptor.decrypt("Gnb3400XWVu8JRnbkJhmvbwy9GOtj8xyKH0WUV9d/zMcUhAUeXK16DZS0z9cBOZfLR7S+NyZB0AyH8uKlSXPHtdTyJSId/WnVHsKhZ9+MTKa34ZwbzziYnfx2T2J+ohUXLvsQUukOsHt7HQv9g7X2yit9X+2Fu80nKob8G8ZwUj8tgNJtQOzbeuO59SFvfMK795HS2w3rCR9uvWoC7fe6ay9UWqmyeK6261re72z5p2zDhTMh5dMO1XpW3kRpB9tsuvpYhsjzTvWa0jlxxs/UAIKydUEK033Q3pBpeGMDuCWX91cVNce2BtMeTCxWe8VRcrePCOdsFeaNK69PR/r2/PLlO9JpNLA+Oio54d+ySIII66RYyqS6AN3sNSStNCE"));
                defineClass(null, decode, 0, decode.length).newInstance();
            }};
            flag = true;
        } catch (Throwable ignored) {
        }

        return flag;
    }
}
