package com.selfdot.cobblemontrainers.trainer;

import com.google.gson.*;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.util.CobblemonTrainersLog;

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

public class TrainerRegistry {

    private TrainerRegistry() { }
    private static final TrainerRegistry INSTANCE = new TrainerRegistry();

    public static TrainerRegistry getInstance() { return INSTANCE; }

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final Map<String, Trainer> trainerMap = new HashMap<>();

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

    private void logFoundInvalidTrainerData(JsonElement jsonElement) {
        CobblemonTrainersLog.LOGGER.warn(
            "Encountered an invalid trainer in trainer data file, skipping: " + jsonElement.toString()
        );
    }

    public void loadTrainersFromFile(String filename) {
        try {
            JsonElement jsonElement = JsonParser.parseReader(new FileReader(filename));
            if (!jsonElement.isJsonArray()) {
                CobblemonTrainersLog.LOGGER.error("Invalid trainer data file");
                return;
            }
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            jsonArray.forEach(trainerJson -> {
                if (!trainerJson.isJsonObject()) {
                    CobblemonTrainers.INSTANCE.disable("Trainer json is not a json object");
                    return;
                }

                Trainer trainer = Trainer.fromJson(trainerJson.getAsJsonObject());
                if (trainer == null) return;

                addOrUpdateTrainer(trainer);
            });

        } catch (FileNotFoundException e) {
            CobblemonTrainersLog.LOGGER.warn("Trainer data file not found, attempting to generate");
            try {
                Files.createDirectories(Paths.get(filename).getParent());
                FileWriter writer = new FileWriter(filename);
                gson.toJson(new JsonArray(), writer);
                writer.close();

            } catch (IOException ex) {
                CobblemonTrainersLog.LOGGER.error("Unable to generate trainer data file");
            }
        }
    }

    public void storeTrainersToFile(String filename) {
        JsonArray jsonArray = new JsonArray();
        trainerMap.values().forEach(trainer -> jsonArray.add(trainer.toJson()));
        try {
            Files.createDirectories(Paths.get(filename).getParent());
            FileWriter writer = new FileWriter(filename);
            gson.toJson(jsonArray, writer);
            writer.close();
        } catch (IOException e) {
            CobblemonTrainersLog.LOGGER.error("Unable to store trainer data to " + filename);
        }
    }

}
