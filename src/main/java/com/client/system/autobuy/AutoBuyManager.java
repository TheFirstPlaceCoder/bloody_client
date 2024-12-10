package com.client.system.autobuy;

import com.client.system.config.ConfigSystem;
import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

import java.io.*;
import java.util.*;

public class AutoBuyManager {
    @Getter
    private static final Set<AutoBuyItem> items = new LinkedHashSet<>();

    @Getter
    private static final List<CustomAutoBuyItem> customAutoBuyItemList = new ArrayList<>();

    public static final CustomAutoBuyItem goldenPickaxe = new CustomAutoBuyItem(Items.GOLDEN_PICKAXE, 0, false);

    public static final CustomAutoBuyItem cerberusSphere = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, false);
    public static final CustomAutoBuyItem fleshSphere = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, false);
    public static final CustomAutoBuyItem damageSphere = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, false);
    public static final CustomAutoBuyItem speedSphere = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, false);
    public static final CustomAutoBuyItem mythicalSphere = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, false);

    public static final CustomAutoBuyItem cerberusTalisman = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, false);
    public static final CustomAutoBuyItem fleshTalisman = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, false);
    public static final CustomAutoBuyItem damageTalisman = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, false);
    public static final CustomAutoBuyItem speedTalisman = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, false);
    public static final CustomAutoBuyItem mythicalTalisman = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, false);

    public static final CustomAutoBuyItem goldenSpawner = new CustomAutoBuyItem(Items.SPAWNER, 0, false);

    public static final CustomAutoBuyItem infinityTalisman = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, false);
    public static final CustomAutoBuyItem infinityHelmet = new CustomAutoBuyItem(Items.NETHERITE_HELMET, 0, false);
    public static final CustomAutoBuyItem infinityChestplate = new CustomAutoBuyItem(Items.NETHERITE_CHESTPLATE, 0, false);
    public static final CustomAutoBuyItem infinityLeggings = new CustomAutoBuyItem(Items.NETHERITE_LEGGINGS, 0, false);
    public static final CustomAutoBuyItem infinityBoots = new CustomAutoBuyItem(Items.NETHERITE_BOOTS, 0, false);

    public static final CustomAutoBuyItem eternitySphere = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, false);
    public static final CustomAutoBuyItem eternityTalisman = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, false);
    public static final CustomAutoBuyItem eternityHelmet = new CustomAutoBuyItem(Items.NETHERITE_HELMET, 0, false);
    public static final CustomAutoBuyItem eternityChestplate = new CustomAutoBuyItem(Items.NETHERITE_CHESTPLATE, 0, false);
    public static final CustomAutoBuyItem eternityLeggings = new CustomAutoBuyItem(Items.NETHERITE_LEGGINGS, 0, false);
    public static final CustomAutoBuyItem eternityBoots = new CustomAutoBuyItem(Items.NETHERITE_BOOTS, 0, false);
    public static final CustomAutoBuyItem eternitySword = new CustomAutoBuyItem(Items.NETHERITE_SWORD, 0, false);
    public static final CustomAutoBuyItem eternityPickaxe = new CustomAutoBuyItem(Items.NETHERITE_PICKAXE, 0, false);

    public static final CustomAutoBuyItem stingerSphere = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, false);
    public static final CustomAutoBuyItem stingerTalisman = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, false);
    public static final CustomAutoBuyItem stingerHelmet = new CustomAutoBuyItem(Items.NETHERITE_HELMET, 0, false);
    public static final CustomAutoBuyItem stingerChestplate = new CustomAutoBuyItem(Items.NETHERITE_CHESTPLATE, 0, false);
    public static final CustomAutoBuyItem stingerLeggings = new CustomAutoBuyItem(Items.NETHERITE_LEGGINGS, 0, false);
    public static final CustomAutoBuyItem stingerBoots = new CustomAutoBuyItem(Items.NETHERITE_BOOTS, 0, false);
    public static final CustomAutoBuyItem stingerSword = new CustomAutoBuyItem(Items.NETHERITE_SWORD, 0, false);
    public static final CustomAutoBuyItem stingerPickaxe = new CustomAutoBuyItem(Items.NETHERITE_PICKAXE, 0, false);

    public static final CustomAutoBuyItem explosiveTrap = new CustomAutoBuyItem(Items.PRISMARINE_SHARD, 0, false);
    public static final CustomAutoBuyItem stan = new CustomAutoBuyItem(Items.NETHER_STAR, 0, false);
    public static final CustomAutoBuyItem explosiveSubstance = new CustomAutoBuyItem(Items.CLAY, 0, false);
    public static final CustomAutoBuyItem universalKey = new CustomAutoBuyItem(Items.TRIPWIRE_HOOK, 0, false);

    public static final CustomAutoBuyItem tntRangA = new CustomAutoBuyItem(Items.TNT, 0, false);
    public static final CustomAutoBuyItem tntRangB = new CustomAutoBuyItem(Items.TNT, 0, false);
    public static final CustomAutoBuyItem shockWave = new CustomAutoBuyItem(Items.TNT, 0, false);
    public static final CustomAutoBuyItem c4 = new CustomAutoBuyItem(Items.TNT, 0, false);
    public static final CustomAutoBuyItem stealer = new CustomAutoBuyItem(Items.TNT, 0, false);

    public static final CustomAutoBuyItem expBottle15lvl = new CustomAutoBuyItem(Items.EXPERIENCE_BOTTLE, 0, false);
    public static final CustomAutoBuyItem expBottle30lvl = new CustomAutoBuyItem(Items.EXPERIENCE_BOTTLE, 0, false);
    public static final CustomAutoBuyItem expBottle50lvl = new CustomAutoBuyItem(Items.EXPERIENCE_BOTTLE, 0, false);
    public static final CustomAutoBuyItem expBottle100lvl = new CustomAutoBuyItem(Items.EXPERIENCE_BOTTLE, 0, false);
    public static final CustomAutoBuyItem expBottleInstantRepair = new CustomAutoBuyItem(Items.EXPERIENCE_BOTTLE, 0, false);

    public static final CustomAutoBuyItem farmerSword = new CustomAutoBuyItem(Items.NETHERITE_SWORD, 0, false);
    public static final CustomAutoBuyItem mysteriousSummonEgg = new CustomAutoBuyItem(Items.BLAZE_SPAWN_EGG, 0, false);
    public static final CustomAutoBuyItem combatFragment = new CustomAutoBuyItem(Items.PRISMARINE_CRYSTALS, 0, false);

    public static final CustomAutoBuyItem winnerPotion = new CustomAutoBuyItem(Items.POTION, 0, false);
    public static final CustomAutoBuyItem justicePotion = new CustomAutoBuyItem(Items.POTION, 0, false);


    public static final CustomAutoBuyItem goldenPickaxeFT = new CustomAutoBuyItem(Items.GOLDEN_PICKAXE, 0, true);
    public static final CustomAutoBuyItem mochniyUdarFT = new CustomAutoBuyItem(Items.GOLDEN_PICKAXE, 0, true);
    public static final CustomAutoBuyItem topPickaxe = new CustomAutoBuyItem(Items.NETHERITE_PICKAXE, 0, true);
    public static final CustomAutoBuyItem topPickaxeSilk = new CustomAutoBuyItem(Items.NETHERITE_PICKAXE, 0, true);
    public static final CustomAutoBuyItem molotTora = new CustomAutoBuyItem(Items.NETHERITE_PICKAXE, 0, true);

    public static final CustomAutoBuyItem andromedaSphere1 = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, true);
    public static final CustomAutoBuyItem andromedaSphere2 = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, true);
    public static final CustomAutoBuyItem andromedaSphereMax = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, true);
    public static final CustomAutoBuyItem apolloneSphere1 = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, true);
    public static final CustomAutoBuyItem apolloneSphere2 = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, true);
    public static final CustomAutoBuyItem apolloneSphereMax = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, true);
    public static final CustomAutoBuyItem astreiSphere1 = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, true);
    public static final CustomAutoBuyItem astreiSphere2 = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, true);
    public static final CustomAutoBuyItem astreiSphereMax = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, true);
    public static final CustomAutoBuyItem osirisSphere1 = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, true);
    public static final CustomAutoBuyItem osirisSphere2 = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, true);
    public static final CustomAutoBuyItem osirisSphereMax = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, true);
    public static final CustomAutoBuyItem pandoraSphere1 = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, true);
    public static final CustomAutoBuyItem pandoraSphere2 = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, true);
    public static final CustomAutoBuyItem pandoraSphereMax = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, true);
    public static final CustomAutoBuyItem titanaSphere1 = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, true);
    public static final CustomAutoBuyItem titanaSphere2 = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, true);
    public static final CustomAutoBuyItem titanaSphereMax = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, true);
    public static final CustomAutoBuyItem himeraSphere1 = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, true);
    public static final CustomAutoBuyItem himeraSphere2 = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, true);
    public static final CustomAutoBuyItem himeraSphereMax = new CustomAutoBuyItem(Items.PLAYER_HEAD, 0, true);

    public static final CustomAutoBuyItem talismanGarmonii1 = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, true);
    public static final CustomAutoBuyItem talismanGarmonii2 = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, true);
    public static final CustomAutoBuyItem talismanGarmoniiMax = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, true);
    public static final CustomAutoBuyItem talismanGrani1 = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, true);
    public static final CustomAutoBuyItem talismanGrani2 = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, true);
    public static final CustomAutoBuyItem talismanGraniMax = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, true);
    public static final CustomAutoBuyItem talismanDedala1 = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, true);
    public static final CustomAutoBuyItem talismanDedala2 = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, true);
    public static final CustomAutoBuyItem talismanDedalaMax = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, true);
    public static final CustomAutoBuyItem talismanExidni1 = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, true);
    public static final CustomAutoBuyItem talismanExidni2 = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, true);
    public static final CustomAutoBuyItem talismanExidniMax = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, true);
    public static final CustomAutoBuyItem talismanTritona1 = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, true);
    public static final CustomAutoBuyItem talismanTritona2 = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, true);
    public static final CustomAutoBuyItem talismanTritonaMax = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, true);
    public static final CustomAutoBuyItem talismanPhoenix1 = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, true);
    public static final CustomAutoBuyItem talismanPhoenix2 = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, true);
    public static final CustomAutoBuyItem talismanPhoenixMax = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, true);

    public static final CustomAutoBuyItem talismanKrusha = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, true);
    public static final CustomAutoBuyItem krushHelmet = new CustomAutoBuyItem(Items.NETHERITE_HELMET, 0, true);
    public static final CustomAutoBuyItem krushChestplate = new CustomAutoBuyItem(Items.NETHERITE_CHESTPLATE, 0, true);
    public static final CustomAutoBuyItem krushLeggins = new CustomAutoBuyItem(Items.NETHERITE_LEGGINGS, 0, true);
    public static final CustomAutoBuyItem krushBoots = new CustomAutoBuyItem(Items.NETHERITE_BOOTS, 0, true);
    public static final CustomAutoBuyItem krushSword = new CustomAutoBuyItem(Items.NETHERITE_SWORD, 0, true);
    public static final CustomAutoBuyItem krushPickaxe = new CustomAutoBuyItem(Items.NETHERITE_PICKAXE, 0, true);

    public static final CustomAutoBuyItem talismanKaratelya = new CustomAutoBuyItem(Items.TOTEM_OF_UNDYING, 0, true);
    public static final CustomAutoBuyItem satanaHelmet = new CustomAutoBuyItem(Items.NETHERITE_HELMET, 0, true);
    public static final CustomAutoBuyItem satanaChestplate = new CustomAutoBuyItem(Items.NETHERITE_CHESTPLATE, 0, true);
    public static final CustomAutoBuyItem satanaLeggins = new CustomAutoBuyItem(Items.NETHERITE_LEGGINGS, 0, true);
    public static final CustomAutoBuyItem satanaBoots = new CustomAutoBuyItem(Items.NETHERITE_BOOTS, 0, true);
    public static final CustomAutoBuyItem satanaSword = new CustomAutoBuyItem(Items.NETHERITE_SWORD, 0, true);
    public static final CustomAutoBuyItem satanaPickaxe = new CustomAutoBuyItem(Items.NETHERITE_PICKAXE, 0, true);

    public static final CustomAutoBuyItem kingHelmet = new CustomAutoBuyItem(Items.NETHERITE_HELMET, 0, true);
    public static final CustomAutoBuyItem kingChestplate = new CustomAutoBuyItem(Items.NETHERITE_CHESTPLATE, 0, true);
    public static final CustomAutoBuyItem kingLeggins = new CustomAutoBuyItem(Items.NETHERITE_LEGGINGS, 0, true);
    public static final CustomAutoBuyItem kingBoots = new CustomAutoBuyItem(Items.NETHERITE_BOOTS, 0, true);
    public static final CustomAutoBuyItem kingSword = new CustomAutoBuyItem(Items.NETHERITE_SWORD, 0, true);
    public static final CustomAutoBuyItem kingPickaxe = new CustomAutoBuyItem(Items.NETHERITE_PICKAXE, 0, true);

    public static final CustomAutoBuyItem princHelmet = new CustomAutoBuyItem(Items.DIAMOND_HELMET, 0, true);
    public static final CustomAutoBuyItem princChestplate = new CustomAutoBuyItem(Items.DIAMOND_CHESTPLATE, 0, true);
    public static final CustomAutoBuyItem princLeggins = new CustomAutoBuyItem(Items.DIAMOND_LEGGINGS, 0, true);
    public static final CustomAutoBuyItem princBoots = new CustomAutoBuyItem(Items.DIAMOND_BOOTS, 0, true);
    public static final CustomAutoBuyItem princSword = new CustomAutoBuyItem(Items.DIAMOND_SWORD, 0, true);
    public static final CustomAutoBuyItem princPickaxe = new CustomAutoBuyItem(Items.DIAMOND_PICKAXE, 0, true);

    public static final CustomAutoBuyItem tntTierWhite = new CustomAutoBuyItem(Items.TNT, 0, true);
    public static final CustomAutoBuyItem tntTierBlack = new CustomAutoBuyItem(Items.TNT, 0, true);

    public static final CustomAutoBuyItem expBottle15lvlFT = new CustomAutoBuyItem(Items.EXPERIENCE_BOTTLE, 0, true);
    public static final CustomAutoBuyItem expBottle30lvlFT = new CustomAutoBuyItem(Items.EXPERIENCE_BOTTLE, 0, true);
    public static final CustomAutoBuyItem expBottle50lvlFT = new CustomAutoBuyItem(Items.EXPERIENCE_BOTTLE, 0, true);

    public static final CustomAutoBuyItem trapka = new CustomAutoBuyItem(Items.NETHERITE_SCRAP, 0, true);
    public static final CustomAutoBuyItem plast = new CustomAutoBuyItem(Items.DRIED_KELP, 0, true);
    public static final CustomAutoBuyItem ognenniySmerch = new CustomAutoBuyItem(Items.FIRE_CHARGE, 0, true);
    public static final CustomAutoBuyItem desorient = new CustomAutoBuyItem(Items.ENDER_EYE, 0, true);
    public static final CustomAutoBuyItem yavnayaPil = new CustomAutoBuyItem(Items.SUGAR, 0, true);
    public static final CustomAutoBuyItem boziyaAura = new CustomAutoBuyItem(Items.PHANTOM_MEMBRANE, 0, true);
    public static final CustomAutoBuyItem snezokZamorozka = new CustomAutoBuyItem(Items.SNOWBALL, 0, true);
    public static final CustomAutoBuyItem cursedSuol = new CustomAutoBuyItem(Items.SOUL_LANTERN, 0, true);
    public static final CustomAutoBuyItem otmichkaArmor = new CustomAutoBuyItem(Items.TRIPWIRE_HOOK, 0, true);
    public static final CustomAutoBuyItem otmichkaTool = new CustomAutoBuyItem(Items.TRIPWIRE_HOOK, 0, true);
    public static final CustomAutoBuyItem otmichkaWeapon = new CustomAutoBuyItem(Items.TRIPWIRE_HOOK, 0, true);
    public static final CustomAutoBuyItem otmichkaItems = new CustomAutoBuyItem(Items.TRIPWIRE_HOOK, 0, true);
    public static final CustomAutoBuyItem otmichkaSphere = new CustomAutoBuyItem(Items.TRIPWIRE_HOOK, 0, true);

    public static final CustomAutoBuyItem katanaSword = new CustomAutoBuyItem(Items.NETHERITE_SWORD, 0, true);
    public static final CustomAutoBuyItem stopArrow = new CustomAutoBuyItem(Items.TIPPED_ARROW, 0, true);
    public static final CustomAutoBuyItem sernayaKislota = new CustomAutoBuyItem(Items.SPLASH_POTION, 0, true);
    public static final CustomAutoBuyItem pobedilka = new CustomAutoBuyItem(Items.SPLASH_POTION, 0, true);
    public static final CustomAutoBuyItem agentPotion = new CustomAutoBuyItem(Items.SPLASH_POTION, 0, true);
    public static final CustomAutoBuyItem killerPotion = new CustomAutoBuyItem(Items.SPLASH_POTION, 0, true);
    public static final CustomAutoBuyItem medicPotion = new CustomAutoBuyItem(Items.SPLASH_POTION, 0, true);
    public static final CustomAutoBuyItem flashPotion = new CustomAutoBuyItem(Items.SPLASH_POTION, 0, true);
    public static final CustomAutoBuyItem otrizkaPotion = new CustomAutoBuyItem(Items.SPLASH_POTION, 0, true);

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




        goldenPickaxeFT.name = "Божье касание";
        goldenPickaxeFT.enchantments = of("glint_override:1");
        goldenPickaxeFT.strings = of("Божье касание");

        mochniyUdarFT.name = "Мощный удар";
        mochniyUdarFT.enchantments = of("glint_override:1");
        mochniyUdarFT.strings = of("Мощный удар");

        krushPickaxe.name = "Кирка крушителя";
        krushPickaxe.enchantments = of("efficiency:10", "fortune:5", "mending:1", "unbreaking:5");
        krushPickaxe.strings = of("Бульдозер II", "Авто-плавка", "Опытный III", "Магнит");

        satanaPickaxe.name = "Кирка Сатаны";
        satanaPickaxe.enchantments = of("efficiency:7", "fortune:4", "mending:1", "unbreaking:4");
        satanaPickaxe.strings = of("Авто-плавка", "Магнит");

        topPickaxe.name = "Топ Кирка";
        topPickaxe.enchantments = of("efficiency:5", "fortune:3", "unbreaking:3");

        topPickaxeSilk.name = "Топ Кирка (Шёлк)";
        topPickaxeSilk.enchantments = of("efficiency:7", "silk_touch:3", "unbreaking:3");

        molotTora.name = "Молот Тора";
        molotTora.enchantments = of("fire_protection:1");
        molotTora.strings = of("Мега-бульдозер");

        krushSword.name = "Меч Крушителя";
        krushSword.enchantments = of("bane_of_arthropods:7", "fire_aspect:2", "looting:5", "mending:1", "sharpness:7", "smite:7", "sweeping:3", "unbreaking:5");
        krushSword.strings = of("Яд III", "Опытный III", "Вампиризм II", "Окисление II", "Детекция III");

        satanaSword.name = "Меч Сатаны";
        satanaSword.enchantments = of("fire_aspect:2", "looting:5", "sharpness:7", "sweeping:3", "unbreaking:5");

        katanaSword.name = "Меч Катана";
        katanaSword.enchantments = of("bane_of_arthropods:5", "fire_aspect:2", "looting:3", "mending:1", "sharpness:5", "smite:5", "sweeping:3", "unbreaking:3");

        stopArrow.name = "Ледяная стрела";
        stopArrow.strings = of("Замедление potion.potency.50", "Слабость ", "765% Скорость", "4 Урон");

        andromedaSphere1.name = "Сфера Андромеды I";
        andromedaSphere1.enchantments = of("vanishing_curse:1");
        andromedaSphere1.strings = of("Уровень: 1/3", "1 Броня", "10% Скорость", "2 Урон", "4 Максимальное здоровье");

        andromedaSphere2.name = "Сфера Андромеды II";
        andromedaSphere2.enchantments = of("vanishing_curse:1");
        andromedaSphere2.strings = of("Уровень: 2/3", "1.5 Броня", "10% Скорость", "2.5 Урон", "4 Максимальное здоровье");

        andromedaSphereMax.name = "Сфера Андромеды MAX";
        andromedaSphereMax.enchantments = of("vanishing_curse:1");
        andromedaSphereMax.strings = of("Уровень: MAX", "2 Броня", "15% Скорость", "3 Урон", "4 Максимальное здоровье");


        apolloneSphere1.name = "Сфера Аполлона I";
        apolloneSphere1.enchantments = of("vanishing_curse:1");
        apolloneSphere1.strings = of("Уровень: 1/3", "10% Скорость", "2 Урон");

        apolloneSphere2.name = "Сфера Аполлона II";
        apolloneSphere2.enchantments = of("vanishing_curse:1");
        apolloneSphere2.strings = of("Уровень: 2/3", "10% Скорость", "3 Урон");

        apolloneSphereMax.name = "Сфера Аполлона MAX";
        apolloneSphereMax.enchantments = of("vanishing_curse:1");
        apolloneSphereMax.strings = of("Уровень: MAX", "10% Скорость", "4 Урон");


        astreiSphere1.name = "Сфера Астрея I";
        astreiSphere1.enchantments = of("vanishing_curse:1");
        astreiSphere1.strings = of("Уровень: 1/3", "20% Скорость атаки", "1 Урон", "2 Максимальное здоровье");

        astreiSphere2.name = "Сфера Астрея II";
        astreiSphere2.enchantments = of("vanishing_curse:1");
        astreiSphere2.strings = of("Уровень: 2/3", "17% Скорость атаки", "2 Урон", "2 Максимальное здоровье");

        astreiSphereMax.name = "Сфера Астрея MAX";
        astreiSphereMax.enchantments = of("vanishing_curse:1");
        astreiSphereMax.strings = of("Уровень: MAX", "15% Скорость атаки", "3 Урон", "4 Максимальное здоровье");


        osirisSphere1.name = "Сфера Осириса I";
        osirisSphere1.enchantments = of("vanishing_curse:1");
        osirisSphere1.strings = of("Уровень: 1/3", "2 Броня", "25% Сопротивление отбрасыванию", "25% Отбрасывающая атака");

        osirisSphere2.name = "Сфера Осириса II";
        osirisSphere2.enchantments = of("vanishing_curse:1");
        osirisSphere2.strings = of("Уровень: 2/3", "2.5 Броня", "20% Сопротивление отбрасыванию", "20% Отбрасывающая атака");

        osirisSphereMax.name = "Сфера Осириса MAX";
        osirisSphereMax.enchantments = of("vanishing_curse:1");
        osirisSphereMax.strings = of("Уровень: MAX", "3 Броня", "15% Сопротивление отбрасыванию", "15% Отбрасывающая атака");


        pandoraSphere1.name = "Сфера Пандоры I";
        pandoraSphere1.enchantments = of("vanishing_curse:1");
        pandoraSphere1.strings = of("Уровень: 1/3", "10% Броня", "10% Скорость", "15% Урон");

        pandoraSphere2.name = "Сфера Осириса II";
        pandoraSphere2.enchantments = of("vanishing_curse:1");
        pandoraSphere2.strings = of("Уровень: 2/3", "10% Броня", "10% Скорость", "20% Урон");

        pandoraSphereMax.name = "Сфера Осириса MAX";
        pandoraSphereMax.enchantments = of("vanishing_curse:1");
        pandoraSphereMax.strings = of("Уровень: MAX", "10% Броня", "10% Скорость", "25% Урон");


        titanaSphere1.name = "Сфера Титана I";
        titanaSphere1.enchantments = of("vanishing_curse:1");
        titanaSphere1.strings = of("Уровень: 1/3", "1 Броня", "1 Твёрдость брони");

        titanaSphere2.name = "Сфера Титана II";
        titanaSphere2.enchantments = of("vanishing_curse:1");
        titanaSphere2.strings = of("Уровень: 2/3", "1.5 Броня", "1.5 Твёрдость брони", "10% Скорость");

        titanaSphereMax.name = "Сфера Титана MAX";
        titanaSphereMax.enchantments = of("vanishing_curse:1");
        titanaSphereMax.strings = of("Уровень: MAX", "2 Броня", "15% Скорость", "2 Твёрдость брони");


        himeraSphere1.name = "Сфера Химеры I";
        himeraSphere1.enchantments = of("vanishing_curse:1");
        himeraSphere1.strings = of("Уровень: 1/3", "1 Урон", "2 Максимальное здоровье");

        himeraSphere2.name = "Сфера Химеры II";
        himeraSphere2.enchantments = of("vanishing_curse:1");
        himeraSphere2.strings = of("Уровень: 2/3", "10% Скорость атаки", "2 Урон", "2 Максимальное здоровье");

        himeraSphereMax.name = "Сфера Химеры MAX";
        himeraSphereMax.enchantments = of("vanishing_curse:1");
        himeraSphereMax.strings = of("Уровень: MAX", "15% Скорость атаки", "3 Урон", "2 Максимальное здоровье");




        talismanGarmonii1.name = "Талисман Гармонии I";
        talismanGarmonii1.enchantments = of("unbreaking:3");
        talismanGarmonii1.strings = of("Уровень: 1/3", "1 Броня", "1 Урон", "1 Максимальное здоровье");

        talismanGarmonii2.name = "Талисман Гармонии II";
        talismanGarmonii2.enchantments = of("unbreaking:3");
        talismanGarmonii2.strings = of("Уровень: 2/3", "1.5 Броня", "1.5 Урон", "1.5 Максимальное здоровье");

        talismanGarmoniiMax.name = "Талисман Гармонии MAX";
        talismanGarmoniiMax.enchantments = of("unbreaking:3");
        talismanGarmoniiMax.strings = of("Уровень: MAX", "2 Броня", "2 Урон", "2 Максимальное здоровье");


        talismanGrani1.name = "Талисман Грани I";
        talismanGrani1.enchantments = of("unbreaking:3");
        talismanGrani1.strings = of("Уровень: 1/3", "1 Урон", "2 Максимальное здоровье");

        talismanGrani2.name = "Талисман Грани II";
        talismanGrani2.enchantments = of("unbreaking:3");
        talismanGrani2.strings = of("Уровень: 2/3", "10% Скорость", "2 Урон", "4 Максимальное здоровье");

        talismanGraniMax.name = "Талисман Грани MAX";
        talismanGraniMax.enchantments = of("unbreaking:3");
        talismanGraniMax.strings = of("Уровень: MAX", "15% Скорость", "3 Урон", "4 Максимальное здоровье");


        talismanDedala1.name = "Талисман Дедала I";
        talismanDedala1.enchantments = of("unbreaking:3");
        talismanDedala1.strings = of("Уровень: 1/3", "3 Урон", "4 Максимальное здоровье");

        talismanDedala2.name = "Талисман Дедала II";
        talismanDedala2.enchantments = of("unbreaking:3");
        talismanDedala2.strings = of("Уровень: 2/3", "4 Урон", "4 Максимальное здоровье");

        talismanDedalaMax.name = "Талисман Дедала MAX";
        talismanDedalaMax.enchantments = of("unbreaking:3");
        talismanDedalaMax.strings = of("Уровень: MAX", "5 Урон", "4 Максимальное здоровье");


        talismanExidni1.name = "Талисман Ехидны I";
        talismanExidni1.enchantments = of("unbreaking:3");
        talismanExidni1.strings = of("Уровень: 1/3", "2 Броня", "2 Твёрдость брони", "4 Урон", "4 Максимальное здоровье");

        talismanExidni2.name = "Талисман Ехидны II";
        talismanExidni2.enchantments = of("unbreaking:3");
        talismanExidni2.strings = of("Уровень: 2/3", "2 Броня", "2 Твёрдость брони", "5 Урон", "4 Максимальное здоровье");

        talismanExidniMax.name = "Талисман Ехидны MAX";
        talismanExidniMax.enchantments = of("unbreaking:3");
        talismanExidniMax.strings = of("Уровень: MAX", "2 Броня", "2 Твёрдость брони", "6 Урон", "4 Максимальное здоровье");


        talismanTritona1.name = "Талисман Тритона I";
        talismanTritona1.enchantments = of("unbreaking:3");
        talismanTritona1.strings = of("Уровень: 1/3", "1 Броня", "2 Твёрдость брони", "1 Максимальное здоровье");

        talismanTritona2.name = "Талисман Тритона II";
        talismanTritona2.enchantments = of("unbreaking:3");
        talismanTritona2.strings = of("Уровень: 2/3", "2 Броня", "3 Твёрдость брони", "2 Максимальное здоровье");

        talismanTritonaMax.name = "Талисман Тритона MAX";
        talismanTritonaMax.enchantments = of("unbreaking:3");
        talismanTritonaMax.strings = of("Уровень: MAX", "3 Броня", "2 Твёрдость брони", "3 Максимальное здоровье");


        talismanPhoenix1.name = "Талисман Феникса I";
        talismanPhoenix1.enchantments = of("unbreaking:3");
        talismanPhoenix1.strings = of("Уровень: 1/3", "10% Скорость атаки", "2 Максимальное здоровье");

        talismanPhoenix2.name = "Талисман Феникса II";
        talismanPhoenix2.enchantments = of("unbreaking:3");
        talismanPhoenix2.strings = of("Уровень: 2/3", "10% Скорость атаки", "4 Максимальное здоровье");

        talismanPhoenixMax.name = "Талисман Феникса MAX";
        talismanPhoenixMax.enchantments = of("unbreaking:3");
        talismanPhoenixMax.strings = of("Уровень: MAX", "10% Скорость атаки", "6 Максимальное здоровье");


        talismanKaratelya.name = "Талисман Карателя";
        talismanKaratelya.enchantments = of("unbreaking:3");
        talismanKaratelya.strings = of("Уровень: MAX", "10% Скорость", "7 Урон", "4 Максимальное здоровье");

        talismanKrusha.name = "Талисман Крушителя";
        talismanKrusha.enchantments = of("unbreaking:3");
        talismanKrusha.strings = of("Уровень: MAX", "2 Броня", "2 Твёрдость брони", "3 Урон", "4 Максимальное здоровье");


        krushHelmet.name = "Шлем Крушителя";
        krushHelmet.enchantments = of("aqua_affinity:1", "blast_protection:5", "fire_protection:5", "mending:1", "projectile_protection:5", "protection:5", "respiration:3", "unbreaking:5");

        krushChestplate.name = "Нагрудник Крушителя";
        krushChestplate.enchantments = of("blast_protection:5", "fire_protection:5", "mending:1", "projectile_protection:5", "protection:5", "unbreaking:5");

        krushLeggins.name = "Штаны Крушителя";
        krushLeggins.enchantments = of("blast_protection:5", "fire_protection:5", "mending:1", "projectile_protection:5", "protection:5", "unbreaking:5");

        krushBoots.name = "Ботинки Крушителя";
        krushBoots.enchantments = of("blast_protection:5", "depth_strider:3", "feather_falling:4", "fire_protection:5", "mending:1", "projectile_protection:5", "protection:5", "soul_speed:3", "unbreaking:5");


        satanaHelmet.name = "Шлем Сатаны";
        satanaHelmet.enchantments = of("aqua_affinity:1", "mending:1", "protection:5", "respiration:3", "unbreaking:5");

        satanaChestplate.name = "Нагрудник Сатаны";
        satanaChestplate.enchantments = of("mending:1", "protection:5", "unbreaking:5");

        satanaLeggins.name = "Штаны Сатаны";
        satanaLeggins.enchantments = of("mending:1", "protection:5", "unbreaking:5");

        satanaBoots.name = "Ботинки Сатаны";
        satanaBoots.enchantments = of("feather_falling:4", "mending:1", "protection:5", "soul_speed:3", "unbreaking:5");


        kingHelmet.name = "Шлем Князя";
        kingHelmet.enchantments = of("aqua_affinity:1", "mending:1", "protection:4", "respiration:3", "unbreaking:5");

        kingChestplate.name = "Нагрудник Князя";
        kingChestplate.enchantments = of("mending:1", "protection:4", "unbreaking:5");

        kingLeggins.name = "Штаны Князя";
        kingLeggins.enchantments = of("mending:1", "protection:4", "unbreaking:5");

        kingBoots.name = "Ботинки Князя";
        kingBoots.enchantments = of("feather_falling:4", "mending:1", "protection:4", "soul_speed:3", "unbreaking:5");

        kingSword.name = "Меч Князя";
        kingSword.enchantments = of("fire_aspect:2", "looting:5", "sharpness:5", "sweeping:3", "unbreaking:5");

        kingPickaxe.name = "Кирка Князя";
        kingPickaxe.enchantments = of("efficiency:5", "fortune:5", "mending:1", "unbreaking:5");


        princHelmet.name = "Шлем Принца";
        princHelmet.enchantments = of("aqua_affinity:1", "protection:4", "respiration:3", "unbreaking:5");

        princChestplate.name = "Нагрудник Принца";
        princChestplate.enchantments = of("protection:4", "unbreaking:5");

        princLeggins.name = "Штаны Принца";
        princLeggins.enchantments = of("protection:4", "unbreaking:5");

        princBoots.name = "Ботинки Принца";
        princBoots.enchantments = of("feather_falling:4", "protection:4", "soul_speed:3", "unbreaking:5");

        princSword.name = "Меч Принца";
        princSword.enchantments = of("fire_aspect:2", "looting:3", "sharpness:5", "sweeping:3", "unbreaking:5");

        princPickaxe.name = "Кирка Принца";
        princPickaxe.enchantments = of("efficiency:5", "fortune:3", "mending:1", "unbreaking:3");

        trapka.name = "Трапка";
        trapka.strings = of("[★] Трапка");

        plast.name = "Пласт";
        plast.strings = of("[★] Пласт");

        ognenniySmerch.name = "Огненный смерч";
        ognenniySmerch.strings = of("[★] Огненный смерч");

        desorient.name = "Дезориентация";
        desorient.strings = of("[★] Дезориентация");

        yavnayaPil.name = "Явная пыль";
        yavnayaPil.strings = of("[★] Явная пыль");

        boziyaAura.name = "Божья аура";
        boziyaAura.strings = of("[★] Божья аура");

        snezokZamorozka.name = "Снежок заморозка";
        snezokZamorozka.strings = of("[★] Снежок заморозка");

        cursedSuol.name = "Проклятая Душа";
        cursedSuol.enchantments = of("flame:1");
        cursedSuol.strings = of("Оригинальный предмет");

        otmichkaArmor.name = "Отмычка к Броне";
        otmichkaArmor.strings = of("Открыть хранилище", "С Броней");

        otmichkaTool.name = "Отмычка к Инструментам";
        otmichkaTool.strings = of("Открыть хранилище", "С Инструментами");

        otmichkaWeapon.name = "Отмычка к Оружию";
        otmichkaWeapon.strings = of("Открыть хранилище", "С Оружием");

        otmichkaItems.name = "Отмычка к Ресурсам";
        otmichkaItems.strings = of("Открыть хранилище", "С Ресурсами");

        otmichkaSphere.name = "Отмычка к Сферам";
        otmichkaSphere.strings = of("Открыть хранилище", "С Сферами");

        tntTierWhite.name = "TNT - TIER WHITE";
        tntTierWhite.strings = of("[★] TNT - TIER WHITE");

        tntTierBlack.name = "TNT - TIER BLACK";
        tntTierBlack.strings = of("[★] TNT - TIER BLACK");

        expBottle15lvlFT.name = "Пузырёк опыта [15 Ур.]";
        expBottle15lvlFT.strings = of("Содержит: 15 Ур. опыта");

        expBottle30lvlFT.name = "Пузырёк опыта [30 Ур.]";
        expBottle30lvlFT.strings = of("Содержит: 30 Ур. опыта");

        expBottle50lvlFT.name = "Пузырёк опыта [50 Ур.]";
        expBottle50lvlFT.strings = of("Содержит: 50 Ур. опыта");

        sernayaKislota.name = "Серная Кислота";
        sernayaKislota.strings = of("Отравление II", "Замедление IV", "Слабость III", "Иссушение V", "60% Скорость", "12 Урон");

        pobedilka.name = "Зелье Победителя";
        pobedilka.strings = of("Прилив здоровья II", "Невидимость", "Регенерация II", "Сопротивление", "8 Максимальное здоровье");

        killerPotion.name = "Зелье Киллера";
        killerPotion.strings = of("Сопротивление", "Сила IV", "12 Урон");

        agentPotion.name = "Зелье Агента";
        agentPotion.strings = of("Невидимость II", "Огнестойкость", "Скорость III", "Спешка", "Сила III", "9 Урон", "60% Скорость");

        medicPotion.name = "Зелье Медика";
        medicPotion.strings = of("Прилив здоровья III", "Регенерация III", "12 Максимальное здоровье");

        flashPotion.name = "Зелье Вспышка";
        flashPotion.strings = of("Слепота", "Свечение");

        otrizkaPotion.name = "Зелье Отрыжки";
        otrizkaPotion.strings = of("Слепота", "Свечение", "Голод potion.potency.10", "Замедление III", "Иссушение V", "45% Скорость");

        CustomAutoBuyItem[] customAutoBuyItems = {
                goldenSpawner, goldenPickaxe,
                infinityTalisman, infinityHelmet, infinityChestplate, infinityLeggings, infinityBoots,
                eternityTalisman, eternitySphere, eternityHelmet, eternityChestplate, eternityLeggings, eternityBoots, eternitySword, eternityPickaxe,
                stingerTalisman, stingerSphere, stingerHelmet, stingerChestplate, stingerLeggings, stingerBoots, stingerSword, stingerPickaxe,
                mythicalTalisman, mythicalSphere, fleshTalisman, fleshSphere, cerberusTalisman, cerberusSphere, speedTalisman, speedSphere, damageTalisman, damageSphere,
                explosiveTrap, stan, explosiveSubstance, universalKey, expBottle100lvl, farmerSword, mysteriousSummonEgg, winnerPotion, justicePotion, combatFragment,
                expBottleInstantRepair, expBottle50lvl, expBottle30lvl, expBottle15lvl, shockWave, tntRangB, tntRangA, stealer,

                goldenPickaxeFT, mochniyUdarFT, topPickaxe, topPickaxeSilk, molotTora,
                andromedaSphere1, andromedaSphere2, andromedaSphereMax,
                apolloneSphere1, apolloneSphere2, apolloneSphereMax,
                astreiSphere1, astreiSphere2, astreiSphereMax,
                osirisSphere1, osirisSphere2, osirisSphereMax,
                pandoraSphere1, pandoraSphere2, pandoraSphereMax,
                titanaSphere1, titanaSphere2, titanaSphereMax,
                himeraSphere1, himeraSphere2, himeraSphereMax,
                talismanGarmonii1, talismanGarmonii2, talismanGarmoniiMax,
                talismanGrani1, talismanGrani2, talismanGraniMax,
                talismanDedala1, talismanDedala2, talismanDedalaMax,
                talismanExidni1, talismanExidni2, talismanExidniMax,
                talismanTritona1, talismanTritona2, talismanTritonaMax,
                talismanPhoenix1, talismanPhoenix2, talismanPhoenixMax,
                talismanKrusha, krushHelmet, krushChestplate, krushLeggins, krushBoots, krushSword, krushPickaxe,
                talismanKaratelya, satanaHelmet, satanaChestplate, satanaLeggins, satanaBoots, satanaSword, satanaPickaxe,
                kingHelmet, kingChestplate, kingLeggins, kingBoots, kingSword, kingPickaxe,
                princHelmet, princChestplate, princLeggins, princBoots, princSword, princPickaxe,
                tntTierWhite, tntTierBlack,
                expBottle15lvlFT, expBottle30lvlFT, expBottle50lvlFT,
                trapka, plast, ognenniySmerch, desorient, yavnayaPil, boziyaAura, snezokZamorozka,
                cursedSuol, otmichkaArmor, otmichkaTool, otmichkaWeapon, otmichkaItems, otmichkaSphere,
                katanaSword, stopArrow, sernayaKislota, pobedilka, agentPotion, killerPotion, medicPotion, flashPotion, otrizkaPotion

        };

        customAutoBuyItemList.addAll(Arrays.asList(customAutoBuyItems));
    }

    private static List<String> of(String... strings) {
        return List.of(strings);
    }

    private static CustomAutoBuyItem getCustomAutoBuyItemByName(String name) {
        return customAutoBuyItemList.stream().filter(customAutoBuyItem -> customAutoBuyItem.name.equalsIgnoreCase(name)).toList().get(0);
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

                    DefaultAutoBuyItem autoBuyItem = new DefaultAutoBuyItem(item, Integer.parseInt(line.split(":")[2]));

                    if (!items.contains(autoBuyItem)) items.add(autoBuyItem);
                }
                if (id == 1) {
                    CustomAutoBuyItem customAutoBuyItem = getCustomAutoBuyItemByName(line.split(":")[1]);
                    CustomAutoBuyItem add = new CustomAutoBuyItem(customAutoBuyItem.item, Integer.parseInt(line.split(":")[2]), customAutoBuyItem.isFTItem);
                    add.strings = customAutoBuyItem.strings;
                    add.enchantments = customAutoBuyItem.enchantments;
                    add.name = customAutoBuyItem.name;
                    add.strictCheck = customAutoBuyItem.strictCheck;

                    if (!items.contains(add)) items.add(add);
                }
            }

            br.close();
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