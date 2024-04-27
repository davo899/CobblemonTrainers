package com.selfdot.cobblemontrainers.trainer;

import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.api.moves.MoveSet;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.*;
import com.cobblemon.mod.common.pokemon.properties.UncatchableProperty;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.selfdot.cobblemontrainers.util.DataKeys;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter @Setter
public class TrainerPokemon {

    public static final Set<UUID> IS_TRAINER_OWNED = new HashSet<>();
    public static final Set<UUID> MUST_REENABLE_LOOT_GAMERULE = new HashSet<>();

    private Species species;
    private Gender gender;
    private int level;
    private Nature nature;
    private Ability ability;
    private MoveSet moveset;
    private IVs ivs;
    private EVs evs;
    private boolean isShiny = false;
    private Item heldItem = Items.AIR;
    private Set<String> aspects = new HashSet<>();

    private final UUID uuid = UUID.randomUUID();

    public TrainerPokemon() { }

    public TrainerPokemon(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String speciesString = jsonObject.get(DataKeys.POKEMON_SPECIES).getAsString();
        species = PokemonSpecies.INSTANCE.getByIdentifier(new Identifier(speciesString));
        if (species == null) throw new IllegalStateException("Invalid species: " + speciesString);

        gender = Gender.valueOf(jsonObject.get(DataKeys.POKEMON_GENDER).getAsString());
        level = jsonObject.get(DataKeys.POKEMON_LEVEL).getAsInt();

        String natureString = jsonObject.get(DataKeys.POKEMON_NATURE).getAsString();
        nature = Natures.INSTANCE.getNature(new Identifier(natureString));
        if (nature == null) throw new IllegalStateException("Invalid nature: " + natureString);

        ability = new Ability(Abilities.INSTANCE.getOrException(
            jsonObject.get(DataKeys.POKEMON_ABILITY).getAsString()
        ), false);
        ivs = (IVs) new IVs().loadFromJSON(jsonObject.get(DataKeys.POKEMON_IVS).getAsJsonObject());
        evs = (EVs) new EVs().loadFromJSON(jsonObject.get(DataKeys.POKEMON_EVS).getAsJsonObject());
        moveset = new MoveSet();
        JsonArray movesetJson = jsonObject.get(DataKeys.POKEMON_MOVESET).getAsJsonArray();
        for (int i = 0; i < Math.min(4, movesetJson.size()); i++) {
            moveset.setMove(i, Moves.INSTANCE.getByName(movesetJson.get(i).getAsString()).create());
        }
        if (jsonObject.has(DataKeys.POKEMON_SHINY)) isShiny = jsonObject.get(DataKeys.POKEMON_SHINY).getAsBoolean();
        if (jsonObject.has(DataKeys.POKEMON_HELD_ITEM)) {
            heldItem = Registry.ITEM.get(
                Identifier.tryParse(jsonObject.get(DataKeys.POKEMON_HELD_ITEM).getAsString())
            );
        }
        if (jsonObject.has(DataKeys.POKEMON_ASPECTS)) {
            JsonArray aspectsArray = jsonObject.getAsJsonArray(DataKeys.POKEMON_ASPECTS);
            aspectsArray.forEach(aspect -> aspects.add(aspect.getAsString()));
        }
    }

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
        jsonObject.addProperty(DataKeys.POKEMON_SHINY, isShiny);
        jsonObject.addProperty(DataKeys.POKEMON_HELD_ITEM, Registry.ITEM.getId(heldItem).toString());
        JsonArray aspectsArray = new JsonArray();
        aspects.forEach(aspectsArray::add);
        jsonObject.add(DataKeys.POKEMON_ASPECTS, aspectsArray);
        return jsonObject;
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
        pokemon.setShiny(isShiny);
        if (heldItem.equals(Items.AIR)) pokemon.removeHeldItem();
        else pokemon.swapHeldItem(new ItemStack(heldItem), false);
        pokemon.setAspects(aspects);
        pokemon.setUuid(uuid);
        pokemon.getCustomProperties().add(UncatchableProperty.INSTANCE.uncatchable());
        IS_TRAINER_OWNED.add(uuid);
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
        trainerPokemon.isShiny = pokemon.getShiny();
        trainerPokemon.heldItem = pokemon.heldItem().getItem();
        trainerPokemon.aspects = pokemon.getAspects();
        return trainerPokemon;
    }

    public String getName() {
        return species.getTranslatedName().getString();
    }

    public void toggleShiny() {
        isShiny = !isShiny;
    }

    public void changeGender() {
        if (gender.equals(Gender.MALE)) gender = Gender.FEMALE;
        else gender = Gender.MALE;
    }

}
