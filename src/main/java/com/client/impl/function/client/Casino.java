package com.client.impl.function.client;

import api.interfaces.EventHandler;
import com.client.event.events.ReceiveChatMessageEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Casino extends Function {
    private final IntegerSetting minStavka = Integer().name("Минимальная ставка (тыс)").min(1).max(20).defaultValue(5).build();

    public Casino() {
        super("Casino", Category.CLIENT);
    }

    public long sendMessageDelay, changeAnDelay, lastMessageSend;
    public List<Runnable> callbacks = new ArrayList<>();
    public int count;

    @Override
    public void onEnable() {
        count = 0;
        callbacks.clear();
        lastMessageSend = 0;
        sendMessageDelay = 0;
        changeAnDelay = System.currentTimeMillis() + 60000 * 5;
    }

    @EventHandler
    private void onReceiveMessageEvent(ReceiveChatMessageEvent event) {
        String message = event.message;


        if (message.contains("отправил вам") && message.startsWith("▶")) {
            String[] args = message.split(" ");
            String player = args[2];
            int p = (int) Double.parseDouble(args[5]);
            float r = new Random().nextFloat();

            if (p < minStavka.get() * 1000) {
                callbacks.add(() -> mc.player.sendChatMessage("/msg " + player + " минимальная ставка для игры - " + (minStavka.get() * 1000) + " монет"));
                return;
            }

            count++;

            if (r < 0.25f) {
                callbacks.add(() -> {
                    mc.player.sendChatMessage("/msg " + player + " удача вас любит) вы заработали " + (p + (p * (0.25f))));
                    mc.player.sendChatMessage("/pay " + player + " " + (p + (p * (0.25f))));
                });
            } else {
                callbacks.add(() -> mc.player.sendChatMessage("/msg " + player + " к сожалению вы проиграли("));
            }
        } else if (message.contains("получено от игрока ")) {
            String[] parts = message.split(" ");
            String player = parts[parts.length - 1];

            Pattern pattern = Pattern.compile("\\$(\\d{1,3}(,\\d{3})*)");
            Matcher matcher = pattern.matcher(message);
            int p = 0;
            float r = new Random().nextFloat();
            if (matcher.find()) {
                String amountStr = matcher.group(1).replace(",", "");
                p = Integer.parseInt(amountStr);
            }

            if (p < minStavka.get() * 1000) {
                callbacks.add(() -> mc.player.sendChatMessage("/msg " + player + " минимальная ставка для игры - " + (minStavka.get() * 1000) + " монет"));
                return;
            }

            count++;

            if (r < 0.25f) {
                int finalP = p;
                callbacks.add(() -> {
                    mc.player.sendChatMessage("/msg " + player + " удача вас любит) вы заработали " + ((int) (finalP + (finalP * (0.25f)))));
                    mc.player.sendChatMessage("/pay " + player + " " + ((int) (finalP + (finalP * (0.25f)))));
                    mc.player.sendChatMessage("/pay " + player + " " + ((int) (finalP + (finalP * (0.25f)))));
                });
            } else {
                callbacks.add(() -> mc.player.sendChatMessage("/msg " + player + " к сожалению вы проиграли("));
            }
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (!callbacks.isEmpty() && (System.currentTimeMillis() - lastMessageSend > 8000)) {
            callbacks.get(0).run();
            callbacks.remove(0);
            lastMessageSend = System.currentTimeMillis();
        }

        if (System.currentTimeMillis() > sendMessageDelay) {
            mc.player.sendChatMessage("!Привет, я казино бот! Испытай свою удачу и заработай монет! /pay " + mc.getSession().getUsername() + " (сумма), если тебе повезет то ты сможешь умножить свои деньги! (мин ставка = " + (minStavka.get() * 1000) + ") игр сыграно - " + count);
            sendMessageDelay = System.currentTimeMillis() + 30000L;
            lastMessageSend = System.currentTimeMillis();
        }
    }
}
