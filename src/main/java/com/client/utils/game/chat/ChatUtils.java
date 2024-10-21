package com.client.utils.game.chat;

import com.client.interfaces.IChatHud;
import com.client.utils.color.Colors;
import mixin.accessor.ChatHudAccessor;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.client.BloodyClient.mc;

public class ChatUtils {
    public static final String PREFIX = "[Bloody Client]";

    public static Text buildPrefix() {
        MutableText text = new LiteralText("");

        for (int i = 0; i < PREFIX.toCharArray().length; i++) {
            char aChar = PREFIX.charAt(i);
            LiteralText temp = new LiteralText("");
            Style style = Style.EMPTY.withColor(TextColor.fromRgb(Colors.getColor(Colors.getIndex(PREFIX.toCharArray().length - i, PREFIX.toCharArray().length * 2)).getRGB()));
            temp.setStyle(style).append(String.valueOf(aChar));
            text.append(temp);
        }

        LiteralText text2 = new LiteralText("");
        text2.append(Formatting.GRAY + " > ");
        text.append(text2);

        return text;
    }

    public static void update() {
        for (ChatHudLine<Text> message : ((ChatHudAccessor) mc.inGameHud.getChatHud()).getMessages()) {
            if (message.getText().getString().startsWith(PREFIX)) {
                MutableText prefix = (MutableText) message.getText().getSiblings().get(0);
                List<MutableText> prefixSiblings = (List) prefix.getSiblings();

                for (int j = 0; j < prefixSiblings.size(); j++) {
                    prefixSiblings.get(j).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Colors.getColor(Colors.getIndex(PREFIX.toCharArray().length - j, PREFIX.toCharArray().length * 2)).getRGB())));
                }
            }
        }

        ((ChatHudAccessor) mc.inGameHud.getChatHud()).getVisibleMessages().clear();

        for(int i = ((ChatHudAccessor) mc.inGameHud.getChatHud()).getMessages().size() - 1; i >= 0; --i) {
            ChatHudLine<Text> chatHudLine = ((ChatHudAccessor) mc.inGameHud.getChatHud()).getMessages().get(i);
            addMessage(chatHudLine.getText(), chatHudLine.getCreationTick(), chatHudLine.getId());
        }
    }

    private static void addMessage(Text message, int ticks, int id) {
        ChatHud hud = mc.inGameHud.getChatHud();

        int i = MathHelper.floor((double)mc.inGameHud.getChatHud().getWidth() / mc.inGameHud.getChatHud().getChatScale());
        List<OrderedText> list = ChatMessages.breakRenderedChatMessageLines(message, i, mc.textRenderer);

        for(int j = 0; j < list.size(); ++j) {
            OrderedText orderedText = list.get(j);
            ((ChatHudAccessor) hud).getVisibleMessages().add(0, new ChatHudLine(ticks, orderedText, id));
        }

    }

    private static BaseText getCustomPrefix(String prefixTitle, Formatting prefixColor) {
        BaseText prefix = new LiteralText("");
        prefix.setStyle(prefix.getStyle().withFormatting(Formatting.GRAY));

        prefix.append("[");

        BaseText moduleTitle = new LiteralText(prefixTitle);
        moduleTitle.setStyle(moduleTitle.getStyle().withFormatting(prefixColor));
        prefix.append(moduleTitle);

        prefix.append("] ");

        return prefix;
    }

    public static void sendMsg(int id, @Nullable String prefixTitle, @Nullable Formatting prefixColor, Text msg) {
        if (mc.world == null) return;

        BaseText message = new LiteralText("");
        message.append(buildPrefix());
        if (prefixTitle != null) message.append(getCustomPrefix(prefixTitle, prefixColor));
        message.append(msg);

        ((IChatHud) mc.inGameHud.getChatHud()).message(message, id);
    }

    public static void sendMsg(Text message) {
        sendMsg(null, message);
    }

    public static void sendMsg(String prefix, Text message) {
        sendMsg(0, prefix, Formatting.LIGHT_PURPLE, message);
    }

    public static void sendMsg(Formatting color, String message, Object... args) {
        sendMsg(0, null, null, color, message, args);
    }

    public static void sendMsg(int id, Formatting color, String message, Object... args) {
        sendMsg(id, null, null, color, message, args);
    }

    public static void sendMsg(int id, @Nullable String prefixTitle, @Nullable Formatting prefixColor, Formatting messageColor, String messageContent, Object... args) {
        sendMsg(id, prefixTitle, prefixColor, formatMsg(messageContent, messageColor, args), messageColor);
    }

    public static void sendMsg(int id, @Nullable String prefixTitle, @Nullable Formatting prefixColor, String messageContent, Formatting messageColor) {
        BaseText message = new LiteralText(messageContent);
        message.setStyle(message.getStyle().withFormatting(messageColor));
        sendMsg(id, prefixTitle, prefixColor, message);
    }

    private static String formatMsg(String format, Formatting defaultColor, Object... args) {
        String msg = String.format(format, args);
        msg = msg.replaceAll("\\(default\\)", defaultColor.toString());
        msg = msg.replaceAll("\\(highlight\\)", Formatting.WHITE.toString());
        msg = msg.replaceAll("\\(underline\\)", Formatting.UNDERLINE.toString());

        return msg;
    }

    public static void error(String message, Object... args) {
        sendMsg(Formatting.RED, message, args);
    }

    public static void error(String prefix, String message, Object... args) {
        sendMsg(0, prefix, Formatting.LIGHT_PURPLE, Formatting.RED, message, args);
    }

    public static void info(String message, Object... args) {
        sendMsg(Formatting.GRAY, message, args);
    }

    public static void info(String prefix, String message, Object... args) {
        sendMsg(0, prefix, Formatting.LIGHT_PURPLE, Formatting.GRAY, message, args);
    }

    public static void warning(String message, Object... args) {
        sendMsg(Formatting.YELLOW, message, args);
    }

    public static void warning(String prefix, String message, Object... args) {
        sendMsg(0, prefix, Formatting.LIGHT_PURPLE, Formatting.YELLOW, message, args);
    }
}
