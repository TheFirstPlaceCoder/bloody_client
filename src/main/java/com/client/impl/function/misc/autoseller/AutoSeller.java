package com.client.impl.function.misc.autoseller;

import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.system.setting.settings.StringSetting;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

import java.util.ArrayList;
import java.util.List;

public class AutoSeller extends Function {
    public AutoSeller() {
        super("Auto Seller", Category.MISC);
        setPremium(true);
    }

    private final ListSetting mode = List().defaultValue("HolyWorld").list(List.of("HolyWorld", "FunTime")).name("Режим").build();
    private final IntegerSetting size = Integer().name("Количество").defaultValue(5).min(1).max(9).build();
    private final IntegerSetting delay = Integer().name("Задержка (MS)").defaultValue(15000).max(30000).min(100).build();
    private final StringSetting sum = String().name("Цена").defaultValue("1000000").build();
    private final BooleanSetting full = Boolean().name("Полная сумма").defaultValue(false).build();

    private boolean sell;
    private long time, sellTime, ahTime, re;
    private int slot;
    private boolean self;

    @Override
    public void onEnable() {
        slot = 0;
        ahTime = -1;
        time = -1;
        sellTime = -1;
        re = -1;
        sell = false;
        self = false;
    }

    @Override
    public void tick(TickEvent.Pre event) {
        sell();

        if (System.currentTimeMillis() > time && sell) {
            resell();
        }
    }

    private void sell() {
        if (sell) return;
        if (mc.currentScreen != null) {
            mc.openScreen(null);
        }

        if (slot >= size.get()) {
            slot = -1;
            time = System.currentTimeMillis() + delay.get();
            sell = true;
        }

        if (System.currentTimeMillis() > sellTime) {
            if (slot >= 0) {
                mc.player.inventory.selectedSlot = slot;
            }

            if (mc.player.age % 4 == 0) {
                if (!mc.player.getMainHandStack().isEmpty()) {
                    mc.player.sendChatMessage("/ah sell " + sum.get() + (full.get() ? " full" : ""));
                }

                slot++;
                sellTime = System.currentTimeMillis() + 500L;
            }
        }
    }

    private void resell() {
        if (mc.currentScreen == null && System.currentTimeMillis() > ahTime) {
            mc.player.sendChatMessage("/ah");
            ahTime = System.currentTimeMillis() + 2000L;
            return;
        }

        if (mc.currentScreen instanceof GenericContainerScreen containerScreen) {
            for (int i = 0; i < containerScreen.getScreenHandler().getInventory().size(); i++) {
                String s = mode.get().equals("FunTime") ? "[☃] Хранилище" : "Активные товары на продаже";
                if (containerScreen.getScreenHandler().getInventory().getStack(i).getName().getString().equals(s)) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player);
                    self = true;
                    break;
                }
            }

            if (self) {
                List<Integer> items = new ArrayList<>();
                for (int i = 0; i < containerScreen.getScreenHandler().getInventory().size(); i++) {
                    ItemStack stack = containerScreen.getScreenHandler().getInventory().getStack(i);
                    String name = stack.getName().getString();
                    if (stack.isEmpty() || name.equals("◀ Вернуться в главное меню")
                            || name.equals("Следующая страница ▶") || name.equals("Что тут делать?") || name.equals("◀ Предыдущая страница")
                            || name.equals("[⟲] Обновить") || name.equals("[⟲] Перевыставить предметы") || name.equals("[⟲] Вернуться")) continue;
                    items.add(i);
                }

                if (items.isEmpty() && System.currentTimeMillis() > re) {
                    self = false;
                    sell = false;
                } else {
                    for (Integer item : items) {
                        if (re > System.currentTimeMillis()) return;
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, item, 0, SlotActionType.PICKUP, mc.player);
                        re = System.currentTimeMillis() + 800L;
                    }
                }
            }
        }
    }
}
