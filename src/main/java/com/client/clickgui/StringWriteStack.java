package com.client.clickgui;

import com.client.clickgui.button.buttons.StringButton;

public class StringWriteStack {
    private static StringButton current;

    public static void setCurrent(StringButton current) {
        StringWriteStack.current = current;
    }

    public static boolean test(StringButton stringButton) {
        return StringWriteStack.current == null || StringWriteStack.current == stringButton;
    }
}
