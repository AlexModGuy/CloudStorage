package com.github.alexthe668.cloudstorage.client.particle;

import com.github.alexthe668.cloudstorage.CloudStorage;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = CloudStorage.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CSParticleRegistry {

    public static final SimpleParticleType BALLOON_SHARD = (SimpleParticleType) new SimpleParticleType(false).setRegistryName(CloudStorage.MODID + ":balloon_shard");
    public static final SimpleParticleType CLOUD_CHEST = (SimpleParticleType) new SimpleParticleType(false).setRegistryName(CloudStorage.MODID + ":cloud_chest");
    public static final SimpleParticleType STATIC_LIGHTNING = (SimpleParticleType) new SimpleParticleType(false).setRegistryName(CloudStorage.MODID + ":static_lightning");
    public static final SimpleParticleType BLOVIATOR_BREATH = (SimpleParticleType) new SimpleParticleType(false).setRegistryName(CloudStorage.MODID + ":bloviator_breath");

    @SubscribeEvent
    public static void registerParticles(RegistryEvent.Register<ParticleType<?>> event) {
        try {
            for (Field f : CSParticleRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof ParticleType) {
                    event.getRegistry().register((ParticleType) obj);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
