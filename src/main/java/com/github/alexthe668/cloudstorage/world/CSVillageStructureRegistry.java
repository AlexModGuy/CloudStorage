package com.github.alexthe668.cloudstorage.world;

import com.github.alexthe666.citadel.server.generation.VillageHouseManager;
import com.github.alexthe668.cloudstorage.CloudStorage;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CSVillageStructureRegistry {

    public static final DeferredRegister<StructurePoolElementType<?>> DEF_REG = DeferredRegister.create(Registries.STRUCTURE_POOL_ELEMENT, CloudStorage.MODID);

    public static final RegistryObject<StructurePoolElementType<BalloonStandPoolElement>> BALLOON_STAND = DEF_REG.register("balloon_stand", () -> () -> BalloonStandPoolElement.CODEC);
    public static void registerHouses() {
        int weight = CloudStorage.CONFIG.balloonStandSpawnWeight.getDefault();
        StructurePoolElement plains = new BalloonStandPoolElement(new ResourceLocation(CloudStorage.MODID, "balloon_stand_plains"), StructurePoolElement.EMPTY);
        VillageHouseManager.register(new ResourceLocation("minecraft:village/plains/houses"), (pool) -> VillageHouseManager.addToPool(pool, plains, weight));
        StructurePoolElement desert = new BalloonStandPoolElement(new ResourceLocation(CloudStorage.MODID, "balloon_stand_desert"), StructurePoolElement.EMPTY);
        VillageHouseManager.register(new ResourceLocation("minecraft:village/desert/houses"), (pool) -> VillageHouseManager.addToPool(pool, desert, weight));
        StructurePoolElement savanna = new BalloonStandPoolElement(new ResourceLocation(CloudStorage.MODID, "balloon_stand_savanna"), StructurePoolElement.EMPTY);
        VillageHouseManager.register(new ResourceLocation("minecraft:village/savanna/houses"), (pool) -> VillageHouseManager.addToPool(pool, savanna, weight));
        StructurePoolElement snowy = new BalloonStandPoolElement(new ResourceLocation(CloudStorage.MODID, "balloon_stand_snowy"), StructurePoolElement.EMPTY);
        VillageHouseManager.register(new ResourceLocation("minecraft:village/snowy/houses"), (pool) -> VillageHouseManager.addToPool(pool, snowy, weight));
        StructurePoolElement taiga = new BalloonStandPoolElement(new ResourceLocation(CloudStorage.MODID, "balloon_stand_taiga"), StructurePoolElement.EMPTY);
        VillageHouseManager.register(new ResourceLocation("minecraft:village/taiga/houses"), (pool) -> VillageHouseManager.addToPool(pool, taiga, weight));
    }

}
