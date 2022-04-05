package com.github.alexthe668.cloudstorage.network;

import com.github.alexthe668.cloudstorage.CloudStorage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageScrollCloudChest {

    public int scrollAmount;

    public MessageScrollCloudChest(int scrollAmount) {
        this.scrollAmount = scrollAmount;
    }


    public MessageScrollCloudChest() {
    }

    public static MessageScrollCloudChest read(FriendlyByteBuf buf) {
        return new MessageScrollCloudChest(buf.readInt());
    }

    public static void write(MessageScrollCloudChest message, FriendlyByteBuf buf) {
        buf.writeInt(message.scrollAmount);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageScrollCloudChest message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            Player player = context.get().getSender();
            if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                player = CloudStorage.PROXY.getClientSidePlayer();
            }
            if (player != null) {
                CloudStorage.PROXY.processScrollPacket(player, message.scrollAmount);
            }
        }
    }
}