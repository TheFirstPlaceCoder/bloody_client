package com.client.utils.misc;

import com.client.utils.files.TwilightTextTranslator;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class Exceptions {
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    public static void printError(Object... exception_string) {
        String path = FabricLoader.getInstance().getGameDir() + "/errors.log";

        writeString(path, format.format(System.currentTimeMillis()) + "\n");
        for (Object o : exception_string) {
            String error = TwilightTextTranslator.translate(o instanceof Throwable err ? ExceptionUtils.getStackTrace(err) : Objects.toString(o));

            writeString(path, error);
            writeString(path, "\n\n\n\n\n");
        }
    }

    public static void writeString(String path, String text) {
        writeString(new File(path), text);
    }

    public static void writeString(File path, String text) {
        try {
            createNewFile(path);
            Files.writeString(path.toPath(), text, StandardOpenOption.APPEND);
        } catch (IOException ex) {
            String message = "[Safety] > writeString, path = " + path + ", text = " + text;
            printError(message, ex);
        }
    }

    public static void createNewFile(File file) {
        try {
            file.getParentFile().mkdirs();
            if (!file.exists()) file.createNewFile();
        } catch (IOException ex) {
            String message = "[Safety] > createNewFile, path = " + file.getAbsolutePath();
            printError(message, ex);
        }
    }
}
