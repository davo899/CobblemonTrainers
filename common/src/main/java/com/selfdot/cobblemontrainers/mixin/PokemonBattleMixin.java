package com.selfdot.cobblemontrainers.mixin;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.selfdot.cobblemontrainers.util.PokemonUtility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PokemonBattle.class)
public abstract class PokemonBattleMixin {

    @Shadow
    public abstract Iterable<BattleActor> getActors();

    @Inject(method = "end", at = @At("HEAD"), remap = false)
    private void injectEnd(CallbackInfo ci) {
        getActors().forEach(actor -> actor.getPlayerUUIDs().forEach(PokemonUtility.IN_TRAINER_BATTLE::remove));
    }

}
