package com.client.utils.changelog;

import com.client.utils.auth.Loader;
import com.client.utils.color.ColorTransfusion;
import com.client.utils.math.Pair;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ChangeLog {
    private static final HashMap<Pair<String, String>, List<Field>> CHANGES_LIST = new HashMap<>();

    public static String CURRENT_VIEW;
    public static String CURRENT_TITLE;

    public static List<Field> changesList() {
        List<Field> fields = new ArrayList<>();
        for (Pair<String, String> stringStringPair : CHANGES_LIST.keySet()) {
            if (stringStringPair.getA().equals(CURRENT_VIEW) && stringStringPair.getB().equals(CURRENT_TITLE)) {
                fields = CHANGES_LIST.get(stringStringPair);
                break;
            }
        }
        return fields;
    }

    private static final int MAX_INDEX = 12;
    private static final int MIN_INDEX = 0;
    private static int INDEX = 0;

    private static FloatRect data;

    public static void init() {
        for (int i = 0; i <= 6; i++) {
            fill(i);
        }
        setupChangeLogV7();
        setupChangeLogV8();
        fill(9);
        setupChangeLogV20();
        setupChangeLogV21();
        setupChangeLogV22();
    }

    private static void fill(int i) {
        CHANGES_LIST.put(new Pair<>("v1.0." + i, "Вы можете узнать больше в дискорде чита!"), new ArrayList<>());
        CURRENT_VIEW = "v1.0."  + i;
        CURRENT_TITLE = "Вы можете узнать больше в дискорде чита!";
        INDEX = i;

        added("");
        rewritten("");
        deleted("");
        corrected("");

        if (changesList().isEmpty()) return;

        changesList().sort(Comparator.comparing(c -> -IFont.getWidth(IFont.MONTSERRAT_BOLD, c.mode.prefix + c.text.getA(), 10)));
        changesList().sort(Comparator.comparing(c -> c.mode.id));
    }

    private static void setupChangeLogV7() {
        CHANGES_LIST.put(new Pair<>("v1.0.7", "Combat Update"), new ArrayList<>());
        CURRENT_VIEW = "v1.0.7";
        CURRENT_TITLE = "Combat Update";
        INDEX = 7;

        added("GPS Module");
        added("Anti Server RP");
        added("Un Hook");
        added("Kill Sound");
        added("Hit Sound");
        added("Ambience");
        added("Block Outline");
        added("Velocity | Режим HolyWorld");
        added("Water Speed | Режим HolyWorld Boost");
        added(".report <сообщение>");
        added(".login <ник>");
        added(".macro <имя> <кнопка> <комманда>");

        rewritten("No Render | Исправлен баг тумана из-за которого рендер мира откисал");
        rewritten("Auto Totem | Пофикшен");
        rewritten("GPS | Добавил рендер текстуры и имени");
        rewritten("Jump Cirlce | Новая текстура");
        rewritten("Name Protect | Возможность ставить свое имя");
        rewritten("Configs | Исправил баги с загрузкой/сохранением конфигов");
        rewritten("Water Speed | Добавил автоматическое ускорение в режиме HolyWorld");
        rewritten("No Interact | Не работал");
        rewritten("Arrows, ESP, Shaders | Убраны фильтры на ботов, монстров и животных");

        deleted("Time Changer -> Ambience");
        deleted("Liquid Movement");

        if (changesList().isEmpty()) return;

        changesList().sort(Comparator.comparing(c -> -IFont.getWidth(IFont.MONTSERRAT_BOLD, c.mode.prefix + c.text.getA(), 10)));
        changesList().sort(Comparator.comparing(c -> c.mode.id));
    }

    private static void setupChangeLogV8() {
        CHANGES_LIST.put(new Pair<>("v1.0.8", "Render Update"), new ArrayList<>());
        CURRENT_VIEW = "v1.0.8";
        CURRENT_TITLE = "Render Update";
        INDEX = 8;

        added("Новый HUD");
        added("Ghost Hand");
        added("Packet Mine (Работает везде и не банится)");
        added("Chams");
        added("Anti Vanish");
        added("FOV");
        added("Change Log");
        added("Break Indicators");
        added("Tracers");
        added("Camera Tweaks");
        added("Attack Aura | Дистанция преследования");
        added("Attack Aura | Элитра таргет");
        added("Ambience | Кастомные партиклы тотема");

        rewritten("China Hat | Фильтр для отображения");
        rewritten("Jump Circle | Фильтр для отображения");
        rewritten("Helper | Чекбокс на уведомления");
        rewritten("Config System | Исправлен баг с несохранением прозрачности в цвете");
        rewritten("Speed | Убра флаги режима HolyWorld");
        rewritten("Ambience | Изменил расплывчатость тумана");
        rewritten("Un Hook | Теперь даже при перезапуске майна чит нельзя обнаружить");
        rewritten("Click GUI | Новые темы");
        rewritten("Speed | Исправил краш");
        rewritten("Attack Aura | Исправил краш");

        deleted("Ambience | Настройка цвета от темы");
        deleted("Liquid Movement");

        if (changesList().isEmpty()) return;

        changesList().sort(Comparator.comparing(c -> -IFont.getWidth(IFont.MONTSERRAT_BOLD, c.mode.prefix + c.text.getA(), 10)));
        changesList().sort(Comparator.comparing(c -> c.mode.id));
    }

    private static void setupChangeLogV20() {
        CHANGES_LIST.put(new Pair<>("v2.0.0", "Client Update"), new ArrayList<>());
        CURRENT_VIEW = "v2.0.0";
        CURRENT_TITLE = "Client Update";
        INDEX = 10;

        added("Новое GUI");
        added("Новый HUD");
        added("Новое Main Menu");
        added("Альт менеджер");
        added("Новые чат уведомления");
        added("Legit Aura");
        added("Elytra Bounce");
        added("Elytra Fly | Работает везде, бесконечно и без фейерверков");
        added("Jesus | Для ФТ");
        added("No Fall");
        added("No Hunger");
        added("Camera Tweaks");
        added("Crosshair");
        added("HUE");
        added("Hands");
        added("Hit Bubbles");
        added("Auto Buy");
        added("Music | Модуль + Худ");
        added("Optimization");
        added("No Slow | Режим FunTime Water");
        added("Speed | Режим FunTime 2");
        added("Trails | Режимы Старый и Новый");
        added(".clear — очищает чат");

        rewritten("Commands | Переписаны полностью");
        rewritten("Новые макросы");
        rewritten("Un Hook | Переписан полность + убраны краши");
        rewritten("Attack Aura | Режим HW и FT");
        rewritten("Auto Armor | Фикс для HW и FT");
        rewritten("Auto Swap | Фикс для HW и FT");
        rewritten("Auto Totem | Фикс для HW и FT");
        rewritten("Helper | Фикс для HW и FT");
        rewritten("Auto Myst | Переписан полностью");
        rewritten("China Hat | Оптимизирован");
        rewritten("Jump Circle | Переписан полностью");
        rewritten("Particles | Оптимизирован");
        rewritten("Shaders | Переписан полностью");
        rewritten("Tags | Оптимизирован + уменьшен");
        rewritten("Trails | Оптимизирован");
        rewritten("Click Gui | Новые настройки");
        rewritten("Hud | Новые настройки");
        rewritten("Notifications | Новые настройки");

        deleted("Water Speed | Режим HolyWorld");

        if (changesList().isEmpty()) return;

        changesList().sort(Comparator.comparing(c -> -IFont.getWidth(IFont.MONTSERRAT_BOLD, c.mode.prefix + c.text.getA(), 10)));
        changesList().sort(Comparator.comparing(c -> c.mode.id));
    }

    private static void setupChangeLogV21() {
        CHANGES_LIST.put(new Pair<>("v2.0.1", "FunTime Update"), new ArrayList<>());
        CURRENT_VIEW = "v2.0.1";
        CURRENT_TITLE = "FunTime Update";
        INDEX = 11;

        added("Client Installer");
        added("Companion");
        added("Casino");
        added("Speed Mine");
        added("Nuker");
        added("Auto Buy | Новые настройки");
        added("Attack Aura | Новые настройки");
        added("Ambience | Начало тумана");

        rewritten("Attack Aura | Режим FunTime");
        rewritten("Auto Swap | Логика работы");
        rewritten("Auto Totem | Свапы на FunTime");
        rewritten("Commands | Переписаны полностью");
        rewritten("Ambience | Исправлен баг с туманом");
        rewritten("Water Speed | FunTime");
        rewritten("Speed | FunTime");
        rewritten("Elytra Fly | FunTime");

        deleted("No Hunger");
        deleted("Jesus");
        deleted("No Slow | Режим FunTime Water");
        deleted("Speed | Режимы HolyWorld, ReallyWorld");

        if (changesList().isEmpty()) return;

        changesList().sort(Comparator.comparing(c -> -IFont.getWidth(IFont.MONTSERRAT_BOLD, c.mode.prefix + c.text.getA(), 10)));
        changesList().sort(Comparator.comparing(c -> c.mode.id));
    }

    private static void setupChangeLogV22() {
        CHANGES_LIST.put(new Pair<>("v2.0.2", "FunTime Update"), new ArrayList<>());
        CURRENT_VIEW = "v2.0.2";
        CURRENT_TITLE = "FunTime Update";
        INDEX = 12;
        added("Inv Walk | Режим FunTime");
        rewritten(  "Music Hud | Позиция кнопки Stop");
        deleted("Shaders | Premium настройки");
        deleted("Hands | Premium настройки");
        if (changesList().isEmpty()) return;
        changesList().sort(Comparator.comparing(c -> -IFont.getWidth(IFont.MONTSERRAT_BOLD, c.mode.prefix + c.text.getA(), 10)));
        changesList().sort(Comparator.comparing(c -> c.mode.id));
    }

    private static void added(String ru) {
        changesList().add(new Field(ChangeMode.ADDED, new Pair<>(ru, ru)));
    }

    private static void deleted(String ru) {
        changesList().add(new Field(ChangeMode.DELETED, new Pair<>(ru, ru)));
    }

    private static void rewritten(String ru) {
        changesList().add(new Field(ChangeMode.REWRITTEN, new Pair<>(ru, ru)));
    }

    private static void corrected(String ru) {
        changesList().add(new Field(ChangeMode.CORRECTED, new Pair<>(ru, ru)));
    }

    public static void draw(int mouseX, int mouseY) {
        float y_pos = 2;

        String in = (INDEX < 10 ? "v1.0." + INDEX : "v2.0." + (INDEX - 10));
        IFont.drawWithShadow(IFont.MONTSERRAT_BOLD, in + " | " + CURRENT_TITLE, 2, y_pos, Color.WHITE, 10);
        y_pos += IFont.getHeight(IFont.MONTSERRAT_BOLD, in + " | " + CURRENT_TITLE, 10) + 2;

        for (Field field : changesList()) {
            field.draw(mouseX, mouseY, 2, y_pos);
            y_pos += IFont.getHeight(IFont.MONTSERRAT_BOLD, field.mode.prefix + field.text.getA(), 8) + 1;
        }

        if (data == null) {
            data = new FloatRect(2, 2, IFont.getWidth(IFont.MONTSERRAT_BOLD, changesList().get(0).mode.prefix + changesList().get(0).text.getA(), 7), y_pos);
        }
    }

    public static void click(int mouseX, int mouseY, int button) {
        if (data == null) return;
        if (data.intersect(mouseX, mouseY)) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                if (INDEX >= MAX_INDEX) {
                    INDEX = MIN_INDEX;
                } else {
                    INDEX++;
                }
                rebuild();
            }
            if (button == GLFW.GLFW_MOUSE_BUTTON_2) {
                if (INDEX <= MIN_INDEX) {
                    INDEX = MAX_INDEX;
                } else {
                    INDEX--;
                }
                rebuild();
            }
        }
    }

    private static void rebuild() {
        CURRENT_VIEW = (INDEX < 10 ? "v1.0." + INDEX : "v2.0." + (INDEX - 10));
        for (Pair<String, String> v : CHANGES_LIST.keySet()) {
            if (v.getA().equals(CURRENT_VIEW)) {
                CURRENT_TITLE = v.getB();
                data = null;
                break;
            }
        }
    }

    public static String get(Pair<String, String> lang) {
        return lang.getA();
    }

    public static class Field {
        public ChangeMode mode;
        public Pair<String, String> text;

        private final ColorTransfusion transfusion;
        private FloatRect data;

        public Field(ChangeMode mode, Pair<String, String> text) {
            this.mode = mode;
            this.text = text;

            transfusion = new ColorTransfusion();
            transfusion.animate(Color.LIGHT_GRAY, 255);
        }

        public void draw(int mouseX, int mouseY, float x, float y) {
            String prefix = mode.prefix + " ";
            String info = get(text);

            if (data == null) {
                data = new FloatRect(x + IFont.getWidth(IFont.MONTSERRAT_BOLD, prefix, 8), y, IFont.getWidth(IFont.MONTSERRAT_BOLD, info, 8), IFont.getHeight(IFont.MONTSERRAT_BOLD, info, 8) + 1);
            }

            if (data.intersect(mouseX, mouseY)) {
                transfusion.animate(Color.WHITE, 3);
            } else {
                transfusion.animate(Color.LIGHT_GRAY, 3);
            }

            IFont.drawWithShadow(IFont.MONTSERRAT_BOLD, prefix, x, y, mode.color, 8);
            IFont.drawWithShadow(IFont.MONTSERRAT_BOLD, info, x + IFont.getWidth(IFont.MONTSERRAT_BOLD, prefix, 7), y, transfusion.getColor(), 8);
        }
    }
}