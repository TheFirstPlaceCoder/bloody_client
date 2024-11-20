package com.client.system.autobuy;

import com.client.alt.Account;
import com.client.system.config.ConfigSystem;
import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoBuyManager {
    @Getter
    private static final List<AutoBuyItem> items = new ArrayList<>();

    @Getter
    private static final List<CustomAutoBuyItem> customAutoBuyItemList = new ArrayList<>();

    public static final CustomAutoBuyItem eternityPickaxe = new CustomAutoBuyItem(Items.NETHERITE_PICKAXE, 0);
    public static final CustomAutoBuyItem stingerPickaxe = new CustomAutoBuyItem(Items.NETHERITE_PICKAXE, 0);
    public static final CustomAutoBuyItem goldenPickaxe = new CustomAutoBuyItem(Items.GOLDEN_PICKAXE, 0);

    public static final CustomAutoBuyItem cerberusSphere = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0);
    public static final CustomAutoBuyItem fleshSphere = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0);
    public static final CustomAutoBuyItem damageSphere = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0);
    public static final CustomAutoBuyItem speedSphere = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0);
    public static final CustomAutoBuyItem eternitySphere = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0);
    public static final CustomAutoBuyItem stingerSphere = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0);
    public static final CustomAutoBuyItem mythicalSphere = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0);

    public static final CustomAutoBuyItem cerberusTalisman = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0);
    public static final CustomAutoBuyItem fleshTalisman = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0);
    public static final CustomAutoBuyItem damageTalisman = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0);
    public static final CustomAutoBuyItem speedTalisman = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0);
    public static final CustomAutoBuyItem infinityTalisman = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0);
    public static final CustomAutoBuyItem eternityTalisman = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0);
    public static final CustomAutoBuyItem stingerTalisman = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0);
    public static final CustomAutoBuyItem mythicalTalisman = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0);

    public static final CustomAutoBuyItem goldenSpawner = new CustomAutoBuyItem(Items.SPAWNER, 0);

    public static final CustomAutoBuyItem infinityHelmet = new CustomAutoBuyItem(Items.NETHERITE_HELMET, 0);
    public static final CustomAutoBuyItem infinityChestplate = new CustomAutoBuyItem(Items.NETHERITE_CHESTPLATE, 0);
    public static final CustomAutoBuyItem infinityLeggings = new CustomAutoBuyItem(Items.NETHERITE_LEGGINGS, 0);
    public static final CustomAutoBuyItem infinityBoots = new CustomAutoBuyItem(Items.NETHERITE_BOOTS, 0);

    public static final CustomAutoBuyItem eternityHelmet = new CustomAutoBuyItem(Items.NETHERITE_HELMET, 0);
    public static final CustomAutoBuyItem eternityChestplate = new CustomAutoBuyItem(Items.NETHERITE_CHESTPLATE, 0);
    public static final CustomAutoBuyItem eternityLeggings = new CustomAutoBuyItem(Items.NETHERITE_LEGGINGS, 0);
    public static final CustomAutoBuyItem eternityBoots = new CustomAutoBuyItem(Items.NETHERITE_BOOTS, 0);
    public static final CustomAutoBuyItem eternitySword = new CustomAutoBuyItem(Items.NETHERITE_SWORD, 0);

    public static final CustomAutoBuyItem stingerHelmet = new CustomAutoBuyItem(Items.NETHERITE_HELMET, 0);
    public static final CustomAutoBuyItem stingerChestplate = new CustomAutoBuyItem(Items.NETHERITE_CHESTPLATE, 0);
    public static final CustomAutoBuyItem stingerLeggings = new CustomAutoBuyItem(Items.NETHERITE_LEGGINGS, 0);
    public static final CustomAutoBuyItem stingerBoots = new CustomAutoBuyItem(Items.NETHERITE_BOOTS, 0);
    public static final CustomAutoBuyItem stingerSword = new CustomAutoBuyItem(Items.NETHERITE_SWORD, 0);

    public static final CustomAutoBuyItem explosiveTrap = new CustomAutoBuyItem(Items.PRISMARINE_SHARD, 0);
    public static final CustomAutoBuyItem stan = new CustomAutoBuyItem(Items.NETHER_STAR, 0);
    public static final CustomAutoBuyItem explosiveSubstance = new CustomAutoBuyItem(Items.CLAY, 0);
    public static final CustomAutoBuyItem universalKey = new CustomAutoBuyItem(Items.TRIPWIRE_HOOK, 0);

    public static final CustomAutoBuyItem tntRangA = new CustomAutoBuyItem(Items.TNT, 0);
    public static final CustomAutoBuyItem tntRangB = new CustomAutoBuyItem(Items.TNT, 0);
    public static final CustomAutoBuyItem shockWave = new CustomAutoBuyItem(Items.TNT, 0);
    public static final CustomAutoBuyItem c4 = new CustomAutoBuyItem(Items.TNT, 0);
    public static final CustomAutoBuyItem stealer = new CustomAutoBuyItem(Items.TNT, 0);

    public static final CustomAutoBuyItem expBottle15lvl = new CustomAutoBuyItem(Items.EXPERIENCE_BOTTLE, 0);
    public static final CustomAutoBuyItem expBottle30lvl = new CustomAutoBuyItem(Items.EXPERIENCE_BOTTLE, 0);
    public static final CustomAutoBuyItem expBottle50lvl = new CustomAutoBuyItem(Items.EXPERIENCE_BOTTLE, 0);
    public static final CustomAutoBuyItem expBottle100lvl = new CustomAutoBuyItem(Items.EXPERIENCE_BOTTLE, 0);
    public static final CustomAutoBuyItem expBottleInstantRepair = new CustomAutoBuyItem(Items.EXPERIENCE_BOTTLE, 0);

    public static final CustomAutoBuyItem farmerSword = new CustomAutoBuyItem(Items.NETHERITE_SWORD, 0);
    public static final CustomAutoBuyItem mysteriousSummonEgg = new CustomAutoBuyItem(Items.BLAZE_SPAWN_EGG, 0);
    public static final CustomAutoBuyItem combatFragment = new CustomAutoBuyItem(Items.PRISMARINE_CRYSTALS, 0);

    public static final CustomAutoBuyItem winnerPotion = new CustomAutoBuyItem(Items.POTION, 0);
    public static final CustomAutoBuyItem justicePotion = new CustomAutoBuyItem(Items.POTION, 0);

    public static void addItem(AutoBuyItem autoBuyItem) {
        items.add(autoBuyItem);
    }

    public static void removeItem(AutoBuyItem autoBuyItem) {
        items.remove(autoBuyItem);
    }

    public static void init() {
        goldenPickaxe.name = "Золотая кирка джейка";
        goldenPickaxe.enchantments = of("spawner-getter-enchant:1");

        //infinity

        infinityHelmet.name = "Шлем infinity";
        infinityHelmet.enchantments = of("impenetrable-enchant-custom:1", "aqua_affinity:1", "blast_protection:5", "fire_protection:5", "mending:1", "projectile_protection:5", "protection:5", "respiration:3", "unbreaking:5");
        infinityHelmet.strictCheck = true;

        infinityChestplate.name = "Нагрудник infinity";
        infinityChestplate.enchantments = of("impenetrable-enchant-custom:1", "blast_protection:5", "fire_protection:5", "mending:1", "projectile_protection:5", "protection:5", "unbreaking:5");
        infinityChestplate.strictCheck = true;

        infinityLeggings.name = "Штаны infinity";
        infinityLeggings.enchantments = of("impenetrable-enchant-custom:1", "blast_protection:5", "fire_protection:5", "mending:1", "projectile_protection:5", "protection:5", "unbreaking:5");
        infinityLeggings.strictCheck = true;

        infinityBoots.name = "Ботинки infinity";
        infinityBoots.enchantments = of("blast_protection:5", "depth_strider:3", "feather_falling:4", "fire_protection:5", "mending:1", "projectile_protection:5", "protection:5", "soul_speed:3", "unbreaking:5");
        infinityBoots.strictCheck = true;

        infinityTalisman.name = "Талисман infinity";
        infinityTalisman.strings = of("Урон II", "Броня I", "Скорость II", "Макс. здоровье II");

        //eternity

        eternityPickaxe.name = "Кирка eternity";
        eternityPickaxe.enchantments = of("drill-enchant-custom:2", "exp-enchant-custom:3", "foundry-enchant-custom:1", "internal-enchant-custom:1", "magnet-enchant-custom:1", "efficiency:10", "fortune:5", "mending:1", "unbreaking:5");

        eternityHelmet.name = "Шлем eternity";
        eternityHelmet.enchantments = of("impenetrable-enchant-custom:1", "aqua_affinity:1", "blast_protection:5", "fire_protection:5", "mending:1", "projectile_protection:5", "protection:5", "respiration:3", "thorns:3", "unbreaking:5");
        eternityHelmet.strictCheck = true;

        eternityChestplate.name = "Нагрудник eternity";
        eternityChestplate.enchantments = of("impenetrable-enchant-custom:1", "blast_protection:5", "fire_protection:5", "mending:1", "projectile_protection:5", "protection:5", "thorns:3", "unbreaking:5");
        eternityChestplate.strictCheck = true;

        eternityLeggings.name = "Штаны eternity";
        eternityLeggings.enchantments = of("impenetrable-enchant-custom:1", "blast_protection:5", "fire_protection:5", "mending:1", "projectile_protection:5", "protection:5", "thorns:3", "unbreaking:5");
        eternityLeggings.strictCheck = true;

        eternityBoots.name = "Ботинки eternity";
        eternityBoots.enchantments = of("blast_protection:5", "depth_strider:3", "feather_falling:4", "fire_protection:5", "mending:1", "projectile_protection:5", "protection:5", "soul_speed:3", "thorns:3", "unbreaking:5");
        eternityBoots.strictCheck = true;

        eternitySword.name = "Меч eternity";
        eternitySword.enchantments = of("critical-enchant-custom:2", "destroyer-enchant-custom:2", "rich-enchant-custom:1", "bane_of_arthropods:7", "fire_aspect:2", "looting:5", "mending:1", "sharpness:7", "smite:7", "sweeping:3", "unbreaking:5");

        eternityTalisman.name = "Талисман eternity";
        eternityTalisman.strings = of("2 Урон", "2 Броня", "20% Скорость");

        //stinger

        stingerPickaxe.name = "Кирка stinger";
        stingerPickaxe.enchantments = of("drill-enchant-custom:1", "exp-enchant-custom:3", "foundry-enchant-custom:1", "internal-enchant-custom:1", "efficiency:8", "fortune:4", "mending:1", "unbreaking:4");

        stingerHelmet.name = "Шлем stinger";
        stingerHelmet.enchantments = of("aqua_affinity:1", "blast_protection:4", "fire_protection:4", "mending:1", "projectile_protection:4", "protection:4", "respiration:3", "thorns:3", "unbreaking:4");
        stingerHelmet.strictCheck = true;

        stingerChestplate.name = "Нагрудник stinger";
        stingerChestplate.enchantments = of("impenetrable-enchant-custom:1", "blast_protection:4", "fire_protection:4", "mending:1", "projectile_protection:4", "protection:4", "thorns:3", "unbreaking:4");
        stingerChestplate.strictCheck = true;

        stingerLeggings.name = "Штаны stinger";
        stingerLeggings.enchantments = of("impenetrable-enchant-custom:1", "blast_protection:4", "fire_protection:4", "mending:1", "projectile_protection:4", "protection:4", "thorns:3", "unbreaking:4");
        stingerLeggings.strictCheck = true;

        stingerBoots.name = "Ботинки stinger";
        stingerBoots.enchantments = of("blast_protection:4", "depth_strider:3", "feather_falling:4", "fire_protection:4", "mending:1", "projectile_protection:4", "protection:4", "soul_speed:3", "thorns:3", "unbreaking:4");
        stingerBoots.strictCheck = true;

        stingerSword.name = "Меч stinger";
        stingerSword.enchantments = of("critical-enchant-custom:2", "rich-enchant-custom:1", "bane_of_arthropods:7", "fire_aspect:2", "looting:5", "mending:1", "sharpness:6", "smite:7", "sweeping:3", "unbreaking:4");

        stingerTalisman.name = "Талисман stinger";
        stingerTalisman.strings = of("2 Урон", "2 Броня", "10% Скорость");

        // sphere / talisman

        cerberusSphere.name = "Сфера Цербера";
        cerberusSphere.strings = of("Урон IV");

        cerberusTalisman.name = "Талисман Цербера";
        cerberusTalisman.strings = of("Урон IV");

        fleshSphere.name = "Сфера Флеша";
        fleshSphere.strings = of("Скорость III", "Макс. здоровье II");

        fleshTalisman.name = "Талисман Флеша";
        fleshTalisman.strings = of("Скорость III", "Макс. здоровье II");

        damageSphere.name = "Сфера на Урон 3";
        damageSphere.strings = of("Урон III");

        damageTalisman.name = "Талисман на Урон 3";
        damageTalisman.strings = of("Урон III");

        speedSphere.name = "Сфера на Скорость 3";
        speedSphere.strings = of("Скорость III");

        speedTalisman.name = "Талисман на Скорость 3";
        speedTalisman.strings = of("Скорость III");

        eternitySphere.name = "Сфера eternity";
        eternitySphere.strings = of("Скорость II", "Броня II", "Урон II");

        stingerSphere.name = "Сфера stinger";
        stingerSphere.strings = of("Скорость I", "Броня II", "Урон II");

        mythicalSphere.name = "Мифическая Сфера";
        mythicalSphere.strings = of("Урон III", "Броня II");

        mythicalTalisman.name = "Мифический Талисман";
        mythicalTalisman.strings = of("Урон III", "Броня II");

        // other

        explosiveTrap.name = "Взрывная трапка";
        explosiveTrap.strings = of("при взрыве наносит урон в радиусе 3 блоков");

        stan.name = "Стан";
        stan.strings = of("игроки в нем не могут использовать");

        goldenSpawner.name = "Золотой спавнер";
        goldenSpawner.strings = of("5 блоков от спавнера игроку");

        explosiveSubstance.name = "Взрывчатое вещество";
        explosiveSubstance.strings = of("используется только для крафта");

        universalKey.name = "Универсальный ключ";
        universalKey.strings = of("Используйте данный ключ для открытия");

        tntRangA.name = "Динамит A";
        tntRangA.strings = of("имеет в 3 раза больший радус взрыва");

        tntRangB.name = "Динамит B";
        tntRangB.strings = of("имеет в 10 раз больший радус взрыва");

        // c4.name = "C4";
        // c4.strings = of("разрушает блок незеритового привата");

        shockWave.name = "Разрывная волна";
        shockWave.strings = of("разрушает блок незеритового привата");

        stealer.name = "Стиллер";
        stealer.strings = of("после взрыва выпадает");

        expBottle15lvl.name = "Бутылёк с 15ур. опыта";
        expBottle15lvl.strings = of("В пузырьке 315 опыта");

        expBottle30lvl.name = "Бутылёк с 30ур. опыта";
        expBottle30lvl.strings = of("В пузырьке 1395 опыта");

        expBottle50lvl.name = "Бутылёк с 50ур. опыта";
        expBottle50lvl.strings = of("В пузырьке 5345 опыта");

        expBottle100lvl.name = "Бутылёк с 100ур. опыта";
        expBottle100lvl.strings = of("В пузырьке 30971 опыта");

        expBottleInstantRepair.name = "Пузырь опыта";
        expBottleInstantRepair.strings = of("при нажатие ПКМ, полностью ремонтирует");

        farmerSword.name = "Выгодный фарм";
        farmerSword.enchantments = of("mob-farmer-enchant:");

        mysteriousSummonEgg.name = "Загадочное яйцо призыва";
        mysteriousSummonEgg.strings = of("Мститель", "Крипер", "Зомби", "Блейз", "Ведьма");

        combatFragment.name = "Боевой фрагмент";
        combatFragment.enchantments = of("luck_of_the_sea:1");

        winnerPotion.name = "Зелье победителя";
        winnerPotion.strings = of("Сила III", "Скорость III", "Невидимость", "Спешка II", "Регенерация II", "Сопротивление II", "Исцеление II");

        justicePotion.name = "Справедливость";
        justicePotion.strings = of("отравление, иссушение и слабость");

        CustomAutoBuyItem[] customAutoBuyItems = {
                goldenSpawner, goldenPickaxe,
                infinityTalisman, infinityHelmet, infinityChestplate, infinityLeggings, infinityBoots,
                eternityTalisman, eternitySphere, eternityHelmet, eternityChestplate, eternityLeggings, eternityBoots, eternitySword, eternityPickaxe,
                stingerTalisman, stingerSphere, stingerHelmet, stingerChestplate, stingerLeggings, stingerBoots, stingerSword, stingerPickaxe,
                mythicalTalisman, mythicalSphere, fleshTalisman, fleshSphere, cerberusTalisman, cerberusSphere, speedTalisman, speedSphere, damageTalisman, damageSphere,
                explosiveTrap, stan, explosiveSubstance, universalKey, expBottle100lvl, farmerSword, mysteriousSummonEgg, winnerPotion, justicePotion, combatFragment,
                expBottleInstantRepair, expBottle50lvl, expBottle30lvl, expBottle15lvl, shockWave, tntRangB, tntRangA, stealer
        };

        customAutoBuyItemList.addAll(Arrays.asList(customAutoBuyItems));
    }

    private static List<String> of(String... strings) {
        return List.of(strings);
    }

    private static CustomAutoBuyItem getCustomAutoBuyItemByName(String name) {
        return customAutoBuyItemList.stream().filter(customAutoBuyItem -> customAutoBuyItem.name.equalsIgnoreCase(name)).toList().get(0);
    }

    public static void load(List<String> lines) {
        for (String line : ConfigSystem.getBlock(lines, "autobuyitemcfg")) {
            int id = Integer.parseInt(line.split(":")[0]);
            if (id == 0) {
                Item item = null;
                for (Item i : Registry.ITEM) {
                    if (i.getTranslationKey().equalsIgnoreCase(line.split(":")[1])) {
                        item = i;
                        break;
                    }
                }
                items.add(new DefaultAutoBuyItem(item, Integer.parseInt(line.split(":")[2])));
            }
            if (id == 1) {
                CustomAutoBuyItem customAutoBuyItem = getCustomAutoBuyItemByName(line.split(":")[1]);
                CustomAutoBuyItem add = new CustomAutoBuyItem(customAutoBuyItem.item, Integer.parseInt(line.split(":")[2]));
                add.strings = customAutoBuyItem.strings;
                add.enchantments = customAutoBuyItem.enchantments;
                add.name = customAutoBuyItem.name;
                add.strictCheck = customAutoBuyItem.strictCheck;
                items.add(add);
            }
        }
    }

    public static void load(File file) {
        if (!file.exists()) return;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;

            while ((line = br.readLine()) != null) {
                int id = Integer.parseInt(line.split(":")[0]);
                if (id == 0) {
                    Item item = null;
                    for (Item i : Registry.ITEM) {
                        if (i.getTranslationKey().equalsIgnoreCase(line.split(":")[1])) {
                            item = i;
                            break;
                        }
                    }
                    items.add(new DefaultAutoBuyItem(item, Integer.parseInt(line.split(":")[2])));
                }
                if (id == 1) {
                    CustomAutoBuyItem customAutoBuyItem = getCustomAutoBuyItemByName(line.split(":")[1]);
                    CustomAutoBuyItem add = new CustomAutoBuyItem(customAutoBuyItem.item, Integer.parseInt(line.split(":")[2]));
                    add.strings = customAutoBuyItem.strings;
                    add.enchantments = customAutoBuyItem.enchantments;
                    add.name = customAutoBuyItem.name;
                    add.strictCheck = customAutoBuyItem.strictCheck;
                    items.add(add);
                }
            }

            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void save(Writer writer) {
        try {
            writer.write("autobuyitemcfg{\n");
            for (AutoBuyItem item : items) {
                if (item instanceof CustomAutoBuyItem) {
                    writer.write("1:" + ((CustomAutoBuyItem) item).name + ":" + item.price + "\n");
                }
                if (item instanceof DefaultAutoBuyItem) {
                    writer.write("0:" + item.item.getTranslationKey() + ":" + item.price + "\n");
                }
            }
            writer.write("}\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void save(File file) {
        if (file.exists()) file.delete();

        try {
            file.createNewFile();

            BufferedWriter br = new BufferedWriter(new FileWriter(file));
            for (AutoBuyItem item : items) {
                if (item instanceof CustomAutoBuyItem) {
                    br.write("1:" + ((CustomAutoBuyItem) item).name + ":" + item.price + "\n");
                }
                if (item instanceof DefaultAutoBuyItem) {
                    br.write("0:" + item.item.getTranslationKey() + ":" + item.price + "\n");
                }
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}