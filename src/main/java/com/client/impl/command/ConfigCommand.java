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
import com.client.utils.Utils;
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
                    error(Utils.isRussianLanguage ? "Папка с конфигами не найдена!" : "Config folder doesn't exist!");
                }

                String cfg_load = args[1];

                ConfigSystem.save();

                int load = ConfigSystem.load(cfg_load);

                ConfigSystem.save();

                int load1 = ConfigSystem.load(cfg_load);

                if (load1 == ConfigSystem.SUCCESSFUL_LOAD) {
                    info(Formatting.AQUA + (Utils.isRussianLanguage ? "Конфиг" : "Config") + " (" + Formatting.WHITE + cfg_load + Formatting.AQUA + ") " + (Utils.isRussianLanguage ? "успешно загружен!" : "successful loaded!"));
                } else if (load1 == ConfigSystem.NO_CONFIG_FILE_EXCEPTION) {
                    info(Formatting.RED + (Utils.isRussianLanguage ? "Конфиг" : "Config") + " (" + Formatting.WHITE + cfg_load + Formatting.RED + ") " + (Utils.isRussianLanguage ? "не найден!" : "not found!"));
                }

                break;

            case "save":
                String cfg_name = args[1];

                ConfigSystem.save(cfg_name);
                info(Formatting.AQUA + (Utils.isRussianLanguage ? "Конфиг" : "Config") + " (" + Formatting.WHITE + cfg_name + Formatting.AQUA + ") " + (Utils.isRussianLanguage ? "был сохранен." : "saved."));
                break;
            case "delete":
                String cfg = args[1];
                if (!dir.exists()) {
                    error(Utils.isRussianLanguage ? "Папка с конфигами не найдена!" : "Config folder doesn't exist!");
                }

                File fileToDelete = new File(dir.getAbsolutePath() + "/" + cfg);

                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                    info(Formatting.YELLOW + (Utils.isRussianLanguage ? "Конфиг" : "Config") + " (" + Formatting.WHITE + cfg + Formatting.YELLOW + ") " + (Utils.isRussianLanguage ? "был удален." : "was deleted."));
                } else {
                    info(Formatting.RED + (Utils.isRussianLanguage ? "Конфиг" : "Config") + " (" + Formatting.WHITE + cfg + Formatting.RED + ") " + (Utils.isRussianLanguage ? "не найден!" : "not found."));
                }
                break;

            case "list":
                if (!dir.exists()) {
                    error(Utils.isRussianLanguage ? "Папка с конфигами не найдена!" : "Config folder doesn't exist!");
                }

                List<File> files = List.of(dir.listFiles());

                if (files.isEmpty()) {
                    error(Text.of(Utils.isRussianLanguage ? "Список конфигов пуст." : "Config list is empty."));
                } else {
                    info(Text.of(Formatting.AQUA + (Utils.isRussianLanguage ? "Список конфигов:" : "Config list:")));
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
                warning(Utils.isRussianLanguage ? "Конфиг сброшен." : "Configs was reset!");
                break;
            default:
                error();
                break;
        }
    }

    @Override
    public void error() {
        warning(Utils.isRussianLanguage ? "Некорректное использование команды!" : "Incorrect use of command!");
        info(".cfg load <название>");
        info(".cfg save <название>");
        info(".cfg delete <название>");
        info(".cfg reset");
        info(".cfg list");
    }
}