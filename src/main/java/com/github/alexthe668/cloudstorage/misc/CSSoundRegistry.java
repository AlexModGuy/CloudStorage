package com.github.alexthe668.cloudstorage.misc;

import com.github.alexthe668.cloudstorage.CloudStorage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = CloudStorage.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CSSoundRegistry {

    public static final SoundEvent BALLOON_HURT = createSoundEvent("balloon_hurt");
    public static final SoundEvent BALLOON_POP = createSoundEvent("balloon_pop");
    public static final SoundEvent BLOVIATOR_IDLE = createSoundEvent("bloviator_idle");
    public static final SoundEvent BLOVIATOR_HURT = createSoundEvent("bloviator_hurt");
    public static final SoundEvent BLOVIATOR_BLOW = createSoundEvent("bloviator_blow");
    public static final SoundEvent BLOVIATOR_LIGHTNING = createSoundEvent("bloviator_lightning");
    public static final SoundEvent CLOUD_CHEST_OPEN = createSoundEvent("cloud_chest_open");
    public static final SoundEvent CLOUD_CHEST_CLOSE = createSoundEvent("cloud_chest_close");
    public static final SoundEvent STATIC_SHOCK = createSoundEvent("static_shock");

    private static SoundEvent createSoundEvent(final String soundName) {
        final ResourceLocation soundID = new ResourceLocation(CloudStorage.MODID, soundName);
        return new SoundEvent(soundID).setRegistryName(soundID);
    }

    @SubscribeEvent
    public static void registerSoundEvents(final RegistryEvent.Register<SoundEvent> event) {
        try {
            for (Field f : CSSoundRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof SoundEvent) {
                    event.getRegistry().register((SoundEvent) obj);
                } else if (obj instanceof SoundEvent[]) {
                    for (SoundEvent soundEvent : (SoundEvent[]) obj) {
                        event.getRegistry().register(soundEvent);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
