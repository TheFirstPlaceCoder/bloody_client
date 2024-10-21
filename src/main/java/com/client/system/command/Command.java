package com.client.system.command;

import com.client.BloodyClient;
import com.client.event.events.KeyEvent;
import com.client.interfaces.IChatHud;
import com.client.utils.color.Colors;
import com.client.utils.game.chat.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.List;

public abstract class Command {
    public static final MinecraftClient mc = BloodyClient.mc;

    private final List<String> firstList, secondList;
    private final String name;

    public Command(String name, List<String> first, List<String> second) {
        this.name = name;
        this.firstList = first;
        this.secondList = second;
    }

    public List<String> getFirstList() {
        return firstList;
    }

    public List<String> getSecondList() {
        return secondList;
    }

    public String getName() {
        return name;
    }

    public abstract void command(String[] args);

    public void onKeyEvent(KeyEvent event) {}

    public abstract void error();

    public void info(Text message) {
        ChatUtils.sendMsg(null, message);
    }

    public void error(Text message) {
        ChatUtils.sendMsg(0, null, Formatting.RED, message);
    }

    public void warning(Text message) {
        ChatUtils.sendMsg(0, null, Formatting.YELLOW, message);
    }

    public void info(String message) {
        ChatUtils.info(message);
    }

    public void error(String message) {
        ChatUtils.error(message);
    }

    public void warning(String message) {
        ChatUtils.warning(message);
    }

    public static String getPrefix() {
        return ".";
    }
}