package com.client.impl.command;

import com.client.BloodyClient;
import com.client.system.command.Command;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.utils.auth.Loader;
import com.client.utils.files.Sender;

import java.util.List;

public class ReportCommand extends Command {
    public ReportCommand() {
        super("Report", List.of("report"), List.of());
    }

    private long time;

    @Override
    public void command(String[] args) {
        if (System.currentTimeMillis() > time) {
            StringBuilder message = new StringBuilder();

            for (String arg : args) {
                message.append(arg).append(" ");
            }

            if (message.isEmpty() || message.toString().isBlank()) {
                warning("Вы не можете отправить пустое сообщение.");
                return;
            }

            try {
                String mes = """ 
                        Bloody - Report | Время {time} | Дата {date}
                        Отправитель: аккаунт - {user} | uid - {uid} | версия - {version}
                        Сообщение: {message}
                        --------------------------------------------------------
                        """;

                mes = mes.replace("{time}", BloodyClient.getTime());
                mes = mes.replace("{date}", BloodyClient.getDate());
                mes = mes.replace("{user}", Loader.getAccountName());
                mes = mes.replace("{uid}", Loader.getUID());
                mes = mes.replace("{message}", message.toString());
                mes = mes.replace("{version}", Loader.VERSION);

                Sender.message(BloodyClient.REPORT_WEBHOOK, mes);
                info("Ваш репорт был успешно отправлен.");
            } catch (Exception exc) {
                exc.printStackTrace();
                error("Произошла неизвестная ошибка.");
            }

            time = System.currentTimeMillis() + (Loader.isPremium() ? 60000 : 600000);
        } else {
            NotificationManager.add(new Notification(NotificationType.CLIENT, "Вы уже отправили сообщение, подождите еще " + getTime(), 2000L), NotificationManager.NotifType.Warning);
        }
    }

    private String getTime() {
        long min = (time - System.currentTimeMillis()) / 60000L;
        long sec = 60 - (((Loader.isPremium() ? 60000 : 600000) - (time - System.currentTimeMillis())) / 1000L);
        String end = min >= 1 ? " минут" : " секунд";
        return (min >= 1 ? min + ":" : "0:") + sec + end;
    }

    @Override
    public void error() {
        warning(".report <сообщение>");
    }
}