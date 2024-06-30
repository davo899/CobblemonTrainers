package com.selfdot.cobblemontrainers.libs.minecraft.screen;

import java.util.List;

public record View<T extends Menu<T>>(List<Component<T>> components) { }
