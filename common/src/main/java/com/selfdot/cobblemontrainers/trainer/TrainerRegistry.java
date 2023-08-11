package com.selfdot.cobblemontrainers.trainer;

import com.google.gson.*;
import com.selfdot.cobblemontrainers.util.CobblemonTrainersLog;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

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

    public boolean removeTrainer(String trainerName) {
        if (!trainerMap.containsKey(trainerName)) return false;
        trainerMap.remove(trainerName);
        return true;
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
                if (!trainerJson.isJsonObject()) { logFoundInvalidTrainerData(trainerJson); return; }

                Trainer trainer = Trainer.fromJson(trainerJson.getAsJsonObject());
                if (trainer == null) { logFoundInvalidTrainerData(trainerJson); return; }

                if (!addTrainer(trainer)) CobblemonTrainersLog.LOGGER.warn(
                    "Trainer with name: " + trainer.getName() + " already exists, ignoring"
                );
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

}
