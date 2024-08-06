package com.selfdot.cobblemontrainers;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter @Setter
public class PlayerData {

    @Expose private final Set<String> trainersBeaten = new HashSet<>();
    @Expose private final Map<String, Long> timesLastBattled = new HashMap<>();

}
