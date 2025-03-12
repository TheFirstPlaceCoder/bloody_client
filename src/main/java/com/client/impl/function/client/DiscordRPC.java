package com.client.impl.function.client;

import api.interfaces.EventHandler;
import com.client.event.events.TickEvent;
import com.client.system.discord.main.DiscordIPC;
import com.client.system.discord.main.RichPresence;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.auth.Loader;
import com.client.utils.game.entity.ServerUtils;

import java.util.List;

public class DiscordRPC extends Function {
    public DiscordRPC() {
        super("Discord RPC", Category.CLIENT);
    }

    public final RichPresence rpc = new RichPresence();

    public boolean init;

    public void setup() {
        if (init) return;
        DiscordIPC.start(1342961141973913731L, null);
        rpc.setStart(System.currentTimeMillis() / 1000L);
        rpc.setLargeImage("https://media1.tenor.com/m/yn3cIsxxv6EAAAAC/bloody.gif", "v3.1");
        rpc.setSmallImage("player_head", Loader.getAccountName());
        rpc.setDetails("UID >> " + Loader.getUID());
        rpc.setState("Build >> " + Loader.RPC_VERSION);
        rpc.addButton("Купить", "https://bloodyhvh.site/");
        rpc.addButton("Телеграм", "https://t.me/bloody_hvh");
        DiscordIPC.setActivity(rpc);
        init = true;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        setup();

        String ip = ServerUtils.getIp();
        if (!ip.isEmpty()) {
            if (ip.contains("holyworld")) {
                rpc.setSmallImage("hw", "Играет на HolyWorld");
            } else if (ip.contains("reallyworld")) {
                rpc.setSmallImage("rw", "Играет на ReallyWorld");
            } else if (ip.contains("funtime")) {
                rpc.setSmallImage("ft", "Играет на FunTime");
            } else {
                rpc.setSmallImage("player_head", Loader.getAccountName());
            }
        } else {
            rpc.setSmallImage("player_head", Loader.getAccountName());
        }

        DiscordIPC.setActivity(rpc);
    }

    @Override
    public void onDisable() {
        DiscordIPC.stop();
        init = false;
    }
}
