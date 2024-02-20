package com.github.alexthe668.cloudstorage.command;

import com.github.alexthe668.cloudstorage.item.BalloonItem;
import com.github.alexthe668.cloudstorage.item.CSItemRegistry;
import com.github.alexthe668.cloudstorage.misc.CloudIndex;
import com.github.alexthe668.cloudstorage.world.CSWorldData;
import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;

public class RetrieveBalloonsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("retrieveballoons").requires((sender) -> {
            return sender.hasPermission(2);
        }).executes((sender) -> {
            return execute(sender.getSource(), ImmutableList.of(sender.getSource().getEntityOrException()));
        }).then(Commands.argument("targets", EntityArgument.entities()).executes((sender) -> {
            return execute(sender.getSource(), EntityArgument.getEntities(sender, "targets"));
        })));
        dispatcher.register(Commands.literal("retrievestaticballoons").requires((sender) -> {
            return sender.hasPermission(2);
        }).executes((sender) -> {
            return executePublic(sender.getSource());
        }));
    }
    private static int execute(CommandSourceStack command, Collection<? extends Entity> retrieveFor){
        CSWorldData worldData = CSWorldData.get(command.getLevel());
        ItemStack balloon = new ItemStack(CSItemRegistry.BALLOON.get());
        int count = 0;
        Entity lastEntity = command.getPlayer();
        if(worldData != null){
            for(Entity entity : retrieveFor){
                for(CloudIndex index : worldData.getAllPrivateCloudsFor(entity.getUUID())){
                    addBalloon(balloon, index.getBalloonColor(), command.getPlayer());
                    count++;
                }
                lastEntity = entity;
            }
        }
        if(count > 0){
            command.source.sendSystemMessage(Component.translatable("commands.retrieveballoons.success", count, lastEntity.getDisplayName()));
        }else{
            command.sendFailure(Component.translatable("commands.retrieveballoons.failure", count,  lastEntity.getDisplayName()));
        }

        return Command.SINGLE_SUCCESS;
    }

    private static MutableComponent createMessage(int count, Component displayName) {
        return Component.translatable("commands.retrieveballoons.success", count, displayName);
    }

    private static int executePublic(CommandSourceStack command){
        CSWorldData worldData = CSWorldData.get(command.getLevel());
        ItemStack staticBalloon = new ItemStack(CSItemRegistry.BALLOON.get());
        BalloonItem.setStatic(staticBalloon, true);
        int count = 0;
        if(worldData != null){
            for(CloudIndex index : worldData.getAllPublicClouds()){
                addBalloon(staticBalloon, index.getBalloonColor(), command.getPlayer());
                count++;
            }
        }
        if(count > 0){
            command.source.sendSystemMessage(Component.translatable("commands.retrievestaticballoons.success", count));
        }else{
            command.sendFailure(Component.translatable("commands.retrievestaticballoons.failure", count));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static void addBalloon(ItemStack base, int color, Player player){
        ItemStack balloon = base.copy();
        if(balloon.getItem() instanceof BalloonItem balloonItem){
            balloonItem.setColor(balloon, color);
        }
        if(!player.addItem(balloon)){
             player.drop(balloon, false);
        }
    }
}
