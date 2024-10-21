package com.client.impl.command.rct;

import com.client.system.command.Command;
import com.client.utils.game.entity.ServerUtils;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;

import java.util.List;

public class RctCom extends Command {
    public RctCom() {
        super("Rct", List.of("rct"), List.of());
    }

    @Override
    public void command(String[] args) {
        if (ServerUtils.isPvp()) {
            error("вы находитесь в режиме боя!");
        } else {
            if (ServerUtils.isHolyWorld()) {
                if (ServerUtils.getAnarchy().contains("Лайт")) {
                    RctFunctionLite.register();
                } else {
                    RctFunctionClassic.register();
                }
            } else if (ServerUtils.isFuntime()) {
                String anca = "";
                for (ScoreboardObjective team : mc.world.getScoreboard().getObjectives()) {
                    String an = team.getDisplayName().getString();
                    if (an.contains("Анархия-")) {
                        anca = an.split("Анархия-")[1];
                        mc.player.sendChatMessage("/hub");
                        break;
                    }
                }
                mc.player.sendChatMessage("/an" + anca);
                String finalAnca = anca;
                new Thread(() -> {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mc.player.sendChatMessage("/an" + finalAnca);
                }).start();
            } else error();
        }
    }

    @Override
    public void error() {
        warning(".rct работает только на HolyWorld и FunTime");
    }
}
