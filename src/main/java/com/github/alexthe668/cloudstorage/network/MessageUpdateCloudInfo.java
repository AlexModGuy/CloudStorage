package com.github.alexthe668.cloudstorage.network;

import com.github.alexthe668.cloudstorage.CloudStorage;
import com.github.alexthe668.cloudstorage.misc.CloudInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageUpdateCloudInfo {

    public int balloonColor;
    public int usedSlots;
    public int slotCount;
    public int usedStaticSlots;
    public int staticSlotCount;

    public MessageUpdateCloudInfo(int balloonColor, int usedSlots, int slotCount, int usedStaticSlots, int staticSlotCount) {
        this.balloonColor = balloonColor;
        this.usedSlots = usedSlots;
        this.slotCount = slotCount;
        this.usedStaticSlots = usedStaticSlots;
        this.staticSlotCount = staticSlotCount;
    }

    public MessageUpdateCloudInfo() {
    }

    public static MessageUpdateCloudInfo read(FriendlyByteBuf buf) {
        return new MessageUpdateCloudInfo(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void write(MessageUpdateCloudInfo message, FriendlyByteBuf buf) {
        buf.writeInt(message.balloonColor);
        buf.writeInt(message.usedSlots);
        buf.writeInt(message.slotCount);
        buf.writeInt(message.usedStaticSlots);
        buf.writeInt(message.staticSlotCount);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageUpdateCloudInfo message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            Player player = context.get().getSender();
            if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                player = CloudStorage.PROXY.getClientSidePlayer();
            }
            if (player != null) {
                CloudStorage.PROXY.setClientCloudInfo(player, message.balloonColor, new CloudInfo(message.balloonColor, message.usedSlots, message.slotCount, message.usedStaticSlots, message.staticSlotCount));
            }
        }
    }
}