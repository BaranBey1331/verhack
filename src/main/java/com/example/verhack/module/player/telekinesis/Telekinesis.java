package com.example.verhack.module.player.telekinesis;

import com.example.verhack.module.Category;
import com.example.verhack.module.Module;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class Telekinesis extends Module {
    private Entity grabbedEntity = null;
    private double targetDistance = 5.0;

    public Telekinesis() {
        super("Telekinesis", "Move entities by holding right click", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (mc().player == null || mc().level == null || mc().screen != null) {
            grabbedEntity = null;
            return;
        }

        if (mc().options.keyUse.isDown()) {
            if (grabbedEntity == null) {
                if (mc().hitResult instanceof EntityHitResult entityHit) {
                    grabbedEntity = entityHit.getEntity();
                }
            }

            if (grabbedEntity != null) {
                if (!grabbedEntity.isAlive() || mc().player.distanceTo(grabbedEntity) > 30.0) {
                    releaseEntity();
                    return;
                }

                Vec3 look = mc().player.getLookAngle();
                Vec3 targetPos = mc().player.getEyePosition().add(look.scale(targetDistance));

                // Move entity using velocity for smoother, "real" telekinesis effect
                Vec3 entityPos = grabbedEntity.position().add(0, grabbedEntity.getEyeHeight() / 2.0, 0);
                Vec3 diff = targetPos.subtract(entityPos);

                double pullStrength = 0.3;
                Vec3 vel = diff.scale(pullStrength);

                // Limit max velocity to prevent crazy flying
                if (vel.length() > 2.0) {
                    vel = vel.normalize().scale(2.0);
                }

                grabbedEntity.setDeltaMovement(vel);
                grabbedEntity.fallDistance = 0;
                grabbedEntity.setOnGround(true);
            }
        } else if (grabbedEntity != null) {
            releaseEntity();
        }
    }

    private void releaseEntity() {
        if (grabbedEntity != null) {
            grabbedEntity.setDeltaMovement(0, 0, 0);
            grabbedEntity.fallDistance = 0;
            // Force set position again on release to prevent ghosting back
            grabbedEntity.setPos(grabbedEntity.getX(), grabbedEntity.getY(), grabbedEntity.getZ());
            grabbedEntity = null;
        }
    }

    @Override
    public void onDisable() {
        grabbedEntity = null;
    }

    public double getTargetDistance() { return targetDistance; }
    public void setTargetDistance(double targetDistance) { this.targetDistance = targetDistance; }
}
