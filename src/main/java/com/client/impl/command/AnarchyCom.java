package com.client.impl.command;

import com.client.impl.command.rct.RctFunctionClassic;
import com.client.impl.command.rct.RctFunctionLite;
import com.client.system.command.Command;
import com.client.utils.game.entity.ServerUtils;

import java.util.List;

public class AnarchyCom extends Command {
    public AnarchyCom() {
        super("Anarchy", List.of("an", "anarchy"), List.of("anarchy <id>"));
    }

    @Override
    public void command(String[] args) {
        if (!ServerUtils.isHolyWorld()) {
            error("Работает только на HolyWorld!");
            return;
        }

        if (ServerUtils.isPvp()) {
            error("Вы в режиме боя!");
            return;
        }

        if (ServerUtils.getAnarchy().contains("Лайт")) {
            RctFunctionLite.register(Integer.parseInt(args[0]));
        } else {
            RctFunctionClassic.register(Integer.parseInt(args[0]));
        }
    }

    @Override
    public void error() {
        warning(".anarchy <anarchy id>");
    }
}
