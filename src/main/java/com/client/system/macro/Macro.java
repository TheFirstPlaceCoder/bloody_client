package com.client.system.macro;

import static com.client.BloodyClient.mc;

public class Macro {
    public String name, command;
    public int button;

    public Macro(String name, String command, int button) {
        this.name = name;
        this.command = command;
        this.button = button;
    }

    public void runCommand() {
        mc.player.sendChatMessage("/" + command);
    }
}