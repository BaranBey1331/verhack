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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class XRay extends Module {
    private final List<BlockPos> ores = new ArrayList<>();
    private final Set<Block> filteredBlocks = new HashSet<>();
    private int radius = 24;
    private long lastScanTime = 0;
    private final ExecutorService scanExecutor = Executors.newSingleThreadExecutor();
    private boolean scanning = false;

    public XRay() {
        super("X-Ray", "See ores through blocks", Category.RENDER);
        // Default ores
        filteredBlocks.add(Blocks.DIAMOND_ORE);
        filteredBlocks.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        filteredBlocks.add(Blocks.GOLD_ORE);
        filteredBlocks.add(Blocks.DEEPSLATE_GOLD_ORE);
        filteredBlocks.add(Blocks.IRON_ORE);
        filteredBlocks.add(Blocks.DEEPSLATE_IRON_ORE);
        filteredBlocks.add(Blocks.COPPER_ORE);
        filteredBlocks.add(Blocks.DEEPSLATE_COPPER_ORE);
        filteredBlocks.add(Blocks.LAPIS_ORE);
        filteredBlocks.add(Blocks.DEEPSLATE_LAPIS_ORE);
        filteredBlocks.add(Blocks.EMERALD_ORE);
        filteredBlocks.add(Blocks.DEEPSLATE_EMERALD_ORE);
        filteredBlocks.add(Blocks.ANCIENT_DEBRIS);
        filteredBlocks.add(Blocks.NETHER_QUARTZ_ORE);
        filteredBlocks.add(Blocks.NETHER_GOLD_ORE);
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
        startScan();
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
        synchronized (ores) {
            ores.clear();
        }
    }

    @Override
    public void onTick() {
        if (System.currentTimeMillis() - lastScanTime > 10000 && !scanning) {
            startScan();
        }
    }

    private void startScan() {
        if (mc().player == null || mc().level == null) return;
        scanning = true;
        BlockPos playerPos = mc().player.blockPosition();
        int scanRadius = radius;

        scanExecutor.execute(() -> {
            try {
                scanForOres(playerPos, scanRadius);
            } finally {
                scanning = false;
                lastScanTime = System.currentTimeMillis();
            }
        });
    }

    private void scanForOres(BlockPos center, int r) {
        if (mc().level == null) return;

        List<BlockPos> foundOres = new ArrayList<>();

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    // We must be careful about accessing world state from another thread.
                    // In many Minecraft versions, getBlockState is somewhat thread-safe for reading.
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
        startScan(); // Rescan immediately
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
