package com.client.impl.command;

import com.client.event.events.KeyEvent;
import com.client.system.command.Command;
import com.client.system.friend.FriendManager;
import com.client.system.macro.Macro;
import com.client.system.macro.Macros;
import com.client.utils.misc.InputUtils;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.List;

public class MacroCommand extends Command {
    public MacroCommand() {
        super("Macro", List.of("macro"), List.of("add <имя> <комманда>", "remove <имя>", "clear", "list"));
    }

    boolean listening = false;
    String name = "", command = "";

    @Override
    public void onKeyEvent(KeyEvent event) {
        if (!listening || event.action != InputUtils.Action.PRESS) return;

        Macros.add(new Macro(name, command, event.key));
        info(Formatting.WHITE + name + Formatting.AQUA + " был добавлен в список макросов.");
        listening = false;
    }

    @Override
    public void command(String[] args) {
        switch (args[0]) {
            case "add" : {
                if (Macros.contains(args[1])) {
                    info(Formatting.WHITE + args[1] + Formatting.RED + " уже есть в списке макросов.");
                } else {
                    String com = Arrays.toString(args).substring(args[0].length() + args[1].length() + 5).replace(",", "").replace("]", "");

                    if (com.isEmpty() || com.isBlank()) {
                        error("Команды для действия устая");
                    } else if (com.contains("/")) {
                        error("Команды для действия нужно вводить без знака " + Formatting.WHITE + "/");
                    } else {
                        info(Formatting.AQUA + "Нажми кнопку бинда.");
                        listening = true;
                        name = args[1];
                        command = com;
                    }
                }
                break;
            }
            case "remove" : {
                if (Macros.contains(args[1])) {
                    Macros.remove(args[1]);
                    info(Formatting.WHITE + args[1] + Formatting.YELLOW + " был удален из списка макросов.");
                } else {
                    info(Formatting.WHITE + args[1] + Formatting.RED + " не находиться в списке макросов.");
                }
                break;

            }
            case "list" : {
                if (Macros.macros.isEmpty()) {
                    error(Text.of("Список макросов пуст."));
                } else {
                    info(Text.of(Formatting.AQUA + "Список макросов:"));
                    for (Macro macro : Macros.macros) {
                        String names = macro.name;
                        String keys = InputUtils.getKeyName(macro.button);
                        String command = macro.command;

                        info(Text.of(Formatting.GRAY + "Имя: " + Formatting.WHITE + names + (Formatting.GRAY + " Бинд: " + Formatting.WHITE + keys + (Formatting.GRAY + " Команда: " + Formatting.WHITE + ("/" + command)))));
                    }
                }
                break;
            }
            case "clear" : {
                Macros.clear();
                warning("Список макросов был очищен.");
                break;
            }
            default:
                error();
                break;
        }
    }

    @Override
    public void error() {
        warning("Некорректное использование команды!");
        info(".macro add <имя> <кнопка> <комманда>");
        info(".macro remove <имя>");
        info(".macro clear");
        info(".macro list");
    }
}