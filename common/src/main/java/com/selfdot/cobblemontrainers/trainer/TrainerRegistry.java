package com.selfdot.cobblemontrainers.trainer;

import com.google.gson.*;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.util.JsonFile;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TrainerRegistry extends JsonFile {

    private final Map<String, Trainer> trainerMap = new HashMap<>();

    public TrainerRegistry(CobblemonTrainers mod) {
        super(mod);
    }

    public boolean addTrainer(Trainer trainer) {
        if (trainerMap.containsKey(trainer.getName())) return false;
        trainerMap.put(trainer.getName(), trainer);
        save();
        return true;
    }

    public void addOrUpdateTrainer(Trainer trainer) {
        trainerMap.put(trainer.getName(), trainer);
        save();
    }

    public boolean removeTrainer(String trainerName) {
        if (!trainerMap.containsKey(trainerName)) return false;
        trainerMap.remove(trainerName);
        save();
        return true;
    }

    @Nullable
    public Trainer getTrainer(String name) {
        return trainerMap.get(name);
    }

    public Set<String> getAllTrainerNames() {
        return trainerMap.keySet();
    }

    public Collection<Trainer> getAllTrainers() {
        return trainerMap.values();
    }

    @Override
    protected JsonElement toJson() {
        JsonArray jsonArray = new JsonArray();
        trainerMap.values().forEach(trainer -> jsonArray.add(trainer.toJson()));
        return jsonArray;
    }

    @Override
    protected String filename() {
        return "config/trainers/trainers.json";
    }

    @Override
    protected void setDefaults() {
        trainerMap.clear();
    }

    @Override
    protected void loadFromJson(JsonElement jsonElement) {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        jsonArray.forEach(trainerJson -> addOrUpdateTrainer(new Trainer(trainerJson)));
    }

}
