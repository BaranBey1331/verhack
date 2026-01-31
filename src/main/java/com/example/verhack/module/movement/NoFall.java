package com.example.verhack.module.movement;

import com.example.verhack.module.Category;
import com.example.verhack.module.Module;

public class NoFall extends Module {
    public NoFall() {
        super("NoFall", "Prevents fall damage", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (mc().player == null) return;

        if (mc().player.fallDistance > 2.0f) {
            // Spoof onGround to prevent damage
            mc().player.setOnGround(true);
            mc().player.fallDistance = 0;
        }
    }
}
