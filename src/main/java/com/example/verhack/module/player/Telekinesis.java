package com.example.verhack.module.player;

import com.example.verhack.module.Category;
import com.example.verhack.module.Module;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class Telekinesis extends Module {
    private Entity grabbedEntity = null;

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
                if (!grabbedEntity.isAlive() || mc().player.distanceTo(grabbedEntity) > 20.0) {
                    releaseEntity();
                    return;
                }

                Vec3 look = mc().player.getLookAngle();
                Vec3 targetPos = mc().player.getEyePosition().add(look.scale(5.0));

                // Smoother interpolation
                double lerpFactor = 0.2;
                double newX = grabbedEntity.getX() + (targetPos.x - grabbedEntity.getX()) * lerpFactor;
                double newY = grabbedEntity.getY() + (targetPos.y - grabbedEntity.getEyeHeight()/2.0 - grabbedEntity.getY()) * lerpFactor;
                double newZ = grabbedEntity.getZ() + (targetPos.z - grabbedEntity.getZ()) * lerpFactor;

                grabbedEntity.setPos(newX, newY, newZ);
                grabbedEntity.setDeltaMovement(0, 0, 0);
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
}
