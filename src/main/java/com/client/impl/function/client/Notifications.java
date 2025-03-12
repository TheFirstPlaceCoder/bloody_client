package com.client.impl.function.client;

import api.interfaces.EventHandler;
import com.client.event.events.EntityEvent;
import com.client.event.events.GameEvent;
import com.client.event.events.PacketEvent;
import com.client.event.events.TickEvent;
import com.client.system.friend.FriendManager;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.Utils;
import com.client.utils.math.MsTimer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.loader.impl.util.StringUtil;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

import java.util.*;

public class Notifications extends Function {
    public final ListSetting mode = List().name("Режим").enName("Mode").list(List.of("Чат", "Уведомление", "Оба")).defaultValue("Оба").build();
    private final MultiBooleanSetting listSetting = MultiBoolean().name("Уведомлять о").enName("Notificate about").defaultValue(List.of(
            new MultiBooleanValue(true, "Поломке брони"),
            new MultiBooleanValue(false, "Приближении игрока"),
            new MultiBooleanValue(false, "Смене гм"),
            new MultiBooleanValue(false, "Потере тотема")
    )).build();

    public Notifications() {
        super("Notifications", Category.CLIENT);
    }

    private boolean alertedHelm;
    private boolean alertedChest;
    private boolean alertedLegs;
    private boolean alertedBoots;
    public static Map<UUID, GameMode> seen = new HashMap<>();
    public MsTimer updater = new MsTimer();
    private final Object2IntMap<UUID> totemPopMap = new Object2IntOpenHashMap<>();
    private final Object2IntMap<UUID> chatIdMap = new Object2IntOpenHashMap<>();

    @Override
    public void onEnable() {
        alertedHelm = false;
        alertedChest = false;
        alertedLegs = false;
        alertedBoots = false;
        updater.reset();
        totemPopMap.clear();
        chatIdMap.clear();
    }

    @EventHandler
    public void onGameEvent(GameEvent.Join event) {
        totemPopMap.clear();
        chatIdMap.clear();
    }

    @Override
    public void onPacket(PacketEvent.Receive event) {
        if (!listSetting.get(3)) return;
        if (!(event.packet instanceof EntityStatusS2CPacket p)) return;

        if (p.getStatus() != 35) return;

        Entity pEntity = p.getEntity(mc.world);

        if (!(pEntity instanceof PlayerEntity entity)) return;

        synchronized (totemPopMap) {
            int pops = totemPopMap.getOrDefault(entity.getUuid(), 0);
            totemPopMap.put(entity.getUuid(), ++pops);

            if (entity == mc.player) NotificationManager.add(new Notification(NotificationType.CLIENT, Utils.isRussianLanguage ? ("Вы потеряли " +  pops + " " + (pops == 1 ? "тотем" : pops < 5 ? "тотема" : "тотемов")) : ("You have lost " +  pops + " " + (pops == 1 ? "totem" : "totems")), 2000L), NotificationManager.NotifType.Warning);
            else NotificationManager.add(new Notification(NotificationType.CLIENT, Utils.isRussianLanguage ? (entity.getGameProfile().getName() + " потерял " + pops + " " + (pops == 1 ? "тотем" : pops < 5 ? "тотема" : "тотемов")) : (entity.getEntityName() + " have lost " + pops + " " + (pops == 1 ? "totem" : "totems")), 2000L), NotificationManager.NotifType.Warning);
        }
    }

