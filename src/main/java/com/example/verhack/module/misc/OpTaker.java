package com.example.verhack.module.misc;

import com.example.verhack.module.Category;
import com.example.verhack.module.Module;

public class OpTaker extends Module {
    public OpTaker() {
        super("OP Taker", "Attempts to gain OP permissions", Category.MISC);
    }

    @Override
    public void onEnable() {
        if (mc().player != null) {
            // Social engineering / Common misconfigurations
            mc().player.connection.sendCommand("op @s");
            mc().player.connection.sendCommand("minecraft:op @s");
            mc().player.connection.sendCommand("sudo console op " + mc().player.getName().getString());
            mc().player.connection.sendCommand("authme op " + mc().player.getName().getString());
        }
        this.setEnabled(false);
    }
}
