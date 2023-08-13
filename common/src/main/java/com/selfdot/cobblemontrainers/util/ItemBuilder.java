package com.selfdot.cobblemontrainers.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class ItemBuilder {
    ItemStack stack = null;

    public ItemBuilder(Item item) {
        this.stack = new ItemStack(item);
    }
    public ItemBuilder(ItemStack item) {
        this.stack = item;
    }

    public ItemBuilder addLore(Text[] lore) {
        NbtCompound nbt = this.stack.getOrCreateNbt();
        NbtCompound displayNbt = this.stack.getOrCreateSubNbt("display");
        NbtList nbtLore = new NbtList();

        for (Text text : lore) {
            Text line = Texts.join(text.getWithStyle(Style.EMPTY.withItalic(false)), Text.of(""));
            nbtLore.add(NbtString.of(Text.Serializer.toJson(line)));
        }

        displayNbt.put("Lore", nbtLore);
        nbt.put("display", displayNbt);
        this.stack.setNbt(nbt);
        return this;
    }

    public ItemBuilder hideAdditional() {
        this.stack.addHideFlag(ItemStack.TooltipSection.ADDITIONAL);
        return this;
    }
    public ItemBuilder setCustomName(Text customName) {
        Text pokemonName = Texts.join(customName.getWithStyle(Style.EMPTY.withItalic(false)), Text.of(""));
        this.stack.setCustomName(pokemonName);
        return this;
    }

    public ItemStack build() {
        return this.stack;
    }
}
