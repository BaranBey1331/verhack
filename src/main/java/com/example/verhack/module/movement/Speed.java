package com.example.verhack.module.movement;

import com.example.verhack.module.Category;
import com.example.verhack.module.Module;
import net.minecraft.world.phys.Vec3;

public class Speed extends Module {
    private double multiplier = 1.5;

    public Speed() {
        super("Speed", "Increases movement speed", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (mc().player == null || !mc().player.onGround() || mc().player.isShiftKeyDown()) return;

        // Simple speed hack by multiplying horizontal velocity
        Vec3 vel = mc().player.getDeltaMovement();
        if (vel.x != 0 || vel.z != 0) {
            mc().player.setDeltaMovement(vel.x * multiplier, vel.y, vel.z * multiplier);
        }
    }

    public double getMultiplier() { return multiplier; }
    public void setMultiplier(double multiplier) { this.multiplier = multiplier; }
}
