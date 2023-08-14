package com.selfdot.cobblemontrainers.screen;

import com.cobblemon.mod.common.CobblemonItems;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerPokemon;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class IVSelectScreen extends Screen {

    private final Trainer trainer;
    private final TrainerPokemon trainerPokemon;

    public IVSelectScreen(Trainer trainer, TrainerPokemon trainerPokemon) {
        super(new TrainerPokemonScreen(trainer, trainerPokemon));
        this.trainer = trainer;
        this.trainerPokemon = trainerPokemon;
    }

    @Override
    public void initialize(Inventory inventory) {
        ItemStack itemStack = new ItemStack(CobblemonItems.STAR_SWEET.get());
        itemStack.setCustomName(Text.literal("IVs"));
        inventory.setStack(columns / 2, itemStack);

        itemStack = new ItemStack(CobblemonItems.HP_UP.get());
        itemStack.setCustomName(Text.literal("HP"));
        inventory.setStack((columns * 2) + (columns / 2) - 1, itemStack);

        itemStack = new ItemStack(CobblemonItems.PROTEIN.get());
        itemStack.setCustomName(Text.literal("Attack"));
        inventory.setStack((columns * 2) + (columns / 2), itemStack);

        itemStack = new ItemStack(CobblemonItems.IRON.get());
        itemStack.setCustomName(Text.literal("Defense"));
        inventory.setStack((columns * 2) + (columns / 2) + 1, itemStack);

        itemStack = new ItemStack(CobblemonItems.CALCIUM.get());
        itemStack.setCustomName(Text.literal("Special Attack"));
        inventory.setStack((columns * 3) + (columns / 2) - 1, itemStack);

        itemStack = new ItemStack(CobblemonItems.ZINC.get());
        itemStack.setCustomName(Text.literal("Special Defense"));
        inventory.setStack((columns * 3) + (columns / 2), itemStack);

        itemStack = new ItemStack(CobblemonItems.CARBOS.get());
        itemStack.setCustomName(Text.literal("Speed"));
        inventory.setStack((columns * 3) + (columns / 2) + 1, itemStack);
    }

    @Override
    public String getDisplayName() {
        return "IVs";
    }
}
