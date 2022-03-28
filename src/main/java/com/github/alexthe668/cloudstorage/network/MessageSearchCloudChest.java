package com.github.alexthe668.cloudstorage.network;

import com.github.alexthe668.cloudstorage.CloudStorage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageSearchCloudChest {

    public String search;

    public MessageSearchCloudChest(String search) {
        this.search = search;
    }


    public MessageSearchCloudChest() {
    }

    public static MessageSearchCloudChest read(FriendlyByteBuf buf) {
        return new MessageSearchCloudChest(buf.readUtf());
    }

    public static void write(MessageSearchCloudChest message, FriendlyByteBuf buf) {
        buf.writeUtf(message.search);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageSearchCloudChest message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            Player player = context.get().getSender();
            if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                player = CloudStorage.PROXY.getClientSidePlayer();
            }
            if (player != null) {
                CloudStorage.PROXY.processSearchPacket(player, message.search);
            }
        }
    }
}