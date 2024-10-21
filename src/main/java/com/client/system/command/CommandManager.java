package com.client.system.command;

import api.interfaces.EventHandler;
import com.client.event.events.KeyEvent;
import com.client.impl.command.*;
import com.client.impl.command.rct.RctCom;

import java.util.ArrayList;

public class CommandManager {
    private static final ArrayList<Command> COMMANDS = new ArrayList<>();

    public static void init() {
        add(new RctCom());
        add(new AnarchyCom());
        add(new FriendsCommand());
        add(new GpsCommand());
        add(new DropCommand());
        add(new ReportCommand());
        add(new HelpCommand());
        add(new StaffCommand());
        add(new ConfigCommand());
        add(new ClearCommand());
        add(new LoginCom());
        add(new MacroCommand());
    }

    public static void add(Command command) {
        COMMANDS.add(command);
    }

    public static void runCommand(String input) {
        String[] _args = input.split(" ");
        String command = _args[0];
        String args = input.substring(command.length()).trim();
        for (Command c : COMMANDS) {
            for (String name : c.getFirstList()) {
                if (_args[0].equalsIgnoreCase(name)) {
                    try {
                        c.command(args.split(" "));
                    } catch (Exception e) {
                        c.error();
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Command> T get(Class<T> klass) {
        for (Command command : getCommands()) {
            if (command.getClass() == klass) {
                return (T) command;
            }
        }

        return null;
    }

    public static ArrayList<Command> getCommands() {
        return COMMANDS;
    }

    @EventHandler
    public void onKey(KeyEvent event) {
        for (Command command : getCommands()) {
            command.onKeyEvent(event);
        }
    }
}
