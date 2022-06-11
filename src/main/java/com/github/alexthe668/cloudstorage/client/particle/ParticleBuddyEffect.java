package com.github.alexthe668.cloudstorage.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleBuddyEffect extends TextureSheetParticle {

    private int type = 0;

    private ParticleBuddyEffect(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, int type) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.setAlpha(1);
        this.lifetime = 3 + random.nextInt(5);
        this.gravity = 0;
        this.type = type;
    }

    public void tick() {
        super.tick();
        if(type == 1){
            float ageScale = age / (float) lifetime;
            this.setAlpha(1F - ageScale);
        }
        this.xd *= 0.0D;
        this.yd *= 0.0D;
        this.zd *= 0.0D;
    }

    public void move(double x, double y, double z) {
        this.setBoundingBox(this.getBoundingBox().move(x, y, z));
        this.setLocationFromBoundingbox();
    }

    public int getLightColor(float partialTicks) {
        if(type == 0){
            float f = 1F - (((float)this.age + partialTicks) / (float)this.lifetime);
            f = Mth.clamp(f, 0.0F, 1.0F);
            int i = super.getLightColor(partialTicks);
            int j = i & 255;
            int k = i >> 16 & 255;
            j += (int)(f * 15.0F * 16.0F);
            if (j > 240) {
                j = 240;
            }

            return j | k << 16;
        }else{
          return super.getLightColor(partialTicks);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class StopSpawn implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public StopSpawn(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ParticleBuddyEffect p = new ParticleBuddyEffect(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, 0);
            p.pickSprite(spriteSet);
            return p;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Cool implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Cool(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ParticleBuddyEffect p = new ParticleBuddyEffect(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, 1);
            p.pickSprite(spriteSet);
            return p;
        }
    }
}
