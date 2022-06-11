package com.github.alexthe668.cloudstorage.network;

import com.github.alexthe668.cloudstorage.CloudStorage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageSortCloudChest  {

    public int type;

    public MessageSortCloudChest(int type) {
        this.type = type;
    }


    public MessageSortCloudChest() {
    }

    public static MessageSortCloudChest read(FriendlyByteBuf buf) {
        return new MessageSortCloudChest(buf.readInt());
    }

    public static void write(MessageSortCloudChest message, FriendlyByteBuf buf) {
        buf.writeInt(message.type);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageSortCloudChest message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            Player player = context.get().getSender();
            if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                player = CloudStorage.PROXY.getClientSidePlayer();
            }
            if (player != null) {
                CloudStorage.PROXY.processSortPacket(player, message.type);
            }
        }
    }
}