    @Override
    public void addEntity(EntityEvent.Add event) {
        if (event.entity.getUuid().equals(mc.player.getUuid()) || !listSetting.get(1)) return;

        if (event.entity instanceof PlayerEntity p) {
            if (FriendManager.isFriend(p)) return;
            NotificationManager.add(new Notification(NotificationType.CLIENT, Formatting.WHITE + p.getGameProfile().getName() + (Utils.isRussianLanguage ? " появился на " : " logged in your distance at") + Formatting.RED + event.entity.getBlockPos().getX() + " " + event.entity.getBlockPos().getY() + " " + event.entity.getBlockPos().getZ(), 3000L), NotificationManager.NotifType.Warning);
        }
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (listSetting.get(0)) {
            Iterable<ItemStack> armorPieces = mc.player.getArmorItems();
            for (ItemStack armorPiece : armorPieces) {
                if (checkThreshold(armorPiece)) {
                    if (isHelm(armorPiece) && !alertedHelm) {
                        NotificationManager.add(new Notification(NotificationType.CLIENT, Utils.isRussianLanguage ? ("Прочность шлема на " + Formatting.RED + "исходе") : ("Your helmet has " + Formatting.RED + "few durability"), 2000L), NotificationManager.NotifType.Warning);
                        alertedHelm = true;
                    }
                }

                if (checkThreshold(armorPiece)) {
                    if (isChest(armorPiece) && !alertedChest) {
                        NotificationManager.add(new Notification(NotificationType.CLIENT, Utils.isRussianLanguage ? ("Прочность нагрудника на " + Formatting.RED + "исходе") : ("Your chestplate has " + Formatting.RED + "few durability"), 2000L), NotificationManager.NotifType.Warning);
                        alertedChest = true;
                    }
                }

                if (checkThreshold(armorPiece)) {
                    if (isLegs(armorPiece) && !alertedLegs) {
                        NotificationManager.add(new Notification(NotificationType.CLIENT, Utils.isRussianLanguage ? ("Прочность штанов на " + Formatting.RED + "исходе") : ("Your leggins has " + Formatting.RED + "few durability"), 2000L), NotificationManager.NotifType.Warning);
                        alertedLegs = true;
                    }
                }

                if (checkThreshold(armorPiece)) {
                    if (isBoots(armorPiece) && !alertedBoots) {
                        NotificationManager.add(new Notification(NotificationType.CLIENT, Utils.isRussianLanguage ? ("Прочность ботинок на " + Formatting.RED + "исходе") : ("Your boots has " + Formatting.RED + "few durability"), 2000L), NotificationManager.NotifType.Warning);
                        alertedBoots = true;
                    }
                }

                if (!checkThreshold(armorPiece)) if (isHelm(armorPiece) && alertedHelm) alertedHelm = false;
                if (!checkThreshold(armorPiece)) if (isChest(armorPiece) && alertedChest) alertedChest = false;
                if (!checkThreshold(armorPiece)) if (isLegs(armorPiece) && alertedLegs) alertedLegs = false;
                if (!checkThreshold(armorPiece)) if (isBoots(armorPiece) && alertedBoots) alertedBoots = false;
            }
        }

        if (listSetting.get(2)) {
            if (updater.passedMs(4_000L)) {
                updater.reset();
                List<UUID> seenPlayers = new ArrayList<>();
                for (PlayerListEntry playerListEntry : mc.getNetworkHandler().getPlayerList()) {
                    UUID id = playerListEntry.getProfile().getId();
                    GameMode gm = playerListEntry.getGameMode();
                    if (gm == null || mc.player.getUuid().equals(id)) {
                        continue;
                    }
                    seenPlayers.add(id);
                    if (!seen.containsKey(id)) {
                        seen.put(id, gm);
                    } else {
                        GameMode gameMode = seen.get(id);
                        if (gameMode != gm) {
                            Formatting formatting = switch (gm) {
                                case CREATIVE -> Formatting.GOLD;
                                case SURVIVAL, ADVENTURE -> Formatting.GREEN;
                                default -> Formatting.RED;
                            };
                            String dName = playerListEntry.getProfile().getName();
                            NotificationManager.add(new Notification(NotificationType.CLIENT, Formatting.WHITE + dName + (Utils.isRussianLanguage ? "сменил гм на " : "just switched gamemode to ") + formatting + StringUtil.capitalize(gm.getName()), 2000L), NotificationManager.NotifType.Warning);
                            seen.put(id, gm);
                        }
                    }
                }
                for (UUID uuid : new ArrayList<>(seen.keySet())) {
                    if (!seenPlayers.contains(uuid)) {
                        seen.remove(uuid);
                    }
                }
            }
        }

        if (listSetting.get(3)) {
            synchronized (totemPopMap) {
                for (PlayerEntity player : mc.world.getPlayers()) {
                    if (!totemPopMap.containsKey(player.getUuid())) continue;

                    if (player.deathTime > 0 || player.getHealth() <= 0) {
                        int pops = totemPopMap.removeInt(player.getUuid());

                        if (player == mc.player) NotificationManager.add(new Notification(NotificationType.CLIENT, Utils.isRussianLanguage ? ("Вы умерли после " +  pops + " " + (pops == 1 ? "тотема" : "тотемов")) : ("You died after losing " + pops + " " + (pops == 1 ? "totem" : "totems")), 2000L), NotificationManager.NotifType.Warning);
                        else NotificationManager.add(new Notification(NotificationType.CLIENT, Utils.isRussianLanguage ? (player.getGameProfile().getName() + " умер после " + pops + " " + (pops == 1 ? "тотема" : "тотемов")) : (player.getGameProfile().getName() + " died after losing " + pops + " " + (pops == 1 ? "totem" : "totems")), 2000L), NotificationManager.NotifType.Warning);
                        chatIdMap.removeInt(player.getUuid());
                    }
                }
            }
        }
    }

    private boolean checkThreshold(ItemStack i) {
        return (((double) (i.getMaxDamage() - i.getDamage()) / i.getMaxDamage()) * 100) <= 35;
    }

    private boolean isHelm(ItemStack itemStack) {
        if (itemStack == null) return false;
        return itemStack.getTranslationKey().contains("helmet");
    }

    private boolean isChest(ItemStack itemStack) {
        if (itemStack == null) return false;
        return itemStack.getTranslationKey().contains("chestplate");
    }

    private boolean isLegs(ItemStack itemStack) {
        if (itemStack == null) return false;
        return itemStack.getTranslationKey().contains("leggings");
    }

    private boolean isBoots(ItemStack itemStack) {
        if (itemStack == null) return false;
        return itemStack.getTranslationKey().contains("boots");
    }
}