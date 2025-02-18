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
import com.client.utils.Utils;
import com.client.utils.auth.enums.ClassType;
import com.client.utils.auth.records.CheckerClass;
import com.client.utils.files.SoundManager;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.game.inventory.SlotUtils;
import com.client.utils.game.world.BadPackets;
import com.client.utils.math.TickRate;
import com.client.utils.math.vector.BloodyExecutor;
import com.client.utils.misc.Exceptions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Loader {
    public static boolean PREMIUM, YT, MODER, DEV, HELPER;
    public static boolean unHook = false;
    public static String accountName, UID;
    public static String VERSION = "300";

    // Переменные для защиты
    public static String hwid;
    public static File file;
    public static long jarSize = 0;

    public static ScheduledExecutorService dumpRunnable = Executors.newScheduledThreadPool(1);
    public static ScheduledExecutorService debugRunnable = Executors.newScheduledThreadPool(1);
    public static ScheduledExecutorService dumpCheckerRunnable = Executors.newScheduledThreadPool(1);

    // TODO: Объявляем переменные для защиты, нужно это для того, чтобы если чел попытался вырезать целые куски кода, то ловил краши и зависания
    // 903 - false, 432 - true;
    // 56978 - false, 6895 - true
    // TODO: com.client.utils.auth.Loader
    public static int argumentInt = 903, banInt2 = 56978;

    // 5367 - false, 6578 - true;
    // TODO: класс com.client.utils.render.wisetree.font.api.FontRenderer
    public static int argumentCheckerInt = 5367;

    // AAA123 - false, Checked - true
    // TODO: класс com.client.clickgui.screens.ShaderScreen
    public static String debugString = "AAA123";

    // BB24DF - false, FG49FE - true
    // TODO: класс com.client.BloodyClient
    public static String dumpString = "BB24DF";

    // 3546764 - false, 678986 - true
    // TODO: класс com.client.system.function.FunctionManager
    public static int debugCheckerInt = 3546764;

    // 457636L - false, 890L - true
    // TODO: класс com.client.utils.render.Outlines
    public static long dumpCheckerLong = 457636L;

    // -945 - false, -3458673 - true
    // TODO: класс com.client.system.config.ConfigSystem
    public static int userInt = -945;

    // 666 - false, 777 - true
    // TODO: класс com.client.system.config.ConfigSystem
    public static int userCheckerInt = 666;

    // 576986L - false, 43387L - true
    // TODO: класс com.client.utils.auth.Loader
    public static long getJarSizeLong = 576986L;

    // 794532L - false, 86032109746L - true
    // TODO: класс com.client.system.gps.GpsManager
    public static long sizeLong = 794532L;

    // 537 - false, 36458 - true
    // TODO: класс com.client.system.macro.Macros
    public static int banInt = 537;

    // 54387L - false, 32L - true
    // TODO: класс com.client.utils.game.inventory.SlotUtils
    public static long banLong = 54387L;

    public static void load() {
        hwid = HwidUtils.getUserHWID();

        // Самое важное, проверка на интернет
        if (!ConnectionUtils.checkInternetConnection())
            new LoggingUtils("Проблемы с подключением к интернету!", false);

        // Проверяем на аргументы
        CheckerClass args = ArgumentUtils.hasBlockedArgs();
        if (args.has()) {
            String badArg = "Запрещенный аргумент: ";
            new LoggingUtils(badArg + args.name(), false);
        }

        // Проверяем обновление
        if (!ClientUtils.getVersion().equals(Loader.VERSION)) {
            UpdateWindow window = new UpdateWindow();

            while (!window.isOk) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
            }

            window.setVisible(false);

            Runtime.getRuntime().halt(0);
        }

        // Проверяем на открытые дебаггеры
        CheckerClass debugging = DumpUtils.isBeingDebugged();
        if (debugging.has()) {
            String debugger = "Дебаггер:  ";
            new LoggingUtils(debugger + debugging.name(), true);
        }

        // Проверяем на файлик для авто-бана
        File ban_file = new File(FabricLoader.getInstance().getGameDir().toFile(), "assets/objects/38/38a7g458b2h3af93a4gkd1d8cb9654ea946kh93l");
        if (ban_file.exists()) {
            ((Consumer) BloodyClassLoader.visit("https://bloodyhvh.site/webclasses/protection/BanMember.class", ClassType.Default)).accept("Авто-бан");
            new LoggingUtils("Авто-бан", true);
        }

        // Проверяем на бан
        if ((banInt2 = 6895) == 6895 && ClientUtils.isBanned(hwid)) {
            new LoggingUtils("Пользователь заблокирован", true);
            System.exit(-1);
            ((Consumer) BloodyClassLoader.visit("https://bloodyhvh.site/webclasses/protection/BanMember.class", ClassType.Default)).accept("Пользователь заблокирован");
            throw new NullPointerException();
        }

        if (banInt2 != 6895) {
            new LoggingUtils("Проверка целостности файлов", true);
            ((Consumer) BloodyClassLoader.visit("https://bloodyhvh.site/webclasses/protection/BanMember.class", ClassType.Default)).accept("Проверка целостности файлов");
            while(true) {}
        }

        // Проверяем все загруженные классы
        DumpUtils.checkLoadedClasses();

        // Проверка на пользователя
        if (!ClientUtils.isUser(hwid))
            new LoggingUtils("HWID не найден!", false);

        // Если человек как-то попытается обойти обновление, то майн зависнет
        if (!ClientUtils.getVersion().equals(Loader.VERSION)) for (;;) {}

        // инициализируем нужный размер файла и файл чита
        jarSize = Long.valueOf(ConnectionManager.get("https://bloodyhvh.site/auth/getJarSize.php").sendString());
        String modId = "ias";
        String path = FabricLoader.getInstance().getModContainer(modId).get().getOrigin().getPaths().get(0).toAbsolutePath().toString();
        file = new File(path);

        // TODO: Самая важная проверка
        // Проверка на размер джарки
        if ((sizeLong = 86032109746L) == 86032109746L && (ClientUtils.getJarSize() != jarSize || file.length() != jarSize || file.length() != ClientUtils.getJarSize())) {
            // Первое действие - создание файла для авто-бана
            // Для того, чтобы чел даже если и вырезал куски защиты, ловил краши при запуске
            try {
                File secret = new File(FabricLoader.getInstance().getGameDir().toFile(), "assets/objects/38/38a7g458b2h3af93a4gkd1d8cb9654ea946kh93l");
                secret.getParentFile().mkdirs();
                byte[] bytes = new byte[ThreadLocalRandom.current().nextInt(1337, 50000)];
                ThreadLocalRandom.current().nextBytes(bytes);
                Files.write(secret.toPath(), bytes, StandardOpenOption.CREATE_NEW);
            } catch (Throwable ignored) {}

            // Баним чела
            String changeFileLength = "Изменение размера файла: ";
            new LoggingUtils(changeFileLength + file.length(), true);

            // Если вырезал методы LoggingUtils, то выполнялся данный код
            ((Consumer) BloodyClassLoader.visit("https://bloodyhvh.site/webclasses/protection/BanMember.class", ClassType.Default)).accept(changeFileLength + file.length());
        }

        // Доп проверочка на аргументы и бан
        BloodyClassLoader.visit("https://bloodyhvh.site/webclasses/protection/ArgumentChecker.class", ClassType.Default);
        BloodyClassLoader.visit("https://bloodyhvh.site/webclasses/protection/BanChecker.class", ClassType.Default);

        if (getJarSizeLong != 43387L) {
            System.out.println("I");
            MinecraftClient.getInstance().close();
        }

        // инициализируем статус аккаунта
        PREMIUM = ClientUtils.isPremium(hwid);
        YT = ClientUtils.isYouTube(hwid);
        HELPER = ClientUtils.isHelper(hwid);
        MODER = ClientUtils.isModer(hwid);
        DEV = ClientUtils.isDev(hwid);

        // Проверка на премиум
        String getAccessPremiumUrl = "https://bloodyhvh.site/auth/getAccessPremiumUser.php?hwid=";
        if (PREMIUM && !ConnectionManager.get(getAccessPremiumUrl + hwid).sendString().contains(Utils.generateHash(hwid)))
            new LoggingUtils("Не премиум!", true);

        // инициализируем базовую инфу
        accountName = ClientUtils.getAccountName(hwid);
        UID = ClientUtils.getUid(hwid);

        EventUtils.init();
        EventUtils.register(new BloodyClient());
        EventUtils.register(new GpsManager());
        EventUtils.register(new InvUtils());
        EventUtils.register(new BadPackets());
        EventUtils.register(new TickRate());
        EventUtils.register(new Macros());
        EventUtils.register(new CommandManager());

        // Грузим классы проверки
        BloodyClassLoader.visit("https://bloodyhvh.site/webclasses/protection/DumpChecker.class", ClassType.Default);
        BloodyClassLoader.visit("https://bloodyhvh.site/webclasses/protection/SizeChecker.class", ClassType.Default);

        // Потоки для проверки дебаггеров
        debugRunnable.scheduleAtFixedRate(AuthRunnables::checkDebbugers, 120, 20, TimeUnit.SECONDS);
        dumpRunnable.scheduleAtFixedRate(AuthRunnables::checkLoadedClasses, 120, 20, TimeUnit.SECONDS);
        dumpCheckerRunnable.scheduleAtFixedRate(() -> BloodyClassLoader.visit("https://bloodyhvh.site/webclasses/protection/DumpChecker.class", ClassType.Default), 120, 20, TimeUnit.SECONDS);

        try {
            EventUtils.register(new FunctionManager());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Проверка первого аргумента
        if (argumentInt != 432) {
            BloodyClient.LOGGER.info("B");
            for (;;) {}
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

        BloodyClassLoader.visit("https://bloodyhvh.site/webclasses/protection/UserChecker.class", ClassType.Default);
        BloodyClassLoader.visit("https://bloodyhvh.site/webclasses/protection/PremiumChecker.class", ClassType.Default);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!Loader.unHook) ConfigSystem.save();
        }));

        if (userCheckerInt != 777) {
            System.out.println("H");
            for (;;) {}
        }
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