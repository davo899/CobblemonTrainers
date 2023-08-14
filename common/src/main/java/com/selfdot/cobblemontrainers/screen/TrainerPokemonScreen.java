package com.selfdot.cobblemontrainers.screen;

import com.cobblemon.mod.common.CobblemonItems;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerPokemon;
import com.selfdot.cobblemontrainers.util.PokemonUtility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public class TrainerPokemonScreen extends Screen {

    private final Trainer trainer;
    private final TrainerPokemon trainerPokemon;
    private int movesSlot;
    private int abilitiesSlot;
    private int evsSlot;
    private int ivsSlot;
    private int deleteSlot;

    public TrainerPokemonScreen(Trainer trainer, TrainerPokemon trainerPokemon) {
        super(new TrainerTeamScreen(trainer));
        this.trainer = trainer;
        this.trainerPokemon = trainerPokemon;
    }

    @Override
    public void initialize(Inventory inventory) {
        inventory.setStack(columns / 2, PokemonUtility.pokemonToInfoItem(trainerPokemon.toPokemon()));

        movesSlot = (columns * 2) + (columns / 2) - 2;
        abilitiesSlot = movesSlot + 1;
        evsSlot = abilitiesSlot + 1;
        ivsSlot = evsSlot + 1;
        deleteSlot = ivsSlot + 1;

        ItemStack movesItem = new ItemStack(Items.MUSIC_DISC_5);
        movesItem.setCustomName(Text.literal("Moves"));
        inventory.setStack(movesSlot, movesItem);

        ItemStack abilitiesItem = new ItemStack(CobblemonItems.CLOVER_SWEET.get());
        abilitiesItem.setCustomName(Text.literal("Abilities"));
        inventory.setStack(abilitiesSlot, abilitiesItem);

        ItemStack evsItem = new ItemStack(CobblemonItems.FLOWER_SWEET.get());
        evsItem.setCustomName(Text.literal("EVs"));
        inventory.setStack(evsSlot, evsItem);

        ItemStack ivsItem = new ItemStack(CobblemonItems.STAR_SWEET.get());
        ivsItem.setCustomName(Text.literal("IVs"));
        inventory.setStack(ivsSlot, ivsItem);

        ItemStack deleteItem = new ItemStack(Items.BARRIER);
        deleteItem.setCustomName(Text.literal("Delete Pokémon"));
        inventory.setStack(deleteSlot, deleteItem);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);

        if (slotIndex == movesSlot) {
            player.openHandledScreen(new TrainerSetupHandlerFactory(new PokemonMovesetScreen(trainer, trainerPokemon)));
        } else if (slotIndex == abilitiesSlot) {
            player.openHandledScreen(new TrainerSetupHandlerFactory(new AbilitySelectScreen(trainer, trainerPokemon)));
        } else if (slotIndex == evsSlot) {
            player.openHandledScreen(new TrainerSetupHandlerFactory(new EVSelectScreen(trainer, trainerPokemon)));
        } else if (slotIndex == ivsSlot) {
            player.openHandledScreen(new TrainerSetupHandlerFactory(new IVSelectScreen(trainer, trainerPokemon)));
        } else if (slotIndex == deleteSlot) {
            player.openHandledScreen(new TrainerSetupHandlerFactory(new ConfirmDeletePokemonScreen(trainer, trainerPokemon)));
        }
    }

    @Override
    public String getDisplayName() {
        return trainerPokemon.getName();
    }

}
