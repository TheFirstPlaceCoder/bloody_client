package com.client.interfaces;

import net.minecraft.text.Text;

public interface IChatHud {
    void message(Text text);

    void message(Text text, int id);

    void message(Text text, int ticks, int id);

    void clear();

    void unHookClear();
}
