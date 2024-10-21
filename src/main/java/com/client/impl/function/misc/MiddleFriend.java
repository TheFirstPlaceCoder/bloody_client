package com.client.impl.function.misc;

import com.client.event.events.MouseEvent;
import com.client.system.friend.FriendManager;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.utils.misc.InputUtils;
import net.minecraft.entity.player.PlayerEntity;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

public class MiddleFriend extends Function {
    public MiddleFriend() {
        super("Middle Friend", Category.MISC);
    }

    @Override
    public void onMouseButton(MouseEvent event) {
        if (event.action != InputUtils.Action.PRESS || event.button != GLFW_MOUSE_BUTTON_MIDDLE || mc.currentScreen != null || !(mc.targetedEntity instanceof PlayerEntity player)) return;

        if (FriendManager.isFriend(player)) {
            FriendManager.remove(player.getEntityName());
            NotificationManager.add(new Notification(NotificationType.CLIENT, player.getEntityName() + " убран из друзей.", 2000L), NotificationManager.NotifType.Warning);
        } else {
            FriendManager.add(player.getEntityName());
            NotificationManager.add(new Notification(NotificationType.CLIENT, player.getEntityName() + " добавлен в друзья.", 2000L), NotificationManager.NotifType.Info);
        }
    }
}