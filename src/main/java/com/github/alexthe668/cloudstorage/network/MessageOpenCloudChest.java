package com.github.alexthe668.cloudstorage.network;

import com.github.alexthe668.cloudstorage.CloudStorage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageOpenCloudChest {

    public int containerSize;

    public MessageOpenCloudChest(int containerSize) {
        this.containerSize = containerSize;
    }

    public MessageOpenCloudChest() {
    }

    public static MessageOpenCloudChest read(FriendlyByteBuf buf) {
        return new MessageOpenCloudChest(buf.readInt());
    }

    public static void write(MessageOpenCloudChest message, FriendlyByteBuf buf) {
        buf.writeInt(message.containerSize);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageOpenCloudChest message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            CloudStorage.PROXY.setVisibleCloudSlots(message.containerSize);
        }
    }
}