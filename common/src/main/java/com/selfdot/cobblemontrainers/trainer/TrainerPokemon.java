package com.selfdot.cobblemontrainers.trainer;

import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.api.moves.MoveSet;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.selfdot.cobblemontrainers.util.CobblemonTrainersLog;
import com.selfdot.cobblemontrainers.util.DataKeys;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.List;

public class TrainerPokemon {

    private static final List<String> REQUIRED_MEMBERS = List.of(
        DataKeys.POKEMON_SPECIES,
        DataKeys.POKEMON_GENDER,
        DataKeys.POKEMON_LEVEL,
        DataKeys.POKEMON_NATURE,
        DataKeys.POKEMON_ABILITY,
        DataKeys.POKEMON_MOVESET,
        DataKeys.POKEMON_IVS,
        DataKeys.POKEMON_EVS
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
        jsonObject.addProperty(DataKeys.POKEMON_SPECIES, species.getResourceIdentifier().toString());
        jsonObject.addProperty(DataKeys.POKEMON_GENDER, gender.name());
        jsonObject.addProperty(DataKeys.POKEMON_LEVEL, level);
        jsonObject.addProperty(DataKeys.POKEMON_NATURE, nature.getName().toString());
        jsonObject.addProperty(DataKeys.POKEMON_ABILITY, ability.getName());
        JsonArray movesetJson = new JsonArray();
        moveset.forEach(move -> movesetJson.add(move.getName()));
        jsonObject.add(DataKeys.POKEMON_MOVESET, movesetJson);
        jsonObject.add(DataKeys.POKEMON_IVS, ivs.saveToJSON(new JsonObject()));
        jsonObject.add(DataKeys.POKEMON_EVS, evs.saveToJSON(new JsonObject()));
        return jsonObject;
    }

    @SuppressWarnings("DataFlowIssue")
    @Nullable
    public static TrainerPokemon fromJson(JsonObject jsonObject) {
        for (String member : REQUIRED_MEMBERS) if (!jsonObject.has(member)) {
            CobblemonTrainersLog.LOGGER.error("Trainer pokemon missing field: " + member);
            return null;
        }

        TrainerPokemon trainerPokemon = new TrainerPokemon();
        try {
            trainerPokemon.species = PokemonSpecies.INSTANCE.getByIdentifier(
                new Identifier(jsonObject.get(DataKeys.POKEMON_SPECIES).getAsString())
            );
            trainerPokemon.gender = Gender.valueOf(jsonObject.get(DataKeys.POKEMON_GENDER).getAsString());
            trainerPokemon.level = jsonObject.get(DataKeys.POKEMON_LEVEL).getAsInt();
            trainerPokemon.nature = Natures.INSTANCE.getNature(
                new Identifier(jsonObject.get(DataKeys.POKEMON_NATURE).getAsString())
            );
            trainerPokemon.ability = new Ability(Abilities.INSTANCE.getOrException(
                jsonObject.get(DataKeys.POKEMON_ABILITY).getAsString()
            ), false);
            trainerPokemon.ivs = (IVs) new IVs().loadFromJSON(jsonObject.get(DataKeys.POKEMON_IVS).getAsJsonObject());
            trainerPokemon.evs = (EVs) new EVs().loadFromJSON(jsonObject.get(DataKeys.POKEMON_EVS).getAsJsonObject());
            trainerPokemon.moveset = new MoveSet();
            JsonArray movesetJson = jsonObject.get(DataKeys.POKEMON_MOVESET).getAsJsonArray();
            for (int i = 0; i < Math.min(4, movesetJson.size()); i++) {
                trainerPokemon.moveset.setMove(i, Moves.INSTANCE.getByName(movesetJson.get(i).getAsString()).create());
            }
            if (trainerPokemon.moveset.getMoves().isEmpty()) {
                CobblemonTrainersLog.LOGGER.error("Trainer pokemon has no moves");
                return null;
            }

        } catch (Exception e) {
            CobblemonTrainersLog.LOGGER.error("Exception when loading trainer pokemon:");
            CobblemonTrainersLog.LOGGER.error(e.getMessage());
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

    public String getName() {
        return species.getTranslatedName().getString();
    }

    public Ability getAbility() {
        return ability;
    }

    public MoveSet getMoveset() {
        return moveset;
    }

    public IVs getIvs() {
        return ivs;
    }

    public EVs getEvs() {
        return evs;
    }

    public void setAbility(Ability ability) {
        this.ability = ability;
    }

    public void setMoveset(MoveSet moveset) {
        this.moveset = moveset;
    }

    public void setIvs(IVs ivs) {
        this.ivs = ivs;
    }

    public void setEvs(EVs evs) {
        this.evs = evs;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
