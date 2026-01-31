package com.example.verhack.module.combat;

import com.example.verhack.module.Category;
import com.example.verhack.module.Module;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.List;

public class KillAura extends Module {
    private double range = 4.5;
    private int attackDelay = 0;

    public KillAura() {
        super("KillAura", "Automatically attacks entities around you", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (mc().player == null || mc().level == null || mc().screen != null) return;

        if (attackDelay > 0) {
            attackDelay--;
            return;
        }

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
            lookAt(target);

            attack(target);
            // Default cooldown: roughly 0.5 seconds (10 ticks)
            attackDelay = 10;
        }
    }

    private void lookAt(Entity target) {
        if (mc().player == null) return;

        double diffX = target.getX() - mc().player.getX();
        double diffY = (target.getY() + target.getEyeHeight()) - (mc().player.getY() + mc().player.getEyeHeight());
        double diffZ = target.getZ() - mc().player.getZ();
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F);
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));

        mc().player.setYRot(yaw);
        mc().player.setXRot(pitch);
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
        if (this.range > 10.0) this.range = 2.0;
    }
}
