package com.selfdot.cobblemontrainers.trainer;

import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.moves.MoveSet;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.selfdot.cobblemontrainers.util.CobblemonTrainersLog;
import com.selfdot.cobblemontrainers.util.ConfigKeys;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.List;

public class TrainerPokemon {

    private static final List<String> REQUIRED_MEMBERS = List.of(
        ConfigKeys.POKEMON_SPECIES,
        ConfigKeys.POKEMON_GENDER,
        ConfigKeys.POKEMON_LEVEL,
        ConfigKeys.POKEMON_NATURE,
        ConfigKeys.POKEMON_ABILITY,
        ConfigKeys.POKEMON_MOVESET,
        ConfigKeys.POKEMON_IVS,
        ConfigKeys.POKEMON_EVS
    );

    private Species species;
    private Gender gender;
    private int level;
    private Nature nature;
    private Ability ability;
    private MoveSet moveset;
    private IVs ivs;
    private EVs evs;

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ConfigKeys.POKEMON_SPECIES, species.getResourceIdentifier().toString());
        jsonObject.addProperty(ConfigKeys.POKEMON_GENDER, gender.name());
        jsonObject.addProperty(ConfigKeys.POKEMON_LEVEL, level);
        jsonObject.addProperty(ConfigKeys.POKEMON_NATURE, nature.getName().toString());
        jsonObject.addProperty(ConfigKeys.POKEMON_ABILITY, ability.getName());
        JsonArray movesetJson = new JsonArray();
        moveset.forEach(move -> movesetJson.add(move.getName()));
        jsonObject.add(ConfigKeys.POKEMON_MOVESET, movesetJson);
        jsonObject.add(ConfigKeys.POKEMON_IVS, ivs.saveToJSON(new JsonObject()));
        jsonObject.add(ConfigKeys.POKEMON_EVS, evs.saveToJSON(new JsonObject()));
        return jsonObject;
    }

    @SuppressWarnings("DataFlowIssue")
    @Nullable
    public static TrainerPokemon fromJson(JsonObject jsonObject) {
        for (String member : REQUIRED_MEMBERS) if (!jsonObject.has(member)) return null;

        TrainerPokemon trainerPokemon = new TrainerPokemon();
        try {
            trainerPokemon.species = PokemonSpecies.INSTANCE.getByIdentifier(
                new Identifier(jsonObject.get(ConfigKeys.POKEMON_SPECIES).getAsString())
            );
            trainerPokemon.gender = Gender.valueOf(jsonObject.get(ConfigKeys.POKEMON_GENDER).getAsString());
            trainerPokemon.level = jsonObject.get(ConfigKeys.POKEMON_LEVEL).getAsInt();
            trainerPokemon.nature = Natures.INSTANCE.getNature(new Identifier(jsonObject.get(ConfigKeys.POKEMON_NATURE).getAsString()));
            trainerPokemon.ability = new Ability(Abilities.INSTANCE.getOrException(
                jsonObject.get(ConfigKeys.POKEMON_ABILITY).getAsString()
            ), false);
            trainerPokemon.ivs = (IVs) new IVs().loadFromJSON(jsonObject.get(ConfigKeys.POKEMON_IVS).getAsJsonObject());
            trainerPokemon.evs = (EVs) new EVs().loadFromJSON(jsonObject.get(ConfigKeys.POKEMON_EVS).getAsJsonObject());
            trainerPokemon.moveset = new MoveSet();
            JsonArray movesetJson = jsonObject.get(ConfigKeys.POKEMON_MOVESET).getAsJsonArray();
            for (int i = 0; i < Math.min(4, movesetJson.size()); i++) {
                trainerPokemon.moveset.setMove(i, Moves.INSTANCE.getByName(movesetJson.get(i).getAsString()).create());
            }
            if (trainerPokemon.moveset.getMoves().isEmpty()) return null;

        } catch (Exception e) {
            return null;
        }
        return trainerPokemon;
    }

    public Pokemon toPokemon() {
        Pokemon pokemon = new Pokemon();
        pokemon.initializeMoveset(true);
        pokemon.setSpecies(species);
        pokemon.setGender(gender);
        pokemon.setLevel(level);
        pokemon.setNature(nature);
        pokemon.setAbility(ability);
        pokemon.getMoveSet().copyFrom(moveset);
        pokemon.setIvs(ivs);
        pokemon.setEvs(evs);
        return pokemon;
    }

    public static TrainerPokemon fromPokemon(Pokemon pokemon) {
        TrainerPokemon trainerPokemon = new TrainerPokemon();
        trainerPokemon.species = pokemon.getSpecies();
        trainerPokemon.gender = pokemon.getGender();
        trainerPokemon.level = pokemon.getLevel();
        trainerPokemon.nature = pokemon.getNature();
        trainerPokemon.ability = pokemon.getAbility();
        trainerPokemon.moveset = pokemon.getMoveSet();
        trainerPokemon.ivs = pokemon.getIvs();
        trainerPokemon.evs = pokemon.getEvs();
        return trainerPokemon;
    }

}
