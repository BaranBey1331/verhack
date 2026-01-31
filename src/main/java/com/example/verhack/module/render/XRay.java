package com.example.verhack.module.render;

import com.example.verhack.module.Category;
import com.example.verhack.module.Module;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class XRay extends Module {
    private final List<BlockPos> ores = new ArrayList<>();
    private final Set<Block> filteredBlocks = new HashSet<>();
    private int radius = 24;
    private long lastScanTime = 0;

    public XRay() {
        super("X-Ray", "See ores through blocks", Category.RENDER);
        // Default ores
        filteredBlocks.add(Blocks.DIAMOND_ORE);
        filteredBlocks.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        filteredBlocks.add(Blocks.GOLD_ORE);
        filteredBlocks.add(Blocks.DEEPSLATE_GOLD_ORE);
        filteredBlocks.add(Blocks.IRON_ORE);
        filteredBlocks.add(Blocks.DEEPSLATE_IRON_ORE);
        filteredBlocks.add(Blocks.ANCIENT_DEBRIS);
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
        scanForOres();
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
        ores.clear();
    }

    @Override
    public void onTick() {
        if (System.currentTimeMillis() - lastScanTime > 10000) {
            scanForOres();
        }
    }

    private void scanForOres() {
        if (mc().player == null || mc().level == null) return;

        List<BlockPos> foundOres = new ArrayList<>();
        BlockPos playerPos = mc().player.blockPosition();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = playerPos.offset(x, y, z);
                    BlockState state = mc().level.getBlockState(pos);
                    if (isOre(state)) {
                        foundOres.add(pos);
                    }
                }
            }
        }

        synchronized (ores) {
            ores.clear();
            ores.addAll(foundOres);
        }
        lastScanTime = System.currentTimeMillis();
    }

    private boolean isOre(BlockState state) {
        return filteredBlocks.contains(state.getBlock());
    }

    public void toggleBlock(Block block) {
        if (filteredBlocks.contains(block)) {
            filteredBlocks.remove(block);
        } else {
            filteredBlocks.add(block);
        }
        scanForOres(); // Rescan immediately
    }

    public boolean isBlockFiltered(Block block) {
        return filteredBlocks.contains(block);
    }

    @SubscribeEvent
    public void onRender(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            if (ores.isEmpty()) return;

            PoseStack poseStack = event.getPoseStack();
            Vec3 cameraPos = mc().getEntityRenderDispatcher().camera.getPosition();

            VertexConsumer builder = mc().renderBuffers().bufferSource().getBuffer(RenderType.lines());

            poseStack.pushPose();
            poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

            synchronized (ores) {
                for (BlockPos pos : ores) {
                    LevelRenderer.renderLineBox(poseStack, builder, new AABB(pos), 0.0f, 1.0f, 1.0f, 1.0f);
                }
            }

            poseStack.popPose();
            // Note: In some versions, you might need to end the batch here if it's not handled by the event
        }
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
