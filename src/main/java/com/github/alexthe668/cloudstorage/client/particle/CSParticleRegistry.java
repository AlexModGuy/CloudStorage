package com.github.alexthe668.cloudstorage.client.particle;

import com.github.alexthe668.cloudstorage.CloudStorage;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CSParticleRegistry {

    public static final DeferredRegister<ParticleType<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, CloudStorage.MODID);

    public static final RegistryObject<SimpleParticleType> BALLOON_SHARD = DEF_REG.register("balloon_shard", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> CLOUD_CHEST = DEF_REG.register("cloud_chest", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> STATIC_LIGHTNING = DEF_REG.register("static_lightning", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> BLOVIATOR_BREATH = DEF_REG.register("bloviator_breath", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> STOP_SPAWN = DEF_REG.register("stop_spawn", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> COOL = DEF_REG.register("cool", () -> new SimpleParticleType(false));

}
