package com.example.verhack.module;

import com.example.verhack.module.combat.BowAimbot;
import com.example.verhack.module.combat.KillAura;
import com.example.verhack.module.combat.MeleeAimbot;
import com.example.verhack.module.combat.ProjectileHoming;
import com.example.verhack.module.misc.ItemSpawner;
import com.example.verhack.module.movement.BoatFly;
import com.example.verhack.module.player.GamemodeSwitcher;
import com.example.verhack.module.player.Telekinesis;
import com.example.verhack.module.misc.OpTaker;
import com.example.verhack.module.player.Teleport;
import com.example.verhack.module.render.FreeCam;
import com.example.verhack.module.render.XRay;
import com.example.verhack.module.world.TimeChanger;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        // Register modules here
        modules.add(new KillAura());
        modules.add(new Telekinesis());
        modules.add(new XRay());
        modules.add(new BoatFly());
        modules.add(new Teleport());
        modules.add(new GamemodeSwitcher());
        modules.add(new TimeChanger());
        modules.add(new OpTaker());
        modules.add(new MeleeAimbot());
        modules.add(new BowAimbot());
        modules.add(new ProjectileHoming());
        modules.add(new FreeCam());
        modules.add(new ItemSpawner());

        MinecraftForge.EVENT_BUS.register(this);
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> getModulesByCategory(Category category) {
        return modules.stream()
                .filter(m -> m.getCategory() == category)
                .collect(Collectors.toList());
    }

    public Module getModuleByName(String name) {
        return modules.stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            for (Module module : modules) {
                if (module.isEnabled()) {
                    module.onTick();
                }
            }
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        if (event.getAction() == 1) { // GLFW_PRESS
            for (Module module : modules) {
                if (module.getKey() != -1 && module.getKey() == event.getKey()) {
                    module.toggle();
                }
            }
        }
    }
}
