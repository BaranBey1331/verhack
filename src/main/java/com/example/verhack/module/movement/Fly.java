package com.example.verhack.module.movement;

import com.example.verhack.module.Category;
import com.example.verhack.module.Module;
import net.minecraft.world.phys.Vec3;

public class Fly extends Module {
    private double speed = 1.0;

    public Fly() {
        super("Fly", "Fly like in creative mode", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (mc().player == null) return;

        mc().player.getAbilities().mayfly = true;

        // Manual movement for smoother control and speed multiplier
        if (mc().player.getAbilities().flying) {
            mc().player.getAbilities().setFlyingSpeed((float) (0.05 * speed));

            // Vertical movement
            Vec3 vel = mc().player.getDeltaMovement();
            double y = 0;
            if (mc().options.keyJump.isDown()) y = 0.5 * speed;
            else if (mc().options.keyShift.isDown()) y = -0.5 * speed;

            if (y != 0) {
                mc().player.setDeltaMovement(vel.x, y, vel.z);
            }
        }

        // NoFall part
        if (mc().player.fallDistance > 2.0f) {
            mc().player.fallDistance = 0;
            mc().player.setOnGround(true);
        }
    }

    @Override
    public void onDisable() {
        if (mc().player != null) {
            if (!mc().player.isCreative() && !mc().player.isSpectator()) {
                mc().player.getAbilities().mayfly = false;
                mc().player.getAbilities().flying = false;
            }
        }
    }

    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }
}
