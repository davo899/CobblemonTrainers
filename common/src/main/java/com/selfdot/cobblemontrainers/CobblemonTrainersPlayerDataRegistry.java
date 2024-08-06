package com.selfdot.cobblemontrainers;

import com.selfdot.libs.io.PlayerDataRegistry;
import com.selfdot.libs.minecraft.MinecraftMod;

public class CobblemonTrainersPlayerDataRegistry extends PlayerDataRegistry<PlayerData> {

    public CobblemonTrainersPlayerDataRegistry(MinecraftMod mod) {
        super(PlayerData.class, PlayerData::new, mod);
    }

}
