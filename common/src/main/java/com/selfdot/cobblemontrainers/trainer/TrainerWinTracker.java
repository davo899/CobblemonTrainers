package com.selfdot.cobblemontrainers.trainer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.util.JsonFile;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public class TrainerWinTracker extends JsonFile {

    private final Map<String, Set<UUID>> trainerWinMap = new HashMap<>();

    public TrainerWinTracker(CobblemonTrainers mod) {
        super(mod);
    }

    public void add(Trainer trainer, UUID playerID) {
        String trainerName = trainer.getName();
        if (!trainerWinMap.containsKey(trainerName)) trainerWinMap.put(trainerName, new HashSet<>());
        trainerWinMap.get(trainerName).add(playerID);
        save();
    }

    public void rename(String oldName, String newName) {
        if (!trainerWinMap.containsKey(oldName)) return;
        trainerWinMap.put(newName, trainerWinMap.remove(oldName));
        save();
    }

    public boolean hasBeaten(ServerPlayerEntity player, String trainerName) {
        if (!trainerWinMap.containsKey(trainerName)) return false;
        return trainerWinMap.get(trainerName).contains(player.getUuid());
    }

    public boolean hasBeaten(ServerPlayerEntity player, Trainer trainer) {
        return hasBeaten(player, trainer.getName());
    }

    public void reset(ServerPlayerEntity player, Trainer trainer) {
        if (!trainerWinMap.containsKey(trainer.getName())) return;
        trainerWinMap.get(trainer.getName()).remove(player.getUuid());
        save();
    }

    @Override
    protected JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        trainerWinMap.forEach((trainer, uuids) -> {
            JsonArray uuidsJson = new JsonArray();
            uuids.forEach(uuid -> uuidsJson.add(uuid.toString()));
            jsonObject.add(trainer, uuidsJson);
        });
        return jsonObject;
    }

    @Override
    protected String filename() {
        return "trainers/trainerWins.json";
    }

    @Override
    protected void setDefaults() {
        trainerWinMap.clear();
    }

    @Override
    protected void loadFromJson(JsonElement jsonElement) {
        jsonElement.getAsJsonObject().entrySet().forEach(entry -> {
            Set<UUID> uuids = new HashSet<>();
            entry.getValue().getAsJsonArray().forEach(uuidJson -> uuids.add(UUID.fromString(uuidJson.getAsString())));
            trainerWinMap.put(entry.getKey(), uuids);
        });
    }

}
