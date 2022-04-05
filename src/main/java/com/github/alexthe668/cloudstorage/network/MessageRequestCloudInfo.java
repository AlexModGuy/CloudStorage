package com.github.alexthe668.cloudstorage.network;

import com.github.alexthe668.cloudstorage.CloudStorage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageRequestCloudInfo {

    public int balloonColor;

    public MessageRequestCloudInfo(int balloonColor) {
        this.balloonColor = balloonColor;
    }


    public MessageRequestCloudInfo() {
    }

    public static MessageRequestCloudInfo read(FriendlyByteBuf buf) {
        return new MessageRequestCloudInfo(buf.readInt());
    }

    public static void write(MessageRequestCloudInfo message, FriendlyByteBuf buf) {
        buf.writeInt(message.balloonColor);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageRequestCloudInfo message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            Player player = context.get().getSender();
            if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                player = CloudStorage.PROXY.getClientSidePlayer();
            }
            if (player != null) {
                CloudStorage.PROXY.processCloudInfoRequest(player, message.balloonColor);
            }
        }
    }
}