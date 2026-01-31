package com.example.verhack.client.gui;

import com.example.verhack.Verhack;
import com.example.verhack.module.Category;
import com.example.verhack.module.Module;
import com.example.verhack.module.combat.KillAura;
import com.example.verhack.module.combat.ProjectileHoming;
import com.example.verhack.module.movement.BoatFly;
import com.example.verhack.module.render.XRay;
import com.example.verhack.module.world.TimeChanger;
import com.example.verhack.module.player.GamemodeSwitcher;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.GameType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.List;

public class HackMenuScreen extends Screen {
    public enum Theme { NEON, CLASSIC, TRANSPARENT }
    public static Theme currentTheme = Theme.NEON;
    private Category selectedCategory = Category.THEME;
    private Module listeningModule = null;

    public HackMenuScreen() {
        super(Component.translatable("gui.verhack.title"));
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
            Component catName = Component.translatable("category." + cat.name().toLowerCase());
            NeonButton btn = new NeonButton(xOffset, yOffset, 80, 20, catName, b -> {
                selectedCategory = cat;
                updateButtons();
            });

            if (selectedCategory == cat) {
                btn.setMessage(Component.literal("> ").append(catName));
            }

            addRenderableWidget(btn);
            yOffset += 25;
        }

        // Module buttons (right side)
        int modXOffset = 135;
        int modYOffset = 55;

        if (selectedCategory == Category.THEME) {
            addRenderableWidget(new NeonButton(modXOffset, modYOffset, 160, 20, Component.translatable("gui.verhack.theme.neon"), b -> {
                currentTheme = Theme.NEON;
                updateButtons();
            }));
            modYOffset += 25;
            addRenderableWidget(new NeonButton(modXOffset, modYOffset, 160, 20, Component.translatable("gui.verhack.theme.classic"), b -> {
                currentTheme = Theme.CLASSIC;
                updateButtons();
            }));
            modYOffset += 25;
            addRenderableWidget(new NeonButton(modXOffset, modYOffset, 160, 20, Component.translatable("gui.verhack.theme.transparent"), b -> {
                currentTheme = Theme.TRANSPARENT;
                updateButtons();
            }));
            return;
        }

        if (selectedCategory == Category.CHANGELOG) {
            return;
        }

