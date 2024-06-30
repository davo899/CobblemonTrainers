package com.selfdot.cobblemontrainers.trainer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.selfdot.cobblemontrainers.libs.io.JsonFile;
import com.selfdot.cobblemontrainers.libs.minecraft.DisableableMod;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TrainerCooldownTracker extends JsonFile {

    private final Map<String, Map<UUID, Long>> lastBattledMap = new HashMap<>();

    public TrainerCooldownTracker(DisableableMod mod) {
        super(mod);
    }

    @Override
    protected JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        lastBattledMap.forEach((trainer, times) -> {
            JsonObject trainerJson = new JsonObject();
            times.forEach((player, time) -> trainerJson.addProperty(player.toString(), time));
            jsonObject.add(trainer, trainerJson);
        });
        return jsonObject;
    }

    @Override
    protected String filename() {
        return "trainers/timesLastBattled.json";
    }

    @Override
    protected void setDefaults() {
        lastBattledMap.clear();
    }

    @Override
    protected void loadFromJson(JsonElement jsonElement) {
        jsonElement.getAsJsonObject().entrySet().forEach(entry -> {
            Map<UUID, Long> trainerMap = new HashMap<>();
            entry.getValue().getAsJsonObject().entrySet().forEach(
                timeEntry -> trainerMap.put(UUID.fromString(timeEntry.getKey()), timeEntry.getValue().getAsLong())
            );
            lastBattledMap.put(entry.getKey(), trainerMap);
        });
    }

    public void onBattleStart(PlayerEntity player, Trainer trainer) {
        if (!lastBattledMap.containsKey(trainer.getName())) lastBattledMap.put(trainer.getName(), new HashMap<>());
        lastBattledMap.get(trainer.getName()).put(player.getUuid(), System.currentTimeMillis());
        save();
    }

    public long remainingCooldownMillis(PlayerEntity player, Trainer trainer) {
        if (!lastBattledMap.containsKey(trainer.getName())) return 0;
        if (!lastBattledMap.get(trainer.getName()).containsKey(player.getUuid())) return 0;
        return Math.max(
            (lastBattledMap.get(trainer.getName()).get(player.getUuid()) + trainer.getCooldownMilliseconds()) -
                System.currentTimeMillis(),
            0
        );
    }

}
