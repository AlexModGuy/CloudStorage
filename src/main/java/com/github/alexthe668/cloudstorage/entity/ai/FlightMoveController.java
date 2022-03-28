package com.github.alexthe668.cloudstorage.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FlightMoveController extends MoveControl {
    private final Mob parentEntity;
    private final float speedGeneral;
    private final boolean needsYSupport;


    public FlightMoveController(Mob mob, float speedGeneral, boolean needsYSupport) {
        super(mob);
        this.parentEntity = mob;
        this.speedGeneral = speedGeneral;
        this.needsYSupport = needsYSupport;
    }


    public FlightMoveController(Mob bird, float speedGeneral) {
        this(bird, speedGeneral, true);
    }

    public void tick() {
        if (this.operation == MoveControl.Operation.MOVE_TO) {
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
                parentEntity.setYRot(-((float) Mth.atan2(vector3d.x, vector3d.z)) * (180F / (float) Math.PI));
                parentEntity.yBodyRot = parentEntity.getYRot();
            }
        }
    }

}