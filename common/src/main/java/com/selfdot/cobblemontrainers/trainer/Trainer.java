package com.selfdot.cobblemontrainers.trainer;

import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.selfdot.cobblemontrainers.util.DataKeys;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Trainer {

    private String name;
    private String group;
    private final List<TrainerPokemon> team;
    private int moneyReward;

    public Trainer(String name, List<TrainerPokemon> team, int moneyReward, String group) {
        this.name = name;
        this.team = team;
        this.moneyReward = moneyReward;
        this.group = group;
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
            .map(pokemon -> new BattlePokemon(pokemon, pokemon))
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
        jsonObject.addProperty(DataKeys.TRAINER_MONEY_REWARD, moneyReward);
        jsonObject.addProperty(DataKeys.TRAINER_GROUP, group);
        return jsonObject;
    }

    @Nullable
    public static Trainer fromJson(JsonObject jsonObject) {
        if (!jsonObject.has(DataKeys.TRAINER_NAME) || !jsonObject.has(DataKeys.TRAINER_TEAM)) return null;

        try {
            String name = jsonObject.get(DataKeys.TRAINER_NAME).getAsString();
            List<TrainerPokemon> team = new ArrayList<>();
            jsonObject.getAsJsonArray(DataKeys.TRAINER_TEAM)
                .forEach(jsonElement -> {
                    TrainerPokemon pokemon = TrainerPokemon.fromJson(jsonElement.getAsJsonObject());
                    if (pokemon == null) return;
                    team.add(pokemon);
                });

            if (name.isEmpty()) return null;

            int moneyReward = jsonObject.has(DataKeys.TRAINER_MONEY_REWARD) ?
                jsonObject.get(DataKeys.TRAINER_MONEY_REWARD).getAsInt() : 0;

            String group;
            if (jsonObject.has(DataKeys.TRAINER_GROUP)) {
                group = jsonObject.get(DataKeys.TRAINER_GROUP).getAsString();
            } else {
                group = DataKeys.UNGROUPED;
            }

            return new Trainer(name, team, moneyReward, group);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getMoneyReward() {
        return moneyReward;
    }

    public void setMoneyReward(int moneyReward) {
        this.moneyReward = moneyReward;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

}
