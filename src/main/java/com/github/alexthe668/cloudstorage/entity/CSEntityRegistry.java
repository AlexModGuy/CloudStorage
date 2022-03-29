package com.github.alexthe668.cloudstorage.entity;

import com.github.alexthe668.cloudstorage.CloudStorage;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = CloudStorage.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CSEntityRegistry {

    public static final DeferredRegister<EntityType<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.ENTITIES, CloudStorage.MODID);
    public static final RegistryObject<EntityType<BadloonEntity>> BADLOON = DEF_REG.register("badloon", () -> EntityType.Builder.of(BadloonEntity::new, MobCategory.MONSTER).sized(0.7F, 0.9F).build("badloon"));
    public static final RegistryObject<EntityType<BadloonHandEntity>> BADLOON_HAND = DEF_REG.register("badloon_hand", () -> (EntityType)EntityType.Builder.of(BadloonHandEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(BadloonHandEntity::new).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true).updateInterval(1).clientTrackingRange(12).build("badloon_hand"));
    public static final RegistryObject<EntityType<BalloonEntity>> BALLOON = DEF_REG.register("balloon", () -> (EntityType)EntityType.Builder.of(BalloonEntity::new, MobCategory.MISC).sized(0.7F, 0.9F).setCustomClientFactory(BalloonEntity::new).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true).updateInterval(10).clientTrackingRange(20).build("balloon"));
    public static final RegistryObject<EntityType<BalloonTieEntity>> BALLOON_TIE = DEF_REG.register("balloon_tie", () -> (EntityType)EntityType.Builder.of(BalloonTieEntity::new, MobCategory.MISC).sized(0.4F, 0.55F).setCustomClientFactory(BalloonTieEntity::new).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true).updateInterval(10).clientTrackingRange(20).build("balloon_tie"));
    public static final RegistryObject<EntityType<BalloonCargoEntity>> BALLOON_CARGO = DEF_REG.register("balloon_cargo", () -> (EntityType)EntityType.Builder.of(BalloonCargoEntity::new, MobCategory.MISC).sized(0.99F, 0.99F).setCustomClientFactory(BalloonCargoEntity::new).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true).updateInterval(10).clientTrackingRange(20).build("balloon_cargo"));
    public static final RegistryObject<EntityType<BloviatorEntity>> BLOVIATOR = DEF_REG.register("bloviator", () -> EntityType.Builder.of(BloviatorEntity::new, MobCategory.MONSTER).sized(2F, 1.3F).build("bloviator"));

    @SubscribeEvent
    public static void initializeAttributes(EntityAttributeCreationEvent event) {
        event.put(BADLOON.get(), BadloonEntity.bakeAttributes().build());
        event.put(BLOVIATOR.get(), BloviatorEntity.bakeAttributes().build());
    }


}
