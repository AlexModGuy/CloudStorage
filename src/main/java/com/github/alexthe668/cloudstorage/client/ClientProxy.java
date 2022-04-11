package com.github.alexthe668.cloudstorage.client;

import com.github.alexthe666.citadel.client.event.EventPosePlayerHand;
import com.github.alexthe668.cloudstorage.CloudStorage;
import com.github.alexthe668.cloudstorage.CommonProxy;
import com.github.alexthe668.cloudstorage.block.CSBlockEntityRegistry;
import com.github.alexthe668.cloudstorage.block.CSBlockRegistry;
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
import com.github.alexthe668.cloudstorage.misc.CloudInfo;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
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

    private static final List<String> FULLBRIGHTS = ImmutableList.of("cloudstorage:static_cloud#");
    public static final ModelLayerLocation PROPELLER_HAT_MODEL = new ModelLayerLocation(new ResourceLocation("cloudstorage", "propeller_hat"), "main");
    private static final Map<Integer, CloudInfo> CLIENT_CLOUD_INFO = new HashMap<>();
    private int visibleSlots;

    public static int getCloudInt(int color, boolean allSlots) {
        CloudInfo info = CLIENT_CLOUD_INFO.get(color);
        if(info == null){
            return 0;
        }else{
            return allSlots ? info.getSlotCount() : info.getUsedSlots();
        }
    }

    public static int getStaticCloudInt(int color, boolean allSlots) {
        CloudInfo info = CLIENT_CLOUD_INFO.get(color);
        if(info == null){
            return 0;
        }else{
            return allSlots ? info.getStaticSlotCount() : info.getUsedStaticSlots();
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientInit() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::bakeModels);
        ItemProperties.register(CSItemRegistry.BALLOON.get(), new ResourceLocation("static"), (stack, lvl, holder, i) -> {
            return BalloonItem.isStatic(stack) ? 1 : 0;
        });
        ItemProperties.register(CSItemRegistry.BALLOON_INVENTORY.get(), new ResourceLocation("static"), (stack, lvl, holder, i) -> {
            return BalloonItem.isStatic(stack) ? 1 : 0;
        });
        EntityRenderers.register(CSEntityRegistry.BADLOON.get(),  RenderBadloon::new);
        EntityRenderers.register(CSEntityRegistry.BADLOON_HAND.get(),  RenderBadloonHand::new);
        EntityRenderers.register(CSEntityRegistry.BALLOON.get(),  RenderBalloon::new);
        EntityRenderers.register(CSEntityRegistry.BALLOON_TIE.get(),  RenderBalloonTie::new);
        EntityRenderers.register(CSEntityRegistry.BALLOON_CARGO.get(),  RenderBalloonCargo::new);
        EntityRenderers.register(CSEntityRegistry.BLOVIATOR.get(),  RenderBloviator::new);
        EntityRenderers.register(CSEntityRegistry.BALLOON_BUDDY.get(),  RenderBalloonBuddy::new);
        //needs to be overwritten so that it renders falling tile entities
        EntityRenderers.register(EntityType.FALLING_BLOCK,  RenderFallingBlockWithTE::new);
        ItemBlockRenderTypes.setRenderLayer(CSBlockRegistry.CLOUD.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(CSBlockRegistry.STATIC_CLOUD.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(CSBlockRegistry.CLOUD_CHEST.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(CSBlockRegistry.STATIC_CLOUD_CHEST.get(), RenderType.translucent());
        BlockEntityRenderers.register(CSBlockEntityRegistry.CLOUD_CHEST.get(), RenderCloudChest::new);
        BlockEntityRenderers.register(CSBlockEntityRegistry.STATIC_CLOUD_CHEST.get(), RenderCloudChest::new);
        MenuScreens.register(CSMenuRegistry.CLOUD_CHEST_MENU, CloudChestScreen::new);
        MenuScreens.register(CSMenuRegistry.BALLOON_STAND_MENU, BalloonStandScreen::new);
    }

    @Override
    public void init(){
        super.init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientProxy::onAddLayers);
    }

    @Override
    public Object getISTERProperties(boolean armor) {
        return new CSItemRenderProperties(armor);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onItemColors(ColorHandlerEvent.Item event) {
        CloudStorage.LOGGER.info("loaded in item colorizer");
        event.getItemColors().register((stack, colorIn) -> colorIn != 1 ? -1 : BalloonItem.getBalloonColor(stack), CSItemRegistry.BALLOON_INVENTORY.get());
        event.getItemColors().register((stack, colorIn) -> colorIn != 1 ? -1 : BalloonItem.getBalloonColor(stack), CSItemRegistry.BALLOON.get());
        event.getItemColors().register((stack, colorIn) -> colorIn != 1 && colorIn != 3 ? -1 : BalloonItem.getBalloonColor(stack), CSItemRegistry.BALLOON_BUDDY_INVENTORY.get());
        event.getItemColors().register((stack, colorIn) -> colorIn != 1  && colorIn != 3 ? -1 : BalloonItem.getBalloonColor(stack), CSItemRegistry.BALLOON_BUDDY.get());
    }



    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onPoseHand(EventPosePlayerHand event) {
        LivingEntity player = (LivingEntity) event.getEntityIn();
        float f = Minecraft.getInstance().getFrameTime();
        boolean rightHand = false;
        boolean leftHand = false;
        boolean flag = false;
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof BalloonItem) {
            leftHand = leftHand || player.getMainArm() == HumanoidArm.LEFT;
            rightHand = rightHand|| player.getMainArm() == HumanoidArm.RIGHT;
            flag = true;
        }
        if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof BalloonItem) {
            leftHand = leftHand || player.getMainArm() != HumanoidArm.LEFT;
            rightHand = rightHand || player.getMainArm() != HumanoidArm.RIGHT;
            flag = true;
        }
        if(flag){
            if (leftHand && event.isLeftHand()) {
                event.getModel().leftArm.xRot = -(float) Math.toRadians(135F);
            }
            if (rightHand && !event.isLeftHand()) {
                event.getModel().rightArm.xRot = -(float) Math.toRadians(135F);
            }
        }

    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onPostRenderEntity(RenderNameplateEvent event) {
        if(event.getEntity() instanceof FallingBlockEntity entity){
            BlockState blockstate = entity.getBlockState();
            PoseStack stack = event.getPoseStack();
            if (blockstate.getRenderShape() == RenderShape.ENTITYBLOCK_ANIMATED) {
                stack.pushPose();
                stack.translate(-0.5D, 0, -0.5D);
                if(blockstate.hasProperty(HorizontalDirectionalBlock.FACING)){
                    float f = blockstate.getValue(HorizontalDirectionalBlock.FACING).toYRot();
                    stack.translate(0.5D, 0.5D, 0.5D);
                    stack.mulPose(Vector3f.YP.rotationDegrees(-f));
                    stack.translate(-0.5D, -0.5D, -0.5D);
                }
                Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockstate, stack, event.getMultiBufferSource(), event.getPackedLight(), OverlayTexture.NO_OVERLAY);
                stack.popPose();
            }

        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {

        event.getRenderer(EntityType.VILLAGER).addLayer(new VillagerHatLayer(event.getRenderer(EntityType.VILLAGER)));
        event.getRenderer(EntityType.ZOMBIE_VILLAGER).addLayer(new VillagerHatLayer(event.getRenderer(EntityType.ZOMBIE_VILLAGER)));

    }

    @Override
    public void setupParticles() {
        CloudStorage.LOGGER.debug("Registered particle factories");
        Minecraft.getInstance().particleEngine.register(CSParticleRegistry.BALLOON_SHARD, ParticleBalloonShard.Factory::new);
        Minecraft.getInstance().particleEngine.register(CSParticleRegistry.CLOUD_CHEST, ParticleCloudChest.Factory::new);
        Minecraft.getInstance().particleEngine.register(CSParticleRegistry.STATIC_LIGHTNING, new ParticleStaticLightning.Factory());
        Minecraft.getInstance().particleEngine.register(CSParticleRegistry.BLOVIATOR_BREATH, ParticleBloviatorBreath.Factory::new);
        Minecraft.getInstance().particleEngine.register(CSParticleRegistry.STOP_SPAWN, ParticleBuddyEffect.StopSpawn::new);
        Minecraft.getInstance().particleEngine.register(CSParticleRegistry.COOL, ParticleBuddyEffect.Cool::new);
    }

    private void bakeModels(final ModelBakeEvent e) {
        for (ResourceLocation id : e.getModelRegistry().keySet()) {
            if(FULLBRIGHTS.contains(id.toString())){
                e.getModelRegistry().put(id, new BakedModelFinalLayerFullbright(e.getModelRegistry().get(id)));
            }
        }
    }

    public void bakeEntityModels(final EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(PROPELLER_HAT_MODEL, () -> PropellerHatModel.createArmorLayer(new CubeDeformation(0.5F)));

    }

    public void setVisibleCloudSlots(int i){
        this.visibleSlots = i;
    }

    public int getVisibleCloudSlots(){
        return this.visibleSlots;
    }

    public Player getClientSidePlayer() {
        return Minecraft.getInstance().player;
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRenderWorldLastEvent(RenderLevelLastEvent event) {
        CSItemRenderer.incrementRenderTick();
    }


    public void setClientCloudInfo(Player player, int balloonColor, CloudInfo cloudInfo) {
        if(Minecraft.getInstance().player == player){
            CLIENT_CLOUD_INFO.put(balloonColor, cloudInfo);
        }
    }

    public void openBookScreen(ItemStack itemStackIn) {
        Minecraft.getInstance().setScreen(new GuideBookScreen(itemStackIn));
    }

}
