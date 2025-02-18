package com.client.system.notification;

import com.client.clickgui.newgui.GuiScreen;
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

    public static Notifications notifications = FunctionManager.get(Notifications.class);

    private static final List<Notification> FIELDS = new CopyOnWriteArrayList<>();

    public static void add(Notification notification) {
        if (!notifications.isEnabled() || mc.currentScreen instanceof GuiScreen && notification.type != NotificationType.CLIENT) return;
        FIELDS.forEach(Notification::next);
        FIELDS.add(notification);
    }

    public static void add(Notification notification, NotifType type) {
        if (!notifications.isEnabled() || mc.currentScreen instanceof GuiScreen && notification.type != NotificationType.CLIENT) return;
        if (!notifications.mode.get().equals("Chat")) {
            FIELDS.forEach(Notification::next);
            FIELDS.add(notification);
        }

        if (!notifications.mode.get().equals("Notification")) {
            switch (type) {
                case Info -> ChatUtils.info(notification.message);
                case Warning -> ChatUtils.warning(notification.message);
                case Error -> ChatUtils.error(notification.message);
            }
        }
    }

    public static void draw() {
        if (!notifications.isEnabled()) return;
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