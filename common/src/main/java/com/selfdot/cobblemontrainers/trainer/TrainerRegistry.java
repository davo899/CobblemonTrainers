package com.selfdot.cobblemontrainers.trainer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.selfdot.cobblemontrainers.libs.io.JsonFile;
import com.selfdot.cobblemontrainers.libs.minecraft.DisableableMod;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TrainerRegistry extends JsonFile {

    private final Map<String, Trainer> trainerMap = new HashMap<>();

    public TrainerRegistry(DisableableMod mod) {
        super(mod);
    }

    public boolean addTrainer(Trainer trainer) {
        if (trainerMap.containsKey(trainer.getName())) return false;
        trainerMap.put(trainer.getName(), trainer);
        trainer.save();
        return true;
    }

    public void addOrUpdateTrainer(Trainer trainer) {
        trainerMap.put(trainer.getName(), trainer);
        trainer.save();
    }

    public boolean removeTrainer(String trainerName) {
        if (!trainerMap.containsKey(trainerName)) return false;
        trainerMap.remove(trainerName).delete();
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
        jsonArray.forEach(trainerJson -> addOrUpdateTrainer(new Trainer(mod, trainerJson)));
    }

    @Override
    public void load() {
        if (new File(filename()).isFile()) { // Pre-0.9.2 registry file
            super.load();
            delete();
        }

        File[] groupDirectories = new File("config/trainers/groups/").listFiles(File::isDirectory);
        if (groupDirectories == null) return;
        for (File groupDirectory : groupDirectories) {
            File[] trainerFiles = groupDirectory.listFiles(File::isFile);
            if (trainerFiles == null) continue;
            String groupName = groupDirectory.getName();
            for (File trainerFile : trainerFiles) {
                String trainerFileName = trainerFile.getName();
                if (trainerFileName.length() <= 5 || !trainerFileName.endsWith(".json")) continue;
                String trainerName = trainerFileName.substring(0, trainerFileName.length() - 5);
                Trainer trainer = new Trainer(mod, trainerName, groupName);
                trainer.load();
                if (!mod.isDisabled()) addOrUpdateTrainer(trainer);
            }
        }
    }

    @Override
    public void save() {
        if (!mod.isDisabled()) trainerMap.values().forEach(JsonFile::save);
    }

}
