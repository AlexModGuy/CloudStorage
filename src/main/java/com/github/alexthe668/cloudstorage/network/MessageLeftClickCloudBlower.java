package com.github.alexthe668.cloudstorage.network;

import com.github.alexthe668.cloudstorage.item.CloudBlowerItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageLeftClickCloudBlower {

    public MessageLeftClickCloudBlower() {
    }

    public static MessageLeftClickCloudBlower read(FriendlyByteBuf buf) {
        return new MessageLeftClickCloudBlower();
    }

    public static void write(MessageLeftClickCloudBlower message, FriendlyByteBuf buf) {
    }

    public static class Handler {

        public Handler() {
        }

        public static void handle(MessageLeftClickCloudBlower message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            context.get().enqueueWork(() -> {
                Player player = context.get().getSender();
                if (player != null) {
                    CloudBlowerItem.onLeftClick(player, player.getItemInHand(InteractionHand.OFF_HAND));
                    CloudBlowerItem.onLeftClick(player, player.getItemInHand(InteractionHand.MAIN_HAND));
                }
            });
        }
    }
}
