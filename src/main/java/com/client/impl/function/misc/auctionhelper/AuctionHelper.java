package com.client.impl.function.misc.auctionhelper;

import com.client.event.events.RenderSlotEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.ColorSetting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class AuctionHelper extends Function {
    public final ColorSetting minItem = Color().name("Самый дешевый").defaultValue(Color.GREEN).build();
    public final ColorSetting bestItem = Color().name("Самый выгодный").defaultValue(Color.RED).build();

    public AuctionHelper() {
        super("Auction Helper", Category.PLAYER);
        setPremium(true);
    }

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    public Slot ahSlot = null;
    public Slot ahSlotCount = null;

    @Override
    public void onEnable() {
        ahSlot = null;
        ahSlotCount = null;
    }

    @Override
    public void tick(TickEvent.Pre event) {
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

    @Override
    public void onRenderSlot(RenderSlotEvent event) {
        event.minCountSlot = ahSlot;
        event.minSlot = ahSlotCount;
        event.minCountColor = minItem.get();
        event.minColor = bestItem.get();
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
}
