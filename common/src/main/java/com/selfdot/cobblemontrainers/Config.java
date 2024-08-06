package com.selfdot.cobblemontrainers;

import com.cobblemon.mod.common.api.battles.model.ai.BattleAI;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.selfdot.cobblemontrainers.libs.io.JsonFile;
import com.selfdot.cobblemontrainers.libs.minecraft.DisableableMod;
import com.selfdot.cobblemontrainers.trainer.Generation5AI;
import com.selfdot.cobblemontrainers.util.CommandExecutor;
import lombok.Getter;
import lombok.Setter;

import static com.selfdot.cobblemontrainers.util.CommandExecutor.CONSOLE;
import static com.selfdot.cobblemontrainers.util.DataKeys.*;

public class Config extends JsonFile {

    private boolean xpEnabled;
    @Getter
    private CommandExecutor commandExecutor;
    @Setter
    private int strongAILevel;

    public Config(DisableableMod mod) {
        super(mod);
    }

    @Override
    protected String filename() {
        return "config/trainers/config.json";
    }

    @Override
    protected void setDefaults() {
        xpEnabled = true;
        commandExecutor = CONSOLE;
        strongAILevel = -1;
    }

    @Override
    public void load() {
        super.load();
        if (!mod.isDisabled()) save();
    }

    @Override
    protected void loadFromJson(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (jsonObject.has(CONFIG_XP_ENABLED)) {
            xpEnabled = jsonObject.get(CONFIG_XP_ENABLED).getAsBoolean();
        }
        if (jsonObject.has(CONFIG_COMMAND_EXECUTOR)) {
            commandExecutor = CommandExecutor.fromString(
                jsonObject.get(CONFIG_COMMAND_EXECUTOR).getAsString()
            );
        }
        if (jsonObject.has(CONFIG_STRONG_AI_LEVEL)) {
            strongAILevel = jsonObject.get(CONFIG_STRONG_AI_LEVEL).getAsInt();
        }
    }

    @Override
    protected JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(CONFIG_XP_ENABLED, xpEnabled);
        jsonObject.addProperty(CONFIG_COMMAND_EXECUTOR, commandExecutor.name());
        jsonObject.addProperty(CONFIG_STRONG_AI_LEVEL, strongAILevel);
        return jsonObject;
    }

    public boolean isXpDisabled() {
        return !xpEnabled;
    }

    public BattleAI getCurrentAI() {
        return strongAILevel == -1 ? new Generation5AI() : new StrongBattleAI(strongAILevel);
    }

}
