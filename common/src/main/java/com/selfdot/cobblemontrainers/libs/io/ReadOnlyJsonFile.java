package com.selfdot.cobblemontrainers.libs.io;

import com.google.gson.*;
import com.selfdot.cobblemontrainers.libs.minecraft.DisableableMod;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public abstract class ReadOnlyJsonFile {

    protected abstract String filename();
    protected abstract void setDefaults();
    protected abstract void loadFromJson(JsonElement jsonElement);

    protected static final Gson GSON = new GsonBuilder()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .create();

    protected final DisableableMod mod;

    public ReadOnlyJsonFile(DisableableMod mod) {
        this.mod = mod;
    }

    public void load() {
        setDefaults();
        try {
            loadFromJson(JsonParser.parseReader(new FileReader(filename())));
            log.info(filename() + " loaded");

        } catch (FileNotFoundException e) {
            log.warn(filename() + " not found, attempting to generate");
            try {
                Files.createDirectories(Paths.get(filename()).getParent());
                FileWriter writer = new FileWriter(filename());
                GSON.toJson(new JsonObject(), writer);
                writer.close();

            } catch (IOException ex) {
                mod.disable();
                log.error("Unable to generate " + filename());
            }

        } catch (Exception e) {
            mod.disable();
            log.error("An exception occurred when loading " + filename() + ":");
            log.error(e.getMessage());
        }
    }

}
