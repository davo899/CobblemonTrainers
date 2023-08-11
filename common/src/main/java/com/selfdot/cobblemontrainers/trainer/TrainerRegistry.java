package com.selfdot.cobblemontrainers.trainer;

import java.util.HashMap;
import java.util.Map;

public class TrainerRegistry {

    private TrainerRegistry() { }
    private static final TrainerRegistry INSTANCE = new TrainerRegistry();

    public TrainerRegistry getInstance() { return INSTANCE; }

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

}
