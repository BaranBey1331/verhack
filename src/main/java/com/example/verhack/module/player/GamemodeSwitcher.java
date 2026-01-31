package com.example.verhack.module.player;

import com.example.verhack.module.Category;
import com.example.verhack.module.Module;
import net.minecraft.world.level.GameType;

public class GamemodeSwitcher extends Module {
    public GamemodeSwitcher() {
        super("Gamemode Switcher", "Switch gamemodes", Category.PLAYER);
    }

    public void setGamemode(GameType type) {
        if (mc().player != null) {
            mc().player.connection.sendCommand("gamemode " + type.getName());
        }
    }

    @Override
    public void onEnable() {
        // Handled via UI buttons
    }
}
