package com.selfdot.cobblemontrainers.screen;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.util.PokemonUtility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SpeciesSelectScreen extends PagedScreen<Species> {

    public static List<Species> SPECIES_LIST = new ArrayList<>();
    private final Trainer trainer;

    public static void loadSpecies() {
        SPECIES_LIST = PokemonSpecies.INSTANCE.getSpecies().stream()
            .sorted(Comparator.comparingInt(Species::getNationalPokedexNumber)).toList();
    }

    public SpeciesSelectScreen(Trainer trainer) {
        super(new TrainerTeamScreen(trainer), SPECIES_LIST, 0);
        this.trainer = trainer;
    }

    @Override
    protected ItemStack toItem(Species species) {
        Pokemon pokemon = new Pokemon();
        pokemon.setSpecies(species);
        return PokemonUtility.pokemonToItem(pokemon);
    }

    @Override
    protected void onSelected(Species species, PlayerEntity player) {
        trainer.addSpecies(species);
        trainer.save();
        switchTo(new TrainerTeamScreen(trainer));
    }

    @Override
    public String getDisplayName() {
        return "Species - Page " + (pageNumber + 1);
    }

}
