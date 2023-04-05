package com.github.alexthe668.cloudstorage.misc;

import com.github.alexthe668.cloudstorage.CloudStorage;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

public class CSDamageTypes {
    public static final ResourceKey<DamageType> SNEAK_BALLOON_ATTACK = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CloudStorage.MODID, "sneak_balloon_attack"));

    public static DamageSource causeSneakBalloonDamage(RegistryAccess registryAccess) {
        return new DamageSource(registryAccess.registry(Registries.DAMAGE_TYPE).get().getHolderOrThrow(SNEAK_BALLOON_ATTACK));

    }
}
