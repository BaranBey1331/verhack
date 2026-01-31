package com.example.verhack.module.player;

import com.example.verhack.module.Category;
import com.example.verhack.module.Module;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class Teleport extends Module {
    public Teleport() {
        super("Teleport", "Instant teleport to look position", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (mc().player == null || mc().screen != null) return;

        if (mc().options.keyUse.isDown()) {
            if (mc().hitResult != null && mc().hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = (BlockHitResult) mc().hitResult;
                double x = blockHit.getBlockPos().getX() + 0.5;
                double y = blockHit.getBlockPos().getY() + 1.0;
                double z = blockHit.getBlockPos().getZ() + 0.5;
                mc().player.setPos(x, y, z);
                this.setEnabled(false); // Disable after teleporting
            }
        }
    }

    @Override
    public void onEnable() {
        // Just stay enabled until used
    }
}
