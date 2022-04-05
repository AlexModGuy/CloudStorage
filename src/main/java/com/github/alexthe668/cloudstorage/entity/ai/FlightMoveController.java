package com.github.alexthe668.cloudstorage.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FlightMoveController extends MoveControl {
    private final Mob parentEntity;
    private final float speedGeneral;
    private final boolean needsYSupport;
    private final float maxTurnY;

    public FlightMoveController(Mob mob, float speedGeneral, boolean needsYSupport) {
        this(mob, speedGeneral, needsYSupport, 90F);
    }

    public FlightMoveController(Mob mob, float speedGeneral, boolean needsYSupport, float maxTurnY) {
        super(mob);
        this.parentEntity = mob;
        this.speedGeneral = speedGeneral;
        this.needsYSupport = needsYSupport;
        this.maxTurnY = maxTurnY;
    }


    public FlightMoveController(Mob bird, float speedGeneral) {
        this(bird, speedGeneral, true);
    }

    public void tick() {
        if (this.operation == MoveControl.Operation.STRAFE) {
            float f = (float)this.mob.getAttributeValue(Attributes.FLYING_SPEED);
            float f1 = (float)this.speedModifier * f;
            float f2 = this.strafeForwards;
            float f3 = this.strafeRight;
            Vec3 vector3d = new Vec3(f3, 0, -f2).yRot((float) (-parentEntity.getYRot() * (180F/Math.PI)));
            double d5 = vector3d.length();
            parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(vector3d.scale(this.speedModifier * 0.5F / d5)));

            this.operation = MoveControl.Operation.WAIT;
        } else if (this.operation == MoveControl.Operation.MOVE_TO) {
            Vec3 vector3d = new Vec3(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
            double d5 = vector3d.length();
            if (d5 < 0.3) {
                this.operation = MoveControl.Operation.WAIT;
                parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().scale(0.5D));
            } else {
                double d0 = this.wantedX - this.parentEntity.getX();
                double d1 = this.wantedY - this.parentEntity.getY();
                double d2 = this.wantedZ - this.parentEntity.getZ();
                double d3 = Mth.sqrt((float) (d0 * d0 + d1 * d1 + d2 * d2));
                parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(vector3d.scale(this.speedModifier * 0.05D / d5)));
                float f = -((float) Mth.atan2(vector3d.x, vector3d.z)) * (180F / (float) Math.PI);
                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f, this.maxTurnY));
                parentEntity.yBodyRot = parentEntity.getYRot();
            }
        }
    }

    public void stop() {
        this.operation = MoveControl.Operation.WAIT;
    }
}