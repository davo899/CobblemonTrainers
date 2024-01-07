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
    private int levelSlot;
    private int natureSlot;

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

        levelSlot = evsSlot + columns;
        natureSlot = levelSlot + 1;

        setSlot(inventory, movesSlot, Items.MUSIC_DISC_5, "Moves");
        setSlot(inventory, abilitiesSlot, CobblemonItems.CLOVER_SWEET, "Abilities");
        setSlot(inventory, evsSlot, CobblemonItems.FLOWER_SWEET, "EVs");
        setSlot(inventory, ivsSlot, CobblemonItems.STAR_SWEET, "IVs");
        setSlot(inventory, deleteSlot, Items.BARRIER, "Delete Pokémon");
        setSlot(inventory, levelSlot, CobblemonItems.WISE_GLASSES, "Level");
        setSlot(inventory, natureSlot, Items.CYAN_DYE, "Nature");
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
        } else if (slotIndex == levelSlot) {
            player.openHandledScreen(new TrainerSetupHandlerFactory(new LevelEditScreen(trainer, trainerPokemon)));
        } else if (slotIndex == natureSlot) {
            player.openHandledScreen(new TrainerSetupHandlerFactory(new NatureSelectScreen(trainer, trainerPokemon)));
        }
    }

    @Override
    public String getDisplayName() {
        return trainerPokemon.getName();
    }

}
