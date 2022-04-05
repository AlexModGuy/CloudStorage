package com.github.alexthe668.cloudstorage.misc;

import com.github.alexthe668.cloudstorage.CSConfig;
import com.github.alexthe668.cloudstorage.CloudStorage;
import com.github.alexthe668.cloudstorage.entity.CSEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class SkyMobSpawner {
    private static final Predicate<? super ServerPlayer> ABOVE_CLOUD_HEIGHT = (serverPlayer -> serverPlayer.isAlive() && serverPlayer.getY() > CloudStorage.CONFIG.cloudHeight.get());
    private final Random random = new Random();
    private final ServerLevel world;
    private int timer;

    public SkyMobSpawner(ServerLevel world) {
        this.world = world;
        this.timer = CloudStorage.CONFIG.skyMobSpawningInterval.get();
        if (world.isNight()) {
            this.timer /= 2;
        }
    }

    public void tick() {
        if (this.timer-- <= 0) {
            this.timer = CloudStorage.CONFIG.skyMobSpawningInterval.get() + random.nextInt(CloudStorage.CONFIG.skyMobSpawningInterval.get() / 2);
            if (world.isNight()) {
                this.timer /= 2;
            }
            attemptSpawn();
        }
    }

    private boolean attemptSpawn() {
        Player playerentity = this.getRandomPlayer();
        if (playerentity == null || !this.world.dimensionType().hasSkyLight()) {
            return false;
        } else if (random.nextDouble() < CloudStorage.CONFIG.skyMobSpawningChance.get()) {
            return false;
        } else {
            BlockPos blockpos = new BlockPos(playerentity.position());
            double minDist = CloudStorage.CONFIG.skyMobMinSpawnDistance.get();
            double maxDist = CloudStorage.CONFIG.skyMobMaxSpawnDistance.get();
            BlockPos blockpos2 = this.generateFarAwayPos(blockpos, (int) minDist, (int) Math.max(maxDist, minDist + 1));
            if (blockpos2 != null && this.hasLightLevel(blockpos2) && blockpos2.distSqr(blockpos) > minDist * minDist) {
                EntityType<? extends Mob> type = createRandomMob(random);
                for (int i = 0; i < 1 + random.nextInt(2); i++) {
                    Mob mob = type.create(this.world);
                    mob.moveTo(Vec3.atCenterOf(blockpos2));
                    mob.finalizeSpawn(world, world.getCurrentDifficultyAt(blockpos2), MobSpawnType.NATURAL, null, null);
                    if (mob.checkSpawnObstruction(world)) {
                        mob.setYRot(random.nextFloat() * 360);
                        world.addFreshEntityWithPassengers(mob);
                    }
                }
                return true;
            }
            return false;
        }
    }

    private EntityType<? extends Mob> createRandomMob(Random random) {
        if (random.nextDouble() < CloudStorage.CONFIG.badloonToBloviatorRatio.get()) {
            return CSEntityRegistry.BADLOON.get();
        } else {
            return CSEntityRegistry.BLOVIATOR.get();
        }
    }

    private Player getRandomPlayer() {
        if (CloudStorage.CONFIG.skyMobSpawnsAroundCloudedPlayers.get()) {
            List<ServerPlayer> list = world.getPlayers(ABOVE_CLOUD_HEIGHT);
            return list.isEmpty() ? null : list.get(this.random.nextInt(list.size()));
        } else {
            return world.getRandomPlayer();
        }
    }

    @Nullable
    private BlockPos generateFarAwayPos(BlockPos center, int minDist, int maxDist) {
        BlockPos blockpos = null;
        for (int i = 0; i < 10; ++i) {
            int j = center.getX() + random.nextInt(maxDist * 2) - maxDist;
            int k = center.getZ() + random.nextInt(maxDist * 2) - maxDist;
            int cloudHeight = CloudStorage.CONFIG.cloudHeight.get() + random.nextInt(10);
            int l = this.world.getHeight(Heightmap.Types.WORLD_SURFACE, j, k);
            BlockPos blockpos1 = new BlockPos(j, Math.max(cloudHeight, l + 1), k);
            double d0 = (double)blockpos1.getX() - center.getX();
            double d2 = (double)blockpos1.getZ() - center.getZ();
            if (world.isEmptyBlock(blockpos1) && (d0 * d0 + d2 * d2) >= minDist * minDist) {
                blockpos = blockpos1;
                break;
            }
        }

        return blockpos;
    }

    private boolean hasLightLevel(BlockPos pos) {
        int blockLight = world.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = world.getBrightness(LightLayer.SKY, pos);
        if (CloudStorage.CONFIG.skyMobSpawnsOnlyInDarkness.get()) {
            return Math.max(skyLight, blockLight) < 2;
        } else {
            return blockLight < 2;
        }
    }
}
