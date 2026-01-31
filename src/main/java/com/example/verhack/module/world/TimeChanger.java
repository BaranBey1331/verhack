package com.example.verhack.module.world;

import com.example.verhack.module.Category;
import com.example.verhack.module.Module;

public class TimeChanger extends Module {
    private long time = 1000;
    private boolean useCommand = false;

    public TimeChanger() {
        super("Time Changer", "Change client-side world time", Category.WORLD);
    }

    @Override
    public void onTick() {
        if (mc().level != null && !useCommand) {
            // Force client-side time every tick for stability
            mc().level.setDayTime(time);
        }
    }

    @Override
    public void onEnable() {
        if (useCommand && mc().player != null) {
            mc().player.connection.sendCommand("time set " + time);
            this.setEnabled(false); // Disable after sending command
        }
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isUseCommand() {
        return useCommand;
    }

    public void setUseCommand(boolean useCommand) {
        this.useCommand = useCommand;
    }
}
