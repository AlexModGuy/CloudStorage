package com.github.alexthe668.cloudstorage;

import com.github.alexthe668.cloudstorage.inventory.CloudChestMenu;
import com.github.alexthe668.cloudstorage.inventory.ItemSorting;
import com.github.alexthe668.cloudstorage.item.CSItemRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CloudStorage.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonProxy {

    public void clientInit() {
    }

    public void init() {
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

    public void bakeEntityModels(final EntityRenderersEvent.RegisterLayerDefinitions event) {
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event){
        if(event.getItemStack().getItem() == CSItemRegistry.BALLOON.get()){
            event.setUseBlock(Event.Result.DENY);
        }
    }
}
