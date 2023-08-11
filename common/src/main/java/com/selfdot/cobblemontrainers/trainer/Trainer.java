package com.selfdot.cobblemontrainers.trainer;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonPropertyExtractor;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import kotlinx.serialization.json.Json;

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

    private static JsonObject trainerPokemonToJson(Pokemon pokemon) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("species", pokemon.getSpecies().getName());
        jsonObject.addProperty("level", pokemon.getLevel());
        jsonObject.addProperty("nature", pokemon.getNature().getName().toString());
        jsonObject.add("ability", pokemon.getAbility().saveToJSON(new JsonObject()));
        jsonObject.add("moveset", pokemon.getMoveSet().saveToJSON(new JsonObject()));
        jsonObject.add("ivs", pokemon.getIvs().saveToJSON(new JsonObject()));
        jsonObject.add("evs", pokemon.getEvs().saveToJSON(new JsonObject()));
        return jsonObject;
    }

    public JsonElement toJson() {
        JsonArray teamArray = new JsonArray(team.size());
        team.forEach(pokemon -> teamArray.add(trainerPokemonToJson(pokemon)));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        jsonObject.add("team", teamArray);
        return jsonObject;
    }

}
