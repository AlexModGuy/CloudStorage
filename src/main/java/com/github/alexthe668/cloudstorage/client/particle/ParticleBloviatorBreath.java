package com.github.alexthe668.cloudstorage.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleBloviatorBreath extends TextureSheetParticle {

    private static final int[] POSSIBLE_COLORS = {0XEDF4F6, 0XD0DBE2, 0XC0C5C7, 0XB1CEE0};


    private ParticleBloviatorBreath(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, int variant) {
        super(world, x, y, z, motionX, motionY, motionZ);
        int color = selectColor(variant, this.random);
        float lvt_18_1_ = (float) (color >> 16 & 255) / 255.0F;
        float lvt_19_1_ = (float) (color >> 8 & 255) / 255.0F;
        float lvt_20_1_ = (float) (color & 255) / 255.0F;
        setColor(lvt_18_1_, lvt_19_1_, lvt_20_1_);
        this.quadSize *= 0.4F + this.random.nextFloat() * 0.4F;
        this.lifetime = 25 + this.random.nextInt(6);
        this.xd *= (double)0.1F;
        this.yd *= (double)0.1F;
        this.zd *= (double)0.1F;
        this.xd += motionX;
        this.yd += motionY;
        this.zd += motionZ;
        this.setAlpha(1);
    }

    public static int selectColor(int variant, RandomSource rand) {
        return POSSIBLE_COLORS[rand.nextInt(POSSIBLE_COLORS.length - 1)];
    }

    public void tick() {
        super.tick();
        float ageScale = age / (float) lifetime;
        this.setAlpha(1F - ageScale);
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
            ParticleBloviatorBreath p = new ParticleBloviatorBreath(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, 0);
            p.pickSprite(spriteSet);
            return p;
        }
    }
}
