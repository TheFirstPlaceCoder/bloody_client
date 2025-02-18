package com.client.impl.command;

import com.client.system.command.Command;
import com.client.system.friend.FriendManager;
import com.client.utils.Utils;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StaffCommand extends Command {
    public StaffCommand() {
        super("Staff", List.of("staff"), List.of("add <ник>", "remove <ник>", "clear", "list"));
    }

    public static final List<String> staff = new ArrayList<>();

    public static void load(File file) {
        if (!file.exists()) return;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;

            while ((line = br.readLine()) != null) {
                staff.add(line);
            }

            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void save(File file) {
        if (file.exists()) file.delete();

        try {
            file.createNewFile();

            BufferedWriter br = new BufferedWriter(new FileWriter(file));
            for (String s : staff) {
                br.write(s + "\n");
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void command(String[] args) {
        switch (args[0]) {
            case "add" : {
                if (staff.contains(args[1])) {
                    info(Formatting.WHITE + args[1] + Formatting.RED + (Utils.isRussianLanguage ? " уже есть в списке!" : " already is in Staff list!"));
                } else {
                    staff.add(args[1]);
                    info(Formatting.WHITE + args[1] + Formatting.AQUA + (Utils.isRussianLanguage ? " был добавлен в список!" : " was added to Staff list!"));
                }
                break;
            }
            case "remove" : {
                if (staff.contains(args[1])) {
                    staff.remove(args[1]);
                    info(Formatting.WHITE + args[1] + Formatting.YELLOW + (Utils.isRussianLanguage ? " был удален из списка!" : " was deleted from Staff list!"));
                } else {
                    info(Formatting.WHITE + args[1] + Formatting.RED + (Utils.isRussianLanguage ? " не находиться в списке!" : " is not in Staff list!"));
                }
                break;

            }
            case "list" : {
                if (staff.isEmpty()) {
                    error(Text.of(Utils.isRussianLanguage ? "Список персонала пуст!" : "Staff list is empty!"));
                } else {
                    info(Text.of(Formatting.AQUA + (Utils.isRussianLanguage ? "Список персонала:" : "Staff list:")));
                    for (String name : staff) {
                        info(Text.of(Formatting.WHITE + name));
                    }
                }
                break;
            }
            case "clear" : {
                staff.clear();
                warning(Utils.isRussianLanguage ? "Список персонала был очищен." : "Staff list was cleared.");
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
        info(".staff remove <ник>");
        info(".staff add <ник>");
        info(".staff clear");
        info(".staff list");
    }
}