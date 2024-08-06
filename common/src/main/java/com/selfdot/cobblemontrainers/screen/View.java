package com.selfdot.cobblemontrainers.screen;

import java.util.List;

public record View<T extends Menu<T>>(List<Component<T>> components) { }
