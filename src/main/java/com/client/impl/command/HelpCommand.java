package com.client.impl.command;

import com.client.system.command.Command;
import com.client.system.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("Help", List.of("help"), List.of());
    }

    @Override
    public void command(String[] args) {
        info(Text.of(Formatting.AQUA + "Список команд:"));
        for (Command command : CommandManager.getCommands()) {
            if (command instanceof HelpCommand) continue;
            info(Text.of(Formatting.WHITE + (command.getName() + " Command — ") + Formatting.AQUA + ("." + command.getFirstList().get(0))));
        }
    }

    @Override
    public void error() {

    }
}
