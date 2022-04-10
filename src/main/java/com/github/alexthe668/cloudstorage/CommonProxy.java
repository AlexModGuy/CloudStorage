package com.github.alexthe668.cloudstorage;

import com.github.alexthe668.cloudstorage.entity.*;
import com.github.alexthe668.cloudstorage.entity.villager.CSVillagerRegistry;
import com.github.alexthe668.cloudstorage.inventory.CloudChestMenu;
import com.github.alexthe668.cloudstorage.inventory.ItemSorting;
import com.github.alexthe668.cloudstorage.item.BalloonItem;
import com.github.alexthe668.cloudstorage.item.CSItemRegistry;
import com.github.alexthe668.cloudstorage.misc.*;
import com.github.alexthe668.cloudstorage.network.MessageUpdateCloudInfo;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = CloudStorage.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonProxy {

    public static CSAdvancementTrigger UPLOAD_TRIGGER = new CSAdvancementTrigger(new ResourceLocation("cloudstorage:upload"));
    public static CSAdvancementTrigger LUFTBALLONS_TRIGGER = new CSAdvancementTrigger(new ResourceLocation("cloudstorage:luftballons"));
    private Random random = new Random();
    private static final Map<ServerLevel, SkyMobSpawner> SKY_MOB_SPAWNER_MAP = new HashMap<ServerLevel, SkyMobSpawner>();

    public void clientInit() {
    }

    public void init() {
        CriteriaTriggers.register(UPLOAD_TRIGGER);
        CriteriaTriggers.register(LUFTBALLONS_TRIGGER);
    }

    public void setupParticles() {
    }

    public Object getISTERProperties(boolean armor) {
        return null;
    }


    public void setVisibleCloudSlots(int i){
    }

    public int getVisibleCloudSlots(){
        return 0;
    }

    public Player getClientSidePlayer() {
        return null;
    }

    public void processSortPacket(Player player, int type) {
        if(player.containerMenu instanceof CloudChestMenu){
            if(type == 0){
                ((CloudChestMenu) player.containerMenu).sort(ItemSorting::defaultCompare);
            }
        }
    }

    public void processSearchPacket(Player player, String search) {
        if(player.containerMenu instanceof CloudChestMenu){
            ((CloudChestMenu) player.containerMenu).search(player, search);
        }
    }

    public void processScrollPacket(Player player, int scrollAmount) {
        if(player.containerMenu instanceof CloudChestMenu){
            ((CloudChestMenu) player.containerMenu).setScrollAmount(scrollAmount);
            player.containerMenu.sendAllDataToRemote();
        }
    }

    public void bakeEntityModels(final EntityRenderersEvent.RegisterLayerDefinitions event) {
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event){
        if(event.getItemStack().getItem() == CSItemRegistry.BALLOON.get()){
            event.setUseBlock(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        if (event.getRayTraceResult() instanceof EntityHitResult){
            Entity entity = ((EntityHitResult) event.getRayTraceResult()).getEntity();
            boolean flag = false;
            if(entity instanceof BalloonBuddyEntity buddy) {
                if (buddy.getPersonality() == BalloonFace.SMIRK) {
                    if (event.getEntity() instanceof AbstractArrow) {
                        //fixes soft crash with vanilla
                        ((AbstractArrow) event.getEntity()).setPierceLevel((byte) 0);
                    }
                    event.setCanceled(true);
                    boolean left = buddy.getRandom().nextBoolean();
                    Vec3 vector3d2 = new Vec3(0, 0, 1).yRot((float) ((left ? event.getProjectile().getYRot() - 90F : event.getProjectile().getYRot() + 90F) * Math.PI / 180F)).normalize();
                    buddy.hasImpulse = true;
                    buddy.setRotZ(left ? -45 : 45);
                    buddy.move(MoverType.SELF, new Vec3(vector3d2.x(), vector3d2.y(), vector3d2.z()));
                    flag = true;
                }
            }
            if(!flag && (entity instanceof LivingBalloon || entity instanceof BalloonEntity)){
                if(event.getProjectile().getOwner() instanceof AbstractSkeleton && entity instanceof BadloonEntity badloon){
                    badloon.dropMusicDisk = true;
                }
            }
        }
    }

    @SubscribeEvent
    public void onMobAttemptSpawn(LivingSpawnEvent.CheckSpawn event){
        if(event.getEntityLiving() instanceof Monster && random.nextFloat() < 0.5F){
            double dist = 64;
            AABB aabb = event.getEntityLiving().getBoundingBox().inflate(dist);
            List<BalloonBuddyEntity> balloonBuddies = event.getWorld().getEntitiesOfClass(BalloonBuddyEntity.class, aabb);
            if(!balloonBuddies.isEmpty()){
                for(BalloonBuddyEntity balloonBuddy : balloonBuddies){
                    if(balloonBuddy.getPersonality() == BalloonFace.HAPPY){
                        event.setResult(Event.Result.DENY);
                        balloonBuddy.getLevel().broadcastEntityEvent(balloonBuddy, (byte)68);
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onMobVisiblity(LivingEvent.LivingVisibilityEvent event){
        if(event.getEntityLiving() instanceof Player){
            double dist = 16;
            AABB aabb = event.getEntityLiving().getBoundingBox().inflate(dist);
            List<BalloonBuddyEntity> balloonBuddies = event.getEntityLiving().getLevel().getEntitiesOfClass(BalloonBuddyEntity.class, aabb);
            if(!balloonBuddies.isEmpty()){
                for(BalloonBuddyEntity balloonBuddy : balloonBuddies){
                    if(balloonBuddy.getPersonality() == BalloonFace.EYEPATCH){
                        event.modifyVisibility(0.25F);
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onVillagerTrades(VillagerTradesEvent event) {
        if (CloudStorage.CONFIG.balloonSalesmanVillager.get() && event.getType() == CSVillagerRegistry.BALLOON_SALESMAN) {
            List<VillagerTrades.ItemListing> level1 = new ArrayList<>();
            List<VillagerTrades.ItemListing> level2 = new ArrayList<>();
            List<VillagerTrades.ItemListing> level3 = new ArrayList<>();
            List<VillagerTrades.ItemListing> level4 = new ArrayList<>();
            List<VillagerTrades.ItemListing> level5 = new ArrayList<>();
            CSVillagerRegistry.initTrades(level1, level2, level3, level4, level5);
            event.getTrades().put(1, level1);
            event.getTrades().put(2, level2);
            event.getTrades().put(3, level3);
            event.getTrades().put(4, level4);
            event.getTrades().put(5, level5);
        }
    }

    public void processCloudInfoRequest(Player player, int balloonColor) {
        if(!player.getLevel().isClientSide){
            CSWorldData data = CSWorldData.get(player.getLevel());
            int usedSlots = 0;
            int allSlots = 0;
            int staticUsedSlots = 0;
            int staticAllSlots = 0;
            if(data != null){
                CloudIndex staticCloud = data.getPublicCloud(balloonColor);
                CloudIndex cloud = data.getPrivateCloud(player.getUUID(), balloonColor);
                if(cloud != null){
                    usedSlots = cloud.calcUsedSlots();
                    allSlots = cloud.getContainerSize();
                }
                if(staticCloud != null){
                    staticUsedSlots = staticCloud.calcUsedSlots();
                    staticAllSlots = staticCloud.getContainerSize();
                }
            }
            CloudStorage.sendMSGToAll(new MessageUpdateCloudInfo(balloonColor, usedSlots, allSlots, staticUsedSlots, staticAllSlots));
        }
    }

    public void setClientCloudInfo(Player player, int balloonColor, CloudInfo cloudInfo) {
    }

    public void openBookScreen(ItemStack itemStackIn) {
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.WorldTickEvent tick) {
        if (!tick.world.isClientSide && tick.world instanceof ServerLevel && CloudStorage.CONFIG.skyMobSpawning.get() && tick.world.getDifficulty() != Difficulty.PEACEFUL) {
            ServerLevel serverWorld = (ServerLevel) tick.world;
            if (SKY_MOB_SPAWNER_MAP.get(serverWorld) == null) {
                SKY_MOB_SPAWNER_MAP.put(serverWorld, new SkyMobSpawner(serverWorld));
            }
            SkyMobSpawner spawner = SKY_MOB_SPAWNER_MAP.get(serverWorld);
            spawner.tick();
        }
    }

    private static void checkForLuftballoons(Player player){
        int redBalloons = 0;
        Inventory inv = player.getInventory();
        for(int i = 0; i < inv.getContainerSize(); i++){
            if(inv.getItem(i).is(CSItemRegistry.BALLOON.get()) && BalloonItem.getBalloonColor(inv.getItem(i)) == BalloonItem.DEFAULT_COLOR){
                redBalloons += inv.getItem(i).getCount();
            }
            if(redBalloons >= 99){
                break;
            }
        }
        if(redBalloons >= 99 && player instanceof ServerPlayer){
            LUFTBALLONS_TRIGGER.trigger((ServerPlayer)player);
        }
    }

    @SubscribeEvent
    public void onPlayerCraft(PlayerEvent.ItemCraftedEvent event) {
        checkForLuftballoons(event.getPlayer());
    }

    @SubscribeEvent
    public void onPlayerPickup(PlayerEvent.ItemPickupEvent event) {
        checkForLuftballoons(event.getPlayer());
    }

    @SubscribeEvent
    public void onPlayerOpenContainer(PlayerContainerEvent event) {
        checkForLuftballoons(event.getPlayer());
    }
}
