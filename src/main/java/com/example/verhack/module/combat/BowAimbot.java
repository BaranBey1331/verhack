package com.example.verhack.module.combat;

import com.example.verhack.module.Category;
import com.example.verhack.module.Module;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.util.Mth;

import java.util.Comparator;
import java.util.List;

public class BowAimbot extends Module {
    private double range = 40.0;
    private float rotationSpeed = 15.0f;

    public BowAimbot() {
        super("Bow Aimbot", "Automatically aim at entities with bow", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (mc().player == null || mc().level == null || mc().screen != null) return;

        // Check if using bow
        if (!(mc().player.getUseItem().getItem() instanceof BowItem)) return;

        List<LivingEntity> targets = mc().level.getEntitiesOfClass(LivingEntity.class, mc().player.getBoundingBox().inflate(range), e ->
                e != mc().player && e.isAlive() && mc().player.distanceTo(e) <= range
        );

        targets.sort(Comparator.comparingDouble(e -> mc().player.distanceTo(e)));

        if (!targets.isEmpty()) {
            smoothLookAt(targets.get(0));
        }
    }

    private void smoothLookAt(Entity target) {
        double diffX = target.getX() - mc().player.getX();
        double diffY = (target.getY() + target.getEyeHeight() * 0.8) - (mc().player.getY() + mc().player.getEyeHeight());
        double diffZ = target.getZ() - mc().player.getZ();
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float targetYaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F);
        float targetPitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));

        // Basic prediction for bow
        double dist = mc().player.distanceTo(target);
        targetPitch -= (float) (dist * 0.1); // Simple arc compensation

        float nextYaw = mc().player.getYRot() + Mth.clamp(Mth.wrapDegrees(targetYaw - mc().player.getYRot()), -rotationSpeed, rotationSpeed);
        float nextPitch = mc().player.getXRot() + Mth.clamp(targetPitch - mc().player.getXRot(), -rotationSpeed, rotationSpeed);

        mc().player.setYRot(nextYaw);
        mc().player.setXRot(nextPitch);
        mc().player.yHeadRot = nextYaw;
    }
}
