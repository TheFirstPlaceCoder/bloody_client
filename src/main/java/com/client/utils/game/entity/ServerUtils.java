package com.client.utils.game.entity;

import mixin.accessor.BossBarHudAccessor;
import net.minecraft.client.gui.hud.ClientBossBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.client.BloodyClient.mc;

public class ServerUtils {
    private static int anarchyID = 0;
    private static String anarchy;
    private static int balance;

    private static final List<String> ID_LIST = new ArrayList<>() {{
        add("Классик");
        add("Спидран");
        add("Лайт");
        add("Лайт-1.20");
    }};

    public static boolean isFuntime() {
        return mc.getCurrentServerEntry() != null && mc.getCurrentServerEntry().address != null && mc.getCurrentServerEntry().address.toLowerCase().contains("funtime");
    }

    public static boolean isHolyWorld() {
        return mc.getCurrentServerEntry() != null && mc.getCurrentServerEntry().address != null && mc.getCurrentServerEntry().address.toLowerCase().contains("holyworld");
    }

    public static boolean isReallyWorld() {
        return mc.getCurrentServerEntry() != null && mc.getCurrentServerEntry().address != null && mc.getCurrentServerEntry().address.toLowerCase().contains("reallyworld");
    }

    public static String getIp() {
        if (mc.isInSingleplayer() || mc.getCurrentServerEntry() == null) return "singleplayer";
        return mc.getCurrentServerEntry().address;
    }

    public static boolean isPvp() {
        Map<UUID, ClientBossBar> bars = ((BossBarHudAccessor) mc.inGameHud.getBossBarHud()).getBossBars();

        for (ClientBossBar value : bars.values()) {
            return value.getName().getString().toLowerCase().contains("pvp");
        }

        return false;
    }

    public static boolean checkServer(String name) {
        for (String s : ID_LIST) if (name.contains(s)) return true;
        return false;
    }

    public static List<String> getIdList() {
        return ID_LIST;
    }

    public static String getAnarchy() {
        return anarchy;
    }

    public static void setAnarchy(String anarchy) {
        ServerUtils.anarchy = anarchy;
    }

    public static int getAnarchyID() {
        return anarchyID;
    }

    public static void setAnarchyID(int anarchyID) {
        ServerUtils.anarchyID = anarchyID;
    }

    public static void setBalance(int balance) {
        ServerUtils.balance = balance;
    }

    public static int getBalance() {
        return balance;
    }
}