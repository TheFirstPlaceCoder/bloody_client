package com.client.impl.command;

import com.client.system.command.Command;
import com.client.system.friend.FriendManager;
import com.client.system.macro.Macro;
import com.client.system.macro.Macros;
import com.client.utils.misc.InputUtils;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class FriendsCommand extends Command {
    public FriendsCommand() {
        super("Friends", List.of("friends"), List.of("add <ник>", "remove <ник>", "clear", "list"));
    }

    @Override
    public void command(String[] args) {
        switch (args[0]) {
            case "add" : {
                if (FriendManager.isFriend(args[1])) {
                    info(Formatting.WHITE + args[1] + Formatting.RED + " уже есть в списке друзей.");
                } else {
                    FriendManager.add(args[1]);
                    info(Formatting.WHITE + args[1] + Formatting.AQUA + " был добавлен в список друзей.");
                }
                break;
            }
            case "remove" : {
                if (FriendManager.isFriend(args[1])) {
                    FriendManager.remove(args[1]);
                    info(Formatting.WHITE + args[1] + Formatting.YELLOW + " был удален из списка друзей.");
                } else {
                    info(Formatting.WHITE + args[1] + Formatting.RED + " не находится в списке друзей.");
                }
                break;

            }
            case "list" : {
                if (FriendManager.getFriends().isEmpty()) {
                    error(Text.of("Список друзей пуст."));
                } else {
                    info(Text.of(Formatting.AQUA + "Список друзей:"));
                    for (String name : FriendManager.getFriends()) {
                        info(Text.of(Formatting.WHITE + name));
                    }
                }

                break;
            }
            case "clear" : {
                FriendManager.getFriends().clear();
                warning("Список друзей был очищен.");
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
        info(".friends remove <ник>");
        info(".friends add <ник>");
        info(".friends clear");
        info(".friends list");
    }
}
