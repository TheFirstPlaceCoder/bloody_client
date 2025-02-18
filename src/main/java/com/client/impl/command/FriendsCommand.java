package com.client.impl.command;

import com.client.system.command.Command;
import com.client.system.friend.FriendManager;
import com.client.system.macro.Macro;
import com.client.system.macro.Macros;
import com.client.utils.Utils;
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
                    info(Formatting.WHITE + args[1] + Formatting.RED + (Utils.isRussianLanguage ? " уже есть в списке друзей!" : " already is in friends list!"));
                } else {
                    FriendManager.add(args[1]);
                    info(Formatting.WHITE + args[1] + Formatting.AQUA + (Utils.isRussianLanguage ? " был добавлен в список друзей!" : " added to friend list!"));
                }
                break;
            }
            case "remove" : {
                if (FriendManager.isFriend(args[1])) {
                    FriendManager.remove(args[1]);
                    info(Formatting.WHITE + args[1] + Formatting.YELLOW + (Utils.isRussianLanguage ? " был удален из списка друзей!" : " was deleted from friend list!"));
                } else {
                    info(Formatting.WHITE + args[1] + Formatting.RED + (Utils.isRussianLanguage ? " не находится в списке друзей!" : " is not in friend list!"));
                }
                break;

            }
            case "list" : {
                if (FriendManager.getFriends().isEmpty()) {
                    error(Text.of(Utils.isRussianLanguage ? "Список друзей пуст." : "Friends list is empty"));
                } else {
                    info(Text.of(Formatting.AQUA + (Utils.isRussianLanguage ? "Список друзей:" : "Friends list:")));
                    for (String name : FriendManager.getFriends()) {
                        info(Text.of(Formatting.WHITE + name));
                    }
                }

                break;
            }
            case "clear" : {
                FriendManager.getFriends().clear();
                warning(Utils.isRussianLanguage ? "Список друзей был очищен." : "Friends list was cleared.");
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
        info(".friends remove <ник>");
        info(".friends add <ник>");
        info(".friends clear");
        info(".friends list");
    }
}
