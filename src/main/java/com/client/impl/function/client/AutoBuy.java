package com.client.impl.function.client;

import api.interfaces.EventHandler;
import com.client.clickgui.autobuy.AutoBuyGui;
import com.client.event.events.*;
import com.client.interfaces.IClickSlotC2SPacket;
import com.client.interfaces.IClientPlayerInteractionManager;
import com.client.system.autobuy.*;
import com.client.system.command.Command;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.KeybindSetting;
import com.client.system.setting.settings.Widget;
import com.client.utils.game.chat.ChatUtils;
import com.client.utils.misc.InputUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.List;

public class AutoBuy extends Function {
    public AutoBuy() {
        super("Auto Buy", Category.CLIENT);
    }

    private final IntegerSetting delay = Integer().name("Задержка (MS)").min(100).max(5000).defaultValue(200).build();
    private final KeybindSetting bind = Keybind().name("Бинд").defaultValue(-1).build();
    private final Widget openScreen = Widget().name("Открыть меню").defaultValue(() -> {
        if (abGui == null) {
            abGui = AutoBuyGui.getInstance();
        } else {
            abGui.open();
        }
        mc.openScreen(abGui);
    }).build();

    public long update;
    public long clickUptime;
    public boolean click;
    public boolean send;

    public boolean enabled = false;
    public boolean packet, packetSend;
    public long idkTime;

    public HistoryItem lastItem;

    public static AutoBuyGui abGui;

    private final HashMap<Long, ItemStack> hash = new HashMap<>();

    @Override
    public void onEnable() {
        hash.clear();
    }

    @EventHandler
    private void onKey(KeyEvent event) {
        if (event.key == bind.get() && event.action == InputUtils.Action.PRESS) {
            enabled = !enabled;
            ChatUtils.info("Автобай " + (enabled ? "включен." : "выключен."));
        }
    }
    @EventHandler
    private void onMouse(MouseEvent event) {
        if (event.button == bind.get() - 90001 && event.action == InputUtils.Action.PRESS) {
            enabled = !enabled;
            ChatUtils.info("Автобай " + (enabled ? "включен." : "выключен."));
        }
    }

    @EventHandler
    private void onPacketEvent(PacketEvent.Send event) {
        if (idkTime > System.currentTimeMillis() && event.packet instanceof ClickSlotC2SPacket clickWindowPacket && ((IClickSlotC2SPacket) clickWindowPacket).getId() != 1337) {
            event.cancel();
        }

        if (event.packet instanceof ClickSlotC2SPacket clickSlotC2SPacket) {
            if (packet && lastItem != null) {
                lastItem.stack = clickSlotC2SPacket.getStack();
                packet = false;
                packetSend = true;
            }
        }
    }

    @EventHandler
    private void onReceiveMessageEvent(ReceiveChatMessageEvent event) {
        if (lastItem != null && packetSend) {
            if (event.message.equals("Не так быстро!")) {
                lastItem = null;
                packetSend = false;
                return;
            }
            lastItem.purchased = !event.message.contains("Этот предмет нельзя купить, т.к он больше не продается.");
            HistoryManager.add(lastItem);
            lastItem = null;
            packetSend = false;
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.currentScreen == null || !enabled) return;
        Screen screen = mc.currentScreen;

        boolean ah = screen.getTitle().getString().contains("Аукцион");
        boolean accept = screen.getTitle().getString().contains("Покупка предмета");
        hash.entrySet().removeIf(a ->  System.currentTimeMillis() > a.getKey());

        if (ah || accept) {
            if (mc.currentScreen instanceof GenericContainerScreen chestScreen) {
                if (ah) {
                    for (int i = 0; i < chestScreen.getScreenHandler().getInventory().size(); i++) {
                        ItemStack stack = chestScreen.getScreenHandler().getInventory().getStack(i);
                        if (hash.containsValue(stack)) continue;

                        String name = stack.getName().getString();

                        if (name.equals("Активные товары на продаже") || name.equals("Завершенные товары") || name.equals("Следующая страница ▶")
                                || name.contains("помощь по аукциону") || name.equals("как продать товар?") || name.equals("сортировка")
                                || name.equals("Категории предметорв") || name.equals("◀ Предыдущая страница"))
                            continue;

                        int[] sum = calcSum(stack.getTooltip(mc.player, TooltipContext.Default.NORMAL).stream().filter(text -> text.getString().contains("▍") && text.getString().contains("¤")).toList());
                        boolean b = false;
                        if (sum != null) {
                            int price = sum[0];
                            if (price <= 0) return;
                            for (AutoBuyItem item : AutoBuyManager.getItems()) {
                                if (item instanceof CustomAutoBuyItem) {
                                    if (((CustomAutoBuyItem) item).tryBuy(stack, price) && System.currentTimeMillis() > idkTime) {
                                        click(i, 1337);
                                        if (lastItem == null) {
                                            lastItem = new HistoryItem(price, stack);
                                        }
                                        packet = true;
                                        idkTime = System.currentTimeMillis() + 200;
                                        b = true;
                                    }
                                }
                                if (item instanceof DefaultAutoBuyItem) {
                                    if (((DefaultAutoBuyItem) item).tryBuy(stack, price) && System.currentTimeMillis() > idkTime) {
                                        click(i, 1337);
                                        if (lastItem == null) {
                                            lastItem = new HistoryItem(price, stack);
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

                        if (stack.getItem().equals(Items.EMERALD) && name.equals("Обновить аукцион ↻") && i == 47 && System.currentTimeMillis() > update && !packet && System.currentTimeMillis() > idkTime) {
                            click(i, 0);
                            update = System.currentTimeMillis() + delay.get();
                        }
                    }
                }
                if (accept) {
                    if (click && System.currentTimeMillis() > clickUptime) {
                        click = false;
                    }
                    for (int i = 0; i < chestScreen.getScreenHandler().getInventory().size(); i++) {
                        ItemStack stack = chestScreen.getScreenHandler().getInventory().getStack(i);
                        String name = stack.getName().getString();
                        if (stack.getItem().equals(Items.GREEN_STAINED_GLASS_PANE) && name.contains("Купить") && !click) {
                            click(i, 0);
                            click = true;
                            clickUptime = System.currentTimeMillis() + 1000L;
                        }
                    }
                } else {
                    click = false;
                }
            }
        }
    }

    private void click(int slot, int id) {
        ((IClientPlayerInteractionManager) mc.interactionManager).click(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP, mc.player, id);
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