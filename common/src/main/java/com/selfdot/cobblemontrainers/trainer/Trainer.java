package com.selfdot.cobblemontrainers.trainer;

import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.selfdot.libs.io.JsonFile;
import com.selfdot.libs.minecraft.DisableableMod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.selfdot.cobblemontrainers.util.DataKeys.*;

public class Trainer extends JsonFile {

    private String name;
    private String group;
    private List<TrainerPokemon> team;
    private String winCommand;
    private String lossCommand;
    private boolean canOnlyBeatOnce;
    private long cooldownSeconds;
    private int partyMaximumLevel;
    private Set<String> defeatRequiredTrainers;

    public Trainer(DisableableMod mod, String name, String group) {
        super(mod);
        setDefaults();
        this.name = name;
        this.group = group;
    }

    public Trainer(DisableableMod mod, JsonElement jsonElement) {
        super(mod);
        setDefaults();
        loadFromJson(jsonElement);
    }

    public void addSpecies(Species species, Set<String> aspects) {
        Pokemon pokemon = new Pokemon();
        pokemon.setSpecies(species);
        pokemon.initializeMoveset(true);
        pokemon.rollAbility();
        pokemon.setGender(Math.random() > 0.5 ? Gender.FEMALE : Gender.MALE);
        pokemon.setAspects(aspects);
        team.add(TrainerPokemon.fromPokemon(pokemon));
    }

    public List<BattlePokemon> getBattleTeam() {
        return team.stream()
            .map(TrainerPokemon::toPokemon)
            .peek(Pokemon::heal)
            .map(pokemon -> new BattlePokemon(pokemon, pokemon))
            .collect(Collectors.toList());
    }

    public void swap(int i, int j) {
        TrainerPokemon temp = team.get(i);
        team.set(i, team.get(j));
        team.set(j, temp);
        save();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        save();
    }

    public String getWinCommand() {
        return winCommand;
    }

    public void setWinCommand(String winCommand) {
        this.winCommand = winCommand;
        save();
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        String oldLocation = filename();
        this.group = group;
        updateLocation(oldLocation);
    }

    public String getLossCommand() {
        return lossCommand;
    }

    public void setLossCommand(String lossCommand) {
        this.lossCommand = lossCommand;
        save();
    }

    public boolean canOnlyBeatOnce() {
        return canOnlyBeatOnce;
    }

    public void setCanOnlyBeatOnce(boolean canOnlyBeatOnce) {
        this.canOnlyBeatOnce = canOnlyBeatOnce;
        save();
    }

    public long getCooldownMilliseconds() {
        return cooldownSeconds * 1000;
    }

    public void setCooldownSeconds(long cooldownSeconds) {
        this.cooldownSeconds = cooldownSeconds;
        save();
    }

    public int getPartyMaximumLevel() {
        return partyMaximumLevel;
    }

    public void setPartyMaximumLevel(int partyMaximumLevel) {
        this.partyMaximumLevel = partyMaximumLevel;
        save();
    }

    public Set<String> getDefeatRequiredTrainers() {
        return defeatRequiredTrainers;
    }

    public void addDefeatRequirement(String defeatRequirement) {
        defeatRequiredTrainers.add(defeatRequirement);
        save();
    }

    public boolean removeDefeatRequirement(String defeatRequirement) {
        boolean existed = defeatRequiredTrainers.remove(defeatRequirement);
        if (existed) save();
        return existed;
    }

    public int getTeamSize() {
        return team.size();
    }

    public TrainerPokemon getTeamSlot(int index) {
        return team.get(index);
    }

    public void addPokemon(Pokemon pokemon) {
        team.add(TrainerPokemon.fromPokemon(pokemon));
        save();
    }

    public void removeTrainerPokemon(TrainerPokemon trainerPokemon) {
        team.remove(trainerPokemon);
        save();
    }

    @Override
    protected String filename() {
        return "config/trainers/groups/" + group + "/" + name + ".json";
    }

    @Override
    protected void setDefaults() {
        team = new ArrayList<>();
        winCommand = "";
        lossCommand = "";
        canOnlyBeatOnce = false;
        cooldownSeconds = 0;
        partyMaximumLevel = 100;
        defeatRequiredTrainers = new HashSet<>();
    }

    @Override
    protected void loadFromJson(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (jsonObject.has(TRAINER_NAME)) {
            name = jsonObject.get(TRAINER_NAME).getAsString();
            if (name.isEmpty()) throw new IllegalStateException("Trainer name cannot be empty");
        }
        team = new ArrayList<>();
        jsonObject.getAsJsonArray(TRAINER_TEAM)
            .forEach(pokemonJson -> team.add(new TrainerPokemon(pokemonJson)));
        winCommand = jsonObject.has(TRAINER_WIN_COMMAND) ?
            jsonObject.get(TRAINER_WIN_COMMAND).getAsString() : "";

        if (jsonObject.has(TRAINER_GROUP)) {
            group = jsonObject.get(TRAINER_GROUP).getAsString();
        }
        if (jsonObject.has(TRAINER_LOSS_COMMAND)) {
            lossCommand = jsonObject.get(TRAINER_LOSS_COMMAND).getAsString();
        }
        if (jsonObject.has(TRAINER_CAN_ONLY_BEAT_ONCE)) {
            canOnlyBeatOnce = jsonObject.get(TRAINER_CAN_ONLY_BEAT_ONCE).getAsBoolean();
        }
        if (jsonObject.has(TRAINER_COOLDOWN_SECONDS)) {
            cooldownSeconds = jsonObject.get(TRAINER_COOLDOWN_SECONDS).getAsLong();
        }
        if (jsonObject.has(PLAYER_PARTY_MAXIMUM_LEVEL)) {
            partyMaximumLevel = jsonObject.get(PLAYER_PARTY_MAXIMUM_LEVEL).getAsInt();
        }
        if (jsonObject.has(PLAYER_DEFEAT_REQUIRED_TRAINERS)) {
            jsonObject.get(PLAYER_DEFEAT_REQUIRED_TRAINERS).getAsJsonArray().forEach(
                elem -> defeatRequiredTrainers.add(elem.getAsString())
            );
        }
    }

    @Override
    public JsonElement toJson() {
        JsonArray teamArray = new JsonArray(team.size());
        team.forEach(pokemon -> teamArray.add(pokemon.toJson()));
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(TRAINER_TEAM, teamArray);
        jsonObject.addProperty(TRAINER_WIN_COMMAND, winCommand);
        jsonObject.addProperty(TRAINER_LOSS_COMMAND, lossCommand);
        jsonObject.addProperty(TRAINER_CAN_ONLY_BEAT_ONCE, canOnlyBeatOnce);
        jsonObject.addProperty(TRAINER_COOLDOWN_SECONDS, cooldownSeconds);
        jsonObject.addProperty(PLAYER_PARTY_MAXIMUM_LEVEL, partyMaximumLevel);
        JsonArray defeatRequiredArray = new JsonArray();
        defeatRequiredTrainers.forEach(defeatRequiredArray::add);
        jsonObject.add(PLAYER_DEFEAT_REQUIRED_TRAINERS, defeatRequiredArray);
        return jsonObject;
    }

}
