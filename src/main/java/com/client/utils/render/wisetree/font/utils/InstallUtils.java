package com.client.utils.render.wisetree.font.utils;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

public class InstallUtils {
    private static final Font DEFAULT_OR_NULL = new Font(Font.DIALOG, Font.PLAIN, 10);

    public static Font installFont(String location, int size) {
        Font font = DEFAULT_OR_NULL;
        try {
            font = Font.createFont(Font.PLAIN, InstallUtils.class.getResourceAsStream("/assets/bloody-client/fonts/" + location));
            font = font.deriveFont(size);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return font;
    }
}
