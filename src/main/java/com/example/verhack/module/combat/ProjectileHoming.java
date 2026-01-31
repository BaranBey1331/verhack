package com.example.verhack.module.combat;

import com.example.verhack.module.Category;
import com.example.verhack.module.Module;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ProjectileHoming extends Module {
    private double range = 20.0;
    private double speed = 0.05; // Reduced for smoother maneuver
    private boolean glowTarget = true;
    private final List<LivingEntity> trackedTargets = new ArrayList<>();

    public ProjectileHoming() {
        super("Projectile Homing", "Arrows track targets", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
        trackedTargets.clear();
    }

    @Override
    public void onTick() {
        if (mc().level == null || mc().player == null) {
            trackedTargets.clear();
            return;
        }

        trackedTargets.clear();
        for (net.minecraft.world.entity.Entity entity : mc().level.entitiesForRendering()) {
            if (entity instanceof AbstractArrow arrow && !arrow.onGround()) {
                if (arrow.getOwner() == mc().player) {
                    LivingEntity target = findTarget(arrow);
                    if (target != null) {
                        Vec3 targetPos = target.position().add(0, target.getEyeHeight() * 0.5, 0);
                        Vec3 dir = targetPos.subtract(arrow.position()).normalize();

                        // Use a smaller fraction of the direction to make it less abrupt
                        Vec3 currentVel = arrow.getDeltaMovement();
                        Vec3 targetVel = dir.scale(currentVel.length());

                        // Interpolate between current velocity and target velocity
                        Vec3 newVel = currentVel.scale(1.0 - speed).add(targetVel.scale(speed));
                        arrow.setDeltaMovement(newVel);

                        if (glowTarget && !trackedTargets.contains(target)) {
                            trackedTargets.add(target);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES && glowTarget && !trackedTargets.isEmpty()) {
            PoseStack poseStack = event.getPoseStack();
            Vec3 cameraPos = mc().getEntityRenderDispatcher().camera.getPosition();
            VertexConsumer builder = mc().renderBuffers().bufferSource().getBuffer(RenderType.lines());

            poseStack.pushPose();
            poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

            for (LivingEntity target : trackedTargets) {
                AABB bb = target.getBoundingBox();
                float r = 0, g = 0, b = 0;

                if (target instanceof Monster || target instanceof Player) {
                    r = 1.0f; // Red for enemies
                } else {
                    g = 1.0f; // Green for others (mobs)
                }

                LevelRenderer.renderLineBox(poseStack, builder, bb, r, g, b, 1.0f);
            }

            poseStack.popPose();
        }
    }

    public boolean isGlowTarget() {
        return glowTarget;
    }

    public void setGlowTarget(boolean glowTarget) {
        this.glowTarget = glowTarget;
    }

    private LivingEntity findTarget(AbstractArrow arrow) {
        List<LivingEntity> targets = mc().level.getEntitiesOfClass(LivingEntity.class, arrow.getBoundingBox().inflate(range), e ->
                e != mc().player && e.isAlive()
        );
        targets.sort(Comparator.comparingDouble(e -> arrow.distanceTo(e)));
        return targets.isEmpty() ? null : targets.get(0);
    }
}
