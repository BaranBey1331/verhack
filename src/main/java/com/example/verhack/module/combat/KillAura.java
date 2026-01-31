package com.example.verhack.module.combat;

import com.example.verhack.module.Category;
import com.example.verhack.module.Module;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;

import java.util.Comparator;
import java.util.List;

public class KillAura extends Module {
    private double range = 4.5;
    private boolean rotations = true;
    private float rotationSpeed = 5.0f;

    public KillAura() {
        super("KillAura", "Automatically attacks entities around you", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (mc().player == null || mc().level == null || mc().screen != null) return;

        // Search for targets using a bounding box inflated by the range
        AABB area = mc().player.getBoundingBox().inflate(range);
        List<LivingEntity> targets = mc().level.getEntitiesOfClass(LivingEntity.class, area, e ->
                e != mc().player && e.isAlive() && mc().player.distanceTo(e) <= range
        );

        // Sort by distance
        targets.sort(Comparator.comparingDouble(e -> mc().player.distanceTo(e)));

        if (!targets.isEmpty()) {
            LivingEntity target = targets.get(0);

            // Look at the target before attacking
            if (rotations) {
                smoothLookAt(target);
            }

            // Attack logic: check attack strength (cooldown)
            if (mc().player.getAttackStrengthScale(0.0f) >= 0.9f) {
                attack(target);
            }
        }
    }

    private void smoothLookAt(Entity target) {
        if (mc().player == null) return;

        double diffX = target.getX() - mc().player.getX();
        double diffY = (target.getY() + target.getEyeHeight()) - (mc().player.getY() + mc().player.getEyeHeight());
        double diffZ = target.getZ() - mc().player.getZ();
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float targetYaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F);
        float targetPitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));

        float currentYaw = mc().player.getYRot();
        float currentPitch = mc().player.getXRot();

        // Smoothly interpolate yaw using lerp for natural feel
        float yawDiff = Mth.wrapDegrees(targetYaw - currentYaw);
        float speedFactor = rotationSpeed / 40.0f; // Scale speed for lerp
        float nextYaw = currentYaw + Mth.wrapDegrees(yawDiff * speedFactor);

        // Smoothly interpolate pitch
        float pitchDiff = targetPitch - currentPitch;
        float nextPitch = currentPitch + (pitchDiff * speedFactor);

        mc().player.setYRot(nextYaw);
        mc().player.setXRot(nextPitch);
        mc().player.yHeadRot = nextYaw;
    }

    private void attack(Entity target) {
        if (mc().gameMode != null && mc().player != null) {
            mc().gameMode.attack(mc().player, target);
            mc().player.swing(InteractionHand.MAIN_HAND);
        }
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public void incrementRange() {
        this.range += 0.5;
        if (this.range > 20.0) this.range = 2.0;
    }

    public boolean isRotations() {
        return rotations;
    }

    public void setRotations(boolean rotations) {
        this.rotations = rotations;
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }
}
