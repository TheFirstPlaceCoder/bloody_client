package com.client.utils.auth;

import api.main.EventUtils;
import com.client.BloodyClient;
import com.client.system.autobuy.AutoBuyManager;
import com.client.system.command.CommandManager;
import com.client.system.config.ConfigSystem;
import com.client.system.function.FunctionManager;
import com.client.system.gps.GpsManager;
import com.client.system.hud.HudManager;
import com.client.system.macro.Macros;
import com.client.system.theme.ThemeManager;
import com.client.utils.files.SoundManager;
import com.client.utils.game.inventory.CooldownManager;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.game.inventory.SlotUtils;
import com.client.utils.game.world.BadPackets;
import com.client.utils.math.TickRate;
import com.client.utils.math.vector.BloodyExecutor;
import com.client.utils.misc.Exceptions;

public class Loader {
    public static boolean PREMIUM = true, YT, ADMIN, DEV = true;
    public static boolean unHook = false;
    public static String accountName, UID;
    public static String RPC_VERSION = "v3.1 (1.16.5)";

    public static void load() {
        accountName = "Artik";
        UID = "1";

        EventUtils.init();
        EventUtils.register(new BloodyClient());
        EventUtils.register(new GpsManager());
        EventUtils.register(new InvUtils());
        EventUtils.register(new BadPackets());
        EventUtils.register(new TickRate());
        EventUtils.register(new Macros());
        EventUtils.register(new CommandManager());
        EventUtils.register(new CooldownManager());

        try {
            EventUtils.register(new FunctionManager());
        } catch (Exception e) {
            e.printStackTrace();
        }

        AutoBuyManager.init();
        BloodyExecutor.init();
        SoundManager.init();
        SlotUtils.init();
        ThemeManager.init();
        FunctionManager.init();
        CommandManager.init();
        try {
            HudManager.init();
        } catch (Exception e) {
            Exceptions.printError(e);
        }
        try {
            ConfigSystem.init();
        } catch (Exception e) {
            Exceptions.printError(e);
        }
        try {
            ConfigSystem.load();
        } catch (Exception e) {
            Exceptions.printError(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!Loader.unHook) ConfigSystem.save();
        }));
    }

    public static String getAccountName() {
        return accountName;
    }

    public static String getUID() {
        return UID;
    }

    public static boolean isPremium() {
        return PREMIUM;
    }

    public static boolean isYouTube() {
        return YT;
    }

    public static boolean isAdmin() {
        return ADMIN;
    }

    public static boolean isDev() {
        return DEV;
    }
}