package com.example.verhack.module.combat.aimbot;

import com.example.verhack.module.Category;
import com.example.verhack.module.Module;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Comparator;
import java.util.List;

public class MeleeAimbot extends Module {
    private double range = 6.0;
    private float smoothness = 0.5f; // 0.0 to 1.0, where 1.0 is slowest
    private float fov = 90.0f;
    private LivingEntity currentTarget = null;

    public MeleeAimbot() {
        super("Melee Aimbot", "Automatically aim at entities with swords/axes", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
        currentTarget = null;
    }

    @Override
    public void onTick() {
        if (mc().player == null || mc().level == null || mc().screen != null) {
            currentTarget = null;
            return;
        }

        // Check if holding melee weapon
        if (!(mc().player.getMainHandItem().getItem() instanceof SwordItem) &&
            !(mc().player.getMainHandItem().getItem() instanceof AxeItem)) {
            currentTarget = null;
            return;
        }

        List<LivingEntity> targets = mc().level.getEntitiesOfClass(LivingEntity.class, mc().player.getBoundingBox().inflate(range), e ->
                e != mc().player && e.isAlive() && mc().player.distanceTo(e) <= range &&
                mc().player.hasLineOfSight(e) && isInFov(e, fov)
        );

        targets.sort(Comparator.comparingDouble(e -> mc().player.distanceTo(e)));

        if (!targets.isEmpty()) {
            currentTarget = targets.get(0);
            smoothLookAt(currentTarget);
        } else {
            currentTarget = null;
        }
    }

    private boolean isInFov(Entity entity, float fov) {
        Vec3 targetVec = entity.position().add(0, entity.getEyeHeight(), 0).subtract(mc().player.getEyePosition());
        Vec3 lookVec = mc().player.getLookAngle();
        double angle = Math.toDegrees(Math.acos(lookVec.dot(targetVec.normalize())));
        return angle <= fov / 2.0;
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

        // Smoother interpolation using a lower speed factor based on smoothness slider
        // smoothness 0.0 -> speedFactor 0.5 (fast)
        // smoothness 1.0 -> speedFactor 0.01 (very slow)
        float speedFactor = (1.0f - smoothness) * 0.45f + 0.05f;

        float nextYaw = currentYaw + Mth.wrapDegrees(Mth.wrapDegrees(targetYaw - currentYaw) * speedFactor);
        float nextPitch = currentPitch + (targetPitch - currentPitch) * speedFactor;

        mc().player.setYRot(nextYaw);
        mc().player.setXRot(nextPitch);
        mc().player.yHeadRot = nextYaw;
    }

    @SubscribeEvent
    public void onRender(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES && currentTarget != null) {
            PoseStack poseStack = event.getPoseStack();
            Vec3 cameraPos = mc().getEntityRenderDispatcher().camera.getPosition();
            VertexConsumer builder = mc().renderBuffers().bufferSource().getBuffer(RenderType.lines());

            poseStack.pushPose();
            poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

            AABB bb = currentTarget.getBoundingBox();
            float r = 0, g = 0, b = 0;

            if (currentTarget instanceof Monster || currentTarget instanceof Player) {
                r = 1.0f; // Red for enemies
            } else {
                g = 1.0f; // Green for others (mobs)
            }

            LevelRenderer.renderLineBox(poseStack, builder, bb, r, g, b, 1.0f);

            poseStack.popPose();
        }
    }

    public float getSmoothness() { return smoothness; }
    public void setSmoothness(float smoothness) { this.smoothness = smoothness; }
    public float getFov() { return fov; }
    public void setFov(float fov) { this.fov = fov; }
}
