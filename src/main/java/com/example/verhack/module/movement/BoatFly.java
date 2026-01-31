package com.example.verhack.module.movement;

import com.example.verhack.module.Category;
import com.example.verhack.module.Module;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

public class BoatFly extends Module {
    private double speed = 1.0;

    public BoatFly() {
        super("BoatFly", "Fly while in a boat", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (mc().player == null || mc().player.getVehicle() == null) return;

        Entity vehicle = mc().player.getVehicle();
        if (vehicle instanceof Boat boat) {
            Vec3 look = mc().player.getLookAngle();
            double motionX = 0;
            double motionY = 0;
            double motionZ = 0;

            if (mc().options.keyUp.isDown()) {
                motionX += look.x * speed;
                motionY += look.y * speed;
                motionZ += look.z * speed;
            }
            if (mc().options.keyJump.isDown()) {
                motionY += speed;
            }
            if (mc().options.keyShift.isDown()) {
                motionY -= speed;
            }

            boat.setDeltaMovement(motionX, motionY, motionZ);
        }
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
