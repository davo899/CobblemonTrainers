package com.selfdot.cobblemontrainers.libs.minecraft.screen;

import net.minecraft.item.ItemStack;

import java.util.function.Consumer;

public record Component<T extends Menu<T>>(int slot, ItemStack icon, Consumer<T> action, Consumer<T> navigation) { }
