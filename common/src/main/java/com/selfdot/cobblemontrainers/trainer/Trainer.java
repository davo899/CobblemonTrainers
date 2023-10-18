package com.selfdot.cobblemontrainers.trainer;

import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.util.DataKeys;
import kotlin.Unit;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Trainer {

    private String name;
    private String group;
    private final List<TrainerPokemon> team;
    private String winCommand;

    public Trainer(String name, List<TrainerPokemon> team, String group, String winCommand) {
        this.name = name;
        this.team = team;
        this.group = group;
        this.winCommand = winCommand;
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
            .map(pokemon -> new BattlePokemon(pokemon, pokemon, (pokemonEntity -> Unit.INSTANCE)))
            .collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JsonElement toJson() {
        JsonArray teamArray = new JsonArray(team.size());
        team.forEach(pokemon -> teamArray.add(pokemon.toJson()));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(DataKeys.TRAINER_NAME, name);
        jsonObject.add(DataKeys.TRAINER_TEAM, teamArray);
        jsonObject.addProperty(DataKeys.TRAINER_WIN_COMMAND, winCommand);
        jsonObject.addProperty(DataKeys.TRAINER_GROUP, group);
        return jsonObject;
    }

    @Nullable
    public static Trainer fromJson(JsonObject jsonObject) {
        if (!jsonObject.has(DataKeys.TRAINER_NAME)) {
            CobblemonTrainers.INSTANCE.disable("Trainer missing name field");
            return null;
        }
        if (!jsonObject.has(DataKeys.TRAINER_TEAM)) {
            CobblemonTrainers.INSTANCE.disable("Trainer missing team field");
            return null;
        }

        try {
            String name = jsonObject.get(DataKeys.TRAINER_NAME).getAsString();
            List<TrainerPokemon> team = new ArrayList<>();
            jsonObject.getAsJsonArray(DataKeys.TRAINER_TEAM)
                .forEach(jsonElement -> {
                    TrainerPokemon pokemon = TrainerPokemon.fromJson(jsonElement.getAsJsonObject());
                    if (pokemon == null) {
                        CobblemonTrainers.INSTANCE.disable("Invalid trainer pokemon");
                        return;
                    }
                    team.add(pokemon);
                });

            if (name.isEmpty()) {
                CobblemonTrainers.INSTANCE.disable("Trainer name is an empty string");
                return null;
            }

            String winCommand;
            if (jsonObject.has(DataKeys.TRAINER_WIN_COMMAND)) {
                winCommand = jsonObject.get(DataKeys.TRAINER_WIN_COMMAND).getAsString();
            } else {
                if (jsonObject.has(DataKeys.TRAINER_MONEY_REWARD)) {
                    winCommand = "eco give %player% " + jsonObject.get(DataKeys.TRAINER_MONEY_REWARD).getAsInt();
                } else {
                    winCommand = "";
                }
            }

            String group;
            if (jsonObject.has(DataKeys.TRAINER_GROUP)) {
                group = jsonObject.get(DataKeys.TRAINER_GROUP).getAsString();
            } else {
                group = DataKeys.UNGROUPED;
            }

            return new Trainer(name, team, group, winCommand);

        } catch (Exception e) {
            CobblemonTrainers.INSTANCE.disable("Exception when loading trainer");
            e.printStackTrace();
            return null;
        }
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

}
