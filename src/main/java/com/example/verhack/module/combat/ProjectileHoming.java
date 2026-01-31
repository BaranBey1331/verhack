package com.example.verhack.module.combat;

import com.example.verhack.module.Category;
import com.example.verhack.module.Module;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

public class ProjectileHoming extends Module {
    private double range = 20.0;
    private double speed = 0.05; // Reduced for smoother maneuver
    private boolean glowTarget = false;

    public ProjectileHoming() {
        super("Projectile Homing", "Arrows track targets", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (mc().level == null || mc().player == null) return;

        for (net.minecraft.world.entity.Entity entity : mc().level.entitiesForRendering()) {
            if (entity instanceof AbstractArrow arrow && !arrow.onGround()) {
                if (arrow.getOwner() == mc().player) {
                    LivingEntity target = findTarget(arrow);
                    if (target != null) {
                        Vec3 targetPos = target.position().add(0, target.getEyeHeight() * 0.5, 0);
                        Vec3 dir = targetPos.subtract(arrow.position()).normalize();

                        // Use a smaller fraction of the direction to make it less abrupt
                        Vec3 currentVel = arrow.getDeltaMovement();
                        Vec3 targetVel = dir.scale(currentVel.length());

                        // Interpolate between current velocity and target velocity
                        Vec3 newVel = currentVel.scale(1.0 - speed).add(targetVel.scale(speed));
                        arrow.setDeltaMovement(newVel);

                        if (glowTarget) {
                            target.setGlowingTag(true);
                        }
                    }
                }
            }
        }
    }

    public boolean isGlowTarget() {
        return glowTarget;
    }

    public void setGlowTarget(boolean glowTarget) {
        this.glowTarget = glowTarget;
    }

    private LivingEntity findTarget(AbstractArrow arrow) {
        List<LivingEntity> targets = mc().level.getEntitiesOfClass(LivingEntity.class, arrow.getBoundingBox().inflate(range), e ->
                e != mc().player && e.isAlive()
        );
        targets.sort(Comparator.comparingDouble(e -> arrow.distanceTo(e)));
        return targets.isEmpty() ? null : targets.get(0);
    }
}
