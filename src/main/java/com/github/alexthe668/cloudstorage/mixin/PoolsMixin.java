package com.github.alexthe668.cloudstorage.mixin;

import com.github.alexthe668.cloudstorage.entity.villager.CSVillagerRegistry;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Pools.class)
public class PoolsMixin {

    @Inject(
            method = {"Lnet/minecraft/data/worldgen/Pools;register(Lnet/minecraft/world/level/levelgen/structure/pools/StructureTemplatePool;)Lnet/minecraft/core/Holder;"},
            remap = true,
            at = @At("HEAD"),
            cancellable = true
    )
    private static void di_register(StructureTemplatePool poolIn, CallbackInfoReturnable<Holder<StructureTemplatePool>> cir) {
        if(!CSVillagerRegistry.registeredHouses){
            CSVillagerRegistry.registerHouses();
        }
    }
}
