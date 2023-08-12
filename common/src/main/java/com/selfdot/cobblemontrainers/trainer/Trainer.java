package com.selfdot.cobblemontrainers.trainer;

import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.selfdot.cobblemontrainers.util.ConfigKeys;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Trainer {

    private final String name;
    private final List<Pokemon> team;

    public Trainer(String name, List<Pokemon> team) {
        this.name = name;
        this.team = team;
    }

    public List<BattlePokemon> getTeam() {
        return team.stream().map(pokemon -> new BattlePokemon(pokemon, pokemon)).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    private static final List<String> TRAINER_POKEMON_REQUIRES = List.of(
        ConfigKeys.POKEMON_SPECIES,
        ConfigKeys.POKEMON_LEVEL,
        ConfigKeys.POKEMON_NATURE,
        ConfigKeys.POKEMON_ABILITY,
        ConfigKeys.POKEMON_MOVESET,
        ConfigKeys.POKEMON_IVS,
        ConfigKeys.POKEMON_EVS
    );

    private static JsonObject trainerPokemonToJson(Pokemon pokemon) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ConfigKeys.POKEMON_SPECIES, pokemon.getSpecies().getResourceIdentifier().toString());
        jsonObject.addProperty(ConfigKeys.POKEMON_LEVEL, pokemon.getLevel());
        jsonObject.addProperty(ConfigKeys.POKEMON_NATURE, pokemon.getNature().getName().toString());
        jsonObject.add(ConfigKeys.POKEMON_ABILITY, pokemon.getAbility().saveToJSON(new JsonObject()));
        jsonObject.add(ConfigKeys.POKEMON_MOVESET, pokemon.getMoveSet().saveToJSON(new JsonObject()));
        jsonObject.add(ConfigKeys.POKEMON_IVS, pokemon.getIvs().saveToJSON(new JsonObject()));
        jsonObject.add(ConfigKeys.POKEMON_EVS, pokemon.getEvs().saveToJSON(new JsonObject()));
        return jsonObject;
    }

    @SuppressWarnings("DataFlowIssue")
    @Nullable
    private static Pokemon trainerPokemonFromJson(JsonObject jsonObject) {
        for (String member : TRAINER_POKEMON_REQUIRES) if (!jsonObject.has(member)) return null;

        Pokemon pokemon = new Pokemon();
        try {
            pokemon.setSpecies(PokemonSpecies.INSTANCE.getByIdentifier(
                new Identifier(jsonObject.get(ConfigKeys.POKEMON_SPECIES).getAsString()))
            );
            pokemon.setLevel(jsonObject.get(ConfigKeys.POKEMON_LEVEL).getAsInt());
            pokemon.setNature(Natures.INSTANCE.getNature(new Identifier(jsonObject.get(ConfigKeys.POKEMON_NATURE).getAsString())));
            pokemon.getAbility().loadFromJSON(jsonObject.get(ConfigKeys.POKEMON_ABILITY).getAsJsonObject());
            pokemon.getMoveSet().loadFromJSON(jsonObject.get(ConfigKeys.POKEMON_MOVESET).getAsJsonObject());
            pokemon.getIvs().loadFromJSON(jsonObject.get(ConfigKeys.POKEMON_IVS).getAsJsonObject());
            pokemon.getEvs().loadFromJSON(jsonObject.get(ConfigKeys.POKEMON_EVS).getAsJsonObject());

        } catch (Exception e) {
            return null;
        }
        return pokemon;
    }

    public JsonElement toJson() {
        JsonArray teamArray = new JsonArray(team.size());
        team.forEach(pokemon -> teamArray.add(trainerPokemonToJson(pokemon)));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ConfigKeys.TRAINER_NAME, name);
        jsonObject.add(ConfigKeys.TRAINER_TEAM, teamArray);
        return jsonObject;
    }

    @Nullable
    public static Trainer fromJson(JsonObject jsonObject) {
        if (!jsonObject.has(ConfigKeys.TRAINER_NAME) || !jsonObject.has(ConfigKeys.TRAINER_TEAM)) return null;

        String name = jsonObject.get(ConfigKeys.TRAINER_NAME).getAsString();
        List<Pokemon> team = new ArrayList<>();
        jsonObject.getAsJsonArray(ConfigKeys.TRAINER_TEAM)
            .forEach(jsonElement -> {
                Pokemon pokemon = trainerPokemonFromJson(jsonElement.getAsJsonObject());
                if (pokemon == null) return;
                team.add(pokemon);
            });

        if (name.isEmpty() || team.isEmpty()) return null;

        return new Trainer(name, team);
    }

    public void healTeam() {
        team.forEach(Pokemon::heal);
    }

}
