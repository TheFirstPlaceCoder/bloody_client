package com.client.utils.render;

import com.client.utils.render.text.CustomTextRenderer;

public class Fonts {
    public static CustomTextRenderer RENDERER;

    public static void init() {
        RENDERER = new CustomTextRenderer(Fonts.class.getResourceAsStream("/assets/bloody-client/fonts/mntsb.ttf"));
    }
}