package com.client.utils.auth;

import api.main.EventUtils;
import com.client.BloodyClient;
import com.client.alt.Accounts;
import com.client.impl.function.client.UnHook;
import com.client.system.autobuy.AutoBuyManager;
import com.client.system.command.CommandManager;
import com.client.system.config.ConfigSystem;
import com.client.system.function.FunctionManager;
import com.client.system.gps.GpsManager;
import com.client.system.hud.HudManager;
import com.client.system.macro.Macros;
import com.client.system.theme.ThemeManager;
import com.client.utils.files.SoundManager;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.game.inventory.SlotUtils;
import com.client.utils.math.TickRate;
import com.client.utils.math.vector.BloodyExecutor;
import com.client.utils.misc.Exceptions;
import com.client.utils.misc.FunctionUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import sun.misc.Unsafe;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.stream.Stream;

import static com.client.BloodyClient.mc;

public class Loader {
    public static boolean PREMIUM, YT, MODER, DEV, HELPER;
    public static boolean unHook = false;
    public static String accountName, UID;
    public static String VERSION = "201";

    public static void load() {
        PREMIUM = true;
        YT = false;
        HELPER = false;
        MODER = false;
        DEV = true;

        accountName = "Bloody";
        UID = "1";

        EventUtils.init();
        EventUtils.register(new BloodyClient());
        EventUtils.register(new GpsManager());
        EventUtils.register(new InvUtils());
        EventUtils.register(new TickRate());
        EventUtils.register(new Macros());
        EventUtils.register(new CommandManager());
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

    public static boolean isHelper() {
        return HELPER;
    }

    public static boolean isModer() {
        return MODER;
    }

    public static boolean isDev() {
        return DEV;
    }
}