package com.selfdot.cobblemontrainers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.selfdot.cobblemontrainers.util.CommandExecutor;
import com.selfdot.libs.io.JsonFile;
import com.selfdot.libs.minecraft.DisableableMod;
import lombok.Getter;

import static com.selfdot.cobblemontrainers.util.CommandExecutor.CONSOLE;
import static com.selfdot.cobblemontrainers.util.DataKeys.CONFIG_COMMAND_EXECUTOR;
import static com.selfdot.cobblemontrainers.util.DataKeys.CONFIG_XP_ENABLED;

public class Config extends JsonFile {

    private boolean xpEnabled;
    @Getter
    private CommandExecutor commandExecutor;

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
    }

    @Override
    protected JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(CONFIG_XP_ENABLED, xpEnabled);
        jsonObject.addProperty(CONFIG_COMMAND_EXECUTOR, commandExecutor.name());
        return jsonObject;
    }

    public boolean isXpDisabled() {
        return !xpEnabled;
    }

}
