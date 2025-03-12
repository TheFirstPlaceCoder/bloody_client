package com.client.impl.function.client;

import api.interfaces.EventHandler;
import com.client.clickgui.autobuy.AutoBuyGui;
import com.client.clickgui.cheststealer.cheststealer.ChestStealerGui;
import com.client.event.events.*;
import com.client.impl.hud.StaffHud;
import com.client.interfaces.IClickSlotC2SPacket;
import com.client.interfaces.IClientPlayerInteractionManager;
import com.client.system.autobuy.*;
import com.client.system.command.Command;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.*;
import com.client.utils.Utils;
import com.client.utils.game.chat.ChatUtils;
import com.client.utils.game.entity.ServerUtils;
import com.client.utils.misc.InputUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class AutoBuy extends Function {
    public AutoBuy() {
        super("Auto Buy", Category.CLIENT);
    }

    public final ListSetting server = List().name("Сервер").list(List.of(
            "FunTime", "HolyWorld"
    )).defaultValue("FunTime").build();

    private final IntegerSetting clickCount = Integer().name("Клики перед прыжками").min(1).max(20).defaultValue(10).visible(() -> server.get().equals("FunTime")).build();
    private final IntegerSetting delay = Integer().name("Задержка обновления").min(100).max(5000).defaultValue(3000).build();
    private final IntegerSetting delayAdd = Integer().name("Задержка действия").min(100).max(3000).defaultValue(1500).visible(() -> server.get().equals("FunTime")).build();
    private final IntegerSetting delayAddPlus = Integer().name("Задержка после клика").min(100).max(3000).defaultValue(1250).visible(() -> server.get().equals("FunTime")).build();
    private final IntegerSetting delayBeforeBuy = Integer().name("Задержка перед кликом").min(100).max(5000).defaultValue(3500).visible(() -> server.get().equals("FunTime")).build();
    private final IntegerSetting delayClick = Integer().name("Задержка между попытками").min(100).max(2000).defaultValue(1500).visible(() -> server.get().equals("FunTime")).build();
    private final BooleanSetting ignoreStaff = Boolean().name("Игнорировать Staff").defaultValue(true).build();
    private final KeybindSetting openScreen = Keybind().name("Открыть меню").defaultValue(-1).build();
    private final KeybindSetting bind = Keybind().name("Включить/Выключить").defaultValue(-1).build();

    public long update;
    public long clickUptime;
    public boolean click;
    public boolean send;

    public boolean enabled = false;
    public boolean packet, packetSend;
    public long idkTime;

    public HistoryItem lastItem;

    public static AutoBuyGui abGui;
    public int countOfClicking;

    private final HashMap<Long, ItemStack> hash = new HashMap<>();
    private final HashMap<Long, Integer> hashOfBlocked = new HashMap<>();

    @Override
    public void onEnable() {
        hash.clear();
        hashOfBlocked.clear();
        isAcceptScreen = false;
    }

    @EventHandler
    public void onKeybindSetting(KeybindSettingEvent event) {
        if (event.action == InputUtils.Action.PRESS) {
            if (openScreen.key(event.key, !event.mouse)) {
                if (abGui == null) {
                    abGui = AutoBuyGui.getInstance();
                } else {
                    abGui.open();
                }
                mc.openScreen(abGui);
            }
        }
    }

    @EventHandler
    private void onKey(KeyEvent event) {
        if (event.key == bind.get() && event.action == InputUtils.Action.PRESS && (mc.currentScreen instanceof GenericContainerScreen || mc.currentScreen == null)) {
            enabled = !enabled;
            ChatUtils.warning("Auto Buy", "Автобай " + (enabled ? "включен." : "выключен."));
            if (enabled) {
                clickedBuy = false;
                countOfClicking = 0;
            }
        }
    }

    @EventHandler
    private void onMouse(MouseEvent event) {
        if (event.button == bind.get() - 90001 && event.action == InputUtils.Action.PRESS && (mc.currentScreen instanceof GenericContainerScreen || mc.currentScreen == null)) {
            enabled = !enabled;
            ChatUtils.warning("Auto Buy", "Автобай " + (enabled ? "включен." : "выключен."));
            if (enabled) {
                clickedBuy = false;
                countOfClicking = 0;
            }
        }
    }

    @EventHandler
    private void onPacketEvent(PacketEvent.Send event) {
        if (!enabled) return;

        if (server.get().equals("HolyWorld")) {
            if (idkTime > System.currentTimeMillis() && event.packet instanceof ClickSlotC2SPacket clickWindowPacket && ((IClickSlotC2SPacket) clickWindowPacket).getId() != 1337) {
                event.cancel();
            }

            if (event.packet instanceof ClickSlotC2SPacket clickSlotC2SPacket) {
                if (packet && lastItem != null) {
                    lastItem.stack = clickSlotC2SPacket.getStack().getItem();
                    packet = false;
                    packetSend = true;
                }
            }
        }
    }

    @EventHandler
    private void onReceiveMessageEvent(ReceiveChatMessageEvent event) {
        if (!enabled) return;

        if (server.get().equals("HolyWorld")) {
            if (lastItem != null && packetSend) {
                if (event.message.equals("Не так быстро!")) {
                    lastItem = null;
                    packetSend = false;
                    return;
                }
                lastItem.purchased = !event.message.contains("Невозможно забрать предмет, так как его уже купили");
                HistoryManager.add(lastItem);
                lastItem = null;
                packetSend = false;
            }
        } else {
            if (event.message.contains("Не так быстро") || event.message.contains("У вас не хватает денег")) {
                lastItem = null;
                blockedIds.add(currentId);
                addTime = System.currentTimeMillis() + delayAddPlus.get();
            }
            else if (event.message.contains("Вы успешно купили") || event.message.contains("Этот товар уже купили")) {
                if (event.message.contains("Этот товар уже купили")) blockedIds.add(currentId);

                lastItem.purchased = !event.message.contains("Этот товар уже купили");
                HistoryManager.add(lastItem);
                lastItem = null;
                addTime = System.currentTimeMillis() + delayAddPlus.get();
            }
        }
    }

    public boolean clickedBuy = false, isAcceptScreen = false;
    public long addTime, currentAcceptTime;
    public long jumpTime;
    public List<Integer> blockedIds = new ArrayList<>();
    public int currentId;

    @Override
    public void onDisable() {
        mc.options.keyJump.setPressed(false);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (server.get().equals("HolyWorld") || !enabled) return;
        if (countOfClicking >= clickCount.get()) {
            if (mc.currentScreen != null) {
                mc.currentScreen.onClose();
                jumpTime = System.currentTimeMillis() + 2500;
            }
            if (System.currentTimeMillis() > jumpTime) {
                mc.options.keyJump.setPressed(false);
                countOfClicking = 0;
                mc.player.sendChatMessage("/ah");
                addTime = System.currentTimeMillis() + delayAdd.get();
            } else {
                mc.options.keyJump.setPressed(true);
            }
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.currentScreen == null || !enabled || System.currentTimeMillis() < addTime || countOfClicking >= clickCount.get()) return;
        Screen screen = mc.currentScreen;

        if (server.get().equals("FunTime")) {
            if (mc.currentScreen instanceof GenericContainerScreen chestScreen) {
                boolean ah = screen.getTitle().getString().toLowerCase().contains("аукцион") || screen.getTitle().getString().toLowerCase().contains("поиск:");
                boolean accept = chestScreen.getTitle().getString().toLowerCase().contains("покупки");
                hashOfBlocked.entrySet().removeIf(a -> System.currentTimeMillis() > a.getKey());

                if (accept) {
                    if (clickedBuy && System.currentTimeMillis() > clickUptime) {
                        clickedBuy = false;
                    }
                    for (int i = 0; i < chestScreen.getScreenHandler().getInventory().size(); i++) {
                        ItemStack stack = chestScreen.getScreenHandler().getInventory().getStack(i);
                        if (stack.getItem().equals(Items.LIME_STAINED_GLASS_PANE) && !clickedBuy) {
                            click(i, 0);
                            clickedBuy = true;
                            clickUptime = System.currentTimeMillis() + 3000;
                        }
                    }
                } else {
                    if (clickedBuy && lastItem != null) {
                        lastItem.purchased = true;
                        HistoryManager.add(lastItem);
                        lastItem = null;
                        addTime = System.currentTimeMillis() + delayAdd.get();
                        clickedBuy = false;
                        return;
                    }

                    clickedBuy = false;

                    if (ah) {
                        for (int i = 0; i < chestScreen.getScreenHandler().getInventory().size(); i++) {
                            ItemStack stack = chestScreen.getScreenHandler().getInventory().getStack(i);
                            if (hashOfBlocked.containsValue(i) || (ignoreStaff.get() && getName(stack) != null && StaffHud.getStaffNicknames().contains(getName(stack)))) continue;

                            String name = stack.getName().asString();

                            if (name.contains("Хранилище") || name.contains("Следующая") || name.contains("Категории"))
                                continue;

                            Integer price = getCost(stack);
                            if (price != null) {
                                if (price <= 0) return;
                                for (AutoBuyItem item : AutoBuyManager.getItems()) {
                                    if (item instanceof CustomAutoBuyItem p) {
                                        if (p.tryBuy(stack, price) && System.currentTimeMillis() > idkTime) {
                                            clickRand(i, 1337);
                                            currentId = i;
                                            if (lastItem == null) {
                                                lastItem = new HistoryItem(price, p.item, stack.hasCustomName() ? stack.getName().getString() : p.item.getDefaultStack().getName().getString());
                                                lastItem.count = stack.getCount();
                                            }

                                            idkTime = System.currentTimeMillis() + delayClick.get();
                                            addTime = System.currentTimeMillis() + delayAdd.get();
                                            hashOfBlocked.put(System.currentTimeMillis() + 10000L, i);
                                            return;
                                        }
                                    }
                                    if (item instanceof DefaultAutoBuyItem p) {
                                        if (p.tryBuy(stack, price) && System.currentTimeMillis() > idkTime) {
                                            clickRand(i, 1337);
                                            currentId = i;
                                            if (lastItem == null) {
                                                lastItem = new HistoryItem(price, p.item, stack.hasCustomName() ? stack.getName().getString() : p.item.getDefaultStack().getName().getString());
                                                lastItem.count = stack.getCount();
                                            }

                                            idkTime = System.currentTimeMillis() + delayClick.get();
                                            addTime = System.currentTimeMillis() + delayAdd.get();
                                            hashOfBlocked.put(System.currentTimeMillis() + 10000L, i);
                                            return;
                                        }
                                    }
                                }
                            }

                            if (i == 49 && System.currentTimeMillis() > update && System.currentTimeMillis() > idkTime) {
                                click(i, 0);
                                update = System.currentTimeMillis() + delay.get();
                                addTime = System.currentTimeMillis() + delayBeforeBuy.get();
                            }
                        }
                    }
                }
            }
        }

        if (server.get().equals("HolyWorld")) {
            boolean ah = screen.getTitle().getString().contains("Аукцион");
            boolean accept = screen.getTitle().getString().contains("Покупка предмета");
            hash.entrySet().removeIf(a -> System.currentTimeMillis() > a.getKey());

            if (ah || accept) {
                if (mc.currentScreen instanceof GenericContainerScreen chestScreen) {
                    if (ah) {
                        for (int i = 0; i < chestScreen.getScreenHandler().getInventory().size(); i++) {
                            ItemStack stack = chestScreen.getScreenHandler().getInventory().getStack(i);
                            if (hash.containsValue(stack) || stack.getItem() == Items.BARRIER || (ignoreStaff.get() && getName(stack) != null && StaffHud.getStaffNicknames().contains(getName(stack)))) continue;

                            String name = stack.getName().getString();

                            if (name.contains("Товары на продаже") || name.contains("Просроченные товары") || name.equals("◀ Предыдущая страница")
                                    || name.equals(" Помощь по аукциону") || name.equals("Следующая страница ▶") || name.equals(" Помощь по системе аукциона:")
                                    || name.equals(" Сортировка") || name.equals(" Категории предметов"))
                                continue;

                            int[] sum = calcSum(stack.getTooltip(mc.player, TooltipContext.Default.NORMAL).stream().filter(text -> text.getString().contains("▍") && text.getString().contains("¤")).toList());
                            boolean b = false;
                            if (sum != null) {
                                int price = sum[0];
                                if (price <= 0) return;
                                for (AutoBuyItem item : AutoBuyManager.getItems()) {
                                    if (item instanceof CustomAutoBuyItem c) {
                                        if (((CustomAutoBuyItem) item).tryBuy(stack, price) && System.currentTimeMillis() > idkTime) {
                                            click(i, 1337);

                                            if (lastItem == null) {
                                                lastItem = new HistoryItem(price, c.item, stack.getName().getString());
                                                lastItem.count = stack.getCount();
                                            }
                                            packet = true;
                                            idkTime = System.currentTimeMillis() + 200;
                                            b = true;
                                        }
                                    }
                                    if (item instanceof DefaultAutoBuyItem d) {
                                        if (((DefaultAutoBuyItem) item).tryBuy(stack, price) && System.currentTimeMillis() > idkTime) {
                                            click(i, 1337);
                                            if (lastItem == null) {
                                                lastItem = new HistoryItem(price, d.item, stack.getName().getString());
                                                lastItem.count = stack.getCount();
                                            }
                                            packet = true;
                                            idkTime = System.currentTimeMillis() + 200;
                                            b = true;
                                        }
                                    }
                                }
                            }

                            if (!b && i != 47) {
                                hash.put(System.currentTimeMillis() + 10000L, stack);
                            }

                            if (stack.getItem().equals(Items.EMERALD) && name.equals("⇵ Обновить аукцион") && i == 47 && System.currentTimeMillis() > update && !packet && System.currentTimeMillis() > idkTime) {
                                click(i, 0);
                                update = System.currentTimeMillis() + delay.get();
                            }
                        }
                    }
                    if (accept) {
                        if (!isAcceptScreen) {
                            isAcceptScreen = true;
                            currentAcceptTime = System.currentTimeMillis() + 500;
                        } else if (System.currentTimeMillis() > currentAcceptTime) {
                            if (click && System.currentTimeMillis() > clickUptime) {
                                click = false;
                            }
                            for (int i = 0; i < chestScreen.getScreenHandler().getInventory().size(); i++) {
                                ItemStack stack = chestScreen.getScreenHandler().getInventory().getStack(i);
                                String name = stack.getName().getString();
                                if (stack.getItem().equals(Items.LIME_STAINED_GLASS_PANE) && name.contains("Купить") && !click) {
                                    click(i, 0);
                                    click = true;
                                    clickUptime = System.currentTimeMillis() + 1000L;
                                }
                            }
                        }
                    } else {
                        click = false;
                        isAcceptScreen = false;
                    }
                }
            }
        }
    }

    private void click(int slot, int id) {
        ((IClientPlayerInteractionManager) mc.interactionManager).click(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP, mc.player, id);
        countOfClicking++;
    }

    private void clickRand(int slot, int id) {
        ((IClientPlayerInteractionManager) mc.interactionManager).click(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.QUICK_MOVE, mc.player, id);
        countOfClicking++;
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

    @Nullable
    private static String getName(ItemStack stack) {
        try {
            return stack.getSubTag("display").getList("Lore", 8).stream()
                    .map(element -> {
                        String string = Text.Serializer.fromJson(element.asString()).getString();

                        if (Stream.of("Продавец:").anyMatch(string::contains)) {
                            List<Character> list = new ArrayList<>();
                            for (char c : string.toCharArray()) {
                                if (c == '.' || c == '(') break;
                                if (isLatinLetter(c) || Character.isDigit(c)) list.add(c);
                            }
                            char[] chars = new char[list.size()];
                            for (int index = 0; index < list.size(); index++) chars[index] = list.get(index);
                            try {
                                return new String(chars);
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

    public static boolean isLatinLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == '_';
    }

    private int[] calcSum(List<Text> s) {
        if (s == null || s.isEmpty()) return null;
        int i1 = -1;
        int i2 = -1;
        for (Text t : s) {
            String string = t.getString();
            StringBuilder stringBuilder = new StringBuilder();
            for (char c : string.toCharArray()) {
                try {
                    stringBuilder.append(Integer.parseInt(String.valueOf(c)));
                } catch (Exception ignore) {}
            }
            if (!stringBuilder.toString().isEmpty()) {
                if (i1 < 0) {
                    i1 = Integer.parseInt(stringBuilder.toString());
                } else {
                    i2 = Integer.parseInt(stringBuilder.toString());
                }
            }
        }
        return new int[]{i1, i2};
    }
}