package com.github.alexthe668.cloudstorage.misc;

import com.github.alexthe668.cloudstorage.CloudStorage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CSSoundRegistry {

    public static final DeferredRegister<SoundEvent> DEF_REG = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CloudStorage.MODID);

    public static final RegistryObject<SoundEvent> BALLOON_HURT = createSoundEvent("balloon_hurt");
    public static final RegistryObject<SoundEvent> BALLOON_POP = createSoundEvent("balloon_pop");
    public static final RegistryObject<SoundEvent> BLOVIATOR_IDLE = createSoundEvent("bloviator_idle");
    public static final RegistryObject<SoundEvent> BLOVIATOR_HURT = createSoundEvent("bloviator_hurt");
    public static final RegistryObject<SoundEvent> BLOVIATOR_BLOW = createSoundEvent("bloviator_blow");
    public static final RegistryObject<SoundEvent> BLOVIATOR_LIGHTNING = createSoundEvent("bloviator_lightning");
    public static final RegistryObject<SoundEvent> CLOUD_CHEST_OPEN = createSoundEvent("cloud_chest_open");
    public static final RegistryObject<SoundEvent> CLOUD_CHEST_CLOSE = createSoundEvent("cloud_chest_close");
    public static final RegistryObject<SoundEvent> STATIC_SHOCK = createSoundEvent("static_shock");
    public static final RegistryObject<SoundEvent> MUSIC_DISC_DRIFT = createSoundEvent("music_disc_drift");

    private static RegistryObject<SoundEvent> createSoundEvent(final String soundName) {
        return DEF_REG.register(soundName, () -> new SoundEvent(new ResourceLocation(CloudStorage.MODID, soundName)));
    }
}
