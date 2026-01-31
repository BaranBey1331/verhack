package com.example.verhack.module.render;

import com.example.verhack.module.Category;
import com.example.verhack.module.Module;
import net.minecraft.world.phys.Vec3;

public class FreeCam extends Module {
    private Vec3 oldPos;
    private float oldYaw, oldPitch;

    public FreeCam() {
        super("FreeCam", "Detached camera movement", Category.RENDER);
    }

    @Override
    public void onEnable() {
        if (mc().player != null) {
            oldPos = mc().player.position();
            oldYaw = mc().player.getYRot();
            oldPitch = mc().player.getXRot();
            mc().player.noPhysics = true;
        }
    }

    @Override
    public void onDisable() {
        if (mc().player != null && oldPos != null) {
            mc().player.setPos(oldPos.x, oldPos.y, oldPos.z);
            mc().player.setYRot(oldYaw);
            mc().player.setXRot(oldPitch);
            mc().player.noPhysics = false;
            mc().player.setDeltaMovement(0, 0, 0);
        }
    }

    @Override
    public void onTick() {
        if (mc().player == null) return;

        mc().player.setDeltaMovement(0, 0, 0);
        mc().player.setOnGround(false);

        double speed = 0.5;
        Vec3 look = mc().player.getLookAngle();

        if (mc().options.keyUp.isDown()) {
            mc().player.setPos(mc().player.getX() + look.x * speed, mc().player.getY() + look.y * speed, mc().player.getZ() + look.z * speed);
        }
        if (mc().options.keyJump.isDown()) {
            mc().player.setPos(mc().player.getX(), mc().player.getY() + speed, mc().player.getZ());
        }
        if (mc().options.keyShift.isDown()) {
            mc().player.setPos(mc().player.getX(), mc().player.getY() - speed, mc().player.getZ());
        }
    }
}
