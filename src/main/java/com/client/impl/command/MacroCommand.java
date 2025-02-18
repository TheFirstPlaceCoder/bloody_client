package com.client.impl.command;

import com.client.event.events.KeyEvent;
import com.client.system.command.Command;
import com.client.system.friend.FriendManager;
import com.client.system.macro.Macro;
import com.client.system.macro.Macros;
import com.client.utils.Utils;
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
        info(Formatting.WHITE + name + Formatting.AQUA + (Utils.isRussianLanguage ? " был добавлен в список макросов." : " was added to Macros list"));
        listening = false;
    }

    @Override
    public void command(String[] args) {
        switch (args[0]) {
            case "add" : {
                if (Macros.contains(args[1])) {
                    info(Formatting.WHITE + args[1] + Formatting.RED + (Utils.isRussianLanguage ? " уже есть в списке макросов!" : " already exists!"));
                } else {
                    String com = Arrays.toString(args).substring(args[0].length() + args[1].length() + 5).replace(",", "").replace("]", "");

                    if (com.isEmpty() || com.isBlank()) {
                        error((Utils.isRussianLanguage ? "Команды для действия пустая" : "Action command is empty!"));
                    } else if (com.contains("/")) {
                        error((Utils.isRussianLanguage ? "Команды для действия нужно вводить без знака " : "Action command must not contains ") + Formatting.WHITE + "/");
                    } else {
                        info(Formatting.AQUA + (Utils.isRussianLanguage ? "Нажми кнопку бинда." : "Press any key."));
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
                    info(Formatting.WHITE + args[1] + Formatting.YELLOW + (Utils.isRussianLanguage ? " был удален из списка макросов!" : " was deleted!"));
                } else {
                    info(Formatting.WHITE + args[1] + Formatting.RED + (Utils.isRussianLanguage ? " не находиться в списке макросов!" : " is not in Macros list!"));
                }
                break;

            }
            case "list" : {
                if (Macros.macros.isEmpty()) {
                    error(Text.of(Utils.isRussianLanguage ? "Список макросов пуст." : "Macros list is empty."));
                } else {
                    info(Text.of(Formatting.AQUA + (Utils.isRussianLanguage ? "Список макросов:" : "Macros list:")));
                    for (Macro macro : Macros.macros) {
                        String names = macro.name;
                        String keys = InputUtils.getKeyName(macro.button);
                        String command = macro.command;

                        info(Text.of(Formatting.GRAY + (Utils.isRussianLanguage ? "Имя: " : "Name: ") + Formatting.WHITE + names + (Formatting.GRAY + (Utils.isRussianLanguage ? " Бинд: " : " Key: ") + Formatting.WHITE + keys + (Formatting.GRAY + (Utils.isRussianLanguage ? " Команда: " : " Action command: ") + Formatting.WHITE + ("/" + command)))));
                    }
                }
                break;
            }
            case "clear" : {
                Macros.clear();
                warning(Utils.isRussianLanguage ? "Список макросов был очищен." : "Macros list was cleared.");
                break;
            }
            default:
                error();
                break;
        }
    }

    @Override
    public void error() {
        warning(Utils.isRussianLanguage ? "Некорректное использование команды!" : "Incorrect use of command!");
        info(Utils.isRussianLanguage ? ".macro add <имя> <кнопка> <комманда>" : ".macro add <name> <key> <action command>");
        info(".macro remove <имя>");
        info(".macro clear");
        info(".macro list");
    }
}