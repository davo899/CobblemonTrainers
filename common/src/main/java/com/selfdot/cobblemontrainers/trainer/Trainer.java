package com.selfdot.cobblemontrainers.trainer;

import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.selfdot.cobblemontrainers.util.DataKeys;
import kotlin.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Trainer {

    private String name;
    private String group = DataKeys.UNGROUPED;
    private List<TrainerPokemon> team = new ArrayList<>();
    private String winCommand = "";
    private String lossCommand = "";

    public Trainer(String name) {
        this.name = name;
    }

    public Trainer(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        name = jsonObject.get(DataKeys.TRAINER_NAME).getAsString();
        if (name.isEmpty()) throw new IllegalStateException("Trainer name cannot be empty");
        team = new ArrayList<>();
        jsonObject.getAsJsonArray(DataKeys.TRAINER_TEAM)
            .forEach(pokemonJson -> team.add(new TrainerPokemon(pokemonJson)));
        if (jsonObject.has(DataKeys.TRAINER_WIN_COMMAND)) {
            winCommand = jsonObject.get(DataKeys.TRAINER_WIN_COMMAND).getAsString();
        } else {
            if (jsonObject.has(DataKeys.TRAINER_MONEY_REWARD)) {
                winCommand = "eco give %player% " + jsonObject.get(DataKeys.TRAINER_MONEY_REWARD).getAsInt();
            } else {
                winCommand = "";
            }
        }
        if (jsonObject.has(DataKeys.TRAINER_GROUP)) {
            group = jsonObject.get(DataKeys.TRAINER_GROUP).getAsString();
        }
        if (jsonObject.has(DataKeys.TRAINER_LOSS_COMMAND)) {
            lossCommand = jsonObject.get(DataKeys.TRAINER_LOSS_COMMAND).getAsString();
        }
    }

    public JsonElement toJson() {
        JsonArray teamArray = new JsonArray(team.size());
        team.forEach(pokemon -> teamArray.add(pokemon.toJson()));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(DataKeys.TRAINER_NAME, name);
        jsonObject.add(DataKeys.TRAINER_TEAM, teamArray);
        jsonObject.addProperty(DataKeys.TRAINER_GROUP, group);
        jsonObject.addProperty(DataKeys.TRAINER_WIN_COMMAND, winCommand);
        jsonObject.addProperty(DataKeys.TRAINER_LOSS_COMMAND, lossCommand);
        return jsonObject;
    }

    public void addSpecies(Species species) {
        Pokemon pokemon = new Pokemon();
        pokemon.setSpecies(species);
        pokemon.initializeMoveset(true);
        pokemon.checkAbility();
        pokemon.setGender(Math.random() > 0.5 ? Gender.FEMALE : Gender.MALE);
        team.add(TrainerPokemon.fromPokemon(pokemon));
    }

    public List<TrainerPokemon> getTeam() {
        return team;
    }

    public List<BattlePokemon> getBattleTeam() {
        return team.stream()
            .map(TrainerPokemon::toPokemon)
            .peek(Pokemon::heal)
            .map(pokemon -> new BattlePokemon(pokemon, pokemon, (pokemonEntity -> Unit.INSTANCE)))
            .collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWinCommand() {
        return winCommand;
    }

    public void setWinCommand(String winCommand) {
        this.winCommand = winCommand;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getLossCommand() {
        return lossCommand;
    }

    public void setLossCommand(String lossCommand) {
        this.lossCommand = lossCommand;
    }

}
