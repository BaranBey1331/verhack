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

        mc().player.setOnGround(false);
        mc().player.getAbilities().flying = true;

        double speed = 0.5;
        Vec3 look = mc().player.getLookAngle();
        Vec3 motion = Vec3.ZERO;

        if (mc().options.keyUp.isDown()) {
            motion = motion.add(look.scale(speed));
        }
        if (mc().options.keyBack.isDown()) {
            motion = motion.subtract(look.scale(speed));
        }
        if (mc().options.keyLeft.isDown()) {
            motion = motion.add(new Vec3(look.z, 0, -look.x).normalize().scale(speed));
        }
        if (mc().options.keyRight.isDown()) {
            motion = motion.add(new Vec3(-look.z, 0, look.x).normalize().scale(speed));
        }
        if (mc().options.keyJump.isDown()) {
            motion = motion.add(0, speed, 0);
        }
        if (mc().options.keyShift.isDown()) {
            motion = motion.subtract(0, speed, 0);
        }

        mc().player.setDeltaMovement(motion);
    }
}
