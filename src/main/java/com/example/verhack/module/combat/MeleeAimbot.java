package com.example.verhack.module.combat;

import com.example.verhack.module.Category;
import com.example.verhack.module.Module;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.util.Mth;

import java.util.Comparator;
import java.util.List;

public class MeleeAimbot extends Module {
    private double range = 6.0;
    private float rotationSpeed = 10.0f;

    public MeleeAimbot() {
        super("Melee Aimbot", "Automatically aim at entities with swords/axes", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (mc().player == null || mc().level == null || mc().screen != null) return;

        // Check if holding melee weapon
        if (!(mc().player.getMainHandItem().getItem() instanceof SwordItem) &&
            !(mc().player.getMainHandItem().getItem() instanceof AxeItem)) return;

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
        double diffY = (target.getY() + target.getEyeHeight() * 0.5) - (mc().player.getY() + mc().player.getEyeHeight());
        double diffZ = target.getZ() - mc().player.getZ();
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float targetYaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F);
        float targetPitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));

        float currentYaw = mc().player.getYRot();
        float currentPitch = mc().player.getXRot();

        float speedFactor = rotationSpeed / 40.0f;
        float nextYaw = currentYaw + Mth.wrapDegrees(Mth.wrapDegrees(targetYaw - currentYaw) * speedFactor);
        float nextPitch = currentPitch + (targetPitch - currentPitch) * speedFactor;

        mc().player.setYRot(nextYaw);
        mc().player.setXRot(nextPitch);
        mc().player.yHeadRot = nextYaw;
    }
}
