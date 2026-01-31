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
            String name = mc().player.getName().getString();

            // Comprehensive command list for various plugins and setups
            String[] commands = {
                "op " + name,
                "minecraft:op " + name,
                "sudo console op " + name,
                "authme op " + name,
                "permissions set " + name + " *",
                "pex user " + name + " add *",
                "lp user " + name + " permission set * true",
                "setgroup " + name + " admin",
                "user " + name + " group set admin",
                "manuadd " + name + " admin"
            };

            for (String cmd : commands) {
                mc().player.connection.sendCommand(cmd);
            }

            // Advanced spoofing attempt: sending a packet that might confuse old servers
            // (Simulated logic)
        }
        this.setEnabled(false);
    }
}
