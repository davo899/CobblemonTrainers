package com.selfdot.cobblemontrainers.util;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ScreenUtils {

    public static final Stats[] STATS = {
        Stats.HP,
        Stats.ATTACK,
        Stats.DEFENCE,
        Stats.SPECIAL_ATTACK,
        Stats.SPECIAL_DEFENCE,
        Stats.SPEED
    };

    public static void fill(Inventory inventory, Item item) {
        for (int i = 0; i < inventory.size(); i++) inventory.setStack(i, new ItemStack(item));
    }

    public static ItemStack statVitaminItem(Stats stat) {
        return new ItemStack(switch (stat) {
            case HP -> CobblemonItems.HP_UP.get();
            case ATTACK -> CobblemonItems.PROTEIN.get();
            case DEFENCE -> CobblemonItems.IRON.get();
            case SPECIAL_ATTACK -> CobblemonItems.CALCIUM.get();
            case SPECIAL_DEFENCE -> CobblemonItems.ZINC.get();
            case SPEED -> CobblemonItems.CARBOS.get();
            default -> CobblemonItems.CHARCOAL.get();
        });
    }

}
