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
    private double speed = 0.2;
    private boolean glowTarget = false;

    public ProjectileHoming() {
        super("Projectile Homing", "Arrows track targets", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (mc().level == null || mc().player == null) return;

        for (net.minecraft.world.entity.Entity entity : mc().level.allEntities()) {
            if (entity instanceof AbstractArrow arrow && !arrow.isOnGround()) {
                if (arrow.getOwner() == mc().player) {
                    LivingEntity target = findTarget(arrow);
                    if (target != null) {
                        Vec3 targetPos = target.position().add(0, target.getEyeHeight() * 0.5, 0);
                        Vec3 dir = targetPos.subtract(arrow.position()).normalize();
                        Vec3 newVel = arrow.getDeltaMovement().add(dir.scale(speed)).normalize().scale(arrow.getDeltaMovement().length());
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
