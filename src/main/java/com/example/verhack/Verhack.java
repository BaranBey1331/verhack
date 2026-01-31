package com.example.verhack;

import com.example.verhack.module.ModuleManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Verhack.MODID)
public class Verhack {
    public static final String MODID = "verhack";
    public static final Logger LOGGER = LogManager.getLogger();

    private static Verhack instance;
    private ModuleManager moduleManager;

    public Verhack() {
        instance = this;
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setupClient(final FMLClientSetupEvent event) {
        moduleManager = new ModuleManager();
        LOGGER.info("Verhack Client Setup Complete");
    }

    public static Verhack getInstance() {
        return instance;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }
}
