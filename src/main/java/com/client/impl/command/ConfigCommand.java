package com.client.impl.command;

import com.client.impl.function.client.ClickGui;
import com.client.system.command.Command;
import com.client.system.config.ConfigSystem;
import com.client.system.friend.FriendManager;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.gps.GpsManager;
import com.client.system.hud.HudFunction;
import com.client.system.hud.HudManager;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.manager.SettingManager;
import com.client.system.setting.settings.*;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.theme.ThemeSetting;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.List;

public class ConfigCommand extends Command {
    public ConfigCommand() {
        super("Config", List.of("cfg", "config"), List.of("load <название>", "list", "reset", "save <название>", "delete <название>"));
    }

    @Override
    public void command(String[] args) {
        File dir = new File(ConfigSystem.PATH + "/configs/");
        switch (args[0]) {
            case "load":
                if (!dir.exists()) {
                    error("Папка с конфигами не найдена!");
                }

                String cfg_load = args[1];

                ConfigSystem.save();

                int load = ConfigSystem.load(cfg_load);

                ConfigSystem.save();

                int load1 = ConfigSystem.load(cfg_load);

                if (load1 == ConfigSystem.SUCCESSFUL_LOAD) {
                    info(Formatting.AQUA + "Конфиг (" + Formatting.WHITE + cfg_load + Formatting.AQUA + ") успешно загружен.");
                } else if (load1 == ConfigSystem.NO_CONFIG_FILE_EXCEPTION) {
                    info(Formatting.RED + "Конфиг (" + Formatting.WHITE + cfg_load + Formatting.RED + ") не найден!");
                }

                break;

            case "save":
                String cfg_name = args[1];

                ConfigSystem.save(cfg_name);
                info(Formatting.AQUA + "Конфиг (" + Formatting.WHITE + cfg_name + Formatting.AQUA + ") был сохранен.");
                break;
            case "delete":
                String cfg = args[1];
                if (!dir.exists()) {
                    error("Папка с конфигами не найдена!");
                }

                File fileToDelete = new File(dir.getAbsolutePath() + "/" + cfg);

                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                    info(Formatting.YELLOW + "Конфиг (" + Formatting.WHITE + cfg + Formatting.YELLOW + ") был удален.");
                } else {
                    info(Formatting.RED + "Конфиг (" + Formatting.WHITE + cfg + Formatting.RED + ") не найден!");
                }
                break;

            case "list":
                if (!dir.exists()) {
                    error("Папка конфигов не найдена.");
                }

                List<File> files = List.of(dir.listFiles());

                if (files.isEmpty()) {
                    error(Text.of("Список конфигов пуст."));
                } else {
                    info(Text.of(Formatting.AQUA + "Список конфигов:"));
                    for (File file : files) {
                        info(Text.of(Formatting.WHITE + file.getName()));
                    }
                }
                break;

            case "reset":
                for (AbstractSettings<?> s : SettingManager.getSettingsList()) {
                    switch (s.getType()) {
                        case Keybind:
                            ((KeybindSetting) s).set(((KeybindSetting) s).getDefaultValue());
                            break;
                        case Boolean:
                            ((BooleanSetting) s).set(((BooleanSetting) s).getDefaultValue());
                            break;
                        case Integer:
                            ((IntegerSetting) s).set(((IntegerSetting) s).getDefaultValue());
                            break;
                        case Double:
                            ((DoubleSetting) s).set(((DoubleSetting) s).getDefaultValue());
                            break;
                        case String:
                            ((StringSetting) s).set(((StringSetting) s).getDefaultValue());
                            break;
                        case List:
                            ((ListSetting) s).set(((ListSetting) s).getDefaultValue());
                            break;
                        case MultiBoolean:
                            ((MultiBooleanSetting) s).set(((MultiBooleanSetting) s).getDefaultValue());
                            break;
                        case Color:
                            ((ColorSetting) s).set(((ColorSetting) s).getDefaultValue());
                            break;
                        case Theme:
                            ((ThemeSetting) s).set(((ThemeSetting) s).getDefaultValue());
                            break;
                    }
                }
                for (HudFunction hudFunction : HudManager.getHudFunctions()) {
                    if (hudFunction.isEnabled()) {
                        hudFunction.toggle();
                    }
                    hudFunction.rect = hudFunction.defaultRect;
                }
                for (Function m : FunctionManager.getFunctionList()) {
                    if (m instanceof ClickGui) {
                        m.setKeyCode(GLFW.GLFW_KEY_RIGHT_SHIFT);
                    } else {
                        m.setKeyCode(-1);
                    }
                    if (m.isEnabled()) {
                        m.toggle(false);
                    }
                }
                GpsManager.clear();
                FriendManager.clear();
                warning("Конфиг сброшен.");
                break;
            default:
                error();
                break;
        }
    }

    @Override
    public void error() {
        warning("Некорректное использование команды!");
        info(".cfg load <название>");
        info(".cfg save <название>");
        info(".cfg delete <название>");
        info(".cfg reset");
        info(".cfg list");
    }
}