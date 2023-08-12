package com.selfdot.cobblemontrainers.trainer;

import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.api.moves.MoveSet;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.*;
import com.google.gson.JsonObject;
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
        jsonObject.add(ConfigKeys.POKEMON_ABILITY, ability.saveToJSON(new JsonObject()));
        jsonObject.add(ConfigKeys.POKEMON_MOVESET, moveset.saveToJSON(new JsonObject()));
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
            trainerPokemon.ability = new Ability(null, false).loadFromJSON(jsonObject.get(ConfigKeys.POKEMON_ABILITY).getAsJsonObject());
            trainerPokemon.moveset = new MoveSet().loadFromJSON(jsonObject.get(ConfigKeys.POKEMON_MOVESET).getAsJsonObject());
            trainerPokemon.ivs = (IVs) new IVs().loadFromJSON(jsonObject.get(ConfigKeys.POKEMON_IVS).getAsJsonObject());
            trainerPokemon.evs = (EVs) new EVs().loadFromJSON(jsonObject.get(ConfigKeys.POKEMON_EVS).getAsJsonObject());

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
