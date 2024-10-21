package com.client.system.notification;

import com.client.clickgui.GuiScreen;
import com.client.impl.function.client.Notifications;
import com.client.system.function.FunctionManager;
import com.client.utils.Utils;
import com.client.utils.game.chat.ChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.client.BloodyClient.mc;

public class NotificationManager {
    public enum NotifType {
        Error,
        Warning,
        Info
    }

    private static final List<Notification> FIELDS = new CopyOnWriteArrayList<>();

    public static void add(Notification notification) {
        if (!FunctionManager.get(Notifications.class).isEnabled() || mc.currentScreen instanceof GuiScreen && notification.type != NotificationType.CLIENT) return;
        FIELDS.forEach(Notification::next);
        FIELDS.add(notification);
    }

    public static void add(Notification notification, NotifType type) {
        if (!FunctionManager.get(Notifications.class).isEnabled() || mc.currentScreen instanceof GuiScreen && notification.type != NotificationType.CLIENT) return;
        if (!FunctionManager.get(Notifications.class).mode.get().equals("Чат")) {
            FIELDS.forEach(Notification::next);
            FIELDS.add(notification);
        }

        if (!FunctionManager.get(Notifications.class).mode.get().equals("Уведомление")) {
            switch (type) {
                case Info -> ChatUtils.info(notification.message);
                case Warning -> ChatUtils.warning(notification.message);
                case Error -> ChatUtils.error(notification.message);
            }
        }
    }

    public static void draw() {
        if (!FunctionManager.get(Notifications.class).isEnabled()) return;
        FIELDS.removeIf(Notification::remove);
        Utils.rescaling(() -> FIELDS.forEach(Notification::draw));
    }

    public static void clear() {
        FIELDS.clear();
    }

    public static int getX() {
        return mc.getWindow().getWidth() / 4;
    }

    public static int getY() {
        return mc.getWindow().getHeight() / 4 + 30;
    }
}