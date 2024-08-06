package com.selfdot.cobblemontrainers.screen;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ItemStackBuilder {

    private final ItemStack itemStack;
    private String name = "";
    private boolean noAdditional = true;
    private final List<String> lore = new ArrayList<>();
    private PlayerEntity playerSkull = null;

    protected ItemStackBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static ItemStackBuilder itemStack(ItemStack itemStack) {
        return new ItemStackBuilder(itemStack);
    }

    public static ItemStackBuilder itemStack(Item item) {
        return new ItemStackBuilder(new ItemStack(item));
    }

    public static ItemStackBuilder skullOf(PlayerEntity player) {
        ItemStackBuilder itemStackBuilder = itemStack(Items.PLAYER_HEAD);
        itemStackBuilder.playerSkull = player;
        return itemStackBuilder;
    }

    public ItemStackBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ItemStackBuilder withAdditional() {
        this.noAdditional = false;
        return this;
    }

    public ItemStackBuilder withLore(String loreLine) {
        lore.add(loreLine);
        return this;
    }

    public ItemStack build() {
        if (!name.isEmpty()) {
            itemStack.getOrCreateSubNbt("display")
                .putString(
                    "Name",
                    "{\"text\":\"" + name.replace("\"", "\\\"") + "\",\"italic\":false}"
                );
        }
        if (noAdditional) {
            itemStack.addHideFlag(ItemStack.TooltipSection.ADDITIONAL);
            itemStack.getOrCreateNbt().putBoolean("HideTooltip", true);
        }
        if (!lore.isEmpty()) {
            NbtCompound displayNbt = itemStack.getOrCreateSubNbt("display");
            NbtList nbtLore = new NbtList();
            lore.forEach(line -> nbtLore.add(NbtString.of(Text.Serializer.toJson(
                Texts.join(Text.literal(line).getWithStyle(Style.EMPTY.withItalic(false)), Text.of(""))
            ))));
            displayNbt.put("Lore", nbtLore);
        }
        if (playerSkull != null) {
            itemStack.getOrCreateNbt().putString("SkullOwner", playerSkull.getGameProfile().getName());
        }
        return itemStack;
    }

}
