package com.example.verhack.client.gui;

import com.example.verhack.Verhack;
import com.example.verhack.module.Category;
import com.example.verhack.module.Module;
import com.example.verhack.module.combat.KillAura;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.List;

public class HackMenuScreen extends Screen {
    private Category selectedCategory = Category.COMBAT;

    public HackMenuScreen() {
        super(Component.literal("Verhack Menu"));
    }

    @Override
    protected void init() {
        updateButtons();
    }

    private void updateButtons() {
        this.clearWidgets();

        int yOffset = 40;
        int xOffset = 20;

        // Category buttons (left side)
        for (Category category : Category.values()) {
            final Category cat = category;
            Button btn = Button.builder(Component.literal(cat.name()), b -> {
                selectedCategory = cat;
                updateButtons();
            }).bounds(xOffset, yOffset, 80, 20).build();

            if (selectedCategory == cat) {
                btn.setMessage(Component.literal("> " + cat.name()));
            }

            addRenderableWidget(btn);
            yOffset += 25;
        }

        // Module buttons (right side)
        int modXOffset = 120;
        int modYOffset = 40;
        if (Verhack.getInstance().getModuleManager() != null) {
            List<Module> modules = Verhack.getInstance().getModuleManager().getModulesByCategory(selectedCategory);
            for (Module module : modules) {
                String status = module.isEnabled() ? " [ENABLED]" : " [DISABLED]";
                Button modBtn = Button.builder(Component.literal(module.getName() + status), b -> {
                    module.toggle();
                    updateButtons(); // Refresh all buttons to update status and settings visibility
                }).bounds(modXOffset, modYOffset, 160, 20).build();

                addRenderableWidget(modBtn);
                modYOffset += 25;

                // Add settings if enabled
                if (module.isEnabled()) {
                    if (module instanceof KillAura ka) {
                        Button rangeBtn = Button.builder(Component.literal(" - Range: " + ka.getRange()), b -> {
                            ka.incrementRange();
                            b.setMessage(Component.literal(" - Range: " + ka.getRange()));
                        }).bounds(modXOffset, modYOffset, 160, 20).build();
                        addRenderableWidget(rangeBtn);
                        modYOffset += 25;
                    }
                }

                if (modYOffset > this.height - 60) {
                    modYOffset = 40;
                    modXOffset += 170;
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);
        guiGraphics.drawString(this.font, "Category: " + selectedCategory.name(), 120, 25, 0xAAAAAA);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
