package com.client.impl.function.combat;

import com.client.event.events.KeybindSettingEvent;
import com.client.event.events.RenderSlotEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.ColorSetting;
import com.client.system.setting.settings.KeybindSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.game.inventory.FindItemResult;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.game.inventory.SlotUtils;
import com.client.utils.misc.InputUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class Helper extends Function {
    public Helper() {
        super("Helper", Category.COMBAT);
    }

    private final ListSetting mode = List().name("Сервер").list(List.of("HolyWorld", "FunTime")).defaultValue("FunTime").build();

    public final BooleanSetting ahHelper = Boolean().name("Аукцион").defaultValue(true).build();
    public final ColorSetting minItem = Color().name("Самый дешевый").defaultValue(Color.GREEN).visible(ahHelper::get).build();
    public final ColorSetting bestItem = Color().name("Самый выгодный").defaultValue(Color.RED).visible(ahHelper::get).build();

    public final KeybindSetting firework = Keybind().name("Фейерверк").defaultValue(-1).build();
    public final KeybindSetting pearl = Keybind().name("Перл").defaultValue(-1).build();

    private final KeybindSetting hw_trapka = Keybind().name("Взрывная трапка").defaultValue(-1).visible(() -> mode.get().equals("HolyWorld")).build();
    private final KeybindSetting hw_stan = Keybind().name("Стан").defaultValue(-1).visible(() -> mode.get().equals("HolyWorld")).build();

    private final KeybindSetting ft_trapka = Keybind().name("Трапка").defaultValue(-1).visible(() -> mode.get().equals("FunTime")).build();
    private final KeybindSetting ft_disorent = Keybind().name("Дезориентация").defaultValue(-1).visible(() -> mode.get().equals("FunTime")).build();
    private final KeybindSetting ft_plast = Keybind().name("Пласт").defaultValue(-1).visible(() -> mode.get().equals("FunTime")).build();
    private final KeybindSetting ft_smerch = Keybind().name("Огненный смерч").defaultValue(-1).visible(() -> mode.get().equals("FunTime")).build();
    private final KeybindSetting ft_yavnayapil = Keybind().name("Явная пыль").defaultValue(-1).visible(() -> mode.get().equals("FunTime")).build();
    private final KeybindSetting ft_godaura = Keybind().name("Божественная аура").defaultValue(-1).visible(() -> mode.get().equals("FunTime")).build();
    private final KeybindSetting ft_shulker = Keybind().name("Открыть шалкер").defaultValue(-1).visible(() -> mode.get().equals("FunTime")).build();

    private final BooleanSetting notification = Boolean().name("Уведомления").defaultValue(true).build();

    private final HashMap<Long, Runnable> callback = new HashMap<>();
    private int prev;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    public Slot ahSlot = null;
    public Slot ahSlotCount = null;

    @Override
    public void onEnable() {
        ahSlot = null;
        ahSlotCount = null;
        callback.clear();
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (ahHelper.get()) {
            executorService.execute(() -> {
                try {
                    ahSlot = null;
                    ahSlotCount = null;

                    if (mc.player.currentScreenHandler instanceof net.minecraft.screen.GenericContainerScreenHandler sh) {
                        int minCount = Integer.MAX_VALUE;
                        int minCountCount = Integer.MAX_VALUE;
                        int theBestSlot = -1;
                        int theBestSlotCount = -1;
                        for (int i = 0; i < sh.getInventory().size(); i++) {
                            if (sh.getInventory().getStack(i) != null && sh.getInventory().getStack(i).getItem() != Items.AIR && getCost(sh.getInventory().getStack(i)) != null && getCost(sh.getInventory().getStack(i)) < minCount) {
                                minCount = getCost(sh.getInventory().getStack(i));
                                theBestSlot = i;
                            }

                            if (sh.getInventory().getStack(i) != null && sh.getInventory().getStack(i).getItem() != Items.AIR && getCost(sh.getInventory().getStack(i)) != null && (getCost(sh.getInventory().getStack(i)) / sh.getInventory().getStack(i).getCount()) < minCountCount) {
                                minCountCount = (getCost(sh.getInventory().getStack(i)) / sh.getInventory().getStack(i).getCount());
                                theBestSlotCount = i;
                            }
                        }

                        ahSlot = sh.getSlot(theBestSlot);
                        ahSlotCount = sh.getSlot(theBestSlotCount);
                    }
                } catch (Exception ignored) {
                }
            });
        }


        for (Map.Entry<Long, Runnable> longRunnableEntry : callback.entrySet()) {
            if (System.currentTimeMillis() > longRunnableEntry.getKey())
                longRunnableEntry.getValue().run();
        }

        callback.entrySet().removeIf(a -> System.currentTimeMillis() > a.getKey());
    }

    @Override
    public void onRenderSlot(RenderSlotEvent event) {
        event.minCountSlot = ahSlot;
        event.minSlot = ahSlotCount;
        event.minCountColor = minItem.get();
        event.minColor = bestItem.get();
    }

    @Override
    public void onKeybindSetting(KeybindSettingEvent event) {
        if (event.action == InputUtils.Action.PRESS) {
            useItem(event, firework, Items.FIREWORK_ROCKET);
            useItem(event, pearl, Items.ENDER_PEARL);

            switch (mode.get()) {
                case "HolyWorld" -> {
                    useItem(event, hw_trapka, Items.PRISMARINE_SHARD);
                    useItem(event, hw_stan, Items.NETHER_STAR);
                }

                case "FunTime" -> {
                    useItem(event, ft_trapka, Items.NETHERITE_SCRAP);
                    useItem(event, ft_disorent, Items.ENDER_EYE);
                    useItem(event, ft_plast, Items.DRIED_KELP);
                    useItem(event, ft_smerch, Items.FIRE_CHARGE);
                    useItem(event, ft_yavnayapil, Items.SUGAR);
                    useItem(event, ft_godaura, Items.PHANTOM_MEMBRANE);
                    useItem(event, ft_shulker, Items.SHULKER_BOX);
                }
            }
        }
    }

    @Nullable
    private static Integer getCost(ItemStack stack) {
        try {
            return stack.getSubTag("display").getList("Lore", 8).stream()
                    .map(element -> {
                        String string = Text.Serializer.fromJson(element.asString()).getString();

                        if (Stream.of("$", "₽", "Цeнa", "Цена:", "Стоимость").anyMatch(string::contains)) {
                            List<Character> list = new ArrayList<>();
                            for (char c : string.toCharArray()) {
                                if (c == '.') break;
                                if (c >= '0' && c <= '9') list.add(c);
                            }
                            char[] chars = new char[list.size()];
                            for (int index = 0; index < list.size(); index++) chars[index] = list.get(index);
                            try {
                                return Integer.parseInt(new String(chars));
                            } catch (NumberFormatException ex) {}
                        }

                        return null;
                    })
                    .filter(Objects::nonNull)
                    .findFirst()
                    .get();
        } catch (Exception ex) {
            return null;
        }
    }

    private void useItem(KeybindSettingEvent event, KeybindSetting setting, Item item) {
        FindItemResult findItemResult = InvUtils.find(item);
        if (item.equals(Items.SHULKER_BOX)) {
            findItemResult = InvUtils.find(i -> i.getItem().getTranslationKey().contains("shulker"));
        }

        if (!setting.key(event.key, !event.mouse)) return;

        if (!findItemResult.found()) {
            print(setting, 0);
            return;
        }

        if (mc.player.getItemCooldownManager().isCoolingDown(item)) {
            print(setting, 1);
            return;
        }

        if (item.equals(Items.SHULKER_BOX)) {
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, SlotUtils.indexToId(findItemResult.slot()), 1, SlotActionType.PICKUP, mc.player);
        } else {
            use(findItemResult.slot());
        }


        print(setting, 2);
    }

    private void use(int slot) {
        if (slot == 45) {
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.OFF_HAND);
            mc.player.swingHand(Hand.OFF_HAND);
        } else if (slot == mc.player.inventory.selectedSlot) {
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);
        } else if (SlotUtils.isHotbar(slot)) {
            prev = mc.player.inventory.selectedSlot;
            mc.interactionManager.pickFromInventory(slot);
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);
            callback.put(150L, () -> mc.player.inventory.selectedSlot = prev);
        } else {
            boolean air = false;
            for (int i = 0; i < SlotUtils.MAIN_START; i++) {
                if (mc.player.inventory.getStack(i).getItem() == Items.AIR) {
                    air = true;
                    break;
                }
            }
            if (air) {
                prev = mc.player.inventory.selectedSlot;
                mc.interactionManager.pickFromInventory(slot);
                mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
                mc.player.swingHand(Hand.MAIN_HAND);
                callback.put(150L, () -> mc.player.inventory.selectedSlot = prev);
            } else {
                mc.interactionManager.pickFromInventory(slot);
                mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
                mc.player.swingHand(Hand.MAIN_HAND);
                mc.interactionManager.pickFromInventory(slot);
            }
        }
    }

    private void print(KeybindSetting setting, int i) {
        if (!notification.get()) return;
        String message = "";

        switch (i) {
            case 0: {
                if (setting.equals(hw_trapka)) message = message.concat("Трапка не найдена!");
                if (setting.equals(hw_stan)) message = message.concat("Стан не найден!");
                if (setting.equals(ft_trapka)) message = message.concat("Трапка не найдена!");
                if (setting.equals(ft_disorent)) message = message.concat("Дезориентация не найдена!");
                if (setting.equals(ft_yavnayapil)) message = message.concat("Явная пыль не найдена!");
                if (setting.equals(ft_shulker)) message = "Шалкер не найден!";
                if (setting.equals(firework)) message = "Фейерверк не найден!";
                if (setting.equals(pearl)) message = "Эндер жемчуг не найден!";

                break;
            }

            case 1: {
                if (setting.equals(hw_trapka)) message = message.concat("Трапка");
                if (setting.equals(hw_stan)) message = message.concat("Стан");
                if (setting.equals(ft_trapka)) message = message.concat("Трапка");
                if (setting.equals(ft_disorent)) message = message.concat("Дезориентация");
                if (setting.equals(ft_yavnayapil)) message = message.concat("Явная пыль");
                if (setting.equals(firework)) message = "Фейерверк";
                if (setting.equals(pearl)) message = "Эндер жемчуг";

                message = message.concat(" в кд!");

                break;
            }

            case 2: {
                message = "Использовал ";

                if (setting.equals(hw_trapka)) message = message.concat("трапку");
                if (setting.equals(hw_stan)) message = message.concat("стан");
                if (setting.equals(ft_trapka)) message = message.concat("трапку");
                if (setting.equals(ft_disorent)) message = message.concat("дезориентацию");
                if (setting.equals(ft_yavnayapil)) message = message.concat("явную пыль");
                if (setting.equals(ft_shulker)) message = "Открыл шалкер";
                if (setting.equals(firework)) message = message.concat("фейерверк");
                if (setting.equals(pearl)) message = message.concat("эндер жемчуг");

                break;
            }
        }

        NotificationManager.add(new Notification(NotificationType.CLIENT, message, 2500L), NotificationManager.NotifType.Info);
    }
}