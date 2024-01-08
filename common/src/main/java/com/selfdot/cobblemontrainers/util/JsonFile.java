package com.selfdot.cobblemontrainers.util;

import com.google.gson.JsonElement;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class JsonFile extends ReadOnlyJsonFile {

    protected abstract JsonElement toJson();

    public JsonFile(DisableableMod mod) {
        super(mod);
    }

    public void save() {
        if (!mod.isDisabled()) {
            try {
                Files.createDirectories(Paths.get(filename()).getParent());
                FileWriter writer = new FileWriter(filename());
                GSON.toJson(toJson(), writer);
                writer.close();
                mod.getLogger().info("Saved " + filename());

            } catch (IOException e) {
                mod.getLogger().error("Unable to store to " + filename());
            }
        }
    }

}