        if (Verhack.getInstance().getModuleManager() != null) {
            List<Module> modules = Verhack.getInstance().getModuleManager().getModulesByCategory(selectedCategory);
            for (Module module : modules) {
                String status = module.isEnabled() ? " [ON]" : " [OFF]";
                Component modName = Component.translatable("module." + module.getName().toLowerCase().replace(" ", "") + ".name");

                NeonButton modBtn = new NeonButton(modXOffset, modYOffset, 160, 20, modName.copy().append(status), b -> {
                    module.toggle();
                    updateButtons();
                });
                addRenderableWidget(modBtn);

                // Keybind button
                String keyName = module.getKey() == -1 ? "NONE" : org.lwjgl.glfw.GLFW.glfwGetKeyName(module.getKey(), 0);
                if (keyName == null && module.getKey() != -1) keyName = "KEY " + module.getKey();

                String btnText = listeningModule == module ? "[PRESS KEY]" : "[" + keyName + "]";
                NeonButton bindBtn = new NeonButton(modXOffset + 165, modYOffset, 60, 20, Component.literal(btnText), b -> {
                    listeningModule = module;
                    updateButtons();
                });
                addRenderableWidget(bindBtn);
                modYOffset += 25;

                // Add settings if enabled
                if (module.isEnabled()) {
                    if (module instanceof KillAura ka) {
                        // Slider for Range
                        AbstractSliderButton rangeSlider = new AbstractSliderButton(modXOffset + 10, modYOffset, 140, 20, Component.literal("Range: " + String.format("%.1f", ka.getRange())), (ka.getRange() - 2.0) / 18.0) {
                            @Override
                            protected void updateMessage() {
                                this.setMessage(Component.literal("Range: " + String.format("%.1f", ka.getRange())));
                            }

                            @Override
                            protected void applyValue() {
                                ka.setRange(2.0 + this.value * 18.0);
                            }
                        };
                        addRenderableWidget(rangeSlider);
                        modYOffset += 22;

                        // Rotations Toggle
                        NeonButton rotBtn = new NeonButton(modXOffset + 10, modYOffset, 140, 20, Component.literal("Rotations: " + (ka.isRotations() ? "ON" : "OFF")), b -> {
                            ka.setRotations(!ka.isRotations());
                            updateButtons();
                        });
                        addRenderableWidget(rotBtn);
                        modYOffset += 22;

                        // Rotation Speed Slider
                        AbstractSliderButton speedSlider = new AbstractSliderButton(modXOffset + 10, modYOffset, 140, 20, Component.literal("Rot Speed: " + String.format("%.1f", ka.getRotationSpeed())), (ka.getRotationSpeed() - 1.0) / 39.0) {
                            @Override
                            protected void updateMessage() {
                                this.setMessage(Component.literal("Rot Speed: " + String.format("%.1f", ka.getRotationSpeed())));
                            }

                            @Override
                            protected void applyValue() {
                                ka.setRotationSpeed(1.0f + (float)this.value * 39.0f);
                            }
                        };
                        addRenderableWidget(speedSlider);
                        modYOffset += 25;
                    } else if (module instanceof ProjectileHoming ph) {
                        NeonButton glowBtn = new NeonButton(modXOffset + 10, modYOffset, 140, 20, Component.literal("Glow Target: " + (ph.isGlowTarget() ? "ON" : "OFF")), b -> {
                            ph.setGlowTarget(!ph.isGlowTarget());
                            updateButtons();
                        });
                        addRenderableWidget(glowBtn);
                        modYOffset += 22;
                    } else if (module instanceof BoatFly bf) {
                        AbstractSliderButton speedSlider = new AbstractSliderButton(modXOffset + 10, modYOffset, 140, 20, Component.literal("Speed: " + String.format("%.1f", bf.getSpeed())), (bf.getSpeed() - 0.1) / 4.9) {
                            @Override
                            protected void updateMessage() {
                                this.setMessage(Component.literal("Speed: " + String.format("%.1f", bf.getSpeed())));
                            }

                            @Override
                            protected void applyValue() {
                                bf.setSpeed(0.1 + this.value * 4.9);
                            }
                        };
                        addRenderableWidget(speedSlider);
                        modYOffset += 25;
                    } else if (module instanceof XRay xray) {
                        AbstractSliderButton radiusSlider = new AbstractSliderButton(modXOffset + 10, modYOffset, 140, 20, Component.literal("Radius: " + xray.getRadius()), (xray.getRadius() - 16.0) / 112.0) {
                            @Override
                            protected void updateMessage() {
                                this.setMessage(Component.literal("Radius: " + xray.getRadius()));
                            }

                            @Override
                            protected void applyValue() {
                                xray.setRadius(16 + (int)(this.value * 112));
                            }
                        };
                        addRenderableWidget(radiusSlider);
                        modYOffset += 25;

                        // Ore filter buttons
                        String[] names = {"Diamond", "Gold", "Iron", "Emerald", "Netherite"};
                        net.minecraft.world.level.block.Block[] blocks = {Blocks.DIAMOND_ORE, Blocks.GOLD_ORE, Blocks.IRON_ORE, Blocks.EMERALD_ORE, Blocks.ANCIENT_DEBRIS};

                        for (int i = 0; i < names.length; i++) {
                            final net.minecraft.world.level.block.Block b = blocks[i];
                            String status = xray.isBlockFiltered(b) ? " [ON]" : " [OFF]";
                            NeonButton oreBtn = new NeonButton(modXOffset + 10, modYOffset, 140, 20, Component.literal(names[i] + status), press -> {
                                xray.toggleBlock(b);
                                updateButtons();
                            });
                            addRenderableWidget(oreBtn);
                            modYOffset += 22;
                        }
                    } else if (module instanceof TimeChanger tc) {
                        AbstractSliderButton timeSlider = new AbstractSliderButton(modXOffset + 10, modYOffset, 140, 20, Component.literal("Time: " + tc.getTime()), (double)tc.getTime() / 24000.0) {
                            @Override
                            protected void updateMessage() {
                                this.setMessage(Component.literal("Time: " + tc.getTime()));
                            }

                            @Override
                            protected void applyValue() {
                                tc.setTime((long)(this.value * 24000));
                            }
                        };
                        addRenderableWidget(timeSlider);
                        modYOffset += 22;

                        NeonButton cmdBtn = new NeonButton(modXOffset + 10, modYOffset, 140, 20, Component.literal("Command: " + (tc.isUseCommand() ? "ON" : "OFF")), b -> {
                            tc.setUseCommand(!tc.isUseCommand());
                            updateButtons();
                        });
                        addRenderableWidget(cmdBtn);
                        modYOffset += 25;
                    } else if (module instanceof GamemodeSwitcher gs) {
                        GameType[] types = {GameType.SURVIVAL, GameType.CREATIVE, GameType.SPECTATOR, GameType.ADVENTURE};
                        for (GameType type : types) {
                            NeonButton gmBtn = new NeonButton(modXOffset + 10, modYOffset, 140, 20, Component.literal(type.getName().toUpperCase()), b -> {
                                gs.setGamemode(type);
                            });
                            addRenderableWidget(gmBtn);
                            modYOffset += 22;
                        }
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
        if (selectedCategory == Category.CHANGELOG) {
            renderChangelog(guiGraphics);
            super.render(guiGraphics, mouseX, mouseY, partialTick);
            return;
        }

        if (currentTheme == Theme.NEON) {
            // Aesthetic background
            guiGraphics.fill(0, 0, this.width, this.height, 0x90000000); // Semi-transparent black
            guiGraphics.fill(20, 20, this.width - 20, this.height - 20, 0x60101010); // Darker inner box

            // Neon border
            int neonColor = 0xFF00FFFF; // Cyan
            guiGraphics.renderOutline(20, 20, this.width - 40, this.height - 40, neonColor);
        } else if (currentTheme == Theme.CLASSIC) {
            // Classic style but transparent background as requested
            guiGraphics.fill(0, 0, this.width, this.height, 0x70000000);
            guiGraphics.fill(30, 30, this.width - 30, this.height - 30, 0xCC101010);
        } else if (currentTheme == Theme.TRANSPARENT) {
            // Fully transparent theme
            guiGraphics.fill(0, 0, this.width, this.height, 0x40000000);
        }

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 25, 0xFFFFFFFF);
        String catKey = "category." + selectedCategory.name().toLowerCase();
        Component translatedCat = Component.translatable(catKey);
        guiGraphics.drawString(this.font, Component.translatable("gui.verhack.category", translatedCat), 135, 40, 0xAAAAAA);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderChangelog(GuiGraphics guiGraphics) {
        this.renderBackground(guiGraphics);
        guiGraphics.fill(20, 20, this.width - 20, this.height - 20, 0xCC101010);
        guiGraphics.drawCenteredString(this.font, Component.translatable("gui.verhack.changelog.title"), this.width / 2, 30, 0x00FFFF);

        int y = 50;
        for (int i = 1; i <= 10; i++) {
            guiGraphics.drawString(this.font, Component.translatable("gui.verhack.changelog.line" + i), 140, y, 0xAAAAAA);
            y += 15;
            if (y > this.height - 40) break;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (listeningModule != null) {
            if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE || keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_DELETE) {
                listeningModule.setKey(-1);
            } else {
                listeningModule.setKey(keyCode);
            }
            listeningModule = null;
            updateButtons();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static class NeonButton extends Button {
        public NeonButton(int x, int y, int width, int height, Component message, OnPress onPress) {
            super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            if (currentTheme == Theme.NEON) {
                int color = this.isHoveredOrFocused() ? 0xFF00FFFF : 0xFFAAAAAA;
                int bgColor = this.isHoveredOrFocused() ? 0x4000FFFF : 0x20000000;

                guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, bgColor);
                guiGraphics.renderOutline(getX(), getY(), width, height, color);
                guiGraphics.drawCenteredString(Minecraft.getInstance().font, getMessage(), getX() + width / 2, getY() + (height - 8) / 2, color);
            } else if (currentTheme == Theme.TRANSPARENT) {
                int color = this.isHoveredOrFocused() ? 0xFFFFFFFF : 0xFFAAAAAA;
                guiGraphics.drawCenteredString(Minecraft.getInstance().font, getMessage(), getX() + width / 2, getY() + (height - 8) / 2, color);
            } else {
                super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
            }
        }
    }
}
