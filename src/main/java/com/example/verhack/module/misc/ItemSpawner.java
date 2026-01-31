package com.example.verhack.module.misc;

import com.example.verhack.module.Category;
import com.example.verhack.module.Module;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.registries.BuiltInRegistries;

public class ItemSpawner extends Module {
    private String itemName = "diamond";
    private int amount = 64;

    public ItemSpawner() {
        super("Item Spawner", "Spawn items (Client-side spoofing)", Category.MISC);
    }

    @Override
    public void onEnable() {
        if (mc().player != null) {
            // Attempt /give
            mc().player.connection.sendCommand("give @s " + itemName + " " + amount);

            // Attempt Creative mode slot spoofing (only works in creative or if server is bugged)
            // This is just a placeholder for the "trick" logic
        }
        this.setEnabled(false);
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }
}
