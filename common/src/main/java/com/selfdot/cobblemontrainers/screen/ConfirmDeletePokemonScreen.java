package com.selfdot.cobblemontrainers.screen;

import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerPokemon;
import com.selfdot.cobblemontrainers.util.ScreenUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public class ConfirmDeletePokemonScreen extends Screen {

    private final Trainer trainer;
    private final TrainerPokemon trainerPokemon;
    private int confirmSlot;

    public ConfirmDeletePokemonScreen(Trainer trainer, TrainerPokemon trainerPokemon) {
        super(new TrainerPokemonScreen(trainer, trainerPokemon));
        this.trainer = trainer;
        this.trainerPokemon = trainerPokemon;
    }

    @Override
    public void initialize(Inventory inventory) {
        setSlot(inventory, columns / 2, Items.BARRIER, "Delete Pokémon");

        confirmSlot = (columns * 2) + (columns / 2);
        setSlot(inventory, confirmSlot, Items.LIME_CONCRETE, "Confirm?");
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);
        if (slotIndex == confirmSlot) {
            trainer.removeTrainerPokemon(trainerPokemon);
            trainer.save();
            player.openHandledScreen(new TrainerSetupHandlerFactory(new TrainerTeamScreen(trainer)));
        }
    }

    @Override
    public String getDisplayName() {
        return "Deleting " + trainerPokemon.getName();
    }

}
