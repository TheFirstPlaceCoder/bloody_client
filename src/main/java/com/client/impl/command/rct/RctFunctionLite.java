package com.client.impl.command.rct;

import api.interfaces.EventHandler;
import api.main.EventUtils;
import com.client.event.events.TickEvent;
import com.client.utils.game.entity.ServerUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import static com.client.BloodyClient.mc;

public class RctFunctionLite {
    private static RctFunctionLite rctFunctionLite;

    public static boolean rct;

    private boolean hub, selectStand, selectAnarchy, callUnregister;
    private long uptime;

    public int anId, standId;
    public String an;

    public static void register() {
        register(ServerUtils.getAnarchyID());
    }

    public static void register(int an) {
        rct = true;
        rctFunctionLite = new RctFunctionLite();
        rctFunctionLite.an = ServerUtils.getAnarchy();
        rctFunctionLite.anId = an;
        rctFunctionLite.standId = an == 5 ? 1 : an == 15 ? 2 : an == 25 ? 3 : 4;
        EventUtils.register(rctFunctionLite);
    }

    public static void unregister() {
        if (rctFunctionLite == null) return;
        rct = false;
        EventUtils.unregister(rctFunctionLite);
        rctFunctionLite = null;
    }

    @EventHandler
    private void onTickEvent(TickEvent.Pre event) {
        if (callUnregister) {
            unregister();
        }

        if (!hub) {
            mc.player.sendChatMessage("/hub");
            hub = true;
        }

        if (mc.currentScreen == null && System.currentTimeMillis() > uptime) {
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
        }

        select();
    }

    private void select() {
        if (mc.player.currentScreenHandler != null) {
            if (mc.player.currentScreenHandler instanceof GenericContainerScreenHandler containerScreen) {
                Inventory inventory = containerScreen.getInventory();

                if (inventory.isEmpty()) return;

                if (!selectAnarchy) {
                    for (int i = 0; i < inventory.size(); i++) {
                        ItemStack stack = inventory.getStack(i);
                        String ver = an.equals("Лайт-1.20") ? "Анархия Лайт, в новой версии 1.20," : "Анархия Лайт представляет из себя";
                        if (checkAn(stack, ver)) {
                            uptime = System.currentTimeMillis() + 2000L;
                            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player);
                            selectAnarchy = true;
                            break;
                        }
                    }
                } else if (!selectStand && !an.equals("Лайт-1.20")) {
                    for (int i = 0; i < inventory.size(); i++) {
                        String stand = standId == 1 ? "СолоЛайт" : standId == 2 ? "ДуоЛайт" : standId == 3 ? "ТриоЛайт" : "КланЛайт";
                        if (checkAn(inventory.getStack(i), stand)) {
                            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, standId, 0, SlotActionType.PICKUP, mc.player);
                            uptime = System.currentTimeMillis() + 2000L;
                            selectStand = true;
                        }
                    }
                } else {
                    for (int i = 0; i < inventory.size(); i++) {
                        ItemStack stack = inventory.getStack(i);
                        if (checkAn(stack, "Лайт #" + anId)) {
                            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player);
                            callUnregister = true;
                            break;
                        }
                    }
                }
            }
        }
    }

    private boolean checkAn(ItemStack stack, String... strings) {
        int i = strings.length;
        for (Text text : stack.getTooltip(mc.player, TooltipContext.Default.ADVANCED)) {
            for (String string : strings) {
                if (text.getString().contains(string)) {
                    i--;
                    if (i <= 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
