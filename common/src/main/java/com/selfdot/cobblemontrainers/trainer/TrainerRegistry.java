package com.selfdot.cobblemontrainers.trainer;

import com.google.gson.*;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.util.CobblemonTrainersLog;
import com.selfdot.cobblemontrainers.util.DisableableMod;
import com.selfdot.cobblemontrainers.util.JsonFile;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TrainerRegistry extends JsonFile {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final Map<String, Trainer> trainerMap = new HashMap<>();

    public TrainerRegistry(DisableableMod mod) {
        super(mod);
    }

    public boolean addTrainer(Trainer trainer) {
        if (trainerMap.containsKey(trainer.getName())) return false;
        trainerMap.put(trainer.getName(), trainer);
        return true;
    }

    public void addOrUpdateTrainer(Trainer trainer) {
        trainerMap.put(trainer.getName(), trainer);
    }

    public boolean removeTrainer(String trainerName) {
        if (!trainerMap.containsKey(trainerName)) return false;
        trainerMap.remove(trainerName);
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
        jsonArray.forEach(trainerJson -> {
            if (!trainerJson.isJsonObject()) {
                CobblemonTrainers.INSTANCE.disable();
                return;
            }
            Trainer trainer = Trainer.fromJson(trainerJson.getAsJsonObject());
            if (trainer == null) return;
            addOrUpdateTrainer(trainer);
        });
    }

}
