package com.example.verhack.module.player;

import com.example.verhack.module.Category;
import com.example.verhack.module.Module;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;

public class Teleport extends Module {
    private int cooldown = 0;

    public Teleport() {
        super("Teleport", "Instant teleport to look position (Use key)", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (mc().player == null || mc().level == null || mc().screen != null) return;

        if (cooldown > 0) {
            cooldown--;
            return;
        }

        if (mc().options.keyUse.isDown()) {
            // Custom long-range raycast (100 blocks)
            Vec3 eyePos = mc().player.getEyePosition();
            Vec3 lookDir = mc().player.getLookAngle();
            Vec3 endPos = eyePos.add(lookDir.scale(100));

            ClipContext context = new ClipContext(eyePos, endPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, mc().player);
            BlockHitResult hit = mc().level.clip(context);

            if (hit.getType() == HitResult.Type.BLOCK) {
                double x = hit.getBlockPos().getX() + 0.5;
                double y = hit.getBlockPos().getY() + 1.0;
                double z = hit.getBlockPos().getZ() + 0.5;

                mc().player.setPos(x, y, z);
                cooldown = 10; // 0.5s cooldown to prevent multiple teleports in one click
            }
        }
    }

    @Override
    public void onEnable() {
        // Just stay enabled until used
    }
}
