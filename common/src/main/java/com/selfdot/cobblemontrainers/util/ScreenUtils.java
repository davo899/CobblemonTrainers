package com.selfdot.cobblemontrainers.util;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.moves.categories.DamageCategories;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.pokemon.Nature;
import com.cobblemon.mod.common.util.DataKeys;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;

import java.util.Set;

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
        return withoutAdditional(switch (stat) {
            case HP -> CobblemonItems.HP_UP;
            case ATTACK -> CobblemonItems.PROTEIN;
            case DEFENCE -> CobblemonItems.IRON;
            case SPECIAL_ATTACK -> CobblemonItems.CALCIUM;
            case SPECIAL_DEFENCE -> CobblemonItems.ZINC;
            case SPEED -> CobblemonItems.CARBOS;
            default -> CobblemonItems.CHARCOAL;
        });
    }

    public static void addLore(ItemStack stack, Text[] lore) {
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtCompound displayNbt = stack.getOrCreateSubNbt("display");
        NbtList nbtLore = new NbtList();

        for (Text text : lore) {
            Text line = Texts.join(text.getWithStyle(Style.EMPTY.withItalic(false)), Text.of(""));
            nbtLore.add(NbtString.of(Text.Serializer.toJson(line)));
        }

        displayNbt.put("Lore", nbtLore);
        nbt.put("display", displayNbt);
        stack.setNbt(nbt);
    }

    public static ItemStack withoutAdditional(Item item) {
        ItemStack itemStack = new ItemStack(item);
        itemStack.addHideFlag(ItemStack.TooltipSection.ADDITIONAL);
        NbtCompound nbt = itemStack.getNbt();
        if (nbt == null) nbt = new NbtCompound();
        nbt.putBoolean(DataKeys.HIDE_TOOLTIP, true);
        itemStack.setNbt(nbt);
        return itemStack;
    }

}
