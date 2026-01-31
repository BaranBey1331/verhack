package com.example.verhack.client.gui;

import com.example.verhack.Verhack;
import com.example.verhack.module.Category;
import com.example.verhack.module.Module;
import com.example.verhack.module.combat.KillAura;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.List;

public class HackMenuScreen extends Screen {
    private Category selectedCategory = Category.COMBAT;

    public HackMenuScreen() {
        super(Component.literal("Verhack Client - v2 Alpha"));
    }

    @Override
    protected void init() {
        updateButtons();
    }

    private void updateButtons() {
        this.clearWidgets();

        int yOffset = 55;
        int xOffset = 35;

        // Category buttons (left side)
        for (Category category : Category.values()) {
            final Category cat = category;
            NeonButton btn = new NeonButton(Button.builder(Component.literal(cat.name()), b -> {
                selectedCategory = cat;
                updateButtons();
            }).pos(xOffset, yOffset).size(80, 20));

            if (selectedCategory == cat) {
                btn.setMessage(Component.literal("> " + cat.name()));
            }

            addRenderableWidget(btn);
            yOffset += 25;
        }

        // Module buttons (right side)
        int modXOffset = 135;
        int modYOffset = 55;
        if (Verhack.getInstance().getModuleManager() != null) {
            List<Module> modules = Verhack.getInstance().getModuleManager().getModulesByCategory(selectedCategory);
            for (Module module : modules) {
                String status = module.isEnabled() ? " [ON]" : " [OFF]";

                NeonButton modBtn = new NeonButton(Button.builder(Component.literal(module.getName() + status), b -> {
                    module.toggle();
                    updateButtons();
                }).pos(modXOffset, modYOffset).size(160, 20));

                addRenderableWidget(modBtn);
                modYOffset += 25;

                // Add settings if enabled
                if (module.isEnabled()) {
                    if (module instanceof KillAura ka) {
                        // Slider for Range
                        AbstractSliderButton rangeSlider = new AbstractSliderButton(modXOffset + 10, modYOffset, 140, 20, Component.literal("Range: " + String.format("%.1f", ka.getRange())), (ka.getRange() - 2.0) / 8.0) {
                            @Override
                            protected void updateMessage() {
                                this.setMessage(Component.literal("Range: " + String.format("%.1f", ka.getRange())));
                            }

                            @Override
                            protected void applyValue() {
                                ka.setRange(2.0 + this.value * 8.0);
                            }
                        };
                        addRenderableWidget(rangeSlider);
                        modYOffset += 22;

                        // Rotations Toggle
                        NeonButton rotBtn = new NeonButton(Button.builder(Component.literal("Rotations: " + (ka.isRotations() ? "ON" : "OFF")), b -> {
                            ka.setRotations(!ka.isRotations());
                            updateButtons();
                        }).pos(modXOffset + 10, modYOffset).size(140, 20));
                        addRenderableWidget(rotBtn);
                        modYOffset += 22;

                        // Rotation Speed Slider
                        AbstractSliderButton speedSlider = new AbstractSliderButton(modXOffset + 10, modYOffset, 140, 20, Component.literal("Rot Speed: " + String.format("%.1f", ka.getRotationSpeed())), (ka.getRotationSpeed() - 1.0) / 19.0) {
                            @Override
                            protected void updateMessage() {
                                this.setMessage(Component.literal("Rot Speed: " + String.format("%.1f", ka.getRotationSpeed())));
                            }

                            @Override
                            protected void applyValue() {
                                ka.setRotationSpeed(1.0f + (float)this.value * 19.0f);
                            }
                        };
                        addRenderableWidget(speedSlider);
                        modYOffset += 25;
                    }
                }

                if (modYOffset > this.height - 60) {
                    modYOffset = 55;
                    modXOffset += 180;
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Aesthetic background
        guiGraphics.fill(0, 0, this.width, this.height, 0x90000000); // Semi-transparent black
        guiGraphics.fill(20, 20, this.width - 20, this.height - 20, 0x60101010); // Darker inner box

        // Neon border
        int neonColor = 0xFF00FFFF; // Cyan
        guiGraphics.renderOutline(20, 20, this.width - 40, this.height - 40, neonColor);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 25, 0x00FFFFFF);
        guiGraphics.drawString(this.font, "Category: " + selectedCategory.name(), 135, 40, 0xAAAAAA);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static class NeonButton extends Button {
        public NeonButton(Button.Builder builder) {
            super(builder);
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int color = this.isHoveredOrFocused() ? 0xFF00FFFF : 0xFFAAAAAA;
            int bgColor = this.isHoveredOrFocused() ? 0x4000FFFF : 0x20000000;

            guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, bgColor);
            guiGraphics.renderOutline(getX(), getY(), width, height, color);
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, getMessage(), getX() + width / 2, getY() + (height - 8) / 2, color);
        }
    }
}
