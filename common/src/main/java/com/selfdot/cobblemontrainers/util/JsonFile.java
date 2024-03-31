package com.selfdot.cobblemontrainers.util;

import com.google.gson.JsonElement;
import com.selfdot.cobblemontrainers.CobblemonTrainers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public abstract class JsonFile extends ReadOnlyJsonFile {

    protected abstract JsonElement toJson();

    public JsonFile(CobblemonTrainers mod) {
        super(mod);
    }

    public void save() {
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

    public void updateLocation(String oldLocation) {
        try {
            Files.move(Path.of(oldLocation), Path.of(filename()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            mod.getLogger().error("Could not move file " + filename());
            mod.getLogger().error(e.getMessage());
        }
    }

    public void delete() {
        try {
            Files.delete(Path.of(filename()));
        } catch (IOException e) {
            mod.getLogger().error("Could not delete file " + filename());
            mod.getLogger().error(e.getMessage());
        }
    }

}
