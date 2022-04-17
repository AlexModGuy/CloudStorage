package com.github.alexthe668.cloudstorage;

import net.minecraftforge.common.ForgeConfigSpec;

public class CSConfig {

    public final ForgeConfigSpec.IntValue maxCloudSlots;
    public final ForgeConfigSpec.IntValue maxStaticCloudSlots;
    public final ForgeConfigSpec.DoubleValue cloudChestOpenDistance;
    public final ForgeConfigSpec.BooleanValue cloudChestNeedsSkyAccess;
    public final ForgeConfigSpec.BooleanValue balloonSalesmanVillager;
    public final ForgeConfigSpec.IntValue balloonStandSpawnWeight;
    public final ForgeConfigSpec.IntValue cloudHeight;
    public final ForgeConfigSpec.BooleanValue skyMobSpawning;
    public final ForgeConfigSpec.IntValue skyMobSpawningInterval;
    public final ForgeConfigSpec.DoubleValue skyMobSpawningChance;
    public final ForgeConfigSpec.BooleanValue skyMobSpawnsAroundCloudedPlayers;
    public final ForgeConfigSpec.BooleanValue skyMobSpawnsOnlyInDarkness;
    public final ForgeConfigSpec.IntValue skyMobMinSpawnDistance;
    public final ForgeConfigSpec.IntValue skyMobMaxSpawnDistance;
    public final ForgeConfigSpec.DoubleValue badloonToBloviatorRatio;
    public final ForgeConfigSpec.BooleanValue generateSkyTemples;
    public final ForgeConfigSpec.BooleanValue generateBigBalloons;
    public final ForgeConfigSpec.IntValue skyTempleMinSeperation;
    public final ForgeConfigSpec.IntValue skyTempleMaxSeperation;
    public final ForgeConfigSpec.IntValue bigBalloonMinSeperation;
    public final ForgeConfigSpec.IntValue bigBalloonMaxSeperation;

    public CSConfig(final ForgeConfigSpec.Builder builder) {
        builder.push("storage");
        maxCloudSlots = builder.comment(" maximum slots that can be uploaded to a cloud chest. A vanilla chest has 27 slots.").translation("max_cloud_slots").defineInRange("max_cloud_slots", 4096, 27, Integer.MAX_VALUE);
        maxStaticCloudSlots = builder.comment(" maximum slots that can be uploaded to a static cloud chest. A vanilla chest has 27 slots.").translation("max_static_cloud_slots").defineInRange("max_static_cloud_slots", 4096, 27, Integer.MAX_VALUE);
        cloudChestOpenDistance = builder.comment(" how far from a player will cloud chests play an opening animation").translation("cloud_chest_open_distance").defineInRange("cloud_chest_open_distance", 6.0D, 1.5D, 16.0D);
        cloudChestNeedsSkyAccess = builder.comment(" whether the cloud chest needs sky access to function").translation("cloud_chest_needs_sky_access").define("cloud_chest_needs_sky_access", true);
        builder.pop();
        builder.push("village");
        balloonSalesmanVillager = builder.comment(" whether the balloon salesman villager is enabled or not").translation("balloon_salesman_villager").define("balloon_salesman_villager", true);
        balloonStandSpawnWeight = builder.comment(" the weight of the balloon stand village structure spawning. set to zero to disable").translation("balloon_stand_spawn_weight").defineInRange("balloon_stand_spawn_weight", 2, 0, 100);
        builder.pop();
        builder.comment(" spawning of sky mobs is handled a bit differently than vanilla, since vanilla spawns are based on height map which would not work in this case.");
        builder.push("spawning");
        cloudHeight = builder.comment(" the y level that is considered 'cloud height' for mob spawns").translation("cloud_height").defineInRange("cloud_height", 196, -64, 320);
        skyMobSpawning = builder.comment(" can badloons and bloviators spawn naturally").translation("sky_mob_spawning").define("sky_mob_spawning", true);
        skyMobSpawningInterval = builder.comment(" interval, in ticks, of how often a sky mob is attempted to spawn. Default value is 1200 ticks, so every 60 seconds a badloon/bloviator spawn is attempted.").translation("sky_mob_spawn_interval").defineInRange("sky_mob_spawn_interval", 1200, 20, 48000);
        skyMobSpawningChance = builder.comment("  for every interval (see above), the percent chance that a sky mob(s) should actually spawn. Default is 0.2, so only 20% of intervals will successfully spawn a badloon/bloviator.").translation("sky_mob_spawn_chance").defineInRange("sky_mob_spawn_chance", 0.2D, 0D, 1D);
        skyMobSpawnsAroundCloudedPlayers = builder.comment(" whether sky mobs should only spawn around players that are above the cloud height, or randomly select any player to spawn around.").translation("sky_mob_spawn_around_clouded_players").define("sky_mob_spawn_around_clouded_players", true);
        skyMobSpawnsOnlyInDarkness = builder.comment(" whether sky mobs should only spawn in darkness. If false, sky mobs will spawn irrespective of sky light, but will not spawn near block light sources (like torches)").translation("sky_mob_spawn_only_in_darkness").define("sky_mob_spawn_only_in_darkness", false);
        skyMobMinSpawnDistance = builder.comment(" minimum distance away from the player sky mobs spawn at, in blocks").translation("sky_mob_min_spawn_distance").defineInRange("sky_mob_min_spawn_distance", 24, 1, 2048);
        skyMobMaxSpawnDistance = builder.comment(" maximum distance away from the player sky mobs spawn at, in blocks").translation("sky_mob_max_spawn_distance").defineInRange("sky_mob_max_spawn_distance", 48, 1, 2048);
        badloonToBloviatorRatio = builder.comment(" the percent chance that a sky mob spawn is a badloon rather than a bloviator").translation("badloon_to_bloviator_ratio").defineInRange("badloon_to_bloviator_ratio", 0.55D, 0D, 1D);
        builder.pop();
        builder.push("generation");
        generateSkyTemples = builder.comment(" can sky temples spawn in the world").translation("generate_sky_temples").define("generate_sky_temples", true);
        generateBigBalloons = builder.comment(" can big balloons spawn in the world").translation("generate_big_balloons").define("generate_big_balloons", true);
        skyTempleMinSeperation = builder.comment(" minimum distance, in chunks, that sky temples spawn from eachother.").translation("sky_temple_min_seperation").defineInRange("sky_temple_min_seperation", 15, 1, 4095);
        skyTempleMaxSeperation = builder.comment(" maximum distance, in chunks, that sky temples spawn from eachother.").translation("sky_temple_max_seperation").defineInRange("sky_temple_max_seperation", 24, 2, 4096);
        bigBalloonMinSeperation = builder.comment(" minimum distance, in chunks, that big balloons spawn from eachother.").translation("big_balloon_min_seperation").defineInRange("big_balloon_min_seperation", 16, 1, 4095);
        bigBalloonMaxSeperation = builder.comment(" maximum distance, in chunks, that big balloons spawn from eachother.").translation("big_balloon_max_seperation").defineInRange("big_balloon_max_seperation", 21, 2, 4096);
        builder.pop();

    }

}
