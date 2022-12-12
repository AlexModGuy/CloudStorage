package com.github.alexthe668.cloudstorage.client;

import com.github.alexthe666.citadel.client.event.EventPosePlayerHand;
import com.github.alexthe668.cloudstorage.CloudStorage;
import com.github.alexthe668.cloudstorage.CommonProxy;
import com.github.alexthe668.cloudstorage.block.CSBlockEntityRegistry;
import com.github.alexthe668.cloudstorage.client.gui.BalloonStandScreen;
import com.github.alexthe668.cloudstorage.client.gui.CloudChestScreen;
import com.github.alexthe668.cloudstorage.client.gui.GuideBookScreen;
import com.github.alexthe668.cloudstorage.client.model.PropellerHatModel;
import com.github.alexthe668.cloudstorage.client.model.baked.BakedModelFinalLayerFullbright;
import com.github.alexthe668.cloudstorage.client.particle.*;
import com.github.alexthe668.cloudstorage.client.render.*;
import com.github.alexthe668.cloudstorage.entity.CSEntityRegistry;
import com.github.alexthe668.cloudstorage.inventory.CSMenuRegistry;
import com.github.alexthe668.cloudstorage.item.BalloonItem;
import com.github.alexthe668.cloudstorage.item.CSItemRegistry;
import com.github.alexthe668.cloudstorage.item.CloudBlowerItem;
import com.github.alexthe668.cloudstorage.misc.CloudInfo;
import com.github.alexthe668.cloudstorage.network.MessageLeftClickCloudBlower;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientProxy extends CommonProxy {

    public static final ModelLayerLocation PROPELLER_HAT_MODEL = new ModelLayerLocation(new ResourceLocation("cloudstorage", "propeller_hat"), "main");
    private static final List<String> FULLBRIGHTS = ImmutableList.of("cloudstorage:static_cloud#");
    private static final Map<Integer, CloudInfo> CLIENT_CLOUD_INFO = new HashMap<>();
    private int visibleSlots;

    public static int getCloudInt(int color, boolean allSlots) {
        CloudInfo info = CLIENT_CLOUD_INFO.get(color);
        if (info == null) {
            return 0;
        } else {
            return allSlots ? info.getSlotCount() : info.getUsedSlots();
        }
    }

    public static int getStaticCloudInt(int color, boolean allSlots) {
        CloudInfo info = CLIENT_CLOUD_INFO.get(color);
        if (info == null) {
            return 0;
        } else {
            return allSlots ? info.getStaticSlotCount() : info.getUsedStaticSlots();
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onItemColors(RegisterColorHandlersEvent.Item event) {
        CloudStorage.LOGGER.info("loaded in item colorizer");
        event.register((stack, colorIn) -> colorIn != 1 ? -1 : getBalloonColorForRender(stack), CSItemRegistry.BALLOON_INVENTORY.get());
        event.register((stack, colorIn) -> colorIn != 1 ? -1 : getBalloonColorForRender(stack), CSItemRegistry.BALLOON.get());
        event.register((stack, colorIn) -> colorIn != 1 && colorIn != 3 ? -1 : getBalloonColorForRender(stack), CSItemRegistry.BALLOON_BUDDY_INVENTORY.get());
        event.register((stack, colorIn) -> colorIn != 1 && colorIn != 3 ? -1 : getBalloonColorForRender(stack), CSItemRegistry.BALLOON_BUDDY.get());
        event.register((stack, colorIn) -> colorIn != 2 ? -1 : getBalloonColorForRender(stack), CSItemRegistry.BALLOON_ARROW.get());
    }

    private static int getBalloonColorForRender(ItemStack stack) {
        int color = BalloonItem.getBalloonColor(stack);
        if (color == -1) {
            //TODO render rainbow colors properly
            return BalloonItem.DEFAULT_COLOR;
        } else {
            return color;
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        event.getRenderer(EntityType.VILLAGER).addLayer(new VillagerHatLayer(event.getRenderer(EntityType.VILLAGER)));
        event.getRenderer(EntityType.ZOMBIE_VILLAGER).addLayer(new VillagerHatLayer(event.getRenderer(EntityType.ZOMBIE_VILLAGER)));

        for (String skinType : event.getSkins()) {
            event.getSkin(skinType).addLayer(new CloudBlowerBackpackLayer(event.getSkin(skinType)));
        }
    }

    public static void setupParticles(RegisterParticleProvidersEvent event) {
        CloudStorage.LOGGER.debug("Registered particle factories");
        event.register(CSParticleRegistry.BALLOON_SHARD.get(), ParticleBalloonShard.Factory::new);
        event.register(CSParticleRegistry.CLOUD_CHEST.get(), ParticleCloudChest.Factory::new);
        event.register(CSParticleRegistry.STATIC_LIGHTNING.get(), new ParticleStaticLightning.Factory());
        event.register(CSParticleRegistry.BLOVIATOR_BREATH.get(), ParticleBloviatorBreath.Factory::new);
        event.register(CSParticleRegistry.STOP_SPAWN.get(), ParticleBuddyEffect.StopSpawn::new);
        event.register(CSParticleRegistry.COOL.get(), ParticleBuddyEffect.Cool::new);
    }

    public static void bakeEntityModels(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(PROPELLER_HAT_MODEL, () -> PropellerHatModel.createArmorLayer(new CubeDeformation(0.5F)));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientInit() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::bakeModels);

        EntityRenderers.register(CSEntityRegistry.BADLOON.get(), RenderBadloon::new);
        EntityRenderers.register(CSEntityRegistry.BADLOON_HAND.get(), RenderBadloonHand::new);
        EntityRenderers.register(CSEntityRegistry.BALLOON.get(), RenderBalloon::new);
        EntityRenderers.register(CSEntityRegistry.BALLOON_TIE.get(), RenderBalloonTie::new);
        EntityRenderers.register(CSEntityRegistry.BALLOON_CARGO.get(), RenderBalloonCargo::new);
        EntityRenderers.register(CSEntityRegistry.BLOVIATOR.get(), RenderBloviator::new);
        EntityRenderers.register(CSEntityRegistry.BALLOON_BUDDY.get(), RenderBalloonBuddy::new);
        //needs to be overwritten so that it renders falling tile entities
        EntityRenderers.register(EntityType.FALLING_BLOCK, RenderFallingBlockWithTE::new);
        BlockEntityRenderers.register(CSBlockEntityRegistry.CLOUD_CHEST.get(), RenderCloudChest::new);
        BlockEntityRenderers.register(CSBlockEntityRegistry.STATIC_CLOUD_CHEST.get(), RenderCloudChest::new);
        MenuScreens.register(CSMenuRegistry.CLOUD_CHEST_MENU.get(), CloudChestScreen::new);
        MenuScreens.register(CSMenuRegistry.BALLOON_STAND_MENU.get(), BalloonStandScreen::new);
    }

    @Override
    public void init() {
        super.init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientProxy::onAddLayers);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientProxy::bakeEntityModels);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientProxy::setupParticles);
    }

    @Override
    public Object getISTERProperties(boolean armor) {
        return new CSItemRenderProperties(armor);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onPoseHand(EventPosePlayerHand event) {
        LivingEntity player = (LivingEntity) event.getEntityIn();
        float f = Minecraft.getInstance().getFrameTime();
        boolean rightHandBalloon = false;
        boolean leftHandBalloon = false;
        boolean flag = false;
        float rightHandCloudBlower = 0F;
        float leftHandCloudBlower = 0F;
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof BalloonItem) {
            leftHandBalloon = leftHandBalloon || player.getMainArm() == HumanoidArm.LEFT;
            rightHandBalloon = rightHandBalloon || player.getMainArm() == HumanoidArm.RIGHT;
            flag = true;
        }
        if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof BalloonItem) {
            leftHandBalloon = leftHandBalloon || player.getMainArm() != HumanoidArm.LEFT;
            rightHandBalloon = rightHandBalloon || player.getMainArm() != HumanoidArm.RIGHT;
            flag = true;
        }
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof CloudBlowerItem) {
            if (player.getMainArm() == HumanoidArm.RIGHT) {
                rightHandCloudBlower = Math.max(rightHandCloudBlower, CloudBlowerItem.getLerpedUseTime(player.getItemInHand(InteractionHand.MAIN_HAND), f));
            } else {
                leftHandCloudBlower = Math.max(leftHandCloudBlower, CloudBlowerItem.getLerpedUseTime(player.getItemInHand(InteractionHand.MAIN_HAND), f));
            }
            flag = true;
        }
        if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof CloudBlowerItem) {
            if (player.getMainArm() == HumanoidArm.RIGHT) {
                leftHandCloudBlower = Math.max(leftHandCloudBlower, CloudBlowerItem.getLerpedUseTime(player.getItemInHand(InteractionHand.OFF_HAND), f));
            } else {
                rightHandCloudBlower = Math.max(rightHandCloudBlower, CloudBlowerItem.getLerpedUseTime(player.getItemInHand(InteractionHand.OFF_HAND), f));
            }
            flag = true;
        }
        if (flag) {
            if (leftHandBalloon) {
                event.getModel().leftArm.xRot = -(float) Math.toRadians(90F);
            }
            if (rightHandBalloon) {
                event.getModel().rightArm.xRot = -(float) Math.toRadians(90F);
            }
            float minArmAngle = (float) Math.toRadians(-270);
            float maxArmAngle = (float) Math.toRadians(50F);
            float yawDiff = (float)Math.toRadians(event.getEntityIn().getYRot() - ((LivingEntity) event.getEntityIn()).yBodyRot);
            if (leftHandCloudBlower > 0) {
                float f1 = Math.min(leftHandCloudBlower, 5F) / 5F;
                float f2 = (event.getModel().head.xRot - (float) Math.toRadians(75)) * f1;
                event.getModel().leftArm.xRot = Mth.clamp(f2, minArmAngle, maxArmAngle);
                event.getModel().leftArm.yRot = yawDiff * f1;
            }
            if (rightHandCloudBlower > 0) {
                float f1 = Math.min(rightHandCloudBlower, 5F) / 5F;
                float f2 = (event.getModel().head.xRot - (float) Math.toRadians(75)) * f1;
                event.getModel().rightArm.xRot = Mth.clamp(f2, minArmAngle, maxArmAngle);
                event.getModel().rightArm.yRot = yawDiff * f1;
            }
            event.setResult(Event.Result.ALLOW);
        }

    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onPostRenderEntity(RenderNameTagEvent event) {
        if (event.getEntity() instanceof FallingBlockEntity entity) {
            BlockState blockstate = entity.getBlockState();
            PoseStack stack = event.getPoseStack();
            if (blockstate.getRenderShape() == RenderShape.ENTITYBLOCK_ANIMATED) {
                stack.pushPose();
                stack.translate(-0.5D, 0, -0.5D);
                if (blockstate.hasProperty(HorizontalDirectionalBlock.FACING)) {
                    float f = blockstate.getValue(HorizontalDirectionalBlock.FACING).toYRot();
                    stack.translate(0.5D, 0.5D, 0.5D);
                    stack.mulPose(Axis.YP.rotationDegrees(-f));
                    stack.translate(-0.5D, -0.5D, -0.5D);
                }
                Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockstate, stack, event.getMultiBufferSource(), event.getPackedLight(), OverlayTexture.NO_OVERLAY);
                stack.popPose();
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onPlayerLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        CloudBlowerItem.onLeftClick(event.getEntity(), event.getEntity().getMainHandItem());
        CloudBlowerItem.onLeftClick(event.getEntity(), event.getEntity().getOffhandItem());
        CloudStorage.sendMSGToServer(new MessageLeftClickCloudBlower());
    }

    private void bakeModels(final ModelEvent.ModifyBakingResult e) {
        for (ResourceLocation id : e.getModels().keySet()) {
            if (FULLBRIGHTS.contains(id.toString())) {
                e.getModels().put(id, new BakedModelFinalLayerFullbright(e.getModels().get(id)));
            }
        }
        ItemProperties.register(CSItemRegistry.BALLOON.get(), new ResourceLocation("loot_or_static"), (stack, lvl, holder, i) -> {
            return BalloonItem.isLoot(stack) ? 1F : BalloonItem.isStatic(stack) ? 0.5F : 0;
        });
        ItemProperties.register(CSItemRegistry.BALLOON_INVENTORY.get(), new ResourceLocation("loot_or_static"), (stack, lvl, holder, i) -> {
            return BalloonItem.isLoot(stack) ? 1F : BalloonItem.isStatic(stack) ? 0.5F : 0;
        });
    }

    public int getVisibleCloudSlots() {
        return this.visibleSlots;
    }

    public void setVisibleCloudSlots(int i) {
        this.visibleSlots = i;
    }

    public Player getClientSidePlayer() {
        return Minecraft.getInstance().player;
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRenderWorldLastEvent(RenderLevelStageEvent event) {
        if(event.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS){
            CSItemRenderer.incrementRenderTick();
        }
    }


    public void setClientCloudInfo(Player player, int balloonColor, CloudInfo cloudInfo) {
        if (Minecraft.getInstance().player == player) {
            CLIENT_CLOUD_INFO.put(balloonColor, cloudInfo);
        }
    }

    public void openBookScreen(ItemStack itemStackIn) {
        Minecraft.getInstance().setScreen(new GuideBookScreen(itemStackIn));
    }

    public void onHoldingBalloon(LivingEntity holder, ItemStack balloon, boolean leftHanded) {
        super.onHoldingBalloon(holder, balloon, leftHanded);
        if (BalloonItem.isStatic(balloon)) {
            CSItemRenderer.renderBalloonStatic(holder, balloon, leftHanded);
        }
    }
}
