package com.selfdot.cobblemontrainers.trainer;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.moves.MoveSet;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.*;
import com.cobblemon.mod.common.pokemon.properties.UncatchableProperty;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kotlin.Unit;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.selfdot.cobblemontrainers.util.DataKeys.*;

@Getter @Setter
public class TrainerPokemon {

    public static final Set<UUID> IS_TRAINER_OWNED = new HashSet<>();

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

        String speciesString = jsonObject.get(POKEMON_SPECIES).getAsString();
        species = PokemonSpecies.INSTANCE.getByIdentifier(new Identifier(speciesString));
        if (species == null) throw new IllegalStateException("Invalid species: " + speciesString);

        gender = Gender.valueOf(jsonObject.get(POKEMON_GENDER).getAsString());
        level = jsonObject.get(POKEMON_LEVEL).getAsInt();

        String natureString = jsonObject.get(POKEMON_NATURE).getAsString();
        nature = Natures.INSTANCE.getNature(new Identifier(natureString));
        if (nature == null) throw new IllegalStateException("Invalid nature: " + natureString);

        ability = new Ability(Abilities.INSTANCE.getOrException(
            jsonObject.get(POKEMON_ABILITY).getAsString()
        ), false);
        ivs = (IVs) new IVs().loadFromJSON(jsonObject.get(POKEMON_IVS).getAsJsonObject());
        evs = (EVs) new EVs().loadFromJSON(jsonObject.get(POKEMON_EVS).getAsJsonObject());
        moveset = new MoveSet();
        JsonArray movesetJson = jsonObject.get(POKEMON_MOVESET).getAsJsonArray();
        for (int i = 0; i < Math.min(4, movesetJson.size()); i++) {
            moveset.setMove(i, Moves.INSTANCE.getByName(movesetJson.get(i).getAsString()).create());
        }
        if (jsonObject.has(POKEMON_SHINY)) isShiny = jsonObject.get(POKEMON_SHINY).getAsBoolean();
        if (jsonObject.has(POKEMON_HELD_ITEM)) {
            heldItem = Registries.ITEM.get(
                Identifier.tryParse(jsonObject.get(POKEMON_HELD_ITEM).getAsString())
            );
        }
        if (jsonObject.has(POKEMON_ASPECTS)) {
            JsonArray aspectsArray = jsonObject.getAsJsonArray(POKEMON_ASPECTS);
            aspectsArray.forEach(aspect -> aspects.add(aspect.getAsString()));
        }
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(POKEMON_SPECIES, species.getResourceIdentifier().toString());
        jsonObject.addProperty(POKEMON_GENDER, gender.name());
        jsonObject.addProperty(POKEMON_LEVEL, level);
        jsonObject.addProperty(POKEMON_NATURE, nature.getName().toString());
        jsonObject.addProperty(POKEMON_ABILITY, ability.getName());
        JsonArray movesetJson = new JsonArray();
        moveset.forEach(move -> movesetJson.add(move.getName()));
        jsonObject.add(POKEMON_MOVESET, movesetJson);
        jsonObject.add(POKEMON_IVS, ivs.saveToJSON(new JsonObject()));
        jsonObject.add(POKEMON_EVS, evs.saveToJSON(new JsonObject()));
        jsonObject.addProperty(POKEMON_SHINY, isShiny);
        jsonObject.addProperty(POKEMON_HELD_ITEM, Registries.ITEM.getId(heldItem).toString());
        JsonArray aspectsArray = new JsonArray();
        aspects.forEach(aspectsArray::add);
        jsonObject.add(POKEMON_ASPECTS, aspectsArray);
        return jsonObject;
    }

    public Pokemon toPokemon() {
        Pokemon pokemon = new Pokemon();
        pokemon.setSpecies(species);
        pokemon.setGender(gender);
        pokemon.setShiny(isShiny);
        Set<String> setAspects = new HashSet<>(aspects);
        if (isShiny) setAspects.add("shiny");
        pokemon.setAspects(setAspects);
        pokemon.setLevel(level);
        pokemon.initializeMoveset(true);
        pokemon.setNature(nature);
        pokemon.updateAbility(ability);
        pokemon.getMoveSet().copyFrom(moveset);
        ivs.spliterator().forEachRemaining(entry -> pokemon.setIV(entry.getKey(), entry.getValue()));
        evs.spliterator().forEachRemaining(entry -> pokemon.setEV(entry.getKey(), entry.getValue()));
        if (heldItem.equals(Items.AIR)) pokemon.removeHeldItem();
        else pokemon.swapHeldItem(new ItemStack(heldItem), false);
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

    public static void registerPokemonSendOutListener() {
        CobblemonEvents.POKEMON_SENT_POST.subscribe(Priority.NORMAL, event -> {
            if (IS_TRAINER_OWNED.contains(event.getPokemon().getUuid())) {
                event.getPokemonEntity().getDataTracker().set(PokemonEntity.getUNBATTLEABLE(), true);
            }
            return Unit.INSTANCE;
        });
    }

}
