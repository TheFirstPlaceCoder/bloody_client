package com.client.impl.function.movement.blink;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class EmptyScreen extends Screen {
    public EmptyScreen() {
        super(Text.of("empty"));
    }
}