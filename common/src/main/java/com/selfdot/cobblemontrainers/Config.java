package com.selfdot.cobblemontrainers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.selfdot.cobblemontrainers.util.DataKeys;
import com.selfdot.cobblemontrainers.util.JsonFile;

public class Config extends JsonFile {

    private boolean xpEnabled;

    public Config(CobblemonTrainers mod) {
        super(mod);
    }

    @Override
    protected String filename() {
        return "config/trainers/config.json";
    }

    @Override
    protected void setDefaults() {
        xpEnabled = true;
    }

    @Override
    public void load() {
        super.load();
        if (!mod.isDisabled()) save();
    }

    @Override
    protected void loadFromJson(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (jsonObject.has(DataKeys.CONFIG_XP_ENABLED)) {
            xpEnabled = jsonObject.get(DataKeys.CONFIG_XP_ENABLED).getAsBoolean();
        }
    }

    @Override
    protected JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(DataKeys.CONFIG_XP_ENABLED, xpEnabled);
        return jsonObject;
    }

    public boolean isXpDisabled() {
        return !xpEnabled;
    }

}
