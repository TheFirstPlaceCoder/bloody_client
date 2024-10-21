package com.client.impl.command;

import com.client.interfaces.IChatHud;
import com.client.system.command.Command;
import com.client.system.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class ClearCommand extends Command {
    public ClearCommand() {
        super("Clear", List.of("clear"), List.of());
    }

    @Override
    public void command(String[] args) {
        ((IChatHud) mc.inGameHud.getChatHud()).clear();
    }

    @Override
    public void error() {

    }
}