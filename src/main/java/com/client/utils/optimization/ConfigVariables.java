package com.client.utils.optimization;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ConfigVariables {
    public static Set<String> blockEntityWhitelist = new HashSet(Arrays.asList("minecraft:beacon", "create:rope_pulley", "create:hose_pulley", "betterend:eternal_pedestal"));
    public static Set<String> entityWhitelist = new HashSet(Arrays.asList("botania:mana_burst"));
    public static Set<String> tickCullingWhitelist = new HashSet(Arrays.asList("minecraft:firework_rocket", "minecraft:boat"));
    public static int tracingDistance = 128;
    public static int sleepDelay = 10;
    public static boolean skipMarkerArmorStands = true;
    public static boolean tickCulling = true;
    public static boolean renderNametagsThroughWalls = true;
}