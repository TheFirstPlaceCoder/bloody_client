package com.client.system.changelogs;

import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ChangeLogs {
    private static List<String> addChangelogs = new ArrayList<>(),
            fixChangelogs = new ArrayList<>(),
            removeChangelogs = new ArrayList<>();
    private static float width = 0;

    public static void init() {
        addChangelogs.addAll(List.of(
                "Cooldown Hud",
                "Auto Farm",
                "Change Logs",
                "Fire Fly",
                "Velocity | Режим FunTime",
                "No Slow | Режим Matrix 2.0",
                "ESP, Shaders | Режим Box, Мигание цвета"
        ));

        fixChangelogs.addAll(List.of(
                "Auto Buy | Баг с зависанием",
                "Discord RPC | Полностью новый",
                "Hud, Notifications, Кнопки майнкрафта | Новый стиль",
                "Attack Aura | Теперь только 4 режима: Matrix, Vulcan/Grim, Grim, FunTime",
                "Speed & Attack Aura | Появилась возможность 'Летать' вокруг таргета",
                "Particles | При потере тотема частицы становятся зелеными"
        ));

        removeChangelogs.addAll(List.of(
                "Middle Click —> Helper + Elytra Helper",
                "Disabler",
                "Jesus",
                "No Fall"
        ));

        addChangelogs.sort(Comparator.comparingDouble(e -> -IFont.getWidth(IFont.Greycliff, e, 9)));
        fixChangelogs.sort(Comparator.comparingDouble(e -> -IFont.getWidth(IFont.Greycliff, e, 9)));
        removeChangelogs.sort(Comparator.comparingDouble(e -> -IFont.getWidth(IFont.Greycliff, e, 9)));
        float addWidth = IFont.getWidth(IFont.Greycliff, "[+] " + addChangelogs.get(0), 9);
        float fixWidth = IFont.getWidth(IFont.Greycliff, "[/] " + fixChangelogs.get(0), 9);
        float removeWidth = IFont.getWidth(IFont.Greycliff, "[-] " + removeChangelogs.get(0), 9);
        width = Math.max(addWidth, Math.max(fixWidth, removeWidth));
    }

    public static void drawChangeLog(FloatRect changelogRect) {
        if (width > 0) changelogRect.setW(width + 10);
        float y = 5;
        for (String add : addChangelogs) {
            IFont.draw(IFont.Greycliff, "[+]", changelogRect.getX() + 5, changelogRect.getY() + 5 + IFont.getHeight(IFont.Greycliff, "Changelogs", 11) + y, new Color(0, 255, 0), 9);
            IFont.draw(IFont.Greycliff, add, changelogRect.getX() + 5 + IFont.getWidth(IFont.Greycliff, "[+] ", 9), changelogRect.getY() + 5 + IFont.getHeight(IFont.Greycliff, "Changelogs", 11) + y, new Color(162, 162, 162).brighter(), 9);

            y += IFont.getHeight(IFont.Greycliff, add, 9);
        }

        y += IFont.getHeight(IFont.Greycliff, "AAA", 9);
        for (String fix : fixChangelogs) {
            IFont.draw(IFont.Greycliff, "[/]", changelogRect.getX() + 5, changelogRect.getY() + 5 + IFont.getHeight(IFont.Greycliff, "Changelogs", 11) + y, new Color(0, 255, 255).brighter(), 9);
            IFont.draw(IFont.Greycliff, fix, changelogRect.getX() + 5 + IFont.getWidth(IFont.Greycliff, "[/] ", 9), changelogRect.getY() + 5 + IFont.getHeight(IFont.Greycliff, "Changelogs", 11) + y, new Color(162, 162, 162).brighter(), 9);

            y += IFont.getHeight(IFont.Greycliff, fix, 9);
        }

        y += IFont.getHeight(IFont.Greycliff, "AAA", 9);
        for (String remove : removeChangelogs) {
            IFont.draw(IFont.Greycliff, "[-]", changelogRect.getX() + 5, changelogRect.getY() + 5 + IFont.getHeight(IFont.Greycliff, "Changelogs", 11) + y, new Color(255, 0, 0).brighter(), 9);
            IFont.draw(IFont.Greycliff, remove, changelogRect.getX() + 5 + IFont.getWidth(IFont.Greycliff, "[-] ", 9), changelogRect.getY() + 5 + IFont.getHeight(IFont.Greycliff, "Changelogs", 11) + y, new Color(162, 162, 162).brighter(), 9);

            y += IFont.getHeight(IFont.Greycliff, remove, 9);
        }

        y += IFont.getHeight(IFont.Greycliff, "AAA", 9);

        changelogRect.setH(5 + IFont.getHeight(IFont.Greycliff, "Changelogs", 11) + y);
    }
}
