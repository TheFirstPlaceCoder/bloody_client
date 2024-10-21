package com.client.system.cheststealer.item.items;

import com.client.system.cheststealer.item.ChestStealerItem;
import com.client.system.setting.api.ConfigManual;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class ChestStealerCustomItem extends ChestStealerItem {
    public static final long EXP_BOTTLE_15LVL = 70564L;
    public static final long EXP_BOTTLE_30LVL = 70615L;
    public static final long EXP_BOTTLE_50LVL = 70940L;
    public static final long EXP_BOTTLE_100LVL = 70757L;

    public static final long TNT_A_RANG = 60387L;
    public static final long TNT_B_RANG = 59362L;
    public static final long TNT_STEALER = 72461L;
    public static final long TNT_SHOCKWAVE = 132149L;

    public static final long ETERNITY_PICKAXE = 96351L;
    public static final long STINGER_PICKAXE = 72030L;

    public static final long TAL_ETERNITY = 48422L;
    public static final long TAL_STINGER = 33609L;
    public static final long SPHERE_ETERNITY = 133308L;
    public static final long SPHERE_STINGER = 133235L;
    public static final long SPHERE_DAMAGE_III = 102428L;

    public static final long HELMET_OF_THE_SUN = 131288L;
    public static final long ARMORED_ELYTRA = 115103L;
    public static final long UNIVERSAL_KEY = 84395L;
    public static final long JAKES_GOLDEN_PICKAXE = 84395L;

    public static final long EXPLOSIVE_TRAP = 98692L;
    public static final long STAN = 155544L;

    private Item item;
    private String name;
    private long id;

    public ChestStealerCustomItem() {
    }

    public ChestStealerCustomItem(Item item, String name, long id) {
        this.item = item;
        this.name = name;
        this.id = id;
    }

    @Override
    public Item getItem() {
        return item;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toCfg() {
        return item.getTranslationKey() + "&" + name + ConfigManual.DEFAULT.split + priority + "^" + id;
    }

    @Override
    public void load(String in, ConfigManual manual) {
        try {
            this.item = Registry.ITEM.stream().filter(f -> f.getTranslationKey().equals(in.split(manual.split)[0].split("&")[0])).toList().get(0);
            this.name = in.split(manual.split)[0].split("&")[1];
            this.priority = Integer.parseInt(in.split(manual.split)[1].split("\\^")[0]);
            this.id = Long.parseLong(in.split(manual.split)[1].split("\\^")[1]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}