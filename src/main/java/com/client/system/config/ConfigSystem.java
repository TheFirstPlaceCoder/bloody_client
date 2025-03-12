package com.client.system.config;

import com.client.BloodyClient;
import com.client.alt.Accounts;
import com.client.impl.command.StaffCommand;
import com.client.system.autobuy.AutoBuyManager;
import com.client.system.cheststealer.ChestStealerManager;
import com.client.system.friend.FriendManager;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.hud.HudFunction;
import com.client.system.hud.HudManager;
import com.client.system.macro.Macros;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.api.SettingsType;
import com.client.system.setting.manager.SettingManager;
import com.client.system.setting.settings.*;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.system.setting.settings.theme.ThemeSetting;
import com.client.utils.auth.Loader;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConfigSystem {
    public static String PATH;

    public static final int SUCCESSFUL_LOAD = 0;
    public static final int SUCCESSFUL_SAVE = 1;
    public static final int NO_CONFIG_FILE_EXCEPTION = 2;

    public static void init() {
        File file = BloodyClient.FOLDER;
        PATH = file.getPath();

        if (!Loader.unHook && !file.exists()) file.mkdir();
        if (!Loader.unHook && !new File(PATH + "/configs").exists()) new File(PATH + "/configs").mkdir();

        if (!Loader.unHook && !file.exists()) file.mkdir();
        if (!Loader.unHook && !new File(PATH + "/music").exists()) new File(PATH + "/music").mkdir();
    }

    public static void renameFolder(boolean enabled) {
        if (enabled) BloodyClient.FOLDER.renameTo(BloodyClient.UNHOOK_FOLDER);
        else BloodyClient.UNHOOK_FOLDER.renameTo(BloodyClient.FOLDER);
    }

    public static int load() {
        return load("lastcfg");
    }

    public static int load(String name) {
        File file = new File(PATH + (name.equals("lastcfg") ? "/lastcfg" : "/configs/" + name));
        if (!file.exists()) {
            return NO_CONFIG_FILE_EXCEPTION;

        }

        if (name.equals("lastcfg")) {
            StaffCommand.load(new File(PATH + "/staff"));
            Accounts.load(new File(PATH + "/alts"));
            AutoBuyManager.load(new File(PATH + "/a_buy"));
        }

        try {
            List<String> strings = new ArrayList<>();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String temp;
            while ((temp = bufferedReader.readLine()) != null) {
                strings.add(temp);
            }
            bufferedReader.close();

            for (HudFunction hudFunction : HudManager.getHudFunctions()) {
                for (String s : getBlock(strings, hudFunction.getName())) {
                    String f = "";
                    String value = "";
                    try {
                        f = s.split(":")[0];
                        value = s.split(":")[1];
                    } catch (Exception ignore) {
                    }

                    if (f.isEmpty() || value.isEmpty()) continue;

                    if (f.equals("enable")) {
                        if (Boolean.parseBoolean(value) || (hudFunction.isEnabled() && !Boolean.parseBoolean(value))) {
                            hudFunction.toggle();
                        }
                        continue;
                    }

                    if (f.equals("pos") && hudFunction.draggable) {
                        hudFunction.rect.setX((float)(int)Float.parseFloat(value.split(",")[0]));
                        hudFunction.rect.setY((float)(int)Float.parseFloat(value.split(",")[1]));
                    }
                }
            }

            for (Function function : FunctionManager.getFunctionList()) {
                for (String s : getBlock(strings, function.getName())) {
                    String cfgName = "";
                    String value = "";
                    try {
                        cfgName = s.split(":")[0];
                        value = s.split(":")[1];
                    } catch (Exception ignored) {
                    }

                    if (cfgName.isEmpty() || value.isEmpty()) continue;

                    if (cfgName.equals("enable") && !function.getName().equals("Music")) {
                        if (function.isPremium() && !Loader.isPremium()) {
                            if (function.isEnabled()) function.toggle(false);
                            continue;
                        }

                        if ((!function.isEnabled() && value.contains("true")) || (function.isEnabled() && !value.contains("true"))) {
                            function.toggle(false);
                        }

                        continue;
                    }

                    if (cfgName.equals("keybind")) {
                        function.setKeyCode(Integer.parseInt(value));
                        continue;
                    }

                    for (AbstractSettings<?> setting : SettingManager.getSettingsList(function)) {
                        if (!setting.getName().equals(cfgName)) continue;
                        String finalValue = value;
                        switch (setting.getType()) {
                            case Theme -> {
                                try {
                                    ((ThemeSetting) setting).set(((ThemeSetting) setting).getList().stream().filter(a -> a.name().equals(finalValue)).toList().get(0));
                                } catch (Exception e) {
                                    ((ThemeSetting) setting).set(((ThemeSetting) setting).getList().get(0));
                                }
                            }
                            case Color -> {
                                try {
                                    ((ColorSetting) setting).set(new Color(toRGBAR(Integer.parseInt(value)), toRGBAG(Integer.parseInt(value)), toRGBAB(Integer.parseInt(value)), toRGBAA(Integer.parseInt(value))));
                                } catch (Exception e) {
                                    ((ColorSetting) setting).set(((ColorSetting) setting).getDefaultValue());
                                }
                            }
                            case Boolean -> {
                                try {
                                    ((BooleanSetting) setting).set(Boolean.parseBoolean(value));
                                } catch (Exception e) {
                                    ((BooleanSetting) setting).set(((BooleanSetting) setting).getDefaultValue());
                                }
                            }
                            case Integer -> {
                                try {
                                    ((IntegerSetting) setting).set(MathHelper.clamp(Integer.parseInt(value), ((IntegerSetting) setting).getMin(), ((IntegerSetting) setting).getMax()));
                                } catch (Exception e) {
                                    ((IntegerSetting) setting).set(((IntegerSetting) setting).getDefaultValue());
                                }
                            }
                            case Double -> {
                                try {
                                    ((DoubleSetting) setting).set(MathHelper.clamp(Double.parseDouble(value), ((DoubleSetting) setting).getMin(), ((DoubleSetting) setting).getMax()));
                                } catch (Exception e) {
                                    ((DoubleSetting) setting).set(((DoubleSetting) setting).getDefaultValue());
                                }
                            }
                            case String -> {
                                try {
                                    ((StringSetting) setting).set(value);
                                } catch (Exception e) {
                                    ((StringSetting) setting).set(((StringSetting) setting).getDefaultValue());
                                }
                            }
                            case Keybind -> {
                                try {
                                    ((KeybindSetting) setting).set(Integer.parseInt(value));
                                } catch (Exception e) {
                                    ((KeybindSetting) setting).set(((KeybindSetting) setting).getDefaultValue());
                                }
                            }
                            case List -> {
                                try {
                                    ((ListSetting) setting).set(value);
                                } catch (Exception e) {
                                    ((ListSetting) setting).set(((ListSetting) setting).getList().get(0));
                                }
                            }
                            case MultiBoolean -> {
                                List<Pair<String, Boolean>> flags = new ArrayList<>();
                                StringBuilder tempName = new StringBuilder();
                                StringBuilder tempValue = new StringBuilder();
                                int t = 0;
                                for (char c : value.toCharArray()) {
                                    if (c == '|') {
                                        flags.add(new Pair<>(tempName.toString(), Boolean.parseBoolean(tempValue.toString())));
                                        tempName = new StringBuilder();
                                        tempValue = new StringBuilder();
                                        t = 0;
                                        continue;
                                    }
                                    if (t > 0) {
                                        tempValue.append(c);
                                    } else {
                                        if (c == ',') {
                                            t++;
                                            continue;
                                        }
                                        tempName.append(c);
                                    }
                                }
                                for (MultiBooleanValue multiBooleanValue : ((MultiBooleanSetting) setting).get()) {
                                    for (Pair<String, Boolean> flag : flags) {
                                        if (multiBooleanValue.getName().equals(flag.getLeft())) {
                                            multiBooleanValue.setValue(flag.getRight());
                                            break;
                                        }
                                    }
                                }
                            }
                            case Widget, EMPTY -> {
                                continue;
                            }
                        }
                    }
                }
            }

            Macros.load(strings);
            ChestStealerManager.load(strings);
            FriendManager.load(strings);

        } catch (IOException ignore) {
        }

        return SUCCESSFUL_LOAD;
    }

    public static List<String> getBlock(List<String> strings, String target) {
        List<String> l = new ArrayList<>();
        boolean flag = false;

        for (int i = 0; i < strings.size(); i++) {
            String string = strings.get(i);

            if (flag) {
                if (string.contains("}")) {
                    break;
                } else {
                    // Проверяем следующую строку
                    if (i + 1 < strings.size()) {
                        String nextString = strings.get(i + 1);
                        // Проверяем наличие символа ':' в текущей строке
                        if (!string.contains(":")) {
                            string += nextString; // Добавляем следующую строку
                            i++; // Пропускаем следующую строку
                        }
                    }
                    l.add(string);
                }
            } else {
                if (string.startsWith(target) && string.endsWith("{")) {
                    flag = true;
                }
            }
        }

        return l;
    }

    public static int save() {
        return save("lastcfg");
    }

    public static int save(String name) {
        File file = new File(PATH + (name.equals("lastcfg") ? "/lastcfg" : "/configs/" + name));

        if (file.exists()) file.delete();

        try {
            StaffCommand.save(new File(PATH + "/staff"));
            Accounts.save(new File(PATH + "/alts"));
            AutoBuyManager.save(new File(PATH + "/a_buy"));

            file.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            for (Function function : FunctionManager.getFunctionList()) {
                bufferedWriter.write(function.getName() + " {\n");
                bufferedWriter.write("enable:" + function.isEnabled() + "\n");
                bufferedWriter.write("keybind:" + function.getKeyCode() + "\n");

                for (AbstractSettings<?> abstractSettings : SettingManager.getSettingsList(function)) {
                    if (abstractSettings.getType() != SettingsType.EMPTY && abstractSettings.getType() != SettingsType.Widget) bufferedWriter.write(abstractSettings.toConfig().replace("\n", "") + "\n");
                }

                bufferedWriter.write("}\n");
            }
            for (HudFunction hudFunction : HudManager.getHudFunctions()) {
                bufferedWriter.write(hudFunction.getName() + "{\n");
                bufferedWriter.write("enable:" + hudFunction.isEnabled() + "\n");
                bufferedWriter.write(hudFunction + "\n");
                bufferedWriter.write("}\n");
            }

            Macros.save(bufferedWriter);
            ChestStealerManager.save(bufferedWriter);
            FriendManager.save(bufferedWriter);
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException ignore) {
        }

        return SUCCESSFUL_SAVE;
    }

    public static int fromRGBA(Color color) {
        return (color.getRed() << 16) + (color.getGreen() << 8) + (color.getBlue()) + (color.getAlpha() << 24);
    }

    public static int toRGBAR(int color) {
        return (color >> 16) & 0x000000FF;
    }

    public static int toRGBAG(int color) {
        return (color >> 8) & 0x000000FF;
    }

    public static int toRGBAB(int color) {
        return (color) & 0x000000FF;
    }

    public static int toRGBAA(int color) {
        return (color >> 24) & 0x000000FF;
    }
}