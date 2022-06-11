package com.github.alexthe668.cloudstorage.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class ParticleCloudChest extends TextureSheetParticle {

    private static final int[] POSSIBLE_COLORS = {0XEDF4F6, 0XD0DBE2, 0XC0C5C7, 0XB1CEE0};
    private final float targetX;
    private final float targetY;
    private final float targetZ;
    private final float distX;
    private final float distZ;

    private ParticleCloudChest(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, int variant) {
        super(world, x, y, z);
        int color = selectColor(variant, this.random);
        float lvt_18_1_ = (float) (color >> 16 & 255) / 255.0F;
        float lvt_19_1_ = (float) (color >> 8 & 255) / 255.0F;
        float lvt_20_1_ = (float) (color & 255) / 255.0F;
        setColor(lvt_18_1_, lvt_19_1_, lvt_20_1_);
        targetX = (float) motionX;
        targetY = (float) motionY;
        targetZ = (float) motionZ;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.quadSize *= 0.4F + this.random.nextFloat() * 0.4F;
        this.lifetime = 25 + this.random.nextInt(6);
        distX = (float) (x - targetX);
        distZ = (float) (z - targetZ);
        this.setAlpha(0);
    }

    public static int selectColor(int variant, RandomSource rand) {
        return POSSIBLE_COLORS[rand.nextInt(POSSIBLE_COLORS.length - 1)];
    }

    public void tick() {
        super.tick();
        float ageScale = age / (float) lifetime;
        float radius = (1.0F - ageScale) * 4F;
        float angle = (float) (ageScale * Math.PI * 4F);
        double extraX = this.targetX + radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = this.targetZ + radius * Mth.cos(angle);
        double d2 = extraX - this.x;
        double d3 = (this.targetY - this.y) * ageScale;
        double d4 = extraZ - this.z;
        float speed = 0.07F;
        this.xd = d2 * speed * 0.7F;
        this.yd = d3 * speed * 0.7F;
        this.zd = d4 * speed * 0.7F;
        if (this.age < this.lifetime / 2) {
            this.setAlpha(((float) this.age - (float) (this.lifetime / 2)) / (float) this.lifetime);
        }
    }

    public void move(double x, double y, double z) {
        this.setBoundingBox(this.getBoundingBox().move(x, y, z));
        this.setLocationFromBoundingbox();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ParticleCloudChest p = new ParticleCloudChest(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, 0);
            p.pickSprite(spriteSet);
            return p;
        }
    }
}
