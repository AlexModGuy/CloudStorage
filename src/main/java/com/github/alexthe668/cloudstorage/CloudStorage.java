package com.github.alexthe668.cloudstorage;

import com.github.alexthe668.cloudstorage.block.CSBlockEntityRegistry;
import com.github.alexthe668.cloudstorage.block.CSBlockRegistry;
import com.github.alexthe668.cloudstorage.block.CSPOIRegistry;
import com.github.alexthe668.cloudstorage.client.ClientProxy;
import com.github.alexthe668.cloudstorage.client.particle.CSParticleRegistry;
import com.github.alexthe668.cloudstorage.entity.CSEntityRegistry;
import com.github.alexthe668.cloudstorage.entity.villager.CSVillagerRegistry;
import com.github.alexthe668.cloudstorage.inventory.CSMenuRegistry;
import com.github.alexthe668.cloudstorage.item.CSItemRegistry;
import com.github.alexthe668.cloudstorage.misc.CSRecipeRegistry;
import com.github.alexthe668.cloudstorage.misc.CSSoundRegistry;
import com.github.alexthe668.cloudstorage.network.*;
import com.github.alexthe668.cloudstorage.world.CSStructureRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("cloudstorage")
public class CloudStorage
{
    public static final String MODID = "cloudstorage";
    public static final Logger LOGGER = LogManager.getLogger();
    public static CommonProxy PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    private static int packetsRegistered = 0;
    public static final SimpleChannel NETWORK_WRAPPER;
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    public static final CSConfig CONFIG;
    private static final ForgeConfigSpec CONFIG_SPEC;

    static {
        final Pair<CSConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CSConfig::new);
        CONFIG = specPair.getLeft();
        CONFIG_SPEC = specPair.getRight();
    }

    static {
        NetworkRegistry.ChannelBuilder channel = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MODID, "main_channel"));
        String version = PROTOCOL_VERSION;
        version.getClass();
        channel = channel.clientAcceptedVersions(version::equals);
        version = PROTOCOL_VERSION;
        version.getClass();
        NETWORK_WRAPPER = channel.serverAcceptedVersions(version::equals).networkProtocolVersion(() -> {
            return PROTOCOL_VERSION;
        }).simpleChannel();
    }

    public CloudStorage() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setupClient);
        bus.addListener(this::setup);
        final ModLoadingContext modLoadingContext = ModLoadingContext.get();
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, CONFIG_SPEC, "cloud-storage.toml");
        CSBlockRegistry.DEF_REG.register(bus);
        CSItemRegistry.DEF_REG.register(bus);
        CSEntityRegistry.DEF_REG.register(bus);
        CSBlockEntityRegistry.DEF_REG.register(bus);
        CSPOIRegistry.DEF_REG.register(bus);
        CSStructureRegistry.STRUCTURE_PIECE_DEF_REG.register(bus);
        CSStructureRegistry.STRUCTURE_TYPE_DEF_REG.register(bus);
        CSSoundRegistry.DEF_REG.register(bus);
        CSMenuRegistry.DEF_REG.register(bus);
        CSVillagerRegistry.DEF_REG.register(bus);
        CSRecipeRegistry.DEF_REG.register(bus);
        CSParticleRegistry.DEF_REG.register(bus);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(PROXY);
        PROXY.init();
    }

    private void setupClient(FMLClientSetupEvent event) {
        PROXY.clientInit();
    }


    private void setup(final FMLCommonSetupEvent event) {
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, MessageSortCloudChest.class, MessageSortCloudChest::write, MessageSortCloudChest::read, MessageSortCloudChest.Handler::handle);
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, MessageSearchCloudChest.class, MessageSearchCloudChest::write, MessageSearchCloudChest::read, MessageSearchCloudChest.Handler::handle);
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, MessageRequestCloudInfo.class, MessageRequestCloudInfo::write, MessageRequestCloudInfo::read, MessageRequestCloudInfo.Handler::handle);
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, MessageUpdateCloudInfo.class, MessageUpdateCloudInfo::write, MessageUpdateCloudInfo::read, MessageUpdateCloudInfo.Handler::handle);
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, MessageScrollCloudChest.class, MessageScrollCloudChest::write, MessageScrollCloudChest::read, MessageScrollCloudChest.Handler::handle);
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, MessageOpenCloudChest.class, MessageOpenCloudChest::write, MessageOpenCloudChest::read, MessageOpenCloudChest.Handler::handle);
    }

    public static <MSG> void sendMSGToAll(MSG message) {
        for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            sendNonLocal(message, player);
        }
    }

    public static <MSG> void sendNonLocal(MSG msg, ServerPlayer player) {
        NETWORK_WRAPPER.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
