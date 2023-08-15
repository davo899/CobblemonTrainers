package com.selfdot.cobblemontrainers.trainer;

import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.selfdot.cobblemontrainers.util.ConfigKeys;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Trainer {

    private String name;
    private final List<TrainerPokemon> team;
    private int moneyReward;

    public Trainer(String name, List<TrainerPokemon> team, int moneyReward) {
        this.name = name;
        this.team = team;
        this.moneyReward = moneyReward;
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
        jsonObject.addProperty(ConfigKeys.TRAINER_NAME, name);
        jsonObject.add(ConfigKeys.TRAINER_TEAM, teamArray);
        jsonObject.addProperty(ConfigKeys.TRAINER_MONEY_REWARD, moneyReward);
        return jsonObject;
    }

    @Nullable
    public static Trainer fromJson(JsonObject jsonObject) {
        if (!jsonObject.has(ConfigKeys.TRAINER_NAME) || !jsonObject.has(ConfigKeys.TRAINER_TEAM)) return null;

        try {
            String name = jsonObject.get(ConfigKeys.TRAINER_NAME).getAsString();
            List<TrainerPokemon> team = new ArrayList<>();
            jsonObject.getAsJsonArray(ConfigKeys.TRAINER_TEAM)
                .forEach(jsonElement -> {
                    TrainerPokemon pokemon = TrainerPokemon.fromJson(jsonElement.getAsJsonObject());
                    if (pokemon == null) return;
                    team.add(pokemon);
                });

            if (name.isEmpty()) return null;

            int moneyReward = jsonObject.has(ConfigKeys.TRAINER_MONEY_REWARD) ?
                jsonObject.get(ConfigKeys.TRAINER_MONEY_REWARD).getAsInt() : 0;
            return new Trainer(name, team, moneyReward);

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

}
