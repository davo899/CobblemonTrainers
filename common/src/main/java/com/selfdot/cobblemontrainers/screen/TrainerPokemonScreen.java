package com.selfdot.cobblemontrainers.screen;

import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerPokemon;
import com.selfdot.cobblemontrainers.util.PokemonUtility;
import net.minecraft.inventory.Inventory;

public class TrainerPokemonScreen extends Screen {

    private final TrainerPokemon trainerPokemon;

    public TrainerPokemonScreen(Trainer trainer, TrainerPokemon trainerPokemon) {
        super(new TrainerTeamScreen(trainer));
        this.trainerPokemon = trainerPokemon;
    }

    @Override
    public void initialize(Inventory inventory) {
        inventory.setStack(columns / 2, PokemonUtility.pokemonToInfoItem(trainerPokemon.toPokemon()));
    }

    @Override
    public String getDisplayName() {
        return trainerPokemon.getName();
    }

}
