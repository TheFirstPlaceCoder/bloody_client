package com.client.impl.function.client;

import api.interfaces.EventHandler;
import com.client.event.events.TickEvent;
import com.client.system.discord.main.DiscordIPC;
import com.client.system.discord.main.RichPresence;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.auth.Loader;
import com.client.utils.game.entity.ServerUtils;

import java.util.List;

public class DiscordRPC extends Function {
    public DiscordRPC() {
        super("Discord RPC", Category.CLIENT);
    }

    public final MultiBooleanSetting draw = MultiBoolean().name("Отображать").defaultValue(List.of(
            new MultiBooleanValue(true, "Имя"),
            new MultiBooleanValue(true, "Uid"),
            new MultiBooleanValue(true, "Группа")
    )).build();

    public final RichPresence rpc = new RichPresence();

    public boolean init;

    public void setup() {
        if (init) return;
        DiscordIPC.start(1171914748426715226L, null);
        rpc.setStart(System.currentTimeMillis() / 1000L);
        rpc.setLargeImage("bloodylogo", "discord.gg/5BbFYMRZfw");
        rpc.setDetails("user: ".concat(Loader.getAccountName()).concat(" | uid: ").concat(Loader.getUID()));
        DiscordIPC.setActivity(rpc);
        init = true;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        setup();


        String details = "";
        if (draw.get(0)) {
            details = details.concat("name: ".concat(Loader.getAccountName()));
        }
        if (draw.get(1)) {
            if (draw.get(0)) {
                details = details.concat(" | uid: ".concat(Loader.getUID()));
            } else {
                details = details.concat("uid: ".concat(Loader.getAccountName()));
            }
        }
        if (details.isEmpty())
            details = "Created by Artik & __aaa__";
        rpc.setDetails(details);
        if (draw.get(2)) {
            String group = "user";
            if (Loader.isPremium()) {
                group = "premium";
            }
            if (Loader.isHelper()) {
                group = "helper";
            }
            if (Loader.isModer()) {
                group = "moder";
            }
            if (Loader.isYouTube()) {
                group = "youtube";
            }
            if (Loader.isDev()) {
                group = "dev";
            }
            rpc.setState("group: ".concat(group));
        } else {
            rpc.setState("   ");
        }

        String ip = ServerUtils.getIp();
        if (!ip.isEmpty()) {
            if (ip.contains("holyworld")) {
                rpc.setSmallImage("hw", ip);
            } else if (ip.contains("reallyworld")) {
                rpc.setSmallImage("rw", ip);
            } else if (ip.contains("funtime")) {
                rpc.setSmallImage("ft", ip);
            } else {
                rpc.setSmallImage("black", ip);
            }
        } else {
            rpc.setSmallImage("black", "idle");
        }

        DiscordIPC.setActivity(rpc);
    }

    @Override
    public void onDisable() {
        DiscordIPC.stop();
        init = false;
    }
}
