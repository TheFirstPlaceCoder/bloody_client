package com.client.impl.command;

import com.client.system.command.Command;
import com.client.system.friend.FriendManager;
import com.client.system.gps.GpsManager;
import com.client.system.gps.GpsPoint;
import com.client.utils.Utils;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class GpsCommand extends Command {
    public GpsCommand() {
        super("GPS", List.of("gps"), List.of("add <x, y / ~, z> <название>", "add <x, z> <название>", "add <x, y / ~, z>", "add <x, z>", "remove <название>", "clear", "list"));
    }

    @Override
    public void command(String[] args) {
        switch (args[0]) {
            case "clear":
                GpsManager.clear();
                warning(Utils.isRussianLanguage ? "Список GPS был очищен!" : "GPS list was cleared!");
                break;
            case "remove":
                String gps_name = args[1];

                if (GpsManager.remove(gps_name)) {
                    info(Formatting.WHITE + gps_name + Formatting.YELLOW + (Utils.isRussianLanguage ? " был(а) удален(а)." : " was deleted."));
                } else {
                    info(Formatting.WHITE + gps_name + Formatting.RED + (Utils.isRussianLanguage ? " не находится в списке." : " doesn't exist."));
                }
                break;
            case "list":
                if (GpsManager.get().isEmpty()) {
                    error(Text.of(Utils.isRussianLanguage ? "Список GPS пуст." : "GPS list is empty."));
                } else {
                    info(Text.of(Formatting.AQUA + (Utils.isRussianLanguage ? "Список GPS:" : "GPS list:")));
                    for (GpsPoint gps : GpsManager.get()) {
                        info(Text.of(Formatting.GRAY + (Utils.isRussianLanguage ? "Имя: " : "Name:") + Formatting.WHITE + gps.name + (Formatting.GRAY + (Utils.isRussianLanguage ? " Координаты: " : " Coords: ") + "(" + Formatting.WHITE + (gps.x + " " + gps.y + " " + gps.z) + Formatting.GRAY + ")")));
                    }
                }
                break;
            case "add":
                int x, y, z;
                String name;

                x = Integer.parseInt(args[1]);

                if (args.length == 5) {
                    y = args[2].equals("~") ? (int) mc.player.getY() : Integer.parseInt(args[2]);
                    z = Integer.parseInt(args[3]);
                    name = args[4];

                    if (GpsManager.add(new GpsPoint(x, y, z, name))) {
                        info(Formatting.AQUA + (Utils.isRussianLanguage ? "Новый GPS (" : "New GPS (") + Formatting.WHITE + name + Formatting.AQUA + (Utils.isRussianLanguage ? ") был добавлен!" : ") added to GPS list!"));
                    } else {
                        error(Formatting.RED + "GPS (" + Formatting.WHITE + name + Formatting.RED + (Utils.isRussianLanguage ? ") уже существует!" : " already exists!"));
                    }
                } else if (args.length == 4) {
                    try {
                        y = args[2].equals("~") ? (int) mc.player.getY() : Integer.parseInt(args[2]);
                        z = Integer.parseInt(args[3]);
                        name = (Utils.isRussianLanguage ? "Новая точка " : "New GPS ") + (GpsManager.get().size() + 1);
                    } catch (Exception e) {
                        y = 100;
                        z = Integer.parseInt(args[2]);
                        name = args[3];
                    }

                    if (GpsManager.add(new GpsPoint(x, y, z, name))) {
                        info(Formatting.AQUA + (Utils.isRussianLanguage ? "Новый GPS (" : "New GPS (") + Formatting.WHITE + name + Formatting.AQUA + (Utils.isRussianLanguage ? ") был добавлен!" : ") added to GPS list!"));
                    } else {
                        info(Formatting.RED + "GPS (" + Formatting.WHITE + name + Formatting.RED + (Utils.isRussianLanguage ? ") уже существует!" : " already exists!"));
                    }
                } else if (args.length == 3) {
                    z = Integer.parseInt(args[2]);

                    if (GpsManager.add(new GpsPoint(x, 100, z, (Utils.isRussianLanguage ? "Новая точка " : "New GPS ") + (GpsManager.get().size() + 1)))) {
                        info(Formatting.AQUA + (Utils.isRussianLanguage ? "Новый GPS был добавлен." : "New GPS added!"));
                    } else {
                        error(Utils.isRussianLanguage ? "GPS уже существует!" : "GPS is already exist!");
                    }
                }
                break;
            default:
                error();
                break;
        }
    }

    @Override
    public void error() {
        warning(Utils.isRussianLanguage ? "Некорректное использование команды!" : "Incorrect use of command!");
        info(".gps add <x, y / ~, z> <название>");
        info(".gps add <x, z> <название>");
        info(".gps add <x, y / ~, z>");
        info(".gps add <x, z>");
        info(".gps remove <название>");
        info(".gps clear");
        info(".gps hide");
    }
}
