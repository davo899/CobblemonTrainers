package com.selfdot.cobblemontrainers.libs.io;

import com.google.gson.JsonElement;
import com.selfdot.cobblemontrainers.libs.minecraft.DisableableMod;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
public abstract class JsonFile extends ReadOnlyJsonFile {

    protected abstract JsonElement toJson();

    public JsonFile(DisableableMod mod) {
        super(mod);
    }

    public void save() {
        try {
            Files.createDirectories(Paths.get(filename()).getParent());
            FileWriter writer = new FileWriter(filename());
            GSON.toJson(toJson(), writer);
            writer.close();
            log.info("Saved " + filename());

        } catch (IOException e) {
            log.error("Unable to store to " + filename());
        }
    }

    public void updateLocation(String oldLocation) {
        try {
            Files.move(Path.of(oldLocation), Path.of(filename()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Could not move file " + filename());
            log.error(e.getMessage());
        }
    }

    public void delete() {
        try {
            Files.delete(Path.of(filename()));

        } catch (IOException e) {
            log.error("Could not delete file " + filename());
            log.error(e.getMessage());
        }
    }

}
