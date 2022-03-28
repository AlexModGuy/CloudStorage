package com.github.alexthe668.cloudstorage.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleBalloonShard extends TextureSheetParticle {

    private float initialRoll = 0;

    private ParticleBalloonShard(ClientLevel world, double x, double y, double z, double rIn, double gIn, double bIn, SpriteSet sprites) {
        super(world, x, y, z);
        this.friction = 0.96F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.setColor((float)rIn, (float)gIn, (float)bIn);
        this.xd = (random.nextFloat() - 0.5F) * 0.7F;
        this.yd = (random.nextFloat() - 0.3F) * 0.7F;
        this.zd = (random.nextFloat() - 0.5F) * 0.7F;
        this.xo += xd * 3;
        this.yo += yd * 3;
        this.zo += zd * 3;
        this.quadSize = 0.15F + this.random.nextFloat() * 0.2F;
        this.lifetime = 20 + this.random.nextInt(20);
        this.gravity = 0.08F;
        this.pickSprite(sprites);
        float f = Mth.sqrt((float) (xd * xd + zd * zd));
        float f1 = -(float)Mth.atan2(yd, f) + (float)Math.toRadians(135);
        this.roll = f1 * 2F;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.xd *= 0.8;
        this.yd *= 0.8;
        this.zd *= 0.8;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.oRoll = this.roll;
            if (!this.onGround) {
                float dist = -initialRoll / (this.lifetime - 6) * Math.min(this.age, this.lifetime - 6);
                this.roll += 0 + (float)Math.sin(age * 0.3F) * 0.5F * (this.age / (float)lifetime);
            }
            this.move(this.xd, this.yd, this.zd);
            this.yd -= (double)this.gravity;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ParticleBalloonShard(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
        }
    }
}
