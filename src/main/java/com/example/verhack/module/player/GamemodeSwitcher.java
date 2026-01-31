package com.example.verhack.module.player;

import com.example.verhack.module.Category;
import com.example.verhack.module.Module;
import net.minecraft.world.level.GameType;

public class GamemodeSwitcher extends Module {
    public GamemodeSwitcher() {
        super("Gamemode Switcher", "Cycle through gamemodes", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        if (mc().gameMode != null && mc().player != null) {
            GameType current = mc().gameMode.getPlayerMode();
            GameType next = switch (current) {
                case SURVIVAL -> GameType.CREATIVE;
                case CREATIVE -> GameType.SPECTATOR;
                case SPECTATOR -> GameType.ADVENTURE;
                default -> GameType.SURVIVAL;
            };

            // This only works if you have permissions or on some servers
            mc().player.connection.sendCommand("gamemode " + next.getName());
        }
        this.setEnabled(false);
    }
}
