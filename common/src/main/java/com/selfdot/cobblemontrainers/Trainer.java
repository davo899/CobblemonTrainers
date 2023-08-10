package com.selfdot.cobblemontrainers;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonPropertyExtractor;
import com.cobblemon.mod.common.pokemon.Pokemon;

import java.util.List;

public class Trainer {

    private String name = "trainer";
    private List<PokemonProperties> team = List.of(new Pokemon().createPokemonProperties(PokemonPropertyExtractor.Companion.getALL()));

}
