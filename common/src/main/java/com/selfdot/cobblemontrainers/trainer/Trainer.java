package com.selfdot.cobblemontrainers.trainer;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonPropertyExtractor;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.logging.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Trainer {

    private String name = "trainer";
    private final List<Pokemon> team = new ArrayList<>();
    {
        for (int i = 0; i < 6; i++) {
            Pokemon pokemon = new Pokemon();
            pokemon.initializeMoveset(true);
            team.add(pokemon);
        }
    }

    public List<BattlePokemon> getTeam() {
        return team.stream().map(pokemon -> new BattlePokemon(pokemon, pokemon)).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

}
