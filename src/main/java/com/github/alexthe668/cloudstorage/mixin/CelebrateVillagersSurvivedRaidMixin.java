package com.github.alexthe668.cloudstorage.mixin;

import com.github.alexthe668.cloudstorage.entity.villager.CSVillagerRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.CelebrateVillagersSurvivedRaid;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CelebrateVillagersSurvivedRaid.class)
public class CelebrateVillagersSurvivedRaidMixin {


    @Inject(
            method = {"Lnet/minecraft/world/entity/ai/behavior/CelebrateVillagersSurvivedRaid;tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;J)V"},
            remap = true,
            at = @At("HEAD"),
            cancellable = true
    )
    private void cs_tick(ServerLevel level, Villager villager, long l, CallbackInfo ci) {
        if(villager.getVillagerData().getProfession() == CSVillagerRegistry.BALLOON_SALESMAN.get()){
            CSVillagerRegistry.onBalloonCelebrate(level, villager);
            ci.cancel();
        }
    }
}
