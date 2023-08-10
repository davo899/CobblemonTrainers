package com.selfdot.cobblemontrainers.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;

public class CobblemonConfig {
  Gson GSON = new GsonBuilder()
          .disableHtmlEscaping()
          .setPrettyPrinting()
          .create();

  public static int shinyRate = 8192;

  public CobblemonConfig() {
    init();
  }

  // Extract from Cobblemon config when initialized.
  public void init() {
    File configFolder = new File(System.getProperty("user.dir") + "/config/cobblemon");
    File configFile = new File(configFolder, "main.json");
    System.out.println("cobblemon config -> " + configFolder.getAbsolutePath());

    try {
      JsonObject obj = GSON.fromJson(new FileReader(configFile), JsonObject.class);
      JsonObject configs = obj.getAsJsonObject();
      shinyRate = configs.get("shinyRate").getAsInt();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
