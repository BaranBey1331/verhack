package com.example.verhack.module.combat;

import com.example.verhack.module.Category;
import com.example.verhack.module.Module;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionHand;

import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

public class KillAura extends Module {
    private double range = 4.5;
    private int attackDelay = 0;

    public KillAura() {
        super("KillAura", "Automatically attacks entities around you", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.level == null) return;

        if (attackDelay > 0) {
            attackDelay--;
            return;
        }

        // Search for targets
        Iterable<Entity> entities = mc.level.entitiesForRendering();
        List<LivingEntity> targets = StreamSupport.stream(entities.spliterator(), false)
                .filter(e -> e instanceof LivingEntity)
                .map(e -> (LivingEntity) e)
                .filter(e -> e != mc.player)
                .filter(e -> e.isAlive())
                .filter(e -> mc.player.distanceTo(e) <= range)
                .sorted(Comparator.comparingDouble(e -> mc.player.distanceTo(e)))
                .toList();

        if (!targets.isEmpty()) {
            LivingEntity target = targets.get(0);
            attack(target);
            // Default cooldown: roughly 0.5 seconds (10 ticks)
            attackDelay = 10;
        }
    }

    private void attack(Entity target) {
        if (mc.gameMode != null && mc.player != null) {
            mc.gameMode.attack(mc.player, target);
            mc.player.swing(InteractionHand.MAIN_HAND);
        }
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public void incrementRange() {
        this.range += 0.5;
        if (this.range > 10.0) this.range = 2.0;
    }
}
