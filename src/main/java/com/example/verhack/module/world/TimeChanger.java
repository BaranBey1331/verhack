package com.example.verhack.module.world;

import com.example.verhack.module.Category;
import com.example.verhack.module.Module;

public class TimeChanger extends Module {
    private long time = 1000;

    public TimeChanger() {
        super("Time Changer", "Change client-side world time", Category.WORLD);
    }

    @Override
    public void onTick() {
        if (mc().level != null) {
            mc().level.setDayTime(time);
        }
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
