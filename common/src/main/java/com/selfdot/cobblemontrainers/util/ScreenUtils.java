package com.selfdot.cobblemontrainers.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ScreenUtils {

    public static void fill(Inventory inventory, Item item) {
        for (int i = 0; i < inventory.size(); i++) inventory.setStack(i, new ItemStack(item));
    }

}
